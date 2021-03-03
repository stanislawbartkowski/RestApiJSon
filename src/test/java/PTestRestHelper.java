import com.rest.test.TestRestHelper;

abstract class PTestRestHelper extends TestRestHelper {

    protected PTestRestHelper() {
        super("localhost", 7999);
    }
}
