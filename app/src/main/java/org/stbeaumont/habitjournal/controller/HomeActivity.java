package org.stbeaumont.habitjournal.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.stbeaumont.habitjournal.model.Habit;
import org.stbeaumont.habitjournal.R;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.YearMonth;
import org.threeten.bp.temporal.WeekFields;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    ArrayList<Habit> habits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AndroidThreeTen.init(this);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewHabitActivity();
            }
        });

        CalendarView calendarView = findViewById(R.id.calendarView);

        YearMonth currentMonth = YearMonth.now();
        YearMonth firstMonth = currentMonth.minusMonths(10);
        YearMonth lastMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();

        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);

        class DayViewContainer extends ViewContainer {
            public TextView textDay;
            public DayViewContainer(View view) {
                super(view);
                textDay = view.findViewById(R.id.calendarDayText);
            }
        }

        calendarView.setDayBinder(new DayBinder<DayViewContainer>() {
            @Override
            public DayViewContainer create(View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(DayViewContainer container, CalendarDay day) {
                container.textDay.setText(Integer.toString(day.getDate().getDayOfMonth()));
                if (day.getOwner() == DayOwner.THIS_MONTH) {
                    container.textDay.setTextColor(Color.BLACK);
                } else {
                    container.textDay.setTextColor(Color.GRAY);
                }
            }
        });


        class MonthHeaderContainer extends ViewContainer {
            public TextView textMonth;
            public TextView textYear;
            public MonthHeaderContainer(View view) {
                super(view);
                textMonth = view.findViewById(R.id.textMonth);
                textYear = view.findViewById(R.id.textYear);
            }
        }

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthHeaderContainer>() {
            @Override
            public MonthHeaderContainer create(View view) {
                return new MonthHeaderContainer(view);
            }

            @Override
            public void bind(MonthHeaderContainer container, CalendarMonth month) {
                container.textMonth.setText(capitalize(month.getYearMonth().getMonth().name().toLowerCase()));
                container.textYear.setText(Integer.toString(month.getYear()));
            }
        });
    }

    public void openNewHabitActivity() {
        Intent i = new Intent(this, NewHabitActivity.class);
        startActivityForResult(i, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Habit h = data.getParcelableExtra("habit");
                Gson gson = new Gson();
                System.out.println(gson.toJson(h));
                habits.add(h);

            }
        }
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}