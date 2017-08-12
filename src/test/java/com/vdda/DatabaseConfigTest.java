package com.vdda;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.vdda.config.DatabaseConfig;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

public class DatabaseConfigTest {

    @Tested
    DatabaseConfig databaseConfig;

    @Before
    public void setUp() {
        databaseConfig = new DatabaseConfig();
    }

    @Test
    public void dataSource(@Mocked MysqlDataSource basicDataSource) throws URISyntaxException {
        new MockUp<System>()
        {
            @Mock
            public String getenv(final String string) {
                return "mysql://user:password@localhost/database?reconnect=true";
            }
        };

        databaseConfig.dataSource();

        new Verifications() {{
            basicDataSource.setUrl("jdbc:mysql://localhost/database?createDatabaseIfNotExist=true");
            basicDataSource.setUser("user");
            basicDataSource.setPassword("password");
        }};
    }

    @Test(expected=NullPointerException.class)
    public void dataSourceException() throws URISyntaxException {
        new MockUp<System>()
        {
            @Mock
            public String getenv(final String string) {
                return null;
            }
        };

        databaseConfig.dataSource();
    }
}