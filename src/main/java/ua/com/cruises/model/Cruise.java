package ua.com.cruises.model;

/*
* Model class ua.com.cruises.model.Cruise presents a trip on a boat on some route.
* */

import ua.com.cruises.repository.CruiseDao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Cruise implements Pojo {
    private int id;
    private Date startDate;
    private Date endDate;
    private int cost;

    private Boat boat;
    private List<Port> ports = new ArrayList<>();

    //-------------------------------------------------------------------------------------------------------Constructor
    public Cruise(int id, Date startDate, Date endDate, int cost) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
    }

    //---------------------------------------------------------------------------------------------------Getters/Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Boat getBoat() {
        return boat;
    }

    public void setBoat(Boat boat) {
        this.boat = boat;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    //-----------------------------------------------------------------------------------------------------Other methods

    /*
    * Returns duration of a cruise in days.
    * */
    public long getDuration() {
        return (getEndDate().getTime() - getStartDate().getTime())/1000/60/60/24;
    }

    /*
    * Returns a cruise's status depending on remaining days till start date and the cruise's end date.
    * */
    public CruiseStatus getCruiseStatus() {
        final int DAYS_TILL_START_REGISTER_IS_CLOSED = 3;
        long remainDays = ((getStartDate().getTime() - new Date(System.currentTimeMillis()).getTime())/1000/60/60/24);

        if (remainDays >= DAYS_TILL_START_REGISTER_IS_CLOSED)
            return CruiseStatus.REGISTRATION_IN_PROGRESS;

        else if (remainDays > 0)
            return CruiseStatus.REGISTRATION_CLOSED;

        else if (Math.abs(remainDays) <= getDuration())
            return CruiseStatus.IN_PROGRESS;

        else
            return CruiseStatus.COMPLETED;
    }

    /*
    * Returns amount of booked seats
    * */
    public int getBookedSeats() {
        return CruiseDao.getInstance().getBookedSeats(this);
    }
}
