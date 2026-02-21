<h1 align="center"> BackOffice · Commerce 운영 관리 시스템 </h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-007396?logo=openjdk" />
  <img src="https://img.shields.io/badge/SpringBoot-3.x-success?logo=springboot" />
  <img src="https://img.shields.io/badge/SpringSecurity-Auth-6DB33F?logo=springsecurity" />
  <img src="https://img.shields.io/badge/JPA-Hibernate-orange" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql" />
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED?logo=docker" />
  <img src="https://img.shields.io/badge/AWS-EC2-FF9900?logo=amazonaws" />
  <img src="https://img.shields.io/badge/CI/CD-GitHub_Actions-blue?logo=githubactions" />
</p>

<p align="center">
  <b>운영 환경의 데이터 무결성과 추적 가능성을 최우선으로 고려한 커머스 백오피스 시스템</b><br/>
  단순 기능 구현을 넘어, <b>감사 로그 추적·상태 정책·권한 분리·배포 자동화</b>까지 실무 지향적으로 설계한 프로젝트입니다.
</p>

<hr/>

<h2>프로젝트 목적 및 설계 철학</h2>

운영 시스템에서 가장 중요한 것은 **데이터 무결성과 변경 추적 가능성**입니다. 이를 달성하기 위해 다음 핵심 목표를 기반으로 아키텍처를 설계했습니다.

- 누가(Actor), 무엇을(Action), 언제 변경했는지 완벽하게 추적할 수 있는 **감사 로그(Audit) 체계** 구축
- **상태-재고 불일치 방지**를 위한 엄격한 비즈니스 정책 수립
- 관리자(ADMIN)와 실무자(MD)의 **권한에 따른 기능 및 UI 완벽 분리**
- 안정적인 서비스 운영을 위한 **도커(Docker) 기반 격리 환경 및 CI/CD 자동화 배포** 도입 (진행 중)

<hr/>

<h2>아키텍처 및 인프라 파이프라인 (진행 중)</h2>

현재 로컬 Docker 환경에서 개발을 완료하였으며, **GitHub Actions와 AWS EC2를 활용한 무중단 배포(CI/CD) 파이프라인 구축을 진행 및 테스트 중**입니다. 

- **Client:** Admin / MD (Thymeleaf, Bootstrap 5)
- **Application:** Spring Boot 3.x, Spring Security
- **Database:** MySQL 8.0, Spring Data JPA
- **Infra & DevOps:** Docker, AWS EC2, GitHub Actions (Self-hosted Runner 예정)

<br/>

> **ERD 및 API 명세서**
> 데이터베이스 ERD 상세 이미지는 [이곳(별도 링크 주소 삽입)]에서 확인하실 수 있습니다.

<hr/>

<h2>핵심 구현 및 설계 역량</h2>

<h3>1. 감사 로그(Audit Log) 기반 변경 추적 설계</h3>
- `actionType`, `targetType`, `targetId`를 분리하여 확장성 있는 로그 테이블 설계
- `actor_user_id`, 접속 IP, User-Agent를 함께 저장하여 보안 사고 대비
- 데이터 변경 전/후 필드를 **JSON 형태의 diff 데이터로 직렬화하여 저장**

<details>
<summary><b>Diff JSON 저장 구조 예시 (클릭하여 펼치기)</b></summary>
<div markdown="1">

```json
{
  "field": "price",
  "before": 10000,
  "after": 12000
}

```

</div>
</details>

<h3>2. 운영 데이터 무결성을 위한 상품 상태 정책</h3>

* `ACTIVE`, `HIDDEN`, `SOLD_OUT`, `DELETED` 4가지 상태 정의
* **재고(Stock)가 0이 되면 자동으로 `SOLD_OUT` 상태로 전환**되도록 비즈니스 로직 격리
* 상품 삭제 시 물리적 삭제가 아닌 **Soft Delete 전략** 사용 및 삭제된 데이터의 수정 원천 차단

<h3>3. 인증 사용자 ID 추출 최적화</h3>

* 감사 로그 등 사용자 정보가 필요한 로직마다 DB를 조회하는 N+1 유사 문제 식별
* `CustomUserDetails` 객체 내부에 `userId`를 캡슐화하여 포함
* `SecurityContext`의 `Principal`에서 직접 ID를 추출하도록 개선하여 **불필요한 DB 쿼리 제거 및 성능 최적화**

<hr/>

<h2>트러블 슈팅 (해결한 주요 문제)</h2>

<h4>[Issue 1] 불변 객체(Map.of) 사용 시 발생하는 NullPointerException 해결</h4>

* **문제 발생:** 감사 로그 생성 시 변경 이력을 담기 위해 `Map.of()`를 사용했으나, 특정 필드의 값이 `null`일 경우 서버 500 에러(NPE)가 발생하는 크리티컬 버그 발견
* **원인 및 해결:** `Map.of()`는 `null`을 허용하지 않는 불변 객체임을 확인. Null 안전성을 보장하고 순서를 유지하는 `LinkedHashMap` 기반의 데이터 구조로 리팩토링 진행
* **결과:** 예외 상황에서도 안정적으로 JSON 직렬화가 수행되도록 보장하여, 운영 중 발생할 수 있는 로그 유실 및 서버 장애 사전 차단

<h4>[Issue 2] 프론트엔드 동적 렌더링 시 JS Inline 변수 미치환 이슈</h4>

* **문제 발생:** 대시보드에서 권한별 화면 분리 시, Thymeleaf의 데이터가 Javascript로 정상적으로 파싱되지 않는 문제 발생
* **원인 및 해결:** Thymeleaf의 `th:inline=&quot;javascript&quot;` 문법을 정확히 적용하고, CDATA 블록을 활용하여 데이터 바인딩 구조 개선
* **결과:** 관리자(ADMIN)와 MD 간의 완벽한 뷰(View) 격리 및 동적 데이터 시각화(Chart.js) 연동 성공

<hr/>

<h2>학습 및 확장 가능성 (Next Step)</h2>

* **CI/CD 파이프라인 완성:** GitHub Actions를 활용한 AWS EC2 자동 배포 체계 마무리
* **Redis 세션 클러스터링:** 다중 서버 환경을 대비한 로그인 세션 관리 고도화 구상
* **대시보드 캐싱 전략:** 트래픽 증가 시 통계 쿼리 부하를 줄이기 위한 데이터 캐시(Cache) 도입
