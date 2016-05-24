package graduation.tatev.myapplication.components;

import java.util.Date;

/**
 * Created by Tatka on 4/24/2016.
 */
public class Container {

    private Terminal departureTerminal;
    private Terminal destinationTerminal;
    private Date recoveryTime;
    private Date arrivalTime;
    private Date readyTime;
    private int quantity;

    public Container() {
    }
    public Container(Container container){
        this.departureTerminal = container.getDepartureTerminal();
        this.destinationTerminal = container.getDestinationTerminal();
        this.recoveryTime = container.getRecoveryTime();
        this.arrivalTime = container.getArrivalTime();
        this.readyTime = container.getReadyTime();
        this.quantity = container.getQuantity();
    }

    public Date getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(Date readyTime) {
        this.readyTime = readyTime;
    }

    public Terminal getDepartureTerminal() {
        return departureTerminal;
    }

    public void setDepartureTerminal(Terminal departureTerminal) {
        this.departureTerminal = departureTerminal;
    }

    public Terminal getDestinationTerminal() {
        return destinationTerminal;
    }

    public void setDestinationTerminal(Terminal destinationTerminal) {
        this.destinationTerminal = destinationTerminal;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(Date recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public boolean isDelayed() {
        return recoveryTime.before(arrivalTime);
    }
}
