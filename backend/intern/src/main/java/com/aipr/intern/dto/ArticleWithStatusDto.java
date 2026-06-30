package com.aipr.intern.dto;

import com.aipr.intern.entity.*;
import java.time.LocalDateTime;

public class ArticleWithStatusDto {
    // Article fields (9)
    private Long id;
    private String source;
    private String category;
    private String contentType;
    private String title;
    private String url;
    private LocalDateTime publishedDate;
    private String content;
    private LocalDateTime createdAt;

    // Summary fields (4)
    private String executiveSummary;
    private String impactLevel;
    private String affectedParties;
    private String topics;

    // Status fields (2)
    private boolean isRead;
    private LocalDateTime readAt;

    // Default constructor
    public ArticleWithStatusDto() {
    }

    public ArticleWithStatusDto(Article article, ArticleSummary summary, boolean isRead) {
        // Extract from Article entity
        this.id = article.getId();
        this.source = article.getSource();
        this.category = article.getCategory();
        this.contentType = article.getContentType();
        this.title = article.getTitle();
        this.url = article.getUrl();
        this.publishedDate = article.getPublishedDate();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();

        // Extract from ArticleSummary entity
        if (summary != null) {
            this.executiveSummary = summary.getExecutiveSummary();
            this.impactLevel = summary.getImpactLevel();
            this.affectedParties = summary.getAffectedParties();
            this.topics = summary.getTopics();
        }

        // Status fields
        this.isRead = isRead;
        this.readAt = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public void setExecutiveSummary(String executiveSummary) {
        this.executiveSummary = executiveSummary;
    }

    public String getImpactLevel() {
        return impactLevel;
    }

    public void setImpactLevel(String impactLevel) {
        this.impactLevel = impactLevel;
    }

    public String getAffectedParties() {
        return affectedParties;
    }

    public void setAffectedParties(String affectedParties) {
        this.affectedParties = affectedParties;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }


    @Override
    public String toString() {
        return "ArticleWithStatusDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}