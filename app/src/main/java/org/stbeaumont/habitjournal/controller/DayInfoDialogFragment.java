package org.stbeaumont.habitjournal.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.stbeaumont.habitjournal.R;
import org.stbeaumont.habitjournal.model.DayInfoHabitAdapter;
import org.stbeaumont.habitjournal.model.Habit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class DayInfoDialogFragment extends AppCompatDialogFragment {

    private LocalDate date;
    private ArrayList<Habit> habits;

    public DayInfoDialogFragment(ArrayList<Habit> habits, LocalDate date) {
        this.date = date;
        this.habits = new ArrayList<>(getHabitsForDay(habits, date));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.day_info_layout, null);

        TextView textDate = v.findViewById(R.id.info_selected_date);
        RecyclerView rvDayList = v.findViewById(R.id.rv_day_list);

        textDate.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));

        DayInfoHabitAdapter habitAdapter = new DayInfoHabitAdapter(habits, date);
        rvDayList.setAdapter(habitAdapter);
        rvDayList.setLayoutManager(new LinearLayoutManager(getActivity()));

        builder.setView(v);
        return builder.create();
    }

    public ArrayList<Habit> getHabitsForDay(ArrayList<Habit> habits, LocalDate date) {
        ArrayList<Habit> tempHabits = new ArrayList<>();
        for (Habit h : habits) {
            try {
                h.checkLogOnDate(date);
                tempHabits.add(h);
            } catch (Habit.NoLogForDateException e) {
                System.out.println(e);
            }
        }
        return tempHabits;
    }

}
