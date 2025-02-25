package com.microtripit.mandrillapp.lutung.controller;

import java.io.IOException;
import java.util.Map;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.model.MandrillHelperClasses.EmailClass;
import com.microtripit.mandrillapp.lutung.view.MandrillWhitelistEntry;

public class MandrillWhitelistsApi {
	private final String key;
	private final String rootUrl;

	public MandrillWhitelistsApi(final String key, final String url) {
		this.key = key;
		this.rootUrl = url;
	}
	
	public MandrillWhitelistsApi(final String key) {
		this(key, MandrillApi.rootUrl);
	}
	
	/**
	 * <p>Adds an email to your email rejection whitelist. If the 
	 * address is currently on your blacklist, that blacklist 
	 * entry will be removed automatically.</p>
	 * @param email An email address to add to the whitelist.
	 * @return If the operation succeeded.
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	public Boolean add(final String email) 
			throws MandrillApiError, IOException {
		
		final Map<String,Object> params = MandrillUtil.paramsWithKey(key);
		params.put("email", email);
		return MandrillUtil.query(rootUrl+ "whitelists/add.json", 
				params, WhitelistsAddResponse.class).getWhether();
		
	}
	
	/**
	 * <p>Retrieves your email rejection whitelist. You can provide 
	 * an email address or search prefix to limit the results. 
	 * Returns up to 1000 results.</p>
	 * @param email An optional email address or prefix to search by.
	 * @return Information for each whitelist entry.
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	public MandrillWhitelistEntry[] list(final String email) 
			throws MandrillApiError, IOException {
		
		final Map<String,Object> params = MandrillUtil.paramsWithKey(key);
		params.put("email", email);
		return MandrillUtil.query(rootUrl+ "whitelists/list.json", 
				params, MandrillWhitelistEntry[].class);
		
	}
	
	/**
	 * <p>Removes an email address from the whitelist.</p>
	 * @param email The email address to remove from the whitelist.
	 * @return Whether the address was deleted successfully.
	 * @throws MandrillApiError Mandrill API Error
	 * @throws IOException IO Error
	 */
	public Boolean delete(final String email) 
			throws MandrillApiError, IOException {
		
		final Map<String,Object> params = MandrillUtil.paramsWithKey(key);
		params.put("email", email);
		return MandrillUtil.query(rootUrl+ "whitelists/delete.json", 
				params, WhitelistsDeleteResponse.class).getDeleted();
		
	}
	
	public static class WhitelistsAddResponse extends EmailClass {
		private Boolean whether;
		public Boolean getWhether() {
			return whether;
		}
	}
	
	public static class WhitelistsDeleteResponse {
		private Boolean deleted;
		public Boolean getDeleted() {
			return deleted;
		}
	}

}
