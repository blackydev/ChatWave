package com.chatwave.authservice.service;

import com.chatwave.authservice.domain.session.Session;
import com.chatwave.authservice.domain.user.UserAuthentication;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface SessionService {
    /**
     * Creates session for user with given id.
     *
     * @param userId
     * @return new session
     * */
    Session createSession(Integer userId);

    /**
     * Searches session by refresh token.
     * Creates new refresh and access tokens.
     *
     * @param refreshToken refresh token id
     * @return new refresh token
     */
    Session refreshSession(String refreshToken);

    /**
     * Gets accessToken from User-Authorization header.
     * Searches session by accessToken.
     * Creates UserAuthentication.
     *
     * @param request with User-Authorization header with accessToken
     * @return user's authentication data
     */
    UserAuthentication getAuthentication(HttpServletRequest request);

    /**
     * Gets all not expired user's sessions.
     *
     * @param userId
     * @return user's sessions
     */
    List<Session> getNotExpiredSessionsByUserId(Integer userId);

    /**
     * Finds not expired session of specified users.
     * Expires these sessions.
     *
     * @param userId
     */
    void expireSessionsByUserId(Integer userId);

    /**
     * Expires session of specified user.
     *
     * @param sessionId
     * @param userId check value used to ensure that the session is owned by the user
     */
    void expireSession(Long sessionId, Integer userId);
}
