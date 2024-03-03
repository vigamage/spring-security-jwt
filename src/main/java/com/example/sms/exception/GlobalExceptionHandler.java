package com.example.sms.exception;

import com.example.sms.dto.ErrorDto;
import com.example.sms.dto.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(SmsException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleSubscriptionException(SmsException ex,
                                                                               WebRequest request) {
        logError(ex, request);
        ErrorDto errorDto = ErrorDto.generateFromSmsException(ex);
        var baseResponseDto = ResponseWrapper.builder().success(false).failure(errorDto).build();
        HttpStatus httpStatusCode;
        if (ex.getErrorEnum() != null) {
            httpStatusCode = ex.getErrorEnum().getHttpStatusCode();
        }
        else {
            httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(baseResponseDto, httpStatusCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Object>> handleOtherExceptions(Exception ex, WebRequest request) {
        logError(ex, request);
        ErrorDto errorDto = ErrorEnum.getErrorDto(ex);
        var baseResponseDto = ResponseWrapper.builder().success(false).failure(errorDto).build();
        var httpStatus = ErrorEnum.getHttpStatusResponseCode(ex);
        return new ResponseEntity<>(baseResponseDto, httpStatus);
    }

    private void logError(Exception ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.error(String.format("Error while handling api request : %s", path), ex);
    }


}
