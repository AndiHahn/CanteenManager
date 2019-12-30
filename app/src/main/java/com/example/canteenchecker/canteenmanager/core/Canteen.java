package com.example.canteenchecker.canteenmanager.core;

public class Canteen {

    private final String id;
    private final String name;
    private final String setMeal;
    private final float setMealPrice;
    private final String location;
    private final String website;
    private final String phoneNumber;
    private final float averageRating;
    private final int averageWaitingTime;
    private final Rating[] ratings;

    public Canteen(String id, String name, String setMeal, float setMealPrice,
                   String location, String website, String phoneNumber,
                   float averageRating,  int averageWaitingTime, Rating[] ratings) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.setMeal = setMeal;
        this.setMealPrice = setMealPrice;
        this.averageRating = averageRating;
        this.location = location;
        this.averageWaitingTime = averageWaitingTime;
        this.ratings = ratings;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getSetMeal() {
        return setMeal;
    }

    public float getSetMealPrice() {
        return setMealPrice;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public String getLocation() {
        return location;
    }

    public int getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public Rating[] getRatings() {
        return ratings;
    }

}
