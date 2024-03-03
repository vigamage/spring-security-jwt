package com.example.sms.exception;

import com.example.sms.dto.ErrorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SmsException extends RuntimeException {

    private String errorCode;

    private String description;

    private ErrorEnum errorEnum;

    public SmsException(String message) {
        super(message);
    }

    public SmsException(ErrorEnum errorEnum) {
        this(errorEnum, null);
    }

    public SmsException(ErrorEnum errorEnum, String customDescription) {
        super(customDescription);
        this.errorEnum = errorEnum;
        if (StringUtils.hasText(customDescription)) {
            this.errorEnum.setDescription(customDescription);
        }
        this.errorCode = errorEnum.getErrorCode();
        this.description = errorEnum.getDescription();
    }

    public SmsException(ErrorDto error) {
        this.errorCode = error.getErrorCode();
        this.description = error.getDescription();
    }

}
