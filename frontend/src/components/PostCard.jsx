import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Avatar from './Avatar';
import { postApi } from '../api';
import { useAuth } from '../hooks/useAuth';
import { dDay, renderContent, timeAgo, TYPE_LABEL } from '../utils';
import { toast } from './Toast';

/**
 * 피드 카드 한 장.
 * 좋아요는 "낙관적 업데이트": 서버 응답을 기다리지 않고 화면부터 바꾸고,
 * 실패하면 원래대로 되돌린다. (MVC 버전은 좋아요마다 페이지 전체 새로고침)
 */
export default function PostCard({ post, onChange, onDelete, detail = false, preview = false }) {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [busy, setBusy] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLike = async () => {
    if (!user) return navigate('/login');
    if (busy) return;
    setBusy(true);
    const before = { liked: post.liked, likeCount: post.likeCount };
    // 1) 화면 먼저 반영
    onChange?.(post.id, () => ({
      liked: !before.liked,
      likeCount: before.likeCount + (before.liked ? -1 : 1),
    }));
    try {
      // 2) 서버 반영 후 서버 값으로 맞춘다
      const res = await postApi.toggleLike(post.id);
      onChange?.(post.id, () => ({ liked: res.liked, likeCount: res.likeCount }));
    } catch (e) {
      // 3) 실패하면 되돌리기
      onChange?.(post.id, () => before);
      toast(e.message);
    } finally {
      setBusy(false);
    }
  };

  const handleShare = async () => {
    const url = `${window.location.origin}/posts/${post.id}`;
    try {
      await navigator.clipboard.writeText(url);
      toast('게시물 링크를 복사했어요. 친구에게 공유해보세요!');
    } catch {
      toast(url);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('게시물을 삭제할까요?')) return;
    await postApi.remove(post.id);
    toast('삭제했습니다.');
    onDelete?.(post.id);
  };

  const status = post.eventStatus;

  return (
    <article className={`card post-card ${post.postType !== 'DAILY' ? 'is-promo' : ''}`}>
      <header className="post-head">
        <Link to={`/members/${post.writer.id}`} className="post-writer">
          <Avatar member={post.writer} size={42} />
          <div>
            <strong>{post.writer.nickname}</strong>
            {post.writer.accountType === 'BUSINESS' && (
              <span className="biz-chip">{post.writer.businessCategory ?? '가게'}</span>
            )}
            <div className="muted small">{timeAgo(post.createdAt)}</div>
          </div>
        </Link>
        <div className="post-head-right">
          {post.postType !== 'DAILY' && (
            <span className={`type-badge type-${post.postType.toLowerCase()}`}>{TYPE_LABEL[post.postType]}</span>
          )}
          {post.mine && (
            <div className="menu">
              <button className="icon-btn" onClick={() => setMenuOpen((v) => !v)} aria-label="메뉴">⋯</button>
              {menuOpen && (
                <div className="menu-pop">
                  <button onClick={() => navigate(`/posts/${post.id}/edit`)}>수정</button>
                  <button className="danger" onClick={handleDelete}>삭제</button>
                </div>
              )}
            </div>
          )}
        </div>
      </header>

      {post.imageUrl && (
        <Link to={`/posts/${post.id}`} className="post-image">
          <img src={post.imageUrl} alt="" loading="lazy" />
          {status === 'ONGOING' && <span className="dday">진행중 · {dDay(post.eventEndDate)}</span>}
          {status === 'UPCOMING' && <span className="dday upcoming">오픈 예정</span>}
          {status === 'ENDED' && <span className="dday ended">종료된 이벤트</span>}
        </Link>
      )}

      <div className="post-body">
        <p className={detail ? '' : 'clamp'}>{renderContent(post.content)}</p>

        {post.postType === 'EVENT' && post.eventStartDate && (
          <div className="event-box">
            📅 이벤트 기간 <b>{post.eventStartDate} ~ {post.eventEndDate}</b>
          </div>
        )}
        {post.linkUrl && (
          <a className="link-btn" href={post.linkUrl} target="_blank" rel="noreferrer">
            가게 정보 / 구매하러 가기 →
          </a>
        )}
      </div>

      {!preview && (
      <footer className="post-actions">
        <button className={`act like ${post.liked ? 'on' : ''}`} onClick={handleLike} aria-label="좋아요">
          {post.liked ? '♥' : '♡'} <span>{post.likeCount}</span>
        </button>
        <Link className="act" to={`/posts/${post.id}`}>💬 <span>{post.commentCount}</span></Link>
        <span className="act muted">👁 {post.viewCount}</span>
        <button className="act share" onClick={handleShare}>공유</button>
      </footer>
      )}
    </article>
  );
}
