package com.aipr.intern.service;

import com.aipr.intern.dto.ArticleWithStatusDto;
import com.aipr.intern.dto.SourceDto;
import com.aipr.intern.dto.UnreadCountDto;
import com.aipr.intern.entity.Article;
import com.aipr.intern.entity.ArticleSummary;
import com.aipr.intern.entity.UserArticleStatus;
import com.aipr.intern.mapper.ArticleMapper;
import com.aipr.intern.repository.ArticleRepo;
import com.aipr.intern.repository.ArticleSummaryRepo;
import com.aipr.intern.repository.UserArticleStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepo articleRepo;
    @Autowired
    private ArticleSummaryRepo summaryRepo;
    @Autowired
    private UserArticleStatusRepo statusRepo;
    @Autowired
    private ArticleMapper mapper;
    @Autowired
    private  AISummaryService aiSummaryService;
    @Autowired
    private RSSCollectionService rssCollectionService;
    @Autowired
    private FinnhubNewsService finnhubNewsService;
    @Autowired
    private MarketauxNewsService marketauxNewsService;

    //Get Requests
    //1.GET /api/articles
    @Transactional(readOnly = true)
    public Page<ArticleWithStatusDto> getArticles(
            String category, String type, String source, String impact,
            String q, String status, Long userId, Pageable pageable) {

        Page<Object[]> results = articleRepo.findArticles(
                category, type, source, impact, q, status, userId, pageable
        );

        List<ArticleWithStatusDto> dtos = results.getContent().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }
    // 2. GET /api/articles/{id}
    @Transactional(readOnly = true)
    public ArticleWithStatusDto getArticleDetails(Long articleId, Long userId) {
        List<Object[]> results = articleRepo.findArticleWithDetailsByIdList(articleId, userId);

        if (results.isEmpty()) {
            throw new RuntimeException("Article not found with id: " + articleId);
        }

        return mapper.toDetailDto(results.get(0));
    }

    //3.GET /api/articles/search
    @Transactional(readOnly = true)
    public Page<ArticleWithStatusDto> searchByTitle(String q, Long userId, Pageable pageable) {
        Page<Object[]> results = articleRepo.searchByTitle(q, userId, pageable);

        List<ArticleWithStatusDto> dtos = results.getContent().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, results.getTotalElements());
    }
    //4. GET /api/me/unread-count
    @Transactional(readOnly = true)
    public UnreadCountDto getUnreadCount(Long userId) {
        long count = statusRepo.countUnreadByUserId(userId);
        return mapper.toUnreadCountDto(count);
    }
    //5. GET /api/sources
    @Transactional(readOnly = true)
    public List<SourceDto> getDistinctSources() {
        List<String> sources = articleRepo.findAllDistinctSources();
        return mapper.toSourceDtoList(sources);
    }

    //POST methods
    //1.POST /api/articles/{id}/read
    @Transactional
    public void markAsRead(Long userId, Long articleId) {
        statusRepo.markAsRead(userId, articleId);
    }
    //2.POST /api/articles/{id}/summarize
    //That is yet to be fully relised(placeholder for now)
    @Transactional
    public ArticleWithStatusDto generateSummary(Long articleId, Long userId) {
        //Get the article we need
        List<Object[]> results = articleRepo.findArticleWithDetailsByIdList(articleId, userId);
        if (results.isEmpty()) {
            throw new RuntimeException("Article not found with id: " + articleId);
        }
        Object[] row = results.get(0);
        Article article = mapper.toArticle(row);

        //Create ai summary:
        ArticleWithStatusDto aiSummary = aiSummaryService.generateSummary(
                article.getContent(),
                article.getCategory(),
                article.getContentType()
        );

        //delete existing summary
        summaryRepo.findSummaryByArticleId(articleId)
                .ifPresent(s -> summaryRepo.deleteSummaryByArticleId(articleId));

        //save new summary
        ArticleSummary newSummary = new ArticleSummary();
        newSummary.setArticleId(articleId);
        newSummary.setExecutiveSummary(aiSummary.getExecutiveSummary());
        newSummary.setImpactLevel(aiSummary.getImpactLevel());
        newSummary.setAffectedParties(aiSummary.getAffectedParties());
        newSummary.setTopics(aiSummary.getTopics());

        summaryRepo.save(newSummary);

        //Return new summary
        return getArticleDetails(articleId, userId);
    }
    //3.POST /api/collector/run
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public int collectArticles() {
        // RSS Collection
        int rssCount=rssCollectionService.collectAllSources();

        // Finnhub API Collection
        int finnhubCount = finnhubNewsService.fetchAllCategories();
        System.out.println("Finnhub collected: " + finnhubCount + " articles");

        //MarketauxCount
        int marketauxCount= marketauxNewsService.fetchNews();
        System.out.println("Marketaux collected: " + marketauxCount + " articles");
        int asum=rssCount+finnhubCount+marketauxCount;
        System.out.println("Total Articles collected from all sources: " + asum);
        return finnhubCount;
    }
    //Delete functions
    //1.  DELETE /api/articles/{id}/read
    @Transactional
    public void markAsUnread(Long userId, Long articleId) {
        statusRepo.markAsUnread(userId, articleId);
    }
}
