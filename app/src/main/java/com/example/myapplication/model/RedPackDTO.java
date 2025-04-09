package com.example.myapplication.model;


public class RedPackDTO {

    String sendId;
    String nativeUrl;
    //keyUsername
    String talker;
    String headImg = "";
    String nickName = "";
    String sessionUserName = "";
    String ver = "v1.0";
    String timingIdentifier = "";
    String leftButtonContinue = "";
    int channelId = 1;
    int msgType = 1;
    int keyWay = 1;


    public RedPackDTO(String sendId, String nativeUrl, String talker, String headImg, String nickName, String sessionUserName, String ver, String timingIdentifier, String leftButtonContinue, int channelId, int msgType, int keyWay) {
        this.sendId = sendId;
        this.nativeUrl = nativeUrl;
        this.talker = talker;
        this.headImg = headImg;
        this.nickName = nickName;
        this.sessionUserName = sessionUserName;
        this.ver = ver;
        this.timingIdentifier = timingIdentifier;
        this.leftButtonContinue = leftButtonContinue;
        this.channelId = channelId;
        this.msgType = msgType;
        this.keyWay = keyWay;
    }

    public String getSendId() {
        return sendId;
    }

    public String getNativeUrl() {
        return nativeUrl;
    }

    public String getTalker() {
        return talker;
    }

    public String getHeadImg() {
        return headImg;
    }

    public String getNickName() {
        return nickName;
    }

    public String getSessionUserName() {
        return sessionUserName;
    }

    public String getVer() {
        return ver;
    }

    public String getTimingIdentifier() {
        return timingIdentifier;
    }

    public String getLeftButtonContinue() {
        return leftButtonContinue;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getMsgType() {
        return msgType;
    }

    public int getKeyWay() {
        return keyWay;
    }
}
