package com.ischuros.chat.controller;

import com.ischuros.chat.model.ChatMessage;
import com.ischuros.chat.model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

	private final SimpMessageSendingOperations messagingTemplate;

	@Autowired
	public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		LOGGER.info("Received a new web socket connection");
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

		String userName = (String) headerAccessor.getSessionAttributes().get("username");

		if (userName == null) {
			return;
		}

		LOGGER.info("User disconnected : {}", userName);

		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setType(MessageType.LEAVE);
		chatMessage.setSender(userName);

		messagingTemplate.convertAndSend("/topic/public", chatMessage);
	}

}
