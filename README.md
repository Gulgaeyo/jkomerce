# jkomerce 🕹️

세션 기반 로그인 + 장바구니 + 주문(DIRECT/CART) + 결제 + 재고 차감까지 이어지는 **Spring Boot 이커머스 미니 백엔드**.

---

## 스택

- Java 17
- Spring Boot 3.x
- MyBatis (XML Mapper)
- PostgreSQL
- Gradle

---

## 핵심 기능

### ✅ Item
- 상품 조회/참조
- 결제 승인 시점 재고 차감(동시성 고려)

### ✅ Cart (장바구니)
- 내 장바구니 조회
- 담기(누적)
- 수량 변경(set 방식, 0이면 삭제)
- 전체 비우기 / 개별 삭제
- 현재는 `cart_items` **hard delete** 사용

### ✅ Order (주문)
- DIRECT 주문 생성
- CART 주문 생성(장바구니 → 주문/주문아이템 생성)
- 내 주문 목록 조회(페이징/정렬/상태 필터)
- 주문 상세 조회(내 주문만)

> 설계 포인트: **주문 생성 단계에서는 재고 선점 안 함.**  
> 재고는 `Payment approve`에서 차감.  
> 그래서 “주문은 만들어졌는데 결제 승인에서 재고 부족으로 실패” 케이스가 발생 가능.

### ✅ Payment (결제)
- 결제 생성
  - `idempotencyKey`로 중복 생성 방지
  - order당 ACTIVE(REQUESTED) 결제 중복 방지
- 결제 승인(approve)
  - 멱등: 이미 PAID면 그대로 반환
  - 재고 먼저 차감(차감 실패 시 payment FAILED + order CANCELED)
  - 성공 시 payment PAID + order PAID
  - 주문 타입이 CART면 **승인 성공 시 장바구니 비우기**

### ✅ 공통 에러 응답
`GlobalExceptionHandler`에서 통일 포맷 사용.

- `IllegalArgumentException` → 400
- `IllegalStateException` → 409
- `UnauthorizedException` → 401
- 그 외 → 500

응답 예시:
```json
{
  "timestamp": "2026-02-06T18:39:17.7752561",
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "로그인이 필요합니다.",
  "path": "/api/cart/items"
}
