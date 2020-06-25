package org.stbeaumont.habitjournal.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.stbeaumont.habitjournal.model.Habit;
import org.stbeaumont.habitjournal.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ArrayList<Habit> habits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
    }

    public void openNewHabitActivity() {
        Intent i = new Intent(this, NewHabitActivity.class);
        startActivity(i);
    }
}