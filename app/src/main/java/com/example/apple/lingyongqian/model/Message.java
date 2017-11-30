package com.example.apple.lingyongqian.model;

/**
 * Created by apple on 2017/3/28.
 */

public class Message {
    private String meaage;
    private String code;

    public String getMeaage() {
        return meaage;
    }

    public void setMeaage(String meaage) {
        this.meaage = meaage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Message(String meaage, String code) {
        this.meaage = meaage;
        this.code = code;
    }

    public Message() {
    }
}
