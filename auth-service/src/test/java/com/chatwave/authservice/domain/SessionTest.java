package com.chatwave.authservice.domain.session;

import com.chatwave.authservice.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {
    private Session session;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        session = new Session(user);
    }

    @Test
    @DisplayName("isExpired() should return false if session is not expired")
    public void t1() {
        assertFalse(session.isExpired());
    }

    @Test
    @DisplayName("isExpired() should return true if session is expired")
    public void t2() {
        session.setExpireDate(LocalDate.now());
        assertTrue(session.isExpired());
    }

    @Test
    @DisplayName("isAccessTokenExpired() should return false if accessToken is not expired")
    public void t3() {
        assertFalse(session.isAccessTokenExpired());
    }

    @Test
    @DisplayName("isAccessTokenExpired() should return true if accessToken is expired")
    public void t4() {
        session.setAccessTokenExpireDate(LocalDateTime.now().minusSeconds(1));
        assertTrue(session.isAccessTokenExpired());
    }

    @Test
    @DisplayName("isAccessTokenExpired() should return true if session is expired")
    public void t5() {
        session.setExpireDate(LocalDate.now());
        assertTrue(session.isAccessTokenExpired());
    }


    @Test
    @DisplayName("expire() should update expire date")
    public void t6() {
        session.expire();
        assertEquals(LocalDate.now(), session.getExpireDate());
    }

    @Test
    @DisplayName("expire() should not update expire date if sessions is already expired")
    public void t7() {
        var yesterday = LocalDate.now().minusDays(1);
        session.setExpireDate(yesterday);
        session.expire();
        assertEquals(yesterday, session.getExpireDate());
    }

    @Test
    @DisplayName("refreshTokens() should generate new tokens and change expire dates")
    public void t8() {
        session.setAccessToken("access");
        session.setRefreshToken("refresh");

        var tomorrow = LocalDate.now().plusDays(1);
        session.setExpireDate(tomorrow);

        var lastHour = LocalDateTime.now().minusHours(1);
        session.setAccessTokenExpireDate(lastHour);

        session.refreshTokens();

        assertNotEquals("access", session.getAccessToken());
        assertNotEquals("refresh", session.getRefreshToken());

        assertNotEquals(tomorrow, session.getAccessTokenExpireDate());
        assertNotEquals(lastHour, session.getExpireDate());
    }
}
