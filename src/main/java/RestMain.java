import com.rest.conf.IRestConfig;
import com.rest.guice.rest.ModuleBuild;
import com.rest.guice.rest.RegisterExecutors;
import com.rest.guice.RestConfigFactory;
import com.rest.main.RestMainHelper;
import com.rest.readjson.IRestActionJSON;
import com.rest.restservice.RestHelper;
import com.rest.service.RestService;
import com.rest.restservice.RestStart;

// -c src/test/resources/testpar/restparam.properties -p 7999


import java.util.Optional;

public class RestMain extends RestStart {


    public static void main(String[] args) throws Exception {

        Optional<RestMainHelper.RestParams> cmd = RestMainHelper.buildCmd(args);
        if (!cmd.isPresent()) System.exit(4);

        RestConfigFactory.setInstance(cmd.get().getConfigfile());
        IRestConfig iRest = ModuleBuild.getI().getInstance(IRestConfig.class);
        for (String p : iRest.listOfPlugins()) RegisterExecutors.registerExecutors(p);

        RestService res = ModuleBuild.getI().getInstance(RestService.class);

        RestStart(cmd.get().getPORT(), (server) -> {
            RestHelper.registerService(server, res);
        }, new String[]{});

    }
}
