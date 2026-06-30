package com.aipr.intern.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String source;

    @Column(nullable = false, length = 30)
    private String category;  // REGULATORY, FOREX, MARKET

    @Column(name = "content_type", nullable = false, length = 20)
    private String contentType;  // NEWS, ANNOUNCEMENT

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Default constructor
    public Article() {
    }

    // Constructor with all fields
    public Article(Long id, String source, String category, String contentType,
                   String title, String url, LocalDateTime publishedDate,
                   String content, LocalDateTime createdAt) {
        this.id = id;
        this.source = source;
        this.category = category;
        this.contentType = contentType;
        this.title = title;
        this.url = url;
        this.publishedDate = publishedDate;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
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
        // Optional: Validate the value
        if (category != null && !isValidCategory(category)) {
            throw new IllegalArgumentException("Invalid category: " + category +
                    ". Must be REGULATORY, FOREX, or MARKET");
        }
        this.category = category;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        // Optional: Validate the value
        if (contentType != null && !isValidContentType(contentType)) {
            throw new IllegalArgumentException("Invalid content type: " + contentType +
                    ". Must be NEWS or ANNOUNCEMENT");
        }
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

    // Validation helper methods
    private boolean isValidCategory(String category) {
        return category != null && (
                category.equals("REGULATORY") ||
                        category.equals("FOREX") ||
                        category.equals("MARKET")
        );
    }

    private boolean isValidContentType(String contentType) {
        return contentType != null && (
                contentType.equals("NEWS") ||
                        contentType.equals("ANNOUNCEMENT")
        );
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", category='" + category + '\'' +
                ", contentType='" + contentType + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", publishedDate=" + publishedDate +
                ", createdAt=" + createdAt +
                '}';
    }
}