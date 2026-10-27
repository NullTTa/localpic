import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// 개발 서버(5173)에서 /api 요청을 Spring Boot(8080)로 넘긴다 → 같은 출처라 세션 쿠키가 그대로 동작
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080',
      '/seed': 'http://localhost:8080',
    },
  },
});
