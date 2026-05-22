import com.rest.guice.rest.RegisterExecutors;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class Test1 extends TestHelper {

    @BeforeMethod
    public void beforeEachTestMethod() throws IOException, RestError {
        initno();
    }

    @Test
    public void test1() throws RestError {
        Path p = getPath1("testerror.json");
        P(p.toString());
        RestError thrown = expectThrows(
                RestError.class,
                () -> readJSONAction(p));
        P(thrown.getMessage());
    }

    @Test
    public void test2() throws RestError {
        Path p = getPath1("testdefa");
        IRestActionJSON j = readJSONAction(p);
        assertEquals(j.action(), "Custom action");
        assertEquals(j.getProc(), IRestActionJSON.PYTHON3);
    }

    @Test
    public void test3() throws RestError {
        Path p = getPath1("testsql.json");
        IRestActionJSON j = readJSONAction(p);
        assertEquals(j.action(), "SQL action");
        assertEquals(j.getProc(), IRestActionJSON.SQL);
        assertEquals(j.format(), IRestActionJSON.FORMAT.JSON);
    }

    @Test
    public void test4() throws RestError {
        Path p = getPath1("testpars.json");
        IRestActionJSON j = readJSONAction(p);
        assertEquals(j.action(), "python");
        assertEquals(j.getProc(), IRestActionJSON.PYTHON3);
        assertEquals(j.getParams().size(), 1);
        assertEquals(j.format(), IRestActionJSON.FORMAT.TEXT);
    }

    @Test
    public void test5() throws RestError {
        Path p = getPath2("testshell.json");
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        assertEquals(j.getProc(), IRestActionJSON.SHELL);
    }

    @Test
    public void test6() throws RestError {
        RestError thrown = expectThrows(
                RestError.class,
                () -> init("testpar1.properties"));
        P(thrown.getMessage());
    }

    @Test
    public void test7() throws RestError {
        Path p = getPath3("testpython1.json");
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        assertEquals(j.getProc(), IRestActionJSON.PYTHON3);
    }


    @Test
    public void test8() throws RestError {
        Path p = getPath4("testsql.json");
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        assertEquals(j.getProc(), IRestActionJSON.SQL);
    }

    @Test
    public void test9() throws RestError {
        Path p = getPath1("testshell.json");
        IRestActionJSON j = readJSONAction(p);
        List<String> res = j.actionL();
        assertEquals(res.size(), 2);
        assertEquals(res.get(0), "x");
        assertEquals(res.get(1), "y");
    }

    @Test
    public void test10() throws RestError {
        Path p = getPath1("testshellzip.json");
        IRestActionJSON j = readJSONAction(p);
        assertTrue(j.isUpload());
        assertEquals(j.format(), IRestActionJSON.FORMAT.ZIP);
    }

    @Test
    public void test11() throws RestError {
        Path p = getPath1("testshelly.yaml");
        IRestActionJSON j = readJSONAction(p);
        List<String> res = j.actionL();
        assertEquals(res.size(), 2);
        assertEquals(res.get(0), "x");
        assertEquals(res.get(1), "y");
    }


}