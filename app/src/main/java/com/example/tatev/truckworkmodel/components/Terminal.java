package com.example.tatev.truckworkmodel.components;

/**
 * Created by tatev on 02.02.2016.
 */
public class Terminal {
    public int getTerminalID() {
        return TerminalID;
    }

    public void setTerminalID(int terminalID) {
        TerminalID = terminalID;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int TerminalID;
    private float latitude;
    private float longitude;
    private int duration;
}
