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
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class MandrillServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {
	
	static final List<Integer> DEFAULT_STATUS_LIST = List.of(HttpStatus.SC_REQUEST_TIMEOUT, HttpStatus.SC_INTERNAL_SERVER_ERROR,
			HttpStatus.SC_BAD_GATEWAY, HttpStatus.SC_SERVICE_UNAVAILABLE, HttpStatus.SC_GATEWAY_TIMEOUT);

	/**
     * Maximum number of allowed retries if the server responds with a HTTP code
     * in our retry code list. Default value is 2.
     */
    private final int maxRetries;

    /**
     * Retry interval between subsequent requests, in milliseconds. Default
     * value is 1 second.
     */
    private final long retryInterval;
    
    private final List<Integer> status;

	public MandrillServiceUnavailableRetryStrategy(final int maxRetries, final int retryInterval, final List<Integer> status) {
		Args.positive(maxRetries, "Max retries");
		Args.positive(retryInterval, "Retry interval");
		Args.notNull(status, "status");
		this.maxRetries = maxRetries;
		this.retryInterval = retryInterval;
		this.status = status;
	}
	
	public MandrillServiceUnavailableRetryStrategy(final int maxRetries, final int retryInterval) {
		this(maxRetries, retryInterval, DEFAULT_STATUS_LIST);
	}

	public MandrillServiceUnavailableRetryStrategy() {
		this(2, 1000, DEFAULT_STATUS_LIST);
	}

	@Override
	public boolean retryRequest(final HttpResponse response, final int executionCount, final HttpContext context) {
		return executionCount <= this.maxRetries && checkStatusCode(response);
	}

	@Override
    public long getRetryInterval() {
        return this.retryInterval;
    }
	
	private boolean checkStatusCode(final HttpResponse response) {
		final int statusCode = Optional.ofNullable(response.getStatusLine()).map(StatusLine::getStatusCode).orElse(200);
		return this.status.contains(statusCode);
	}


}
