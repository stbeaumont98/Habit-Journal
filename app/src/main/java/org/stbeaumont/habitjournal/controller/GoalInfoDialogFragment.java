package org.stbeaumont.habitjournal.controller;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.stbeaumont.habitjournal.R;
import org.stbeaumont.habitjournal.model.Habit;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;

public class GoalInfoDialogFragment  extends AppCompatDialogFragment {

    Habit habit;
    int habitPos;
    GoalInfoInterface goalInfoInterface;
    Boolean isPercent = true;

    //Views
    TextView textGoalName;
    CheckBox checkBox;
    TextView textProgress;
    ProgressBar bgProgressBar;
    ProgressBar fgProgressBar;
    TextView textNextReminder;

    private ArrayList<DayOfWeek> dayList = new ArrayList<>();

    public GoalInfoDialogFragment(Habit habit, int habitPos, GoalInfoInterface goalInfoInterface) {
        this.habit = habit;
        this.habitPos = habitPos;
        this.goalInfoInterface = goalInfoInterface;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.goal_info_layout, null);

        dayList.add(DayOfWeek.SUNDAY);
        dayList.add(DayOfWeek.MONDAY);
        dayList.add(DayOfWeek.TUESDAY);
        dayList.add(DayOfWeek.WEDNESDAY);
        dayList.add(DayOfWeek.THURSDAY);
        dayList.add(DayOfWeek.FRIDAY);
        dayList.add(DayOfWeek.SATURDAY);

        textGoalName = v.findViewById(R.id.info_habit_name);
        checkBox = v.findViewById(R.id.check_box);
        textProgress = v.findViewById(R.id.info_goal_progress);
        bgProgressBar = v.findViewById(R.id.bg_progressbar);
        fgProgressBar = v.findViewById(R.id.fg_progressbar);
        textNextReminder = v.findViewById(R.id.info_next_reminder);

        try {
            checkBox.setChecked(habit.checkLogOnDate(LocalDate.now()));
        } catch (Habit.NoLogForDateException e) {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                habit.logDate(LocalDate.now(), isChecked);
                fgProgressBar.setProgress(habit.getNumberOfSuccesses());
                updateProgress();
            }
        });

        textGoalName.setText(habit.getName());

        if (habit.hasGoal()) {
            textProgress.setVisibility(View.VISIBLE);
            bgProgressBar.setVisibility(View.VISIBLE);
            fgProgressBar.setVisibility(View.VISIBLE);

            bgProgressBar.setMax(habit.getGoal());
            bgProgressBar.setProgress(habit.getGoal());

            fgProgressBar.setMax(habit.getGoal());
            fgProgressBar.setProgress(habit.getNumberOfSuccesses());

            updateProgress();

            textProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPercent = !isPercent;
                    updateProgress();
                }
            });
        } else {
            textProgress.setVisibility(View.GONE);
            bgProgressBar.setVisibility(View.GONE);
            fgProgressBar.setVisibility(View.GONE);
        }

        LocalDate nextAlarmDate = getNextAlarmDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        String nextReminder = "The next reminder for this habit is "
                + nextAlarmDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                + " at " + habit.getReminderTime().format(formatter);
        textNextReminder.setText(nextReminder);

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goalInfoInterface.onEditClick(habitPos);
            }
        });

        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        goalInfoInterface.updateData();
    }

    public void updateProgress() {
        float percent = (float) habit.getNumberOfSuccesses() / (float) habit.getGoal();
        percent *= 100;
        String progress = (isPercent ? String.format(Locale.getDefault(), "%.2f", percent) + "%" : habit.getNumberOfSuccesses() + "/" + habit.getGoal());
        textProgress.setText(progress);
    }

    public LocalDate getNextAlarmDate() {
        LocalDate d = LocalDate.now();
        LocalTime t = LocalTime.now();
        if (habit.getFrequency() == 0) { //daily
            int i = dayList.indexOf(d.getDayOfWeek());
            while (!habit.getDaysOfWeek().get(i)) {
                i++;
                if (i >= 7) {
                    i = 0;
                }
            }
            return d.with(TemporalAdjusters.nextOrSame(dayList.get(i)));
        } else if (habit.getFrequency() == 1) { //weekly
            if (d.compareTo(habit.getWeeklyStartDate()) < 0) {
                // if we haven't hit the start date yet
                return habit.getWeeklyStartDate();
            } else {
                LocalDate weekly = habit.getWeeklyStartDate();
                while (d.compareTo(weekly) > 0) {
                    // if current date is after the weekly date, add to the weeks by the interval and check again
                    weekly = weekly.plusWeeks(habit.getWeeklyInterval());
                }
                return weekly;
            }
        } else { //monthly
            if (habit.getDayOfMonth() == 1) {
                if ((d.compareTo(d.with(TemporalAdjusters.firstDayOfMonth())) == 0 && t.compareTo(habit.getReminderTime()) > 0) || d.compareTo(d.with(TemporalAdjusters.firstDayOfMonth())) > 0) {
                    // if it is today and the reminder time has passed
                    return d.with(TemporalAdjusters.firstDayOfNextMonth());
                } else {
                    // otherwise get the first day of the current month
                    return d.with(TemporalAdjusters.firstDayOfMonth());
                }
            } else if (habit.getDayOfMonth() == 31) {
                if (d.compareTo(d.with(TemporalAdjusters.lastDayOfMonth())) <= 0 && t.compareTo(habit.getReminderTime()) < 0) {
                    // if it is today or before and hasn't passed the reminder time
                    return d.with(TemporalAdjusters.lastDayOfMonth());
                } else {
                    // return the last day of next month
                    LocalDate dateNextMonth = d.with(TemporalAdjusters.firstDayOfNextMonth());
                    return dateNextMonth.with(TemporalAdjusters.lastDayOfMonth());
                }
            } else {
                if (d.getDayOfMonth() < habit.getDayOfMonth()) {
                    // if you haven't passed it yet
                    return d.withDayOfMonth(habit.getDayOfMonth());
                } else {
                    // if you've already passed it for this month
                    LocalDate dateNextMonth = d.with(TemporalAdjusters.firstDayOfNextMonth());
                    return dateNextMonth.withDayOfMonth(habit.getDayOfMonth());
                }
            }
        }
    }

    public interface GoalInfoInterface {
        void updateData();
        void onEditClick(int position);
    }
}
