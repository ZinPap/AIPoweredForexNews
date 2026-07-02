import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { articleApi } from '../api/client';
import { Card, Button, Space, Tag, Typography, Spin, Empty, message, Divider } from 'antd';
import { ArrowLeftOutlined, RobotOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

const { Title, Text, Paragraph } = Typography;

const ArticlePage = () => {
    const { id } = useParams();
    const [article, setArticle] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchArticle = async () => {
            try {
                const response = await articleApi.getArticle(id);
                setArticle(response.data);
            } catch (err) {
                setError('Failed to load article');
            } finally {
                setLoading(false);
            }
        };
        fetchArticle();
    }, [id]);

    const handleGenerateSummary = async () => {
        try {
            const response = await articleApi.generateSummary(id, 1);
            setArticle(response.data);
            message.success('AI summary generated!');
        } catch (err) {
            message.error('Failed to generate summary');
        }
    };

    const handleReadToggle = async () => {
        try {
            if (article.isRead) {
                await articleApi.markAsUnread(id, 1);
                setArticle({ ...article, isRead: false });
                message.success('Marked as unread');
            } else {
                await articleApi.markAsRead(id, 1);
                setArticle({ ...article, isRead: true });
                message.success('Marked as read');
            }
        } catch (err) {
            message.error('Failed to update status');
        }
    };

    if (loading) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}>
                <Spin size="large" tip="Loading article..." />
            </div>
        );
    }

    if (error || !article) {
        return <Empty description={error || 'Article not found'} />;
    }

    const getCategoryColor = (category) => {
        switch (category) {
            case 'REGULATORY': return 'gold';
            case 'FOREX': return 'blue';
            case 'MARKET': return 'green';
            default: return 'default';
        }
    };

    const getImpactColor = (impact) => {
        switch (impact) {
            case 'CRITICAL': return 'red';
            case 'HIGH': return 'orange';
            case 'MEDIUM': return 'gold';
            case 'LOW': return 'green';
            default: return 'default';
        }
    };

    return (
        <div>
            <Link to="/">
                <Button icon={<ArrowLeftOutlined />} style={{ marginBottom: 16 }}>
                    Back to Dashboard
                </Button>
            </Link>

            <Card>
                <Space size={8} style={{ marginBottom: 16, flexWrap: 'wrap' }}>
                    <Tag color="purple">{article.source}</Tag>
                    <Tag color={getCategoryColor(article.category)}>{article.category}</Tag>
                    <Tag>{article.contentType}</Tag>
                    {article.impactLevel && (
                        <Tag color={getImpactColor(article.impactLevel)}>{article.impactLevel}</Tag>
                    )}
                    <Tag icon={article.isRead ? <CheckCircleOutlined /> : <CloseCircleOutlined />}
                         color={article.isRead ? 'success' : 'warning'}>
                        {article.isRead ? 'Read' : 'Unread'}
                    </Tag>
                </Space>

                <Title level={2}>{article.title}</Title>

                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
                    <Text type="secondary">
                        Published: {new Date(article.publishedDate).toLocaleString()}
                    </Text>
                    <a href={article.url} target="_blank" rel="noopener noreferrer">
                        Read Original
                    </a>
                </div>

                <Divider>Full Content</Divider>

                <Paragraph style={{ whiteSpace: 'pre-wrap' }}>
                    {article.content || 'No content available'}
                </Paragraph>

                {article.executiveSummary && (
                    <>
                        <Divider>
                            <RobotOutlined /> AI Summary
                        </Divider>
                        <Card size="small" style={{ background: '#f8fafc' }}>
                            <Title level={4}>Executive Summary</Title>
                            <Paragraph>{article.executiveSummary}</Paragraph>

                            <Space direction="vertical" style={{ marginTop: 12 }}>
                                <div>
                                    <Text strong>Impact Level: </Text>
                                    <Tag color={getImpactColor(article.impactLevel)}>{article.impactLevel}</Tag>
                                </div>
                                <div>
                                    <Text strong>Affected Parties: </Text>
                                    <Text>{article.affectedParties || 'N/A'}</Text>
                                </div>
                                <div>
                                    <Text strong>Topics: </Text>
                                    <Text>{article.topics || 'N/A'}</Text>
                                </div>
                            </Space>
                        </Card>
                    </>
                )}

                <Divider />

                <Space>
                    <Button type="primary" onClick={handleReadToggle}>
                        {article.isRead ? 'Mark as Unread' : 'Mark as Read'}
                    </Button>
                    {!article.executiveSummary && (
                        <Button icon={<RobotOutlined />} onClick={handleGenerateSummary}>
                            Generate AI Summary
                        </Button>
                    )}
                </Space>
            </Card>
        </div>
    );
};

export default ArticlePage;