package com.jporpha.fitsprint.exception;

/** Violação de uma regra de negócio (ex.: developer de outro time, valor fora do domínio). */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
