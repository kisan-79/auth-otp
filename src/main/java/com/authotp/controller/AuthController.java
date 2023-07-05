package com.authotp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import com.authotp.constants.AuthConstants;
import com.authotp.services.AuthService;
import com.authotp.valueobjects.AuthRequest;
import com.authotp.valueobjects.AuthResponse;
import com.authotp.valueobjects.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/api/")
public class AuthController {

	private Logger LOG = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	AuthService authService;
	
	// unauthorized request
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return new ResponseEntity<>("hello from unauth", HttpStatus.OK);
	}
	
	@GetMapping(value = "auth/test")
	public ResponseEntity<String> getOtp() {
		return new ResponseEntity<String>("Hello from allowed endpoint", HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, path = "auth/login")
	public ResponseEntity<AuthResponse> init(@RequestBody UserInfo userInfo, HttpServletRequest request) {
		try {
			// setting user details into http session
			HttpSession activeSession = request.getSession(true);
			activeSession.setAttribute(AuthConstants.USERINFO_SESSION_KEY, userInfo);
			ObjectMapper om = new ObjectMapper();
			String userAsString = om.writeValueAsString(userInfo);
			LOG.info("userinfo is " + userAsString);
			return authService.init(userInfo.getPhoneNumber());
		} catch (Exception e) {
			LOG.error("Exception while loggin in. " + e.getMessage(), e);
			AuthResponse response = new AuthResponse();
			response.setError("Error in processing request, please try gain later.");
			response.setStatus("Failed");
			return new ResponseEntity<AuthResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(method = RequestMethod.POST, path = "auth/verifyOtp")
	public ResponseEntity<AuthResponse> verify(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
		UserInfo userInfo = null;
		HttpSession activeSession = request.getSession(false);
		if (null != activeSession && activeSession.getAttribute(AuthConstants.USERINFO_SESSION_KEY) != null) {
			// retriving userinfo from session
			userInfo = (UserInfo) activeSession.getAttribute(AuthConstants.USERINFO_SESSION_KEY);
		}
		return authService.verify(userInfo.getPhoneNumber(), authRequest.getRequest_id(), authRequest.getOtp());

	}

}
