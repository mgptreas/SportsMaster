package com.example.sportsmasterapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseStatAdapter extends RecyclerView.Adapter<ExerciseStatAdapter.ViewHolder> {

    private List<ExerciseStat> exerciseStats;

    public ExerciseStatAdapter(List<ExerciseStat> exerciseStats) {
        this.exerciseStats = exerciseStats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_stat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseStat stat = exerciseStats.get(position);
        holder.tvExerciseName.setText(stat.getExerciseName());
        holder.tvExerciseName.setTextColor(Color.WHITE);
        holder.tvDescription.setText(stat.getDescription());
        holder.tvDescription.setTextColor(Color.WHITE);
        holder.tvCoT.setText(String.valueOf(stat.getCoT()));
        holder.tvCoT.setTextColor(Color.WHITE);
        holder.tvAvgTOC.setText(String.valueOf(stat.getAvgTOC()));
        holder.tvAvgTOC.setTextColor(Color.WHITE);
        holder.tvAvgChallenging.setText(String.valueOf(stat.getAvgChallenging()));
        holder.tvAvgChallenging.setTextColor(Color.WHITE);
        holder.tvAvgFeedback.setText(String.valueOf(stat.getAvgFeedback()));
        holder.tvAvgFeedback.setTextColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return exerciseStats.size();
    }

    // Update the stats list and notify the adapter of changes
    public void updateStats(List<ExerciseStat> newStats) {
        this.exerciseStats = newStats;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvDescription, tvCoT, tvAvgTOC, tvAvgChallenging, tvAvgFeedback;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvCoT = itemView.findViewById(R.id.tv_cot);
            tvAvgTOC = itemView.findViewById(R.id.tv_avg_toc);
            tvAvgChallenging = itemView.findViewById(R.id.tv_avg_challenging);
            tvAvgFeedback = itemView.findViewById(R.id.tv_avg_feedback);
        }
    }
}
