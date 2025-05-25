package Projeto.semear;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Patterns;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import java.text.NumberFormat;
import java.util.Locale;

public class DoacaoActivity extends AppCompatActivity {
    private EditText inputNome, inputCpfCnpj, inputTelefone, inputEmail, inputValor;
    private Button btnEscolherValor, btnEscolherMaterial, btnPix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doacao_page);

        inputNome = findViewById(R.id.input_nome);
        inputCpfCnpj = findViewById(R.id.input_cpf);
        inputTelefone = findViewById(R.id.input_telefone);
        inputEmail = findViewById(R.id.input_email);
        inputValor = findViewById(R.id.input_valor);

        btnEscolherValor = findViewById(R.id.btn_valor);
        btnEscolherMaterial = findViewById(R.id.btn_valor2);
        btnPix = findViewById(R.id.btn_pix);

        // Preenche automaticamente caso existam dados vindos da QrcodeActivity
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("nome"))       inputNome.setText(intent.getStringExtra("nome"));
            if (intent.hasExtra("cpfCnpj"))    inputCpfCnpj.setText(intent.getStringExtra("cpfCnpj"));
            if (intent.hasExtra("telefone"))   inputTelefone.setText(intent.getStringExtra("telefone"));
            if (intent.hasExtra("email"))      inputEmail.setText(intent.getStringExtra("email"));
            if (intent.hasExtra("valor"))      inputValor.setText(intent.getStringExtra("valor"));
        }

        // Botão "Escolher Valor" - Altera cor ao clicar
        btnEscolherValor.setOnClickListener(v -> {
            v.setSelected(true);
            ((Button) v).setTextColor(ContextCompat.getColor(DoacaoActivity.this, R.color.white));
            v.setBackgroundColor(ContextCompat.getColor(DoacaoActivity.this, R.color.orange));
            inputNome.requestFocus();
            InputMethodManager imm = (InputMethodManager) DoacaoActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(inputNome, InputMethodManager.SHOW_IMPLICIT);
        });

        // Botão "Escolher Material" - Abre o WhatsApp
        btnEscolherMaterial.setOnClickListener(v -> abrirWhatsAppComMensagem());

        // Botão "Pix" - Valida e redireciona para o QR Code
        btnPix.setOnClickListener(v -> validarFormularioEIrParaQrcode());

        // Validação de campos
        inputNome.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validarNome();
        });

        inputCpfCnpj.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }
                String clean = s.toString().replaceAll("[^\\d]", "");
                String formatted;
                if (clean.length() <= 11) {
                    formatted = formatCpf(clean);
                } else {
                    clean = clean.substring(0, Math.min(14, clean.length()));
                    formatted = formatCnpj(clean);
                }
                isUpdating = true;
                inputCpfCnpj.setText(formatted);
                inputCpfCnpj.setSelection(formatted.length());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        inputTelefone.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString().replaceAll("\\D", "");
                if (isUpdating) {
                    oldText = str;
                    isUpdating = false;
                    return;
                }
                String formatted = "";
                int len = str.length();
                if (len > 11) {
                    str = str.substring(0, 11);
                    len = 11;
                }
                if (len <= 2) {
                    formatted = "(" + str;
                } else if (len <= 6) {
                    formatted = "(" + str.substring(0, 2) + ") " + str.substring(2);
                } else if (len <= 10) {
                    formatted = "(" + str.substring(0, 2) + ") " + str.substring(2, len - 4) + "-" + str.substring(len - 4);
                } else {
                    formatted = "(" + str.substring(0, 2) + ") " + str.substring(2, 7) + "-" + str.substring(7, 11);
                }
                isUpdating = true;
                inputTelefone.setText(formatted);
                inputTelefone.setSelection(formatted.length());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        inputEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validarEmail();
        });

        inputValor.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    inputValor.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            double parsed = Double.parseDouble(cleanString) / 100;
                            if (parsed > 999999999.00) {
                                parsed = 999999999.00;
                            }
                            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                            String formatted = nf.format(parsed);

                            current = formatted;
                            inputValor.setText(formatted);
                            inputValor.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        current = "";
                        inputValor.setText("");
                    }

                    inputValor.addTextChangedListener(this);
                }
            }
        });
    }

    private void validarFormularioEIrParaQrcode() {
        if (!validarNome()) {
            inputNome.requestFocus();
            return;
        }
        if (!validarCpfCnpj()) {
            inputCpfCnpj.requestFocus();
            return;
        }
        if (!validarTelefone()) {
            inputTelefone.requestFocus();
            return;
        }
        if (!validarEmail()) {
            inputEmail.requestFocus();
            return;
        }
        if (!validarValor()) {
            inputValor.requestFocus();
            return;
        }

        // Redireciona para a página do QR Code
        Intent intent = new Intent(this, QrcodeActivity.class);
        intent.putExtra("nome", inputNome.getText().toString().trim());
        intent.putExtra("cpfCnpj", inputCpfCnpj.getText().toString().trim());
        intent.putExtra("telefone", inputTelefone.getText().toString().trim());
        intent.putExtra("email", inputEmail.getText().toString().trim());
        intent.putExtra("valor", getValorParaBackend()); // Envia o valor formatado para o backend

        startActivity(intent);
        finish();
    }

    // Método para converter o valor formatado para o formato do backend
    private String getValorParaBackend() {
        String valorFormatado = inputValor.getText().toString().trim();
        String valorLimpo = valorFormatado.replaceAll("[^\\d,]", "").replace(",", ".");
        try {
            double valor = Double.parseDouble(valorLimpo);
            return String.format(Locale.US, "%.2f", valor);
        } catch (NumberFormatException e) {
            return "0.00";
        }
    }

    private boolean validarNome() {
        String nome = inputNome.getText().toString().trim();
        if (nome.isEmpty()) {
            inputNome.setError("Campo obrigatório");
            return false;
        }
        if (!nome.matches("^[\\p{L}\\s]{1,50}$")) {
            inputNome.setError("Somente letras (com acentos) e espaços são permitidos");
            return false;
        }
        if (nome.length() > 50) {
            inputNome.setError("Máximo de 50 caracteres");
            return false;
        }
        inputNome.setError(null);
        return true;
    }

    private boolean validarCpfCnpj() {
        String texto = inputCpfCnpj.getText().toString().replaceAll("[^\\d]", "");
        if (texto.length() == 11 || texto.length() == 14) {
            return true;
        } else {
            inputCpfCnpj.setError("Digite um CPF ou CNPJ válido");
            inputCpfCnpj.requestFocus();
            return false;
        }
    }

    private boolean validarTelefone() {
        String telefone = inputTelefone.getText().toString().replaceAll("\\D", "");
        if (telefone.isEmpty()) {
            inputTelefone.setError("Campo obrigatório");
            return false;
        }
        if (telefone.length() != 10 && telefone.length() != 11) {
            inputTelefone.setError("Telefone inválido");
            return false;
        }
        inputTelefone.setError(null);
        return true;
    }

    private boolean validarEmail() {
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty()) {
            inputEmail.setError("Campo obrigatório");
            return false;
        }
        if (!email.equals(email.toLowerCase())) {
            inputEmail.setError("O e-mail não pode conter letras maiúsculas");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Email inválido");
            return false;
        }
        inputEmail.setError(null);
        return true;
    }

    private boolean validarValor() {
        String valorFormatado = inputValor.getText().toString().trim();
        if (valorFormatado.isEmpty()) {
            inputValor.setError("Campo obrigatório");
            return false;
        }

        String valorLimpo = valorFormatado.replaceAll("[^\\d,]", "").replace(",", ".");
        try {
            double valor = Double.parseDouble(valorLimpo);
            if (valor < 1.00) {
                inputValor.setError("Valor mínimo para doação é de R$ 1,00");
                return false;
            }
            if (valor > 999999999.00) {
                inputValor.setError("Valor máximo permitido é 999.999.999,00");
                return false;
            }
        } catch (NumberFormatException e) {
            inputValor.setError("Valor inválido");
            return false;
        }
        inputValor.setError(null);
        return true;
    }

    private String formatCpf(String cpf) {
        if (cpf.length() <= 3) return cpf;
        else if (cpf.length() <= 6) return cpf.substring(0, 3) + "." + cpf.substring(3);
        else if (cpf.length() <= 9) return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6);
        else return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private String formatCnpj(String cnpj) {
        if (cnpj.length() <= 2) return cnpj;
        else if (cnpj.length() <= 5) return cnpj.substring(0, 2) + "." + cnpj.substring(2);
        else if (cnpj.length() <= 8) return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5);
        else if (cnpj.length() <= 12) return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + cnpj.substring(8);
        else return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
    }

    private void abrirWhatsAppComMensagem() {
        String numero = "5581996675144";
        String mensagem = "Olá, gostaria de doar materiais para um dos projetos de vocês!";
        String url = "https://wa.me/" + numero + "?text=" + Uri.encode(mensagem);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.whatsapp");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(fallback);
        }
    }
}