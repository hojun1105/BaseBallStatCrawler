# 프로젝트 실행 가이드

## 1. IntelliJ IDEA에서 실행

IntelliJ IDEA는 Kotlin 플러그인이 기본 내장되어 있으므로 **별도 설치 필요 없습니다**.

### 실행 방법:

1. **프로젝트 열기**
   - IntelliJ IDEA에서 이 프로젝트 폴더를 엽니다
   - Gradle 프로젝트로 자동 인식됩니다

2. **의존성 다운로드**
   - IntelliJ가 자동으로 Gradle 빌드를 시작합니다
   - 또는 우측 상단의 Gradle 탭에서 `Refresh Gradle Project` 클릭

3. **실행**
   - `src/main/kotlin/Application.kt` 파일을 열고
   - `main` 함수 왼쪽의 녹색 실행 버튼 클릭
   - 또는 `Shift + F10`

## 2. 명령줄에서 실행 (Gradle 사용)

### Windows:
```bash
# Gradle Wrapper 사용 (별도 Gradle 설치 불필요)
.\gradlew.bat bootRun
```

### Mac/Linux:
```bash
./gradlew bootRun
```

## 3. JAR 파일로 빌드 후 실행

### 빌드:
```bash
# Windows
.\gradlew.bat bootJar

# Mac/Linux
./gradlew bootJar
```

### 실행:
```bash
java -jar build/libs/BaseBallStatCrawler-1.0-SNAPSHOT.jar
```

## 필수 설정

### 1. 데이터베이스 설정
`src/main/resources/application.yml` 파일에서 PostgreSQL 설정 확인:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/kbohub
    username: postgres
    password: 1234
```

### 2. 네이버 API 키 설정
환경 변수로 설정하거나 `application.yml`에 직접 입력:
```yaml
naver:
  api:
    client-id: your_client_id
    client-secret: your_client_secret
```

또는 환경 변수:
- `NAVER_CLIENT_ID`
- `NAVER_CLIENT_SECRET`

### 3. ChromeDriver 설정
ChromeDriver가 설치되어 있어야 합니다:
- 경로: `C:/chromedriver/chromedriver.exe` (기본값)
- 또는 환경 변수 `CHROME_DRIVER_PATH`로 설정

## 실행 후 확인

애플리케이션이 시작되면:
- 기본 포트: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API 엔드포인트: `http://localhost:8080/api/naver-map/...`

## 문제 해결

1. **Gradle 빌드 오류**
   - IntelliJ: File → Invalidate Caches → Invalidate and Restart
   - 명령줄: `.\gradlew.bat clean build`

2. **의존성 다운로드 실패**
   - 네트워크 연결 확인
   - Maven Central 접근 가능 여부 확인

3. **Kotlin 컴파일 오류**
   - IntelliJ: File → Project Structure → Project → SDK: Java 17 설정 확인
   - Gradle: `jvmToolchain(17)` 설정 확인

