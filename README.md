<h1 align="center"> BackOffice · Commerce 운영 관리 시스템 </h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-007396?logo=openjdk" />
  <img src="https://img.shields.io/badge/SpringBoot-3.2-success?logo=springboot" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql" />
  <img src="https://img.shields.io/badge/SpringSecurity-JWT-6DB33F?logo=springsecurity" />
  <img src="https://img.shields.io/badge/Thymeleaf-Template-005F0F?logo=thymeleaf" />
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED?logo=docker" />
</p>

<p align="center">
  <b>관리자(Admin) · MD 전용 백오피스 시스템</b><br/>
  상품·카테고리·사용자·감사로그를 통합 관리하고,<br/>
  실무 운영을 고려한 감사 추적(Audit)과 대시보드 지표를 설계·구현한 프로젝트입니다.
</p>

<hr/>

<h2>프로젝트 개요</h2>
<ul>
  <li>Spring Boot 기반 <b>백오피스 운영 관리 시스템</b></li>
  <li>Admin / MD 역할 분리 및 권한 제어</li>
  <li>상품·카테고리·사용자 관리 + 감사로그(Audit Log) 추적</li>
  <li>운영 지표 기반 <b>대시보드 시각화</b> 제공</li>
</ul>

<hr/>

<h2>핵심 설계 포인트</h2>

<h3>1️⃣ 도메인 중심 패키지 구조</h3>
<ul>
  <li><b>product / category / user / audit / dashboard</b> 도메인 분리</li>
  <li>Controller → Service → Repository 계층 명확 분리</li>
  <li>관리자 전용 BaseAdminController 공통화</li>
</ul>

<h3>2️⃣ 감사 로그(Audit Log) 시스템</h3>
<ul>
  <li>로그인 성공/실패 추적</li>
  <li>상품 생성/수정/삭제/상태변경 이력 기록</li>
  <li>변경 필드 diff JSON 저장</li>
  <li>행위자(actor_user_id), IP, User-Agent 기록</li>
</ul>

<b>예시 diff 구조</b>
<pre>
{
  "field": "price",
  "before": 10000,
  "after": 12000
}
</pre>

<ul>
  <li>Immutable Map.of() → NullPointerException 문제 해결</li>
  <li>LinkedHashMap 기반 안전한 JSON 직렬화 구조 설계</li>
</ul>

<h3>3️⃣ 상품 상태 정책 설계</h3>
<ul>
  <li>ACTIVE / HIDDEN / SOLD_OUT / DELETED</li>
  <li>재고(stock) 기반 자동 상태 보정 로직</li>
  <li>DELETED는 soft delete 정책</li>
</ul>

<hr/>

<h2>대시보드 기능</h2>

<ul>
  <li>전체 상품 수 / 판매중 / 판매중지 / 품절 / 삭제 KPI 표시</li>
  <li>최근 7일 감사 이벤트 추이 (Line Chart)</li>
  <li>Top Action Type 집계 (Bar Chart)</li>
  <li>최근 활동 20건 테이블</li>
  <li>재고 임계치 이하 상품 알림</li>
</ul>

<b>권한별 UI 분리</b>
<ul>
  <li>ADMIN → 감사로그/차트/최근활동 전체 표시</li>
  <li>MD → 상품 KPI + 재고 알림만 표시</li>
</ul>

<hr/>

<h2>인증 · 보안 구조</h2>

<ul>
  <li>Spring Security 기반 인증</li>
  <li>세션 기반 Web 체인 + JWT 기반 API 체인 분리</li>
  <li>CustomUserDetails 구현 → user id를 SecurityContext에 포함</li>
  <li>CSRF 보호 유지</li>
</ul>

<hr/>

<h2>기술 스택</h2>

<ul>
  <li><b>Backend:</b> Java 21, Spring Boot 3, Spring Security, JPA</li>
  <li><b>Database:</b> MySQL 8</li>
  <li><b>Frontend:</b> Thymeleaf, Bootstrap 5, Chart.js</li>
  <li><b>Infra:</b> Docker (개발 환경)</li>
</ul>

<hr/>

<h2>아키텍처 구조</h2>

<pre>
Client (Admin / MD)
        ↓
Spring Security
        ↓
Controller Layer
        ↓
Service Layer
        ↓
JPA Repository
        ↓
MySQL
</pre>

<hr/>

<h2>주요 구현 경험</h2>

<ul>
  <li>감사로그 설계 시 actionType / targetType 분리 전략 수립</li>
  <li>diff JSON 구조 설계 및 변경 필드 추적 로직 구현</li>
  <li>Null 안전성 개선 (Map.of → LinkedHashMap)</li>
  <li>CustomUserDetails를 통한 인증 사용자 ID 최적화</li>
  <li>대시보드 비동기 API 분리 및 프론트 fetch 병렬 처리</li>
  <li>권한 기반 UI 조건 렌더링 (Thymeleaf th:if + JS inline)</li>
</ul>

<hr/>

<h2>향후 고도화 계획</h2>

<ul>
  <li>Redis 기반 로그인 실패 카운트 분리</li>
  <li>감사로그 검색 필터 고도화 (기간/행위자/액션별)</li>
  <li>대시보드 캐싱 전략 도입</li>
  <li>상품 매출 통계 모듈 확장</li>
</ul>

<hr/>

<h2>한 줄 정리</h2>

<p>
  <b>
    실무 운영을 가정한 백오피스 시스템으로,
    상품·권한·감사 로그·대시보드까지
    운영 중심 설계와 보안 구조를 직접 설계·구현한 프로젝트입니다.
  </b>
</p>
