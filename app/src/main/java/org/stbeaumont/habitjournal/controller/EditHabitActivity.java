package org.stbeaumont.habitjournal.controller;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.stbeaumont.habitjournal.R;
import org.stbeaumont.habitjournal.model.Habit;
import org.stbeaumont.habitjournal.model.NotificationAlarm;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

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

    private LocalDate weeklyStartingDate = LocalDate.now();
    private int intReminderHour = 12;
    private int intReminderMin = 0;
    private boolean boolReminderSet = false;

    Habit habit;
    ArrayList<Habit> habits;

    private TextInputEditText editTextHabit;
    private TextView textViewReminderTime;
    private TextView textViewPrevWeekDate;
    private Button buttonEveryday;
    private CheckBox checkBoxGoal;
    private NumberPicker pickerWeeks;
    private ConstraintLayout constraintGoal;
    private EditText editTextGoal;
    private NumberPicker pickerDayOfMonth;
    private DatePicker datePicker;
    private TimePicker timePicker;

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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        Button buttonDaily = findViewById(R.id.button_daily);
        Button buttonWeekly = findViewById(R.id.button_weekly);
        Button buttonMonthly = findViewById(R.id.button_monthly);
        Button buttonSun = findViewById(R.id.button_sunday);
        Button buttonMon = findViewById(R.id.button_monday);
        Button buttonTue = findViewById(R.id.button_tuesday);
        Button buttonWed = findViewById(R.id.button_wednesday);
        Button buttonThu = findViewById(R.id.button_thursday);
        Button buttonFri = findViewById(R.id.button_friday);
        Button buttonSat = findViewById(R.id.button_saturday);
        Button buttonFirstDay = findViewById(R.id.button_first);
        Button buttonLastDay = findViewById(R.id.button_last);
        Button buttonCustomDay = findViewById(R.id.button_custom);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab_save);

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

        buttonEveryday = findViewById(R.id.button_everyday);

        editTextHabit = findViewById(R.id.edit_text_habit);

        textViewPrevWeekDate = findViewById(R.id.text_start_date);
        textViewReminderTime = findViewById(R.id.text_reminder_time);

        ConstraintLayout constraintDaily = findViewById(R.id.constraint_daily);
        ConstraintLayout constraintWeekly = findViewById(R.id.constraint_weekly);
        ConstraintLayout constraintMonthly = findViewById(R.id.constraint_monthly);
        constraintGoal = findViewById(R.id.constraint_goal);

        frequencyConstraintList.add(constraintDaily);
        frequencyConstraintList.add(constraintWeekly);
        frequencyConstraintList.add(constraintMonthly);

        pickerWeeks = findViewById(R.id.picker_weeks);
        pickerDayOfMonth = findViewById(R.id.picker_day_of_month);
        pickerDayOfMonth.setEnabled(false);
        editTextGoal = findViewById(R.id.edit_text_goal);

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

        checkBoxGoal = findViewById(R.id.check_box_goal);

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

        textViewPrevWeekDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = EditHabitActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.datepicker_dialog_layout, null);
                datePicker = view.findViewById(R.id.date_picker);

                datePicker.updateDate(weeklyStartingDate.getYear(), weeklyStartingDate.getMonthValue() - 1, weeklyStartingDate.getDayOfMonth());

                new MaterialAlertDialogBuilder(EditHabitActivity.this)
                        .setView(view)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setWeeklyStartingDate(LocalDate.of(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
                            }
                        }).show();
            }
        });

        textViewReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = EditHabitActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.timepicker_dialog_layout, null);
                timePicker = view.findViewById(R.id.time_picker);

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
                                setReminderTime(LocalTime.of(timePicker.getHour(), timePicker.getMinute()));
                            }
                        }).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStorage data = new DataStorage(getApplicationContext());
                habit.setName(Objects.requireNonNull(editTextHabit.getText()).toString());
                String goalValue = editTextGoal.getText().toString();
                habit.setGoal(goalValue.isEmpty() ? 0 : Integer.parseInt(goalValue));
                Intent i = new Intent();
                if (mode == MODE_EDIT) {
                    habits.set(position, habit);
                    data.updateData(habits);
                    NotificationAlarm notificationAlarm = new NotificationAlarm(getApplicationContext(), position);
                    notificationAlarm.scheduleNextNotification(LocalDate.now(), LocalTime.now());
                } else {
                    habits.add(habit);
                    data.updateData(habits);
                    NotificationAlarm notificationAlarm = new NotificationAlarm(getApplicationContext(), habits.size() - 1);
                    notificationAlarm.scheduleNextNotification(LocalDate.now(), LocalTime.now());
                }
                i.putExtra("habits", habits);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent i = new Intent();
                i.putExtra("habits", habits);
                setResult(RESULT_OK, i);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        position = intent.getIntExtra("pos", 0);
        habits = intent.getParcelableArrayListExtra("habits");

        if (mode == MODE_EDIT) {
            habit = habits.get(position);
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
        setWeeklyStartingDate(habit.getWeeklyStartDate());
        if (habit.getDayOfMonth() == 1) {
            selectDayOfMonth(0);
        } else if (habit.getDayOfMonth() == 31) {
            selectDayOfMonth(1);
        } else {
            selectDayOfMonth(2);
            pickerDayOfMonth.setValue(habit.getDayOfMonth());
        }
        checkBoxGoal.setChecked(habit.hasGoal());
        editTextGoal.setText(String.format(Locale.getDefault(), "%d", habit.getGoal()));
        setReminderTime(habit.getReminderTime());
    }

    public void setReminderTime(LocalTime reminderTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String time = reminderTime.format(formatter);
        textViewReminderTime.setText(time);

        habit.setReminderTime(reminderTime);
        boolReminderSet = true;
    }

    public void setWeeklyStartingDate(LocalDate weeklyStartingDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.getDefault());
        String text = weeklyStartingDate.format(formatter);
        textViewPrevWeekDate.setText(text);

        this.weeklyStartingDate = weeklyStartingDate;

        habit.setWeeklyStartDate(weeklyStartingDate);
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
}