package hu.progmasters.dailybugle.controller;

import hu.progmasters.dailybugle.dto.incoming.LoginCommand;
import hu.progmasters.dailybugle.dto.incoming.RegisterCommand;
import hu.progmasters.dailybugle.dto.outgoing.LoginResponse;
import hu.progmasters.dailybugle.dto.outgoing.UserProfileResponse;
import hu.progmasters.dailybugle.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<Void> resgisterUser(@Valid @RequestBody RegisterCommand registerCommand) {
        log.info("Register user: {}", registerCommand);
        userService.register(registerCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginCommand loginCommand) {
        log.info("Login attempt for email: {}", loginCommand.getEmail());
        LoginResponse response = userService.login(loginCommand);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        log.info("Get user profile for id: {}", id);
        UserProfileResponse response = userService.getUserProfile(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
