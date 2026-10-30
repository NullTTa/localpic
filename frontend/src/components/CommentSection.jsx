import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Avatar from './Avatar';
import { commentApi } from '../api';
import { useAuth } from '../hooks/useAuth';
import { timeAgo } from '../utils';
import { toast } from './Toast';

export default function CommentSection({ postId, onCountChange }) {
  const { user } = useAuth();
  const [comments, setComments] = useState([]);
  const [text, setText] = useState('');
  const [sending, setSending] = useState(false);

  useEffect(() => {
    commentApi.list(postId).then(setComments);
  }, [postId]);

  const submit = async (e) => {
    e.preventDefault();
    if (!text.trim() || sending) return;
    setSending(true);
    try {
      const saved = await commentApi.write(postId, text.trim());
      setComments((prev) => [...prev, saved]); // 새로고침 없이 바로 목록 끝에 추가
      onCountChange?.(1);
      setText('');
    } catch (err) {
      toast(err.message);
    } finally {
      setSending(false);
    }
  };

  const remove = async (id) => {
    await commentApi.remove(id);
    setComments((prev) => prev.filter((c) => c.id !== id));
    onCountChange?.(-1);
  };

  return (
    <section className="card comments">
      <h3>댓글 {comments.length}</h3>
      <ul>
        {comments.map((c) => (
          <li key={c.id}>
            <Link to={`/members/${c.writer.id}`}><Avatar member={c.writer} size={32} /></Link>
            <div className="comment-body">
              <div>
                <strong>{c.writer.nickname}</strong>
                <span className="muted small"> · {timeAgo(c.createdAt)}</span>
              </div>
              <p>{c.content}</p>
            </div>
            {c.mine && <button className="text-btn" onClick={() => remove(c.id)}>삭제</button>}
          </li>
        ))}
        {comments.length === 0 && <li className="muted">첫 댓글을 남겨보세요.</li>}
      </ul>
      {user ? (
        <form className="comment-form" onSubmit={submit}>
          <input value={text} onChange={(e) => setText(e.target.value)} placeholder="댓글 달기..." maxLength={500} />
          <button className="btn" disabled={!text.trim() || sending}>게시</button>
        </form>
      ) : (
        <p className="muted small"><Link to="/login">로그인</Link> 후 댓글을 남길 수 있어요.</p>
      )}
    </section>
  );
}
