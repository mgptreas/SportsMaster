package com.example.sportsmasterapp;

public class ExerciseStat {
    private String exerciseName;
    private String description;
    private int CoT;
    private double avgTOC;
    private double avgChallenging;
    private double avgFeedback;

    // Getters and setters
    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCoT() {
        return CoT;
    }

    public void setCoT(int coT) {
        CoT = coT;
    }

    public double getAvgTOC() {
        return avgTOC;
    }

    public void setAvgTOC(double avgTOC) {
        this.avgTOC = avgTOC;
    }

    public double getAvgChallenging() {
        return avgChallenging;
    }

    public void setAvgChallenging(double avgChallenging) {
        this.avgChallenging = avgChallenging;
    }

    public double getAvgFeedback() {
        return avgFeedback;
    }

    public void setAvgFeedback(double avgFeedback) {
        this.avgFeedback = avgFeedback;
    }
}
