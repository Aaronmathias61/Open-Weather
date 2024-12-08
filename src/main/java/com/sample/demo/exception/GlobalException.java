package com.sample.demo.exception;

import lombok.Data;

@Data
public class GlobalException extends RuntimeException {

    private String errorMessage;

    public GlobalException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
