# Order-Management-MSA 

### 🚀 프로젝트 개요

주문 플랫폼 MSA로 구성해보기 

### 📂 API Document

프로젝트의 API 명세는 아래 링크에서 확인하실 수 있습니다.

- [API 문서](https://fluttering-sponge-a4d.notion.site/API-14e1b4e6922f80cfb104db4a02b01018?pvs=4)

### 📄 서비스 구성 및 실행방법

```
/root (최상위 프로젝트 디렉토리)
  ├── build.gradle (최상위 빌드 스크립트)
  ├── settings.gradle (프로젝트 설정 파일)
  ├── /common (공통 설정)
  ├── /msa_exam (유레카)
  |
  ├── /product (상품 관련 모듈)
  |   ├── application/ # 주요 비즈니스 로직과 흐름을 관리
  |   |   ├── domain/ # 핵심 비즈니스 로직에서 사용될 벨류 객체
  |   |   ├── inputport/ # 애플리케이션의 유즈케이스를 구현
  |   |   ├── outputport/ # 애플리케이션에서 외부 시스템과 상호작용하는 인터페이스를 정의
  |   |   └── usecase/ # 외부에서 애플리케이션이 제공하는 서비스를 호출하는 인터페이스를 정의
  |   ├── domain/ # 도메인 로직을 포함
  |   |   └──  vo/ # 값 객체를 정의    
  |   └── framework/ # 시스템의 외부 인터페이스 관련 코드 위치
  |       ├── adapter/ # 어댑터 역할을 하는 코드가 위치
  |       ├── repository/ # 애플리케이션에서 외부 시스템과 상호작용
  |       └── web/ # 웹 관련 코드가 위치
  |           ├── controller/ # REST API 컨트롤러와 요청 핸들링 클래스
  |           └── dto/ # 데이터 전송 객체를 모아두는 계층
  |
  ├── /gateway (게이트웨이 모듈)
  ├── /order (주문 관련 모듈)
  └── /auth (인증 관련 모듈)
```

- 헥사고날 아키텍처와 도메인 주도 설계(DDD)를 중심으로 프로젝트를 구성하였습니다.
- 마이크로서비스 아키텍처(MSA) 실습을 목표로 멀티 모듈 구조를 채택하여 각 모듈 간의 독립성을 유지하는 방식으로 구성하였습니다. 

### 🔍 How To Start

Intellij 개발 툴을 사용해 개발하였습니다. 

1. git clone

```
https://github.com/sososo0/Order-Management-MSA.git
```

2. .env 파일 생성 

- application.yml에 환경 변수를 주입하기 위해 .env 파일을 작성하였습니다.

  - auth 
    ```
    SECRET_KEY=
    DB_NAME=# auth의 DB
    DB_USER=
    DB_PASSWORD=#dev와 prod에 맞춰서 작성합니다. 
    REDIS_HOST=#redis 실행 서버 
    REDIS_PORT=
    REDIS_PW=
    ```
  - gateway
    ```
    SECRET_KEY=# auth 와 값 동일 
    TOKEN_ISSUER=# auth의 spring.applicaiton.name
    ```
  - order 
    ```
    DB_NAME=# order의 DB
    DB_USER=
    DB_PASSWORD=#dev와 prod에 맞춰서 작성합니다. 
    REDIS_HOST=#redis 실행 서버 
    REDIS_PORT=
    REDIS_PW=
    ```
  - product 
    ```
    DB_NAME=# product의 DB
    DB_USER=
    DB_PASSWORD=#dev와 prod에 맞춰서 작성합니다. 
    REDIS_HOST=#redis 실행 서버 
    REDIS_PORT=
    REDIS_PW=
    ```

3. db 구성
    1) dev (application-dev.yml) : docker container나 로컬 DB에 연결합니다. 
    2) prod (application-prod.yml) : AWS RDS를 사용합니다. 

3. redis container 생성 

- refresh token 보관용 redis를 생성합니다. 

```
docker run -d \
  --name auth_redis \
  --restart always \
  -e REDIS_ARGS="--requirepass 설정하는_redis_비밀번호" \
  -p 6379:6379 \
  -p 8001:8001 \
  redis/redis-stack
```

- 캐싱용 redis도 필요하지만, 캐싱 기능을 구현하지 못하여 생략합니다.

4. Zipkin 컨테이너 생성

```
docker run -d -p 9411:9411 openzipkin/zipkin
```
