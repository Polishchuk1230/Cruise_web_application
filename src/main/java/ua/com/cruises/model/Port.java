package ua.com.cruises.model;

/*
* Model class port presents one of the real existing seaports.
* */

public class Port implements Pojo {
    private int id;
    private String country;
    private String city;

    public Port(int id, String country, String city) {
        this.id = id;
        this.country = country;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return String.format("\u2693 id[%d] %s %s", getId(), getCountry(), getCity());
    }
}
