package com.vdda;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by francois on 2016-10-23.
 */
@Configuration
public class DatabaseConfig {
    @Bean
    @Profile("default")
    public MysqlDataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("CLEARDB_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();

        MysqlDataSource basicDataSource = new MysqlDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUser(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }
}
