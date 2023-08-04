package com.chatwave.chatservice.domain;

import com.chatwave.chatservice.domain.dto.MessageResponse;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);
    @Mapping(source = "sendMessageRequest.message", target = "content")
    Message toMessage(SendMessageRequest sendMessageRequest, Integer authorId, Integer receiverId);

    MessageResponse toMessageResponse(Message message);
}
