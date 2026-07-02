package com.aipr.intern.mapper;

import com.aipr.intern.dto.ArticleWithStatusDto;
import com.aipr.intern.dto.SourceDto;
import com.aipr.intern.dto.UnreadCountDto;
import com.aipr.intern.entity.Article;
import com.aipr.intern.entity.ArticleSummary;
import com.aipr.intern.entity.AppUser;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArticleMapper {

    // Takes article, its summary, and read status
    public ArticleWithStatusDto toDto(Article article, ArticleSummary summary, boolean isRead) {
        if (article == null) {
            return null;
        }

        ArticleWithStatusDto dto = new ArticleWithStatusDto();

        dto.setId(article.getId());
        dto.setSource(article.getSource());
        dto.setCategory(article.getCategory());
        dto.setContentType(article.getContentType());
        dto.setTitle(article.getTitle());
        dto.setUrl(article.getUrl());
        dto.setPublishedDate(article.getPublishedDate());
        dto.setContent(article.getContent());
        dto.setCreatedAt(article.getCreatedAt());

        if (summary != null) {
            dto.setExecutiveSummary(summary.getExecutiveSummary());
            dto.setImpactLevel(summary.getImpactLevel());
            dto.setAffectedParties(summary.getAffectedParties());
            dto.setTopics(summary.getTopics());
        }

        dto.setRead(isRead);
        dto.setReadAt(null);

        return dto;
    }

    // For the single article endpoint (14 columns)
    // Expected: [id, source, category, content_type, title, url, published_date, content, created_at,
    //           executive_summary, impact_level, affected_parties, topics, isRead]
    public ArticleWithStatusDto toDetailDto(Object[] row) {
        if (row == null || row.length < 14) {
            return null;
        }

        // Build Article from row data
        Article article = new Article();
        article.setId(((Number) row[0]).longValue());
        article.setSource((String) row[1]);
        article.setCategory((String) row[2]);
        article.setContentType((String) row[3]);
        article.setTitle((String) row[4]);
        article.setUrl((String) row[5]);
        article.setPublishedDate(row[6] != null ? (LocalDateTime) row[6] : null);
        article.setContent((String) row[7]);
        article.setCreatedAt(row[8] != null ? (LocalDateTime) row[8] : null);

        // Build Summary
        ArticleSummary summary = new ArticleSummary();
        summary.setExecutiveSummary((String) row[9]);
        summary.setImpactLevel((String) row[10]);
        summary.setAffectedParties((String) row[11]);
        summary.setTopics((String) row[12]);

        // Handle isRead (comes as Integer from CAST)
        boolean isRead = false;
        Object isReadObj = row[13];
        if (isReadObj != null) {
            if (isReadObj instanceof Number) {
                isRead = ((Number) isReadObj).intValue() == 1;
            } else if (isReadObj instanceof Boolean) {
                isRead = (Boolean) isReadObj;
            }
        }

        return toDto(article, summary, isRead);
    }

    // For the articles list query (14 columns)
    public ArticleWithStatusDto toDto(Object[] row) {
        return toDetailDto(row);
    }

    // Batch convert multiple rows to DTOs
    public List<ArticleWithStatusDto> toDtoList(List<Object[]> rows) {
        if (rows == null) {
            return List.of();
        }
        return rows.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Simple source name → SourceDto
    public SourceDto toSourceDto(String sourceName) {
        if (sourceName == null) {
            return null;
        }
        SourceDto dto = new SourceDto();
        dto.setName(sourceName);
        return dto;
    }

    // List of source names → list of SourceDto
    public List<SourceDto> toSourceDtoList(List<String> sourceNames) {
        if (sourceNames == null) {
            return List.of();
        }
        return sourceNames.stream()
                .map(this::toSourceDto)
                .collect(Collectors.toList());
    }
    public Article toArticle(Object[] row) {
        if (row == null || row.length < 9) {
            return null;
        }
        Article article = new Article();
        article.setId(((Number) row[0]).longValue());
        article.setSource((String) row[1]);
        article.setCategory((String) row[2]);
        article.setContentType((String) row[3]);
        article.setTitle((String) row[4]);
        article.setUrl((String) row[5]);
        article.setPublishedDate(row[6] != null ? (LocalDateTime) row[6] : null);
        article.setContent((String) row[7]);
        article.setCreatedAt(row[8] != null ? (LocalDateTime) row[8] : null);
        return article;
    }

    // Wrap a count in a DTO for the unread count endpoint
    public UnreadCountDto toUnreadCountDto(long count) {
        return new UnreadCountDto(count);
    }

    // Safely get user ID or null
    public Long getUserId(AppUser user) {
        return user != null ? user.getId() : null;
    }

    // Safely get username or null
    public String getUsername(AppUser user) {
        return user != null ? user.getUsername() : null;
    }
}