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
import org.threeten.bp.LocalDate;

public class GoalInfoDialogFragment  extends AppCompatDialogFragment {

    Habit habit;

    public GoalInfoDialogFragment(Habit habit) {
        this.habit = habit;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.goal_info_layout, null);

        TextView textGoalName = v.findViewById(R.id.info_habit_name);
        CheckBox checkBox = v.findViewById(R.id.check_box);
        TextView textProgress = v.findViewById(R.id.info_goal_progress);
        ProgressBar bgProgressBar = v.findViewById(R.id.bg_progressbar);
        ProgressBar fgProgressBar = v.findViewById(R.id.fg_progressbar);
        TextView textNextReminder = v.findViewById(R.id.info_next_reminder);

        checkBox.setChecked(habit.checkLogOnDate(LocalDate.now()));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                habit.logDate(LocalDate.now(), isChecked);
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

            String progress = habit.getNumberOfSuccesses() + "/" + habit.getGoal();
            textProgress.setText(progress);
        } else {
            textProgress.setVisibility(View.GONE);
            bgProgressBar.setVisibility(View.GONE);
            fgProgressBar.setVisibility(View.GONE);
        }

        String nextReminder = "The next reminder for this habit is ";
        textNextReminder.setText(nextReminder);

        builder.setView(v);

        return builder.create();
    }

    //TODO: Update Calendar and RecyclerView when exiting the fragment
}
