import com.rest.conf.IRestConfig;
import com.rest.conf.RegisterGet;
import com.rest.guice.ModuleBuild;
import com.rest.guice.RestConfigFactory;
import com.rest.guice.rest.SetInjector;
import com.rest.main.RestMainHelper;
import com.rest.restservice.RestHelper;
import com.rest.service.RestService;
import com.rest.service.RestVersion;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executors;

// Reuses RestMain's bootstrap but keeps a handle to the HttpServer so a single
// JVM can host successive @BeforeClass calls from different test classes.
final class TestServer {

    private static HttpServer server;

    private TestServer() {
    }

    static synchronized void start(String propertiesPath, int port) throws Exception {
        stop();
        SetInjector.setInjector();
        RestConfigFactory.setInstance(Paths.get(propertiesPath), Optional.empty());
        IRestConfig iconfig = ModuleBuild.getI().getInstance(IRestConfig.class);
        RestMainHelper.registerExecutors(iconfig);
        RestService rs = ModuleBuild.getI().getInstance(RestService.class);

        server = HttpServer.create(new InetSocketAddress(port), 0);
        RegisterGet.RegisterGetService(iconfig, server);
        RestHelper.registerService(server, new RestVersion());
        RestHelper.registerService(server, rs);
        server.setExecutor(iconfig.isSingle() ? null : Executors.newCachedThreadPool());
        server.start();
    }

    static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }
}
