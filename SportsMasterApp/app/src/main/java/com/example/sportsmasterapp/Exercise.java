package com.example.sportsmasterapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Exercise implements Parcelable {
    @SerializedName("eID") // Map to the "eID" field in JSON
    private int eID;

    @SerializedName("sID")
    private int sID;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("video")
    private String video;

    @SerializedName("difficulty")
    private int difficulty;

    @SerializedName("field1")
    private int field1;

    @SerializedName("field2")
    private int field2;

    @SerializedName("field3")
    private int field3;

    @SerializedName("field4")
    private int field4;

    @SerializedName("field5")
    private int field5;

    @SerializedName("sport_name") // Map to the "sport_name" field in JSON
    private String sportName;

    @SerializedName("is_unlocked")
    private boolean isUnlocked;


    // Constructor
    public Exercise(int eID, int sID, String name, String description, String video, int difficulty,
                    int field1, int field2, int field3, int field4, int field5, String sportName, boolean isUnlocked) {
        this.eID = eID;
        this.sID = sID;
        this.name = name;
        this.description = description;
        this.video = video;
        this.difficulty = difficulty;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.sportName = sportName;
        this.isUnlocked = isUnlocked;
    }

    // Parcelable Constructor (for passing between Activities)
    protected Exercise(Parcel in) {
        eID = in.readInt();
        sID = in.readInt();
        name = in.readString();
        description = in.readString();
        video = in.readString();
        difficulty = in.readInt();
        field1 = in.readInt();
        field2 = in.readInt();
        field3 = in.readInt();
        field4 = in.readInt();
        field5 = in.readInt();
        sportName = in.readString();
        isUnlocked = in.readByte() != 0; // Read boolean as byte
    }

    // Getters and setters
    public int getEID() {
        return eID;
    }

    public void setEID(int eID) {
        this.eID = eID;
    }

    public int getSID() {
        return sID;
    }

    public void setSID(int sID) {
        this.sID = sID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getField1() {
        return field1;
    }

    public void setField1(int field1) {
        this.field1 = field1;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }

    public int getField3() {
        return field3;
    }

    public void setField3(int field3) {
        this.field3 = field3;
    }

    public int getField4() {
        return field4;
    }

    public void setField4(int field4) {
        this.field4 = field4;
    }

    public int getField5() {
        return field5;
    }

    public void setField5(int field5) {
        this.field5 = field5;
    }

    // New getters for sportName and isUnlocked
    public String getSportName() {
        return sportName;
    }

    public boolean getIsUnlocked() {
        return isUnlocked;
    }

    public void setSportName(String sportName){
        this.sportName = sportName;
    }

    public void setIsUnlocked(boolean isUnlocked){
        this.isUnlocked = isUnlocked;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(eID);
        dest.writeInt(sID);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(video);
        dest.writeInt(difficulty);
        dest.writeInt(field1);
        dest.writeInt(field2);
        dest.writeInt(field3);
        dest.writeInt(field4);
        dest.writeInt(field5);
        dest.writeString(sportName);
        dest.writeByte((byte) (isUnlocked ? 1 : 0)); // Write boolean as byte
    }

    public static final Parcelable.Creator<Exercise> CREATOR = new Parcelable.Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
}
