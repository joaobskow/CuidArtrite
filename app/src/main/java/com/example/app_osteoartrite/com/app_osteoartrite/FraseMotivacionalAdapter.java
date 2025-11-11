package com.example.app_osteoartrite.com.app_osteoartrite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FraseMotivacionalAdapter extends RecyclerView.Adapter<FraseMotivacionalAdapter.FraseViewHolder> {

    private List<String> frases;

    public FraseMotivacionalAdapter(List<String> frases) {
        this.frases = frases;
    }

    @NonNull
    @Override
    public FraseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frase_motivacional, parent, false);
        return new FraseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FraseViewHolder holder, int position) {
        String fraseCompleta = frases.get(position);
        String[] partes = fraseCompleta.split("---"); // Usaremos "---" para separar frase e autor

        holder.tvFrase.setText(partes[0].trim());
        if (partes.length > 1) {
            holder.tvAutor.setText("- " + partes[1].trim());
            holder.tvAutor.setVisibility(View.VISIBLE);
        } else {
            holder.tvAutor.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return frases.size();
    }

    static class FraseViewHolder extends RecyclerView.ViewHolder {
        TextView tvFrase;
        TextView tvAutor;

        public FraseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFrase = itemView.findViewById(R.id.tvFrase);
            tvAutor = itemView.findViewById(R.id.tvAutor);
        }
    }
}