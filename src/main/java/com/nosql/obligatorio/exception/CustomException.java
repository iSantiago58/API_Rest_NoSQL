package com.nosql.obligatorio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class CustomException extends RuntimeException{

    public CustomException(String mensaje) {
        super(mensaje);
    }
}
