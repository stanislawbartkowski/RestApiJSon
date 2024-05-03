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



}