package org.stbeaumont.habitjournal.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import org.stbeaumont.habitjournal.model.HabitAdapter;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.temporal.WeekFields;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements HabitAdapter.HabitClickListener, GoalInfoDialogFragment.GoalInfoInterface {

    private LocalDate selectedDate = null;
    private ArrayList<Habit> habits = new ArrayList<>();
    private HabitAdapter habitAdapter;
    private CalendarView calendarView;
    private GoalInfoDialogFragment goalInfoDialogFragment;
    private DataStorage dataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AndroidThreeTen.init(this);

        dataStorage = new DataStorage(this);

        habits.addAll(dataStorage.loadData());

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayShowTitleEnabled(false);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditHabitActivity(EditHabitActivity.MODE_NEW, null);
            }
        });

        calendarView = findViewById(R.id.calendarView);

        setupCalendar(calendarView);

        RecyclerView habitRecyclerView = findViewById(R.id.rv);

        habitAdapter = new HabitAdapter(habits, this);

        habitRecyclerView.setAdapter(habitAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);

        habitRecyclerView.setLayoutManager(llm);

    }

    public void setupCalendar(CalendarView calendarView) {
        final LocalDate today = LocalDate.now();

        YearMonth currentMonth = YearMonth.now();
        YearMonth firstMonth = currentMonth.minusMonths(10);
        YearMonth lastMonth = currentMonth.plusMonths(10);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();

        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);

        calendarView.setDayBinder(new DayBinder<DayViewContainer>() {
            @Override
            public DayViewContainer create(View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(DayViewContainer container, CalendarDay day) {
                container.setDay(day);
                TextView textDay = container.getTextDay();
                View dotView = container.getEventView();

                textDay.setText(String.format(Locale.getDefault(), "%d", day.getDate().getDayOfMonth()));

                if (day.getOwner() == DayOwner.THIS_MONTH) {
                    if (day.getDate().equals(today)) {
                        textDay.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.white));
                        textDay.setBackgroundResource(R.drawable.bg_selected_day);
                        dotView.setVisibility(View.INVISIBLE);
                    } else {
                        textDay.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.black));
                        textDay.setBackgroundResource(0);
                        dotView.setVisibility(View.INVISIBLE);
                        for (Habit h : habits) {
                            dotView.setVisibility(h.checkLogOnDate(day.getDate()) ? View.VISIBLE : View.INVISIBLE);
                        }
                    }
                } else {
                    textDay.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.darker_gray));
                    dotView.setVisibility(View.INVISIBLE);
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
                textYear.setText(String.format(Locale.getDefault(), "%d", month.getYear()));
            }
        });
    }

    public void openEditHabitActivity(int mode, @Nullable Integer position) {
        Intent i = new Intent(this, EditHabitActivity.class);
        if (mode == EditHabitActivity.MODE_EDIT) {
            Habit h = habits.get(position);
            i.putExtra("pos", position);
        }
        i.putExtra("habits", habits);
        i.putExtra("mode", mode);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                habits.clear();
                ArrayList<Habit> h = data.getParcelableArrayListExtra("habits");
                habits.addAll(h);
                habitAdapter.notifyDataSetChanged();
            }
        }
    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void updateData() {
        habitAdapter.notifyDataSetChanged();
        calendarView.notifyCalendarChanged();
        dataStorage.updateData(habits);
    }

    @Override
    public void onEditClick(int position) {
        goalInfoDialogFragment.dismiss();
        openEditHabitActivity(EditHabitActivity.MODE_EDIT, position);
    }

    class DayViewContainer extends ViewContainer {
        private CalendarDay day;
        private TextView textDay;
        private View eventView;
        public DayViewContainer(View view) {
            super(view);
            textDay = view.findViewById(R.id.calendarDayText);
            eventView = view.findViewById(R.id.eventDotView);

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

        public View getEventView() {
            return eventView;
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

    @Override
    public void onHabitClick(int position) {
        goalInfoDialogFragment = new GoalInfoDialogFragment(habits.get(position), position,this);
        goalInfoDialogFragment.show(getSupportFragmentManager(), "goal_info");
    }
}