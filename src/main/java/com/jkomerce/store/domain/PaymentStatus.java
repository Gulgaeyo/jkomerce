package com.jkomerce.store.domain;

public enum PaymentStatus {
    REQUESTED,
    PAID,
    FAILED,
    EXPIRED,
    CANCELED;

    public static PaymentStatus fromNullableParam(String raw) {
        if(raw == null || raw.isBlank()){
            return null;
        }

        String normalized = raw.trim().toUpperCase();

        try{
            return PaymentStatus.valueOf(normalized);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("PaymentStatus값이 올바르지 않습니다. value= " + normalized);
        }
    }

    public static PaymentStatus fromRequired(String raw){
        if(raw == null || raw.isBlank()){
            throw new IllegalArgumentException("PaymentStatus값이 올바르지 않습니다.");
        }
        return fromNullableParam(raw);
    }

    public String toDbValue(){
        return this.name();
    }
}
