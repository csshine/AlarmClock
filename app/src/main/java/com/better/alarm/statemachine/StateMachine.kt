package com.better.alarm.statemachine

import com.better.alarm.logger.Logger
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.properties.Delegates

/**
 * State machine processes incoming events in it's [currentState]. If this state cannot hadle the event
 * ([State.processEvent] returned false), event is propagated to the parent of the state.
 */
open class StateMachine<T : Any>(
        val name: String,
        private val log: Logger
) {
    /** State hierarchy as a tree. Root Node is null.*/
    private lateinit var tree: Map<State<T>, Node<T>>

    /** Current state of the state machine */
    private var currentState: State<T> by Delegates.notNull()

    /** State into which the state machine transitions */
    private var targetState: State<T> by Delegates.notNull()

    /** Flag which is set when state machine is processing an event */
    private var processing = false

    /** Events not processed by current state and put on hold until the next transition */
    private val deferred = mutableListOf<T>()

    private val onStateChangedListeners = CopyOnWriteArrayList<(State<T>) -> Unit>()

    /**
     * Process the event. If [State.processEvent] returns false, event goes to the next parent state.
     */
    fun processEvent(event: T) {
        processing = true

        val hierarchy = branchToRoot(currentState)

        log.debug { "[$name] (${hierarchy.joinToString(" <- ")}) <- $event" }

        val processedIn: State<T>? = hierarchy.firstOrNull { state: State<T> ->
            log.debug { "[$name] $state.processEvent()" }
            state.processEvent(event)
        }

        requireNotNull(processedIn) { "[$name] was not able to handle $event" }

        performTransitions(event)

        processing = false
    }

    /**
     * Loop until [currentState] is not the same as [targetState] which can be caused by [transitionTo]
     */
    private fun performTransitions(reason: T) {
        check(processing)
        while (currentState != targetState) {
            val currentBranch = branchToRoot(currentState)
            val targetBranch = branchToRoot(targetState)
            val common = currentBranch.intersect(targetBranch)


            val toExit = currentBranch.minus(common)
            val toEnter = targetBranch.minus(common).reversed()

            // now that we know the branches, change the current state to target state
            // calling exit/enter may change this afterwards
            currentState = targetState

            log.debug { "[$name] exiting $toExit, entering $toEnter" }

            toExit.forEach {
                it.exit(reason)
            }

            toEnter.forEach { state ->
                onStateChangedListeners.forEach { it(state) }
                state.enter(reason)
                state.resume(reason)
            }

            processDeferred()
        }
    }

    private fun processDeferred() {
        val copy = deferred.toList()
        deferred.clear()
        copy.forEach { processEvent(it) }
    }

    private fun branchToRoot(state: State<T>): List<State<T>> {
        return generateSequence(tree.getValue(state)) { it.parentNode }
                .map { it.state }
                .toList()
    }

    /**
     * Trigger a transition. Allowed only while processing events. All exiting states will
     * receive [State.exit] and all entering states [State.enter] and [State.resume] calls.
     * Transitions will be performed after the [processEvent] is done.
     * Can be called from [State.enter]. In this case last call wins.
     */
    protected fun transitionTo(state: State<T>) {
        check(processing) { "transitionTo can only be called within processEvent" }
        targetState = state
        log.debug { "[$name] ${currentState.name} -> ${targetState.name}" }
    }

    /**
     * Indicate that current state defers processing of this event to the next state.
     * Event will be delivered upon state change to the next state.
     */
    protected fun deferEvent(event: T) {
        log.debug { "$event in $name" }
        deferred.add(event)
    }

    /** This will be called before calling [State.enter] */
    fun addOnStateChangedListener(onStateChangedListener: (State<T>) -> Unit) {
        onStateChangedListeners.add(onStateChangedListener)
    }

    /**
     * Configure and start the state machine. Both [State.enter] (unless [suppressEnterMethods])
     * and [State.resume] will be called on the initial state and it's parents.
     */
    fun start(event: T, suppressEnterMethods: Boolean = false, configurator: StateMachineBuilder.(StateMachineBuilder) -> Unit) {
        tree = StateMachineBuilder().apply {
            configurator(this, this)
            locked = true
        }.mutableTree.toMap()
        enterInitialState(suppressEnterMethods, event)
    }

    /** Goes all states from root to [currentState] and invokes [State.enter] and [State.resume] */
    private fun enterInitialState(suppressEnterMethods: Boolean, event: T) {
        processing = true
        val toEnter = branchToRoot(currentState).reversed()
        log.debug { "[$name] entering $toEnter" }
        toEnter.forEach {
            if (!suppressEnterMethods) {
                it.enter(event)
            }
            it.resume(event)
        }
        performTransitions(event)
        processing = false
    }

    inner class StateMachineBuilder {
        internal val mutableTree = mutableMapOf<State<T>, Node<T>>()
        internal var locked = false
        /**
         * Adds a new state. Parent must be added before it is used here as [parent].
         *
         * At least one state must have [initial] == true or [setInitialState]
         */
        fun addState(state: State<T>, parent: State<T>? = null, initial: Boolean = false) {
            check(!locked) { "StateMachineBuilder can only be used once!" }
            val parentNode: Node<T>? = parent?.let {
                requireNotNull(mutableTree[it]) { "Parent $parent must be added before adding a child" }
            }

            mutableTree[state] = Node(state, parentNode)

            if (initial) setInitialState(state)
        }

        fun setInitialState(state: State<T>) {
            currentState = state
            targetState = state
        }

        val states: List<State<T>> get() = mutableTree.keys.toList()
    }
}

/** Node in the state tree */
internal class Node<T>(
        var state: State<T>,
        var parentNode: Node<T>?
)

/**
 * Event handler in a [StateMachine]
 */
abstract class State<T> {
    /** State is entered */
    open fun enter(reason: T) = Unit

    /** State is resumed ([StateMachine] comes out of a hibernation) */
    open fun resume(reason: T) = Unit

    /** State is exited */
    open fun exit(reason: T) = Unit

    /** Process events in the state */
    abstract fun processEvent(event: T): Boolean

    val name: String = javaClass.simpleName

    override fun toString(): String = name
}
