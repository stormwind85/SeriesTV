package fr.eni.campus.series.seriestv.model;

import java.io.Serializable;
import java.util.Date;

public class Episode implements Serializable {
    private Integer number;
    private String title;
    private Date sortieDate;
    private Saison saison;

    public Episode() {
    }

    public Episode(Integer number, String title, Date sortieDate, Saison saison) {
        this.number = number;
        this.title = title;
        this.sortieDate = sortieDate;
        this.saison = saison;
    }

    public Integer getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public Date getSortieDate() {
        return sortieDate;
    }

    public Saison getSaison() {
        return saison;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSortieDate(Date sortieDate) {
        this.sortieDate = sortieDate;
    }

    public void setSaison(Saison saison) {
        this.saison = saison;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "number=" + number +
                ", title='" + title + '\'' +
                ", sortieDate=" + sortieDate +
                '}';
    }
}
