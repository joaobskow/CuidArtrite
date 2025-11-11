package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Informacoes extends AppCompatActivity {

    private MaterialButton btnajuda;
    private ImageButton btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informacoes);

        View mainView = findViewById(R.id.main);
        final int originalPaddingLeft = mainView.getPaddingLeft();
        final int originalPaddingRight = mainView.getPaddingRight();

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    originalPaddingLeft + systemBars.left,
                    systemBars.top,
                    originalPaddingRight + systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        // Botão de ajuda
        btnajuda = findViewById(R.id.btnajuda);
        btnajuda.setOnClickListener(v -> {
            Intent intent = new Intent(Informacoes.this, chat.class);
            startActivity(intent);
        });

        // ✅ Botão de voltar funcionando
        btnVoltar = findViewById(R.id.Btn_Voltar);
        btnVoltar.setOnClickListener(v -> onBackPressed());
    }
}
