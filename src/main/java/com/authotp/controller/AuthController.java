package com.authotp.controller;

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
import javax.validation.constraints.NotBlank;
import com.authotp.services.AuthService;
import com.authotp.valueobjects.AuthRequest;
import com.authotp.valueobjects.AuthResponse;

@RestController
@RequestMapping(value = "/api")
public class AuthController {
	
	@Autowired
	AuthService authService;

	@GetMapping(value = "/auth/test")
	public ResponseEntity<String> getOtp() {
		return new ResponseEntity<String>("Hello", HttpStatus.OK);
	}

	@PostMapping(path = "/auth/sendOtp")
	public ResponseEntity<AuthResponse> init(String identifier) {
		return authService.init(identifier);

	}

	@PostMapping(path = "/auth/verifyOtp")
	public ResponseEntity<AuthResponse> verify(@RequestBody AuthRequest request) {
		return authService.verify(request.getPhoneNumber(), request.getRequest_id(), request.getOtp());

	}
	
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		return new ResponseEntity<>("hello", HttpStatus.OK);
	}
}
