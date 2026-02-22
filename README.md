<h1 align="center"> BackOffice · Commerce 운영 관리 시스템 </h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-007396?logo=openjdk" />
  <img src="https://img.shields.io/badge/SpringBoot-3.x-success?logo=springboot" />
  <img src="https://img.shields.io/badge/SpringSecurity-Auth-6DB33F?logo=springsecurity" />
  <img src="https://img.shields.io/badge/JPA-Hibernate-orange" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql" />
  <img src="https://img.shields.io/badge/Docker-Containerized-2496ED?logo=docker" />
  <img src="https://img.shields.io/badge/AWS-EC2-FF9900?logo=amazonaws" />
  <img src="https://img.shields.io/badge/Nginx-009639?logo=nginx" />
  <img src="https://img.shields.io/badge/Cloudflare-F38020?logo=cloudflare" />
  <img src="https://img.shields.io/badge/CI/CD-GitHub_Actions-blue?logo=githubactions" />
</p>

<p align="center">
  <b>운영 환경의 데이터 무결성과 추적 가능성을 최우선으로 고려한 커머스 백오피스 시스템</b><br/>
  단순 기능 구현을 넘어, <b>감사 로그 추적·상태 정책·권한 분리·배포 자동화 및 보안 인프라 구축</b>까지 실무 지향적으로 설계한 프로젝트입니다.
</p>

<p align="center">
  <b>개발 기간:</b> 2026.01.28 ~ 2026.02.23
</p>

<div align="center">
  <h3>🔗 Live Demo</h3>
  <a href="https://shop.rladntjd85.site">Commerce 운영 관리 시스템 바로가기</a><br/>
  <sub>* 관련 프로젝트 (ERP/MES): <a href="https://rladntjd85.site">https://rladntjd85.site</a></sub>
</div>

<hr/>

## 1. 프로젝트 목적 및 설계 철학
운영 시스템에서 가장 중요한 것은 **데이터 무결성과 변경 추적 가능성**입니다.

- **감사 로그(Audit) 체계**: 누가, 무엇을, 언제 변경했는지 완벽하게 추적
- **상태-재고 정책**: 재고 불일치 방지를 위한 엄격한 비즈니스 로직 적용
- **권한 분리**: ADMIN과 MD의 기능 및 UI 접근 권한 완벽 격리
- **인프라 자동화**: 도커(Docker) 기반 격리 환경 및 CI/CD 자동화 배포 완료

<hr/>

## 2. 아키텍처 및 인프라
GitHub Actions와 AWS EC2를 활용한 무중단 배포(CI/CD) 파이프라인을 구축하였으며, Nginx와 Cloudflare를 연동하여 보안성과 가용성을 확보했습니다.

- **Client:** Admin / MD (Thymeleaf, Bootstrap 5)
- **Application:** Spring Boot 3.x, Spring Security
- **Database:** MySQL 8.0, Spring Data JPA
- **Infra & DevOps:** Docker, AWS EC2, GitHub Actions, Nginx, Cloudflare

<hr/>

## 3. 핵심 구현 및 설계 역량

### 감사 로그(Audit Log) 기반 변경 추적
- `actionType`, `targetType`, `targetId` 분리로 확장성 확보
- 접속 IP 및 User-Agent 정보를 함께 저장하여 보안 사고 대비
- 데이터 변경 전/후 필드를 **JSON diff 데이터로 직렬화하여 저장**

### 운영 데이터 무결성을 위한 상품 상태 정책
- `ACTIVE`, `HIDDEN`, `SOLD_OUT`, `DELETED` 상태 정의
- **재고 0 도달 시 자동으로 SOLD_OUT 전환** 로직 격리
- 삭제 데이터 복구 및 추적을 위한 **Soft Delete 전략** 적용

<hr/>

## 4. 트러블 슈팅

### [Issue 1] EC2 환경 배포 중 메모리 부족(OOM) 해결
- **문제:** 프리티어(RAM 1GB) 환경에서 빌드 및 다중 컨테이너 구동 시 프로세스 강제 종료 발생
- **해결:** EBS 디스크 공간을 활용한 **Swap 메모리(2GB) 할당**으로 가용 메모리 확보
- **결과:** 인스턴스 사양 업그레이드 없이 안정적인 서버 인프라 유지

### [Issue 2] Cloudflare Flexible SSL 환경 무한 리다이렉트 해결
- **문제:** HTTPS 접속 시 `ERR_TOO_MANY_REDIRECTS` 오류 발생
- **원인:** Cloudflare-Nginx 간 통신 프로토콜 충돌(HTTP/HTTPS)에 따른 무한 루프 확인
- **해결:** Nginx 설정 내 HTTPS 강제 리다이렉트 제거 및 `X-Forwarded-Proto` 헤더 명시
- **결과:** 정상적인 HTTPS 통신 및 도메인 기반 라우팅 성공

<hr/>

## 5. Next Step
- **데이터베이스 백업 자동화**: crontab 및 S3 연동 정기 백업 파이프라인 구축
- **대시보드 캐싱**: 통계 쿼리 부하 절감을 위한 Redis 캐시 도입