package com.example.sportsmasterapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnlockedExercisesAdapter extends RecyclerView.Adapter<UnlockedExercisesAdapter.ViewHolder> {

    private final Map<String, List<Exercise>> exercisesBySport;
    private final OnExerciseClickListener listener; // Removed unlockedExerciseIds

    public UnlockedExercisesAdapter(Map<String, List<Exercise>> exercisesBySport, OnExerciseClickListener listener) {
        this.exercisesBySport = exercisesBySport;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false); // Using a simple list item layout for now
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int currentPosition = 0;
        for (Map.Entry<String, List<Exercise>> entry : exercisesBySport.entrySet()) {
            String sportName = entry.getKey();
            List<Exercise> exercises = entry.getValue();

            if (currentPosition == position) {
                holder.textView.setText(sportName);
                holder.textView.setTextColor(Color.YELLOW);
                return;
            }
            currentPosition++;

            for (Exercise exercise : exercises) {
                if (currentPosition == position) {
                    holder.textView.setText(exercise.getName());
                    holder.textView.setTextColor(exercise.getIsUnlocked() ? Color.GREEN : Color.RED);

                    holder.itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
                    return;
                }
                currentPosition++;
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (List<Exercise> exercises : exercisesBySport.values()) {
            count += exercises.size() + 1; // +1 for the sport header
        }
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }
}
