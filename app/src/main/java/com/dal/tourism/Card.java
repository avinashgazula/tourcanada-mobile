package com.dal.tourism;

public class Card {
    private long id;
    private String title;
    private String cardnum;
    private String expiry;
    private String cvv;
    private String date;
    private String time;

    Card(String title, String cardnum, String expiry, String cvv, String date, String time){
        this.title = title;
        this.cardnum = cardnum;
        this.expiry = expiry;
        this.cvv = cvv;
        this.date = date;
        this.time = time;
    }

    Card(long id, String title, String cardnum, String expiry, String cvv, String date, String time){
        this.id = id;
        this.title = title;
        this.cardnum = cardnum;
        this.expiry = expiry;
        this.cvv = cvv;
        this.date = date;
        this.time = time;
    }
    Card(){
       // empty constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCardnum() {
        return cardnum;
    }

    public void setCardnum(String cardnum) {
        this.cardnum = cardnum;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
