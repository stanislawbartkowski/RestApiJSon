package com.rest.runjson.executors;

import com.rest.readjson.Helper;
import com.rest.readjson.HelperJSon;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetResourceDirExecutor extends AbstractResourceDirExecutor {

    private Path getDir(IRestActionJSON j) {
        return new File(rootdirlist.getFiles()[0], j.action()).toPath();
    }

    private boolean isYAMLorJS(Path p) {
        return Helper.isExtension(p, IRestActionJSON.JSONEXT) || Helper.isExtension(p, IRestActionJSON.YAMLEXT);
    }

    private List<Path> createList(IRestActionJSON j) throws IOException {
        return Files.list(getDir(j)).filter(file -> !Files.isDirectory(file) && isYAMLorJS(file)).collect(Collectors.toList());
    }

    @Override
    public void executeJSON(IRestActionJSON j, RunResult res, Map<String, ParamValue> values) throws RestError {
        JSONArray a = new JSONArray();
        try {
            List<Path> plist = createList(j);
            for (Path p : plist) {
                File f = new File(j.action(),p.getName(p.getNameCount()-1).toString());
                JSONObject jo  = HelperJSon.readJS(rootdirlist,f.toString());
                a.put(jo);
            }
        } catch (IOException e) {
            Helper.throwException("Error while reading list of files in " + getDir(j), e);
        }
        res.json = new JSONObject();
        res.json.put("res", a);
    }
}
