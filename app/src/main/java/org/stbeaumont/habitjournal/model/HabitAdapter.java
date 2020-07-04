package org.stbeaumont.habitjournal.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.stbeaumont.habitjournal.R;

import java.util.ArrayList;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private ArrayList<Habit> habits;

    public HabitAdapter(ArrayList<Habit> habits) {
        this.habits = habits;
    }

    @NonNull
    @Override
    public HabitAdapter.HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.habit_list_item, parent, false);
        return new HabitViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitAdapter.HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);

        TextView textHabitName = holder.habitName;

        textHabitName.setText(habit.getName());
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public class HabitViewHolder extends RecyclerView.ViewHolder {

        private TextView habitName;

        public HabitViewHolder(@NonNull View v) {
            super(v);
            this.habitName = v.findViewById(R.id.textHabitName);
        }
    }
}
