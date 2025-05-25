package Projeto.semear;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.Locale;

public class FormularioActivity extends AppCompatActivity {

    private EditText edtNome, edtTelefone, edtCpf, edtEmail, edtSkills;
    private CheckBox cbManha, cbTarde, cbNoite;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formulario_page);

        edtNome = findViewById(R.id.input_nome);
        edtTelefone = findViewById(R.id.input_telefone);
        edtCpf = findViewById(R.id.input_cpf);
        edtEmail = findViewById(R.id.email1);
        edtSkills = findViewById(R.id.caixa_skills);

        cbManha = findViewById(R.id.manha);
        cbTarde = findViewById(R.id.tarde);
        cbNoite = findViewById(R.id.noite);

        btnEnviar = findViewById(R.id.btn_enviar2);

        // Filtro para bloquear números e caracteres especiais no nome, mas aceitar letras, espaços e hífen
        edtNome.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(50),
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        char c = source.charAt(i);
                        if (!Character.isLetter(c) && c != ' ' && c != '-') {
                            return "";
                        }
                    }
                    return null;
                }
        });

        edtNome.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private int cursorPos = 0;
            private String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isUpdating) {
                    cursorPos = edtNome.getSelectionStart();
                    oldText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                isUpdating = true;
                String texto = s.toString();
                String capitalizado = capitalizeWords(texto);

                if (!capitalizado.equals(texto)) {
                    int diff = capitalizado.length() - oldText.length();
                    int newCursorPos = cursorPos + diff;
                    if (newCursorPos < 0) newCursorPos = 0;
                    if (newCursorPos > capitalizado.length()) newCursorPos = capitalizado.length();

                    edtNome.setText(capitalizado);
                    edtNome.setSelection(newCursorPos);
                }
                isUpdating = false;
            }
        });

        // Máscara Telefone (xx) xxxxx-xxxx
        edtTelefone.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        edtTelefone.addTextChangedListener(new MaskWatcher("(##) #####-####", edtTelefone));

        // Máscara CPF xxx.xxx.xxx-xx
        edtCpf.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        edtCpf.addTextChangedListener(new MaskWatcher("###.###.###-##", edtCpf));

        // Filtro para skills (letras, espaço e hífens)
        edtSkills.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        char c = source.charAt(i);
                        if (!Character.isLetter(c) && c != ' ' && c != '-') {
                            return "";
                        }
                    }
                    return null;
                }
        });

        btnEnviar.setOnClickListener(v -> {
            if (validarCampos()) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("nomeCompleto", edtNome.getText().toString().trim());
                    json.put("telefone", edtTelefone.getText().toString().trim());
                    json.put("cpf", edtCpf.getText().toString().trim());
                    json.put("email", edtEmail.getText().toString().trim());
                    json.put("skills", edtSkills.getText().toString().trim());

                    JSONObject turnos = new JSONObject();
                    turnos.put("manha", cbManha.isChecked());
                    turnos.put("tarde", cbTarde.isChecked());
                    turnos.put("noite", cbNoite.isChecked());
                    json.put("disponibilidade", turnos);

                    // Exibindo a mensagem de sucesso
                    Toast.makeText(this, "Dados enviados com sucesso!", Toast.LENGTH_LONG).show();

                    // Redireciona para a página inicial
                    Intent intent = new Intent(FormularioActivity.this, LandingPageActivity.class); // ou MainActivity.class
                    startActivity(intent);
                    finish(); // Fecha a activity atual
                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao montar JSON", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validarCampos() {
        boolean valido = true;

        String nome = edtNome.getText().toString().trim();
        if (nome.isEmpty()) {
            edtNome.setError("Campo obrigatório");
            valido = false;
        } else if (!nome.matches("([A-Za-zÀ-ÿ]+[\\s-]?)+")) {
            edtNome.setError("Nome inválido, sem números");
            valido = false;
        } else {
            edtNome.setError(null);
        }

        String telefone = edtTelefone.getText().toString().trim();
        if (telefone.isEmpty()) {
            edtTelefone.setError("Campo obrigatório");
            valido = false;
        } else if (!telefone.matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
            edtTelefone.setError("Telefone inválido, use (xx) xxxxx-xxxx");
            valido = false;
        } else {
            edtTelefone.setError(null);
        }

        String cpf = edtCpf.getText().toString().trim();
        if (cpf.isEmpty()) {
            edtCpf.setError("Campo obrigatório");
            valido = false;
        } else if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            edtCpf.setError("CPF inválido, use xxx.xxx.xxx-xx");
            valido = false;
        } else {
            edtCpf.setError(null);
        }

        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            edtEmail.setError("Campo obrigatório");
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email inválido");
            valido = false;
        } else {
            edtEmail.setError(null);
        }

        String skills = edtSkills.getText().toString().trim();
        if (skills.isEmpty() || !temDuasPalavras(skills)) {
            edtSkills.setError("Informe ao menos 2 características suas");
            valido = false;
        } else if (!skills.matches("([A-Za-zÀ-ÿ]+[\\s-]?)+")) {
            edtSkills.setError("Skills inválidas, sem números ou caracteres especiais");
            valido = false;
        } else {
            edtSkills.setError(null);
        }

        if (!cbManha.isChecked() && !cbTarde.isChecked() && !cbNoite.isChecked()) {
            Toast.makeText(this, "Selecione pelo menos um turno de disponibilidade", Toast.LENGTH_LONG).show();
            valido = false;
        }

        return valido;
    }

    private boolean temDuasPalavras(String texto) {
        String[] partes = texto.trim().split("\\s+");
        return partes.length >= 2;
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;

        StringBuilder capitalized = new StringBuilder();
        boolean capitalizeNext = true;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                capitalized.append(c);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                capitalized.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                capitalized.append(Character.toLowerCase(c));
            }
        }
        return capitalized.toString();
    }

    private static class MaskWatcher implements android.text.TextWatcher {
        private final String mask;
        private boolean isUpdating;
        private EditText editText;

        public MaskWatcher(String mask, EditText editText) {
            this.mask = mask;
            this.editText = editText;
            this.isUpdating = false;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;
            isUpdating = true;

            String str = unmask(s.toString());
            StringBuilder masked = new StringBuilder();
            int i = 0;

            for (char m : mask.toCharArray()) {
                if (m != '#') {
                    masked.append(m);
                } else {
                    if (i < str.length()) {
                        masked.append(str.charAt(i));
                        i++;
                    } else {
                        break;
                    }
                }
            }

            editText.setText(masked.toString());
            editText.setSelection(masked.length());

            isUpdating = false;
        }

        private String unmask(String s) {
            return s.replaceAll("[^\\d]", "");
        }
    }
}