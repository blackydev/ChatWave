package com.chatwave.authservice.unit.domain;

import com.chatwave.authservice.domain.dto.request.AuthenticationRequest;
import com.chatwave.authservice.domain.dto.request.RegisterRequest;
import com.chatwave.authservice.domain.user.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.chatwave.authservice.utils.TestVariables.LOGIN_NAME;
import static com.chatwave.authservice.utils.TestVariables.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UserMapper")
public class UserMapperTest {
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    @DisplayName("toUser(RegisterRequest) should map createUserRequest to user entity")
    public void t1() {
        var createUserRequest = new RegisterRequest(LOGIN_NAME, PASSWORD);

        var user = mapper.toUser(createUserRequest);

        assertNull(user.getId());
        assertEquals(LOGIN_NAME, user.getLoginName());
        assertEquals(PASSWORD, user.getPassword());
    }

    @Test
    @DisplayName("toUser(AuthenticationRequest) should map authenticateUserRequest to user entity")
    public void t2() {
        var authenticateUserRequest = new AuthenticationRequest(LOGIN_NAME, PASSWORD);

        var user = mapper.toUser(authenticateUserRequest);

        assertNull(user.getId());
        assertEquals(LOGIN_NAME, user.getLoginName());
        assertEquals(PASSWORD, user.getPassword());
    }
}
