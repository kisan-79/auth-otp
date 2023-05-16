package com.authotp.services;

import com.authotp.utils.JWTUtil;
import com.authotp.valueobjects.AuthResponse;
import com.vonage.client.VonageClient;
import com.vonage.client.verify.CheckResponse;
import com.vonage.client.verify.VerifyResponse;
import com.vonage.client.verify.VerifyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	@Autowired
	JWTUtil jwtUtil;

	@Value("${vonage-api-key}")
	String apiKey;

	@Value("${vonage-secret-key}")
	String secretKey;

	VonageClient client = VonageClient.builder().apiKey(apiKey).apiSecret(secretKey).build();

	public ResponseEntity<AuthResponse> init(String identifier) {

		AuthResponse response = new AuthResponse();

		VerifyResponse verifyResponse = client.getVerifyClient().verify("917406454790", "Vonage");
		
		//VerifyResponse verifyResponse = new VerifyResponse(VerifyStatus.OK);
		if (verifyResponse.getStatus() == VerifyStatus.OK) {
			response.setRequest_id(verifyResponse.getRequestId());
			response.setStatus("OTP sent successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setError("otp service error, please try again");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<AuthResponse> verify(String phNumber, String request_id, String otp) {
		AuthResponse response = new AuthResponse();
		CheckResponse checkResponse = client.getVerifyClient().check(request_id, otp);
		//CheckResponse checkResponse = new CheckResponse(VerifyStatus.OK);
		if (checkResponse.getStatus() == VerifyStatus.OK) {
			response.setJwt(jwtUtil.generateToken(phNumber));
			return new ResponseEntity<AuthResponse>(response, HttpStatus.OK);

		} else {
			response.setError("Verification failed: " + checkResponse.getErrorText());
			return new ResponseEntity<AuthResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
