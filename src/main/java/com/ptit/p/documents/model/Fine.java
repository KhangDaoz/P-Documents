package com.ptit.p.documents.model;

public class Fine {
    private int id;
    private String name;
    private double fineRate;
    private String description;

    public Fine() {
    }

    public Fine(int id, String name, double fineRate, String description) {
        this.id = id;
        this.name = name;
        this.fineRate = fineRate;
        this.description = description;
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

    public double getFineRate() {
        return fineRate;
    }

    public void setFineRate(double fineRate) {
        this.fineRate = fineRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
