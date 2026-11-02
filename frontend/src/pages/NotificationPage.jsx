import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { etcApi } from '../api';
import Avatar from '../components/Avatar';
import { timeAgo } from '../utils';

const MESSAGE = {
  LIKE: '님이 회원님의 게시물을 좋아합니다.',
  COMMENT: '님이 회원님의 게시물에 댓글을 남겼습니다.',
  FOLLOW: '님이 회원님을 팔로우하기 시작했습니다.',
};

export default function NotificationPage() {
  const [list, setList] = useState(null);

  useEffect(() => {
    // 목록을 먼저 받아서 '안 읽음' 표시를 보여준 뒤, 전체 읽음 처리
    etcApi.notifications().then((data) => {
      setList(data);
      etcApi.readAll();
    });
  }, []);

  if (!list) return <p className="empty">불러오는 중…</p>;

  return (
    <section className="card">
      <h2>알림</h2>
      {list.length === 0 && <p className="muted">새 알림이 없어요.</p>}
      <ul className="noti-list">
        {list.map((n) => (
          <li key={n.id} className={n.read ? '' : 'unread'}>
            <Link to={`/members/${n.sender.id}`}><Avatar member={n.sender} size={40} /></Link>
            <Link to={n.postId ? `/posts/${n.postId}` : `/members/${n.sender.id}`} className="noti-text">
              <b>{n.sender.nickname}</b>{MESSAGE[n.type]}
              <span className="muted small"> {timeAgo(n.createdAt)}</span>
            </Link>
          </li>
        ))}
      </ul>
    </section>
  );
}
