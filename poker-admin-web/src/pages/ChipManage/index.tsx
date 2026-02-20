import { useState } from 'react';
import { Card, Form, InputNumber, Input, Button, message, Table } from 'antd';
import { chipManageApi } from '../../services/adminApi';

export default function ChipManage() {
  const [balance, setBalance] = useState<{ userId?: number; username?: string; chips?: number } | null>(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);

  const onQueryBalance = async (values: { userId: number }) => {
    try {
      const res: any = await chipManageApi.getBalance(values.userId);
      if (res.code === 200) {
        setBalance(res.data);
      } else {
        message.error(res.message);
      }
    } catch {
      message.error('查询失败');
    }
  };

  const onAdjust = async (values: { targetUserId: number; amount: number; reason: string }) => {
    setLoading(true);
    try {
      const res: any = await chipManageApi.adjust(values);
      if (res.code === 200) {
        message.success(`调整成功，调整后余额: ${res.data.balanceAfter}`);
        setBalance(null);
      } else {
        message.error(res.message);
      }
    } catch {
      message.error('调整失败');
    } finally {
      setLoading(false);
    }
  };

  const loadTransactions = async (userId?: number) => {
    try {
      const res: any = await chipManageApi.transactions({ userId, page: 1, size: 20 });
      if (res.code === 200) {
        setTransactions(res.data.records || []);
      }
    } catch {
      // ignore
    }
  };

  const txColumns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '目标用户', dataIndex: 'targetUserId', key: 'targetUserId' },
    { title: '操作管理员', dataIndex: 'adminUserId', key: 'adminUserId' },
    { title: '金额', dataIndex: 'amount', key: 'amount',
      render: (v: number) => <span style={{ color: v > 0 ? 'green' : 'red' }}>{v > 0 ? `+${v}` : v}</span>
    },
    { title: '调整前', dataIndex: 'balanceBefore', key: 'balanceBefore' },
    { title: '调整后', dataIndex: 'balanceAfter', key: 'balanceAfter' },
    { title: '原因', dataIndex: 'reason', key: 'reason' },
    { title: '时间', dataIndex: 'createdAt', key: 'createdAt' },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 16 }}>筹码管理</h2>

      <Card title="查询余额" style={{ marginBottom: 16 }}>
        <Form layout="inline" onFinish={onQueryBalance}>
          <Form.Item name="userId" label="用户ID" rules={[{ required: true }]}>
            <InputNumber style={{ width: 160 }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">查询</Button>
          </Form.Item>
        </Form>
        {balance && (
          <div style={{ marginTop: 12 }}>
            用户: {balance.username} | 当前筹码: <strong>{balance.chips?.toLocaleString()}</strong>
          </div>
        )}
      </Card>

      <Card title="调整筹码" style={{ marginBottom: 16 }}>
        <Form layout="inline" onFinish={onAdjust}>
          <Form.Item name="targetUserId" label="用户ID" rules={[{ required: true }]}>
            <InputNumber style={{ width: 120 }} />
          </Form.Item>
          <Form.Item name="amount" label="金额" rules={[{ required: true }]}>
            <InputNumber style={{ width: 140 }} placeholder="正数增加，负数扣减" />
          </Form.Item>
          <Form.Item name="reason" label="原因" rules={[{ required: true }]}>
            <Input style={{ width: 200 }} placeholder="调整原因" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>确认调整</Button>
          </Form.Item>
        </Form>
      </Card>

      <Card title="调整记录" extra={<Button onClick={() => loadTransactions()}>加载记录</Button>}>
        <Table rowKey="id" columns={txColumns} dataSource={transactions} size="small" />
      </Card>
    </div>
  );
}
