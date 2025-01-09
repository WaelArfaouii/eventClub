package org.example.springbootsecurityjwt.request;


import org.example.springbootsecurityjwt.models.ERole;
import org.example.springbootsecurityjwt.models.Role;
import org.example.springbootsecurityjwt.models.User;
import org.example.springbootsecurityjwt.repositories.RoleRepository;
import org.example.springbootsecurityjwt.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationService authenticationService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }


    @PostMapping("/signup")
    public RedirectView registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) Set<String> role) {

        // Check if email is already in use
        if (userRepository.existsByEmail(email)) {
            return new RedirectView("/signup?error=email_taken");
        }

        // Create new user's account
        User user = new User(username, email, encoder.encode(password));

        // Set user roles
        Set<String> strRoles = role != null && !role.isEmpty() ? role : new HashSet<>(Collections.singletonList("admin"));
        Set<Role> roles = new HashSet<>();
        System.out.println(strRoles);

        if (strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(r -> {
                switch (r) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                        break;
                    case "club":
                        Role modRole = roleRepository.findByName(ERole.ROLE_CLUB)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        // Save the user with the assigned roles
        user.setRoles(roles);
        userRepository.save(user);
        return new RedirectView("/login");
    }
}
