package com.barneymorris.ecommerce.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerNowFoundException extends RuntimeException {
    private final String msg;
}
