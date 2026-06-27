package com.bluesoftware.petshop.services;

import com.bluesoftware.petshop.dtos.*;
import com.bluesoftware.petshop.entities.Role;
import com.bluesoftware.petshop.entities.User;
import com.bluesoftware.petshop.entities.Veterinarian;
import com.bluesoftware.petshop.exceptions.BadRequestException;
import com.bluesoftware.petshop.repositories.UserRepository;
import com.bluesoftware.petshop.repositories.VeterinarianRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bluesoftware.petshop.security.JwtUtils;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       VeterinarianRepository veterinarianRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email o contraseña inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Email o contraseña inválidos");
        }

        if (!user.isActive()) {
            throw new BadCredentialsException(
                user.getRole() == Role.VETERINARIAN
                    ? "Cuenta pendiente de aprobación por el administrador"
                    : "Cuenta desactivada");
        }

        String token = generateToken(user.getEmail());
        return new AuthResponse(token, mapToUserResponse(user));
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        if (role == Role.VETERINARIAN) {
            if (request.getMatricula() == null || request.getMatricula().isBlank()) {
                throw new BadRequestException("La matrícula profesional es obligatoria para veterinarios");
            }
            if (veterinarianRepository.existsByMatricula(request.getMatricula())) {
                throw new BadRequestException("La matrícula ya está registrada");
            }
            if (request.getSlug() == null || request.getSlug().isBlank()) {
                throw new BadRequestException("El slug es obligatorio para definir tu subdominio");
            }
            if (veterinarianRepository.existsBySlug(request.getSlug())) {
                throw new BadRequestException("El slug ya está en uso");
            }
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(role != Role.VETERINARIAN)
                .build();

        user = userRepository.save(user);

        if (role == Role.VETERINARIAN) {
            veterinarianRepository.save(Veterinarian.builder()
                    .matricula(request.getMatricula())
                    .slug(request.getSlug())
                    .user(user)
                    .approved(false)
                    .build());

            return new AuthResponse(null, mapToUserResponse(user));
        }

        String token = generateToken(user.getEmail());
        return new AuthResponse(token, mapToUserResponse(user));
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));
        return mapToUserResponse(user);
    }

    private String generateToken(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return jwtUtils.generateToken(userDetails);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
