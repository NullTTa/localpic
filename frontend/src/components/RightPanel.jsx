import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { etcApi, memberApi, postApi } from '../api';
import { useAuth } from '../hooks/useAuth';
import Avatar from './Avatar';
import { dDay } from '../utils';

export default function RightPanel() {
  const { user } = useAuth();
  const [tags, setTags] = useState([]);
  const [events, setEvents] = useState([]);
  const [stores, setStores] = useState([]);

  useEffect(() => {
    // 세 영역은 서로 의존하지 않으므로 동시에 요청
    Promise.all([etcApi.trending(), postApi.ongoingEvents(), memberApi.recommend()])
      .then(([t, e, s]) => { setTags(t); setEvents(e); setStores(s); })
      .catch(() => {});
  }, [user]);

  const follow = async (id) => {
    await memberApi.toggleFollow(id);
    setStores((prev) => prev.filter((s) => s.id !== id));
  };

  return (
    <aside className="side">
      <section className="card side-box">
        <h4>🔥 지금 뜨는 해시태그</h4>
        <ol className="trend">
          {tags.map((t, i) => (
            <li key={t.name}>
              <span className="rank">{i + 1}</span>
              <Link to={`/search?q=${encodeURIComponent('#' + t.name)}`}>#{t.name}</Link>
              <span className="muted small">{t.count}</span>
            </li>
          ))}
        </ol>
      </section>

      <section className="card side-box">
        <h4>🎁 진행 중인 이벤트</h4>
        {events.length === 0 && <p className="muted small">진행 중인 이벤트가 없어요.</p>}
        {events.map((e) => (
          <Link key={e.id} to={`/posts/${e.id}`} className="event-item">
            {e.imageUrl && <img src={e.imageUrl} alt="" />}
            <div>
              <strong>{e.writer.nickname}</strong>
              <p className="muted small clamp1">{e.content}</p>
            </div>
            <span className="dday-chip">{dDay(e.eventEndDate)}</span>
          </Link>
        ))}
      </section>

      {stores.length > 0 && (
        <section className="card side-box">
          <h4>🏪 추천 동네 가게</h4>
          {stores.map((s) => (
            <div key={s.id} className="store-item">
              <Link to={`/members/${s.id}`} className="store-link">
                <Avatar member={s} size={34} />
                <div>
                  <strong>{s.nickname}</strong>
                  <div className="muted small">{s.businessCategory}</div>
                </div>
              </Link>
              {user && <button className="text-btn accent" onClick={() => follow(s.id)}>팔로우</button>}
            </div>
          ))}
        </section>
      )}
    </aside>
  );
}
