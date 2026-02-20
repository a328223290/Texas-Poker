import { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useGameStore } from '../../store/gameStore';
import { joinRoom, leaveRoom, sendPlayerAction } from '../../socket/gameSocket';
import './Game.css';

export default function Game() {
  const { roomId } = useParams<{ roomId: string }>();
  const navigate = useNavigate();
  const {
    phase,
    pot,
    communityCards,
    holeCards,
    players,
    myTurn,
    currentBet,
    minRaise,
    reset,
  } = useGameStore();

  useEffect(() => {
    if (roomId) {
      joinRoom(roomId);
    }
    return () => {
      if (roomId) {
        leaveRoom(roomId);
      }
      reset();
    };
  }, [roomId, reset]);

  const handleAction = (action: 'FOLD' | 'CHECK' | 'CALL' | 'RAISE' | 'ALL_IN', amount?: number) => {
    if (roomId) {
      sendPlayerAction(roomId, action, amount);
    }
  };

  const handleLeave = () => {
    navigate('/lobby');
  };

  return (
    <div className="game-container">
      {/* 顶部信息栏 */}
      <div className="game-header">
        <button className="btn-leave" onClick={handleLeave}>离开房间</button>
        <div className="game-info">
          <span>阶段: {phase}</span>
          <span className="pot-info">底池: {pot}</span>
        </div>
      </div>

      {/* 游戏桌面 */}
      <div className="poker-table">
        {/* 公共牌 */}
        <div className="community-cards">
          {communityCards.map((card, i) => (
            <div key={i} className="card">
              <span className={`suit-${card.suit}`}>{card.suit}</span>
              <span>{card.rank}</span>
            </div>
          ))}
          {/* 占位牌 */}
          {Array.from({ length: 5 - communityCards.length }).map((_, i) => (
            <div key={`empty-${i}`} className="card card-empty" />
          ))}
        </div>

        {/* 玩家座位 */}
        <div className="player-seats">
          {players.map((player) => (
            <div
              key={player.userId}
              className={`player-seat seat-${player.seatIndex}`}
            >
              <div className="player-name">{player.nickname}</div>
              <div className="player-chips">{player.chips}</div>
              {player.currentBet > 0 && (
                <div className="player-bet">{player.currentBet}</div>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* 手牌 */}
      <div className="hole-cards">
        {holeCards.map((card, i) => (
          <div key={i} className="card hole-card">
            <span className={`suit-${card.suit}`}>{card.suit}</span>
            <span>{card.rank}</span>
          </div>
        ))}
      </div>

      {/* 操作按钮 */}
      {myTurn && (
        <div className="action-panel">
          <button className="btn-fold" onClick={() => handleAction('FOLD')}>
            弃牌
          </button>
          {currentBet === 0 ? (
            <button className="btn-check" onClick={() => handleAction('CHECK')}>
              过牌
            </button>
          ) : (
            <button className="btn-call" onClick={() => handleAction('CALL')}>
              跟注 {currentBet}
            </button>
          )}
          <button
            className="btn-raise"
            onClick={() => handleAction('RAISE', minRaise)}
          >
            加注 {minRaise}
          </button>
          <button className="btn-allin" onClick={() => handleAction('ALL_IN')}>
            全押
          </button>
        </div>
      )}
    </div>
  );
}
