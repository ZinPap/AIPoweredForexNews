package com.aipr.intern.repository;

import com.aipr.intern.entity.UserArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserArticleStatusRepo extends JpaRepository<UserArticleStatus, Long> {

    // POST /api/articles/{id}/read
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_article_status (user_id, article_id, is_read, read_at) " +
            "VALUES (:userId, :articleId, true, CURRENT_TIMESTAMP) " +
            "ON DUPLICATE KEY UPDATE is_read = true, read_at = CURRENT_TIMESTAMP",
            nativeQuery = true)
    void markAsRead(@Param("userId") Long userId, @Param("articleId") Long articleId);

    // DELETE /api/articles/{id}/read
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_article_status (user_id, article_id, is_read, read_at) " +
            "VALUES (:userId, :articleId, false, NULL) " +
            "ON DUPLICATE KEY UPDATE is_read = false, read_at = NULL",
            nativeQuery = true)
    void markAsUnread(@Param("userId") Long userId, @Param("articleId") Long articleId);

    // GET /api/me/unread-count
    @Query("SELECT COUNT(s) FROM UserArticleStatus s WHERE s.userId = :userId AND s.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);
    Optional<UserArticleStatus> findByUserIdAndArticleId(Long userId, Long articleId);
}