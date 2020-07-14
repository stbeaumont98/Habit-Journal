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
import java.util.Locale;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private ArrayList<Habit> habits;
    private HabitClickListener habitClickListener;

    public HabitAdapter(ArrayList<Habit> habits, HabitClickListener habitClickListener) {
        this.habits = habits;
        this.habitClickListener = habitClickListener;
    }

    @NonNull
    @Override
    public HabitAdapter.HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.habit_list_item, parent, false);

        System.out.println("onCreateViewHolder");
        return new HabitViewHolder(v, habitClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitAdapter.HabitViewHolder holder, int position) {
        Habit habit = habits.get(position);

        TextView textHabitName = holder.habitName;
        TextView textProgress = holder.habitProgress;
        ImageView imageHabitDone = holder.habitDone;

        textHabitName.setText(habit.getName());

        imageHabitDone.setVisibility(View.GONE);
        if (habit.hasGoal()) {
            textProgress.setVisibility(View.VISIBLE);
            float percent = (float) habit.getNumberOfSuccesses() / (float) habit.getGoal();
            percent *= 100;
            String progress = String.format(Locale.getDefault(), "%.2f", percent) + "%";
            textProgress.setText(progress);
        } else {
            textProgress.setVisibility(View.GONE);
        }

        System.out.println("onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public class HabitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView habitName;
        private TextView habitProgress;
        private ImageView habitDone;
        private HabitClickListener habitClickListener;

        public HabitViewHolder(@NonNull View v, HabitClickListener habitClickListener) {
            super(v);
            this.habitName = v.findViewById(R.id.text_habit_name);
            this.habitProgress = v.findViewById(R.id.text_goal_progress);
            this.habitDone = v.findViewById(R.id.image_habit_done);

            this.habitClickListener = habitClickListener;

            v.setOnClickListener(this);

            System.out.println("HabitViewHolder");
        }

        @Override
        public void onClick(View v) {
            habitClickListener.onHabitClick(getAdapterPosition());
        }
    }

    public interface HabitClickListener {
        void onHabitClick(int position);
    }
}
