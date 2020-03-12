package com.hyx.controller;


import com.hyx.config.JwtTokenUtil;
import com.hyx.model.JwtRequest;
import com.hyx.model.JwtResponse;
import com.hyx.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Expose a POST API /authenticate using the JwtAuthenticationController.
 * The POST API gets the username and password in the body.
 * Using the Spring Authentication Manager, we authenticate the username and password.
 * If the credentials are valid, a JWT token is created using the JWTTokenUtil and is provided to the client.
 * 使用JwtAuthenticationController暴露 POST API /authenticate身份验证接口。
 * POST API获取主体中的用户名和密码。
 * 使用Spring身份验证管理器，我们对用户名和密码进行身份验证。
 * 如果凭据有效，则使用JWTTokenUtil创建JWT令牌并提供给客户端。
 */
@RestController
@CrossOrigin
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
