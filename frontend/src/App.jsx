import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import FeedPage from './pages/FeedPage';
import PostDetailPage from './pages/PostDetailPage';
import PostWritePage from './pages/PostWritePage';
import ProfilePage from './pages/ProfilePage';
import SearchPage from './pages/SearchPage';
import NotificationPage from './pages/NotificationPage';
import InsightPage from './pages/InsightPage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import { useAuth } from './hooks/useAuth';

function RequireLogin({ children }) {
  const { user, loading } = useAuth();
  if (loading) return null;
  return user ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/signup" element={<SignupPage />} />
      <Route element={<Layout />}>
        <Route path="/" element={<FeedPage />} />
        <Route path="/posts/:postId" element={<PostDetailPage />} />
        <Route path="/write" element={<RequireLogin><PostWritePage /></RequireLogin>} />
        <Route path="/posts/:postId/edit" element={<RequireLogin><PostWritePage /></RequireLogin>} />
        <Route path="/members/:memberId" element={<ProfilePage />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/notifications" element={<RequireLogin><NotificationPage /></RequireLogin>} />
        <Route path="/insight" element={<RequireLogin><InsightPage /></RequireLogin>} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
