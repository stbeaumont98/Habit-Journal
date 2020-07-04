package org.stbeaumont.habitjournal.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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

import org.stbeaumont.habitjournal.model.Event;
import org.stbeaumont.habitjournal.model.Habit;
import org.stbeaumont.habitjournal.R;
import org.stbeaumont.habitjournal.model.HabitAdapter;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.temporal.WeekFields;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private LocalDate selectedDate = null;
    private ArrayList<Habit> habits = new ArrayList<>();
    private CalendarView calendarView;
    private HashMap<LocalDate, ArrayList<Event>> events = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AndroidThreeTen.init(this);

        final LocalDate today = LocalDate.now();

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

        calendarView = findViewById(R.id.calendarView);

        YearMonth currentMonth = YearMonth.now();
        YearMonth firstMonth = currentMonth.minusMonths(10);
        YearMonth lastMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();

        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);

        class DayViewContainer extends ViewContainer {
            private CalendarDay day;
            private TextView textDay;
            public DayViewContainer(View view) {
                super(view);
                textDay = view.findViewById(R.id.calendarDayText);

                textDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (day.getOwner() == DayOwner.THIS_MONTH) {
                            if (selectedDate == day.getDate()) {
                                selectedDate = null;
                                calendarView.notifyDayChanged(day);
                            } else {
                                LocalDate oldDate = selectedDate;
                                selectedDate = day.getDate();
                                calendarView.notifyDateChanged(day.getDate());
                                if (oldDate != null)
                                {
                                    calendarView.notifyDateChanged(oldDate);
                                }
                            }
                        }
                    }
                });
            }

            public void setDay(CalendarDay day) {
                this.day = day;
            }

            public TextView getTextDay() {
                return textDay;
            }
        }

        class MonthHeaderContainer extends ViewContainer {
            private TextView textMonth;
            private TextView textYear;

            public MonthHeaderContainer(View view) {
                super(view);
                textMonth = view.findViewById(R.id.textMonth);
                textYear = view.findViewById(R.id.textYear);
            }

            public TextView getTextMonth() {
                return textMonth;
            }

            public TextView getTextYear() {
                return textYear;
            }
        }

        calendarView.setDayBinder(new DayBinder<DayViewContainer>() {
            @Override
            public DayViewContainer create(View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(DayViewContainer container, CalendarDay day) {
                container.setDay(day);
                TextView textDay = container.getTextDay();
                textDay.setText(Integer.toString(day.getDate().getDayOfMonth()));
                if (day.getOwner() == DayOwner.THIS_MONTH) {
                    if (day.getDate() == selectedDate) {
                        textDay.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.white));
                        textDay.setBackgroundResource(R.drawable.bg_selected_day);
                    } else if (day.getDate().equals(today)) {
                        textDay.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.colorAccent));
                        textDay.setBackgroundResource(0);
                    } else {
                        textDay.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.black));
                        textDay.setBackgroundResource(0);
                    }
                } else {
                    textDay.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.darker_gray));
                }
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthHeaderContainer>() {
            @Override
            public MonthHeaderContainer create(View view) {
                return new MonthHeaderContainer(view);
            }

            @Override
            public void bind(MonthHeaderContainer container, CalendarMonth month) {
                TextView textMonth = container.getTextMonth();
                TextView textYear = container.getTextYear();
                textMonth.setText(capitalize(month.getYearMonth().getMonth().name().toLowerCase()));
                textYear.setText(Integer.toString(month.getYear()));
            }
        });

        RecyclerView habitRecyclerView = findViewById(R.id.rv);

        HabitAdapter habitAdapter = new HabitAdapter(habits);

        habitRecyclerView.setAdapter(habitAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        habitRecyclerView.setLayoutManager(llm);

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