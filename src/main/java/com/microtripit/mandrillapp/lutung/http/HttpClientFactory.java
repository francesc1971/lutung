/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Blueknow Lutung
 *
 * (c) Copyright 2009-2021 Blueknow, S.L.
 *
 * ALL THE RIGHTS ARE RESERVED
 */
package com.microtripit.mandrillapp.lutung.http;

import java.util.List;

import org.apache.http.client.HttpClient;

@FunctionalInterface
public interface HttpClientFactory extends ClientFactory<HttpClient> {

    static HttpClientFactory ofDefaults() {
        return new DefaultHttpClientFactory ();
    }

    static HttpClientFactory of(final int connTimeoutInMillis, final int socketTimeoutInMillis) {
        final DefaultHttpClientFactory factory = new DefaultHttpClientFactory();
        factory.setConnTimeout(connTimeoutInMillis);
        factory.setSoTimeout(socketTimeoutInMillis);
        return factory;
    }
    
    static HttpClientFactoryBuilder custom() {
    	return new HttpClientFactoryBuilder();
    }
    
    
    class HttpClientFactoryBuilder {
    	
    	/**Number of maximum connections*/
        private int max = 50;
        /**Connection timeout in milliseconds*/
        private int connTimeout = DefaultHttpClientFactory.CONNECTION_TIMEOUT_MILLIS;
        /**Connection Request timeout in milliseconds*/
        private int connRequestTimeout = DefaultHttpClientFactory.CONNECTION_REQUEST_TIMEOUT_MILLIS;
        /**Socket timeout in milliseconds*/
        private int soTimeout = DefaultHttpClientFactory.SOCKET_TIMEOUT_MILLIS;
        /**Max retries when an  status 4xx or 5xx appears*/
        private int maxRetries = 2;
        /**Retry interval*/
        private int retryInterval = 1000;
        /**List of status 4xx or 5xx to deal with a retry*/
        private List<Integer> status = MandrillServiceUnavailableRetryStrategy.DEFAULT_STATUS_LIST;
         
        HttpClientFactoryBuilder() {
        }
        
        /**Number of maximum connections*/
        public HttpClientFactoryBuilder maxConnections(final int max) {
        	this.max = max;
        	return this;
        }
        
        /**Connection timeout in milliseconds*/
        public HttpClientFactoryBuilder connTimeout(final int connTimeout) {
        	this.connTimeout = connTimeout;
        	return this;
        }
        
        /**Connection Request timeout in milliseconds*/
        public HttpClientFactoryBuilder connRequestTimeout(final int connRequestTimeout) {
        	this.connRequestTimeout = connRequestTimeout;
        	return this;
        }
        
        /**Socket timeout in milliseconds*/
        public HttpClientFactoryBuilder socketTimeout(final int soTimeout) {
        	this.soTimeout = soTimeout;
        	return this;
        }
        
        /**Max retries when an  status 4xx or 5xx appears*/
        public HttpClientFactoryBuilder maxRetries(final int maxRetries) {
        	this.maxRetries = maxRetries;
        	return this;
        }
        
        /**Retry interval*/
        public HttpClientFactoryBuilder retryInterval(final int retryInterval) {
        	this.retryInterval = retryInterval;
        	return this;
        }
        
        /**List of status 4xx or 5xx to deal with a retry*/
        public HttpClientFactoryBuilder status(final List<Integer> status) {
        	this.status = status;
        	return this;
        }
    	
		public HttpClientFactory build() {
			final DefaultHttpClientFactory factory = new DefaultHttpClientFactory();
			factory.setMax(max);
			factory.setConnTimeout(connTimeout);
			factory.setSoTimeout(soTimeout);
			factory.setConnRequestTimeout(connRequestTimeout);
			factory.setStrategy(
					new MandrillServiceUnavailableRetryStrategy(this.maxRetries, this.retryInterval, this.status));
			return factory;
		}
    	
    }

}