package com.vdda;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by francois on 2016-10-25.
 */
public class DatabaseConfigTest {

    @Tested
    DatabaseConfig databaseConfig;

    @Before
    public void setUp() throws Exception {
        databaseConfig = new DatabaseConfig();
    }

    @Test
    public void dataSource(@Mocked MysqlDataSource basicDataSource) throws Exception {
        new MockUp<System>()
        {
            @Mock
            public String getenv(final String string) {
                return "mysql://user:password@localhost/database?reconnect=true";
            }
        };

        MysqlDataSource mysqlDataSource = databaseConfig.dataSource();

        new Verifications() {{
            basicDataSource.setUrl("jdbc:mysql://localhost/database");
            basicDataSource.setUser("user");
            basicDataSource.setPassword("password");
        }};
    }

    @Test(expected=NullPointerException.class)
    public void dataSourceException() throws Exception {
        new MockUp<System>()
        {
            @Mock
            public String getenv(final String string) {
                return null;
            }
        };

        MysqlDataSource mysqlDataSource = databaseConfig.dataSource();
    }
}
