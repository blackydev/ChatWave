package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.User;
import com.chatwave.authservice.domain.user.UserAuthentication;
import com.chatwave.authservice.repository.SessionRepository;
import com.chatwave.authservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;
    private final AuthenticationManager authManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserAuthentication getUserAuthentication(HttpServletRequest request) {
        var authHeader = request.getHeader("User-Authorization");
        if(authHeader == null)
            return null;

        if(!authHeader.startsWith("Bearer "))
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken");

        var accessToken = authHeader.substring(7);
        var optionalSession = sessionRepository.findNotExpiredByAccessToken(accessToken);

        if(optionalSession.isEmpty())
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid accessToken");

        var session = optionalSession.get();
        return new UserAuthentication(session, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createUser(User user){
        if(repository.findById(user.getId()).isPresent()) {
            log.warn("Possible data inconsistency! Client tried to create user with busy ID: " + user.getId());
            throw new ResponseStatusException(CONFLICT, "User with given id already exists");
        }

        var encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);

        repository.save(user);
        log.info("new user has been created: " + user.getId());

        return sessionService.createSession(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session authenticateUser(User user) {
        authenticate(user);

        log.info("User has been authenticated: " + user.getId());
        return sessionService.createSession(user);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void patchUser(User user, String newPassword) {
        authenticate(user);
        var founduser = repository.findById(user.getId()).get();

        var encoded = passwordEncoder.encode(newPassword);
        founduser.setPassword(encoded);

        repository.save(founduser);
    }

    /**
     * Authenticates user without creating a session.
     * @param user with id and password
     */
    private void authenticate(User user) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getId(),
                            user.getPassword()
                    )
            );
        } catch(Exception e) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid password");
        }
    }
}
