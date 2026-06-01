package com.sarpreetsingh.nevis.controller;

import com.sarpreetsingh.nevis.dto.response.ErrorResponse.ErrorDto;
import com.sarpreetsingh.nevis.exception.ClientNotFoundException;
import com.sarpreetsingh.nevis.exception.DuplicateEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.http.MediaType.ALL_VALUE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ClientNotFoundException.class)
    public ErrorDto handleClientNotFoundException(ClientNotFoundException e) {
        log.info("ClientNotFoundException => {}", e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEmailException.class)
    public ErrorDto handleDuplicateEmailException(DuplicateEmailException e) {
        log.info("DuplicateEmailException => {}", e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorDto handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info("HttpMessageNotReadableException => {}", e.getLocalizedMessage());
        return new ErrorDto("invalid payload");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().isEmpty() ? "invalid payload"
                : e.getAllErrors().get(0).getDefaultMessage();
        log.info("MethodArgumentNotValidException => {}", message);
        return new ErrorDto(message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("MethodArgumentTypeMismatchException => {}", e.getMessage());
        return new ErrorDto("invalid " + e.getParameter().getParameterName());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ErrorDto handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String message = e.getAllErrors().isEmpty() ? "invalid payload"
                : e.getAllErrors().get(0).getDefaultMessage();
        log.info("HandlerMethodValidationException => {}", message);
        return new ErrorDto(message);
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorDto handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.info("HttpMediaTypeNotSupportedException => {}", e.getMessage());
        return new ErrorDto("only application/json content type supported");
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(value = HttpMediaTypeNotAcceptableException.class, produces = ALL_VALUE)
    public String handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        log.info("HttpMediaTypeNotAcceptableException => {}", e.getMessage());
        return "only application/json content type accepted";
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorDto handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.info("HttpRequestMethodNotSupportedException => {}", e.getMessage());
        return new ErrorDto("only GET and POST methods allowed");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ErrorDto handleNoResourceFoundException(NoResourceFoundException e) {
        log.info("NoResourceFoundException => {}", e.getMessage());
        return new ErrorDto("url not found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleUnknownException(Exception e) {
        log.error("UnknownException => {}", e.getMessage(), e);
        return new ErrorDto("internal server error");
    }
}
