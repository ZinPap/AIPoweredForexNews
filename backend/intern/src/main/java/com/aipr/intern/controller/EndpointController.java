package com.aipr.intern.controller;

import com.aipr.intern.dto.ArticleWithStatusDto;
import com.aipr.intern.dto.SourceDto;
import com.aipr.intern.dto.UnreadCountDto;
import com.aipr.intern.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EndpointController {

    @Autowired
    private ArticleService articleService;

    // 1. GET /api/articles
    @GetMapping("/articles")
    public ResponseEntity<Page<ArticleWithStatusDto>> getArticles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String impact,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(articleService.getArticles(
                category, type, source, impact, q, status, userId, pageable
        ));
    }

    // 2. GET /api/articles/{id}
    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleWithStatusDto> getArticleDetails(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(articleService.getArticleDetails(id, userId));
    }

    // 3. GET /api/articles/search
    @GetMapping("/articles/search")
    public ResponseEntity<Page<ArticleWithStatusDto>> searchArticles(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(articleService.searchByTitle(q, userId, pageable));
    }

    // 4. POST /api/articles/{id}/read
    @PostMapping("/articles/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        articleService.markAsRead(userId, id);
        return ResponseEntity.ok(Map.of("message", "Article marked as read"));
    }

    // 5. DELETE /api/articles/{id}/read
    @DeleteMapping("/articles/{id}/read")
    public ResponseEntity<Map<String, String>> markAsUnread(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        articleService.markAsUnread(userId, id);
        return ResponseEntity.ok(Map.of("message", "Article marked as unread"));
    }

    // 6. GET /api/me/unread-count
    @GetMapping("/me/unread-count")
    public ResponseEntity<UnreadCountDto> getUnreadCount() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(articleService.getUnreadCount(userId));
    }

    // 7. GET /api/sources
    @GetMapping("/sources")
    public ResponseEntity<List<SourceDto>> getSources() {
        return ResponseEntity.ok(articleService.getDistinctSources());
    }

    // 8. POST /api/articles/{id}/summarize
    @PostMapping("/articles/{id}/summarize")
    public ResponseEntity<ArticleWithStatusDto> generateSummary(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(articleService.generateSummary(id, userId));
    }

    // 9. POST /api/collector/run
    @PostMapping("/collector/run")
    public ResponseEntity<Map<String, Object>> triggerCollection() {
        int count = articleService.collectArticles();
        return ResponseEntity.ok(Map.of(
                "message", "Collection triggered",
                "articlesCollected", count
        ));
    }

    private Long getCurrentUserId() {
        // For testing:not yet header id
        return 1L;
    }
}