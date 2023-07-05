package com.authotp.services;

import com.authotp.utils.JWTUtil;
import com.authotp.valueobjects.AuthResponse;
import com.vonage.client.VonageClient;
import com.vonage.client.verify.CheckResponse;
import com.vonage.client.verify.VerifyResponse;
import com.vonage.client.verify.VerifyStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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

	VonageClient client = VonageClient.builder().apiKey("55a7ec32").apiSecret("dDepvBrMCIl62PGg").build();

	// need to- add database to check user credentials
	public ResponseEntity<AuthResponse> init(String identifier) {

		AuthResponse response = new AuthResponse();

		//VerifyResponse otpServiceResponse =client.getVerifyClient().verify("917406454790", "Vonage");

		VerifyResponse otpServiceResponse = new VerifyResponse(VerifyStatus.OK);
		if (otpServiceResponse.getStatus() == VerifyStatus.OK) {
			response.setRequest_id(otpServiceResponse.getRequestId());
			response.setStatus("OTP sent successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setError("otp service error, please try again");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<AuthResponse> verify(String phNumber, String request_id, String otp) {
		AuthResponse response = new AuthResponse();
		//CheckResponse otpServiceResponse = client.getVerifyClient().check(request_id, otp);
		CheckResponse otpServiceResponse = new CheckResponse(VerifyStatus.OK);
		if (otpServiceResponse.getStatus() == VerifyStatus.OK) {
			response.setJwt(jwtUtil.generateToken(phNumber));
			response.setStatus("logged in successfully");
			HttpHeaders header = new HttpHeaders();
			header.set("jwtToken", response.getJwt());
			return  ResponseEntity.ok().headers(header).body(response);

		} else {
			response.setError("Verification failed: " + otpServiceResponse.getErrorText());
			return new ResponseEntity<AuthResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
