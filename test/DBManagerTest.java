import org.junit.jupiter.api.Test;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DBManagerTest {

    @Test
    void testDatabaseConnectionIsEstablished() throws Exception {
        DBManager dbManager = new DBManager();

        try {
            dbManager.connect();
            Connection connection = dbManager.getConnection();

            assertNotNull(connection);
            assertFalse(connection.isClosed());
        } finally {
            dbManager.close();
        }
    }
}