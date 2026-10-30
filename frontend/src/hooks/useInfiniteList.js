import { useCallback, useEffect, useRef, useState } from 'react';

/**
 * 무한 스크롤 공통 훅.
 * - fetchPage(page) 는 { content, hasNext } 를 돌려줘야 한다.
 * - 목록 맨 아래 div 에 sentinelRef 를 달아두면, 그 div 가 화면에 보일 때 다음 페이지를 불러온다.
 * - deps 가 바뀌면(탭 전환 등) 처음부터 다시 불러온다.
 *   이전 탭의 응답이 늦게 도착해도 version 이 달라서 버려진다.
 */
export default function useInfiniteList(fetchPage, deps = []) {
  const [state, setState] = useState({ items: [], page: 0, hasNext: true });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const stateRef = useRef(state);
  const fetchRef = useRef(fetchPage);
  const loadingRef = useRef(false);
  const versionRef = useRef(0);
  const sentinelRef = useRef(null);

  stateRef.current = state;
  fetchRef.current = fetchPage;

  const load = useCallback(async () => {
    const { page, hasNext } = stateRef.current;
    if (loadingRef.current || !hasNext) return;

    const version = versionRef.current;
    loadingRef.current = true;
    setLoading(true);
    try {
      const res = await fetchRef.current(page);
      if (version !== versionRef.current) return; // 탭이 바뀌었으면 버린다
      const next = {
        items: page === 0 ? res.content : [...stateRef.current.items, ...res.content],
        page: page + 1,
        hasNext: res.hasNext,
      };
      stateRef.current = next;
      setState(next);
    } catch (e) {
      if (version === versionRef.current) {
        setError(e.message);
        setState((s) => ({ ...s, hasNext: false }));
      }
    } finally {
      if (version === versionRef.current) {
        loadingRef.current = false;
        setLoading(false);
      }
    }
  }, []);

  // 조건이 바뀌면 초기화 후 첫 페이지 로드
  useEffect(() => {
    versionRef.current += 1;
    loadingRef.current = false;
    stateRef.current = { items: [], page: 0, hasNext: true };
    setState(stateRef.current);
    setError(null);
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  // 맨 아래가 보이면 다음 페이지
  useEffect(() => {
    const el = sentinelRef.current;
    if (!el) return undefined;
    const observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting) load();
    }, { rootMargin: '300px' });
    observer.observe(el);
    return () => observer.disconnect();
  }, [load]);

  /** 목록 안의 한 항목만 바꿀 때 (좋아요 등) — 전체를 다시 불러오지 않는다 */
  const updateItem = (id, updater) =>
    setState((s) => ({ ...s, items: s.items.map((it) => (it.id === id ? { ...it, ...updater(it) } : it)) }));

  const removeItem = (id) => setState((s) => ({ ...s, items: s.items.filter((it) => it.id !== id) }));

  return {
    items: state.items,
    hasNext: state.hasNext,
    loading,
    error,
    sentinelRef,
    updateItem,
    removeItem,
  };
}
