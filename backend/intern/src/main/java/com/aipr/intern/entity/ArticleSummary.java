package com.aipr.intern.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "article_summary",
        uniqueConstraints = @UniqueConstraint(name = "uq_summary_article", columnNames = "article_id"))
public class ArticleSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_id", nullable = false)
    private Long articleId;


    @Column(name = "executive_summary", columnDefinition = "TEXT")
    private String executiveSummary;

    @Column(name = "impact_level", length = 10)
    private String impactLevel;  // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "affected_parties", columnDefinition = "TEXT")
    private String affectedParties;

    @Column(name = "topics", length = 500)
    private String topics;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Default constructor
    public ArticleSummary() {
    }

    // Constructor with all fields
    public ArticleSummary(Long id, Long articleId, String executiveSummary,
                          String impactLevel, String affectedParties,
                          String topics, LocalDateTime createdAt) {
        this.id = id;
        this.articleId = articleId;
        this.executiveSummary = executiveSummary;
        this.impactLevel = impactLevel;
        this.affectedParties = affectedParties;
        this.topics = topics;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ArticleSummary{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", impactLevel='" + impactLevel + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}