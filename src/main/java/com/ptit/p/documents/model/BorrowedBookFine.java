package com.ptit.p.documents.model;

public class BorrowedBookFine {
    private int id;
    private double fineRate;
    private double totalFine;
    private Fine fine;

    public BorrowedBookFine() {
    }

    public BorrowedBookFine(int id, double fineRate, double totalFine, Fine fine) {
        this.id = id;
        this.fineRate = fineRate;
        this.totalFine = totalFine;
        this.fine = fine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getFineRate() {
        return fineRate;
    }

    public void setFineRate(double fineRate) {
        this.fineRate = fineRate;
    }

    public double getTotalFine() {
        return totalFine;
    }

    public void setTotalFine(double totalFine) {
        this.totalFine = totalFine;
    }

    public Fine getFine() {
        return fine;
    }

    public void setFine(Fine fine) {
        this.fine = fine;
    }

}
