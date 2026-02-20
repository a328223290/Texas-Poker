import { useEffect, useState } from 'react';
import { Card, Col, Row, Statistic } from 'antd';
import { UserOutlined, HomeOutlined, DollarOutlined } from '@ant-design/icons';
import { dashboardApi } from '../../services/adminApi';

export default function Dashboard() {
  const [overview, setOverview] = useState({ onlineUsers: 0, activeRooms: 0, totalChips: 0 });

  useEffect(() => {
    loadOverview();
  }, []);

  const loadOverview = async () => {
    try {
      const res: any = await dashboardApi.getOverview();
      if (res.code === 200) {
        setOverview(res.data);
      }
    } catch {
      // ignore
    }
  };

  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>仪表盘</h2>
      <Row gutter={24}>
        <Col span={8}>
          <Card>
            <Statistic
              title="在线人数"
              value={overview.onlineUsers}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="活跃房间"
              value={overview.activeRooms}
              prefix={<HomeOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="筹码流通量"
              value={overview.totalChips}
              prefix={<DollarOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
