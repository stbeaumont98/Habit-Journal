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
import android.widget.EditText;
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

public class EditHabitActivity extends AppCompatActivity {

    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;

    int mode;
    int position;

    private int intReminderHour = 12;
    private int intReminderMin = 0;
    private boolean boolReminderSet = false;

    Habit habit;

    private TextInputEditText editTextHabit;
    private TextView textViewReminderTime;
    private Button buttonEveryday;
    private CheckBox checkBoxGoal;
    private NumberPicker pickerWeeks;
    private ConstraintLayout constraintGoal;
    private EditText editTextGoal;
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
        setContentView(R.layout.activity_edit_habit);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

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

        pickerWeeks = findViewById(R.id.numberPickerWeeks);
        pickerDayOfMonth = findViewById(R.id.numberPickerDayOfMonth);
        pickerDayOfMonth.setEnabled(false);
        editTextGoal = findViewById(R.id.editTextGoal);

        pickerWeeks.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                habit.setWeeklyInterval(newVal);
            }
        });

        pickerDayOfMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                habit.setDayOfMonth(newVal);
            }
        });

        checkBoxGoal = findViewById(R.id.checkBoxGoal);

        checkBoxGoal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    constraintGoal.setVisibility(View.VISIBLE);
                    habit.setHasGoal(true);
                } else {
                    constraintGoal.setVisibility(View.GONE);
                    habit.setHasGoal(false);
                }
            }
        });

        pickerWeeks.setMinValue(1);
        pickerWeeks.setMaxValue(6);

        pickerDayOfMonth.setMinValue(1);
        pickerDayOfMonth.setMaxValue(31);

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
                            boolean tempInverse = !habit.getDaysOfWeek().get(j);
                            habit.getDaysOfWeek().set(j, tempInverse);
                        }
                        boolean temp = habit.getDaysOfWeek().get(finalI);
                        habit.getDaysOfWeek().set(finalI, !temp);
                    } else if (!isEveryDay() && isOnlyOne() && habit.getDaysOfWeek().get(finalI)) {
                        //do nothing
                    } else {
                        boolean temp = habit.getDaysOfWeek().get(finalI);
                        habit.getDaysOfWeek().set(finalI, !temp);
                    }
                    habit.setDaysOfWeek(habit.getDaysOfWeek());
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
                    habit.getDaysOfWeek().set(i, true);
                }
                habit.setDaysOfWeek(habit.getDaysOfWeek());
                updateDayButtons();
            }
        });

        textViewReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = EditHabitActivity.this.getLayoutInflater();
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

                new MaterialAlertDialogBuilder(EditHabitActivity.this)
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
                habit.setName(Objects.requireNonNull(editTextHabit.getText()).toString());
                String goalValue = editTextGoal.getText().toString();
                habit.setGoal(goalValue.isEmpty() ? 0 : Integer.parseInt(goalValue));
                Intent intent = new Intent();
                if (mode == MODE_EDIT) {
                    intent.putExtra("pos", position);
                }
                intent.putExtra("mode", mode);
                intent.putExtra("habit", habit);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        position = intent.getIntExtra("pos", 0);

        if (mode == MODE_EDIT) {
            habit = intent.getParcelableExtra("habit");
        } else {
            habit = new Habit();
        }

        updateFields();
    }

    public void updateFields() {
        editTextHabit.setText(habit.getName());
        selectFrequency(habit.getFrequency());
        updateDayButtons();
        pickerWeeks.setValue(habit.getWeeklyInterval());
        if (habit.getDayOfMonth() == 1) {
            selectDayOfMonth(0);
        } else if (habit.getDayOfMonth() == 31) {
            selectDayOfMonth(1);
        } else {
            selectDayOfMonth(2);
        }
        pickerDayOfMonth.setValue(habit.getDayOfMonth());
        checkBoxGoal.setChecked(habit.hasGoal());
        editTextGoal.setText(String.format(Locale.getDefault(), "%d", habit.getGoal()));
        setReminderTime(toHours(habit.getReminderTime()), toMin(habit.getReminderTime()));
    }

    public void setReminderTime(int hour, int min) {
        String time = String.format(Locale.getDefault(),"%d:%02d", (hour == 0 ? 12 : (hour > 12 ? hour - 12 : hour)), min) + (hour >= 12 ? " PM" : " AM");
        textViewReminderTime.setText(time);

        intReminderHour = hour;
        intReminderMin = min;
        habit.setReminderTime(toMilliseconds(hour, min));
        boolReminderSet = true;
    }

    public void selectFrequency(int index) {
        for (int i = 0; i < 3; i++) {
            if (i == index) {
                frequencyButtonList.get(i).setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
                frequencyButtonList.get(i).setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
                frequencyConstraintList.get(i).setVisibility(View.VISIBLE);
                habit.setFrequency(i);;
            } else {
                frequencyButtonList.get(i).setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
                frequencyButtonList.get(i).setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
                frequencyConstraintList.get(i).setVisibility(View.GONE);
            }
        }
    }

    public void selectDayOfMonth(int index) {
        for (int i = 0; i < 3; i++) {
            if (i == index) {
                dayOfMonthButtonList.get(i).setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
                dayOfMonthButtonList.get(i).setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
            } else {
                dayOfMonthButtonList.get(i).setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
                dayOfMonthButtonList.get(i).setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
            }
        }
        switch (index) {
            case 0:
                habit.setDayOfMonth(1);
                break;
            case 1:
                habit.setDayOfMonth(31);
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
                if (habit.getDaysOfWeek().get(i)) {
                    dayButtonList.get(i).setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
                    dayButtonList.get(i).setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
                } else {
                    dayButtonList.get(i).setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
                    dayButtonList.get(i).setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
                }
            }
            buttonEveryday.setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
            buttonEveryday.setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
        } else {
            for (int i = 0; i < 7; i++) {
                if (habit.getDaysOfWeek().get(i)) {
                    dayButtonList.get(i).setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
                    dayButtonList.get(i).setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
                } else {
                    dayButtonList.get(i).setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
                    dayButtonList.get(i).setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
                }
            }
            buttonEveryday.setTextColor(ContextCompat.getColor(EditHabitActivity.this, R.color.colorPrimary));
            buttonEveryday.setBackgroundColor(ContextCompat.getColor(EditHabitActivity.this, R.color.white));
        }
    }

    public boolean isEveryDay() {
        return (habit.getDaysOfWeek().get(Habit.SUN)
                && habit.getDaysOfWeek().get(Habit.MON)
                && habit.getDaysOfWeek().get(Habit.TUE)
                && habit.getDaysOfWeek().get(Habit.WED)
                && habit.getDaysOfWeek().get(Habit.THU)
                && habit.getDaysOfWeek().get(Habit.FRI)
                && habit.getDaysOfWeek().get(Habit.SAT));
    }

    public boolean isOnlyOne() {
        if (Collections.frequency(habit.getDaysOfWeek(), true) == 1) {
            return true;
        } else {
            return false;
        }
    }

    public long toMilliseconds(int hour, int min) {
        return ((long) hour * 3600000) + ((long) min * 60000);
    }

    public int toHours(long milliseconds) {
        return (int) (milliseconds / 3600000);
    }

    public int toMin(long milliseconds) {
        long remainder = milliseconds % 3600000;
        return (int) remainder / 60000;
    }
}