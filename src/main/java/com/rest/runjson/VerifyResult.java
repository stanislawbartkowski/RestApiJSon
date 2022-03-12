package com.rest.runjson;

import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.RestLogger;
import com.rest.service.RestService;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class VerifyResult {

    static void verifyResult(String s, IRestActionJSON.FORMAT format) throws RestError {

        // do not verify JSON - already verified
        switch (format) {
            case JS: {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                if (engine == null) {
                    RestLogger.info("nashorn engine not available, JS code not verified");
                    break;
                }
                try {
                    engine.eval(s);
                } catch (ScriptException e) {
                    Helper.throwException(s, e);
                }
                break;
            }
            case XML: {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = null;
                try {
                    InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
                    dBuilder = factory.newDocumentBuilder();
                    Document doc = dBuilder.parse(stream);
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    Helper.throwException(s, e);
                }
            }
        }
    }
}
