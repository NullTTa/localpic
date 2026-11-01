import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { etcApi } from '../api';
import { TYPE_LABEL } from '../utils';

/** 사업자 계정 홍보 성과 대시보드 (확장 기능) */
export default function InsightPage() {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    etcApi.insight().then(setData).catch((e) => setError(e.message));
  }, []);

  if (error) return <p className="empty">{error}</p>;
  if (!data) return <p className="empty">불러오는 중…</p>;

  const kpis = [
    { label: '총 조회수', value: data.totalViews.toLocaleString() },
    { label: '총 좋아요', value: data.totalLikes.toLocaleString() },
    { label: '총 댓글', value: data.totalComments.toLocaleString() },
    { label: '참여율', value: `${data.engagementRate}%`, hint: '(좋아요+댓글)/조회수' },
    { label: '팔로워', value: data.followerCount.toLocaleString() },
  ];

  return (
    <>
      <div className="insight-head">
        <h2>📊 홍보 인사이트</h2>
        <p className="muted">최근 7일 동안 내 가게 게시물이 얼마나 반응을 얻었는지 확인하세요.</p>
      </div>

      <div className="kpis">
        {kpis.map((k) => (
          <div key={k.label} className="card kpi">
            <span className="muted small">{k.label}</span>
            <strong>{k.value}</strong>
            {k.hint && <span className="muted tiny">{k.hint}</span>}
          </div>
        ))}
      </div>

      <section className="card">
        <h3>일자별 조회수 · 신규 팔로워</h3>
        <BarChart daily={data.daily} />
      </section>

      <div className="insight-grid">
        <section className="card">
          <h3>반응 좋은 게시물 TOP 3</h3>
          <ol className="top-posts">
            {data.topPosts.map((p, i) => (
              <li key={p.id}>
                <span className="rank big">{i + 1}</span>
                {p.imageUrl && <img src={p.imageUrl} alt="" />}
                <Link to={`/posts/${p.id}`}>
                  <span className={`type-badge mini type-${p.postType.toLowerCase()}`}>{TYPE_LABEL[p.postType]}</span>
                  <p className="clamp1">{p.content}</p>
                  <span className="muted small">👁 {p.viewCount} · ♥ {p.likeCount} · 💬 {p.commentCount}</span>
                </Link>
              </li>
            ))}
          </ol>
        </section>

        <section className="card">
          <h3>유형별 평균 좋아요</h3>
          <TypeBars avg={data.avgLikeByType} />
          <h3 className="mt">많이 쓴 해시태그</h3>
          <div className="chips">
            {data.topHashtags.map((t) => <span key={t.name} className="chip">#{t.name} · {t.count}</span>)}
          </div>
        </section>
      </div>
    </>
  );
}

function BarChart({ daily }) {
  const W = 620, H = 220, P = 32;
  const max = Math.max(1, ...daily.map((d) => d.views));
  const bw = (W - P * 2) / daily.length;
  return (
    <svg viewBox={`0 0 ${W} ${H}`} className="chart" role="img" aria-label="일자별 조회수">
      {[0, 0.5, 1].map((r) => (
        <g key={r}>
          <line x1={P} x2={W - P} y1={H - P - (H - P * 2) * r} y2={H - P - (H - P * 2) * r} className="grid-line" />
          <text x={P - 6} y={H - P - (H - P * 2) * r + 4} textAnchor="end" className="axis">{Math.round(max * r)}</text>
        </g>
      ))}
      {daily.map((d, i) => {
        const h = ((H - P * 2) * d.views) / max;
        const x = P + i * bw + bw * 0.2;
        return (
          <g key={d.date}>
            <rect x={x} y={H - P - h} width={bw * 0.6} height={h} rx="4" className="bar" />
            <text x={x + bw * 0.3} y={H - P - h - 6} textAnchor="middle" className="val">{d.views}</text>
            {d.newFollowers > 0 && (
              <text x={x + bw * 0.3} y={H - P - h - 22} textAnchor="middle" className="follow-val">+{d.newFollowers}👤</text>
            )}
            <text x={x + bw * 0.3} y={H - 10} textAnchor="middle" className="axis">{d.date.slice(5).replace('-', '/')}</text>
          </g>
        );
      })}
    </svg>
  );
}

function TypeBars({ avg }) {
  const entries = Object.entries(avg);
  const max = Math.max(1, ...entries.map(([, v]) => v));
  return (
    <div className="type-bars">
      {entries.map(([type, v]) => (
        <div key={type} className="type-bar">
          <span>{TYPE_LABEL[type]}</span>
          <div className="track"><div className={`fill type-${type.toLowerCase()}`} style={{ width: `${(v / max) * 100}%` }} /></div>
          <b>{v.toFixed(1)}</b>
        </div>
      ))}
    </div>
  );
}
