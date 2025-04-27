package org.example.exception.impl;

import org.example.common.Response;
import org.example.exception.CustomException;

public class ResourceException extends CustomException {
    public ResourceException(Response response) { super(response); }
}
