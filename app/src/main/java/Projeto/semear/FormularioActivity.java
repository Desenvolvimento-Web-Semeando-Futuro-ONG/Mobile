package Projeto.semear;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FormularioActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://back-end-n1cl.onrender.com";
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

        // Filtro Nome
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

        // Máscara Telefone
        edtTelefone.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        edtTelefone.addTextChangedListener(new MaskWatcher("(##) #####-####", edtTelefone));

        // Máscara CPF
        edtCpf.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        edtCpf.addTextChangedListener(new MaskWatcher("###.###.###-##", edtCpf));

        // Filtro Skills: letras, espaço, hífen, vírgula, acentos e ç/Ç
        edtSkills.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        char c = source.charAt(i);
                        if (!(Character.isLetter(c) || c == ' ' || c == '-' || c == ',' || c == 'ç' || c == 'Ç'
                                || String.valueOf(c).matches("[áàâãéèêíïóôõöúçñÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ]"))) {
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
                    json.put("Nome", edtNome.getText().toString().trim());
                    // CPF e Telefone só com números!
                    json.put("CPF", edtCpf.getText().toString().replaceAll("[^0-9]", ""));
                    json.put("Email", edtEmail.getText().toString().trim());
                    json.put("Telefone", edtTelefone.getText().toString().replaceAll("[^0-9]", ""));
                    json.put("Habilidades", edtSkills.getText().toString().trim());

                    // Monta a string de disponibilidade
                    StringBuilder disponibilidade = new StringBuilder();
                    if (cbManha.isChecked()) disponibilidade.append("manhã, ");
                    if (cbTarde.isChecked()) disponibilidade.append("tarde, ");
                    if (cbNoite.isChecked()) disponibilidade.append("noite, ");
                    if (disponibilidade.length() > 0)
                        disponibilidade.setLength(disponibilidade.length() - 2);

                    json.put("Disponibilidade", disponibilidade.toString());

                    // Campos opcionais: pode enviar null se não for usar
                    json.put("ProjetoId", JSONObject.NULL); // ou um número, se quiser
                    json.put("FuncaoDesejada", JSONObject.NULL);

                    // Envio POST para o endpoint correto
                    new ApiTask().execute(
                            BASE_URL + "/api/Voluntario/cadastrar",
                            json.toString()
                    );
                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao montar JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // --- Validação ---
    private boolean validarCampos() {
        boolean valido = true;

        String nome = edtNome.getText().toString().trim();
        if (nome.isEmpty()) {
            edtNome.setError("Campo obrigatório");
            valido = false;
        } else if (!nome.matches("([A-Za-zÀ-ÿçÇ]+[\\s-]?)+")) {
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
        String regex = "^(?!.*[._-]{2})[A-Za-z0-9][A-Za-z0-9._-]{0,62}[A-Za-z0-9]@[A-Za-z0-9][A-Za-z0-9.-]{0,253}[A-Za-z0-9]\\.[A-Za-z]{2,}$";
//        boolean valido = true;
        if (email.isEmpty()) {
            edtEmail.setError("Campo obrigatório");
            valido = false;
        } else if (!email.matches(regex)) {
            edtEmail.setError("E-mail inválido. Use apenas letras, números, ponto, -, _ e respeite a estrutura correta.");
            valido = false;
        } else {
            edtEmail.setError(null);
        }

        String skills = edtSkills.getText().toString().trim();
        if (skills.isEmpty() || !temDuasPalavras(skills)) {
            edtSkills.setError("Informe ao menos 2 características suas");
            valido = false;
        } else if (!skills.matches("^[A-Za-zÀ-ÿçÇ ,\\-]+$")) {
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
        String[] partes = texto.trim().split("[\\s,]+");
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

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
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

    private class ApiTask extends android.os.AsyncTask<String, Void, ApiResponse> {
        @Override
        protected ApiResponse doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(params[1].getBytes(StandardCharsets.UTF_8));
                }

                int statusCode = connection.getResponseCode();
                StringBuilder response = new StringBuilder();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(statusCode < 400 ?
                                connection.getInputStream() : connection.getErrorStream()));

                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                return new ApiResponse(statusCode, response.toString());

            } catch (Exception e) {
                return new ApiResponse(500, "Exception: " + e.getMessage());
            } finally {
                if (connection != null) connection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(ApiResponse response) {
            if (response.statusCode >= 200 && response.statusCode < 300) {
                Toast.makeText(FormularioActivity.this, "Cadastro realizado!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FormularioActivity.this, LandingPageActivity.class));
                finish();
            } else {
                Toast.makeText(FormularioActivity.this,
                        "Erro ao enviar: " + response.statusCode + "\n" + response.body,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class ApiResponse {
        int statusCode;
        String body;
        ApiResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }

}
