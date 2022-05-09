package ua.com.cruises.model;

/*
* This ua.com.cruises.model class presents a boat on the firm's balance.
* */

public class Boat implements Pojo {
    private int id;
    private String name;
    private int capacity;
    private Crew crew;

    public Boat(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Crew getCrew() {
        return crew;
    }

    public void setCrew(Crew crew) {
        this.crew = crew;
    }

    @Override
    public String toString() {
        return String.format("\uD83D\uDEF3<br /> id[%d], name[%s], capacity[%d]", getId(), getName(), getCapacity());
    }
}
