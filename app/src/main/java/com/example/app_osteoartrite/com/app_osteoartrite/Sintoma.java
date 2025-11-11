// Sintoma.java
package com.example.app_osteoartrite.com.app_osteoartrite; // Use o seu pacote

public class Sintoma {
    // Título foi removido, será gerado pelo Adapter
    private String descricao;
    private String localCorpo;
    private String data;
    private String nivelDor; // "Leve", "Média", "Grave"
    private String documentId;

    // Construtor VAZIO - OBRIGATÓRIO para o Firestore
    public Sintoma() {
    }

    // Construtor principal
    public Sintoma(String descricao, String localCorpo, String data, String nivelDor) {
        this.descricao = descricao;
        this.localCorpo = localCorpo;
        this.data = data;
        this.nivelDor = nivelDor;
    }

    public String getDescricao() { return descricao; }
    public String getLocalCorpo() { return localCorpo; }
    public String getData() { return data; }
    public String getNivelDor() { return nivelDor; }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}