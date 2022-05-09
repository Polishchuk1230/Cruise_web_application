package ua.com.cruises.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Order implements Pojo, Serializable {
    private int id;
    private int seats;
    private Timestamp bookTime;
    private boolean confirmed;

    private User user;
    private Cruise cruise;

    //-------------------------------------------------------------------------------------------------------Constructor
    public Order(int id, int seats, boolean confirmed) {
        this.id = id;
        this.seats = seats;
        this.confirmed = confirmed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Timestamp getBookTime() {
        return bookTime;
    }

    public void setBookTime(Timestamp bookTime) {
        this.bookTime = bookTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Cruise getCruise() {
        return cruise;
    }

    public void setCruise(Cruise cruise) {
        this.cruise = cruise;
    }
}
