package com.alfonso.jwtsecurity.controller;


import com.alfonso.jwtsecurity.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.alfonso.jwtsecurity.entity.Error;

@ControllerAdvice
public class HandlerExceptionController {
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<Error> handleBadCredentials(BadCredentialsException ex) {
        Error error = new Error();
        error.setError(ex.getMessage());
        error.setMsg("Usuario o contraseña incorrecta");
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(error);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Error> handleUserNotFound(UserNotFoundException ex) {
        Error error = new Error();
        error.setError(ex.getMessage());
        error.setMsg("Usuario no encontrado");
        error.setStatus(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(error);
    }

    @ExceptionHandler({InvalidRefreshToken.class})
    public ResponseEntity<Error> handleInvalidRefreshToken(InvalidRefreshToken ex) {
        Error error = new Error();
        error.setError(ex.getMessage());
        error.setMsg("Token refresh inválido");
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(error);
    }

    @ExceptionHandler({UserAlreadyExists.class})
    public ResponseEntity<Error> handleUserAlreadyExists(UserAlreadyExists ex) {
        Error error = new Error();
        error.setError(ex.getMessage());
        error.setMsg("El username ya está siendo usado. Por favor, utilice uno distinto");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(error);
    }

    @ExceptionHandler({InvalidJwtTokenException.class})
    public ResponseEntity<Error> handleInvalidJwtToken(InvalidJwtTokenException ex) {
        Error error = new Error();
        error.setError(ex.getMessage());
        error.setMsg("El token proporcionado es inválido o ha expirado");
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(error);
    }

    @ExceptionHandler({MissingAuthorizationHeaderException.class})
    public ResponseEntity<Error> handleMissingAuthorization(MissingAuthorizationHeaderException ex) {
        Error error = new Error();
        error.setError(ex.getMessage());
        error.setMsg("Token no proporcionado");
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(error);
    }
}
