package com.example.app_osteoartrite.com.app_osteoartrite;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AcompanhamentoSintomasActivity extends AppCompatActivity
        implements SintomaAdapter.OnItemDeleteListener {

    private static final String TAG = "AcompanhamentoSintomas";

    private RecyclerView recyclerViewSintomas;
    private SintomaAdapter sintomaAdapter;
    private List<Sintoma> listaDeSintomas;
    private FloatingActionButton fabAddSintoma;
    private TextView tvListaVazia;
    private ImageButton btnVoltarConta; // <-- botão voltar

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acompanhamento);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicialização de views
        recyclerViewSintomas = findViewById(R.id.sintomas_recycler_view);
        fabAddSintoma = findViewById(R.id.fab_add_sintoma);
        tvListaVazia = findViewById(R.id.tv_lista_vazia);
        btnVoltarConta = findViewById(R.id.btnVoltarConta);

        // Configuração da lista
        listaDeSintomas = new ArrayList<>();
        recyclerViewSintomas.setLayoutManager(new LinearLayoutManager(this));
        sintomaAdapter = new SintomaAdapter(this, listaDeSintomas, this);
        recyclerViewSintomas.setAdapter(sintomaAdapter);

        // Botão de adicionar sintoma
        fabAddSintoma.setOnClickListener(v -> {
            Intent intent = new Intent(this, Registrar_Sintoma.class);
            startActivity(intent);
        });

        btnVoltarConta.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarSintomasDoFirestore();
    }

    private void carregarSintomasDoFirestore() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) {
            Toast.makeText(this, "Usuário não logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = usuarioAtual.getUid();
        listaDeSintomas.clear();

        db.collection("users").document(uid).collection("sintomas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        tvListaVazia.setVisibility(View.VISIBLE);
                        recyclerViewSintomas.setVisibility(View.GONE);
                    } else {
                        tvListaVazia.setVisibility(View.GONE);
                        recyclerViewSintomas.setVisibility(View.VISIBLE);
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Sintoma sintoma = document.toObject(Sintoma.class);
                            sintoma.setDocumentId(document.getId());
                            listaDeSintomas.add(sintoma);
                        }
                    }
                    sintomaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao carregar sintomas", e);
                    Toast.makeText(this, "Erro ao carregar sintomas.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteClick(String documentId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Sintoma")
                .setMessage("Tem certeza que deseja excluir este sintoma?")
                .setPositiveButton("Excluir", (dialog, which) ->
                        excluirSintomaDoFirestore(documentId, position))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirSintomaDoFirestore(String documentId, int position) {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) return;
        String uid = usuarioAtual.getUid();

        db.collection("users").document(uid).collection("sintomas").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Sintoma excluído.", Toast.LENGTH_SHORT).show();
                    listaDeSintomas.remove(position);
                    sintomaAdapter.notifyItemRemoved(position);
                    sintomaAdapter.notifyItemRangeChanged(position, listaDeSintomas.size());
                    if (listaDeSintomas.isEmpty()) {
                        tvListaVazia.setVisibility(View.VISIBLE);
                        recyclerViewSintomas.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao excluir sintoma.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao excluir sintoma", e);
                });
    }
}
