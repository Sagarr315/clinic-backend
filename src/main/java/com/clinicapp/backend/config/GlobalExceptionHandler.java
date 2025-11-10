package com.clinicapp.backend.config; //  SAME PACKAGE

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", ex.getMessage());

		HttpStatus status = HttpStatus.BAD_REQUEST;
		if (ex.getMessage().contains("not found")) {
			status = HttpStatus.NOT_FOUND;
		} else if (ex.getMessage().contains("already booked") || ex.getMessage().contains("already exists")) {
			status = HttpStatus.CONFLICT; // 409 for duplicates
		} else if (ex.getMessage().contains("Invalid") || ex.getMessage().contains("Unauthorized")) {
			status = HttpStatus.UNAUTHORIZED;
		}

		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", "Internal server error");
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}