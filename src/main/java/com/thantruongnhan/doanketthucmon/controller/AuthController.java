package com.thantruongnhan.doanketthucmon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thantruongnhan.doanketthucmon.dto.LoginDto;
import com.thantruongnhan.doanketthucmon.dto.RegisterDto;
import com.thantruongnhan.doanketthucmon.entity.User;
import com.thantruongnhan.doanketthucmon.entity.enums.Role;
import com.thantruongnhan.doanketthucmon.payload.response.JwtResponse;
import com.thantruongnhan.doanketthucmon.payload.response.MessageResponse;
import com.thantruongnhan.doanketthucmon.repository.UserRepository;
import com.thantruongnhan.doanketthucmon.security.jwt.JwtUtils;
import com.thantruongnhan.doanketthucmon.security.services.UserDetailsImpl;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
        private final JwtUtils jwtUtils;

        @Autowired
        public AuthController(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtUtils jwtUtils) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.authenticationManager = authenticationManager;
                this.jwtUtils = jwtUtils;
        }

        @PostMapping("register")
        public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
                // Kiểm tra username đã tồn tại
                if (userRepository.existsByUsername(registerDto.getUsername())) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(new MessageResponse("Username is taken!"));
                }

                // Kiểm tra email đã tồn tại
                if (userRepository.existsByEmail(registerDto.getEmail())) {
                        return ResponseEntity
                                        .badRequest()
                                        .body(new MessageResponse("Email is already in use!"));
                }

                User user = new User();
                user.setUsername(registerDto.getUsername());
                user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                user.setEmail(registerDto.getEmail());

                // Luôn set role là USER - không cho phép user tự chọn role khi đăng ký
                user.setRole(Role.USER);

                userRepository.save(user);

                return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        }

        @PostMapping("login")
        public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                                                loginDto.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(item -> item.getAuthority())
                                .toList();

                return ResponseEntity.ok(new JwtResponse(
                                jwt,
                                userDetails.getId(),
                                userDetails.getUsername(),
                                userDetails.getEmail(),
                                roles));
        }
}