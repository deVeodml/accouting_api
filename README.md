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
