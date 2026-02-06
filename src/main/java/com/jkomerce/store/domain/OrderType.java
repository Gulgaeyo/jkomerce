package com.jkomerce.store.domain;

import java.util.Arrays;

public enum OrderType {
    DIRECT,
    CART;

    public static OrderType fromNullableParam(String raw) {
        if(raw == null || raw.isBlank()){
            return null;
        }

        String normalized = raw.trim().toUpperCase();

        try{
            return OrderType.valueOf(normalized);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Order type값이 올바르지 않습니다. orderType= "+ normalized);
        }
    }

    public static OrderType fromRequired(String raw){
        if(raw == null || raw.isBlank()){
            throw new IllegalArgumentException("Order type이 필요합니다.");
        }
        return fromNullableParam(raw);
    }

    public String toDbValue(){
        return this.name();
    }
}
