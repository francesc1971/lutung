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

import com.microtripit.mandrillapp.lutung.util.FeatureDetector;

import java.util.*;

/**
 * 
 * @author rschreijer
 * @since Jan 7, 2013
 * @param <O> The type that response-data/ response-content is parsed to.
 */
public interface RequestModel<O,R> {
	
	/**
	 * @return The url for this request, as {@link String}.
	 */
	 String getUrl();

	/**
	 * @return The request object describing the request to 
	 * be made w/ a http client.
	 *
	 * @since Mar 22, 2013
	 */
	R getRequest();
	
	/**
	 * <p>Checks weather the response-status is as-expected 
	 * for this request.</p>
	 * @param httpResponseStatus The HTTP response status
	 * @return <code>true</code> if the response status is as expected,
	 * <code>false</code> otherwise.
	 */
	boolean validateResponseStatus(final int httpResponseStatus);
	
	/**
	 * <p>Parses the content/data of this request's response into
	 * a desired format {@link O}.
	 * @param responseString
	 * @return
	 * @throws HandleResponseException
	 */
	O handleResponse(final String responseString) throws HandleResponseException;

	interface Factory<R> {

		<O> RequestModel<O,R> createRequestModel(final String url,
												   final Map<String,Object> params, final Class<O> responseType);

		static <R> Factory<R> noOp() {
			return new Factory<>( ) {

				@Override
				public <O> RequestModel<O, R> createRequestModel(String url, Map<String, Object> params, Class<O> responseType) {
					return null;
				}
			};
		}
	}

	@SuppressWarnings("unchecked")
	static <R> RequestModel.Factory<R> lookup() {
		RequestModel.Factory<R> instance = null;
		try {
			final var providers = ServiceLoader.load (RequestModel.Factory.class).iterator ();
			while (providers.hasNext()) {
				//retrieve the implementation, we only accept ONE
				final var spi = providers.next();
				if (instance != null) {
					throw new IllegalStateException("Multiple RequestModel.Factory implementations found: "
															+ spi.getClass().getName() + " and "
															+ instance.getClass().getName());
				}
				instance = spi;
			}
		} catch (final ServiceConfigurationError sce) {//[30-03-2020] if something wrong happens we must provide a NoOp Provider

			//return a default file
			instance = (Factory<R>) defaultFactory();
		}
		instance = Optional.ofNullable(instance).orElseGet(() -> (Factory<R>) defaultFactory());
		return instance;
	}

	private static RequestModel.Factory<?> defaultFactory( ) {
		return FeatureDetector.isClassPresent("org.apache.http.client.HttpClient") ?
				new ApacheHttpClientRequestModel.Factory(): RequestModel.Factory.noOp();
	}

}