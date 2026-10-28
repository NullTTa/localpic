import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api';
import { useAuth } from '../hooks/useAuth';

const CATEGORIES = ['카페', '베이커리', '음식점', '꽃집', '의류', '뷰티', '기타'];

export default function SignupPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({
    email: '', password: '', passwordCheck: '', nickname: '',
    accountType: 'PERSONAL', businessCategory: '카페', location: '',
  });
  const [error, setError] = useState('');
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    if (form.password !== form.passwordCheck) return setError('비밀번호가 서로 달라요.');
    try {
      const { passwordCheck, ...body } = form;
      if (body.accountType === 'PERSONAL') { body.businessCategory = null; body.location = null; }
      await authApi.signup(body);
      await login(form.email, form.password);
      navigate('/');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="auth">
      <div className="auth-brand">
        <h1>Local<span>Pick</span></h1>
        <p>사장님이라면 사업자 계정으로 가입하고<br />홍보·이벤트 게시물과 인사이트를 사용해보세요.</p>
      </div>
      <form className="card auth-card" onSubmit={submit}>
        <h2>회원가입</h2>
        <div className="type-select two">
          <button type="button" className={form.accountType === 'PERSONAL' ? 'on' : ''} onClick={() => setForm({ ...form, accountType: 'PERSONAL' })}>
            <b>개인</b><span>소식 구독 · 일상 공유</span>
          </button>
          <button type="button" className={form.accountType === 'BUSINESS' ? 'on' : ''} onClick={() => setForm({ ...form, accountType: 'BUSINESS' })}>
            <b>사업자</b><span>가게 홍보 · 인사이트</span>
          </button>
        </div>
        <input type="email" placeholder="이메일" value={form.email} onChange={set('email')} required />
        <input placeholder="닉네임 (가게 이름)" value={form.nickname} onChange={set('nickname')} required />
        <input type="password" placeholder="비밀번호 (8자 이상)" value={form.password} onChange={set('password')} required />
        <input type="password" placeholder="비밀번호 확인" value={form.passwordCheck} onChange={set('passwordCheck')} required />
        {form.accountType === 'BUSINESS' && (
          <div className="row2">
            <select value={form.businessCategory} onChange={set('businessCategory')}>
              {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
            </select>
            <input placeholder="가게 위치 (예: 인천 부평구)" value={form.location} onChange={set('location')} />
          </div>
        )}
        {error && <p className="error">{error}</p>}
        <button className="btn btn-primary wide">가입하기</button>
        <p className="muted small center">이미 계정이 있나요? <Link to="/login">로그인</Link></p>
      </form>
    </div>
  );
}
