package com.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.response.ApiResponse;

// 統一處理所有controller發生的例外
@RestControllerAdvice
public class GlobalExceptionHandler {
	// 統一處理BookException
	@ExceptionHandler(BookException.class)
	public ResponseEntity<ApiResponse<String>> handleBookException(BookException e) {
		return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
	}

}
