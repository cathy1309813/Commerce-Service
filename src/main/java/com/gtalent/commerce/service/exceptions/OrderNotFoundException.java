package com.gtalent.commerce.service.exceptions;


//自訂的 exception → 自己寫 class
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
