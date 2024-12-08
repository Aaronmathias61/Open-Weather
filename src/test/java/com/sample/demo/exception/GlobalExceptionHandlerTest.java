package com.sample.demo.exception;

import com.sample.demo.constant.MockConstants;
import com.sample.demo.model.APIResponse;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private Logger logger;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        logger = spy(globalExceptionHandler.logger);
        globalExceptionHandler.logger = logger;
    }

    @Test
    void globalExceptionTest() {
        assertNotNull(getAccessDeniedException());
    }

    public GlobalException getAccessDeniedException() {
        return new GlobalException(MockConstants.DEMO);
    }

    @Test
    void testHandleGlobalException() {
        GlobalException globalException = new GlobalException(MockConstants.GLOBAL_EXCEPTION);
        ResponseEntity<APIResponse> response = globalExceptionHandler.handleGlobalException(globalException);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleResourceAccessException() {
        ConnectException connectException = new ConnectException(MockConstants.UNABLE_TO_CONNECT_EXTERNAL_API);
        ResponseEntity<APIResponse> response = globalExceptionHandler.handleResourceAccessException(connectException);
        assertNotNull(response);
        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
    }

    @Test
    void testHandleUnknownHostException() {
        UnknownHostException unknownHostException = new UnknownHostException(MockConstants.UNKNOWN__HOST);
        ResponseEntity<APIResponse> response = globalExceptionHandler.handleUnknownHostException(unknownHostException);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
    }

    @Test
    void testHandleHttpClientErrorException() {
        HttpClientErrorException httpClientErrorException =
                HttpClientErrorException.create(HttpStatus.NOT_FOUND, MockConstants.RESOURCE_NOT_FOUND, null, null, null);
        ResponseEntity<APIResponse> response = globalExceptionHandler.handleHttpClientErrorException(httpClientErrorException);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testHandleValidationExceptions() {
        ConstraintViolationException constraintViolationException = new ConstraintViolationException(MockConstants.INCORRECT_CITY_NAME,new HashSet<>());
        ResponseEntity<APIResponse> response = globalExceptionHandler.handleValidationExceptions(constraintViolationException);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}