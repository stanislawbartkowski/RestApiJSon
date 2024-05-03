import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;

import java.util.Collections;

class SecurityHelper {

    // auth
    static final String authurl = "http://77.79.208.62:8080/";
    static final String secret = "FzNEeFyhRw1rIZ5RVDAl3lS6ZijsFFKf";
    static final String realm = "Perseus";
    static final String clientid = "React-auth";
    static final String user = "perseus";
    static final String password = "perseus";

    // getToken
    static String authenticateUser(String authurl, String realm, String secret, String clientid, String user, String password) {
        Configuration conf = new Configuration(
                authurl,
                realm,
                clientid,
                Collections.singletonMap("secret", secret),
                null
        );
        AuthzClient authzClient = AuthzClient.create(conf);

        AuthorizationRequest request = new AuthorizationRequest();
        AuthorizationResponse response = authzClient.authorization(user, password).authorize(request);
        String rpt = response.getToken();
        return rpt;
    }

    static String authenticateTestUser() {
        return authenticateUser(authurl,realm,secret,clientid,user,password);
    }
}
