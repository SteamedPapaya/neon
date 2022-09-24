package com.mouken.infra;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBaseTest {


    static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer("postgres:14.5");
        POSTGRE_SQL_CONTAINER.start();
    }

}
