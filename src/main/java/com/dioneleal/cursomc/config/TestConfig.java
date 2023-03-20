package com.dioneleal.cursomc.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.dioneleal.cursomc.services.DBService;

@Configuration
@Profile("test")
public class TestConfig {

	@Autowired
	private DBService service;

	@Bean
	public boolean instantiateDatabase() throws ParseException {

		service.instantiateTestDatabase();

		return true;
	}
}
