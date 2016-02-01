package com.uni.unidasher.data.entity;

import com.easemob.chat.EMMessage;

/**
 * Created by Administrator on 2015/7/3.
 */
public class ChatInfo {
    String orderId;
    String chatId;
    String chatName;
    String chatLogo;
    String lastMsg;
    String lastTime;
    String shopName;
    int unReadCount = 0;
    EMMessage message;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatLogo() {
        return chatLogo;
    }

    public void setChatLogo(String chatLogo) {
        this.chatLogo = chatLogo;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public void addUnReadCount(boolean isUnRead){
        if(isUnRead){
            unReadCount++;
        }
    }
    public EMMessage getMessage() {
        return message;
    }

    public void setMessage(EMMessage message) {
        this.message = message;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
