package com.example.sdsyd.yvely.KakaoBlogSearch;

public class FoodBlogListviewitem {

    private String blogImage;
    private String blogUrl;
    private String blogDateTime;
    private String blogTitle;
    private String blogContent;

    public void setBlogImage(String image){ blogImage = image; }
    public void setBlogUrl(String url){ blogUrl = url; }
    public void setBlogDateTime(String dateTime){ blogDateTime = dateTime; }
    public void setBlogTitle(String title){ blogTitle = title; }
    public void setBlogContent(String content){ blogContent = content; }

    public String getBlogImage(){ return this.blogImage; }
    public String getBlogUrl(){ return this.blogUrl; }
    public String getBlogDateTime(){ return this.blogDateTime; }
    public String getBlogTitle(){ return this.blogTitle; }
    public String getBlogContent(){ return this.blogContent; }

}
