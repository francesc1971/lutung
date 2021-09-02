/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Blueknow Lutung
 *
 * (c) Copyright 2009-2019 Blueknow, S.L.
 *
 * ALL THE RIGHTS ARE RESERVED
 */
package com.microtripit.mandrillapp.lutung.http;

import java.util.Objects;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

class DefaultHttpClientFactory implements  HttpClientFactory {

    /**
     * See https://hc.apache.org/httpcomponents-core-4.3.x/httpcore/apidocs/org/apache/http/params/HttpConnectionParams.html#setSoTimeout(org.apache.http.params.HttpParams, int)
     *
     * A value of 0 means no timeout at all.
     * The value is expressed in milliseconds.
     * */
    static final int SOCKET_TIMEOUT_MILLIS = Integer.getInteger("blueknow.lutung.apache.socket.timeout-in-millis", 0);

    /**
     * See https://hc.apache.org/httpcomponents-core-4.3.x/httpcore/apidocs/org/apache/http/params/HttpConnectionParams.html#setConnectionTimeout(org.apache.http.params.HttpParams, int)
     *
     * A value of 0 means no timeout at all.
     * The value is expressed in milliseconds.
     * */
    static final int CONNECTION_TIMEOUT_MILLIS = Integer.getInteger("blueknow.lutung.apache.connection.timeout-in-millis", 0);

    /**
     * Returns the timeout in milliseconds used when requesting a connection from the connection manager.
     * A timeout value of zero is interpreted as an infinite timeout. A negative value is interpreted as undefined (system default if applicable).
     */
    static final int CONNECTION_REQUEST_TIMEOUT_MILLIS = Integer.getInteger("blueknow.lutung.apache.connection-request.timeout-in-millis", 0);

    /**Number of maximum connections*/
    private int max = 50;
    /**Connection timeout in milliseconds*/
    private int connTimeout = CONNECTION_TIMEOUT_MILLIS;
    /**Connection Request timeout in milliseconds*/
    private int connRequestTimeout = CONNECTION_REQUEST_TIMEOUT_MILLIS;
    /**Socket timeout in milliseconds*/
    private int soTimeout = SOCKET_TIMEOUT_MILLIS;
    
    private ServiceUnavailableRetryStrategy strategy = new MandrillServiceUnavailableRetryStrategy();

    public void setMax(final int max) {
        this.max = max;
    }

    public void setSoTimeout(final int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public void setConnTimeout(final int connTimeout) {
        this.connTimeout = connTimeout;
    }

    public void setConnRequestTimeout(final int connRequestTimeout) {
        this.connRequestTimeout = connRequestTimeout;
    }
    
    public void setStrategy(final ServiceUnavailableRetryStrategy strategy) {
		this.strategy = Objects.requireNonNull(strategy);
	}
    

    @Override
    public HttpClient createClient() {
            final var connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setDefaultMaxPerRoute(this.max);
            connectionManager.setMaxTotal (this.max);
            connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(this.soTimeout).build());
            final var defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(this.soTimeout)
                    .setConnectTimeout(this.connTimeout)
                    .setConnectionRequestTimeout(this.connRequestTimeout).build();
            return HttpClients.custom().setUserAgent("/Lutung-0.1")
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .setConnectionManager(connectionManager)
                    .useSystemProperties()
                    .setServiceUnavailableRetryStrategy(this.strategy)
                    .build();
    }

}