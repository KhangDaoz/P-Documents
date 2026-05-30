package com.ptit.p.documents.model;

import java.time.LocalDate;

public class Bill {
    private int id;
    private String bookStatus;
    private LocalDate paymentDate;
    private int overdueDay;
    private double fine;
    private double amount;
    private String paymentType;
    private String note;
    private Borrowing borrowing;

    public Bill() {
    }

    public Bill(int id, String bookStatus, LocalDate paymentDate, int overdueDay, double fine,
                double amount, String paymentType, String note, Borrowing borrowing) {
        this.id = id;
        this.bookStatus = bookStatus;
        this.paymentDate = paymentDate;
        this.overdueDay = overdueDay;
        this.fine = fine;
        this.amount = amount;
        this.paymentType = paymentType;
        this.note = note;
        this.borrowing = borrowing;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getOverdueDay() {
        return overdueDay;
    }

    public void setOverdueDay(int overdueDay) {
        this.overdueDay = overdueDay;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Borrowing getBorrowing() {
        return borrowing;
    }

    public void setBorrowing(Borrowing borrowing) {
        this.borrowing = borrowing;
    }

}
