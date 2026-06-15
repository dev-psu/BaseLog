# 진행 현황

## 완료

### common 모듈
- [x] `BusinessException(httpStatus, code, message)` — 공통 예외
- [x] `GlobalExceptionHandler` — BusinessException, Validation, 404/405/400/500 전체 처리
- [x] `MdcLoggingFilter` — 요청별 requestId MDC 주입, X-Request-Id 헤더
- [x] `RequestResponseLoggingFilter` — 요청/응답 로깅, 민감 정보 마스킹
- [x] `ApiResponse<T>` + `ErrorDetail` — 공통 응답 포맷
- [x] `JwtProvider` — HS256 토큰 발급/검증
- [x] `JwtAuthenticationFilter` — Bearer 토큰 파싱, SecurityContext 설정
- [x] `SecurityConfig` — Spring Security FilterChain, 공개/보호 경로 분리

### member 모듈
- [x] `Member`, `Email`, `Nickname`, `SocialAccount` 도메인 모델
- [x] `MemberService` — register, getById, checkEmailAvailable, checkNicknameAvailable
- [x] `MemberController` — POST /api/members, GET /api/members/{id}, GET check/email, GET check/nickname
- [x] `RegisterMemberRequest` — @Valid 검증 (@NotBlank, @Email, @Size)
- [x] `MemberJpaEntity`, `MemberPersistenceAdapter`
- [x] `ErrorCode` enum — DUPLICATE_EMAIL, DUPLICATE_NICKNAME, MEMBER_NOT_FOUND, INVALID_TOKEN, EXPIRED_TOKEN
- [x] `AuthService` — login, refresh (rotation), logout
- [x] `AuthController` — POST /auth/login (임시), /auth/refresh, /auth/logout
- [x] `RefreshToken` 도메인 모델 + 퍼시스턴스 (JpaEntity, Repository, Adapter)

### 인프라
- [x] H2 인메모리 DB (개발용, ddl-auto: create-drop)
- [x] OSIV 비활성화 (spring.jpa.open-in-view: false)
- [x] logback-spring.xml — requestId 포함 로그 패턴 (local/default 프로파일)
- [x] 포트 8081 설정

---

## 남은 작업

### 1순위 — 소셜 로그인 (Kakao / Google)
- [ ] `SocialLoginUseCase` 정의
- [ ] Kakao OAuth2 토큰 교환 클라이언트 구현
- [ ] Google OAuth2 토큰 교환 클라이언트 구현
- [ ] `/auth/login` 플레이스홀더를 실제 소셜 로그인 흐름으로 교체
  - 현재: `{ memberId: Long }` 직접 입력
  - 목표: `{ provider: "KAKAO"|"GOOGLE", code: String }` 인가 코드 방식

### 2순위 — CORS 설정
- [ ] `CorsConfigurationSource` 빈 등록
- [ ] Flutter 앱 origin 허용 (개발: `*`, 운영: 특정 도메인)
- [ ] SecurityConfig에 `.cors { }` 추가

### 3순위 — Flyway 마이그레이션
- [ ] `flyway-core` 의존성 추가
- [ ] `spring.jpa.hibernate.ddl-auto: validate`로 전환
- [ ] `src/main/resources/db/migration/V1__init.sql` 작성
- [ ] 운영 DB 전환 시 필수

### 4순위 — Swagger / OpenAPI
- [ ] `springdoc-openapi` 의존성 추가 (Spring Boot 4.x 호환 버전 확인 필요)
- [ ] 컨트롤러 어노테이션 추가
- [ ] 인증 헤더 설정 (Bearer token)

### 5순위 — 운영 환경 설정
- [ ] `application-prod.yml` 작성
- [ ] logback prod 프로파일 추가 (파일 appender, JSON 포맷)
- [ ] `jwt.secret` 환경변수 주입 방식 정립
- [ ] 실제 운영 DB 연결 설정 (MySQL / PostgreSQL)

### 6순위 — 토큰 만료 시간 조정 및 보안 강화
- [ ] Refresh Token 만료 시간 슬라이딩 방식 검토
- [ ] 동일 memberId의 refresh token 중복 발급 제한 (기기별 관리)
- [ ] Access Token Blacklist 또는 짧은 만료 + silent refresh 전략 결정
