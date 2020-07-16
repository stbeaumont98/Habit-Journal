package org.stbeaumont.habitjournal.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.stbeaumont.habitjournal.R;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;

public class DayInfoHabitAdapter extends RecyclerView.Adapter<DayInfoHabitAdapter.HabitViewHolder> {

    private ArrayList<Habit> habits;
    private LocalDate day;

    public DayInfoHabitAdapter(ArrayList<Habit> habits, LocalDate day) {
        this.habits = habits;
        this.day = day;
    }

    @NonNull
    @Override
    public DayInfoHabitAdapter.HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.habit_list_item, parent, false);
        return new HabitViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayInfoHabitAdapter.HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);

        TextView textHabitName = holder.habitName;
        TextView textProgress = holder.habitProgress;
        ImageView imageHabitDone = holder.habitDone;

        textHabitName.setText(habit.getName());

        imageHabitDone.setVisibility(View.VISIBLE);
        textProgress.setVisibility(View.GONE);

        try {
            imageHabitDone.setBackgroundResource(habit.checkLogOnDate(day) ? R.drawable.ic_habit_done : R.drawable.ic_habit_not_done);
        } catch (Habit.NoLogForDateException noLogForDateException) {
            noLogForDateException.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public class HabitViewHolder extends RecyclerView.ViewHolder {

        private TextView habitName;
        private TextView habitProgress;
        private ImageView habitDone;

        public HabitViewHolder(@NonNull View v) {
            super(v);
            this.habitName = v.findViewById(R.id.text_habit_name);
            this.habitProgress = v.findViewById(R.id.text_goal_progress);
            this.habitDone = v.findViewById(R.id.image_habit_done);
        }
    }
}
