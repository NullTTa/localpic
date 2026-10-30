import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { postApi } from '../api';
import PostCard from '../components/PostCard';
import CommentSection from '../components/CommentSection';

export default function PostDetailPage() {
  const { postId } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    setPost(null);
    postApi.detail(postId).then(setPost).catch((e) => setError(e.message));
  }, [postId]);

  if (error) return <p className="empty">{error}</p>;
  if (!post) return <p className="empty">불러오는 중…</p>;

  return (
    <>
      <button className="text-btn back" onClick={() => navigate(-1)}>← 뒤로</button>
      <PostCard
        detail
        post={post}
        onChange={(id, updater) => setPost((p) => ({ ...p, ...updater(p) }))}
        onDelete={() => navigate('/')}
      />
      <CommentSection
        postId={post.id}
        onCountChange={(d) => setPost((p) => ({ ...p, commentCount: p.commentCount + d }))}
      />
    </>
  );
}
