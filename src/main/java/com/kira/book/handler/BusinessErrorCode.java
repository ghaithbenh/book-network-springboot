package com.kira.book.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BusinessErrorCode {

    NO_CODE(0, NOT_IMPLEMENTED,"no code"),
    INCORRECT_CURRENT_PASSWORD(300,BAD_REQUEST,"incorrrect password"),
    NEW_PASSWORD_DOES_NOT_MATCH(301,BAD_REQUEST,"new password does not match"),
    ACCOUNT_LOCKED(302,FORBIDDEN,"User account is locked"),
    ACCOUNT_DISABLED(303,FORBIDDEN,"User account is disabled"),
    BAD_CREDENTIALS(304,FORBIDDEN,"login/ or password is incorrect")
    ;
    @Getter
    private final int code;
    @Getter
    private final String Description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCode(int code,HttpStatus httpStatus, String description ) {
        this.code = code;
        Description = description;
        this.httpStatus = httpStatus;
    }
}
