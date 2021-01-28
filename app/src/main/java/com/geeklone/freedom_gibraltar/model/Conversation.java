package com.geeklone.freedom_gibraltar.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Conversation implements Serializable {

    private String id = "";
    private String msg = "";
    private String timeStamp = "";
    private String msgType = "";
    private String name = "";
    private String from = "";
    private boolean sending;
    private String msgTime = "";
    private String conversationDate = "";
    private boolean conversationDateVisibility ;

    public Conversation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isSending() {
        return sending;
    }

    public void setSending(boolean sending) {
        this.sending = sending;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public String getConversationDate() {
        return conversationDate;
    }

    public void setConversationDate(String conversationDate) {
        this.conversationDate = conversationDate;
    }

    public void setMsgTime(String msgTime) {


        this.msgTime = msgTime;
    }

    public boolean isConversationDateVisibility() {
        return conversationDateVisibility;
    }

    public void setConversationDateVisibility(boolean conversationDateVisibility) {
        this.conversationDateVisibility = conversationDateVisibility;
    }
}

