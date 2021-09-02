/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Blueknow Lutung
 *
 * (c) Copyright 2009-2021 Blueknow, S.L.
 *
 * ALL THE RIGHTS ARE RESERVED
 */
package com.microtripit.mandrillapp.lutung.model;

import com.microtripit.mandrillapp.lutung.http.HttpClientFactory;
import com.microtripit.mandrillapp.lutung.logging.Logger;
import com.microtripit.mandrillapp.lutung.logging.LoggerFactory;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError.MandrillError;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * @author rschreijer
 * @since Feb 21, 2013
 * @ThreadSafe
 */
@SuppressWarnings("deprecation")
class ApacheHttpClientRequestDispatcher implements RequestDispatcher<HttpUriRequest> {

	private static final String UNEXPECTED_ERROR = "unexpected-error";
	
    private static final Logger log = LoggerFactory.getLogger(ApacheHttpClientRequestDispatcher.class);

	/***/
	private final HttpClient httpClient;
	/***/
	private boolean detectProxy;

	ApacheHttpClientRequestDispatcher(final HttpClientFactory factory) {
		this.httpClient = factory.createClient();
	}

	ApacheHttpClientRequestDispatcher(final HttpClientFactory factory, final boolean detectProxy) {
		this(factory);
		this.setDetectProxy (detectProxy);
	}

	public static class Factory implements RequestDispatcher.Factory {

		@SuppressWarnings("rawtypes")
		@Override
		public RequestDispatcher createRequestDispatcher( ) {
			final var detectProxy = Boolean.getBoolean ("blueknow.lutung.detect.proxy");
			final var itFactory = ServiceLoader.load (HttpClientFactory.class).iterator ();
			final ApacheHttpClientRequestDispatcher dispatcher;
			if (itFactory.hasNext ()) {
				final var factory = itFactory.next ();//the first element
				dispatcher = new ApacheHttpClientRequestDispatcher(factory, detectProxy);
			} else {
				dispatcher = new ApacheHttpClientRequestDispatcher(HttpClientFactory.ofDefaults(), detectProxy);
			}
			return dispatcher;
		}
	}

	public final void setDetectProxy(final boolean detectProxy) {
		this.detectProxy = detectProxy;
	}

	public final <T> T execute(final RequestModel<T, HttpUriRequest> requestModel) throws MandrillApiError, IOException {
		//The HttpPost
		final var request = Objects.requireNonNull(requestModel).getRequest();
		//The response for Mandrill API (method POST)
		HttpResponse response = null;
		try {
			if (this.detectProxy) {
				this.detectProxyIfNecessary(requestModel);
			}
			if (log.isDebugEnabled()) {
				log.debug("starting request '" + requestModel.getUrl() + "'");
			}
			response = httpClient.execute(request);
			//The response as String
			final var responseString = EntityUtils.toString(Objects
					.requireNonNull(response, "HttpResponse is null for RequestModel : " + requestModel).getEntity());
			//retrieve the Status Code
			final var status = response.getStatusLine();
			if ( requestModel.validateResponseStatus(status.getStatusCode()) ) {
				try {
					return requestModel.handleResponse( responseString );
				} catch(final HandleResponseException e) {
					throw new IOException(
							"Failed to parse response from request '"
							+requestModel.getUrl()+ "'", e);

				}

			} else {
				// ==> compile mandrill error!
				MandrillError error;
				try {
					error = responseString.isBlank() ? this.defaultMandrillError(status.getStatusCode())
							: LutungGsonUtils.getGson().fromJson(responseString, MandrillError.class);
				} catch (final Throwable ex) {// NOSONAR
					error = new MandrillError("Invalid Error Format", "Invalid Error Format", responseString,
							status.getStatusCode());
				}

				throw new MandrillApiError(String.format("Unexpected http status in response: %d (%s)",
						status.getStatusCode(), status.getReasonPhrase())).withError(error);

			}

		} finally {
			//consume the Entity
			HttpClientUtils.closeQuietly (response);
			//release connection
			if (request instanceof HttpRequestBase) {
				HttpRequestBase.class.cast (request).releaseConnection ();
			}
		}
	}

	private <T> void detectProxyIfNecessary(final RequestModel<T, HttpUriRequest> requestModel) {
		// use proxy?
		final var proxyData = this.detectProxyServer(requestModel.getUrl());
		if (proxyData != null) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Using proxy @%s:%s", proxyData.host,
						proxyData.port));
			}
			final var proxy = new HttpHost(proxyData.host,
					proxyData.port);
			//[28-05-2021] it has been deprecated and it throws an exception
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}
	}

    private ProxyData detectProxyServer(final String url) {
        try {
            final List<Proxy> proxies = ProxySelector.getDefault().select(new URI(url));
            if (proxies != null) {
                for (final var proxy : proxies) {
                    final var address = (InetSocketAddress) proxy.address();
                    if (address != null) {
                        return new ProxyData(address.getHostName(), address.getPort());
                    }
                }
            }
            // no proxy detected!
            return null;

        } catch (final Throwable t) {//NOSONAR
            log.error("Error detecting proxy server", t);
            return null;

        }
    }
    
    private MandrillApiError.MandrillError defaultMandrillError(final Integer code) {//a server error without message and withou
        return new MandrillApiError.MandrillError(UNEXPECTED_ERROR, UNEXPECTED_ERROR, "Server Error", code);
    }

    private static class ProxyData {

        final String host;
        final int port;

        private ProxyData(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

    }

}