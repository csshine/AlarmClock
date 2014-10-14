package com.premium.alarm.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.github.androidutils.logger.Logger;
import com.premium.alarm.model.AlarmsManager;
import com.premium.alarm.model.interfaces.Alarm;
import com.premium.alarm.model.interfaces.AlarmNotFoundException;
import com.premium.alarm.model.interfaces.IAlarmsManager;
import com.premium.alarm.model.interfaces.Intents;
import com.premium.alarm.presenter.TimePickerDialogFragment.AlarmTimePickerDialogHandler;
import com.premium.alarm.presenter.TimePickerDialogFragment.OnAlarmTimePickerCanceledListener;

public class TransparentActivity extends Activity implements AlarmTimePickerDialogHandler,
        OnAlarmTimePickerCanceledListener {

    private IAlarmsManager alarmsManager;
    private Alarm alarm;
    Logger log;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        alarmsManager = AlarmsManager.getAlarmsManager();
        log = Logger.getDefaultLogger();
        Intent intent = getIntent();
        log.d("Intent in TransparentActivity was received");
        int id = intent.getIntExtra(Intents.EXTRA_ID, -1);
        try {
            alarm = alarmsManager.getAlarm(id);
            log.d("Alarm, that has to be rescheduled:" + alarm.getNextTime());
        } catch (AlarmNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TimePickerDialogFragment.showTimePicker(getFragmentManager());
        log.d("Do you see time picker?");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.getDefaultLogger().d("TransparentActivity.onDestroy()");
        // No longer care about the alarm being killed.

    }

    @Override
    public void onTimePickerCanceled() {
        finish();

    }

    @Override
    public void onDialogTimeSet(int hourOfDay, int minute) {
        alarm.snooze(hourOfDay, minute);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
