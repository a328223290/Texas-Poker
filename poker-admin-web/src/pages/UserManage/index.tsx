import { useEffect, useState } from 'react';
import { Table, Button, Input, Tag, Space, Modal, message } from 'antd';
import { userManageApi } from '../../services/adminApi';

export default function UserManage() {
  const [users, setUsers] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [searchName, setSearchName] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadUsers();
  }, [page]);

  const loadUsers = async () => {
    setLoading(true);
    try {
      const res: any = await userManageApi.list({
        page,
        size: 10,
        username: searchName || undefined,
      });
      if (res.code === 200) {
        setUsers(res.data.records || []);
        setTotal(res.data.total || 0);
      }
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  };

  const handleBan = (id: number, currentStatus: string) => {
    const newStatus = currentStatus === 'BANNED' ? 'ACTIVE' : 'BANNED';
    Modal.confirm({
      title: newStatus === 'BANNED' ? '确认封禁该用户？' : '确认解封该用户？',
      onOk: async () => {
        const res: any = await userManageApi.updateStatus(id, newStatus);
        if (res.code === 200) {
          message.success('操作成功');
          loadUsers();
        }
      },
    });
  };

  const handleResetPwd = (id: number) => {
    Modal.confirm({
      title: '确认重置该用户密码为 123456？',
      onOk: async () => {
        const res: any = await userManageApi.resetPassword(id);
        if (res.code === 200) {
          message.success('密码已重置');
        }
      },
    });
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '用户名', dataIndex: 'username', key: 'username' },
    { title: '昵称', dataIndex: 'nickname', key: 'nickname' },
    { title: '筹码', dataIndex: 'chips', key: 'chips', render: (v: number) => v?.toLocaleString() },
    { title: '总局数', dataIndex: 'totalGames', key: 'totalGames' },
    { title: '胜场', dataIndex: 'totalWins', key: 'totalWins' },
    {
      title: '状态', dataIndex: 'status', key: 'status',
      render: (s: string) => (
        <Tag color={s === 'ACTIVE' ? 'green' : s === 'BANNED' ? 'red' : 'default'}>{s}</Tag>
      ),
    },
    {
      title: '操作', key: 'actions',
      render: (_: unknown, record: any) => (
        <Space>
          <Button size="small" danger={record.status !== 'BANNED'} onClick={() => handleBan(record.id, record.status)}>
            {record.status === 'BANNED' ? '解封' : '封禁'}
          </Button>
          <Button size="small" onClick={() => handleResetPwd(record.id)}>重置密码</Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2 style={{ marginBottom: 16 }}>用户管理</h2>
      <Space style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索用户名"
          value={searchName}
          onChange={(e) => setSearchName(e.target.value)}
          onSearch={loadUsers}
          style={{ width: 240 }}
        />
      </Space>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={users}
        loading={loading}
        pagination={{ current: page, total, pageSize: 10, onChange: setPage }}
      />
    </div>
  );
}
