## 컴포넌트 Basic

### 5.1 컴포넌트란?
- View, Data, Code의 세트
- 재사용이 가능
- views : 페이지라고 부르는 화면 하나하나에 해당하는 vue 컴포넌트파일 생성
- components : 다른 vue 파일에서 호출해서 공통으로 사용할 수 있는 vue 컴포넌트 파일 생성 & 관리

### 5.2 컴포넌트 구조 이해하기
- 5.2.1 컴포넌트 기본 구조
    - 많이 쓰이는 구조가 있는데 그것을 Snippet에 등록하여 편하게 쓰기
- 5.2.2 Snippet 설정
```
"Generate Basic Vue Code": {
		 	"prefix": "vue-start",
		 	"body": [
		 		"<template>\n<div></div>\n</template>\n<script>\nexport default{ \n\tname:'',\n\tcomponents:{},\n\tdata(){\n\t\treturn{\n\t\t\tsampleData:''\n\t\t};\n\t},\n\tsetup(){},\n\tcreated(){},\n\tmounted(){},\n\tunmounted(){},\n\tmethods:{}\n}\n</script>"
		 	],
		 	"description": "Generate Basic Vue Code"
	}
```
### 5.3 데이터 바인딩
- Vue는 양방향 데이터 바인딩(Two-way data binding)을 지원
- Angular는 양방향 데이터 바인딩, React는 단방향 데이터 바인딩 지원
- 5.3.1 문자열 데이터 바인딩 (실습 : vue-project)
    - ![image](./5.3.1%20%EB%AC%B8%EC%9E%90%EC%97%B4%20%EB%8D%B0%EC%9D%B4%ED%84%B0%20%EB%B0%94%EC%9D%B8%EB%94%A9.png)
    - Hello, {{title}}!로 title에 텍스트 World를 넣어 바인딩

- 5.3.2 raw(원시) HTML 데이터 바인딩 (실습 : vue-project)
    -![image](./5.3.2%20raw(%EC%9B%90%EC%8B%9C)%20HTML%20%EB%8D%B0%EC%9D%B4%ED%84%B0%20%EB%B0%94%EC%9D%B8%EB%94%A9.png)
    - HTML문법까지 포함하여 출력하는 데이터 바인딩

- 5.3.3 Form 입력 데이터 바인딩
    - 5.3.3.1 Input type=text
    - ![image](./5.3.3.1%20Form%20%EC%9E%85%EB%A0%A5%20%EB%8D%B0%EC%9D%B4%ED%84%B0%20%EB%B0%94%EC%9D%B8%EB%94%A9%20Input%20type%3Dtext.png)
    
    

