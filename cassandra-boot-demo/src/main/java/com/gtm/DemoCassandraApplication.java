package com.gtm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;

/**
 * Insert your comment for DemoCassandraApplication here
 *
 * @author kumargautam
 */
@SpringBootApplication(exclude = { CassandraAutoConfiguration.class, CassandraDataAutoConfiguration.class })
public class DemoCassandraApplication {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoCassandraApplication.class, args);
    }

}

