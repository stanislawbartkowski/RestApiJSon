import com.rest.conf.ConstructRestConfig;
import com.rest.conf.IRestConfig;
import com.rest.main.RestMainHelper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.RestHelper;
import com.rest.runjson.RestRunJson;
import com.rest.service.RestService;
import com.rest.restservice.RestStart;

// -c src/test/resources/testpar/restparam.properties -p 7999


import java.util.Optional;

public class RestMain extends RestStart {


    public static void main(String[] args) throws Exception {

        Optional<RestMainHelper.RestParams> cmd = RestMainHelper.buildCmd(args);
        if (! cmd.isPresent()) return;

        IRestConfig iconfig = ConstructRestConfig.create(cmd.get().getConfigfile());

        RestRunJson.setRestConfig(iconfig);
        RestMainHelper.registerExecutors(IRestActionJSON.SQL);
        RestMainHelper.registerExecutors(IRestActionJSON.PYTHON3);

        RestStart(cmd.get().getPORT(), (server) -> {
            RestHelper.registerService(server, new RestService(iconfig));
        }, new String[]{});

    }
}
