package ua.com.cruises.model;

import ua.com.cruises.repository.CadreDao;

import java.util.ArrayList;
import java.util.List;

public class Crew implements Pojo {
    private int id;
    private List<Cadre> cadres = new ArrayList<>();

    public Crew(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Cadre> getCadres() {
        return cadres;
    }

    public void setCadres(List<Cadre> cadres) {
        this.cadres = cadres;
    }

    public boolean saveStateInDB() {
        return CadreDao.getInstance().assignCrewId(id, (Cadre[]) cadres.toArray());
    }
}
