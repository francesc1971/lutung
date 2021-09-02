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
import java.util.Map;
import java.util.function.Consumer;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.model.MandrillHelperClasses.MandrillRejectsDeleted;
import com.microtripit.mandrillapp.lutung.model.MandrillHelperClasses.MandrillRejectsAdded;
import com.microtripit.mandrillapp.lutung.view.MandrillRejectsEntry;

/**
 * @author rschreijer
 * @since Mar 19, 2013
 */
public class MandrillRejectsApi {

	private final String key;
	private final String rootUrl;
	private final Consumer<Throwable> errorHandler;


	public MandrillRejectsApi(final String key, final String url, final Consumer<Throwable> errorHandler) {
		this.key = key;
		this.rootUrl = url;
		this.errorHandler = errorHandler;
	}

	public Boolean add(final String email, final String comment, 
			final String subaccount) throws MandrillApiError, IOException {
		
		final Map<String,Object> params = MandrillUtil.paramsWithKey(key);
		params.put("email", email);
		params.put("comment", comment);
		params.put("subaccount", subaccount);
		try {
				return MandrillUtil.query(rootUrl+ "rejects/add.json",
					params, MandrillRejectsAdded.class).getAdded();

		} catch (final Exception e) {
			this.errorHandler.accept(e);
			throw e;
		}
		
	}
	
	/**
	 * <p>Retrieve your email rejection blacklist. You can 
	 * provide an email address to limit the results. Returns 
	 * up to 1000 results. By default, entries that have expired 
	 * are excluded from the results; use includeExpired to 
	 * true to include them.</p>
	 * @param email An optional email address to search by.
	 * @param includeExpired Whether to include rejections that 
	 * have already expired.
	 * @return Up to 1000 {@link MandrillRejectsEntry} objects.
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	public MandrillRejectsEntry[] list(final String email, 
			final Boolean includeExpired) throws MandrillApiError, IOException {
	
		return list(email, includeExpired, null);
		
	}
	
	/**
	 * <p>Retrieve your email rejection blacklist. You can 
	 * provide an email address to limit the results. Returns 
	 * up to 1000 results. By default, entries that have expired 
	 * are excluded from the results; use includeExpired to 
	 * true to include them.</p>
	 * @param email An optional email address to search by.
	 * @param includeExpired Whether to include rejections that 
	 * have already expired.
	 * @param subaccount An optional unique identifier for the 
	 * subaccount to limit the blacklist.
	 * @return Up to 1000 {@link MandrillRejectsEntry} objects.
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	public MandrillRejectsEntry[] list(final String email, 
			final Boolean includeExpired, final String subaccount) 
					throws MandrillApiError, IOException {
		
		final Map<String,Object> params = MandrillUtil.paramsWithKey(key);
		params.put("email", email);
		params.put("include_expired", includeExpired);
		if(subaccount != null) {
			params.put("subaccount", subaccount);
		}
		try {
			return MandrillUtil.query(rootUrl + "rejects/list.json",
									  params, MandrillRejectsEntry[].class);

		} catch (final Exception e) {
			this.errorHandler.accept(e);
			throw e;
		}
		
	}
	
	/**
	 * <p>Delete an email rejection. There is no limit to 
	 * how many rejections you can remove from your blacklist, 
	 * but keep in mind that each deletion has an affect on 
	 * your reputation.</p>
	 * @param email The email address that was removed from 
	 * the blacklist.
	 * @return Whether the address was deleted successfully.
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	public Boolean delete(final String email) 
			throws MandrillApiError, IOException {
		
		return delete(email, null);
		
	}
	
	/**
	 * <p>Delete an email rejection. There is no limit to 
	 * how many rejections you can remove from your blacklist, 
	 * but keep in mind that each deletion has an affect on 
	 * your reputation.</p>
	 * @param email The email address that was removed from 
	 * the blacklist.
	 * @param subaccount An optional unique identifier for the 
	 * subaccount to limit the blacklist.
	 * @return Whether the address was deleted successfully.
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	public Boolean delete(final String email, final String subaccount) 
			throws MandrillApiError, IOException {
		
		final Map<String,Object> params = MandrillUtil.paramsWithKey(key);
		params.put("email", email);
		if(subaccount != null) {
			params.put("subaccount", subaccount);
		}
		try {
			return MandrillUtil.query(rootUrl + "rejects/delete.json",
									  params, MandrillRejectsDeleted.class).getDeleted( );
		} catch (final Exception e) {
			this.errorHandler.accept(e);
			throw e;
		}
		
	}
	
}
