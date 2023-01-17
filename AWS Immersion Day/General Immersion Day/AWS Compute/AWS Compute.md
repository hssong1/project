# AWS Compute

![image](./AWS%20Compute1.png)

AWS 컴퓨팅 서비스 : EC2, ECS/EKS/Fargate, LAMBDA
- EC2 : 가상서버 인스턴스
- ECS/EKS/Fargate : Docker 컨테이너를 대규모로 실행/관리
- LAMBDA : 코드만 올리면 실행, Serverless (서버 관리가 필요 없다는 의미)

AMI - Amazone Machine Image
* Instance를 생성하고 Image를 따면 Instance를 삭제해도 Intance처럼 사용할 수 있다. (확인 필요)

Amazone Elastic Block Store (영구 스토리지 : EBS)
Amazone Instnce Store (임시 스토리지)

AWS Savings Plan
인스턴스 유형 : 카테고리 + 기능 + 옵션 = 575+
* 카테고리, 기능, 옵션을 고려하여 유형은 엄청 많다!
* AWS는 그 중 제일 합리적인 비용 계획을 제시

Elastic Load Balancing : 트래픽에 따라 자동 조정
* Application Load Balancer
* Network Load Balancer

실습 결과
![image](./AWS%20Compute2.png)