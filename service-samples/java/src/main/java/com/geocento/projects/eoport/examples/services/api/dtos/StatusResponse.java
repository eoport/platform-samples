package com.geocento.projects.eoport.examples.services.api.dtos;

/**
 *
 * a standard status response
 */
public class StatusResponse {

    int code;
    String message;

    public StatusResponse() {
    }

    public StatusResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
