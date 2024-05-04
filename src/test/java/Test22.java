import com.rest.auth.KeycloakAuth;
import org.junit.Test;
import org.keycloak.common.VerificationException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import static org.junit.Assert.*;
import com.auth0.jwt.exceptions.JWTDecodeException;


public class Test22 extends TestHelper {


    @Test
    public void test1() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, VerificationException {

        String token = SecurityHelper.authenticateTestUser();

        Optional<PublicKey> p = KeycloakAuth.getPublicKeyForToken(SecurityHelper.authurl, SecurityHelper.realm, token);

        assertTrue(p.isPresent());

        KeycloakAuth.verifyToken(token,p.get(),SecurityHelper.clientid);
    }

    @Test
    public void test2() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String token = "xxxxxx";
        assertThrows(JWTDecodeException.class, () -> KeycloakAuth.getPublicKeyForToken(SecurityHelper.authurl, SecurityHelper.realm, token));
    }

    @Test
    public void test3() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, VerificationException {
        String token = SecurityHelper.authenticateTestUser();

        Optional<PublicKey> p = KeycloakAuth.getPublicKeyForToken(SecurityHelper.authurl, SecurityHelper.realm, token);

        assertTrue(p.isPresent());

        assertThrows(VerificationException.class, () -> KeycloakAuth.verifyToken(token,p.get(),"xxxxxxx"));
    }

    @Test
    public void test4() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, VerificationException {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyV2tzXzZTRmlnM3oxVEtIaURPYnNDNGZGVXdCTTEyWjZmTTF0R3duRllrIn0.eyJleHAiOjE3MTQ4MTY1NTEsImlhdCI6MTcxNDgxNjI1MSwianRpIjoiYTc5OTcwZDItZDdjNS00ZWZhLTkxZGYtZjY3YTg3ZjFiZWIyIiwiaXNzIjoiaHR0cDovLzc3Ljc5LjIwOC42Mjo4MDgwL3JlYWxtcy9QZXJzZXVzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImQxOGU1ZTc1LWRmZWItNDQ5OC1iYjRiLTg1MWNiN2UwY2M4ZiIsInR5cCI6IkJlYXJlciIsImF6cCI6IlJlYWN0LWF1dGgiLCJzZXNzaW9uX3N0YXRlIjoiZTc3MTU5OWQtZGY1Mi00YjgyLWIwNjctMTI0M2U2MDZlNWE0IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1wZXJzZXVzIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsInNpZCI6ImU3NzE1OTlkLWRmNTItNGI4Mi1iMDY3LTEyNDNlNjA2ZTVhNCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InBlcnNldXMiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJwZXJzZXVzIiwiZ2l2ZW5fbmFtZSI6InBlcnNldXMiLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoicGVyc2V1c0B3cC5wbCJ9.dL2LmDXnkA5qnpyxS2BwO6YM2hTjnW1xdjPTakv-CPm3oacRnbhVpCvYEzNWBmax487wddNjzf3TRi-5wX_rN1FOHJnPW4X_ZKOHXQb6ygO1Piv_oYsKekud12uqvwPwjLJkeODo7vGBNxcFTaTYrtqNa_XovUbVYomEdjGrmrnUPwXBc2OU2y9yCd2cQ8BMJ7G-IaTrn6fvaNUqNbKQASAuf6AeMyZdZA6FwceAl5iEgUJlOVEstWYEiIBCHAZS8U_wMZvdauYQNLiWrOLv0aWNrskqpVNClICOw1iwZOsU62JkA10CoxbS-Nve2gToO2hgZBuhzrmjUvBG2hA9Qg";

        Optional<PublicKey> p = KeycloakAuth.getPublicKeyForToken(SecurityHelper.authurl, SecurityHelper.realm, token);

        assertTrue(p.isPresent());

        KeycloakAuth.verifyToken(token,p.get(),SecurityHelper.clientid);

    }




}