/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Blueknow Lutung
 *
 * (c) Copyright 2009-2021 Blueknow, S.L.
 *
 * ALL THE RIGHTS ARE RESERVED
 */
package com.microtripit.mandrillapp.lutung.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.microtripit.mandrillapp.lutung.model.*;

/**
 * @author rschreijer
 * @since Mar 19, 2013
 */
public class MandrillUtil {

	private static MandrillUtil me;

	/**The ThreadSafe component dealing with send messages to Mandrill API*/
	private final RequestDispatcher<? extends Object> dispatcher;

	private final RequestModel.Factory<? extends Object> factory;

	private MandrillUtil() {
		this.dispatcher = RequestDispatcher.lookup();
		this.factory = RequestModel.lookup();
	}

	public static void bootstrap() {
		getInstance();
	}

	/**
	 * @param key
	 * @return
	 */
	static Map<String, Object> paramsWithKey(final String key) {
		final Map<String, Object> params = new HashMap<>();
		params.put("key", key);
		return params;

	}
	
	/**
	 * @param url
	 * @param params
	 * @param responseType
	 * @return
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	@SuppressWarnings("unchecked")
	static <O> O query(final String url, final Map<String, Object> params, final Class<O> responseType)
			throws MandrillApiError, IOException {
		/* The Request Model to be send */
		final RequestModel<O, ?> requestModel = getInstance().getFactory().createRequestModel(url, params,
				responseType);
		/* execute the operation thru Mandrill API */
		return (O) getInstance().getDispatcher().execute(requestModel);
	}

	private static MandrillUtil getInstance() {
		if (me == null) {
			me = new MandrillUtil ();
		}
		return me;
	}

	@SuppressWarnings("rawtypes")
	private RequestDispatcher getDispatcher() {
		return Optional.ofNullable (dispatcher).orElseThrow (() -> new IllegalStateException ("You must call bootstrap() before to do something"));
	}

	@SuppressWarnings("rawtypes")
	private RequestModel.Factory getFactory() {
		return Optional.ofNullable (factory).orElseThrow (() -> new IllegalStateException ("You must call bootstrap() before to do something"));
	}

}