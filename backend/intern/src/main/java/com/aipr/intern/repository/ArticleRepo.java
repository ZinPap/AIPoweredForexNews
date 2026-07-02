package com.aipr.intern.repository;

import com.aipr.intern.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepo extends JpaRepository<Article, Long> {

    @Query(
            value = "SELECT a.id, a.source, a.category, a.content_type, a.title, a.url, a.published_date, a.content, a.created_at, " +
                    "s.executive_summary, s.impact_level, s.affected_parties, s.topics, " +
                    "CAST(u.is_read AS UNSIGNED) as isRead " +
                    "FROM article a " +
                    "LEFT JOIN article_summary s ON a.id = s.article_id " +
                    "LEFT JOIN user_article_status u ON a.id = u.article_id AND u.user_id = :userId " +
                    "WHERE (:category IS NULL OR a.category = :category) " +
                    "AND (:type IS NULL OR a.content_type = :type) " +
                    "AND (:source IS NULL OR a.source = :source) " +
                    "AND (:impact IS NULL OR s.impact_level = :impact) " +
                    "AND (:status IS NULL OR " +
                    "     (:status = 'read' AND u.is_read = 1) OR " +
                    "     (:status = 'unread' AND (u.is_read IS NULL OR u.is_read = 0))" +
                    "    ) " +
                    "AND (:q IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%'))) " +
                    "ORDER BY a.published_date DESC",
            countQuery = "SELECT COUNT(*) FROM article a " +
                    "LEFT JOIN article_summary s ON a.id = s.article_id " +
                    "WHERE (:category IS NULL OR a.category = :category) " +
                    "AND (:type IS NULL OR a.content_type = :type) " +
                    "AND (:source IS NULL OR a.source = :source) " +
                    "AND (:impact IS NULL OR s.impact_level = :impact) " +
                    "AND (:status IS NULL OR " +
                    "     (:status = 'read' AND EXISTS (SELECT 1 FROM user_article_status u WHERE u.article_id = a.id AND u.user_id = :userId AND u.is_read = 1)) OR " +
                    "     (:status = 'unread' AND NOT EXISTS (SELECT 1 FROM user_article_status u WHERE u.article_id = a.id AND u.user_id = :userId AND u.is_read = 1))" +
                    "    ) " +
                    "AND (:q IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%')))",
            nativeQuery = true
    )
    Page<Object[]> findArticles(
            @Param("category") String category,
            @Param("type") String type,
            @Param("source") String source,
            @Param("impact") String impact,
            @Param("q") String q,
            @Param("status") String status,
            @Param("userId") Long userId,
            Pageable pageable
    );

    // FOR GET /api/articles/{id}
    @Query(
            value = "SELECT a.id, a.source, a.category, a.content_type, a.title, a.url, a.published_date, a.content, a.created_at, " +
                    "s.executive_summary, s.impact_level, s.affected_parties, s.topics, " +
                    "CAST(u.is_read AS UNSIGNED) as isRead " +
                    "FROM article a " +
                    "LEFT JOIN article_summary s ON a.id = s.article_id " +
                    "LEFT JOIN user_article_status u ON a.id = u.article_id AND u.user_id = :userId " +
                    "WHERE a.id = :articleId",
            nativeQuery = true
    )
    List<Object[]> findArticleWithDetailsByIdList(
            @Param("articleId") Long articleId,
            @Param("userId") Long userId
    );

    @Query(
            value = "SELECT a.id, a.source, a.category, a.content_type, a.title, a.url, a.published_date, a.content, a.created_at, " +
                    "s.executive_summary, s.impact_level, s.affected_parties, s.topics, " +
                    "CAST(u.is_read AS UNSIGNED) as isRead " +
                    "FROM article a " +
                    "LEFT JOIN article_summary s ON a.id = s.article_id " +
                    "LEFT JOIN user_article_status u ON a.id = u.article_id AND u.user_id = :userId " +
                    "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
                    "ORDER BY a.published_date DESC",
            countQuery = "SELECT COUNT(*) FROM article a " +
                    "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%'))",
            nativeQuery = true
    )
    Page<Object[]> searchByTitle(
            @Param("q") String q,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query(value = "SELECT DISTINCT a.source FROM article a ORDER BY a.source", nativeQuery = true)
    List<String> findAllDistinctSources();

    boolean existsByUrl(String url);

}