package com.vdda;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

        URI dbUri = new URI(System.getenv("GLOAT_DB_URL"));

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