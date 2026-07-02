package com.aipr.intern.repository;

import com.aipr.intern.entity.ArticleSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
public interface ArticleSummaryRepo extends JpaRepository<ArticleSummary,Long> {
    //for POST /api/articles/{id}/summarize
    @Query("SELECT s FROM ArticleSummary s WHERE s.articleId = :articleId")
    Optional<ArticleSummary> findSummaryByArticleId(@Param("articleId") Long articleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ArticleSummary s WHERE s.articleId = :articleId")
    void deleteSummaryByArticleId(@Param("articleId") Long articleId);
}
