package com.example.BattleRoyal.bean;

/**
 * @author Mirage
 * @date 2021/4/19
 **/
public class Packet {
    public String message;
    public User loginInfo;
    public PlayerLocation spwan;
    public int userNumber;

    @Override
    public String toString() {
        return "Packet{" +
                "message='" + message + '\'' +
                ", loginInfo=" + loginInfo.toString() +
                ", spwan=" + spwan.toString() +
                '}';
    }
}
