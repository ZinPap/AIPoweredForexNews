package com.aipr.intern.config;

public class SourceConfig {
    private String name;
    private String rssUrl;
    private String category;
    private String contentType;

    public SourceConfig(String name, String rssUrl, String category, String contentType) {
        this.name = name;
        this.rssUrl = rssUrl;
        this.category = category;
        this.contentType = contentType;
    }

    public String getName() { return name; }
    public String getRssUrl() { return rssUrl; }
    public String getCategory() { return category; }
    public String getContentType() { return contentType; }
}