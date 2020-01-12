package com.project.appclo.dataentryapp;

public class EntryOperator {
    String firstName, lastName, Shop, location, userId, userEmpId;

   String Email;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setShop(String shop) {
        Shop = shop;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserEmpId(String userEmpId) {
        this.userEmpId = userEmpId;
    }

    public String getFirstName() {

        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getShop() {
        return Shop;
    }

    public String getLocation() {
        return location;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmpId() {
        return userEmpId;
    }
}
