package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.User;
import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.apache.commons.lang.Validate.notNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {
    private SessionRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(User user) {
        var session = new Session(user);

        repository.save(session);
        log.info("new session has been created: " + session.getId());

        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session refreshSession(String refreshToken)  {
        var session = repository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid refresh token."));

        if(!session.isAccessTokenExpired() || session.isExpired()) {
            log.info("User tried to use expired token. Possible theft of refresh token from a user. SessionId: " + session.getId());
            throw new ResponseStatusException(BAD_REQUEST, "Invalid refresh token.");
        }

        session.refreshTokens();
        repository.save(session);

        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Session> getUserCurrentSessions(Integer userId) {
        return repository.findAllNotExpiredByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expireAllUserSessions(Integer userId) {
        var sessionList = repository.findAllNotExpiredByUserId(userId);
        if(sessionList.isEmpty()) return;

        for(var session : sessionList)
            session.expire();

        repository.saveAll(sessionList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expireUserSession(Integer userId, Long sessionId) {
        var session = repository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "The session with given ID does not exist."));

        var sessionUserId = session.getUser().getId();

        if(!sessionUserId.equals(userId)) {
            log.info("user with id " + userId + " tried to expire other user's session with id " + sessionId);
            throw new ResponseStatusException(NOT_FOUND, "The session with given ID does not exist.");
        }

        if(session.isExpired())
            throw new ResponseStatusException(BAD_REQUEST, "The session has been already expired.");

        session.expire();
        repository.save(session);
    }

    @Autowired
    public void setRepository(SessionRepository repository) {
        notNull(repository, "SessionRepository can not be null!");
        this.repository = repository;
    }
}