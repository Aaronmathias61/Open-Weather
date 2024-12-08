package com.sample.demo.exception;

import com.sample.demo.constant.MockConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionTest {

    @Test
    public void testGlobalExceptionConstructor() {
        GlobalException exception = new GlobalException(MockConstants.RESOURCE_NOT_FOUND);
        assertNotNull(exception);
        assertEquals(MockConstants.RESOURCE_NOT_FOUND, exception.getErrorMessage());
    }

    @Test
    public void testGlobalExceptionWithNullMessage() {
        GlobalException exception = new GlobalException(null);
        assertNotNull(exception);
        assertNull(exception.getErrorMessage());
    }
}
