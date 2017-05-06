package br.usp.ime.checkattendance.models;

/**
 * Created by kanashiro on 5/6/17.
 */

public class Seminar {

    private String id;
    private String name;

    public Seminar(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "ID: " + this.id + "; Name: " + this.name;
    }
}
