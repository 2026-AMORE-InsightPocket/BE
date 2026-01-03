## 📮 Insight Pocket Backend

## ⌨️ BE Developer

|                                                            Developer                                                             |
|:--------------------------------------------------------------------------------------------------------------------------------:|
| <a href="https://github.com/HeejuKo"><img src="https://avatars.githubusercontent.com/u/142784710?v=4" width="120px;" alt=""/></a> |
|                                                               고희주                                                                |


## 📌 Infos
- Amazon Best Seller 랭킹 및 LANEIGE 전 제품 크롤링 데이터를 저장·관리합니다.
- 수집된 데이터는 다음 기능의 기초 데이터 소스로 활용됩니다.
	- 랭킹 히스토리 조회
	- 제품 상세/리뷰 분석
	- AI 인사이트 및 RAG 기반 문서 분석
- 분석 전용 테이블을 분리하지 않고, 스냅샷 기반 데이터 모델로 정합성을 유지합니다.


## 🛠 Language and Tools
| **역할**     | **종류**   | **선정 이유**                     |
| ------------ | --------------------------- | -------------------------------- |
| Framework    | ![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)                                                                                                       | 생산성 높은 Java 기반 웹 프레임워크 |
| Language     | ![Java17](https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=openjdk&logoColor=white)                                                                                                                | 안정적이고 대규모 프로젝트에 적합   |
| ORM          | ![JPA](https://img.shields.io/badge/JPA%20(Hibernate)-59666C?style=for-the-badge&logo=hibernate&logoColor=white)                                                                                                        | 객체지향적인 DB 접근                |
| Database     | ![Oracle](https://img.shields.io/badge/Oracle-F80000?style=for-the-badge&logo=oracle&logoColor=white)                                                                                                                   | RAG 문서·임베딩 벡터를 함께 저장하고 검색하기 위한 DB      |                                                                                   
| Deployment   | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)        | 컨테이너 기반 배포, 클라우드 확장성  |
| CI/CD        | ![GitHubActions](https://img.shields.io/badge/GitHubActions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)                                                                                               | 자동화된 빌드/배포 파이프라인        |


## ⚙️ Functions
📊 **Data Ingestion**
- Amazon Best Seller 랭킹 스냅샷 저장
- LANEIGE 전 제품 상세 크롤링 데이터 수신 및 저장
- 제품 스냅샷 단위 데이터 관리

📈 **Data Query**
- 랭킹 스냅샷 기준 TOP N 조회
- 제품별 최신 스냅샷 조회
- 기간별 랭킹 히스토리 제공

🧠 **Review Analysis Support**
- 리뷰 요약(`Customers Say`) 조회
- 평점 분포 / 감정 비율 계산
- `product_snapshot_id` 기준 키워드 분석
- 최신 스냅샷 기준 분석 보장

📚 **RAG Data Provider**
- 규칙 문서 / 데일리 리포트 관리
- RAG 서비스에서 재사용 가능한 문서 구조 제공


## ▶️ 로컬 실행 방법
```bash
# 의존성 설치
./gradlew build -x test

# 로컬 실행
./gradlew bootRun
```
기본 포트: `8080`

## 📂 Project Structure
```
BE
 ┣ 📂 src/main/java/com/pocketmon/insightpocket
 ┃ ┣ 📂 domain
 ┃ ┃ ┣ 📂 dashboard    # 요약 대시보드
 ┃ ┃ ┣ 📂 laneige      # LANEIGE 전 제품
 ┃ ┃ ┣ 📂 ranking      # Amazon Best Seller 랭킹
 ┃ ┃ ┣ 📂 review       # 리뷰 분석
 ┃ ┃ ┗ 📂 rag          # RAG 기반 문서 관리 및 검색
 ┃ ┣ 📂 global
 ┃ ┃ ┣ 📂 common       # 공통 응답, 예외, 유틸 클래스
 ┃ ┃ ┣ 📂 config       # 설정 (DB, Swagger 등)
 ┃ ┃ ┗ 📂 security     # API Key 인증, 필터
 ┣ 📂 src/main/resources
 ┃ ┗ 📜 application.yml
 ┣ 📜 build.gradle
 ┗ 📜 Dockerfile
```

 ## 📄 API Docs
	•	Swagger UI : https://www.tenma.store/swagger-ui/index.html
