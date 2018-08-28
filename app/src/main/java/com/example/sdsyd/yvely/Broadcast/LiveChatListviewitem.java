package com.example.sdsyd.yvely.Broadcast;


public class LiveChatListviewitem {

    private String playerId;
    private String playerMessage;

    public void setPlayerId(String id){ playerId = id; }
    public void setPlayerMessage(String message){ playerMessage = message; }

    public String getPlayerId(){ return this.playerId; }
    public String getPlayerMessage(){ return this.playerMessage; }

}
