import com.rest.guice.rest.RegisterExecutors;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Test1 extends TestHelper {

    @Before
    public void beforeEachTestMethod() throws IOException, RestError {
        initno();
    }

    @Test
    public void test1() throws RestError {
        Path p = getPath1("testerror.json");
        P(p.toString());
        RestError thrown = assertThrows(
                RestError.class,
                () -> readJSONAction(p));
        P(thrown.getMessage());
    }

    @Test
    public void test2() throws RestError {
        Path p = getPath1("testdefa.json");
        IRestActionJSON j = readJSONAction(p);
        assertEquals("Custom action", j.action());
        assertEquals(IRestActionJSON.PYTHON3, j.getProc());
    }

    @Test
    public void test3() throws RestError {
        Path p = getPath1("testsql.json");
        IRestActionJSON j = readJSONAction(p);
        assertEquals("SQL action", j.action());
        assertEquals(IRestActionJSON.SQL, j.getProc());
        assertEquals(IRestActionJSON.FORMAT.JSON, j.format());
    }

    @Test
    public void test4() throws RestError {
        Path p = getPath1("testpars.json");
        IRestActionJSON j = readJSONAction(p);
        assertEquals("python", j.action());
        assertEquals(IRestActionJSON.PYTHON3, j.getProc());
        assertEquals(1, j.getParams().size());
        assertEquals(IRestActionJSON.FORMAT.TEXT, j.format());
    }

    @Test
    public void test5() throws RestError {
        Path p = getPath2("testshell.json");
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        assertEquals(IRestActionJSON.SHELL, j.getProc());
    }

    @Test
    public void test6() throws RestError {
        RestError thrown = assertThrows(
                RestError.class,
                () -> init("testpar1.properties"));
        P(thrown.getMessage());
    }

    @Test
    public void test7() throws RestError {
        Path p = getPath3("testpython1.json");
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        assertEquals(IRestActionJSON.PYTHON3, j.getProc());
    }


    @Test
    public void test8() throws RestError {
        Path p = getPath4("testsql.json");
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        assertEquals(IRestActionJSON.SQL, j.getProc());
    }

    @Test
    public void test9() throws RestError {
        Path p = getPath1("testshell.json");
        IRestActionJSON j = readJSONAction(p);
        List<String> res = j.actionL();
        assertEquals(2,res.size());
        assertEquals("x",res.get(0));
        assertEquals("y",res.get(1));
    }

    @Test
    public void test10() throws RestError {
        Path p = getPath1("testshellzip.json");
        IRestActionJSON j = readJSONAction(p);
        assertTrue(j.isUpload());
        assertEquals(IRestActionJSON.FORMAT.ZIP, j.format());
    }

    @Test
    public void test11() throws RestError {
        Path p = getPath1("testshelly.yaml");
        IRestActionJSON j = readJSONAction(p);
        List<String> res = j.actionL();
        assertEquals(2,res.size());
        assertEquals("x",res.get(0));
        assertEquals("y",res.get(1));
    }



}