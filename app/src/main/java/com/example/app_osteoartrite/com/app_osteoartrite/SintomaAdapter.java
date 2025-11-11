package com.example.app_osteoartrite.com.app_osteoartrite; // Use o seu pacote

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // IMPORTADO
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SintomaAdapter extends RecyclerView.Adapter<SintomaAdapter.SintomaViewHolder> {

    private List<Sintoma> sintomasList;
    private Context context;

    // --- 1. Definir uma interface "ouvinte" ---
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDeleteClick(String documentId, int position);
    }
    // ------------------------------------------

    // --- 2. Atualizar o construtor ---
    public SintomaAdapter(Context context, List<Sintoma> sintomasList, OnItemDeleteListener deleteListener) {
        this.context = context;
        this.sintomasList = sintomasList;
        this.deleteListener = deleteListener; // ADICIONADO
    }
    // -------------------------------------

    @NonNull
    @Override
    public SintomaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card (item_sintoma_card.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_sintoma_card, parent, false);
        return new SintomaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SintomaViewHolder holder, int position) {
        // Pega o sintoma da posição atual
        Sintoma sintoma = sintomasList.get(position);

        // Gera o título dinamicamente (Ex: "Sintoma 1")
        holder.tvTitulo.setText("Sintoma " + (position + 1));

        // Preenche o resto dos TextViews com os dados
        holder.tvDescricao.setText(sintoma.getDescricao());
        holder.tvData.setText(sintoma.getData());
        holder.tvLocal.setText(sintoma.getLocalCorpo());
        holder.tvNivelDor.setText(sintoma.getNivelDor());

        // --- LÓGICA DAS CORES (permanece igual) ---
        String nivel = sintoma.getNivelDor();
        int drawableResId;
        int corTexto = Color.WHITE;

        switch (nivel.toLowerCase()) {
            case "leve":
                drawableResId = R.drawable.badge_background_leve;
                break;
            case "média":
            case "media":
                drawableResId = R.drawable.badge_background_media;
                corTexto = ContextCompat.getColor(context, R.color.texto_amarelo); // Texto preto
                break;
            case "grave":
                drawableResId = R.drawable.badge_background_grave;
                break;
            default:
                drawableResId = R.drawable.badge_background_padrao;
                break;
        }

        holder.tvNivelDor.setBackground(ContextCompat.getDrawable(context, drawableResId));
        holder.tvNivelDor.setTextColor(corTexto);

        // --- 3. Adicionar o listener de clique no ícone ---
        holder.ivDelete.setOnClickListener(v -> {
            // Verifica se o listener não é nulo e se o ID do documento existe
            if (deleteListener != null && sintoma.getDocumentId() != null) {
                // Chama a função da interface, passando o ID e a posição
                deleteListener.onDeleteClick(sintoma.getDocumentId(), holder.getAdapterPosition());
            }
        });
        // --------------------------------------------------
    }

    @Override
    public int getItemCount() {
        return sintomasList.size();
    }

    // --- 4. Atualizar o ViewHolder ---
    public static class SintomaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescricao, tvData, tvLocal, tvNivelDor;
        ImageView ivDelete; // <-- ADICIONADO

        public SintomaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.sintoma_titulo);
            tvDescricao = itemView.findViewById(R.id.sintoma_descricao_valor); // ID do valor
            tvData = itemView.findViewById(R.id.sintoma_data_valor);         // ID do valor
            tvLocal = itemView.findViewById(R.id.sintoma_local_valor);       // ID do valor
            tvNivelDor = itemView.findViewById(R.id.sintoma_nivel_dor_badge);

            ivDelete = itemView.findViewById(R.id.sintoma_delete_icon); // <-- ADICIONADO
        }
    }
    // ----------------------------------
}