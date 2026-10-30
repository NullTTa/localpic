import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { memberApi, postApi } from '../api';
import { useAuth } from '../hooks/useAuth';
import useInfiniteList from '../hooks/useInfiniteList';
import Avatar from '../components/Avatar';
import { toast } from '../components/Toast';
import { TYPE_LABEL } from '../utils';

export default function ProfilePage() {
  const { memberId } = useParams();
  const { user, setUser } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({ nickname: '', bio: '', location: '' });
  const [image, setImage] = useState(null);

  const fetchPage = useCallback((page) => postApi.byMember(memberId, page), [memberId]);
  const { items, sentinelRef, hasNext } = useInfiniteList(fetchPage, [memberId]);

  useEffect(() => {
    memberApi.profile(memberId).then((p) => {
      setProfile(p);
      setForm({ nickname: p.nickname, bio: p.bio ?? '', location: p.location ?? '' });
    });
  }, [memberId]);

  const toggleFollow = async () => {
    if (!user) return navigate('/login');
    const res = await memberApi.toggleFollow(memberId);
    setProfile((p) => ({ ...p, following: res.following, followerCount: res.followerCount }));
  };

  const saveProfile = async (e) => {
    e.preventDefault();
    const data = new FormData();
    Object.entries(form).forEach(([k, v]) => data.append(k, v));
    if (image) data.append('image', image);
    try {
      const updated = await memberApi.updateMe(data);
      setProfile(updated);
      setUser(updated);
      setEditing(false);
      toast('프로필을 저장했어요.');
    } catch (err) {
      toast(err.message);
    }
  };

  if (!profile) return <p className="empty">불러오는 중…</p>;

  return (
    <>
      <section className="card profile">
        <Avatar member={profile} size={96} />
        <div className="profile-info">
          <div className="profile-name">
            <h2>{profile.nickname}</h2>
            {profile.accountType === 'BUSINESS' && <span className="biz-chip">{profile.businessCategory}</span>}
            {profile.me ? (
              <button className="btn" onClick={() => setEditing((v) => !v)}>프로필 편집</button>
            ) : (
              <button className={`btn ${profile.following ? '' : 'btn-primary'}`} onClick={toggleFollow}>
                {profile.following ? '팔로잉 ✓' : '팔로우'}
              </button>
            )}
          </div>
          <ul className="stats">
            <li><b>{profile.postCount}</b> 게시물</li>
            <li><b>{profile.followerCount}</b> 팔로워</li>
            <li><b>{profile.followingCount}</b> 팔로잉</li>
          </ul>
          {profile.location && <p className="muted small">📍 {profile.location}</p>}
          <p>{profile.bio}</p>
        </div>
      </section>

      {editing && (
        <form className="card edit-profile" onSubmit={saveProfile}>
          <label className="label">프로필 사진</label>
          <input type="file" accept="image/*" onChange={(e) => setImage(e.target.files[0])} />
          <label className="label">닉네임</label>
          <input value={form.nickname} onChange={(e) => setForm({ ...form, nickname: e.target.value })} />
          <label className="label">소개</label>
          <textarea rows={3} value={form.bio} onChange={(e) => setForm({ ...form, bio: e.target.value })} />
          <label className="label">위치</label>
          <input value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} />
          <div className="form-actions">
            <button type="button" className="btn" onClick={() => setEditing(false)}>취소</button>
            <button className="btn btn-primary">저장</button>
          </div>
        </form>
      )}

      <div className="grid">
        {items.map((p) => (
          <Link key={p.id} to={`/posts/${p.id}`} className="grid-item">
            {p.imageUrl ? <img src={p.imageUrl} alt="" /> : <div className="grid-text">{p.content}</div>}
            {p.postType !== 'DAILY' && <span className={`type-badge mini type-${p.postType.toLowerCase()}`}>{TYPE_LABEL[p.postType]}</span>}
            <div className="grid-hover">♥ {p.likeCount} · 💬 {p.commentCount}</div>
          </Link>
        ))}
      </div>
      {!hasNext && items.length === 0 && <p className="empty">아직 게시물이 없어요.</p>}
      <div ref={sentinelRef} style={{ height: 1 }} />
    </>
  );
}
