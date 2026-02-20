import { Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Lobby from './pages/Lobby';
import Game from './pages/Game';
import { useAuthStore } from './store/authStore';

function App() {
  const token = useAuthStore((s) => s.token);

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/lobby"
        element={token ? <Lobby /> : <Navigate to="/login" />}
      />
      <Route
        path="/game/:roomId"
        element={token ? <Game /> : <Navigate to="/login" />}
      />
      <Route path="*" element={<Navigate to={token ? '/lobby' : '/login'} />} />
    </Routes>
  );
}

export default App;
