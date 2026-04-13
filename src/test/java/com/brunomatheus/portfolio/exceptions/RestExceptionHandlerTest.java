package com.brunomatheus.portfolio.exceptions;

import com.brunomatheus.portfolio.dtos.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    private HttpServletRequest mockRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
        return request;
    }

    @Test
    void shouldHandleNotFoundException() {
        HttpServletRequest request = mockRequest();
        NotFoundException ex = new NotFoundException("Project not found");

        ResponseEntity<ErrorResponseDTO> response = handler.handleNotFoundException(ex, request);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Project not found", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldHandleBusinessException() {
        HttpServletRequest request = mockRequest();
        BusinessException ex = new BusinessException("Invalid status transition");

        ResponseEntity<ErrorResponseDTO> response = handler.handleBusinessException(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid status transition", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldHandleValidationException() throws NoSuchMethodException {
        HttpServletRequest request = mockRequest();

        DummyRequest target = new DummyRequest();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "dummyRequest");
        bindingResult.addError(new FieldError("dummyRequest", "name", "Name is required"));
        bindingResult.addError(new FieldError("dummyRequest", "budget", "Budget must be greater than zero"));

        Method method = DummyController.class.getDeclaredMethod("dummyMethod", DummyRequest.class);
        MethodParameterStub methodParameterStub = new MethodParameterStub(method, 0);

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParameterStub, bindingResult);

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidationException(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Validation error", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
        assertNotNull(response.getBody().getValidationErrors());
        assertEquals("Name is required", response.getBody().getValidationErrors().get("name"));
        assertEquals("Budget must be greater than zero", response.getBody().getValidationErrors().get("budget"));
    }

    @Test
    void shouldHandleGenericException() {
        HttpServletRequest request = mockRequest();
        Exception ex = new RuntimeException("Unexpected problem");

        ResponseEntity<ErrorResponseDTO> response = handler.handleGenericException(ex, request);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Unexpected internal error", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldHandleNoResourceFoundException() {
        HttpServletRequest request = mockRequest();
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/api/reports/portfolio");

        ResponseEntity<ErrorResponseDTO> response = handler.handleNoResourceFoundException(ex, request);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void shouldHandleHttpMessageNotReadableException() {
        HttpServletRequest request = mockRequest();
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("JSON parse error");

        ResponseEntity<ErrorResponseDTO> response =
                handler.handleHttpMessageNotReadableException(ex, request);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Invalid request body. Check field values and enum names.", response.getBody().getMessage());
        assertEquals("/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    private static class DummyController {
        @SuppressWarnings("unused")
        public void dummyMethod(DummyRequest request) {
        }
    }

    private static class DummyRequest {
        private String name;
        private String budget;
    }

    private static class MethodParameterStub extends org.springframework.core.MethodParameter {
        public MethodParameterStub(Method method, int parameterIndex) {
            super(method, parameterIndex);
        }
    }
}