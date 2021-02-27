import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static com.rest.readjson.RestActionJSON.*;
import static org.junit.Assert.*;

public class Test1 extends TestHelper {

    @Test
    public void test1() {
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


}