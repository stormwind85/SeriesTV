package fr.eni.campus.series.seriestv.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Serie implements Serializable {
    private Long id;
    private String title;
    private String status;
    private String imageUrl;
    private Integer creationDate;
    private List<Saison> saisons;

    public Serie() {
        saisons = new LinkedList<>();
    }

    public Serie(Long id, String title, String status, String imageUrl, Integer creationDate, List<Saison> saisons) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.imageUrl = imageUrl;
        this.creationDate = creationDate;
        this.saisons = saisons;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCreationDate(Integer creationDate) {
        this.creationDate = creationDate;
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

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getCreationDate() {
        return creationDate;
    }

    public List<Saison> getSaisons() {
        return saisons;
    }

    public void addSaison(Saison saison) {
        saisons.add(saison);
    }

    @Override
    public String toString() {
        return "Serie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", creationDate=" + creationDate +
                ", saisons=" + saisons.toString() +
                '}';
    }
}
