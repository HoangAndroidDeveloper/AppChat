package com.example.appchat.Object;

import java.io.Serializable;

public class Friend implements Serializable
{
    private long id;
    private String name, avt;

    public Friend() {

    }

    public Friend(long id, String name, String avt) {
        this.id = id;
        this.name = name;
        this.avt = avt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt)
    {
        this.avt = avt;
    }
}
