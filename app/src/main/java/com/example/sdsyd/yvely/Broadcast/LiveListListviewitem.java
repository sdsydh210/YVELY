package com.example.sdsyd.yvely.Broadcast;

public class LiveListListviewitem {

    private String broadcastingImage;
    private String broadcasterId;
    private String broadcastingTitle;
    private String broadcastingStartTime;

    public void setBroadcastingImage(String image){ broadcastingImage = image; }
    public void setBroadcasterId(String id){ broadcasterId = id; }
    public void setBroadcastingTitle(String title){ broadcastingTitle = title; }
    public void setBroadcastingStartTime(String time){ broadcastingStartTime = time; }

    public String getBroadcastingImage(){ return this.broadcastingImage; }
    public String getBroadcasterId(){ return this.broadcasterId; }
    public String getBroadcastingTitle(){ return this.broadcastingTitle; }
    public String getBroadcastingStartTime(){ return this.broadcastingStartTime; }
}
