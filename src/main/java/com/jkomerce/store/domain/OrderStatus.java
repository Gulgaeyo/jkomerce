package com.jkomerce.store.domain;

public enum OrderStatus {
    PENDING,
    PAID,
    CANCELED,
    EXPIRED;

    public static OrderStatus fromNullableParam(String raw){
        if(raw == null || raw.isBlank()){
            return null;
        }

        String normalized = raw.trim().toUpperCase();

        try{
            return OrderStatus.valueOf(normalized);
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException("status값이 올바르지 않습니다. status="+raw);
        }
    }
    // 필수 값 파싱
    public static OrderStatus fromRequired(String raw){
        if(raw == null || raw.isBlank()){
            throw new IllegalArgumentException("status값이 올바르지 않습니다. status=" + raw);
        }
        return fromNullableParam(raw);
    }

    public String toDbValue(){
        return this.name();
    }
}
