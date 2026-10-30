import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { etcApi } from '../api';
import Avatar from '../components/Avatar';
import PostCard from '../components/PostCard';

export default function SearchPage() {
  const [params, setParams] = useSearchParams();
  const q = params.get('q') ?? '';
  const [input, setInput] = useState(q);
  const [result, setResult] = useState(null);
  const [trending, setTrending] = useState([]);

  useEffect(() => { etcApi.trending().then(setTrending); }, []);

  useEffect(() => {
    setInput(q);
    if (!q.trim()) { setResult(null); return; }
    etcApi.search(q).then(setResult);
  }, [q]);

  const submit = (e) => {
    e.preventDefault();
    if (input.trim()) setParams({ q: input.trim() });
  };

  const updatePost = (id, updater) =>
    setResult((r) => ({
      ...r,
      posts: { ...r.posts, content: r.posts.content.map((p) => (p.id === id ? { ...p, ...updater(p) } : p)) },
    }));

  return (
    <>
      <form className="card search-bar" onSubmit={submit}>
        <input value={input} onChange={(e) => setInput(e.target.value)} placeholder="#해시태그, 가게 이름, 키워드" />
        <button className="btn btn-primary">검색</button>
      </form>

      {!result && (
        <section className="card">
          <h3>인기 해시태그</h3>
          <div className="chips big">
            {trending.map((t) => (
              <Link key={t.name} className="chip" to={`/search?q=${encodeURIComponent('#' + t.name)}`}>
                #{t.name} <span className="muted small">{t.count}</span>
              </Link>
            ))}
          </div>
        </section>
      )}

      {result && (
        <>
          {result.members.length > 0 && (
            <section className="card">
              <h3>계정</h3>
              <div className="member-list">
                {result.members.map((m) => (
                  <Link key={m.id} to={`/members/${m.id}`} className="member-item">
                    <Avatar member={m} size={44} />
                    <div><strong>{m.nickname}</strong><div className="muted small">{m.businessCategory ?? '개인'}</div></div>
                  </Link>
                ))}
              </div>
            </section>
          )}
          <h3 className="section-title">"{q}" 게시물 {result.posts.content.length}개</h3>
          {result.posts.content.map((p) => <PostCard key={p.id} post={p} onChange={updatePost} />)}
          {result.posts.content.length === 0 && <p className="empty">검색 결과가 없어요.</p>}
        </>
      )}
    </>
  );
}
