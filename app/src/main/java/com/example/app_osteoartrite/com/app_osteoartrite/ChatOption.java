package com.example.app_osteoartrite.com.app_osteoartrite;

public class ChatOption {
    String text;         // Texto da opção (ex: "✅ Entendendo a condição")
    String nextNodeId;   // ID do próximo nó se esta opção for escolhida

    public ChatOption(String text, String nextNodeId) {
        this.text = text;
        this.nextNodeId = nextNodeId;
    }
    // Getters podem ser adicionados se necessário
}