package com.geeklone.freedom_gibraltar.helper;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by irfan A.
 */

public class CustomTimePicker implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private final Context context;
    private final EditText view;

    public CustomTimePicker(Context context, EditText view) {
        this.context = context;
        this.view = view;
        this.view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(context, this, hour, minute, false);
        dialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
        String time = hourOfDay + ":" + minutes;
        String newTime = Utils.formatDateTimeFromString(time, "HH:mm", "hh:mm a");

        updateDisplay(newTime);
    }

    private void updateDisplay(String newTime) {
        view.setText(newTime);
    }
}