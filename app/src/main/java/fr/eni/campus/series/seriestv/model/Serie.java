package fr.eni.campus.series.seriestv.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Serie implements Serializable {
    private Long id;
    private String title;
    private Status status;
    private Double note;
    private String imageUrl;
    private List<Saison> saisons;

    public Serie() {
        saisons = new LinkedList<>();
    }

    public Serie(Long id, String title, Status status, Double note, String imageUrl, List<Saison> saisons) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.note = note;
        this.imageUrl = imageUrl;
        this.saisons = saisons;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setNote(Double note) {
        this.note = note;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setSaisons(List<Saison> saisons) {
        this.saisons = saisons;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return status;
    }

    public Double getNote() {
        return note;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Saison> getSaisons() {
        return saisons;
    }

    public void addSaison(Saison saison) {
        saisons.add(saison);
    }

    @Override
    public String toString() {
        String string = "Serie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status.toString() + '\'' +
                ", note='" + note.toString() + '\'' +
                ", imageUrl='" + imageUrl + '\'';
        if(saisons.size() > 0)
            string += ", saisons=" + saisons.toString();
        string += '}';
        return string;
    }
}
