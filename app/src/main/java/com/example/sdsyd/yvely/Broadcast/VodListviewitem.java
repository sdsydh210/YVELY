package com.example.sdsyd.yvely.Broadcast;

public class VodListviewitem {

    private String vodImage;
    private String vodId;
    private String vodTitle;
    private String vodUrl;

    public void setVodImage(String image){ vodImage = image; }
    public void setVodId(String id){ vodId = id; }
    public void setVodTitle(String title){ vodTitle = title; }
    public void setVodUrl(String vod){ vodUrl = vod; }

    public String getVodImage(){ return this.vodImage; }
    public String getVodId(){ return this.vodId; }
    public String getVodTitle(){ return this.vodTitle; }
    public String getVodUrl(){ return this.vodUrl; }

}
