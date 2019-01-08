package e.kyco.fitnessapp;

import android.widget.ImageView;

public class Workout {
    private String WorkoutType;
    private String WorkoutLength;
    private int WorkoutImage;

    public Workout(){

    }

    public Workout(String workoutType, String workoutLength, int workOutImage){
        WorkoutType = workoutType;
        WorkoutLength = workoutLength;
        WorkoutImage = workOutImage;

    }

    public void setWorkoutType(String workoutType) {
        WorkoutType = workoutType;
    }

    public void setWorkoutLength(String workoutLength) {
        WorkoutLength = workoutLength;
    }

    public void setWorkoutImage(int workoutImage) {
        WorkoutImage = workoutImage;
    }

    public String getWorkoutType(){
        return WorkoutType;
    }
    public String getWorkoutLength(){
        return WorkoutLength;
    }
    public int getWorkoutImage(){
        return WorkoutImage;
    }
}
