package org.stbeaumont.habitjournal.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputEditText;

import org.stbeaumont.habitjournal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewHabitActivity extends AppCompatActivity {

    TextInputEditText editTextHabit, editTextTime;
    Button buttonDaily, buttonWeekly, buttonMonthly;
    Button buttonSun, buttonMon, buttonTue, buttonWed, buttonThu, buttonFri, buttonSat;
    Button buttonEveryday;
    ConstraintLayout constraintDaily, constraintWeekly, constraintMonthly;
    Switch switchGoal;
    ConstraintLayout constraintGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_habit);

        editTextHabit = findViewById(R.id.editTextHabit);
        editTextTime = findViewById(R.id.editTextTime);

        buttonDaily = findViewById(R.id.buttonDaily);
        buttonWeekly = findViewById(R.id.buttonWeekly);
        buttonMonthly = findViewById(R.id.buttonMonthly);

        constraintDaily = findViewById(R.id.constraintDaily);
        constraintWeekly = findViewById(R.id.constraintWeekly);
        constraintMonthly = findViewById(R.id.constraintMonthly);

        buttonDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDaily();
            }
        });

        buttonWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWeekly();
            }
        });

        buttonMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMonthly();
            }
        });

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewHabitActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = selectedHour + ":" + selectedMinute;
                        editTextTime.setText(time);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

    }

    public void selectDaily() {
        buttonDaily.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
        buttonDaily.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonWeekly.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonWeekly.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
        buttonMonthly.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonMonthly.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));

        constraintDaily.setVisibility(View.VISIBLE);
        constraintWeekly.setVisibility(View.GONE);
        constraintMonthly.setVisibility(View.GONE);
    }

    public void selectWeekly() {
        buttonWeekly.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
        buttonWeekly.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonDaily.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonDaily.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
        buttonMonthly.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonMonthly.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));

        constraintWeekly.setVisibility(View.VISIBLE);
        constraintDaily.setVisibility(View.GONE);
        constraintMonthly.setVisibility(View.GONE);
    }

    public void selectMonthly() {
        buttonMonthly.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
        buttonMonthly.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonDaily.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonDaily.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));
        buttonWeekly.setTextColor(ContextCompat.getColor(NewHabitActivity.this, R.color.colorPrimary));
        buttonWeekly.setBackgroundColor(ContextCompat.getColor(NewHabitActivity.this, R.color.white));

        constraintMonthly.setVisibility(View.VISIBLE);
        constraintDaily.setVisibility(View.GONE);
        constraintWeekly.setVisibility(View.GONE);
    }
}