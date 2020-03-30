package com.better.alarm.statemachine;

public abstract class ComplexTransition<T> extends State<T> {
    abstract public void performComplexTransition();

    @Override
    public final void enter(T reason) {
        performComplexTransition();
    }

    @Override
    public final void resume(T reason) {
        //empty
    }

    @Override
    public final boolean processEvent(T msg) {
        throw new RuntimeException("performComplexTransition() must transit immediately");
    }

    @Override
    public final void exit(T reason) {
        // nothing to do
    }
}
