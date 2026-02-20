import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../../services/api';
import { useAuthStore } from '../../store/authStore';
import './Login.css';

export default function Login() {
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);

  const [isRegister, setIsRegister] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isRegister) {
        const res: any = await authApi.register({ username, password, nickname });
        if (res.code === 200) {
          login(res.data.token, res.data.userId, nickname || username, 10000);
          navigate('/lobby');
        } else {
          setError(res.message);
        }
      } else {
        const res: any = await authApi.login({ username, password });
        if (res.code === 200) {
          const { token, userId, userInfo } = res.data;
          login(token, userId, userInfo.nickname, userInfo.chips);
          navigate('/lobby');
        } else {
          setError(res.message);
        }
      }
    } catch {
      setError('网络错误，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1 className="login-title">Texas Poker</h1>
        <p className="login-subtitle">德州扑克</p>

        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="用户名"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="密码"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {isRegister && (
            <input
              type="text"
              placeholder="昵称（可选）"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
            />
          )}

          {error && <p className="login-error">{error}</p>}

          <button type="submit" disabled={loading}>
            {loading ? '请稍候...' : isRegister ? '注册' : '登录'}
          </button>
        </form>

        <p className="login-switch" onClick={() => setIsRegister(!isRegister)}>
          {isRegister ? '已有账号？点击登录' : '没有账号？点击注册'}
        </p>
      </div>
    </div>
  );
}
