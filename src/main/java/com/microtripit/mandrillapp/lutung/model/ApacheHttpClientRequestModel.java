/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Blueknow Lutung
 *
 * (c) Copyright 2009-2019 Blueknow, S.L.
 *
 * ALL THE RIGHTS ARE RESERVED
 */
package com.microtripit.mandrillapp.lutung.model;

import com.microtripit.mandrillapp.lutung.logging.Logger;
import com.microtripit.mandrillapp.lutung.logging.LoggerFactory;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

import java.util.Map;

/**
 * @author rschreijer
 * @since Mar 16, 2013
 */
class ApacheHttpClientRequestModel<OUT> implements RequestModel<OUT, HttpUriRequest> {

    private static final Logger log = LoggerFactory.getLogger(ApacheHttpClientRequestModel.class);

	private final String url;
	private final Class<OUT> responseContentType;
	private final Map<String,?> requestParams;
	
	 ApacheHttpClientRequestModel(final String url,
										final Map<String,Object> params,
										final Class<OUT> responseType ) {
		
		if(responseType == null) {
			throw new NullPointerException();
			
		}
		this.url = url;
		this.requestParams = params;
		this.responseContentType = responseType;
	}

	public static class Factory implements RequestModel.Factory<HttpUriRequest> {
		@Override
		public <O> RequestModel<O, HttpUriRequest> createRequestModel(String url, Map<String, Object> params, Class<O> responseType) {
			return new ApacheHttpClientRequestModel<>(url, params, responseType);
		}
	}

	public final String getUrl() {
		return url;
	}

	public final HttpUriRequest getRequest() {
		final String paramsStr = LutungGsonUtils.getGson().toJson(
				requestParams, requestParams.getClass());
        if (log.isDebugEnabled()) {
			log.debug("raw content for request:\n" + paramsStr);
		}
		final var entity = new StringEntity(paramsStr, "UTF-8");
		entity.setContentType("application/json");
		final var request = new HttpPost(url);
		request.setEntity(entity);
		return request;
		
	}

	public final boolean validateResponseStatus(final int httpResponseStatus) {
		return (httpResponseStatus == 200);
	}

	public final OUT handleResponse(final String responseString) 
			throws HandleResponseException {
		
		try {
            if (log.isDebugEnabled()) {
				log.debug("raw content from response:\n" + responseString);
			}
			return LutungGsonUtils.getGson().fromJson(
					responseString, responseContentType);
			
		} catch(final Throwable t) {
			String msg = "Error handling Mandrill response " +
					((responseString != null)?": '"+responseString+"'" : "");
			throw new HandleResponseException(msg, t);
			
		}
	}

	@Override
	public String toString() {
		return "{url: "+this.url+", requestParams: "+this.requestParams+"}";
	}

}