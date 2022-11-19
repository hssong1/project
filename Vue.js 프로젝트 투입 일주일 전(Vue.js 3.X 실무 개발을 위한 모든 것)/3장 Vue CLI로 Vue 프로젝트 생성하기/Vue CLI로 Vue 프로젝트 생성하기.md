## Vue CLI로 Vue 프로젝트 생성하기

### 3.1 Vue CLI 설치하기
- Vue CLI
    - Vue 프로젝트를 빠르게 구성하고, 빌드, 디플로이할 수 있게 도와주는 도구
    - npm install -g @vue/cli
        - -g(global) : -g 옵션을 사용하면 패키지가 현재 디렉토리 뿐만 아니라 앞으로 생성하게 되는 모든 프로젝트에서 사용할 수 있는 global 패키지로 등록
        - --save : 현재 작업 중인 디렉토리 내에 있는 ./node_modules에 패키지를 설치, package.json 파일에 패키지 정보가 추가됨. package.json 파일을 팀원들에게 공유하면 명령어 npm install을 입력하여 패키지 전체를 한번에 설치 가능

### 3.2 Default 옵션으로 프로젝트 설치하기
- vue create vue-project (vue 프로젝트 만들기)
- npm run serve (서버 동작)
    - Multiple assets emit different content to the same filename index.html 오류 - vue-project > public > index.html 파일을 index2.html로 이름을 변경하니 해결
- Vue 프로젝트 파일 구조
    - node_modules : npm으로 설치된 패키지 파일들이 모여있는 디렉토리
    - public : 웹팩을 통해 관리되지 않는 정적 리소스가 모여있는 디렉토리
    - src/assets : 이미지, css, 폰트 등을 관리하는 디렉토리
    - src/components : Vue 컴포넌트 파일이 모여있는 디렉토리
    - App.vue : 최상위(Root) 컴포넌트
    - main.js : 가장 먼저 실행되는 자바스크립트 파일로써 Vue 인스턴스를 생성하는 역할
    - .gitignore : 깃허브에 업로드할 때 제외할 파일 설정
    - babel.congif.js : 바벨 설정파일
    - package-loca.json : 설치된 package의 dependency 정보를 관리하는 파일
    - package.json : 프로젝트에 필요한 package를 정의하고 관리하는 파일
    - README.md : 프로젝트 정보를 기록하는 파일

### 3.3 Manually select features 옵션으로 프로젝트 설치하기
- vue create vue-project-manually
- 설정
    1. Manullay select features
    2. Babel, Router, Vuex, Linter/Formatter
    3. Vue version : 3.X
    4. Vue-Router history mode : y
    5. ESLint + Standart config <코딩 규칙을 위해 사용
    6. Lint on save
    7. In package.json <앞서 선택한 features 설정 옵션을 어디에 만들지 지정
    8. 향후 프로젝트를 만들 때 앞서 선택한 옵션과 동일하게 프로젝트를 생성할 수 있도록 preset을 저장하는 옵션 : y
    9. preset : vue basic
- preset을 사용하여 프로젝트 생성 (vue-preset)
- Vue 프로젝트 파일 구조
    - router, store 폴더가 추가 생성

### 3.4 Vue 프로젝트 매니저로 프로젝트 설치
- vue ui (프로젝트 매니저 실행 명령어)
- cmd에서 선택한 것을 GUI로 쉽게 프로젝트 생성
- 개발 메뉴
    - 대시보드
        - 프로젝트 모니터링 or 관리
    - 플러그인
        - 설치되어있는 플러그인 목록 확인
        - 플러그인 검색 & 설치
    - 의존성
        - 개발에 사용하고 있는 플러그인의 버전과 정보 확인
    - 설정
        - 프로젝트 생성 시 선택했던 features 기능의 config 파일 관리
    - 작업목록
        - Vue 프로젝트를 실행하고 빌드하는 작업을 수행