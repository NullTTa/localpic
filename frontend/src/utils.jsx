import { Link } from 'react-router-dom';

export function timeAgo(iso) {
  if (!iso) return '';
  const diff = (Date.now() - new Date(iso).getTime()) / 1000;
  if (diff < 60) return '방금 전';
  if (diff < 3600) return `${Math.floor(diff / 60)}분 전`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}시간 전`;
  if (diff < 86400 * 7) return `${Math.floor(diff / 86400)}일 전`;
  return new Date(iso).toLocaleDateString('ko-KR');
}

/** 이벤트 종료까지 남은 일수 */
export function dDay(endDate) {
  if (!endDate) return null;
  const end = new Date(`${endDate}T00:00:00`);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const days = Math.round((end - today) / 86400000);
  return days === 0 ? 'D-DAY' : days > 0 ? `D-${days}` : '종료';
}

/** 본문 속 #해시태그 를 검색 링크로 바꿔서 보여준다 */
export function renderContent(text) {
  return text.split(/(#[0-9A-Za-z가-힣_]{1,30})/g).map((part, i) =>
    part.startsWith('#') ? (
      <Link key={i} className="tag" to={`/search?q=${encodeURIComponent(part)}`}>{part}</Link>
    ) : (
      <span key={i}>{part}</span>
    )
  );
}

export const TYPE_LABEL = { DAILY: '일상', PROMOTION: '홍보', EVENT: '이벤트' };
