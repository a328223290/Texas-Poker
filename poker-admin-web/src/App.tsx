import { Routes, Route, Navigate } from 'react-router-dom';
import AdminLayout from './layouts/AdminLayout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import UserManage from './pages/UserManage';
import ChipManage from './pages/ChipManage';
import RoomManage from './pages/RoomManage';
import GameRecord from './pages/GameRecord';
import Announcement from './pages/Announcement';

function App() {
  const token = localStorage.getItem('admin_token');

  if (!token) {
    return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    );
  }

  return (
    <Routes>
      <Route path="/" element={<AdminLayout />}>
        <Route index element={<Navigate to="/dashboard" />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="users" element={<UserManage />} />
        <Route path="chips" element={<ChipManage />} />
        <Route path="rooms" element={<RoomManage />} />
        <Route path="games" element={<GameRecord />} />
        <Route path="announcements" element={<Announcement />} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" />} />
    </Routes>
  );
}

export default App;
