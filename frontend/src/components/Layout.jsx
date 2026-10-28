import { useEffect, useState } from 'react';
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { etcApi } from '../api';
import Avatar from './Avatar';
import RightPanel from './RightPanel';
import ToastHost from './Toast';

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [unread, setUnread] = useState(0);

  // 페이지 이동할 때마다 안 읽은 알림 수 갱신
  useEffect(() => {
    if (!user) return;
    etcApi.unreadCount().then(setUnread).catch(() => {});
  }, [user, location.pathname]);

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  return (
    <div className="shell">
      <aside className="nav">
        <Link to="/" className="logo">Local<span>Pick</span></Link>
        <p className="logo-sub">우리 동네 가게 소식</p>
        <nav>
          <NavLink to="/" end>🏠 홈</NavLink>
          <NavLink to="/search">🔍 검색</NavLink>
          {user && (
            <NavLink to="/notifications">
              🔔 알림 {unread > 0 && <span className="badge">{unread}</span>}
            </NavLink>
          )}
          {user?.accountType === 'BUSINESS' && <NavLink to="/insight">📊 인사이트</NavLink>}
          {user && <NavLink to={`/members/${user.id}`}>👤 프로필</NavLink>}
        </nav>
        {user ? (
          <>
            <button className="btn btn-primary wide" onClick={() => navigate('/write')}>+ 새 게시물</button>
            <div className="nav-me">
              <Avatar member={user} size={36} />
              <div>
                <strong>{user.nickname}</strong>
                <div className="muted small">{user.accountType === 'BUSINESS' ? '사업자 계정' : '개인 계정'}</div>
              </div>
              <button className="text-btn" onClick={handleLogout}>로그아웃</button>
            </div>
          </>
        ) : (
          <div className="nav-guest">
            <p className="muted small">로그인하고 동네 가게 소식을 받아보세요.</p>
            <button className="btn btn-primary wide" onClick={() => navigate('/login')}>로그인</button>
            <button className="btn wide" onClick={() => navigate('/signup')}>회원가입</button>
          </div>
        )}
      </aside>

      <main className="main-col">
        <Outlet />
      </main>

      <RightPanel />
      <ToastHost />
    </div>
  );
}
