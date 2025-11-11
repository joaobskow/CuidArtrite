package com.example.app_osteoartrite.com.app_osteoartrite;

public class ChatMessage {
    private String messageText;
    private boolean isUserMessage;

    public ChatMessage(String messageText, boolean isUserMessage) {
        this.messageText = messageText;
        this.isUserMessage = isUserMessage;
    }

    public String getMessageText() {
        return messageText;
    }

    public boolean isUserMessage() {
        return isUserMessage;
    }
}