package com.geeklone.freedom_gibraltar.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class Group implements Serializable {

    private String id = "";
//    private String chatUserId = "";
    private String name = "";
    private String groupImg = "";
//    private String deviceToken = "";
    private String lastMsg = "";
    private String lastMsgBy = "";
    private String createdDate = "";
    private String createdBy = "";
    private String createdById = "";
    private String updatedDate = "";
    private String unreadMsgCount = "";
//    private boolean msgRead;
    private String msgDateTime = "";
    private String memberCount = "";
    List<String > members;

    public Group() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupImg() {
        return groupImg;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsgBy() {
        return lastMsgBy;
    }

    public void setLastMsgBy(String lastMsgBy) {
        this.lastMsgBy = lastMsgBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(String unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getMsgDateTime() {
        return msgDateTime;
    }

    public void setMsgDateTime(String msgDateTime) {
        this.msgDateTime = msgDateTime;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }
}

