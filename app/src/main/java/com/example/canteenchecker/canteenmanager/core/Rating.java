package com.example.canteenchecker.canteenmanager.core;

public class Rating {

    private final int id;
    private final String userName;
    private final String remark;
    private final int ratingPoints;
    private final long timestamp;

    public Rating(int id, String userName, String remark,
                  int ratingPoints, long timestamp) {
        this.id = id;
        this.userName = userName;
        this.remark = remark;
        this.ratingPoints = ratingPoints;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getRemark() {
        return remark;
    }

    public int getRatingPoints() {
        return ratingPoints;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
