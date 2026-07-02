import React, { useState, useEffect } from 'react';
import { Card, Spin, Empty, Button, message, Pagination, Row, Col, Select, Input } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import { articleApi } from '../api/client';
import ArticleCard from './ArticleCard';

const { Search } = Input;
const { Option } = Select;

const ArticleList = () => {
    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [totalElements, setTotalElements] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [sources, setSources] = useState([]);
    const [filters, setFilters] = useState({
        page: 0,
        size: 10,
        category: null,
        type: null,
        source: null,
        impact: null,
        status: null,
        q: null,
    });

    // Fetch sources for filter dropdown
    useEffect(() => {
        const fetchSources = async () => {
            try {
                const response = await articleApi.getSources();
                setSources(response.data || []);
            } catch (err) {
                console.error('Failed to fetch sources:', err);
            }
        };
        fetchSources();
    }, []);

    // Fetch articles when filters change
    const fetchArticles = async () => {
        setLoading(true);
        try {
            const params = { ...filters };
            Object.keys(params).forEach(key => {
                if (params[key] === null || params[key] === undefined) {
                    delete params[key];
                }
            });
            const response = await articleApi.getArticles(params);
            setArticles(response.data.content || []);
            setTotalElements(response.data.totalElements || 0);
            setTotalPages(response.data.totalPages || 0);
        } catch (err) {
            console.error('Failed to fetch articles:', err);
            message.error('Failed to load articles');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchArticles();
    }, [filters]);

    const handleFilterChange = (key, value) => {
        setFilters(prev => ({ ...prev, [key]: value, page: 0 }));
    };

    const handlePageChange = (page) => {
        setFilters(prev => ({ ...prev, page: page - 1 }));
    };

    const handleSearch = (value) => {
        setFilters(prev => ({ ...prev, q: value, page: 0 }));
    };

    const handleRefresh = () => {
        fetchArticles();
        message.success('Refreshed!');
    };

    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}>
                <Spin size="large" tip="Loading articles..." />
            </div>
        );
    }

    return (
        <div>
            {/* Filters */}
            <Card size="small" style={{ marginTop: 16, marginBottom: 16 }}>
                <Row gutter={[16, 12]}>
                    <Col xs={24} sm={12} md={6}>
                        <Search
                            placeholder="Search articles..."
                            allowClear
                            onSearch={handleSearch}
                        />
                    </Col>
                    <Col xs={12} sm={6} md={3}>
                        <Select
                            placeholder="Category"
                            allowClear
                            style={{ width: '100%' }}
                            onChange={(value) => handleFilterChange('category', value)}
                        >
                            <Option value="REGULATORY">Regulatory</Option>
                            <Option value="FOREX">Forex</Option>
                            <Option value="MARKET">Market</Option>
                        </Select>
                    </Col>
                    <Col xs={12} sm={6} md={3}>
                        <Select
                            placeholder="Type"
                            allowClear
                            style={{ width: '100%' }}
                            onChange={(value) => handleFilterChange('type', value)}
                        >
                            <Option value="NEWS">News</Option>
                            <Option value="ANNOUNCEMENT">Announcement</Option>
                        </Select>
                    </Col>
                    <Col xs={12} sm={6} md={3}>
                        <Select
                            placeholder="Source"
                            allowClear
                            style={{ width: '100%' }}
                            onChange={(value) => handleFilterChange('source', value)}
                        >
                            {sources.map((source) => (
                                <Option key={source.name} value={source.name}>
                                    {source.name}
                                </Option>
                            ))}
                        </Select>
                    </Col>
                    <Col xs={12} sm={6} md={3}>
                        <Select
                            placeholder="Impact"
                            allowClear
                            style={{ width: '100%' }}
                            onChange={(value) => handleFilterChange('impact', value)}
                        >
                            <Option value="LOW">Low</Option>
                            <Option value="MEDIUM">Medium</Option>
                            <Option value="HIGH">High</Option>
                            <Option value="CRITICAL">Critical</Option>
                        </Select>
                    </Col>
                    <Col xs={12} sm={6} md={3}>
                        <Select
                            placeholder="Status"
                            allowClear
                            style={{ width: '100%' }}
                            onChange={(value) => handleFilterChange('status', value)}
                        >
                            <Option value="read">Read</Option>
                            <Option value="unread">Unread</Option>
                        </Select>
                    </Col>
                    <Col xs={12} sm={6} md={3}>
                        <Button
                            onClick={() => setFilters({ page: 0, size: 10 })}
                            style={{ width: '100%' }}
                        >
                            Clear Filters
                        </Button>
                    </Col>
                </Row>
            </Card>

            {/* Article List */}
            <div style={{ marginTop: 16 }}>
                {articles.length === 0 ? (
                    <Empty description="No articles found">
                        <Button type="primary" onClick={handleRefresh}>Refresh</Button>
                    </Empty>
                ) : (
                    articles.map((article) => (
                        <ArticleCard
                            key={article.id}
                            article={article}
                            onRefresh={fetchArticles}
                        />
                    ))
                )}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
                <div style={{ display: 'flex', justifyContent: 'center', marginTop: 24 }}>
                    <Pagination
                        current={filters.page + 1}
                        total={totalElements}
                        pageSize={filters.size}
                        onChange={handlePageChange}
                        showSizeChanger={false}
                    />
                </div>
            )}
        </div>
    );
};

export default ArticleList;