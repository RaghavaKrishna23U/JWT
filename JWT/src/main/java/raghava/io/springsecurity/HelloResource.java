package raghava.io.springsecurity;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import raghava.io.springsecurity.model.AuthenticationRequest;
import raghava.io.springsecurity.model.AuthenticationResponse;
import raghava.io.springsecurity.services.MyUserDetailsService;
import raghava.io.springsecurity.util.JwtUtil;

@RestController
@RequestMapping("/v1")
public class HelloResource {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService; 
	
	@Autowired
	private JwtUtil jwtTokenUtil;
	
	@GetMapping
	public String hello() {
		return "Hello World";
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest	) throws Exception{
	
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),authenticationRequest.getPassword())
					);
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password",e);
		}
		
		final UserDetails userDetails = myUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
}
