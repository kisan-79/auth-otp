package com.authotp.filters;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
public class AuthenticationFilter extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private String apiKey;

	private String keySecret;

	/**
	 * 
	 * Creates a token with the supplied array of authorities.
	 *
	 * 
	 * 
	 * @param authorities the collection of <tt>GrantedAuthority</tt>s for the
	 *                    principal
	 * 
	 *                    represented by this authentication object.
	 * 
	 */

	public AuthenticationFilter(String apiKey, String keySecret, Collection<? extends GrantedAuthority> authorities) {

		super(authorities);

		this.apiKey = apiKey;

		this.keySecret = keySecret;

		setAuthenticated(true);

	}

	@Override

	public Object getCredentials() {

		return keySecret;

	}

	@Override

	public Object getPrincipal() {

		return apiKey;

	}
}
