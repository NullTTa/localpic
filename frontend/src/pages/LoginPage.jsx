import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');

  const doLogin = async (email, password) => {
    try {
      await login(email, password);
      navigate('/');
    } catch (e) {
      setError(e.message);
    }
  };

  const submit = (e) => {
    e.preventDefault();
    doLogin(form.email, form.password);
  };

  return (
    <div className="auth">
      <div className="auth-brand">
        <h1>Local<span>Pick</span></h1>
        <p>우리 동네 가게의 신메뉴, 이벤트, 할인 소식을<br />SNS 피드로 가장 먼저 만나보세요.</p>
      </div>
      <form className="card auth-card" onSubmit={submit}>
        <h2>로그인</h2>
        <input type="email" placeholder="이메일" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input type="password" placeholder="비밀번호" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        {error && <p className="error">{error}</p>}
        <button className="btn btn-primary wide">로그인</button>
        <div className="demo">
          <span className="muted small">시연용 계정</span>
          <button type="button" className="btn" onClick={() => doLogin('cafe@localpick.com', 'password123')}>☕ 사업자(오늘의원두)</button>
          <button type="button" className="btn" onClick={() => doLogin('yeongho@localpick.com', 'password123')}>🙋 개인(영호)</button>
        </div>
        <p className="muted small center">계정이 없나요? <Link to="/signup">회원가입</Link></p>
      </form>
    </div>
  );
}
