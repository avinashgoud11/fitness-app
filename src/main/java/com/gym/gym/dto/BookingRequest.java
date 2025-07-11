package com.gym.gym.dto;

public class BookingRequest {
    private Long memberId;
    private Long classId;
    private double amount;

    public BookingRequest() {
        // Default constructor
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

