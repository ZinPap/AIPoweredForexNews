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
    @Query("UPDATE UserArticleStatus s SET s.isRead = true, s.readAt = CURRENT_TIMESTAMP " +
            "WHERE s.userId = :userId AND s.articleId = :articleId")
    void markAsRead(@Param("userId") Long userId, @Param("articleId") Long articleId);

    // DELETE /api/articles/{id}/read
    @Modifying
    @Transactional
    @Query("UPDATE UserArticleStatus s SET s.isRead = false, s.readAt = NULL " +
            "WHERE s.userId = :userId AND s.articleId = :articleId")
    void markAsUnread(@Param("userId") Long userId, @Param("articleId") Long articleId);

    // GET /api/me/unread-count
    @Query("SELECT COUNT(s) FROM UserArticleStatus s WHERE s.userId = :userId AND s.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);
    Optional<UserArticleStatus> findByUserIdAndArticleId(Long userId, Long articleId);
}