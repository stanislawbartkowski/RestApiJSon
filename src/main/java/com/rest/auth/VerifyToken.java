package com.rest.auth;

import com.google.inject.Inject;
import com.rest.conf.IRestConfig;
import com.rest.restservice.RestLogger;
import com.sun.net.httpserver.Headers;
import org.keycloak.common.VerificationException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import static org.junit.Assert.assertTrue;

public class VerifyToken implements IVerifyToken {

    private final IRestConfig iConfig;

    private final static String AUTHORIZATION = "Authorization";
    private final static String BEARER = "Bearer";

    @Inject
    public VerifyToken(IRestConfig iconfig) {
        this.iConfig = iconfig;
    }

    @Override
    public boolean verifyToken(Headers headers) {
        if (!iConfig.isSecured()) return true;

        List<String> li = headers.get(AUTHORIZATION);
        if (li == null || li.size() == 0) {
            String mess = String.format("%s param not found in the request header", AUTHORIZATION);
            RestLogger.L.severe(mess);
            return false;
        }
        String auth = li.get(0);
        if (!auth.startsWith(BEARER)) {
            String mess = String.format("%s not found in %s".format(BEARER, auth));
            RestLogger.L.severe(mess);
            return false;
        }
        String token = auth.substring(BEARER.length()).trim();
        if (TokenCache.tokenInCache(token)) return true;

        RestLogger.info("Token validation");
        try {
            Optional<PublicKey> p = KeycloakAuth.getPublicKeyForToken(iConfig.getAuthUrl(), iConfig.getAuthRealm(), token);
            if (p.isEmpty()) return false;
            KeycloakAuth.verifyToken(token, p.get(), iConfig.getAuthClientId());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | VerificationException | com.auth0.jwt.exceptions.JWTDecodeException e) {
            RestLogger.L.log(Level.SEVERE, "Not authorized", e);
            return false;
        }
        return true;
    }
}
