import { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { postApi } from '../api';
import { useAuth } from '../hooks/useAuth';
import PostCard from '../components/PostCard';
import { toast } from '../components/Toast';

const TYPES = [
  { key: 'DAILY', label: '일상', desc: '가볍게 나누는 이야기' },
  { key: 'PROMOTION', label: '홍보', desc: '신메뉴·상품 소개', biz: true },
  { key: 'EVENT', label: '이벤트', desc: '기간 한정 혜택', biz: true },
];

const today = () => new Date().toISOString().slice(0, 10);

/**
 * 글쓰기/수정 화면.
 * - 유형에 따라 필요한 입력만 보여준다 (이벤트 → 기간, 홍보/이벤트 → 링크)
 * - 해시태그를 입력하는 즉시 칩으로 보여준다
 * - 오른쪽 미리보기로 피드에 어떻게 보일지 바로 확인
 */
export default function PostWritePage() {
  const { postId } = useParams();
  const editing = Boolean(postId);
  const { user } = useAuth();
  const navigate = useNavigate();
  const fileRef = useRef(null);

  const [form, setForm] = useState({
    postType: 'DAILY', content: '', eventStartDate: today(), eventEndDate: '', linkUrl: '',
  });
  const [image, setImage] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [dragging, setDragging] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!editing) return;
    postApi.detail(postId).then((p) => {
      setForm({
        postType: p.postType, content: p.content,
        eventStartDate: p.eventStartDate ?? today(), eventEndDate: p.eventEndDate ?? '',
        linkUrl: p.linkUrl ?? '',
      });
      setPreviewUrl(p.imageUrl);
    });
  }, [editing, postId]);

  // 선택한 이미지를 업로드 전에 바로 미리보기
  useEffect(() => {
    if (!image) return undefined;
    const url = URL.createObjectURL(image);
    setPreviewUrl(url);
    return () => URL.revokeObjectURL(url);
  }, [image]);

  const hashtags = useMemo(() => {
    const found = form.content.match(/#[0-9A-Za-z가-힣_]{1,30}/g) ?? [];
    return [...new Set(found.map((t) => t.toLowerCase()))];
  }, [form.content]);

  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  const pickFile = (file) => {
    if (!file) return;
    if (!file.type.startsWith('image/')) return setError('이미지 파일만 올릴 수 있어요.');
    if (file.size > 10 * 1024 * 1024) return setError('이미지는 10MB 이하만 가능해요.');
    setError('');
    setImage(file);
  };

  const validate = () => {
    if (!form.content.trim()) return '내용을 입력해주세요.';
    if (form.postType === 'EVENT') {
      if (!form.eventStartDate || !form.eventEndDate) return '이벤트 기간을 입력해주세요.';
      if (form.eventEndDate < form.eventStartDate) return '종료일이 시작일보다 빠를 수 없어요.';
    }
    return '';
  };

  const submit = async (e) => {
    e.preventDefault();
    const msg = validate();
    if (msg) return setError(msg);

    const data = new FormData();
    data.append('content', form.content);
    data.append('postType', form.postType);
    if (form.postType === 'EVENT') {
      data.append('eventStartDate', form.eventStartDate);
      data.append('eventEndDate', form.eventEndDate);
    }
    if (form.postType !== 'DAILY' && form.linkUrl) data.append('linkUrl', form.linkUrl);
    if (image) data.append('image', image);

    setSaving(true);
    try {
      const saved = editing ? await postApi.update(postId, data) : await postApi.create(data);
      toast(editing ? '수정했어요.' : '게시물을 올렸어요!');
      navigate(`/posts/${saved.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const isBiz = user?.accountType === 'BUSINESS';
  const previewPost = {
    id: 0,
    writer: user,
    content: form.content || '내용을 입력하면 여기에 미리 보여요 #해시태그',
    imageUrl: previewUrl,
    postType: form.postType,
    eventStartDate: form.postType === 'EVENT' ? form.eventStartDate : null,
    eventEndDate: form.postType === 'EVENT' ? form.eventEndDate : null,
    eventStatus: form.postType === 'EVENT' && form.eventEndDate ? 'ONGOING' : null,
    linkUrl: form.postType !== 'DAILY' ? form.linkUrl : null,
    likeCount: 0, commentCount: 0, viewCount: 0, createdAt: new Date().toISOString(),
  };

  return (
    <div className="write-wrap">
      <form className="card write-form" onSubmit={submit}>
        <h2>{editing ? '게시물 수정' : '새 게시물'}</h2>

        <label className="label">게시물 유형</label>
        <div className="type-select">
          {TYPES.map((t) => {
            const locked = t.biz && !isBiz;
            return (
              <button
                type="button" key={t.key} disabled={locked}
                className={form.postType === t.key ? 'on' : ''}
                onClick={() => setForm((f) => ({ ...f, postType: t.key }))}
                title={locked ? '사업자 계정만 사용할 수 있어요' : ''}
              >
                <b>{t.label}</b>
                <span>{locked ? '🔒 사업자 전용' : t.desc}</span>
              </button>
            );
          })}
        </div>

        <label className="label">사진</label>
        <div
          className={`dropzone ${dragging ? 'drag' : ''} ${previewUrl ? 'has' : ''}`}
          onClick={() => fileRef.current.click()}
          onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
          onDragLeave={() => setDragging(false)}
          onDrop={(e) => { e.preventDefault(); setDragging(false); pickFile(e.dataTransfer.files[0]); }}
        >
          {previewUrl ? <img src={previewUrl} alt="미리보기" /> : <p>📷 클릭하거나 사진을 끌어다 놓으세요</p>}
          <input ref={fileRef} type="file" accept="image/*" hidden onChange={(e) => pickFile(e.target.files[0])} />
        </div>

        <label className="label">내용 <span className="muted small">{form.content.length}/2000</span></label>
        <textarea
          rows={6} maxLength={2000} value={form.content} onChange={set('content')}
          placeholder="가게 소식을 알려주세요. #해시태그 를 달면 검색에 노출돼요."
        />
        {hashtags.length > 0 && (
          <div className="chips">{hashtags.map((t) => <span key={t} className="chip">{t}</span>)}</div>
        )}

        {form.postType === 'EVENT' && (
          <div className="row2">
            <div>
              <label className="label">시작일</label>
              <input type="date" value={form.eventStartDate} onChange={set('eventStartDate')} />
            </div>
            <div>
              <label className="label">종료일</label>
              <input type="date" min={form.eventStartDate} value={form.eventEndDate} onChange={set('eventEndDate')} />
            </div>
          </div>
        )}

        {form.postType !== 'DAILY' && (
          <>
            <label className="label">가게·상품 링크 <span className="muted small">(선택)</span></label>
            <input type="url" value={form.linkUrl} onChange={set('linkUrl')} placeholder="https://map.naver.com/..." />
          </>
        )}

        {error && <p className="error">{error}</p>}
        <div className="form-actions">
          <button type="button" className="btn" onClick={() => navigate(-1)}>취소</button>
          <button className="btn btn-primary" disabled={saving}>{saving ? '올리는 중…' : editing ? '수정하기' : '공유하기'}</button>
        </div>
      </form>

      <div className="write-preview">
        <p className="label">피드 미리보기</p>
        {user && <PostCard post={previewPost} preview detail />}
      </div>
    </div>
  );
}
