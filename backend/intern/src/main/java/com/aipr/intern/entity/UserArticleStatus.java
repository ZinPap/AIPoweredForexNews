package com.aipr.intern.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_article_status",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_article", columnNames = {"user_id", "article_id"}))
public class UserArticleStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public UserArticleStatus() {
    }

    public UserArticleStatus(Long id, Long userId, Long articleId,
                             boolean isRead, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.articleId = articleId;
        this.isRead = isRead;
        this.readAt = readAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
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
        return "UserArticleStatus{" +
                "id=" + id +
                ", userId=" + userId +
                ", articleId=" + articleId +
                ", isRead=" + isRead +
                ", readAt=" + readAt +
                '}';
    }
}