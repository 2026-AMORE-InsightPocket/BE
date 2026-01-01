## 📮 Insight Pocket Backend

## ⌨️ Developer

|                                                            Developer                                                             |
|:--------------------------------------------------------------------------------------------------------------------------------:|
| <a href="https://github.com/HeejuKo"><img src="https://avatars.githubusercontent.com/u/142784710?v=4" width="120px;" alt=""/></a> |
|                                                               고희주                                                                |


## 📌 Infos
- Amazon Best Seller, LANEIGE All Products 크롤링 데이터를 저장·관리합니다.
- 저장된 데이터는 랭킹 스냅샷 조회, 제품 분석, RAG 기반 분석 기능의 기초 데이터로 활용됩니다.


## 🛠 Language and Tools
- Language: Java (Spring Boot)
- API: RESTful API
- Security: API Key 기반 인증
- Data: Oracle Database
- Deploy: Docker, AWS EC2
- CI/CD: GitHub Actions


## ⚙️ Functions
- Amazon Best Seller 랭킹 스냅샷 데이터 저장
- LANEIGE 전 제품 상세 데이터 수신 및 저장
- Oracle DB 기반 중복 데이터 관리 및 정합성 유지
- API Key 기반 외부 크롤링 요청 인증
- 데이터 유효성 검증 및 예외 처리
- 랭킹·제품 데이터 조회 API 제공
- 분석/RAG 서비스에서 재사용 가능한 데이터 구조 제공


## ▶️ 로컬 실행 방법
```bash
# 의존성 설치
./gradlew build -x test

# 로컬 실행
./gradlew bootRun
```

## 📂 프로젝트 구조
```
BE
 ┣ 📂 src/main/java/com/pocketmon/insightpocket
 ┃ ┣ 📂 domain
 ┃ ┃ ┣ 📂 laneige      # Amazon LANEIGE 전 제품
 ┃ ┃ ┣ 📂 ranking      # Amazon Best Seller 랭킹
 ┃ ┃ ┗ 📂 rag          # RAG 기반 문서 관리 및 검색
 ┃ ┣ 📂 global
 ┃ ┃ ┣ 📂 common       # 공통 응답, 예외, 유틸 클래스
 ┃ ┃ ┣ 📂 config       # 애플리케이션/인프라 설정
 ┃ ┃ ┗ 📂 security     # 인증·인가, API Key, 필터
 ┣ 📂 src/main/resources
 ┃ ┗ 📜 application.yml
 ┣ 📜 build.gradle
 ┗ 📜 Dockerfile
```

 ## 📄 API Docs
	•	Swagger UI : https://www.tenma.store/swagger-ui/index.html
