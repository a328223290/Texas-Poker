import { io, Socket } from 'socket.io-client';
import { useGameStore } from '../store/gameStore';

let socket: Socket | null = null;

export function connectGameSocket(token: string) {
  if (socket?.connected) return;

  socket = io('/ws/game', {
    auth: { token },
    transports: ['websocket'],
    reconnection: true,
    reconnectionAttempts: 10,
    reconnectionDelay: 2000,
  });

  socket.on('connect', () => {
    console.log('WebSocket 已连接');
  });

  socket.on('disconnect', (reason) => {
    console.log('WebSocket 断开:', reason);
  });

  // ---- 监听游戏事件 ----

  socket.on('PLAYER_JOINED', (data) => {
    const store = useGameStore.getState();
    store.updateGameState({
      players: [...store.players, data.player],
    });
  });

  socket.on('PLAYER_LEFT', (data) => {
    const store = useGameStore.getState();
    store.updateGameState({
      players: store.players.filter((p) => p.userId !== data.userId),
    });
  });

  socket.on('GAME_START', (data) => {
    useGameStore.getState().updateGameState({
      phase: 'PRE_FLOP',
      pot: 0,
      communityCards: [],
      ...data,
    });
  });

  socket.on('DEAL_CARDS', (data) => {
    useGameStore.getState().setHoleCards(data.holeCards);
  });

  socket.on('COMMUNITY_CARDS', (data) => {
    useGameStore.getState().updateGameState({
      phase: data.round,
      communityCards: data.cards,
    });
  });

  socket.on('YOUR_TURN', (data) => {
    useGameStore.getState().setMyTurn(true, data.currentBet, data.minRaise);
  });

  socket.on('PLAYER_ACTED', (data) => {
    useGameStore.getState().updateGameState({
      pot: data.currentPot,
    });
  });

  socket.on('GAME_END', (data) => {
    useGameStore.getState().updateGameState({
      phase: 'SETTLEMENT',
      ...data,
    });
  });

  socket.on('GAME_STATE', (data) => {
    useGameStore.getState().updateGameState(data);
  });

  socket.on('ERROR', (data) => {
    console.error('游戏错误:', data.message);
  });
}

// ---- 发送游戏事件 ----

export function joinRoom(roomId: string, seatIndex?: number) {
  socket?.emit('JOIN_ROOM', { roomId, seatIndex: seatIndex ?? -1 });
}

export function leaveRoom(roomId: string) {
  socket?.emit('LEAVE_ROOM', { roomId });
}

export function sendPlayerAction(
  roomId: string,
  action: 'FOLD' | 'CHECK' | 'CALL' | 'RAISE' | 'ALL_IN',
  amount?: number,
) {
  socket?.emit('PLAYER_ACTION', { roomId, action, amount: amount ?? 0 });
  useGameStore.getState().setMyTurn(false, 0, 0);
}

export function sendChatMessage(roomId: string, message: string) {
  socket?.emit('CHAT_MESSAGE', { roomId, message });
}

export function disconnectGameSocket() {
  socket?.disconnect();
  socket = null;
}
