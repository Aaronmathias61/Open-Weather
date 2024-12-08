package com.sample.demo.exception;

import com.sample.demo.constant.Constant;
import com.sample.demo.model.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.net.ConnectException;
import java.net.UnknownHostException;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    public Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<APIResponse> handleGlobalException(GlobalException globalException) {
        logger.error(globalException.getErrorMessage());
        return ResponseEntity.badRequest().body(new APIResponse(globalException.getErrorMessage()));
    }

    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<APIResponse> handleResourceAccessException(ConnectException connectException) {
        logger.error(connectException.getMessage());
        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new APIResponse(Constant.CONNECTION_ERROR));
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<APIResponse> handleUnknownHostException(UnknownHostException unknownHostException) {
        logger.error(unknownHostException.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(new APIResponse(Constant.UNKNOWN_HOST));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<APIResponse> handleHttpClientErrorException(HttpClientErrorException httpClientErrorException) {
        logger.error(httpClientErrorException.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new APIResponse(Constant.RESOURCE_NOT_FOUND));
    }
}
