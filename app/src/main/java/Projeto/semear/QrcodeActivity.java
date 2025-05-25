package Projeto.semear;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class QrcodeActivity extends AppCompatActivity {

    private static final String TAG = "QrcodeActivity";
    private static final String BASE_URL = "http://10.0.2.2:5189"; // Emulador Android Studio

    private TextView tvNome, tvCpfCnpj, tvTelefone, tvEmail, tvValor;
    private ImageView ivQrCode;
    private Button btnVoltar, btnFinalizar;
    private TextView tvQrcodeLink;
    private ImageButton btnCopyLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_page);

        initViews();
        getIntentData();
        setupPixAndQrCode();
        setupButtonListeners();
    }

    private void initViews() {
        tvNome = findViewById(R.id.verifique_nome);
        tvCpfCnpj = findViewById(R.id.verifique_cpf_cnpj);
        tvTelefone = findViewById(R.id.verifique_telefone);
        tvEmail = findViewById(R.id.verifique_email);
        tvValor = findViewById(R.id.verique_valor);
        ivQrCode = findViewById(R.id.ivQrCode);
        btnVoltar = findViewById(R.id.btn_voltar);
        btnFinalizar = findViewById(R.id.btn_finalizar);
        tvQrcodeLink = findViewById(R.id.text_qrcode_link);
        btnCopyLink = findViewById(R.id.button_copy_link);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        tvNome.setText(intent.getStringExtra("nome"));
        tvCpfCnpj.setText(intent.getStringExtra("cpfCnpj"));
        tvTelefone.setText(intent.getStringExtra("telefone"));
        tvEmail.setText(intent.getStringExtra("email"));
        tvValor.setText(intent.getStringExtra("valor"));
    }

    private void setupPixAndQrCode() {
        String chavePix = "rodriguesbmb@outlook.com";
        String valorNumerico = tvValor.getText().toString().replaceAll("[^\\d,.]", "").replace(",", ".");

        String payload = gerarPixPayload(chavePix, valorNumerico, "Doação via app");
        gerarQrCode(payload);
        tvQrcodeLink.setText(payload);
    }

    private void setupButtonListeners() {
        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(QrcodeActivity.this, DoacaoActivity.class);
            intent.putExtra("nome", tvNome.getText().toString());
            intent.putExtra("cpfCnpj", tvCpfCnpj.getText().toString());
            intent.putExtra("telefone", tvTelefone.getText().toString());
            intent.putExtra("email", tvEmail.getText().toString());
            intent.putExtra("valor", tvValor.getText().toString());
            startActivity(intent);
            finish();
        });

        btnCopyLink.setOnClickListener(v -> copyToClipboard(tvQrcodeLink.getText().toString()));
        btnFinalizar.setOnClickListener(v -> registrarDoador());
    }

    private void registrarDoador() {
        try {
            JSONObject jsonDoacao = new JSONObject();

            // Remover formatação do CPF/CNPJ (enviar apenas números)
            String cpfCnpj = tvCpfCnpj.getText().toString().replaceAll("[^0-9]", "");

            // Usar exatamente os nomes de campos que o backend espera
            jsonDoacao.put("Nome", tvNome.getText().toString());
            jsonDoacao.put("Telefone", tvTelefone.getText().toString().replaceAll("[^0-9]", ""));
            jsonDoacao.put("CPF", cpfCnpj);
            jsonDoacao.put("Email", tvEmail.getText().toString());

            // Formatar o valor corretamente (remover R$ e converter para double)
            String valorStr = tvValor.getText().toString()
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim();

            jsonDoacao.put("ValorDoacao", Double.parseDouble(valorStr));

            // Usar apenas um campo de método de pagamento (conforme o backend espera)
            jsonDoacao.put("MetodoPagamento", "Pix"); // ou "MetodoPagamento" conforme necessário

            Log.d("API_DEBUG", "JSON enviado: " + jsonDoacao.toString());

            new ApiTask().execute(BASE_URL + "/api/Doador", jsonDoacao.toString());

        } catch (JSONException e) {
            Log.e("API_ERROR", "Erro ao criar JSON", e);
            Toast.makeText(this, "Erro ao formatar dados", Toast.LENGTH_LONG).show();
        } catch (NumberFormatException e) {
            Log.e("API_ERROR", "Erro no formato do valor", e);
            Toast.makeText(this, "Valor inválido: " + tvValor.getText().toString(), Toast.LENGTH_LONG).show();
        }
    }
    private class ApiTask extends AsyncTask<String, Void, ApiResponse> {
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
                Toast.makeText(QrcodeActivity.this, "Doação registrada com sucesso!", Toast.LENGTH_SHORT).show();
                redirectToLandingPage();
            } else {
                showError("Erro " + response.statusCode + ": " + response.body);
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

    private void showError(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void redirectToLandingPage() {
        startActivity(new Intent(this, LandingPageActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Pix Copia e Cola", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Código Pix copiado!", Toast.LENGTH_SHORT).show();
    }

    private void gerarQrCode(String payload) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(payload, BarcodeFormat.QR_CODE, 800, 800);
            ivQrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao gerar QR Code", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Erro ao gerar QR Code", e);
        }
    }

    // Pix Payload utilitário
    private String gerarPixPayload(String chavePix, String valor, String descricao) {
        String merchantName = "DOADOR";
        String merchantCity = "SAO PAULO";

        String gui = "BR.GOV.BCB.PIX";
        String merchantAccountInfo = "00" + String.format("%02d", gui.length()) + gui
                + "01" + String.format("%02d", chavePix.length()) + chavePix;

        String payloadSemCRC = "000201" +
                "26" + String.format("%02d", merchantAccountInfo.length()) + merchantAccountInfo +
                "52040000" +
                "5303986" +
                "54" + String.format("%02d", valor.length()) + valor +
                "5802BR" +
                "59" + String.format("%02d", merchantName.length()) + merchantName +
                "60" + String.format("%02d", merchantCity.length()) + merchantCity +
                "62070503***";

        String payloadParaCRC = payloadSemCRC + "6304";
        String crc = calcularCRC16(payloadParaCRC);

        return payloadSemCRC + "63" + "04" + crc;
    }

    private String calcularCRC16(String text) {
        int polynomial = 0x1021;
        int crc = 0xFFFF;

        byte[] bytes = text.getBytes();
        for (byte b : bytes) {
            crc ^= (b << 8) & 0xFFFF;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = ((crc << 1) ^ polynomial) & 0xFFFF;
                } else {
                    crc = (crc << 1) & 0xFFFF;
                }
            }
        }
        return String.format("%04X", crc);
    }
}

