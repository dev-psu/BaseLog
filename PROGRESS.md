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
- [x] `GameStatus` enum — SCHEDULED, COMPLETED, CANCELED, POSTPONED
- [x] `Game` 도메인 모델 — gameNumber(더블헤더 순번) 포함
- [x] `GameErrorCode` — GAME_NOT_FOUND
- [x] `GetGameUseCase` — getBySeasonAndMonth, getById
- [x] `GameRepository` 포트
- [x] `GameService`
- [x] `GameJpaEntity`, `GameJpaRepository`, `GamePersistenceAdapter`
- [x] `GameController` — GET /api/games, GET /api/games/{id} (인증 불필요)
- [x] Flyway V4 — game 테이블 (UNIQUE: season + game_type + game_date + home_team + away_team + game_number)

### kbo-crawler (별도 프로젝트)
- [x] 프로젝트 세팅 — Playwright, SQLAlchemy, APScheduler
- [x] `GameType`, `GameStatus`, `Game` 도메인 모델 (BaseLog와 동일 값)
- [x] `GameEntity` — BaseLog game 테이블과 동일 스키마 (team 테이블 없음, KboTeam 코드 직접 저장)
- [x] `upsert_games` — MySQL ON DUPLICATE KEY UPDATE
- [x] `KakaoOAuth2Adapter` — KBO 일정 페이지 Playwright 크롤링 (연도/월/경기종류 선택)
- [x] `parse_schedule` — 더블헤더 game_number 자동 추적, 상태 파싱
- [x] `crawl_month` / `crawl_season` / `crawl_all` — 월/시즌/전체 수집 단위
- [x] CLI — `--full`, `--year`, `--type`, `--month` 인자 지원
- [x] `scheduler.py` — APScheduler, 매일 08:00 KST `run_full` 실행

### 인프라
- [x] H2 인메모리 DB (기본/테스트, ddl-auto: create-drop)
- [x] Docker Compose + MySQL 8.0 (로컬 개발 환경)
- [x] Flyway 마이그레이션 — V1(초기 스키마), V2(프로필 필드), V3(email nullable), V4(game 테이블)
- [x] `application-local.yml` — MySQL + Flyway 활성화 프로파일
- [x] OSIV 비활성화
- [x] logback-spring.xml — requestId 포함 로그 패턴
- [x] 포트 8081

---

## 남은 작업

### 1순위 — 크롤러 셀렉터 검증
- [ ] KBO 사이트 실제 HTML 확인 — `#tblScheduleList`, `#ddlSeries`, `td.day` 셀렉터
- [ ] `_GAME_TYPE_VALUES` 딕셔너리 값 검증 (현재 추정값)
- [ ] 팀명 표기 확인 — `TEAM_NAME_TO_CODE` 매핑
- [ ] E2E 크롤링 테스트 (정규시즌 1개월)

### 2순위 — 직관/집관 기록 (WatchLog)
- [ ] `WatchLog` 도메인 설계 (경기, 사용자, 좌석, 동반자, 메모 등)
- [ ] 기록 작성/수정/삭제 API
- [ ] 기록 공개/비공개 설정 (isPublic)
- [ ] Flyway V5 — watch_log 테이블

### 3순위 — 피드/공유
- [ ] 공개 기록 피드 조회 API
- [ ] 팔로우/팔로워 기능 여부 결정

### 4순위 — Swagger / OpenAPI
- [ ] `springdoc-openapi` 의존성 추가 (Spring Boot 4.x 호환 버전 확인 필요)
- [ ] 컨트롤러 어노테이션 추가
- [ ] 인증 헤더 설정 (Bearer token)

### 5순위 — 운영 환경 설정
- [ ] `application-prod.yml` 작성
- [ ] logback prod 프로파일 (파일 appender, JSON 포맷)
- [ ] 실제 운영 DB 연결 설정

### 6순위 — 토큰 보안 강화
- [ ] 기기별 refresh token 관리
- [ ] Access Token Blacklist 또는 silent refresh 전략 결정

### 보류
- [ ] 구글 로그인 — 나중에 필요 시 추가
- [ ] 네이버 로그인 — 연령대 높은 야구 팬 커버용으로 추후 고려
- [ ] Apple 로그인 — iOS 앱 심사 시 소셜 로그인 있으면 필수
- [ ] 실시간 경기 결과 — KBO 공식 API 없음, 유료 서비스(SportRadar 등) 또는 추후 결정
