# TODO List

JWT 학습 프로젝트의 향후 작업 목록.

> 마지막 업데이트: 2026-04-27

---

## 🔧 카테고리 A — 백엔드 보강 (보안 / 완성도)

### A-1. AuthenticationEntryPoint / AccessDeniedHandler 도입
- [ ] `JwtAuthenticationFilter`에서 발생하는 401·403 응답을 `ErrorResponse` 포맷으로 통일
- [ ] `AuthenticationEntryPoint` 구현 (인증 실패 = 401)
- [ ] `AccessDeniedHandler` 구현 (권한 부족 = 403)
- [ ] `SecurityConfig`에 등록

**배경**
현재 `ControllerExceptionHandler`는 `@RestControllerAdvice`로 동작하지만, **필터단(DispatcherServlet 앞)에서 발생한 예외는 잡지 못함**. 만료된 AT로 보호 API를 호출하면 빈 응답이 오거나 기본 스프링 보안 응답이 가서 응답 포맷이 들쭉날쭉.

**난이도**: 중

---

### A-2. Refresh Token Rotation
- [ ] `/reissue` 호출 시 새 AT뿐 아니라 **새 RT**도 발급
- [ ] 기존 RT는 DB에서 삭제 (또는 무효화 플래그)
- [ ] 새 RT를 HttpOnly Cookie로 응답에 실어 보냄

**배경**
보안 표준은 **RT 사용 시마다 새 RT를 발급하고 기존 RT는 폐기**. RT 탈취 감지에 강해짐 (재사용 탐지 가능).

**난이도**: 중

---

### A-3. `@Valid` 검증 추가
- [ ] `LoginRequest` 필드에 `@NotBlank`, `@Size` 등 제약 추가
- [ ] `SignupRequest` 필드에 동일 처리
- [ ] 컨트롤러 파라미터에 `@Valid` 추가
- [ ] `build.gradle`에 `spring-boot-starter-validation` 의존성 확인

**배경**
이미 `ControllerExceptionHandler`에 `MethodArgumentNotValidException` 핸들러를 만들어두었으나, 활용 시점이 없음. DTO에 제약 어노테이션을 붙이면 즉시 활성화.

**난이도**: 하

---

### A-4. 회원가입 엔드포인트 E2E 검증
- [ ] `POST /auth/signup` → DB insert 확인
- [ ] 신규 계정으로 로그인 → 토큰 발급 확인
- [ ] 중복 loginId 시 `409 AUTH-409` 응답 확인

**배경**
코드는 작성되었지만 실제 흐름이 통째로 검증된 적 없음.

**난이도**: 하

---

## 🎨 카테고리 B — 프론트엔드 구현

### B-1. `main.js` 모듈 스코프 AT 저장
- [ ] 모듈 스코프 변수에 AT 저장
- [ ] 페이지 로드 시 자동으로 `/auth/reissue` 호출하여 AT 재발급
- [ ] 모든 페이지에서 `main.js` 임포트

**배경**
이전 세션에서 합의된 MPA + JS 변수 전략. 페이지 진입마다 RT(쿠키)로 AT를 재발급받는 흐름.

**난이도**: 하

---

### B-2. fetch/axios interceptor — 401 시 자동 재발급
- [ ] API 호출 응답이 401이면 `/auth/reissue` 자동 호출
- [ ] 새 AT로 원 요청 재시도
- [ ] `/reissue`도 실패하면 AT 변수 초기화 → `/auth/logout` 호출 → 로그인 페이지로 이동
- [ ] 동시에 여러 401이 나는 경우(race condition) 처리

**배경**
사용자가 정리한 인증 흐름의 핵심.

**난이도**: 중

---

### B-3. 로그인 / 로그아웃 / 보호 API 호출 UI
- [ ] 로그인 폼 페이지
- [ ] 로그아웃 버튼
- [ ] 보호 API 호출 테스트 버튼

**배경**
흐름을 실제로 눈으로 확인할 최소 UI.

**난이도**: 하

---

### B-4. (옵션) Service Worker 도입
- [ ] Service Worker 등록
- [ ] fetch 가로채기 → Authorization 헤더 자동 첨부
- [ ] AT 만료 감지 → 자동 재발급

**배경**
fetch interceptor 패턴을 더 깔끔하게 만드는 대안. 학습 비용 있음.

