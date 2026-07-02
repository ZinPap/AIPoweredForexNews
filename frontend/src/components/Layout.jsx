import React, { useState, useEffect } from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import {
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    HomeOutlined,
    BellOutlined,
} from '@ant-design/icons';
import { Button, Layout as AntLayout, Menu, theme, Badge, Avatar } from 'antd';
import { articleApi } from '../api/client';

const { Header, Sider, Content } = AntLayout;

const Layout = () => {
    const [collapsed, setCollapsed] = useState(false);
    const [unreadCount, setUnreadCount] = useState(0);
    const location = useLocation();
    const {
        token: { colorBgContainer },
    } = theme.useToken();

    useEffect(() => {
        const fetchUnreadCount = async () => {
            try {
                const response = await articleApi.getUnreadCount();
                setUnreadCount(response.data.unreadCount || 0);
            } catch (err) {
                console.error('Failed to fetch unread count:', err);
            }
        };
        fetchUnreadCount();
    }, []);

    const menuItems = [
        {
            key: '/',
            icon: <HomeOutlined />,
            label: <Link to="/">Dashboard</Link>,
        },
    ];

    return (
        <AntLayout style={{ minHeight: '100vh' }}>
            <Sider
                trigger={null}
                collapsible
                collapsed={collapsed}
                width={240}
                collapsedWidth={80}
                style={{
                    height: '100vh',
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    background: '#001529',
                    overflow: 'auto',
                }}
            >
                <div style={{
                    height: 64,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'white',
                    fontSize: collapsed ? 16 : 20,
                    fontWeight: 'bold',
                    borderBottom: '1px solid rgba(255,255,255,0.1)',
                    letterSpacing: '1px',
                }}>
                    {collapsed ? 'RN' : 'RegNews'}
                </div>
                <Menu
                    theme="dark"
                    mode="inline"
                    selectedKeys={[location.pathname]}
                    items={menuItems}
                    style={{ borderRight: 0 }}
                />
            </Sider>

            <AntLayout style={{ marginLeft: collapsed ? 80 : 240, transition: 'margin-left 0.2s' }}>
                <Header style={{
                    padding: '0 24px',
                    background: colorBgContainer,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    borderBottom: '1px solid #f0f0f0',
                    height: 64,
                }}>
                    <Button
                        type="text"
                        icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                        onClick={() => setCollapsed(!collapsed)}
                        style={{
                            fontSize: '16px',
                            width: 64,
                            height: 64,
                        }}
                    />
                    <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                        <Badge count={unreadCount} size="small">
                            <BellOutlined style={{ fontSize: '20px', cursor: 'pointer', color: '#555' }} />
                        </Badge>
                        <Avatar style={{ background: '#2563eb', cursor: 'pointer' }}>
                            U
                        </Avatar>
                    </div>
                </Header>

                <Content
                    style={{
                        margin: 0,
                        padding: 24,
                        minHeight: 'calc(100vh - 64px)',
                        background: colorBgContainer,
                    }}
                >
                    <Outlet />
                </Content>
            </AntLayout>
        </AntLayout>
    );
};

export default Layout;
