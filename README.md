# BaseLog

Spring Boot 4.x 기반 멀티모듈 헥사고날 아키텍처 백엔드 프로젝트.  
Flutter 앱과 REST API로 연동하며, JWT 인증 + 소셜 로그인(Kakao/Google) 구조를 목표로 합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Kotlin 2.1.21 |
| Framework | Spring Boot 4.0.6 (Spring Framework 7.x) |
| Security | Spring Security 7.x + JWT (nimbus-jose-jwt) |
| ORM | Spring Data JPA + Hibernate 7.x |
| DB (개발) | H2 인메모리 |
| Build | Gradle 9.4.1 (Kotlin DSL) |
| Java | Java 21 |

---

## 모듈 구조

```
BaseLog/
├── app/        # 실행 진입점 (@SpringBootApplication)
├── common/     # 공통 인프라 — 예외, 필터, 응답 포맷, Security
└── member/     # 회원 도메인
```

**의존 방향:** `app` → `common`, `member` / `member` → `common`

### 헥사고날 패키지 구조 (member 기준)

```
adapter/
  input/web/              # Controller, DTO
  output/persistence/     # JpaEntity, JpaRepository, PersistenceAdapter
application/service/      # UseCase 구현체
domain/
  exception/              # ErrorCode
  model/                  # Member, RefreshToken, TokenPair 등 VO
  port/input/             # UseCase 인터페이스
  port/output/            # Repository 인터페이스
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
| POST | `/auth/login` | 로그인 (임시: memberId 직접 입력) |
| POST | `/auth/refresh` | 액세스 토큰 갱신 |
| POST | `/auth/logout` | 로그아웃 |

### 인증 필요 (`Authorization: Bearer <accessToken>`)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/members/{id}` | 회원 조회 |

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

## 공통 인프라 (common 모듈)

| 클래스 | 역할 |
|--------|------|
| `MdcLoggingFilter` | 요청마다 `requestId` MDC 주입 (X-Request-Id 헤더 or UUID 자동 생성) |
| `RequestResponseLoggingFilter` | 요청/응답 본문 로깅, 민감 정보 마스킹 (password, token 등) |
| `GlobalExceptionHandler` | `BusinessException` 및 Spring MVC 예외 전체 처리 |
| `SecurityConfig` | Spring Security FilterChain 설정 |
| `JwtProvider` | JWT 발급/검증 |
| `JwtAuthenticationFilter` | Bearer 토큰 파싱 및 SecurityContext 설정 |
