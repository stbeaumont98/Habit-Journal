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
import org.stbeaumont.habitjournal.notifications.NotificationAlarm;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
                updateNextReminder();
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
            updateNextReminder();

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

    public void updateNextReminder() {
        LocalDate nextAlarmDate = null;
        try {
            nextAlarmDate = NotificationAlarm.getNextAlarmDate(habit, LocalDate.now(), LocalTime.now());
        } catch (Habit.NoLogForDateException e) {
            e.printStackTrace();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

        String nextReminder = "The next reminder for this habit is "
                + nextAlarmDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                + " at " + habit.getReminderTime().format(formatter);
        textNextReminder.setText(nextReminder);
    }

    public interface GoalInfoInterface {
        void updateData();
        void onEditClick(int position);
    }
}
