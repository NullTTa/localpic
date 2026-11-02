# LocalPick 🏪 — 우리 동네 가게 홍보/마케팅 SNS

> 동네 카페·빵집·꽃집 같은 소상공인이 **신메뉴·이벤트·할인 소식**을 SNS 피드로 올리고,
> 주민들은 팔로우·좋아요·댓글·해시태그로 소식을 받아보는 **지역 기반 홍보 SNS** 입니다.

## 기술 스택
| 구분 | 사용 기술 |
|---|---|
| Backend | Java 17, Spring Boot 3.3, Spring Data JPA, Bean Validation, BCrypt |
| DB | H2 (개발, 샘플 데이터 자동 생성) / MySQL (배포) |
| Frontend | React 18, React Router 6, Axios, Vite |
| 협업 | Git / GitHub (팀원별 브랜치 → dev → main) |

## 실행 방법
### 1) 백엔드
```bash
cd backend
mvn spring-boot:run        # 또는 IntelliJ 에서 LocalPickApplication 실행 (Maven 프로젝트로 열기)
```
- http://localhost:8080 (dev 프로필: H2 메모리 DB + 샘플 데이터)
- H2 콘솔: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:localpick`)

### 2) 프론트엔드
```bash
cd frontend
npm install
npm run dev
```
- http://localhost:5173 접속 (`/api` 요청은 Vite 프록시로 8080 에 전달)

### 시연용 계정 (비밀번호 모두 `password123`)
| 계정 | 유형 |
|---|---|
| cafe@localpick.com (오늘의원두) | 사업자 – 홍보/이벤트 글, 인사이트 |
| bakery@localpick.com (빵굽는마을) | 사업자 |
| yeongho@localpick.com (영호) | 개인 |

## 주요 기능
- 회원가입/로그인 (개인 · 사업자 계정 구분, 세션 기반)
- 피드: 전체 / 팔로잉 / 홍보 / 이벤트 탭, **무한 스크롤**
- 게시물 CRUD: 일상·홍보·이벤트 유형, 이미지 업로드, 이벤트 기간(D-day), 가게 링크
- 좋아요(낙관적 업데이트), 댓글, 팔로우, 공유 링크 복사
- 해시태그 자동 추출 · 인기 해시태그 · 해시태그/계정/키워드 검색
- 알림 (좋아요·댓글·팔로우, 안 읽은 개수 배지)
- **홍보 인사이트 대시보드** (사업자 전용: 일자별 조회수, 신규 팔로워, 참여율, TOP 게시물)

## REST API
| Method | URL | 설명 |
|---|---|---|
| POST | /api/auth/signup · /login · /logout | 회원가입 · 로그인 · 로그아웃 |
| GET | /api/auth/me | 로그인 회원 정보 |
| GET | /api/posts?type=&page= | 피드 (type: PROMOTION, EVENT, DAILY) |
| GET | /api/posts/following | 팔로잉 피드 |
| GET | /api/posts/{id} | 게시물 상세 (조회수 +1) |
| POST / PUT / DELETE | /api/posts, /api/posts/{id} | 작성(multipart) · 수정 · 삭제 |
| POST | /api/posts/{id}/like | 좋아요 토글 |
| GET / POST | /api/posts/{id}/comments | 댓글 목록 · 작성 |
| DELETE | /api/comments/{id} | 댓글 삭제 |
| GET | /api/posts/events/ongoing | 진행 중 이벤트 |
| GET / PUT | /api/members/{id}, /api/members/me | 프로필 조회 · 수정 |
| POST | /api/members/{id}/follow | 팔로우 토글 |
| GET | /api/members/recommend | 추천 가게 |
| GET | /api/hashtags/trending | 인기 해시태그 |
| GET | /api/search?q= | 검색 (`#태그` 또는 키워드) |
| GET / PATCH | /api/notifications, /unread-count, /read-all | 알림 |
| GET | /api/insights/me | 홍보 인사이트 (사업자) |

모든 응답은 `{ "success": true, "message": "OK", "data": ... }` 형태입니다.

## 패키지 구조
```
backend/src/main/java/com/localpick
├── common      ApiResponse, PageResponse, BaseTimeEntity, 예외 처리
├── config      WebConfig(CORS·업로드), @LoginMember 리졸버, FileStore, 샘플 데이터
└── domain
    ├── member  회원·인증       ├── follow        팔로우
    ├── post    게시물·검색     ├── hashtag       해시태그
    ├── like    좋아요          ├── notification  알림
    ├── comment 댓글            └── insight       조회 로그·인사이트
frontend/src
├── api         axios 인스턴스, API 함수
├── hooks       useAuth, useInfiniteList
├── components  Layout, PostCard, CommentSection, RightPanel ...
└── pages       Feed, PostDetail, PostWrite, Profile, Search, Notification, Insight, Login, Signup
```

## 브랜치 전략
- `main` : 배포/제출 버전 (v1.0 MVC → v2.0 REST + React)
- `dev` : 통합 브랜치
- `yeongho` `minsu` `seoyeon` `jihun` : 팀원별 작업 브랜치 → `dev` 로 merge
