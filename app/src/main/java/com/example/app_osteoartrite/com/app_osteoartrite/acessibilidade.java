package com.example.app_osteoartrite.com.app_osteoartrite;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.Locale;

public class acessibilidade extends AppCompatActivity {

    private MaterialButtonToggleGroup toggleGroupTema, toggleGroupFonte;
    private com.google.android.material.button.MaterialButton btnSalvarAlteracoes;
    private android.widget.Spinner spinnerIdioma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acessibilidade);

        toggleGroupTema = findViewById(R.id.toggleGroupTema);
        toggleGroupFonte = findViewById(R.id.toggleGroupFonte);
        spinnerIdioma = findViewById(R.id.spinnerIdioma);
        btnSalvarAlteracoes = findViewById(R.id.btnSalvarAlteracoes);

        // Configuração do Spinner de Idiomas
        String[] idiomas = {"Português", "Inglês"};
        android.widget.ArrayAdapter<String> adapterIdiomas = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, idiomas);
        adapterIdiomas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIdioma.setAdapter(adapterIdiomas);

        // Carregar preferências salvas
        carregarConfiguracoes();

        // Botão salvar
        btnSalvarAlteracoes.setOnClickListener(v -> salvarConfiguracoes());

        // Botão voltar
        ImageButton Voltar = findViewById(R.id.Btn_Voltar);
        Voltar.setOnClickListener(v -> finish());
    }

    private void salvarConfiguracoes() {
        SharedPreferences prefs = getSharedPreferences("config_acessibilidade", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Tema
        int checkedTemaId = toggleGroupTema.getCheckedButtonId();
        String temaSelecionado = (checkedTemaId == R.id.btnTemaDark) ? "Dark" : "Light";
        editor.putString("tema", temaSelecionado);

        // Idioma
        String idiomaSelecionado = spinnerIdioma.getSelectedItem().toString();
        editor.putString("idioma", idiomaSelecionado);

        // Fonte (Normal ou Negrito)
        int checkedFonteId = toggleGroupFonte.getCheckedButtonId();
        String fonteSelecionada = (checkedFonteId == R.id.btnFonteNegrito) ? "Negrito" : "Normal";
        editor.putString("fonte", fonteSelecionada);

        editor.apply();

        aplicarTema(temaSelecionado);
        aplicarIdioma(idiomaSelecionado);
        aplicarFonte(fonteSelecionada);

        Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show();
    }

    private void carregarConfiguracoes() {
        SharedPreferences prefs = getSharedPreferences("config_acessibilidade", MODE_PRIVATE);

        // Tema
        String tema = prefs.getString("tema", "Light");
        if (tema.equals("Dark")) toggleGroupTema.check(R.id.btnTemaDark);
        else toggleGroupTema.check(R.id.btnTemaLight);

        // Idioma
        String idioma = prefs.getString("idioma", "Português");
        spinnerIdioma.setSelection(idioma.equals("Inglês") ? 1 : 0);

        // Fonte
        String fonte = prefs.getString("fonte", "Normal");
        if (fonte.equals("Negrito"))
            toggleGroupFonte.check(R.id.btnFonteNegrito);
        else
            toggleGroupFonte.check(R.id.btnFonteNormal);
    }

    private void aplicarTema(String tema) {
        if (tema.equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void aplicarIdioma(String idioma) {
        Locale locale = idioma.equals("Inglês") ? new Locale("en") : new Locale("pt");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void aplicarFonte(String fonte) {
        // (no momento apenas salva a escolha)
    }
}
