package com.authotp.controller;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authotp.valueobjects.ProductsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping(value = "/api")
@RestController
public class ProductsController {

	ResourceLoader resourceLoader = new DefaultResourceLoader();

	private Logger LOG = LoggerFactory.getLogger(ProductsController.class);

	@GetMapping(value = "/getProductsFromTemplate")
	public ResponseEntity<ProductsResponse> getProducts() {
		ProductsResponse response = new ProductsResponse();
		Resource resourse = resourceLoader.getResource("classpath:" + "json/template/products.json");
		ObjectMapper objectMapper = new ObjectMapper();
		try (Reader reader = new InputStreamReader(resourse.getInputStream(), StandardCharsets.UTF_8)) {
			String json = FileCopyUtils.copyToString(reader);
			response = objectMapper.readValue(json, ProductsResponse.class);
		} catch (Exception e) {
			LOG.error("error while reading data from template ", e.getMessage());
			response.setError("We are facing issue right now, please try after some time");
			response.setSuccess(false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
		return ResponseEntity.ok().body(response);
	}

}
