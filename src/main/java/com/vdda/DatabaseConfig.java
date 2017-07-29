package com.vdda;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by francois on 2016-10-23 for
 * vandiedakaf solutions
 */
@Configuration
public class DatabaseConfig {
    @Bean
    public MysqlDataSource dataSource() throws URISyntaxException {
        String dbUrl = System.getenv("JAWSDB_URL");
        if (dbUrl == null) {
            dbUrl = System.getenv("GLOAT_DB_URL");
        }
        URI dbUri = new URI(dbUrl);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String url = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath() + "?createDatabaseIfNotExist=true";

        MysqlDataSource basicDataSource = new MysqlDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUser(username);
        basicDataSource.setPassword(password);

        return basicDataSource;
    }
}