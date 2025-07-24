# 이커머스 경영 경리 프로그램 - 자동 회계 처리 API

이 프로젝트는 `bank_transactions.csv`와 `rules.json` 파일을 기반으로 거래 내역을 자동 분류하고,  
사업체별로 분류된 결과를 조회할 수 있는 Spring Boot + MyBatis 기반 REST API입니다.

---

## 📦 기술 스택

- Java 17
- Spring Boot 3.x
- MyBatis
- Oracle Database

---

## 🛠️ 프로젝트 실행 방법

### 1. Oracle DB 테이블 생성

```sql
-- 회사 테이블
CREATE TABLE COMPANY (
    COMPANY_ID VARCHAR2(20) PRIMARY KEY,
    COMPANY_NAME VARCHAR2(100)
);

-- 계정과목 테이블
CREATE TABLE CATEGORIES (
    CATEGORY_ID VARCHAR2(20) PRIMARY KEY,
    COMPANY_ID VARCHAR2(20),
    CATEGORY_NAME VARCHAR2(100),
    FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(COMPANY_ID)
);

-- 거래 테이블
CREATE TABLE TRANSACTIONS (
    TRANSACTION_ID VARCHAR2(20) PRIMARY KEY,
    COMPANY_ID VARCHAR2(20),
    TRANSACTION_DATE DATE,
    CATEGORY_ID VARCHAR2(20),
    CATEGORY_NAME VARCHAR2(100),
    DIV VARCHAR2(1),
    AMOUNT NUMBER(15, 2),
    BAL NUMBER(15, 2),
    CLASSIFY_STATUS VARCHAR2(20),
    DESCRIPTION VARCHAR2(255),
    FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(COMPANY_ID),
    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES(CATEGORY_ID)
);
```

---

## 🧪 API 테스트 (cURL)

### 1. 거래 자동 분류 요청

```bash
curl -X POST http://localhost:8080/api/v1/accounting/process   -F "transactions=@/path/to/bank_transactions.csv"   -F "rules=@/path/to/rules.json"
```

- `transactions`: CSV 파일 (`date,description,in_amount,out_amount,balance`)
- `rules`: JSON 파일 (`company_id`, `category_id`, `keywords` 등 포함)

### 2. 특정 회사의 거래 분류 결과 조회

```bash
curl -X GET "http://localhost:8080/api/v1/accounting/records?companyId=com_1"   -H "Accept: application/json"
```

---

## 📂 샘플 데이터

- `bank_transactions.csv`: 거래 일자, 적요, 입금/출금 금액, 잔액 포함
- `rules.json`: 회사, 계정과목, 키워드 기반 분류 규칙

---

## 📌 분류 상태

- `분류완료`: 규칙에 따라 회사 및 계정과목이 지정된 거래
- `미분류`: 어떤 규칙과도 매칭되지 않은 거래

---

# 자동분류 로직 및 보안/문제 해결 방안

## **핵심 자동분류 로직**
1. **Rules.json**을 파싱해서 `Map` 형태로 저장  
2. **bank_transaction.csv**를 한 줄씩 읽어 `Transaction` 객체로 변환  
3. `Transaction`의 **적요(description)**에 규칙의 **keyword**가 포함되어 있는지 확인  
4. 규칙이 일치하면 **company 정보와 category 정보를 매핑**하여 저장  
5. **조회 시 미분류 판단**이 아닌 **저장 시점에 분류 상태를 정제**  
   - 규칙 일치 시: **분류완료**  
   - 규칙 불일치 시: **미분류**  

---

## **규칙이 더 복잡해질 때의 확장 아이디어**
1. **금액구간 조건 추가**  
   - `rules.json`에 금액 범위 지정 필드를 추가:  
     ```json
     "amountRange": { "min": 0, "max": 5000 }
     ```  
     → 금액 구간별로 계정 변경 가능  
2. **특수 거래 처리**  
   - 매출·매입 이외의 **취소·환불·교환 거래**는  
     - 거래 저장 시 **데이터를 저장하지 않거나**  
     - **별도 제외 테이블**에 저장하도록 개선  

---

## **보안 강화 방안**
- **공인인증서와 패스워드 평문 저장 금지**  
- **민감 정보 관리 전용 서버 구축**  
  - 접근 제한 및 로그 관리 강화  
  - 감사 로그 기록으로 이상 접근 탐지  

---

## **문제상황 해결책 제시**
### 시나리오: 한 고객사의 거래 데이터가 다른 고객사 대시보드에 노출된 경우
1. **즉시 대응 조치**  
   - 문제 발생 서비스 **즉시 사용 중단 및 접근 제한**  
   - **관계자 보고 및 고객사 통보**  
2. **원인 분석**  
   - **로그 분석** 및 **쿼리·API 점검**  
   - 필터링 조건, 권한 예외 상황 점검  
3. **재발 방지 대책**  
   - 모든 조회 쿼리에 **고객사 ID 검증 로직 필수화**  
   - **권한 관리 세분화**로 정밀한 접근 통제  
   - **개발·배포 시 보안 코드 삽입 의무화**  
   - **보안 코드 리뷰 및 테스트 자동화**로 사전 차단

---

