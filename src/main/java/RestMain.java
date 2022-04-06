import com.rest.conf.IRestConfig;
import com.rest.guice.ModuleBuild;
import com.rest.guice.RestConfigFactory;
import com.rest.guice.rest.SetInjector;
import com.rest.main.RestMainHelper;
import com.rest.restservice.RestHelper;
import com.rest.service.RestService;
import com.rest.restservice.RestStart;
import com.rest.service.RestVersion;

// -c src/test/resources/testpar/restparam.properties -p 7999

import java.util.Optional;

public class RestMain extends RestStart {


    public static void main(String[] args) throws Exception {

        Optional<RestMainHelper.RestParams> cmd = RestMainHelper.buildCmd(args);
        if (!cmd.isPresent()) System.exit(4);
        SetInjector.setInjector();

        RestConfigFactory.setInstance(cmd.get().getConfigfile(),Optional.empty());

        IRestConfig ires = ModuleBuild.getI().getInstance(IRestConfig.class);
        RestMainHelper.registerExecutors(ires);

        RestService res = ModuleBuild.getI().getInstance(RestService.class);

        RestStart(cmd.get().getPORT(), ires.isSingle(), (server) -> {
            RestHelper.registerService(server,new RestVersion());
            RestHelper.registerService(server, res);
        }, new String[]{});

    }
}
