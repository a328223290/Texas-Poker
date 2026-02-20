import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { lobbyApi } from '../../services/api';
import { useAuthStore } from '../../store/authStore';
import { connectGameSocket } from '../../socket/gameSocket';
import './Lobby.css';

interface RoomItem {
  roomId: number;
  name: string;
  blindSmall: number;
  blindBig: number;
  currentPlayers: number;
  maxPlayers: number;
  status: string;
}

export default function Lobby() {
  const navigate = useNavigate();
  const { token, nickname, chips, logout } = useAuthStore();
  const [rooms, setRooms] = useState<RoomItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      connectGameSocket(token);
    }
    loadRooms();
  }, [token]);

  const loadRooms = async () => {
    try {
      const res: any = await lobbyApi.getRooms();
      if (res.code === 200) {
        setRooms(res.data.rooms || []);
      }
    } catch {
      console.error('加载房间列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleJoinRoom = (roomId: number) => {
    navigate(`/game/${roomId}`);
  };

  const handleCreateRoom = async () => {
    try {
      const res: any = await lobbyApi.createRoom({
        name: `${nickname}的房间`,
        blindSmall: 10,
        blindBig: 20,
        maxPlayers: 6,
      });
      if (res.code === 200) {
        navigate(`/game/${res.data.roomId}`);
      }
    } catch {
      console.error('创建房间失败');
    }
  };

  return (
    <div className="lobby-container">
      <header className="lobby-header">
        <h1>Texas Poker</h1>
        <div className="lobby-user-info">
          <span className="lobby-nickname">{nickname}</span>
          <span className="lobby-chips">{chips?.toLocaleString()} 筹码</span>
          <button className="lobby-logout" onClick={logout}>退出</button>
        </div>
      </header>

      <div className="lobby-content">
        <div className="lobby-actions">
          <button className="btn-create" onClick={handleCreateRoom}>创建房间</button>
          <button className="btn-refresh" onClick={loadRooms}>刷新列表</button>
        </div>

        {loading ? (
          <p className="lobby-loading">加载中...</p>
        ) : rooms.length === 0 ? (
          <p className="lobby-empty">暂无房间，快来创建一个吧</p>
        ) : (
          <div className="room-list">
            {rooms.map((room) => (
              <div key={room.roomId} className="room-card">
                <div className="room-info">
                  <h3>{room.name}</h3>
                  <p>盲注: {room.blindSmall}/{room.blindBig}</p>
                  <p>玩家: {room.currentPlayers}/{room.maxPlayers}</p>
                </div>
                <button
                  className="btn-join"
                  onClick={() => handleJoinRoom(room.roomId)}
                  disabled={room.currentPlayers >= room.maxPlayers}
                >
                  {room.currentPlayers >= room.maxPlayers ? '已满' : '加入'}
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
