package com.example.ktgk.model;

import java.util.Locale;

public class Booking {
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private int id;
    private int fieldId;
    private String fieldName;
    private String fieldCode;
    private String customerName;
    private String customerPhone;
    private String bookingDate; // Ngày thuê (Định dạng YYYY-MM-DD)
    private int startHour;      // Giờ bắt đầu (0..23)
    private int startMinute;    // Phút bắt đầu (0..59)
    private int durationHours;  // Số giờ thuê (1..24)
    private double totalPrice;
    private String status;      // Trạng thái đơn đặt (ACTIVE: Đang đặt, CANCELLED: Đã hủy)

    public Booking() {
        this.status = STATUS_ACTIVE;
    }

    public Booking(int id, int fieldId, String customerName, String customerPhone, String bookingDate,
                   int startHour, int startMinute, int durationHours, double totalPrice, String status) {
        this.id = id;
        this.fieldId = fieldId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.bookingDate = bookingDate;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.durationHours = durationHours;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormattedStartTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);
    }

    public String getFormattedEndTime() {
        int endHour = (startHour + durationHours) % 24;
        return String.format(Locale.getDefault(), "%02d:%02d", endHour, startMinute);
    }

    public String getTimeRangeText() {
        return getFormattedStartTime() + " - " + getFormattedEndTime() + " (" + durationHours + " giờ)";
    }

    public String getStatusText() {
        if (STATUS_CANCELLED.equals(status)) {
            return "Đã hủy";
        }
        return "Đã đặt";
    }

    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
}
