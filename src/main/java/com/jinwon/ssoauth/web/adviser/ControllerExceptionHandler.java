package com.jinwon.ssoauth.web.adviser;

import com.jinwon.ssoauth.infra.config.jwt.enums.TokenMessage;
import com.jinwon.ssoauth.web.dto.ErrorDto;
import com.jinwon.ssoauth.web.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 컨트롤러 예외 핸들러
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * 잡지 않은 모든 Exception 예외 처리
     *
     * @param e 모든 예외
     * @return 응답 상태 값에 따른 예외 반환
     */
    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ErrorDto> exceptionHandler(Exception e) {
        log.error(ExceptionUtils.getStackTrace(e));
        return new ResponseEntity<>(
                new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 커스텀 예외 처리
     *
     * @param ce 커스텀 예외
     * @return 커스텀 상태 값에 따른 예외 반환
     */
    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<ErrorDto> customExceptionHandler(CustomException ce) {
        log.error(ExceptionUtils.getStackTrace(ce));

        final Optional<TokenMessage> tokenMessageOp = Optional.of(ce)
                .map(CustomException::getTokenMessage);

        final HttpStatus httpStatus = tokenMessageOp.map(TokenMessage::getHttpStatus)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        final String message = tokenMessageOp.map(Enum::name)
                .orElse(StringUtils.EMPTY);

        return new ResponseEntity<>(
                new ErrorDto(httpStatus, message), httpStatus);
    }

}
