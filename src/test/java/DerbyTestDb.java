import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Embedded Derby database used by SQL-related tests. Schema and seed data mirror
// what jdir4/jdir11/jresoudir action JSONs reference. The DB lives in memory
// for the JVM lifetime; initialize() is idempotent and re-seeds tables on each
// call so successive @BeforeClass invocations get a known state.
final class DerbyTestDb {

    static final String JDBC_URL = "jdbc:derby:memory:testrest;create=true";
    static final String USER = "testrest";
    static final String PASSWORD = "secret";

    private static boolean schemaCreated;

    private DerbyTestDb() {
    }

    static synchronized void initialize() throws SQLException {
        try (Connection c = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement st = c.createStatement()) {
            if (!schemaCreated) {
                createSchema(st);
                schemaCreated = true;
            }
            seedData(st);
        }
    }

    private static void createSchema(Statement st) throws SQLException {
        drop(st, "TEST");
        st.execute("CREATE TABLE TEST (X INT)");
        drop(st, "TESTM");
        st.execute("CREATE TABLE TESTM (NAME VARCHAR(100))");
        drop(st, "TESTNUM");
        st.execute("CREATE TABLE TESTNUM (ID VARCHAR(6), NUM NUMERIC(20,2))");
        drop(st, "RESTTEST");
        st.execute("CREATE TABLE RESTTEST (ID INT, NAME VARCHAR(100))");
        drop(st, "DEPT");
        st.execute("CREATE TABLE DEPT (DEPTNO VARCHAR(3), DEPTNAME VARCHAR(36), MGRNO VARCHAR(6), ADMRDEPT VARCHAR(3))");
    }

    private static void seedData(Statement st) throws SQLException {
        st.execute("DELETE FROM TEST");
        st.execute("INSERT INTO TEST VALUES (1)");
        st.execute("INSERT INTO TEST VALUES (3)");
        st.execute("INSERT INTO TEST VALUES (3)");
        st.execute("INSERT INTO TEST VALUES (2)");

        st.execute("DELETE FROM TESTM");

        st.execute("DELETE FROM TESTNUM");
        st.execute("INSERT INTO TESTNUM VALUES ('AA', 123.56)");

        st.execute("DELETE FROM RESTTEST");

        // 14 rows mirroring the IBM SAMPLE.DEPT shape that Test10 expects.
        st.execute("DELETE FROM DEPT");
        for (int i = 1; i <= 14; i++) {
            String deptno = String.format("D%02d", i);
            st.execute("INSERT INTO DEPT VALUES ('" + deptno + "', 'DEPT " + i + "', NULL, NULL)");
        }
    }

    private static void drop(Statement st, String table) {
        try {
            st.execute("DROP TABLE " + table);
        } catch (SQLException ignored) {
        }
    }
}
