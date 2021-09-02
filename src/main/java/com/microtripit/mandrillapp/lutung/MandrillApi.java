/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Blueknow Lutung
 *
 * (c) Copyright 2009-2019 Blueknow, S.L.
 *
 * ALL THE RIGHTS ARE RESERVED
 */
package com.microtripit.mandrillapp.lutung;

import com.microtripit.mandrillapp.lutung.controller.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author rschreijer
 * @since Mar 17, 2013
 */
public class MandrillApi {

	public static final String rootUrl = "https://mandrillapp.com/api/1.0/";

	private String key;

	private final MandrillUsersApi users;
	private final MandrillMessagesApi messages;
	private final MandrillTagsApi tags;
	private final MandrillRejectsApi rejects;
	private final MandrillWhitelistsApi whitelists;
	private final MandrillSendersApi senders;
	private final MandrillUrlsApi urls;
	private final MandrillTemplatesApi templates;
	private final MandrillWebhooksApi webhooks;
	private final MandrillSubaccountsApi subaccounts;
	private final MandrillInboundApi inbound;
	private final MandrillExportsApi exports;
	private final MandrillIpsApi ips;
	
	public MandrillApi(final String key) {
		this(key, rootUrl, e -> {});
	}

	public MandrillApi(final String key, final Consumer<Throwable> errorHandler) {//it could be nullable
		this(key, rootUrl, Optional.ofNullable(errorHandler).orElseGet(() -> e -> {}));
	}

	public MandrillApi(final String key, final String rootUrl) {
		this(key, rootUrl, e -> {});
	}

	public MandrillApi(final String key, final String rootUrl, final Consumer<Throwable> errorHandler) {
		//assertions
		Objects.requireNonNull (key, "'key' is null; please provide Mandrill API key");
		Objects.requireNonNull (rootUrl, String.format("'rootUrl' is null; please provide Mandrill URL (default: %s)", rootUrl));
		Objects.requireNonNull (errorHandler, String.format("'errorHandler' is null; please provide a non nullable consumer of errors", rootUrl));
		this.key = key;
		users = new MandrillUsersApi(key, rootUrl);
		messages = new MandrillMessagesApi(key, rootUrl, errorHandler);
		tags = new MandrillTagsApi(key, rootUrl);
		rejects = new MandrillRejectsApi(key, rootUrl, errorHandler);
		whitelists = new MandrillWhitelistsApi(key, rootUrl);
		senders = new MandrillSendersApi(key, rootUrl);
		urls = new MandrillUrlsApi(key, rootUrl);
		templates = new MandrillTemplatesApi(key, rootUrl);
		webhooks = new MandrillWebhooksApi(key, rootUrl);
		subaccounts = new MandrillSubaccountsApi(key, rootUrl);
		inbound = new MandrillInboundApi(key, rootUrl);
		exports = new MandrillExportsApi(key, rootUrl);
		ips = new MandrillIpsApi(key, rootUrl);
		//load at init the ApacheHttpClientRequestDispatcher (and the HttpClient)
		MandrillUtil.bootstrap();
	}

	/**
	 * @return Your Mandrill API key.o
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * <p>Get access to 'users' calls.</p>
	 * @return An object with access to user calls.
	 */
	public MandrillUsersApi users() {
		return users;
	}
	
	public MandrillMessagesApi messages() {
		return messages;
	}
	
	public MandrillTagsApi tags() {
		return tags;
	}
	
	public MandrillRejectsApi rejects() {
		return rejects;
	}
	
	public MandrillWhitelistsApi whitelists() {
		return whitelists;
	}
	
	public MandrillSendersApi senders() {
		return senders;
	}
	
	public MandrillUrlsApi urls() {
		return urls;
	}
	
	public MandrillTemplatesApi templates() {
		return templates;
	}
	
	public MandrillWebhooksApi webhooks() {
		return webhooks;
	}
	
	public MandrillSubaccountsApi subaccounts() {
		return subaccounts;
	}
	
	public MandrillInboundApi inbound() {
		return inbound;
	}
	
	public MandrillExportsApi exports() {
		return exports;
	}
	
	public MandrillIpsApi ips() {
		return ips;
	}
	
}