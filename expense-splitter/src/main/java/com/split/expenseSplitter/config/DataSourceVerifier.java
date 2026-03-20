package com.split.expenseSplitter.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Profile("!test")
public class DataSourceVerifier {

    private final DataSource dataSource;

    public DataSourceVerifier(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void verify() {
        System.out.println("DataSource class = " + dataSource.getClass().getName());

        if (dataSource instanceof HikariDataSource hikari) {
            System.out.println("Hikari pool name = " + hikari.getPoolName());
            System.out.println("Hikari jdbc url = " + hikari.getJdbcUrl());
            System.out.println("Hikari min idle = " + hikari.getMinimumIdle());
            System.out.println("Hikari max pool size = " + hikari.getMaximumPoolSize());
        } else {
            throw new IllegalStateException("Not using HikariCP");
        }
    }
}