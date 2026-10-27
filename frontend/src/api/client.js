import axios from 'axios';

/**
 * 모든 API 호출이 거치는 axios 인스턴스.
 * - withCredentials: 세션 쿠키(JSESSIONID)를 같이 보낸다
 * - 응답 인터셉터: 서버의 {success, message, data} 에서 data 만 꺼내 주고,
 *   실패하면 서버 message 를 Error 로 바꿔 화면에서 그대로 보여줄 수 있게 한다
 */
const client = axios.create({
  baseURL: '/api',
  withCredentials: true,
});

client.interceptors.response.use(
  (res) => res.data.data,
  (err) => {
    const message = err.response?.data?.message || '네트워크 오류가 발생했습니다.';
    const error = new Error(message);
    error.status = err.response?.status;
    return Promise.reject(error);
  }
);

export default client;
