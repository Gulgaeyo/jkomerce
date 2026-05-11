# jkomerce (E-Commerce Backend)

세션 기반 인증(Session) + 장바구니(Cart) + 주문(Order: DIRECT/CART) + 결제(Payment) + 재고 차감까지 이어지는 **이커머스 백엔드 학습/구현 프로젝트**입니다.

---

## Highlights

* 결제 승인(Approve) 시점에 **재고 차감을 원자적으로 처리**하여 정합성 확보
* 결제 생성에 **멱등키(idempotencyKey) 기반 중복 방지** 적용
* DIRECT / CART 주문 흐름을 통합하면서도 **주문 시점 가격 스냅샷(order_items.unit_price)** 유지
* Enum 기반 상태 관리로 **상태값 오타/불일치 리스크 최소화**
* 예외 응답 포맷 통일(400/401/409/500)

---

## Tech Stack

* Java 17
* Spring Boot 3.x
* MyBatis (XML Mapper)
* PostgreSQL
* Gradle

---

## Core Features

### Auth (Session)

* `HttpSession` 기반 로그인 유지(쿠키 세션)
* `SessionUserProvider`(또는 유사 컴포넌트)로 userId 추출/검증 로직 일원화

### Cart (장바구니)

* 담기(누적), 조회, 수량 변경(set), 개별 삭제, 전체 비우기
* 조회 응답에서 프론트 편의성을 위해 `itemName`, `price` 포함(Items 조인)
* `quantity=0`이면 삭제 처리
* 현재 `cart_items`는 **hard delete** 기반으로 단순화

### Order (주문)

* **DIRECT 주문 생성**: itemId/quantity로 주문+주문아이템 생성
* **CART 주문 생성**: 장바구니 항목을 주문 스냅샷으로 주문+주문아이템 생성
* 내 주문 목록 조회(페이징/정렬/상태 필터)
* 주문 상세 조회(내 주문만)

> 정책: 주문 생성 시점에는 재고 선점 없음.
> 재고는 결제 승인 시점에 확정/차감 → “주문은 생성됐지만 approve에서 재고 부족으로 실패” 케이스 가능.

### Payment (결제)

* 결제 생성:

  * `idempotencyKey`로 중복 생성 방지
  * order당 활성 REQUESTED 결제 과생성 방지
* 결제 승인(approve):

  * 멱등: 이미 `PAID`면 그대로 반환
  * 승인 단계에서 **재고 먼저 차감**
  * 성공: payment `PAID`, order `PAID`
  * 실패(재고 부족 등): payment `FAILED`, order `CANCELED` (409)
  * CART 주문은 approve 성공 시 **장바구니 비우기** 적용

---

## Domain Enums

* `OrderStatus`: `PENDING`, `PAID`, `CANCELED`, `EXPIRED`
* `OrderType`: `DIRECT`, `CART`
* `PaymentStatus`: `REQUESTED`, `PAID`, `FAILED`

`fromRequired() / fromNullableParam()` 패턴으로 입력/DB 값을 정규화 및 검증합니다.

---

## Error Handling

`GlobalExceptionHandler`에서 예외를 HTTP 상태코드로 매핑하고 통일된 JSON 포맷으로 반환합니다.

* `IllegalArgumentException` → 400 `BAD_REQUEST`
* `IllegalStateException` → 409 `CONFLICT`
* `UnauthorizedException` → 401 `UNAUTHORIZED`
* 기타 → 500 `INTERNAL_ERROR`

예시:

```json
{
  "timestamp": "2026-02-06T18:39:17.7752561",
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "로그인이 필요합니다.",
  "path": "/api/cart/items"
}
```

---

## API Overview

### Cart

* `POST /api/cart/items` : 담기(누적)
* `GET /api/cart/items` : 내 장바구니 조회 (itemName/price 포함)
* `PATCH /api/cart/items/{itemId}` : 수량 변경 (0이면 삭제)
* `DELETE /api/cart/items/{itemId}` : 개별 삭제 (멱등)
* `DELETE /api/cart/items` : 전체 비우기

### Order

* `POST /api/orders` : DIRECT 주문 생성
* `POST /api/orders/cart` : CART 주문 생성
* `GET /api/orders` : 내 주문 목록(페이징/상태 필터)
* `GET /api/orders/{orderId}` : 주문 상세(내 주문만)

### Payment

* `POST /api/payments` : 결제 생성 (idempotencyKey)
* `POST /api/payments/{paymentId}/approve` : 결제 승인(재고 차감 포함)
* (옵션) `POST /api/payments/{paymentId}/fail` : 결제 실패 처리

---

## Stock & Concurrency Policy

재고 차감은 approve 시점에만 수행하며, 아래 형태로 원자적 업데이트를 사용합니다.

* `UPDATE items SET stock = stock - ? WHERE item_id = ? AND stock >= ?`
* update row count가 `0`이면 재고 부족으로 판단 → 결제 실패/주문 취소 처리

---

## Database (Tables)

* `items`
* `carts`
* `cart_items`
* `orders`
* `order_items`
* `payments`

ERD: `e_commerce ERD.drawio`

---

## Getting Started (Local)

### 1) DB 준비

* PostgreSQL 실행
* DB 생성 및 테이블 생성(프로젝트 SQL/ERD 기준)

### 2) 설정

`application.yml`에 datasource 및 mybatis 설정:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/store
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

### 3) 실행

```bash
./gradlew bootRun
```

---

## Quick Integration Scenario

1. 로그인(세션 생성)
2. 장바구니 담기/조회
3. 주문 생성(DIRECT 또는 CART)
4. 결제 생성
5. 결제 승인(approve) → 재고 차감 + 주문 확정
6. CART 주문이면 approve 성공 후 장바구니 비워짐 확인

---

## Roadmap

* CART 주문 생성 요청에도 멱등성 강화(중복 주문 생성 방지 정책 확정)
* 결제/주문 만료 배치(REQUESTED/PENDING 정리) 적용
* 통합 테스트/동시성 테스트 케이스 확장

---
