package com.example.BattleRoyal.responsebean;

/**
 * @author MirageLee
 */
public class BaseResponse {
    public static int OK = 1;
    public static int ERROR = 2;

    private int responseCode;
    private String responseMessage;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
