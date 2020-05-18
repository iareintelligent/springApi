package com.topher.jcalc.api.controller;

import com.auth0.spring.security.api.authentication.AuthenticationJsonWebToken;
import com.topher.jcalc.api.db.entities.User;
import com.topher.jcalc.api.model.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers(final AuthenticationJsonWebToken jwt) {
        return ResponseEntity.ok(userService.getAll(jwt));
    }

    @PostMapping("/")
    public ResponseEntity<User> createTestUser(@RequestBody final User userRequest, final AuthenticationJsonWebToken jwt) {
        return ResponseEntity.ok(userService.createTestUser(userRequest, jwt));
    }

    @GetMapping("/me")
    public ResponseEntity<User> get(final AuthenticationJsonWebToken jwt) {
        return ResponseEntity.ok(
                userService.getUserByJwt(jwt)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") final long id, @RequestBody final User userRequest, final AuthenticationJsonWebToken jwt) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest, jwt));
    }
}


