package org.example.exception.impl;

import org.example.common.Response;
import org.example.exception.CustomException;

public class AuthException extends CustomException {
    public AuthException(Response response) { super(response); }
}
