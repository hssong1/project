# Container

어플리케이션 주요 구성요소 : Runtime Engine, Code, Dependencies, Configuration

VM vs Container
VM : Hypervisor + OS
- 운영/관리 측면에서 힘듦
- Snowflake 현상 : 건드리면 눈 녹듯이 녹는 현상을 빗대어 운영 관리가 그만큼 어렵다는 뜻

Container : Container Runtime

|VM|Container|
|---|---|
|Hypervisor + OS|Container Runtime|

VM의 단점
- 비교적 운영/관리 측면에서 힘듦
- Snowflake 현상 : 건드리면 눈 녹듯이 녹는 현상을 빗대어 운영 관리가 그만큼 어렵다는 뜻

프로세스 격리 기술
- chroot
- namespace
- cgroup

Container 중 가장 상용화 되어있는 Docker

Docker 
- 애플리케이션을 신속하게 구축, 테스트 및 배포
- 컨테이너라는 표준화된 유닛으로 패키징

명령형 vs 선언형
명령형 : 한 줄 한 줄 명령어를 입력하여 실행
선언형 : yaml 파일을 만들어 바로 실행 (추천)

선언형 패키징 순서 : Container File을 빌드하여 이미지 생성 → 이미지를 Container Registry에 push → Container Run

컨테이너 설계할 때 고려해야할 점
- 이미지 최적화 (Base Image 경량화, Docker Ignore 활용, Layer 최소화, 빌드캐시 최적화)
- 이미지 버전 관리
- 이미지 취약점 스캔
- 배포 자동화
- 불변성 유지
- 로깅 및 모니터링 메커니즘 설계




