package com.example.cardactivation.controller;


import com.example.cardactivation.dto.core.ResponseObject;
import com.example.cardactivation.dto.enums.Error;
import com.example.cardactivation.exception.ServiceException;
import com.example.cardactivation.exception.TxException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nonnull;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class CardActivatedExceptionHandler extends ResponseEntityExceptionHandler  {

    private final Logger log;


    @ExceptionHandler({ResourceAccessException.class,
            HttpClientErrorException.class,
            HttpServerErrorException.class,
            ConnectException.class})
    public ResponseObject<?> handleNetworkException(Exception e) {

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        PrintStream out2 = new PrintStream(out1);
        e.printStackTrace(out2);
        String message = out1.toString(StandardCharsets.UTF_8);

        log.error("I/O error occurred : {} : {}", e.getClass().getSimpleName(), e.getMessage());
        log.error(message);

        return ResponseObject
                .error()
                .message(Error.NETWORK_ERROR.getErrorDescription())
                .code(Error.NETWORK_ERROR.getErrorCode())
                .build();
    }


    @ExceptionHandler({DataIntegrityViolationException.class,
            PersistenceException.class,
            TooManyResultsException.class,
            SQLIntegrityConstraintViolationException.class,
            SQLSyntaxErrorException.class})
    public ResponseObject<?> handleDataIntegrityViolationException(Exception e) {

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        PrintStream out2 = new PrintStream(out1);
        e.printStackTrace(out2);
        String message = out1.toString(StandardCharsets.UTF_8);log.error("Database error: {}", message);

        return ResponseObject
                .error()
                .message(Error.INTERNAL_ERROR.getErrorDescription())
                .code(Error.INTERNAL_ERROR.getErrorCode())
                .build();
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<Object> handleInternalErrorException(Exception e) {

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        PrintStream out2 = new PrintStream(out1);
        e.printStackTrace(out2);
        String message = out1.toString(StandardCharsets.UTF_8);

        log.error(message);

        return ResponseEntity.ok(
                ResponseObject.error()
                        .message(Error.INTERNAL_ERROR.getErrorDescription())
                        .code(Error.INTERNAL_ERROR.getErrorCode())
                        .build()
        );
    }

    @ExceptionHandler({ExpiredJwtException.class,
            JwtException.class,
            AccessDeniedException.class,
            MalformedJwtException.class})
    public ResponseEntity<ResponseObject<?>> securityHandler(RuntimeException e) {

        log.error("Authentication failed. JWT error: {}", e.getMessage());

        return ResponseEntity.ok(
                ResponseObject.fail()
                        .exception(e.getClass())
                        .description(e.getMessage())
                        .code(Error.ACCESS_DENIED.getErrorCode())
                        .build()
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @Nonnull HttpHeaders headers,
                                                                  @Nonnull HttpStatus status,
                                                                  @Nonnull WebRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        log.error("Validation error.");
        for (FieldError error : fieldErrors) {
            log.error("{} ({}) : {}", error.getField(), error.getRejectedValue(), error.getDefaultMessage());
        }

        return ResponseEntity.ok(
                ResponseObject.fail()
                        .description("Validation error: " + fieldErrors.get(0).getDefaultMessage())
                        .exception(ex.getClass())
                        .code(Error.VALIDATION_ERROR.getErrorCode())
                        .build()
        );
    }

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<Object> handleStatementException(ServiceException e) {
        log.error(e.getMessage());

        return ResponseEntity.ok(
                ResponseObject.fail()
                        .description(e.getMessage())
                        .exception(e.getClass())
                        .code(Error.SERVICE_ERROR.getErrorCode())
                        .build()
        );
    }

    @ExceptionHandler({SecurityException.class,
            IllegalArgumentException.class,
            IllegalBlockSizeException.class,
            BadPaddingException.class})
    public ResponseObject<?> handleSecurityException(Exception e) {

        log.error("Decryption error: {}", e.getMessage());
        return ResponseObject.fail()
                .description(Error.VALIDATION_ERROR.getErrorDescription())
                .exception(e.getClass())
                .code(Error.VALIDATION_ERROR.getErrorCode())
                .build();
    }

    @ExceptionHandler(TxException.class)
    public ResponseEntity<Object> handleTxException(TxException e) {
        log.error("TX error: " + e.getMessage());

        return ResponseEntity.ok(
                ResponseObject.fail()
                        .description(e.getMessage())
                        .exception(e.getClass())
                        .code(Error.PROCESSING_ERROR.getErrorCode())
                        .build()
        );
    }
}
