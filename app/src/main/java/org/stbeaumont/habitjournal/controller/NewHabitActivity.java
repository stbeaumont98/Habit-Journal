package org.stbeaumont.habitjournal.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.stbeaumont.habitjournal.R;
import org.stbeaumont.habitjournal.model.Habit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class NewHabitActivity extends AppCompatActivity {

    private int intReminderHour = 12;
    private int intReminderMin = 0;
    private boolean boolReminderSet = false;

    private String habitName;
    private int intFrequency; //daily, weekly, or monthly
    private ArrayList<Boolean> daysOfWeekList = new ArrayList<>();
    private int intWeeklyInterval = 0;
    private int intDayOfMonth = 0;
    private boolean boolHasGoal = false;
    private int intGoal = 0;
    private long longReminderTime = toMilliseconds(intReminderHour, intReminderMin);

    private TextInputEditText editTextHabit;
    private TextView textViewReminderTime;
    private Button buttonEveryday;
    private ConstraintLayout constraintGoal;
    private NumberPicker pickerDayOfMonth;
    private TimePicker timePicker;
    ExtendedFloatingActionButton fab;

    private ArrayList<Button> frequencyButtonList = new ArrayList<>();
    private ArrayList<Button> dayButtonList = new ArrayList<>();
    private ArrayList<Button> dayOfMonthButtonList = new ArrayList<>();
    private ArrayList<ConstraintLayout> frequencyConstraintList = new ArrayList<>();

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
            daysOfWeekList.add(true);
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

        fab = findViewById(R.id.fabCreateHabit);

        frequencyButtonList.add(buttonDaily);
        frequencyButtonList.add(buttonWeekly);
        frequencyButtonList.add(buttonMonthly);

        dayButtonList.add(buttonSun);
        dayButtonList.add(buttonMon);
        dayButtonList.add(buttonTue);
        dayButtonList.add(buttonWed);
        dayButtonList.add(buttonThu);
        dayButtonList.add(buttonFri);
        dayButtonList.add(buttonSat);

        dayOfMonthButtonList.add(buttonFirstDay);
        dayOfMonthButtonList.add(buttonLastDay);
        dayOfMonthButtonList.add(buttonCustomDay);

        buttonEveryday = findViewById(R.id.buttonEveryday);

        editTextHabit = findViewById(R.id.editTextHabit);

        textViewReminderTime = findViewById(R.id.textReminderTime);

        ConstraintLayout constraintDaily = findViewById(R.id.constraintDaily);
        ConstraintLayout constraintWeekly = findViewById(R.id.constraintWeekly);
        ConstraintLayout constraintMonthly = findViewById(R.id.constraintMonthly);
        constraintGoal = findViewById(R.id.constraintGoal);

        frequencyConstraintList.add(constraintDaily);
        frequencyConstraintList.add(constraintWeekly);
        frequencyConstraintList.add(constraintMonthly);

        NumberPicker pickerWeeks = findViewById(R.id.numberPickerWeeks);
        pickerDayOfMonth = findViewById(R.id.numberPickerDayOfMonth);
        pickerDayOfMonth.setEnabled(false);
        NumberPicker pickerGoal = findViewById(R.id.numberPickerGoal);

        pickerWeeks.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                intWeeklyInterval = newVal;
            }
        });

        pickerDayOfMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                intDayOfMonth = newVal;
            }
        });

        pickerGoal.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                intGoal = newVal;
            }
        });

        CheckBox checkBoxGoal = findViewById(R.id.checkBoxGoal);

        checkBoxGoal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    constraintGoal.setVisibility(View.VISIBLE);
                    boolHasGoal = true;
                } else {
                    constraintGoal.setVisibility(View.GONE);
                    boolHasGoal = false;
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
            frequencyButtonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectFrequency(finalI);
                }
            });
        }

        for (int i = 0; i < 7; i++) {
            final int finalI = i;
            dayButtonList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEveryDay()) {
                        for (int j = 0; j < 7; j++) {   //inverts the values
                            boolean tempInverse = !daysOfWeekList.get(j);
                            daysOfWeekList.set(j, tempInverse);
                        }
                        boolean temp = daysOfWeekList.get(finalI);
                        daysOfWeekList.set(finalI, !temp);
                    } else if (!isEveryDay() && isOnlyOne() && daysOfWeekList.get(finalI)) {
                        //do nothing
                    } else {
                        boolean temp = daysOfWeekList.get(finalI);
                        daysOfWeekList.set(finalI, !temp);
                    }
                    updateDayButtons();
                }
            });
        }

        for (int i = 0; i < 3; i++) {
            final int finalI = i;
            dayOfMonthButtonList.get(i).setOnClickListener(new View.OnClickListener() {
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
                    daysOfWeekList.set(i, true);
                }
                updateDayButtons();
            }
        });

        textViewReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = NewHabitActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.timepicker_dialog_layout, null);
                timePicker = view.findViewById(R.id.timePicker);

                Calendar calendar = Calendar.getInstance();
                int hour, min;
                if (!boolReminderSet) {
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    min = calendar.get(Calendar.MINUTE);

                    if (min > 0 && min < 30) {
                        min = 30;
                    } else {
                        hour++;
                        min = 0;
                    }

                    if (hour >= 24) {
                        hour = 0;
                    }
                } else {
                    hour = intReminderHour;
                    min = intReminderMin;
                }

                timePicker.setHour(hour);
                timePicker.setMinute(min);

                new MaterialAlertDialogBuilder(NewHabitActivity.this)
                        .setView(view)
                        .setNegativeButton(getString(R.string.timepicker_negative), null)
                        .setPositiveButton(getString(R.string.timepicker_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setReminderTime(timePicker.getHour(), timePicker.getMinute());
                            }
                        }).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habitName = Objects.requireNonNull(editTextHabit.getText()).toString();
                Habit h = new Habit(habitName, intFrequency, daysOfWeekList, intWeeklyInterval, intDayOfMonth, boolHasGoal, intGoal, longReminderTime);
                Intent intent = new Intent();
                intent.putExtra("habit", h);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        updateDayButtons();
    }

    public void setReminderTime(int hour, int min) {
        String time = String.format(Locale.getDefault(),"%d:%02d", (hour == 0 ? 12 : (hour > 12 ? hour - 12 : hour)), min) + (hour >= 12 ? " PM" : " AM");
        textViewReminderTime.setText(time);

        intReminderHour = hour;
        intReminderMin = min;
        longReminderTime = toMilliseconds(hour, min);
        boolReminderSet = true;
    }

    public void selectFrequency(int index) {
        for (int i = 0; i < 3; i++) {
            if (i == index) {
                frequencyButtonList.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                frequencyButtonList.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                frequencyConstraintList.get(i).setVisibility(View.VISIBLE);
                intFrequency = i;
            } else {
                frequencyButtonList.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                frequencyButtonList.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                frequencyConstraintList.get(i).setVisibility(View.GONE);
            }
        }
    }

    public void selectDayOfMonth(int index) {
        for (int i = 0; i < 3; i++) {
            if (i == index) {
                dayOfMonthButtonList.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                dayOfMonthButtonList.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
            } else {
                dayOfMonthButtonList.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                dayOfMonthButtonList.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
            }
        }
        switch (index) {
            case 0:
                intDayOfMonth = 1;
                break;
            case 1:
                intDayOfMonth = 31;
            case 2:
                if (index == 2) {
                    pickerDayOfMonth.setEnabled(true);
                } else {
                    pickerDayOfMonth.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    public void updateDayButtons() {
        if (isEveryDay()) {
            for (int i = 0; i < 7; i++) {
                if (daysOfWeekList.get(i)) {
                    dayButtonList.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                    dayButtonList.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                } else {
                    dayButtonList.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                    dayButtonList.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                }
            }
            buttonEveryday.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
            buttonEveryday.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        } else {
            for (int i = 0; i < 7; i++) {
                if (daysOfWeekList.get(i)) {
                    dayButtonList.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                    dayButtonList.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                } else {
                    dayButtonList.get(i).setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
                    dayButtonList.get(i).setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
                }
            }
            buttonEveryday.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
            buttonEveryday.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
        }
    }

    public boolean isEveryDay() {
        return (daysOfWeekList.get(Habit.SUN)
                && daysOfWeekList.get(Habit.MON)
                && daysOfWeekList.get(Habit.TUE)
                && daysOfWeekList.get(Habit.WED)
                && daysOfWeekList.get(Habit.THU)
                && daysOfWeekList.get(Habit.FRI)
                && daysOfWeekList.get(Habit.SAT));
    }

    public boolean isOnlyOne() {
        if (Collections.frequency(daysOfWeekList, true) == 1) {
            return true;
        } else {
            return false;
        }
    }

    public long toMilliseconds(int hour, int min) {
        return ((long) hour * 3600000) + ((long) min * 60000);
    }
}