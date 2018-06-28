package fr.eni.campus.series.seriestv.model;

public enum Status {
    CONTINUING("En cours"),
    ENDED("Finie");

    private String name;

    Status(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
