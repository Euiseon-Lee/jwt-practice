## 웹의 발전 과정

#### 1세대: MPA (Multi Page Application) = SSR (Server-Side Rendering)
페이지를 이동할 때마다 서버에서 HTML 전체를 새로 받아옴 (서버가 HTML을 완성해서 전송).
페이지 일부만 갱신하지 못하고 매번 전체 HTML을 새로 받아옴.
- 페이지 전환 시 깜빡임 발생
- 전체 페이지를 매번 다시 로드하기 때문에 서버 부하가 증가
- 대표 기술: JSP, Thymeleaf, PHP, ERB

#### 2세대: AJAX 도입 (MPA 개선)
AJAX (Asynchronous JavaScript And XML, 현재는 거의 JSON 사용)
페이지 전체를 새로 받지 않고 필요한 데이터만 서버에 요청, JS로 해당 영역만 업데이트.
AJAX로 받은 JSON은 **JS가 화면(DOM)으로 만들어 붙이는 부분 CSR** 패턴.  
즉, 페이지의 **뼈대는 서버(SSR)**, **동적 영역만 브라우저(CSR)** 가 담당.

```
[브라우저가 받은 첫 응답 — SSR로 받은 HTML]
┌──────────────────────────────────────┐
│  <헤더>                               │  ← JSP/Thymeleaf로 서버에서 그림 (SSR)
│  <사이드바>                           │  ← 서버에서 그림 (SSR)
│  <메인 콘텐츠 영역>                    │
│   <div id="user-table"></div>        │  ← 빈 껍데기 (서버는 비워둠)
│  </메인>                              │
│  <푸터>                               │  ← 서버에서 그림 (SSR)
└──────────────────────────────────────┘

[페이지 로드 후 JS가 동작]
$.get('/api/users') → JSON 받음 → JS가 #user-table 안에 <tr>들 만들어 채움 (CSR)
```

- MPA 구조는 그대로 유지하면서 부분 갱신만 추가
- AJAX 코드가 복잡해지고 페이지마다 중복 코드 발생 가능성 증가
- (참고) AJAX는 SPA에서도 그대로 사용됨 — 기술이지 아키텍처가 아님

#### 3세대: SPA (Single Page Application) = CSR (Client-Side Rendering)
처음에 HTML/CSS/JS 전체를 한 번만 로드하고, 이후 페이지 전환 시 서버에 데이터(JSON)만 요청하여 JS가 화면을 동적으로 렌더링.
- 페이지 전환이 빠르고 깜빡임 없음
- 서버는 데이터(API)만 제공 (서버는 HTML 생성 책임이 없음) → 프론트/백엔드 완전 분리
- 같은 백엔드 API를 웹/앱/외부 파트너가 공유 가능
- 대표 기술: React, Vue, Angular
- 약점: 초기 로딩 느림, SEO 불리, JS 꺼지면 빈 화면

#### 4세대: SSR + CSR 하이브리드
SPA의 약점을 보완하기 위해 첫 응답은 SSR, 이후 동작은 SPA 방식.
- 첫 화면이 빠르고 SEO에도 강함
- 이후 페이지 전환은 SPA처럼 부드러움
- 대표 기술: Next.js (React), Nuxt.js (Vue)
- SSG (Static Site Generation): 빌드 시점에 HTML을 미리 생성 (블로그 등)

JSP와 비교해서 무엇이 분리되는지:

|관점|JSP (MPA)|SPA|
|---|---|---|
|**저장소(repo)**|보통 한 곳 (백엔드 프로젝트 안에 .jsp)|보통 분리 (`backend-repo` / `frontend-repo`)|
|**배포**|한 번에 같이 (war 파일에 묶임)|분리 배포 (백엔드: WAS / 프론트: S3·Nginx·CDN)|
|**빌드**|`gradle build` 한 번|`gradle build` + `npm run build` 두 번|
|**언어**|Java + JSP/HTML 혼합|백엔드 Java만, 프론트 JS/TS만|
|**팀**|풀스택 (한 사람이 다 함)|백엔드 팀 / 프론트 팀 분리|
|**백엔드 개발자가 아는 것**|HTML, CSS, JSP 태그까지|**API 스펙(JSON 모양)만**|
|**프론트 개발자가 아는 것**|거의 없음 (백엔드가 다 함)|API 호출, UI/UX, 상태관리|