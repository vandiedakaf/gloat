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
        databaseConfig.dataSource();

        new Verifications() {{
            basicDataSource.setUrl("jdbc:mysql://localhost/gloat?createDatabaseIfNotExist=true");
            basicDataSource.setUser("root");
            basicDataSource.setPassword("password");
        }};
    }
}