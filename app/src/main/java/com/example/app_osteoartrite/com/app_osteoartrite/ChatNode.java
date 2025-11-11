package com.example.app_osteoartrite.com.app_osteoartrite;

import java.util.List;

public class ChatNode {
    String id;          // Identificador único (ex: "root", "sintomas_principais")
    String message;     // Mensagem que o bot exibe (pode conter HTML básico)
    List<ChatOption> options; // Lista de opções para o usuário

    // Construtor vazio (opcional)
    public ChatNode() {}

    // Getters e Setters (importante para manipulação)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<ChatOption> getOptions() { return options; }
    public void setOptions(List<ChatOption> options) { this.options = options; }
}