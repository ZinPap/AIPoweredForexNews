package com.aipr.intern.service;

import com.aipr.intern.config.SourceConfig;
import com.aipr.intern.entity.Article;
import com.aipr.intern.repository.ArticleRepo;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RSSCollectionService {

    @Autowired
    private ArticleRepo articleRepo;

    // List of RSS sources from requirements
    private static final List<SourceConfig> SOURCES = List.of(
            // Regulatory sources
            new SourceConfig("SEC", "https://www.sec.gov/news/pressreleases.rss", "REGULATORY", "ANNOUNCEMENT"),

            // Forex sources
            new SourceConfig("FXStreet", "https://www.fxstreet.com/rss/news", "FOREX", "NEWS"),
            new SourceConfig("ForexLive", "https://www.forexlive.com/feed", "FOREX", "NEWS"),
            new SourceConfig("Myfxbook", "https://www.myfxbook.com/rss/latest-forex-news", "FOREX", "NEWS"),

            // Market sources
            new SourceConfig("FXStreet Analysis", "https://www.fxstreet.com/rss/analysis", "MARKET", "NEWS"),
            new SourceConfig("Investing.com", "https://www.investing.com/rss/news.rss", "MARKET", "NEWS")
    );
    @Transactional
    public int collectAllSources() {
        System.out.println("Starting RSS collection...");
        int total = 0;

        for (SourceConfig source : SOURCES) {
            try {
                int count = collectFromSource(source);
                total += count;
                System.out.println(source.getName() + ": " + count + " new articles");
            } catch (Exception e) {
                System.err.println("->" + source.getName() + ": " + e.getMessage());
                return 0;
            }
        }

        System.out.println("Artcles Received from RSS service: " + total);
        return total;
    }

    @Transactional
    public int collectFromSource(SourceConfig source) throws Exception {
        URL feedUrl = new URL(source.getRssUrl());
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));

        List<Article> articles = new ArrayList<>();

        for (SyndEntry entry : feed.getEntries()) {
            try {
                String url = entry.getLink();

                // Skip if already exists
                if (articleRepo.existsByUrl(url)) {
                    continue;
                }

                String content = "";

                // Try to get full content first
                if (entry.getContents() != null && !entry.getContents().isEmpty()) {
                    content = entry.getContents().get(0).getValue();
                }

                // If no full content, try description
                if ((content == null || content.isEmpty()) && entry.getDescription() != null) {
                    content = entry.getDescription().getValue();
                }

                //skip if no content at all
                if (content == null || content.trim().isEmpty()) {
                    continue;
                }

                // Strip HTML
                content = stripHtml(content);

                // If after stripping HTML it's still empty, skip it
                if (content.trim().isEmpty()) {
                    continue;
                }

                Article article = new Article();
                article.setSource(source.getName());
                article.setCategory(source.getCategory());
                article.setContentType(source.getContentType());
                article.setTitle(entry.getTitle());
                article.setUrl(url);
                article.setContent(content);

                // Set published date
                Date pubDate = entry.getPublishedDate();
                if (pubDate != null) {
                    article.setPublishedDate(LocalDateTime.ofInstant(
                            pubDate.toInstant(), ZoneId.systemDefault()
                    ));
                } else {
                    article.setPublishedDate(LocalDateTime.now());
                }

                articles.add(article);

            } catch (Exception e) {
                System.err.println("Failed to parse entry: " + e.getMessage());
            }
        }

        if (!articles.isEmpty()) {
            articleRepo.saveAll(articles);
        }

        return articles.size();
    }
    // Helper method to strip HTML tags
    private String stripHtml(String html) {
        if (html == null) {
            return "";
        }
        // Remove HTML tags
        return html.replaceAll("<[^>]*>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}