<h1 align="center"> BackOffice · Commerce 운영 관리 시스템 </h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-007396?logo=openjdk" />
  <img src="https://img.shields.io/badge/SpringBoot-3.x-success?logo=springboot" />
  <img src="https://img.shields.io/badge/SpringSecurity-Auth-6DB33F?logo=springsecurity" />
  <img src="https://img.shields.io/badge/JPA-Hibernate-orange" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql" />
  <img src="https://img.shields.io/badge/Docker-2496ED?logo=docker" />
  <img src="https://img.shields.io/badge/AWS-EC2-FF9900?logo=amazonaws" />
  <img src="https://img.shields.io/badge/Nginx-009639?logo=nginx" />
  <img src="https://img.shields.io/badge/Cloudflare-F38020?logo=cloudflare" />
  <img src="https://img.shields.io/badge/CI/CD-GitHub_Actions-blue?logo=githubactions" />
</p>

<p align="center">
  <b>운영 환경의 데이터 무결성과 추적 가능성을 최우선으로 고려한 커머스 백오피스 시스템</b><br/>
  단순 기능 구현을 넘어, <b>감사 로그 추적·상태 정책·권한 분리·배포 자동화 및 보안 인프라 구축</b>까지 실무 지향적으로 설계한 프로젝트입니다.
</p>

<hr/>

<h2>1. 프로젝트 개요</h2>

<ul>
  <li><b>개발 기간:</b> 2026.01.28 ~ 2026.02.23</li>
  <li><b>Live Demo:</b> <a href="https://shop.rladntjd85.site">Commerce 운영 관리 시스템 바로가기</a></li>
  <li><b>테스트 계정 (ADMIN):</b> admin@metree.co.kr / MetreeMaster123! (전체 권한)</li>
  <li><b>테스트 계정 (MD):</b> md_lee@metree.co.kr / MetreeMaster123! (상품/카테고리 실무)</li>
</ul>

<hr/>

<h2>2. 아키텍처 및 기술 스택</h2>

<ul>
  <li><b>Backend:</b> Java 21, Spring Boot 3.x, Spring Data JPA</li>
  <li><b>Security:</b> Spring Security (RBAC 기반 권한 제어)</li>
  <li><b>Database:</b> MySQL 8.0</li>
  <li><b>Infra & DevOps:</b> AWS EC2, Docker, Nginx, Cloudflare, GitHub Actions</li>
</ul>

<hr/>

<h2>3. 핵심 구현 및 설계 역량</h2>

<h3>1. 역할 기반 접근 제어 (RBAC)</h3>

<ul>
  <li><b>ADMIN (전체 관리자):</b> 시스템의 모든 제어권을 보유하며 회원 관리, 권한 설정, 전체 감사 로그 조회를 수행</li>
  <li><b>MD (운영 관리자):</b> 상품 등록 및 카테고리 관리 등 운영 실무 권한만 부여, 보안 및 설정 영역 접근 차단</li>
</ul>

<h3>2. 데이터 무결성 및 감사 로그</h3>

<ul>
  <li><b>감사 로그 (Audit Log):</b> 데이터 변경 전/후 상태를 JSON diff 형식으로 직렬화하여 완벽한 변경 추적성 확보</li>
  <li><b>상품 상태 정책:</b> 재고 0 도달 시 자동으로 SOLD_OUT 전환 로직 격리 및 Soft Delete 전략 적용</li>
</ul>

<hr/>

<h2>4. 해결한 주요 문제 (Trouble Shooting)</h2>

<h3>1. EC2 프리티어 메모리 부족 (OOM) 해결</h3>

<ul>
  <li><b>문제:</b> RAM 1GB 환경에서 빌드 시 프로세스 강제 종료 발생</li>
  <li><b>해결:</b> EBS 디스크 공간을 활용한 2GB Swap 메모리 할당으로 가용 메모리 확보</li>
  <li><b>결과:</b> 인스턴스 사양 업그레이드 없이 안정적인 서버 인프라 유지</li>
</ul>

<h3>2. Cloudflare SSL 무한 리다이렉트 해결</h3>

<ul>
  <li><b>문제:</b> HTTPS 접속 시 ERR_TOO_MANY_REDIRECTS 오류 발생</li>
  <li><b>원인:</b> Cloudflare(Flexible SSL)와 Nginx 간 통신 프로토콜 충돌</li>
  <li><b>해결:</b> Nginx 내 HTTPS 강제 리다이렉트 제거 및 X-Forwarded-Proto 헤더 명시</li>
  <li><b>결과:</b> 정상적인 HTTPS 통신 및 도메인 기반 라우팅 성공</li>
</ul>

<hr/>

<h2>5. 확장 가능성 (Next Step)</h2>

<ul>
  <li><b>결제 시스템 통합:</b> PG사 연동을 통한 결제/취소 및 재고 트랜잭션 보장 로직 구현</li>
  <li><b>데이터베이스 백업 자동화:</b> S3 연동 정기 백업 파이프라인 구축</li>
  <li><b>대시보드 성능 최적화:</b> Redis 캐시 도입을 통한 통계 쿼리 부하 절감</li>
</ul>
