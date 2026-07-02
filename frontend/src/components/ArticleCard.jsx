import React, { useState } from 'react';
import { Card, Button, Space, Tag, Typography, message } from 'antd';
import { EyeOutlined, CheckCircleOutlined, CloseCircleOutlined, RobotOutlined } from '@ant-design/icons';
import { articleApi } from '../api/client';

const { Text, Paragraph } = Typography;

const ArticleCard = ({ article, onRefresh }) => {
    const [isRead, setIsRead] = useState(article.isRead);
    const [loading, setLoading] = useState(false);

    const formatDate = (date) => {
        return new Date(date).toLocaleDateString('en-US', {
            month: 'short',
            day: 'numeric',
            year: 'numeric',
        });
    };

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

    const handleReadToggle = async () => {
        setLoading(true);
        try {
            if (isRead) {
                await articleApi.markAsUnread(article.id, 1);
                message.success('Marked as unread');
            } else {
                await articleApi.markAsRead(article.id, 1);
                message.success('Marked as read');
            }
            setIsRead(!isRead);
            if (onRefresh) onRefresh();
        } catch (err) {
            message.error('Failed to update status');
        } finally {
            setLoading(false);
        }
    };

    const handleSummarize = async () => {
        setLoading(true);
        try {
            await articleApi.generateSummary(article.id, 1);
            message.success('AI summary generated!');
            if (onRefresh) onRefresh();
        } catch (err) {
            message.error('Failed to generate summary');
        } finally {
            setLoading(false);
        }
    };

    const handleView = () => {
        window.location.href = `/article/${article.id}`;
    };

    return (
        <Card
            style={{
                marginBottom: 16,
                borderLeft: `4px solid ${isRead ? '#52c41a' : '#1890ff'}`,
            }}
        >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div style={{ flex: 1 }}>
                    <Space size={8} style={{ marginBottom: 8, flexWrap: 'wrap' }}>
                        <Tag color="purple">{article.source}</Tag>
                        <Tag color={getCategoryColor(article.category)}>{article.category}</Tag>
                        <Tag>{article.contentType}</Tag>
                        {article.impactLevel && (
                            <Tag color={getImpactColor(article.impactLevel)}>{article.impactLevel}</Tag>
                        )}
                        <Tag icon={isRead ? <CheckCircleOutlined /> : <CloseCircleOutlined />}
                             color={isRead ? 'success' : 'warning'}>
                            {isRead ? 'Read' : 'Unread'}
                        </Tag>
                    </Space>

                    <h3 style={{ margin: '8px 0' }}>{article.title}</h3>

                    <Paragraph ellipsis={{ rows: 2 }}>
                        {article.executiveSummary || article.content?.substring(0, 150) + '...' || 'No content'}
                    </Paragraph>

                    <Space size={16}>
                        <Text type="secondary" style={{ fontSize: 12 }}>
                            {formatDate(article.publishedDate)}
                        </Text>
                        {article.executiveSummary && (
                            <Tag icon={<RobotOutlined />} color="purple">AI Summary</Tag>
                        )}
                    </Space>
                </div>

                <Space direction="vertical" align="end" style={{ minWidth: 120 }}>
                    <Button type="primary" icon={<EyeOutlined />} onClick={handleView} loading={loading}>
                        Details
                    </Button>
                    <Button
                        type={isRead ? 'default' : 'primary'}
                        onClick={handleReadToggle}
                        loading={loading}
                    >
                        {isRead ? 'Unread' : 'Read'}
                    </Button>
                    {!article.executiveSummary && (
                        <Button
                            type="dashed"
                            icon={<RobotOutlined />}
                            onClick={handleSummarize}
                            loading={loading}
                            size="small"
                        >
                            AI Summary
                        </Button>
                    )}
                </Space>
            </div>
        </Card>
    );
};

export default ArticleCard;