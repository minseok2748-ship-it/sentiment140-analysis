# sentiment140-analysis

Assignment 3: Twitter 감정 분석 (Sentiment140 Dataset)  
Kotlin + Gradle 기반 데이터 전처리 및 분석 프로젝트

---

# 프로젝트 개요
이 프로젝트는 Sentiment140(160만 개 트윗) 데이터셋을 활용하여  
**감정 분석용 전처리 및 기초 분석을 수행**하는 과제입니다.

요구사항에 따라 다음 분석을 수행했습니다:

- 감정(Positive/Negative) 분포 분석
- 트윗 텍스트 길이 분석
- 상위 단어 Top 20 (감정별)
- 사용자별 트윗 수 분석
- 대용량 CSV 전처리 (안전한 CSV 파싱)
- 분석 결과를 `analysis.md` 파일로 자동 출력

---

## 프로젝트 구조
sentiment140-analysis/
 src/main/kotlin/Main.kt # 전체 분석 코드
 build.gradle.kts
 README.md # 실행 방법 / 요약 설명
 analysis.md # 실행 후 자동 생성되는 분석 결과

yaml
Copy code

---

## 실행 방법

### 1. 데이터 파일 준비
Kaggle에서 Sentiment140 데이터를 다운로드 후 압축 해제  
파일 이름:
training.1600000.processed.noemoticon.csv

makefile
Copy code

### 2. IntelliJ 실행 설정
Run → Edit Configurations → Program arguments 입력

예:
"C:\Users\PC\Downloads\training.1600000.processed.noemoticon.csv"

shell
Copy code

### 3. 실행
./gradlew run --args="C:/Users/PC/Downloads/training.1600000.processed.noemoticon.csv"

yaml
Copy code

---

## 구현 내용 요약

### CSV 파싱
- 텍스트에 쉼표가 포함된 데이터 안전 처리  
- 정규식 기반 safe CSV split 구현

### 감정 분포 분석
- Positive(4), Negative(0) 개수 및 비율 계산

### 텍스트 길이 분석
- 감정별 평균 텍스트 길이 계산

### 단어 빈도 분석
- 영문 소문자 변환 + 특수문자 제거
- Stopwords 제거
- 감정별 상위 단어 20개 출력

### 사용자 분석
- 사용자별 트윗 수 집계
- 상위 사용자 Top 10 추출

### 결과 자동 저장
실행 후 **analysis.md** 파일 생성  
→ 분석 요약 자동 기록됨

---

## 주의사항
- 데이터 파일(CSV)은 용량 문제로 **GitHub 업로드 금지**
- 절대 GitHub 리포지토리에 포함하면 안 됨
- analysis.md는 코드 실행 후 자동 생성됨

---

## 개발 환경
- Kotlin 1.9.0  
- Gradle JVM 프로젝트  
- IntelliJ IDEA  
- Java 17+

---

## AI 활용 내용
- ChatGPT를 이용하여 CSV 파싱 로직, 전처리 구조, 단어 분석 로직 설계 보조
- 코드 검증 및 오류 수정 도움받음
