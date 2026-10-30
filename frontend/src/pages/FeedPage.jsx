import { useCallback, useState } from 'react';
import { postApi } from '../api';
import PostCard from '../components/PostCard';
import useInfiniteList from '../hooks/useInfiniteList';
import { useAuth } from '../hooks/useAuth';

const TABS = [
  { key: 'ALL', label: '전체' },
  { key: 'FOLLOWING', label: '팔로잉', needLogin: true },
  { key: 'PROMOTION', label: '홍보' },
  { key: 'EVENT', label: '이벤트' },
];

export default function FeedPage() {
  const { user } = useAuth();
  const [tab, setTab] = useState('ALL');
  const fetchPage = useCallback((page) => postApi.feed({ tab, page }), [tab]);
  const { items, loading, hasNext, error, sentinelRef, updateItem, removeItem } =
    useInfiniteList(fetchPage, [tab]);

  return (
    <>
      <div className="tabs sticky">
        {TABS.filter((t) => !t.needLogin || user).map((t) => (
          <button key={t.key} className={tab === t.key ? 'on' : ''} onClick={() => setTab(t.key)}>
            {t.label}
          </button>
        ))}
      </div>

      {items.map((post) => (
        <PostCard key={post.id} post={post} onChange={updateItem} onDelete={removeItem} />
      ))}

      {loading && <SkeletonCard />}
      {error && <p className="empty">{error}</p>}
      {!loading && !hasNext && items.length === 0 && (
        <p className="empty">{tab === 'FOLLOWING' ? '팔로우한 가게의 새 소식이 없어요.' : '게시물이 없어요.'}</p>
      )}
      {!hasNext && items.length > 0 && <p className="end">모든 소식을 확인했어요 ✓</p>}
      <div ref={sentinelRef} style={{ height: 1 }} />
    </>
  );
}

function SkeletonCard() {
  return (
    <div className="card skeleton">
      <div className="sk-row"><div className="sk-circle" /><div className="sk-line w40" /></div>
      <div className="sk-img" />
      <div className="sk-line" /><div className="sk-line w60" />
    </div>
  );
}
