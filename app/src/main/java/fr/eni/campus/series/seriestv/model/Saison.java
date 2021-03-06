package fr.eni.campus.series.seriestv.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Saison implements Serializable {
    private Integer number;
    private List<Episode> episodes;
    private Serie serie;

    public Saison() {
        episodes = new LinkedList<>();
    }

    public Saison(Integer number, List<Episode> episodes, Serie serie) {
        this.number = number;
        this.episodes = episodes;
        this.serie = serie;
    }

    public Integer getNumber() {
        return number;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
    }

    @Override
    public String toString() {
        if(number != null) {
            String string = "Saison{" +
                    "number=" + number +
                    ", serie=" + serie.getTitle();
            if(episodes.size() > 0) {
                string += ", episodes=" + episodes.toString();
            }
            string += '}';
            return string;
        }
        return "Pas de détails de saison";
    }
}
