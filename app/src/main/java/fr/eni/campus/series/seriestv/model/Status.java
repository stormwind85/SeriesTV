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

    public static boolean contains(String test) {
        for (Status status : Status.values()) {
            if (status.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}
