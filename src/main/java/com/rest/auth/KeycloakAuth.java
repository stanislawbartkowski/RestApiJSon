package com.rest.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rest.restservice.RestLogger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Optional;
import java.util.Scanner;

public class KeycloakAuth {

    private KeycloakAuth() {
    }

    private static String getResultData(InputStream i) {
        StringBuilder b = new StringBuilder();
        try (Scanner scanner = new Scanner(i)) {
            while (scanner.hasNextLine()) {
                if (b.length() != 0) b.append('\n');
                b.append(scanner.nextLine());
            }
        }
        return b.toString();
    }

    private static Optional<JSONObject> getKeycloakPublicKey(String keycloakurl, String realm) throws IOException {
        String urlK = keycloakurl + "/realms/" + realm + "/protocol/openid-connect/certs";
        URL url = new URL(urlK);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int res = con.getResponseCode();
        if (res != HttpURLConnection.HTTP_OK) {
            String errmess = String.format("%s - cannot obtain public key, HTTP code received: %d", urlK, res);
            RestLogger.L.severe(errmess);
            return Optional.empty();
        }
        String resData = getResultData(con.getInputStream());
        JSONObject o = new JSONObject(resData);
        return Optional.of(o);
    }

    public static Optional<PublicKey> getPublicKeyForToken(String keycloakurl, String realm, String token) throws JWTDecodeException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        Optional<JSONObject> pubkey = getKeycloakPublicKey(keycloakurl, realm);
        if (pubkey.isEmpty()) return Optional.empty();
        DecodedJWT jwt = JWT.decode(token);
        String id = jwt.getKeyId();
        JSONObject p = pubkey.get();
        JSONArray a = p.getJSONArray("keys");
        PublicKey pkey = null;
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = (JSONObject) a.get(i);
            String kid = o.getString("kid");
            if (kid.equals(id)) {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                String modulusBase64 = o.getString("n");
                String exponentBase64 = o.getString("e");
                Base64.Decoder urlDecoder = Base64.getUrlDecoder();
                BigInteger modulus = new BigInteger(1, urlDecoder.decode(modulusBase64));
                BigInteger publicExponent = new BigInteger(1, urlDecoder.decode(exponentBase64));
                pkey = keyFactory.generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
                return Optional.of(pkey);
            }
        }
        String errmess = String.format("Cannot find Keycloak public key for %s", id);
        RestLogger.L.severe(errmess);
        return Optional.empty();
    }

    public static void verifyToken(String token, PublicKey publicKey, String clientId) throws VerificationException {

        TokenVerifier<AccessToken> vtoken = TokenVerifier.create(token, AccessToken.class);
        vtoken.audience(clientId);
        vtoken.publicKey(publicKey);
        vtoken.verify();
    }

    ;
}
