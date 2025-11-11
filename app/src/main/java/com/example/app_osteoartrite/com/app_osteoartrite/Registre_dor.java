package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton; // <-- IMPORT CORRIGIDO
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Registre_dor extends AppCompatActivity {

    // Componentes de UI
    private ImageButton btnVoltar;
    private EditText editDescricaoDor;
    private SeekBar seekBarDor;
    private TextView tvNivelDorReal;
    private ViewFlipper viewFlipperCorpo;
    private MaterialButton btnSalvar;

    private ImageButton btnAjudaFloating;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Detector de Gestos
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registre_dor);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Conectar Views
        btnVoltar = findViewById(R.id.btnVoltar);
        editDescricaoDor = findViewById(R.id.editDescricaoDor);
        seekBarDor = findViewById(R.id.seekBarDor);
        tvNivelDorReal = findViewById(R.id.tvNivelDor);
        viewFlipperCorpo = findViewById(R.id.viewFlipperCorpo);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnAjudaFloating = findViewById(R.id.btnAjudaFloating);

        // Configurar Listeners
        setupSeekBar();
        setupViewFlipper();
        setupClickListeners();
    }

    private void setupSeekBar() {
        tvNivelDorReal.setText(String.valueOf(seekBarDor.getProgress()));
        seekBarDor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvNivelDorReal.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupViewFlipper() {
        gestureDetector = new GestureDetector(this, new SwipeGestureListener(this, viewFlipperCorpo));
        viewFlipperCorpo.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void setupClickListeners() {
        btnVoltar.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> salvarRegistroDor());

        // --- CORREÇÃO DE CRASH 3: Usar a variável correta ---
        btnAjudaFloating.setOnClickListener(v -> {
            Intent intent = new Intent(Registre_dor.this, chat.class);
            startActivity(intent);
        });
    }

    private void salvarRegistroDor() {
        FirebaseUser usuarioAtual = mAuth.getCurrentUser();
        if (usuarioAtual == null) {
            Toast.makeText(this, "Erro: Usuário não logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String descricao = editDescricaoDor.getText().toString().trim();
        int nivelDor = seekBarDor.getProgress();

        // Força o Locale para "pt", "BR" para consistência
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
        sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        String dataRegistro = sdf.format(new Date());

        if (descricao.isEmpty()) {
            editDescricaoDor.setError("Descreva sua dor, por favor.");
            return;
        }

        Map<String, Object> registroDor = new HashMap<>();
        registroDor.put("descricao", descricao);
        registroDor.put("nivelDor", nivelDor);
        registroDor.put("data", dataRegistro);

        String uid = usuarioAtual.getUid();
        db.collection("users").document(uid).collection("registros_dor")
                .add(registroDor)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Registro de dor salvo!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar registro.", Toast.LENGTH_SHORT).show();
                });
    }

    // --- CLASSE INTERNA PARA CUIDAR DO SWIPE (GIRO) ---
    private static class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        private ViewFlipper viewFlipper;
        private Context context;

        public SwipeGestureListener(Context context, ViewFlipper viewFlipper) {
            this.context = context;
            this.viewFlipper = viewFlipper;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                if (diffX > 0) {
                    // Swipe para a Direita (Girar para a Esquerda)
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_left));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_right));
                    viewFlipper.showPrevious();
                } else {
                    // Swipe para a Esquerda (Girar para a Direita)
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_right));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_left));
                    viewFlipper.showNext();
                }
                return true;
            }
            return false;
        }
    }
}