package com.example.canteenchecker.canteenmanager.core;

public class Rating {

    private final int ratingId;
    private final String username;
    private final String remark;
    private final int ratingPoints;
    private final long timestamp;

    public Rating(int ratingId, String username, String remark,
                  int ratingPoints, long timestamp) {
        this.ratingId = ratingId;
        this.username = username;
        this.remark = remark;
        this.ratingPoints = ratingPoints;
        this.timestamp = timestamp;
    }

    public int getId() {
        return ratingId;
    }

    public String getUserName() {
        return username;
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
