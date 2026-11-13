package com.example.ktgk.model;

public class Field {
    private int id;
    private String code;
    private String name;
    private String address;
    private double pricePerHour;
    private boolean isBooked;

    public Field() {
    }

    public Field(int id, String code, String name, String address, double pricePerHour) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
    }

    public Field(String code, String name, String address, double pricePerHour) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public String getStatusText() {
        return isBooked ? "Đã được đặt" : "Còn trống";
    }

    @Override
    public String toString() {
        return "[" + code + "] " + name;
    }
}
