# 진행 현황

## 완료

### common 모듈
- [x] `BusinessException(httpStatus, code, message)` — 공통 예외
- [x] `GlobalExceptionHandler` — BusinessException, Validation, 404/405/400/500 전체 처리
- [x] `MdcLoggingFilter` — 요청별 requestId MDC 주입, X-Request-Id 헤더
- [x] `RequestResponseLoggingFilter` — 요청/응답 로깅, 민감 정보 마스킹
- [x] `ApiResponse<T>` + `ErrorDetail` — 공통 응답 포맷
- [x] `JwtProvider` — HS256 토큰 발급/검증, onboarding 토큰 발급/검증 (10분 만료)
- [x] `JwtAuthenticationFilter` — Bearer 토큰 파싱, SecurityContext 설정
- [x] `SecurityConfig` — Spring Security FilterChain, CORS 설정, 공개/보호 경로 분리
- [x] `KboTeam` enum — member, game 모듈 공유 (typealias로 기존 코드 호환 유지)

### member 모듈
- [x] `Member`, `Email`, `Nickname`, `SocialAccount`, `KboTeam`, `KboTeam` 도메인 모델
- [x] `Member` 프로필 필드 — `profileImageUrl`, `favoriteTeam(KboTeam)`, `bio`, `isPublic`
- [x] `MemberService` — register, getById, checkEmailAvailable, checkNicknameAvailable, updateProfile
- [x] `MemberController` — POST /api/members, GET /api/members/me, PATCH /api/members/me, GET check/email, GET check/nickname
- [x] `UpdateProfileUseCase` + `UpdateProfileCommand`
- [x] `MemberJpaEntity`, `MemberPersistenceAdapter`
- [x] `ErrorCode` enum — DUPLICATE_EMAIL, DUPLICATE_NICKNAME, MEMBER_NOT_FOUND, INVALID_TOKEN, EXPIRED_TOKEN, UNSUPPORTED_PROVIDER, SOCIAL_AUTH_FAILED, INVALID_ONBOARDING_TOKEN
- [x] `RefreshToken` 도메인 모델 + 퍼시스턴스 (JpaEntity, Repository, Adapter)

### 소셜 로그인
- [x] `AuthUseCase` — `socialLogin()`, `completeSignup()`, `refresh()`, `logout()`
- [x] `SocialOAuth2Port` — 소셜 제공자 출력 포트
- [x] `KakaoOAuth2Adapter` — RestClient 기반 카카오 토큰 교환 + 프로필 조회
- [x] `AuthService` — find-or-create 로직, onboarding 토큰 발급
- [x] `MemberRepository` — `findBySocialAccount()` 추가 (JPQL)
- [x] `POST /auth/social-login` — 기존 회원: TokenPair / 신규 회원: onboardingToken
- [x] `POST /auth/complete-signup` — onboardingToken + 닉네임 + 응원팀(선택) → 회원 생성 + TokenPair
- [x] `POST /auth/refresh` — refresh token rotation
- [x] `POST /auth/logout`
- [x] `GET /auth/kakao/callback` — 로컬 테스트용 콜백 엔드포인트
- [x] 이메일 nullable 처리 — 카카오 비즈앱 없이도 providerId만으로 회원 식별
- [x] 카카오 로그인 E2E 테스트 완료 (신규 회원, 재로그인 모두 확인)

### 온보딩 플로우
```
카카오 로그인
  → 신규: onboardingToken 발급
      → POST /auth/complete-signup (nickname + favoriteTeam?)
          → accessToken + refreshToken
  → 기존: accessToken + refreshToken 바로 발급
```

### game 모듈
- [x] `GameType` enum — EXHIBITION(시범), REGULAR(정규), POSTSEASON(포스트)
- [x] `GameStatus` enum — SCHEDULED, LIVE, COMPLETED, CANCELED, POSTPONED
- [x] `Game` 도메인 모델 — gameNumber(더블헤더 순번), detail(GameDetail?) 포함
- [x] `GameDetail` 도메인 모델 — 이닝별 스코어, H/E
- [x] `GameErrorCode` — GAME_NOT_FOUND
- [x] `GetGameUseCase` — getBySeasonAndMonth, getById
- [x] `GameRepository` 포트 — findDetailByGameId 포함
- [x] `GameService` — getById 시 game_detail 함께 조회
- [x] `GameJpaEntity`, `GameJpaRepository`, `GamePersistenceAdapter`
- [x] `GameDetailJpaEntity`, `GameDetailJpaRepository` — innings JSON 컬럼 포함
- [x] `InningsConverter` — Jackson 3.x 기반 JSON ↔ InningsData 변환
- [x] `GameController` — GET /api/games, GET /api/games/{id} (인증 불필요)
- [x] `GET /api/games/{id}` 응답에 `detail` 포함 (nullable)
- [x] Flyway V4 — game 테이블 생성
- [x] Flyway V5 — no-op (game_number는 V4에 포함)
- [x] Flyway V6 — game.status LIVE 추가, game_detail 테이블 생성
- [x] Flyway V7 — game.game_number SMALLINT → INT
- [x] Flyway V8 — game_detail 컬럼 TINYINT → INT

