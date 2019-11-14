package com.exebar.poc.spring.boot.filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.FilterRegistration;

@SpringBootApplication
@Configuration
public class FilterExceptionPocApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilterExceptionPocApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean crazyFilterThatThrowsExceptionAllTheTime() {
		FilterRegistrationBean crazyFilter = new FilterRegistrationBean(new CrazyFilterThatThrowsExceptionAllTheTime());
		crazyFilter.addUrlPatterns("/sayHello");
		crazyFilter.setOrder(1);
		return crazyFilter;
	}

	@Bean
	public FilterRegistrationBean exceptionResolverFilter() {
		FilterRegistrationBean crazyFilter = new FilterRegistrationBean(new RuntimeExceptionResolverFilter());
		crazyFilter.addUrlPatterns("/*");
		crazyFilter.setOrder(0);
		return crazyFilter;
	}
}
