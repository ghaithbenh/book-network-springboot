package com.kira.book.auth;

import com.kira.book.email.EmailService;
import com.kira.book.email.EmailTemplate;
import com.kira.book.role.RoleRepository;
import com.kira.book.security.JwtService;
import com.kira.book.user.Token;
import com.kira.book.user.TokenRepository;
import com.kira.book.user.User;
import com.kira.book.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(@Valid RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("role user not found"));

        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(user.getEmail(), user.getFullName(), EmailTemplate.ACTIVATE_ACCOUNT ,activationUrl,newToken,
                "account activation"
                );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {
        var auth=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims=new HashMap<String,Object>();
        var user=((User)auth.getPrincipal());
        claims.put("fullname",user.getFullName());
        var jwtToken=jwtService.generateToken(claims,user);

        return  AuthenticationResponse.builder().Token(jwtToken).build();

    }
//@Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken=tokenRepository.findByToken(token).orElseThrow(()->new RuntimeException("invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("activation token has expired");
        }
        var user=userRepository.findById(savedToken.getUser().getId()).orElseThrow(()->new UsernameNotFoundException("username not found"));
    user.setEnabled(true) ;
    userRepository.save(user);
    savedToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(savedToken);


    }
}