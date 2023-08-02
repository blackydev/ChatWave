package com.chatwave.chatservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateMessageRequest(
        @NotNull(message = "The receiverId can not be null.")
        Integer receiverId,
        @NotEmpty(message = "The message can not be empty.")
        @Length(max = 2000, message = "The message can have at most 2000 characters.")
        String message
) {}
