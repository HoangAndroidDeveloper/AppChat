package com.example.appchat.Object;

public class User {
    private String name, sex, age, dayOfBrith
            , favorite;
    private String avatar, coverimage;
    private long id;

    public User() {
    }

    public User(String name, String sex, String age, String dayOfBrith, String favorite, String avatar, String coverimage, long id) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.dayOfBrith = dayOfBrith;
        this.favorite = favorite;
        this.avatar = avatar;
        this.coverimage = coverimage;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDayOfBrith() {
        return dayOfBrith;
    }

    public void setDayOfBrith(String dayOfBrith) {
        this.dayOfBrith = dayOfBrith;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCoverimage() {
        return coverimage;
    }

    public void setCoverimage(String coverimage) {
        this.coverimage = coverimage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
