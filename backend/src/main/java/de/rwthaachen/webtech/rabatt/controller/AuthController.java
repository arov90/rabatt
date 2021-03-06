package de.rwthaachen.webtech.rabatt.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.rwthaachen.webtech.rabatt.model.User;
import de.rwthaachen.webtech.rabatt.repository.UserRepository;
import de.rwthaachen.webtech.rabatt.util.JwtTokenParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

  @Autowired
  UserRepository userRepository;

  @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
  public ResponseEntity<?> login(@RequestBody User login) {

    String jwtToken = "";
    HashMap<String, String> result = new HashMap<String, String>();

    if (login.getUsername() == null || login.getPassword() == null) {
      result.put("message", "Please fill in username and password");
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(result);
    }

    String username = login.getUsername();
    String password = login.getPassword();

    List<User> users = userRepository.findByUsername(username);

    if (users.isEmpty()) {
      result.put("message", "Username not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    User user = users.get(0);
    String pwd = user.getPassword();

    if (!password.equals(pwd)) {
      result.put("message", "Invalid login. Please check your name and password.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    jwtToken = Jwts.builder().setSubject(username).claim("roles", "user").setIssuedAt(new Date())
        .signWith(SignatureAlgorithm.HS256, "secretkey").compact();

    result.put("token", jwtToken);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @RequestMapping(value = "/current_user", method = RequestMethod.GET,
      produces = "application/json")
  public ResponseEntity<?> currentUser(@RequestHeader(value = "Authorization") String authHeader)
      throws ServletException {

    HashMap<String, String> result = new HashMap<String, String>();
    JwtTokenParser parser = new JwtTokenParser();

    Claims claim = parser.deserialize(authHeader);
    String username = claim.getSubject();

    List<User> users = userRepository.findByUsername(username);

    if (users.isEmpty()) {
      result.put("message", "Username not found");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    User user = users.get(0);
    return new ResponseEntity<User>(user, HttpStatus.OK);
  }
}
