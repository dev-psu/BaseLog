# BaseLog

Spring Boot 4.x 기반 멀티모듈 헥사고날 아키텍처 백엔드 프로젝트.  
Flutter 앱과 REST API로 연동하며, JWT 인증 + 소셜 로그인(Kakao) 구조.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Kotlin 2.1.21 |
| Framework | Spring Boot 4.0.6 (Spring Framework 7.x) |
| Security | Spring Security 7.x + JWT (nimbus-jose-jwt) |
| ORM | Spring Data JPA + Hibernate 7.x |
| DB (개발) | H2 인메모리 |
| DB (로컬) | MySQL 8.0 (Docker Compose) |
| Migration | Flyway |
| Build | Gradle 9.4.1 (Kotlin DSL) |
| Java | Java 21 |

---

## 모듈 구조

```
BaseLog/
├── app/        # 실행 진입점 (@SpringBootApplication)
├── common/     # 공통 인프라 — 예외, 필터, 응답 포맷, Security, KboTeam
├── member/     # 회원 도메인
└── game/       # 경기 일정 도메인
```

**의존 방향:** `app` → `common`, `member`, `game` / `member`, `game` → `common`

### 헥사고날 패키지 구조 (game 기준)

```
adapter/
  input/web/              # Controller, DTO
  output/persistence/     # JpaEntity, JpaRepository, PersistenceAdapter
application/service/      # UseCase 구현체
domain/
  exception/              # GameErrorCode
  model/                  # Game, GameStatus, GameType
  port/input/             # GetGameUseCase
  port/output/            # GameRepository
```

---

## 실행

```bash
./gradlew :app:bootRun
```

- API: `http://localhost:8081`
- H2 콘솔: `http://localhost:8081/h2-console`
  - JDBC URL: `jdbc:h2:mem:baselog`
  - Username: `sa` / Password: (없음)

> 8080은 Jenkins가 점유 중이므로 8081 사용

---

## API 엔드포인트

### 인증 불필요

| Method | Path | 설명 |
|--------|------|------|
| POST | `/api/members` | 회원 등록 |
| GET | `/api/members/check/email?email=` | 이메일 중복 확인 |
| GET | `/api/members/check/nickname?nickname=` | 닉네임 중복 확인 |
| POST | `/auth/social-login` | 소셜 로그인 (카카오) |
| POST | `/auth/complete-signup` | 온보딩 완료 (닉네임 + 응원팀) |
| POST | `/auth/refresh` | 액세스 토큰 갱신 |
| POST | `/auth/logout` | 로그아웃 |
| GET | `/auth/kakao/callback` | 카카오 콜백 (로컬 테스트용) |
| GET | `/api/games?season=&month=` | 월별 경기 일정 조회 |
| GET | `/api/games/{id}` | 경기 상세 조회 |

### 인증 필요 (`Authorization: Bearer <accessToken>`)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/members/me` | 내 정보 조회 |
| PATCH | `/api/members/me` | 프로필 수정 |

### 응답 포맷

```json
// 성공
{ "success": true, "data": { ... }, "error": null }

// 실패
{ "success": false, "data": null, "error": { "code": "ERROR_CODE", "message": "..." } }
```

---

## JWT 인증 구조

- **Access Token**: HS256 JWT, 30분 유효 (`jwt.access-token-expiry: 1800`)
- **Refresh Token**: UUID, DB 저장, 7일 유효 (`jwt.refresh-token-expiry: 604800`)
- Refresh Token은 갱신 시 교체 (Rotation) — 재사용 공격 방지
- 만료/무효 토큰 → `401 INVALID_TOKEN` / `EXPIRED_TOKEN`

### 환경변수 오버라이드 (운영)

```bash
JWT_SECRET=<base64-encoded-32bytes-이상>
```

`application.yml`의 `jwt.secret`은 개발용 기본값이므로 운영 환경에서는 반드시 환경변수로 교체

---

## 경기 도메인 (game 모듈)

| 필드 | 타입 | 설명 |
|------|------|------|
| season | SMALLINT | 연도 |
| gameType | ENUM | EXHIBITION / REGULAR / POSTSEASON |
| gameDate | DATE | 경기 날짜 |
| gameTime | TIME nullable | 시작 시간 |
| homeTeam | ENUM(KboTeam) | 홈팀 |
| awayTeam | ENUM(KboTeam) | 원정팀 |
| venue | VARCHAR(100) nullable | 구장 |
| homeScore / awayScore | INT nullable | 점수 (예정 경기는 null) |
| status | ENUM | SCHEDULED / COMPLETED / CANCELED / POSTPONED |
| gameNumber | SMALLINT | 더블헤더 순번 (기본 1) |

UNIQUE: `(season, game_type, game_date, home_team, away_team, game_number)`

---

## 공통 인프라 (common 모듈)

| 클래스 | 역할 |
|--------|------|
| `KboTeam` | KBO 10개 구단 enum (member, game 공유) |
| `MdcLoggingFilter` | 요청마다 `requestId` MDC 주입 |
| `RequestResponseLoggingFilter` | 요청/응답 본문 로깅, 민감 정보 마스킹 |
| `GlobalExceptionHandler` | `BusinessException` 및 Spring MVC 예외 전체 처리 |
| `SecurityConfig` | Spring Security FilterChain 설정 |
| `JwtProvider` | JWT 발급/검증 |
| `JwtAuthenticationFilter` | Bearer 토큰 파싱 및 SecurityContext 설정 |
