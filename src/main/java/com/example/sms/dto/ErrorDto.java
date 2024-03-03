package com.example.sms.dto;

import com.example.sms.exception.ErrorEnum;
import com.example.sms.exception.SmsException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {

	@JsonIgnore
	private int httpStatusCode;

	private String errorCode;

	private String description;

	private String errorInstant;

	public static ErrorDto generateFromSmsException(final SmsException ex) {
		return generateFromSmsException(ex, null);
	}

	public static ErrorDto generateFromSmsException(final SmsException ex,
															 final ErrorEnum errorEnum) {

		var builder = ErrorDto.builder();

		builder.errorCode(ex.getErrorCode())
				.description(StringUtils.hasText(ex.getDescription()) ? ex.getDescription() : ex.getMessage())
				.errorInstant(Instant.now().toString());
		if (errorEnum != null) {
			builder.errorCode(errorEnum.getErrorCode());
		}
		return builder.build();
	}

}
