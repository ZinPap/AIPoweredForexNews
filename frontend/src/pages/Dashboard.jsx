import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Spin, Button, message } from 'antd';
import { FileTextOutlined, EyeOutlined, RobotOutlined, ReloadOutlined } from '@ant-design/icons';
import { articleApi } from '../api/client';
import ArticleList from '../components/ArticleList';

const Dashboard = () => {
    const [totalArticles, setTotalArticles] = useState(0);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(true);

    const fetchStats = async () => {
        try {
            const articlesRes = await articleApi.getArticles({ page: 0, size: 1 });
            setTotalArticles(articlesRes.data.totalElements || 0);

            const unreadRes = await articleApi.getUnreadCount(1);
            setUnreadCount(unreadRes.data.unreadCount || 0);
        } catch (err) {
            console.error('Failed to fetch stats:', err);
            message.error('Failed to load data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStats();
    }, []);

    const handleRefresh = () => {
        setLoading(true);
        fetchStats();
        message.success('Refreshed!');
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                <h1 style={{ marginBottom: 0 }}>📰 Dashboard</h1>
                <Button icon={<ReloadOutlined />} onClick={handleRefresh}>
                    Refresh
                </Button>
            </div>
            <p style={{ color: '#64748b', marginBottom: 24 }}>
                Welcome back! Here's what's happening with your news feed.
            </p>

            {loading ? (
                <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}>
                    <Spin size="large" />
                </div>
            ) : (
                <>
                    <Row gutter={16}>
                        <Col span={12}>
                            <Card>
                                <Statistic
                                    title="Total Articles"
                                    value={totalArticles}
                                    prefix={<FileTextOutlined />}
                                    valueStyle={{ color: '#2563eb' }}
                                />
                            </Card>
                        </Col>
                        <Col span={12}>
                            <Card>
                                <Statistic
                                    title="Unread"
                                    value={unreadCount}
                                    prefix={<EyeOutlined />}
                                    valueStyle={{ color: '#f97316' }}
                                />
                            </Card>
                        </Col>
                    </Row>

                    <ArticleList />
                </>
            )}
        </div>
    );
};

export default Dashboard;