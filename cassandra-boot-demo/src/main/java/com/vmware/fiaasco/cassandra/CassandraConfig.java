/*
 * Copyright (c) 2018 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vmware.fiaasco.cassandra;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.datastax.driver.core.LatencyTracker;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryLogger;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SSLOptions;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.CompressionType;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.core.cql.session.DefaultSessionFactory;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.lang.Nullable;

/**
 * Base class for Spring Cassandra configuration that can handle creating namespaces, execute
 * arbitrary CQL on startup & shutdown, and optionally drop keyspaces.
 *
 * @author kumargautam
 */
@Configuration
@EnableConfigurationProperties(CassandraProperties.class)
@Log4j2
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Override
    protected String getKeyspaceName() {
        return cassandraProperties.getKeyspaceName();
    }

    /*
     * Creating keyspace if does not exists
     */
    @Override
    protected List<String> getStartupScripts() {
        List<String> startupScripts = new ArrayList<>();
        startupScripts.add(cassandraProperties.getAlterSystemAuth());
        startupScripts.add(cassandraProperties.getFiaascoKeyspace());
        return startupScripts;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return cassandraProperties.getSchemaAction();
    }

    /**
     * Base packages to scan for entities annotated with {@link Table} annotations. By default,
     * returns the package name of {@literal this} ({@code this.getClass().getPackage().getName()}.
     * This method must never return {@literal null}.
     */
    @Override
    public String[] getEntityBasePackages() {
        return new String[] { getClass().getPackage().getName(), "com.vmware.fiaasco.model" };
    }

    /**
     * Creates a {@link CassandraClusterFactoryBean} that provides a Cassandra
     * {@link com.datastax.driver.core.Cluster}. The lifecycle of
     * {@link CassandraClusterFactoryBean} executes {@link #getStartupScripts() startup} and
     * {@link #getShutdownScripts() shutdown} scripts.
     *
     * @return the {@link CassandraClusterFactoryBean}.
     * @see #cluster()
     * @see #getStartupScripts()
     * @see #getShutdownScripts()
     */
    @Bean
    @Override
    public CassandraClusterFactoryBean cluster() {

        CassandraClusterFactoryBean bean = new CassandraClusterFactoryBean();

        bean.setAddressTranslator(getAddressTranslator());
        bean.setAuthProvider(getAuthProvider());
        bean.setClusterBuilderConfigurer(getClusterBuilderConfigurer());
        bean.setClusterName(getClusterName());
        bean.setContactPoints(getContactPoints());
        bean.setPort(getPort());
        bean.setUsername(getUsername());
        bean.setPassword(getPassword());
        bean.setSslEnabled(getSslEnabled());
        bean.setSslOptions(getSslOptions());
        bean.setCompressionType(getCompressionType());
        bean.setLoadBalancingPolicy(getLoadBalancingPolicy());
        bean.setMaxSchemaAgreementWaitSeconds(getMaxSchemaAgreementWaitSeconds());
        bean.setMetricsEnabled(getMetricsEnabled());
        bean.setNettyOptions(getNettyOptions());
        bean.setPoolingOptions(getPoolingOptions());
        bean.setProtocolVersion(getProtocolVersion());
        bean.setQueryOptions(getQueryOptions());
        bean.setReconnectionPolicy(getReconnectionPolicy());
        bean.setRetryPolicy(getRetryPolicy());
        bean.setSpeculativeExecutionPolicy(getSpeculativeExecutionPolicy());
        bean.setSocketOptions(getSocketOptions());
        bean.setTimestampGenerator(getTimestampGenerator());
        bean.setLatencyTracker(getLatencyTracker());

        bean.setKeyspaceCreations(getKeyspaceCreations());
        bean.setKeyspaceDrops(getKeyspaceDrops());
        bean.setStartupScripts(getStartupScripts());
        bean.setShutdownScripts(getShutdownScripts());
        bean.setJmxReportingEnabled(false);

        return bean;
    }

    /**
     * Creates a {@link CassandraSessionFactoryBean} that provides a Cassandra
     * {@link com.datastax.driver.core.Session}. The lifecycle of
     * {@link CassandraSessionFactoryBean} initializes the {@link #getSchemaAction() schema} in the
     * {@link #getKeyspaceName() configured keyspace}.
     *
     * @return the {@link CassandraSessionFactoryBean}.
     * @see #cluster()
     * @see #cassandraConverter()
     * @see #getKeyspaceName()
     * @see #getSchemaAction()
     * @see #getStartupScripts()
     * @see #getShutdownScripts()
     */
    @Bean
    @Override
    public CassandraSessionFactoryBean session() {

        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();

        session.setCluster(getRequiredCluster());
        session.setConverter(cassandraConverter());
        session.setKeyspaceName(getKeyspaceName());
        session.setSchemaAction(getSchemaAction());
        session.setStartupScripts(getStartupScripts());
        session.setShutdownScripts(getShutdownScripts());

        return session;
    }

    /**
     * Creates a {@link DefaultSessionFactory} using the configured {@link #session()} to be used
     * with {@link org.springframework.data.cassandra.core.CassandraTemplate}.
     *
     * @return {@link SessionFactory} used to initialize the Template API.
     * @since 2.0
     */
    @Bean
    @Override
    public SessionFactory sessionFactory() {
        return new DefaultSessionFactory(getRequiredSession());
    }

    /**
     * Creates a {@link CassandraAdminTemplate}.
     *
     * @throws Exception
     *             if the {@link com.datastax.driver.core.Session} could not be obtained.
     */
    @Bean
    @Override
    public CassandraAdminTemplate cassandraTemplate() throws Exception {
        return new CassandraAdminTemplate(sessionFactory(), cassandraConverter());
    }

    /**
     * Returns the cluster name.
     *
     * @return the cluster name; may be {@literal null}.
     * @since 1.5
     */
    @Nullable
    @Override
    public String getClusterName() {
        return cassandraProperties.getClusterName();
    }

    /**
     * Returns the Cassandra contact points. Defaults to {@code localhost}
     *
     * @return the Cassandra contact points
     * @see CassandraClusterFactoryBean#DEFAULT_CONTACT_POINTS
     */
    @Override
    public String getContactPoints() {
        return cassandraProperties.getContactPoints();
    }

    /**
     * Returns the Cassandra port. Defaults to {@code 9042}.
     *
     * @return the Cassandra port
     * @see CassandraClusterFactoryBean#DEFAULT_PORT
     */
    @Override
    public String getPort() {
        return cassandraProperties.getPort();
    }

    /**
     * SSL enabled or not.
     *
     * @return true/false
     */
    public boolean getSslEnabled() {
        return cassandraProperties.isSsl();
    }

    /**
     * Get Username of db.
     *
     * @return
     */
    public String getUsername() {
        return cassandraProperties.getUsername();
    }

    /**
     * Get Password of db.
     *
     * @return
     */
    public String getPassword() {
        return cassandraProperties.getPassword();
    }

    /**
     * Returns the {@link SocketOptions}.
     *
     * @return the {@link SocketOptions}, may be {@literal null}.
     */
    @Nullable
    @Override
    public SocketOptions getSocketOptions() {
        PropertyMapper map = PropertyMapper.get();
        SocketOptions options = new SocketOptions();
        map.from(this.cassandraProperties::getConnectTimeout).whenNonNull().to(options::setConnectTimeoutMillis);
        map.from(this.cassandraProperties::getReadTimeout).whenNonNull().to(options::setReadTimeoutMillis);
        return options;
    }

    /**
     * Returns the {@link ReconnectionPolicy}.
     *
     * @return the {@link ReconnectionPolicy}, may be {@literal null}.
     */
    @Nullable
    @Override
    public ReconnectionPolicy getReconnectionPolicy() {
        return new ExponentialReconnectionPolicy(1000, cassandraProperties.getReconnectDelay());
    }

    /**
     * Returns the {@link QueryOptions}.
     *
     * @return the {@link QueryOptions}, may be {@literal null}.
     * @since 1.5
     */
    @Nullable
    @Override
    public QueryOptions getQueryOptions() {
        PropertyMapper map = PropertyMapper.get();
        QueryOptions options = new QueryOptions();
        map.from(cassandraProperties::getConsistencyLevel).whenNonNull().to(options::setConsistencyLevel);
        map.from(cassandraProperties::getSerialConsistencyLevel).whenNonNull().to(options::setSerialConsistencyLevel);
        map.from(cassandraProperties::getFetchSize).to(options::setFetchSize);
        return options;
    }

    /**
     * Returns the {@link LatencyTracker}.
     *
     * @return the {@link LatencyTracker}.
     */
    public LatencyTracker getLatencyTracker() {
        return QueryLogger.builder().build();
    }

    /**
     *
     * @return the {@link SSLOptions}
     */
    private SSLOptions getSslOptions() {
        String[] cipherSuites = { "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA" };
        return new FISslOptions(getSslContext(), cipherSuites);
    }

    /**
     * Returns the {@link LoadBalancingPolicy}.
     *
     * @return the {@link LoadBalancingPolicy}, may be {@literal null}.
     */
    @Nullable
    @Override
    public LoadBalancingPolicy getLoadBalancingPolicy() {
        return DCAwareRoundRobinPolicy.builder().build();
    }

    /**
     * Returns the {@link CompressionType}.
     *
     * @return the {@link CompressionType}, may be {@literal null}.
     */
    @Nullable
    @Override
    public CompressionType getCompressionType() {
        return cassandraProperties.getCompression();
    }

    /**
     * Returns the {@link PoolingOptions}.
     *
     * @return the {@link PoolingOptions}, may be {@literal null}.
     */
    @Nullable
    @Override
    public PoolingOptions getPoolingOptions() {
        CassandraProperties.Pool pool = cassandraProperties.getPool();
        PropertyMapper map = PropertyMapper.get();
        PoolingOptions options = new PoolingOptions();
        map.from(pool::getIdleTimeout).whenNonNull().asInt(Duration::getSeconds).to(options::setIdleTimeoutSeconds);
        map.from(pool::getPoolTimeout).whenNonNull().asInt(Duration::toMillis).to(options::setPoolTimeoutMillis);
        map.from(pool::getHeartbeatInterval).whenNonNull().asInt(Duration::getSeconds)
                .to(options::setHeartbeatIntervalSeconds);
        map.from(pool::getMaxQueueSize).to(options::setMaxQueueSize);
        return options;
    }

    private SSLContext getSslContext() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            //NOSONAR
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            //NOSONAR
            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            //NOSONAR
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustAllCerts, null);
            return sc;
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}