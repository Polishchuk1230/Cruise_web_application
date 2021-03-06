package ua.com.cruises.model;

public class Cadre implements Pojo {
    private int id;
    private String name;
    private String surname;
    private String position;
    private String characteristic;

    public Cadre(int id, String name, String surname, String position, String characteristic) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.position = position;
        this.characteristic = characteristic;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(String characteristic) {
        this.characteristic = characteristic;
    }
}
