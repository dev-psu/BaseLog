# Troubleshooting

Spring Boot 4.x / Spring Framework 7.x / Jackson 3.x / Gradle 9.x 조합에서 발생한 오류 및 해결 방법 모음.

---

## Jackson 3.x 패키지 변경

**증상**
```
Cannot construct instance of `RegisterMemberRequest` (no Creators, like default constructor, exist)
```

**원인**  
Jackson 3.x에서 패키지가 `com.fasterxml.jackson` → `tools.jackson`으로 변경됨.  
`com.fasterxml.jackson.module:jackson-module-kotlin` (2.x)를 사용하면 Kotlin 데이터 클래스 역직렬화 실패.

**해결**
```toml
# libs.versions.toml
jackson-module-kotlin = { module = "tools.jackson.module:jackson-module-kotlin" }
```

---

## @WebMvcTest 제거됨

**증상**
```
Unresolved reference: WebMvcTest
```

**원인**  
Spring Boot 4.x에서 `@WebMvcTest` 어노테이션이 제거됨.

**해결**  
`MockMvcBuilders.standaloneSetup()`으로 대체:
```kotlin
@ExtendWith(MockitoExtension::class)
class SomeControllerTest {
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }
}
```

---

## @MockBean → @MockitoBean

**증상**  
Spring Boot 4.x에서 `@MockBean` 컴파일 오류 또는 동작 이상.

**해결**  
`@MockitoBean`으로 교체 (Spring Boot 4.x 기준).

---

## JUnit Platform Launcher 누락

**증상**
```
No tests found
```
또는 테스트 실행 자체가 안 됨.

**원인**  
Gradle 9.x에서 JUnit Platform Launcher를 명시적으로 선언해야 함.

**해결**
```kotlin
testRuntimeOnly("org.junit.platform:junit-platform-launcher")
```

---

## mockito-kotlin 버전 미관리

**증상**
```
Could not find org.mockito.kotlin:mockito-kotlin
```

**원인**  
Spring Boot BOM에 `mockito-kotlin`이 포함되어 있지 않아 버전을 직접 지정해야 함.

**해결**
```toml
mockito-kotlin = { module = "org.mockito.kotlin:mockito-kotlin", version = "5.4.0" }
```

---

## ContentCachingRequestWrapper cacheLimit 누락

**증상**
```
No value passed for parameter 'cacheLimit'
```

**원인**  
Spring Boot 4.x에서 `ContentCachingRequestWrapper` 생성자가 변경됨. `cacheLimit` 파라미터 필수.

**해결**
```kotlin
ContentCachingRequestWrapper(request, MAX_BODY_LENGTH)
```

---

## Spring Data JPA 메서드명 오류

**증상**
```
PropertyReferenceException: No property 'existsNickname' found for type 'MemberJpaEntity'
```

**원인**  
Spring Data JPA 메서드 이름 규칙 위반. 필드명 앞에 `By`가 빠짐.

**해결**
```kotlin
// 잘못됨
fun existsNickname(nickname: String): Boolean

// 올바름
fun existsByNickname(nickname: String): Boolean
```

---

## nimbus-jose-jwt BOM 미관리

**증상**
```
Could not find com.nimbusds:nimbus-jose-jwt:.
```

**원인**  
Spring Boot 4.x BOM이 `nimbus-jose-jwt`를 직접 관리하지 않음.

**해결**  
직접 선언 대신 `spring-security-oauth2-jose`를 사용 (BOM 관리됨, nimbus가 transitive로 포함):
```toml
spring-security-oauth2-jose = { module = "org.springframework.security:spring-security-oauth2-jose" }
```

---

## Spring Boot 4.x Flyway 자동 설정 모듈 분리

**증상**
Flyway가 전혀 실행되지 않음 (관련 로그 없음), `spring.flyway.enabled: true` 설정도 무시됨.

**원인**
Spring Boot 4.x에서 Flyway 자동 설정이 `spring-boot-autoconfigure`에서 `spring-boot-flyway`로 분리됨.

**해결**
```toml
spring-boot-flyway = { module = "org.springframework.boot:spring-boot-flyway" }
```
```kotlin
implementation(libs.spring.boot.flyway)
```

---

## 포트 충돌 (8080 → 8081)

**증상**  
`Response code: 403 (Forbidden)` — Spring이 아닌 Jenkins 응답.

**원인**  
8080 포트를 Jenkins가 점유 중.

**해결**
```yaml
# application.yml
server:
  port: 8081
```
