package org.stbeaumont.habitjournal.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.stbeaumont.habitjournal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class NewHabitActivity extends AppCompatActivity {

    private static final int SUN = 0;
    private static final int MON = 1;
    private static final int TUE = 2;
    private static final int WED = 3;
    private static final int THU = 4;
    private static final int FRI = 5;
    private static final int SAT = 6;

    private TextInputEditText editTextHabit;
    private TextView textReminderTime;
    private Button buttonEveryday;
    private ConstraintLayout constraintGoal;
    private NumberPicker pickerDayOfMonth;

    private ArrayList<Button> frequencyButtons = new ArrayList<>();
    private ArrayList<Button> dayButtons = new ArrayList<>();
    private ArrayList<Button> dayOfMonthButtons = new ArrayList<>();
    private ArrayList<ConstraintLayout> frequencyConstraints = new ArrayList<>();
    private ArrayList<Boolean> daysOfWeek = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_habit);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        for (int i = 0; i < 7; i++) { //make it true for every day by default
            daysOfWeek.add(true);
        }

        Button buttonDaily = findViewById(R.id.buttonDaily);
        Button buttonWeekly = findViewById(R.id.buttonWeekly);
        Button buttonMonthly = findViewById(R.id.buttonMonthly);
        Button buttonSun = findViewById(R.id.buttonSunday);
        Button buttonMon = findViewById(R.id.buttonMonday);
        Button buttonTue = findViewById(R.id.buttonTuesday);
        Button buttonWed = findViewById(R.id.buttonWednesday);
        Button buttonThu = findViewById(R.id.buttonThursday);
        Button buttonFri = findViewById(R.id.buttonFriday);
        Button buttonSat = findViewById(R.id.buttonSaturday);
        Button buttonFirstDay = findViewById(R.id.buttonFirst);
        Button buttonLastDay = findViewById(R.id.buttonLast);
        Button buttonCustomDay = findViewById(R.id.buttonCustom);

        frequencyButtons.add(buttonDaily);
        frequencyButtons.add(buttonWeekly);
        frequencyButtons.add(buttonMonthly);

        dayButtons.add(buttonSun);
        dayButtons.add(buttonMon);
        dayButtons.add(buttonTue);
        dayButtons.add(buttonWed);
        dayButtons.add(buttonThu);
        dayButtons.add(buttonFri);
        dayButtons.add(buttonSat);

        dayOfMonthButtons.add(buttonFirstDay);
        dayOfMonthButtons.add(buttonLastDay);
        dayOfMonthButtons.add(buttonCustomDay);

        buttonEveryday = findViewById(R.id.buttonEveryday);

        editTextHabit = findViewById(R.id.editTextHabit);

        textReminderTime = findViewById(R.id.textReminderTime);

        ConstraintLayout constraintDaily = findViewById(R.id.constraintDaily);
        ConstraintLayout constraintWeekly = findViewById(R.id.constraintWeekly);
        ConstraintLayout constraintMonthly = findViewById(R.id.constraintMonthly);
        constraintGoal = findViewById(R.id.constraintGoal);

        frequencyConstraints.add(constraintDaily);
        frequencyConstraints.add(constraintWeekly);
        frequencyConstraints.add(constraintMonthly);

        NumberPicker pickerWeeks = findViewById(R.id.numberPickerWeeks);
        pickerDayOfMonth = findViewById(R.id.numberPickerDayOfMonth);
        pickerDayOfMonth.setEnabled(false);
        NumberPicker pickerGoal = findViewById(R.id.numberPickerGoal);

        Switch switchGoal = findViewById(R.id.switchGoal);

        switchGoal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    constraintGoal.setVisibility(View.VISIBLE);
                } else {
                    constraintGoal.setVisibility(View.GONE);
                }
            }
        });

        pickerWeeks.setMinValue(1);
        pickerWeeks.setMaxValue(6);

        pickerDayOfMonth.setMinValue(1);
        pickerDayOfMonth.setMaxValue(31);

        pickerGoal.setMinValue(1);
        pickerGoal.setMaxValue(999);

        for (int i = 0; i < 3; i++) {
            final int finalI = i;
            frequencyButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectFrequency(finalI);
                }
            });
        }

        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            dayButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEveryDay()) {
                        for (int j = 0; j < 7; j++) {   //inverts the values
                            boolean tempInverse = !daysOfWeek.get(j);
                            daysOfWeek.set(j, tempInverse);
                        }
                        boolean temp = daysOfWeek.get(finalI);
                        daysOfWeek.set(finalI, !temp);
                    } else if (!isEveryDay() && isOnlyOne() && daysOfWeek.get(finalI)) {
                        //do nothing
                    } else {
                        boolean temp = daysOfWeek.get(finalI);
                        daysOfWeek.set(finalI, !temp);
                    }
                    updateDayButtons();
                }
            });
        }

        for (int i = 0; i < 3; i++) {
            final int finalI = i;
            dayOfMonthButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDayOfMonth(finalI);
                }
            });
        }

        buttonEveryday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 7; i++) { //set every day to true
                    daysOfWeek.set(i, true);
                }
                updateDayButtons();
            }
        });

        textReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewHabitActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = String.format(Locale.getDefault(),"%02d:%02d", (selectedHour > 12 ? selectedHour - 12 : selectedHour), selectedMinute) + (selectedHour > 12 ? " PM" : " AM");
                        textReminderTime.setText(time);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        updateDayButtons();

    }

    public void selectFrequency(int index) {
        for (int i = 0; i < 3; i++) {
            if (i == index) {
                frequencyButtons.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                frequencyButtons.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                frequencyConstraints.get(i).setVisibility(View.VISIBLE);
            } else {
                frequencyButtons.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                frequencyButtons.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                frequencyConstraints.get(i).setVisibility(View.GONE);
            }
        }
    }

    public void selectDayOfMonth(int index) {
        for (int i = 0; i < 3; i++) {
            if (i == index) {
                dayOfMonthButtons.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                dayOfMonthButtons.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
            } else {
                dayOfMonthButtons.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                dayOfMonthButtons.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
            }
            if (index == 2) {
                pickerDayOfMonth.setEnabled(true);
            } else {
                pickerDayOfMonth.setEnabled(false);
            }
        }
    }

    public boolean isEveryDay() {
        return (daysOfWeek.get(SUN) && daysOfWeek.get(MON) && daysOfWeek.get(TUE) && daysOfWeek.get(WED) && daysOfWeek.get(THU) && daysOfWeek.get(FRI) && daysOfWeek.get(SAT));
    }

    public boolean isOnlyOne() {
        if (Collections.frequency(daysOfWeek, true) == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void updateDayButtons() {
        if (isEveryDay()) {
            for (int i = 0; i < 7; i++) {
                if (daysOfWeek.get(i)) {
                    dayButtons.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                    dayButtons.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                } else {
                    dayButtons.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                    dayButtons.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                }
            }
            buttonEveryday.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
            buttonEveryday.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        } else {
            for (int i = 0; i < 7; i++) {
                if (daysOfWeek.get(i)) {
                    dayButtons.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                    dayButtons.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                } else {
                    dayButtons.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                    dayButtons.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                }
            }
            buttonEveryday.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
            buttonEveryday.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
        }
    }
}