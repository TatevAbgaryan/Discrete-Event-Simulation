package com.example.tatev.truckworkmodel.components;

/**
 * Created by tatev on 02.02.2016.
 */
public class Route {
    private int terminalID;
    private int truckID;

    public int getSequance() {
        return sequance;
    }

    public void setSequance(int sequance) {
        this.sequance = sequance;
    }

    public int getTruckID() {
        return truckID;
    }

    public void setTruckID(int truckID) {
        this.truckID = truckID;
    }

    public int getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(int terminalID) {
        this.terminalID = terminalID;
    }

    private int sequance;
}