**난이도**: 상

---

## 📚 카테고리 C — 문서화

### C-1. `docs/auth-flow.md` 작성
- [ ] MPA vs SPA 개념 정리 (이번 세션 학습 내용)
- [ ] 저장 위치 / 전송 방법 / 서버 검증 표
- [ ] 3단계 인증 흐름 (로그인 / API 호출 / 재발급)
- [ ] 참고: SPA였다면 어떻게 달라지는가

**배경**
사용자가 직접 정리한 인증 흐름과 학습 내용을 문서로 박제.

**난이도**: 하

---

### C-2. `README.md` 보강
- [ ] 프로젝트 소개
- [ ] 빌드 / 실행 방법
- [ ] `application-local.yaml`에 필요한 키 안내
- [ ] API 엔드포인트 요약

**난이도**: 하

---

### C-3. (선택) `docs/error-codes.md`
- [ ] `AuthErrorCode`, `CommonErrorCode` 전체 카탈로그
- [ ] 각 코드별 발생 조건 + HTTP 상태 + 응답 예시

**배경**
프론트/모바일 클라이언트와의 응답 계약서. 클라이언트 측 분기 로직 작성 시 참조.

**난이도**: 하

---

## 🛠 카테고리 D — 운영 / 정리

### D-1. `application-local.yaml` 가이드
- [ ] 필요한 키 목록 정리 (JWT secret, DB 접속 정보 등)
- [ ] 예시 값 또는 생성 방법 안내
- [ ] README 또는 별도 docs 파일에 포함

**난이도**: 하

---

### D-2. `.gitignore` 재점검
- [ ] secret 포함 파일 누출 위험 재확인
- [ ] 신규 추가된 파일들 검토

**난이도**: 하

---

## 🎯 추천 진행 순서

```
A-1 (필터단 예외 통일)
   ↓  흐름의 마지막 구멍을 막음. 응답 계약이 100% 일관됨.
A-3 (@Valid 검증)
   ↓  이미 핸들러는 만들어둔 상태라 추가 비용 거의 0.
A-2 (RT Rotation)
   ↓  보안 표준 적용. 백엔드 인증 부분의 완성도 마무리.
A-4 (회원가입 E2E)
   ↓  백엔드 검증의 마무리.
─── 백엔드 안정화 완료 ───
C-1 (docs/auth-flow.md)
   ↓  백엔드가 완성된 시점에 문서화하면 내용이 정확해짐.
B-3 → B-1 → B-2 (프론트엔드 구현)
   ↓  단순한 UI 먼저 → AT 저장 → interceptor.
─── 시연 가능한 시점 ───
B-4 (Service Worker, 옵션)
C-2, C-3, D-1, D-2 (문서/운영 마무리)
```

### 우선순위 근거

| 단계 | 이유 |
|---|---|
| **A-1을 가장 먼저** | 프론트엔드 작성 전에 응답 계약을 통일해두지 않으면, interceptor 로직을 두 번 짜게 됨. |
| **A-3은 빠르게** | 이미 만든 `MethodArgumentNotValidException` 핸들러를 활용할 뿐. 30분 작업. |
| **A-2는 보안 가치 큼** | 클라이언트 코드 수정 불필요라 백엔드 단계에 포함 가능. |
| **C-1은 백엔드 확정 후** | 흐름이 굳은 다음에 적어야 다시 안 고침. |
| **B는 백엔드 계약이 굳은 다음** | interceptor 로직은 백엔드 응답 포맷에 직접 의존. |

---

## ✅ 최근 완료 (참고)

- 2026-04 — `GlobalExceptionHandler` 도입 + `BusinessException` / `ErrorResponse` / `ErrorCode` 체계 정립
- 2026-04 — `ErrorCode`를 interface로 분리하고 `AuthErrorCode` / `CommonErrorCode`로 도메인별 분산
- 2026-04 — Swagger UI에서 쿠키 파라미터 입력칸 숨김 처리
- 2026-04 — 중복 로그인 방지 (신규 RT 저장 전 기존 RT 삭제)
- 2026-04 — 회원가입 엔드포인트 추가
- 2026-04 — MyBatis `parameterType` 정합성 수정 (ClassCastException)
- 2026-04 — `.gitignore` 보강 (`application-local.yaml`)