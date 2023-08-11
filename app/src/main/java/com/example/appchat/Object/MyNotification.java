package com.example.appchat.Object;

public class MyNotification
{
    private String message, time, avt, name;

    public MyNotification() {

    }

    public MyNotification(String message, String time, String avt, String name) {
        this.message = message;
        this.time = time;
        this.avt = avt;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
