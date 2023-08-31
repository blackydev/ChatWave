package com.chatwave.chatservice.controller;

import com.chatwave.chatservice.domain.MessageMapper;
import com.chatwave.chatservice.domain.dto.GetMessagesRequest;
import com.chatwave.chatservice.domain.dto.MessageResponse;
import com.chatwave.chatservice.domain.dto.SendMessageRequest;
import com.chatwave.chatservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService service;
    private final MessageMapper mapper;

    @GetMapping("/{memberId}")
    public List<MessageResponse> getMessages(@Valid @RequestBody GetMessagesRequest getMessagesRequest, @AuthenticationPrincipal Integer authorId, @PathVariable Integer memberId) {
        return service
                .getMessages(authorId, memberId, getMessagesRequest.from(), getMessagesRequest.newer())
                .parallelStream()
                .map(message -> mapper.toMessageResponse(message))
                .collect(Collectors.toList());
    }

    @PostMapping("/{receiverId}")
    public void sendMessage(@Valid @RequestBody SendMessageRequest sendMessageRequest, @AuthenticationPrincipal Integer authorId, @PathVariable Integer receiverId) {
        var message = mapper.toMessage(sendMessageRequest, authorId, receiverId);
        service.sendMessage(message);
    }
}
