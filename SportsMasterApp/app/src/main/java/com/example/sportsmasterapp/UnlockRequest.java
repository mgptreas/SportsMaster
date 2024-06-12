package com.example.sportsmasterapp;

public class UnlockRequest {
    private int uID;
    private int eID;

    public UnlockRequest(int uID, int eID) {
        this.uID = uID;
        this.eID = eID;
    }

    public int getUID() {
        return uID;
    }

    public void setUID(int uID) {
        this.uID = uID;
    }

    public int getEID() {
        return eID;
    }

    public void setEID(int eID) {
        this.eID = eID;
    }
}