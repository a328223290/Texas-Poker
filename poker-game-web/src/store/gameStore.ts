import { create } from 'zustand';

export interface CardInfo {
  suit: string;
  rank: string;
}

export interface PlayerInfo {
  userId: number;
  nickname: string;
  chips: number;
  seatIndex: number;
  status: string;
  currentBet: number;
}

interface GameState {
  roomId: string | null;
  phase: string;
  pot: number;
  communityCards: CardInfo[];
  holeCards: CardInfo[];
  players: PlayerInfo[];
  currentPlayerId: number | null;
  myTurn: boolean;
  currentBet: number;
  minRaise: number;

  setRoom: (roomId: string) => void;
  updateGameState: (state: Partial<GameState>) => void;
  setHoleCards: (cards: CardInfo[]) => void;
  setCommunityCards: (cards: CardInfo[]) => void;
  setMyTurn: (turn: boolean, currentBet: number, minRaise: number) => void;
  reset: () => void;
}

export const useGameStore = create<GameState>((set) => ({
  roomId: null,
  phase: 'WAITING',
  pot: 0,
  communityCards: [],
  holeCards: [],
  players: [],
  currentPlayerId: null,
  myTurn: false,
  currentBet: 0,
  minRaise: 0,

  setRoom: (roomId) => set({ roomId }),

  updateGameState: (state) => set((prev) => ({ ...prev, ...state })),

  setHoleCards: (cards) => set({ holeCards: cards }),

  setCommunityCards: (cards) => set({ communityCards: cards }),

  setMyTurn: (turn, currentBet, minRaise) =>
    set({ myTurn: turn, currentBet, minRaise }),

  reset: () =>
    set({
      roomId: null,
      phase: 'WAITING',
      pot: 0,
      communityCards: [],
      holeCards: [],
      players: [],
      currentPlayerId: null,
      myTurn: false,
      currentBet: 0,
      minRaise: 0,
    }),
}));
