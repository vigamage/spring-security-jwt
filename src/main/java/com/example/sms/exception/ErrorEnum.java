package com.example.sms.exception;

import com.example.sms.dto.ErrorDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum ErrorEnum {

	// 400
	ILLEGAL_ARGUMENTS(IllegalArgumentException.class, HttpStatus.BAD_REQUEST, "400001", "ERROR_ILLEGAL_ARGUMENTS"),

	// 500
	GENERIC500("ERROR_SERVER_ERROR", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),

	OPERATOR_EXISTS("ERROR_OPERATOR_EXISTS", "Operator Exists", HttpStatus.BAD_REQUEST),

	// Third party calls
	INVALID_STUDENT_ID("ERROR_INVALID_STUDENT_ID", "Invalid student id provided", HttpStatus.BAD_REQUEST)

	;

	private Class<?> clazz;

	private final HttpStatus httpStatusCode;

	private final String errorCode;

	private String description;

	ErrorEnum(Class<?> clazz, HttpStatus httpStatusCode, String errorCode) {
		this(clazz, httpStatusCode, errorCode, null);
	}

	ErrorEnum(Class<?> clazz, HttpStatus httpStatusCode, String errorCode, String description) {
		this.clazz = clazz;
		this.httpStatusCode = httpStatusCode;
		this.errorCode = errorCode;
		this.description = description;
	}

	ErrorEnum(String errorCode, String description, HttpStatus statusCode) {
		this.errorCode = errorCode;
		this.description = description;
		this.httpStatusCode = statusCode;
	}

	private static final Map<Class<?>, ErrorEnum> BY_EXCEPTION_CLAZZ = new HashMap<>();
	static {
		for (ErrorEnum error : values()) {
			BY_EXCEPTION_CLAZZ.put(error.clazz, error);
		}
	}

	public static ErrorDto getErrorDto(Exception t) {
		final ErrorEnum errorType = BY_EXCEPTION_CLAZZ.getOrDefault(t.getClass(), GENERIC500);

		return ErrorDto.builder()
			.errorCode(errorType.getErrorCode())
			.description(StringUtils.hasText(t.getMessage()) ? t.getMessage() : errorType.getDescription())
			.errorInstant(Instant.now().toString())
			.build();

	}

	public static <T extends Exception> HttpStatus getHttpStatusResponseCode(T throwable) {
		var applicationError = BY_EXCEPTION_CLAZZ.getOrDefault(throwable.getClass(), GENERIC500);
		return applicationError.httpStatusCode;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
