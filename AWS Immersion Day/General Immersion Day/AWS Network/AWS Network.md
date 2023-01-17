# AWS Network (VPC)

VPC : 가상의 데이터 센터 (Virtual Private Cloud)
![image](./AWS%20Network1.png)

VM을 띄우기 위해서는 서브넷이 필요
* 서브넷 : 네트워크의 일부를 구성하는 망

필요 설정 : IP 범위, Subnet, Route Table, Network ACL, Gateway

인터넷과 연결하기 위한 설정 : 공인IP, 인터넷 게이트웨이, 라우팅테이블, 방화벽

NAT Gateway ???

Network ACL - 서브넷 방화벽, Stateless, In & Out 모두 설정해주어야한다.

보안그룹 - 인스턴스 방화벽, Statefull, 가용영역이 떨어져 있어도 묶을 수 있다.
* Web Server 보안그룹 Id를 Application Server 보안그룹 Id와 연결하여 Web에서 허용된 것만 Application Server에 접근할 수 있도록 할 수 있다.

VPC 간 연결방법 - Peering이라는 기술로 Request를 던지면 수락하여 연결한다.
* VPC가 많아지면 n(n-1)/2개의 Peering 연결이 필요하다.

AWS Trnsit Gateway - VPC가 많을 때 관리하기 위하여 중앙 연결장치 같은 것


실습 결과
![image](./AWS%20Network2.png)