### kbo-crawler (별도 프로젝트)
- [x] 프로젝트 세팅 — Playwright, SQLAlchemy, APScheduler, requests
- [x] `GameType`, `GameStatus`(LIVE 포함), `Game` 도메인 모델
- [x] `GameEntity`, `GameDetailEntity` — BaseLog game/game_detail 테이블과 동일 스키마
- [x] `upsert_games` — MySQL ON DUPLICATE KEY UPDATE
- [x] `upsert_today_statuses`, `find_game_id`, `upsert_game_detail`
- [x] `fetch_schedule_html` — KBO 공식 사이트 Playwright 크롤링
- [x] `parse_schedule` — 더블헤더 game_number 자동 추적, 상태 파싱
- [x] `crawler/naver.py` — Naver API 클라이언트 (경기 목록, 이닝 스코어)
- [x] 3-트랙 스케줄러 — Track A(08:00 일정), Track B(매시간 상태), Track C(17~23시 라이브)
- [x] CLI — `--full`, `--year`, `--type`, `--month` 인자 지원
- [x] E2E 테스트 완료 — 정규시즌 파싱, 이닝 스코어 저장, API 응답 검증

### 인프라
- [x] H2 인메모리 DB (기본/테스트, ddl-auto: create-drop)
- [x] Docker Compose + MySQL 8.0 (로컬 개발 환경)
- [x] Flyway 마이그레이션 — V1~V8
- [x] `application-local.yml` — MySQL + Flyway 활성화 프로파일
- [x] OSIV 비활성화
- [x] logback-spring.xml — requestId 포함 로그 패턴
- [x] 포트 8081

---

## 남은 작업

### 1순위 — 직관/집관 기록 (WatchLog) ✅
- [x] `WatchLog` 도메인 설계 — game, venue, seatInfo, companions, cost, weather, mood, isPublic
- [x] 기록 CRUD API — POST/PATCH/DELETE /api/watch-logs, GET /api/watch-logs/{id}
- [x] 내 기록 목록 — GET /api/watch-logs/me (페이징)
- [x] 승률/통계 — GET /api/watch-logs/me/stats (직관/집관 승률 분리, 연승 스트릭, 구장 도장판, 시즌 지출)
- [x] 날씨 참고 — 경기 상세/목록에 기상청 초단기실황 포함 (오늘 경기만, 30분 Caffeine 캐시)
- [x] 응원팀 상단 정렬 — GET /api/games?favoriteTeam=KIA
- [x] Flyway V9 — watch_log 테이블 (cost, weather, venue, seat 컬럼 포함)
- [x] Flyway V10 — watch_log_image 테이블
- [ ] 이미지 업로드 — S3 presigned URL (S3 설정 후 구현 예정)

### 2순위 — 피드/공유
- [ ] 공개 기록 피드 조회 API
- [ ] 팔로우/팔로워 기능 여부 결정

### 3순위 — Swagger / OpenAPI
- [ ] `springdoc-openapi` 의존성 추가 (Spring Boot 4.x 호환 버전 확인 필요)
- [ ] 컨트롤러 어노테이션 추가
- [ ] 인증 헤더 설정 (Bearer token)

### 4순위 — 운영 환경 설정
- [ ] `application-prod.yml` 작성
- [ ] logback prod 프로파일 (파일 appender, JSON 포맷)
- [ ] 실제 운영 DB 연결 설정

### 5순위 — 토큰 보안 강화
- [ ] 기기별 refresh token 관리
- [ ] Access Token Blacklist 또는 silent refresh 전략 결정

### 보류
- [ ] 구글 로그인 — 나중에 필요 시 추가
- [ ] 네이버 로그인 — 연령대 높은 야구 팬 커버용으로 추후 고려
- [ ] Apple 로그인 — iOS 앱 심사 시 소셜 로그인 있으면 필수
