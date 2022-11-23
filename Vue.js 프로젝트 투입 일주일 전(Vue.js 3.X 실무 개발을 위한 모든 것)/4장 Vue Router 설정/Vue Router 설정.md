## Vue Router 설정

### 4.1 라우팅이란?
- 클라이언트에서 url 주소에 따라 페이지가 전환되는 것

### 4.2 Vue-Router 설치
- vue add router

### 4.3 Lazy Load 적용하기 (비동기 컴포넌트)
- 리소스를 컴포넌트 단위로 분리하여 컴포넌트 혹은 라우터 단위로 필요한 것들만 그때 그때 다운받을 수 있게 하는 방법
- ★ 주의 : 라우터에서 Lazy Load로 컴포넌트를 import 한 것은 내부적으로 Vue CLI의 prefetch 기능이 사용되는 것
    - prefetch : 미래에 사용될 수 있는 리소스를 캐시에 저장하는 것 <br/> (prefetch 기능은 기본으로 true로 설정되어 있기 때문에 Lazy Load가 적용된 컴포넌트는 모두 prefetch 기능이 적용)
- prefetch 기능을 끄는 방법
    - vue.config.js에 다음 코드 추가
    - 전체에 대한 prefetch 기능을 끄는 것
    ```
    module.exports = {
        chainWebpack : config => {
            config.plugins.delete('prefetch');
        }
    }
    ```
    - prefetch를 사용하고 싶은 컴포넌트에는 다음 코드 추가
    ```
    import(/* webpackPrefetch: true*/ './views/About.vue');
    ```
    - prefetch 기능이 정말 사라졌는지 확인하는 방법
    ```
    vue inspect --plugin prefetch
    define << 다음의 결과가 나옴
    ```
