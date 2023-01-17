# AWS Database

![image](./AWS%20Database1.png)

DB의 진화과정
Mainframe → Client-Server → 3 tier → Microservices

AWS는 DB를 스키마 설계, 쿼리 생성, 쿼리 최적화를 제외한 나머지를 관리함 (ex ) 패치)

최근에 DB의 종류가 여러가지로 나뉘었기 때문에 목적에 맞게 8개의 유형 중 하나를 선택

DB 백업
- 원본을 1개의 가용영역에 두고 다른 가용영역에 복제하여 사용
- 원본은 Writer의 권한을 갖지만 복제된 DB는 Reader의 권한을 갖음 → 용도에 따라 트래픽이 나눠서 사용할 수 있음 (이는 스프링에서도 어노테이션을 통해 사용 가능)

Amazone Aurora
- 자동으로 용량이 증가 (따로 공간 신경 쓸 필요 X)

SCT (이기종 간 스키마 변형 도구)
- Schema Conversion Tool
- 전송 Oracle, 수신 MySQL일 때 자동으로 변환

실습 결과
![image](./AWS%20Database2.png)

실습할 때, Secrets Manager를 사용하면 코드에 DB의 정보를 넣어두지 않아도 라이브러리를 통해 DB에 접근이 가능했다. < 이런건 좋은 듯!? AWS 시스템을 사용하지 않아도 방법이 있을까?