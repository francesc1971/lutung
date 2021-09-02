/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Blueknow Lutung
 *
 * (c) Copyright 2009-2021 Blueknow, S.L.
 *
 * ALL THE RIGHTS ARE RESERVED
 */
package com.microtripit.mandrillapp.lutung;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.Recipient;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage.Recipient.Type;

public class MandrillAPIShould { //NOSONAR

	private static final String RESPONSE_SUCCESS = "[\n"
			+ "  {\n"
			+ "    \"email\": \"linobot+and-the-bots@botslikeus.com\",\n"
			+ "    \"status\": \"sent\",\n"
			+ "    \"reject_reason\": \"\",\n"
			+ "    \"_id\": \"linoid.123456789012\"\n"
			+ "  }\n"
			+ "]";
	
	private static final String BAD_GATEWAY = "<html>\n"
			+ "<head><title>502 Bad Gateway</title></head>\n"
			+ "<body bgcolor=\"white\">\n"
			+ "<center><h1>502 Bad Gateway</h1></center>\n"
			+ "<hr><center>nginx/1.12.2</center>\n"
			+ "</body>\n"
			+ "</html>";
	
	@Rule
	public WireMockRule rule = new WireMockRule(9854);
	
	final MandrillApi api = new MandrillApi("junit.wiremock", "http://localhost:9854/");
	
	@Test
	public void send_message_correctly() throws IOException, MandrillApiError {
	
		rule.stubFor(
				post(urlEqualTo("/messages/send.json")).willReturn(okJson(RESPONSE_SUCCESS)));
		
		final MandrillMessageStatus[] status = api.messages().send(this.createMessage(), false);
		
		Assert.assertNotNull(status);
        Assert.assertEquals("sent", status[0].getStatus());
		
        rule.verify(1, postRequestedFor(urlEqualTo("/messages/send.json")));
	}

	
	@Test
	public void send_message_with_server_errors_502() throws IOException, MandrillApiError {
	
		rule.stubFor(
				post(urlEqualTo("/messages/send.json")).willReturn(aResponse().withStatus(502).withBody(BAD_GATEWAY)));
		try {
			//error detected a 502
			api.messages().send(this.createMessage(), false);
			Assert.fail();
		} catch (final MandrillApiError error) {
			Assert.assertNotNull(error);
			Assert.assertEquals(Integer.valueOf(502), error.getMandrillErrorCode());
		}
		
		 rule.verify(3, postRequestedFor(urlEqualTo("/messages/send.json")));
	}
	
	private MandrillMessage createMessage() {
		final Recipient to = new Recipient();
		to.setEmail("lutung.mandrill@gmail.com");
		to.setType(Type.TO);
		final List<Recipient> recipients = List.of(to);
		final MandrillMessage message = new MandrillMessage();
		message.setFromEmail("from@blcart.com");
		message.setTo(recipients);
		return message;
	}
}
