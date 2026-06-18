# BaseLog API

KBO 직관 기록 서비스의 백엔드 API 서버.  
Spring Boot 4.x / Kotlin / 헥사고날 아키텍처 기반 멀티모듈 프로젝트.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Kotlin 2.1.21 |
| Framework | Spring Boot 4.0.6 (Spring Framework 7.x) |
| Security | Spring Security 7.x + JWT (nimbus-jose-jwt) |
| ORM | Spring Data JPA + Hibernate 7.x |
| DB | MySQL 8.0 (Docker Compose) |
| Migration | Flyway |
| Build | Gradle 9.4.1 (Kotlin DSL) |
| Java | Java 21 |

---

## 모듈 구조

```
BaseLog/
├── app/        # 실행 진입점, application.yml, Flyway 마이그레이션
├── common/     # 공통 인프라 — 예외, 필터, 응답 포맷, Security, JWT, KboTeam
├── member/     # 회원/인증 도메인
├── game/       # 경기 일정/상세 도메인
└── watchlog/   # 직관·집관 기록 도메인
```

헥사고날 패키지 구조 (모듈별 동일):

```
adapter/
  input/web/           # Controller, DTO
  output/persistence/  # JpaEntity, JpaRepository, PersistenceAdapter
application/service/   # UseCase 구현체
domain/
  model/               # 도메인 모델
  port/input/          # UseCase 인터페이스
  port/output/         # Repository 포트
```

---

## 실행

Docker MySQL이 실행 중이어야 합니다.

```bash
# MySQL 컨테이너 시작
docker-compose up -d

# API 서버 시작
./gradlew :app:bootRun
```

- API: `http://localhost:8081`

> 8080은 Jenkins가 점유 중이므로 8081 사용

---

## 환경 설정

`application.yml` 기준 MySQL 로컬 개발 설정:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/baselog
    username: root
    password: baselog
  flyway:
    enabled: true
  jpa:
    hibernate:
      ddl-auto: validate
```

운영 환경에서 교체해야 하는 값:

| 환경변수 | 설명 |
|---------|------|
| `JWT_SECRET` | HS256 서명 키 (32바이트 이상 base64) |
| `KMA_SERVICE_KEY` | 기상청 초단기실황 API 서비스 키 |
| `CORS_ALLOWED_ORIGINS` | 허용 오리진 (기본값 `*`) |

---

## API 엔드포인트

### 인증 불필요

| Method | Path | 설명 |
|--------|------|------|
| POST | `/auth/social-login` | 카카오 소셜 로그인 |
| POST | `/auth/complete-signup` | 온보딩 완료 (닉네임 + 응원팀) |
| POST | `/auth/refresh` | 액세스 토큰 갱신 |
| POST | `/auth/logout` | 로그아웃 |
| GET | `/api/games` | 월별 경기 일정 (`?season=&month=&favoriteTeam=`) |
| GET | `/api/games/{id}` | 경기 상세 (이닝 스코어, 날씨 포함) |
| GET | `/api/members/{id}/followers` | 팔로워 목록 |
| GET | `/api/members/{id}/following` | 팔로잉 목록 |

### 인증 필요 (`Authorization: Bearer <accessToken>`)

| Method | Path | 설명 |
|--------|------|------|
| GET | `/api/members/me` | 내 프로필 조회 |
| PATCH | `/api/members/me` | 프로필 수정 |
| POST | `/api/watch-logs` | 기록 작성 |
| GET | `/api/watch-logs/{id}` | 기록 상세 |
| PATCH | `/api/watch-logs/{id}` | 기록 수정 |
| DELETE | `/api/watch-logs/{id}` | 기록 삭제 |
| GET | `/api/watch-logs/me` | 내 기록 목록 (페이징) |
| GET | `/api/watch-logs/me/stats` | 승률·통계·구장 도장판 |
| POST | `/api/members/{id}/follow` | 팔로우 |
| DELETE | `/api/members/{id}/follow` | 언팔로우 |

### 공통 응답 포맷

```json
{ "success": true,  "data": { ... }, "error": null }
{ "success": false, "data": null,   "error": { "code": "ERROR_CODE", "message": "..." } }
```

---

## 인증 플로우

```
카카오 로그인 (access token)
  → POST /auth/social-login
      ├── 기존 회원 → { accessToken, refreshToken }
      └── 신규 회원 → { isNewUser: true, onboardingToken }
          → POST /auth/complete-signup (nickname + favoriteTeam?)
              → { accessToken, refreshToken }
```

- Access Token: JWT HS256, 30분
- Refresh Token: UUID, DB 저장, 7일, 갱신 시 교체(Rotation)

---

## 경기 일정 연동

경기 데이터는 [kbo-crawler](../kbo-crawler) 프로젝트가 MySQL에 직접 upsert.  
API 서버는 DB에서 읽기만 함.

날씨 정보 (`GET /api/games`, `GET /api/games/{id}`):
- 오늘 경기 한정으로 기상청 초단기실황 데이터 포함
- 구장별 격자 좌표 매핑, 30분 Caffeine 캐시

---

## DB 마이그레이션 이력

| Version | 내용 |
|---------|------|
| V1 | 초기 스키마 (member, refresh_token) |
| V2 | member 프로필 필드 추가 |
| V3 | email nullable |
| V4 | game 테이블 생성 |
| V5 | game_number 컬럼 |
| V6 | game.status LIVE 추가, game_detail 테이블 |
| V7 | game_number SMALLINT → INT |
| V8 | game_detail 컬럼 TINYINT → INT |
| V9 | watch_log 테이블 |
| V10 | watch_log_image 테이블 |
| V11 | follows 테이블 |
