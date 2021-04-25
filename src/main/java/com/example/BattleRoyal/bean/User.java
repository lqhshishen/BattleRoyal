package com.example.BattleRoyal.bean;

/**
 * @author Mirage
 * @date 2021/4/19
 **/
public class User {
    int userId;
    String username;
    int userModel;

    public int getUserModel() {
        return userModel;
    }

    public void setUserModel(int userModel) {
        this.userModel = userModel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", userModel=" + userModel +
                '}';
    }
}
