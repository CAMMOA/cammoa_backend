package org.example.exception.impl;

import org.example.common.Response;
import org.example.exception.CustomException;

public class ChatException extends CustomException {
    public ChatException(Response response) { super(response);}
}
