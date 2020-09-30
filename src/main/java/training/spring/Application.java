package training.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import training.spring.security.AuthenticateRequest;
import training.spring.security.IdToken;

import java.util.Collections;

@SpringBootApplication
@RestController
@EnableWebSecurity
public class Application extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@GetMapping
	public String hello(@RequestParam(value = "who", defaultValue = "World") String who) {
		return String.format("Hello %s!", who);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<String> authenticate(@RequestBody AuthenticateRequest authenticateRequest) {
		try {
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					authenticateRequest.getUser(),
					authenticateRequest.getPassword()
				)
			);
		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		}

		UserDetails user = userDetailsService.loadUserByUsername(authenticateRequest.getUser());
		return ResponseEntity.ok(IdToken.of(user));
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/authenticate")
				.permitAll().anyRequest().authenticated();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return userName -> new User(userName, "123", Collections.emptyList());
	}
}
