package com.rest.auth;

import com.sun.net.httpserver.Headers;

public interface IVerifyToken {

    boolean verifyToken(Headers headers);
}
