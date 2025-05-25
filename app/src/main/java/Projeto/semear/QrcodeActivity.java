
//package Projeto.semear;
//
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.zxing.BarcodeFormat;
//import com.journeyapps.barcodescanner.BarcodeEncoder;
//
//public class QrcodeActivity extends AppCompatActivity {
//
//    private TextView tvNome, tvCpfCnpj, tvTelefone, tvEmail, tvValor;
//    private ImageView ivQrCode;
//    private Button btnVoltar, btnFinalizar;
//    private TextView tvQrcodeLink;
//    private ImageButton btnCopyLink;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.qrcode_page);
//
//        tvNome     = findViewById(R.id.verifique_nome);
//        tvCpfCnpj  = findViewById(R.id.verifique_cpf_cnpj);
//        tvTelefone = findViewById(R.id.verifique_telefone);
//        tvEmail    = findViewById(R.id.verifique_email);
//        tvValor    = findViewById(R.id.verique_valor);
//        ivQrCode   = findViewById(R.id.ivQrCode);
//        btnVoltar  = findViewById(R.id.btn_voltar);
//        btnFinalizar = findViewById(R.id.btn_finalizar);
//
//        // Novos componentes para link e cópia
//        tvQrcodeLink = findViewById(R.id.text_qrcode_link);
//        btnCopyLink = findViewById(R.id.button_copy_link);
//
//        Intent intent = getIntent();
//        String nome     = intent.getStringExtra("nome");
//        String cpfCnpj  = intent.getStringExtra("cpfCnpj");
//        String telefone = intent.getStringExtra("telefone");
//        String email    = intent.getStringExtra("email");
//        String valor    = intent.getStringExtra("valor");
//
//        tvNome.setText(nome);
//        tvCpfCnpj.setText(cpfCnpj);
//        tvTelefone.setText(telefone);
//        tvEmail.setText(email);
//        tvValor.setText(valor);
//
//        String chavePix = "rodriguesbmb@outlook.com"; // substitua pela sua chave Pix real
//        String descricao = "Doação via app";
//        String valorNumerico = valor.replaceAll("[^\\d,\\.]", "").replace(",", ".");
//
//        String payload = gerarPixPayload(chavePix, valorNumerico, descricao);
//
//        gerarQrCode(payload);
//
//        // Mostrar link logo abaixo do QR code
//        tvQrcodeLink.setText(payload);
//
//        // Botão copia link para área de transferência
//        btnCopyLink.setOnClickListener(v -> {
//            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("Link QR Code", payload);
//            clipboard.setPrimaryClip(clip);
//            Toast.makeText(QrcodeActivity.this, "Link copiado para a área de transferência", Toast.LENGTH_SHORT).show();
//        });
//
//        btnVoltar.setOnClickListener(v -> finish());
//
////        btnFinalizar.setOnClickListener(v -> {
////            Toast.makeText(QrcodeActivity.this, "Obrigado por sua ajuda!", Toast.LENGTH_LONG).show();
////
////            Intent landingIntent = new Intent(QrcodeActivity.this, LandingPageActivity.class);
////            landingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////            startActivity(landingIntent);
////            finish();
////        });
//
//        btnFinalizar.setOnClickListener(v -> {
//            // Captura os dados dos campos da tela
//            String nome2 = tvNome.getText().toString();
//            String valorStr = tvValor.getText().toString().replaceAll("[^\\d,\\.]", "").replace(",", ".");
//            double valor2 = 0;
//            try { valor2 = Double.parseDouble(valorStr); } catch (Exception ex) {}
//            String metodoPagamento = "Pix";
//
//            // Data/hora atual em formato ISO (exemplo: 2024-05-25T09:15:00)
//            String dataDoacao = java.time.LocalDateTime.now().toString();
//
//            // Monta o JSON
//            org.json.JSONObject jsonDoacao = new org.json.JSONObject();
//            try {
//                jsonDoacao.put("doadorNome", nome);
//                jsonDoacao.put("valor", valor);
//                jsonDoacao.put("dataDoacao", dataDoacao);
//                jsonDoacao.put("metodoPagamento", metodoPagamento);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(QrcodeActivity.this, "Erro ao montar JSON!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // URL do backend (emulador: 10.0.2.2)
//            String url = "http://10.0.2.2:5189/api/Doacao";
//
//            // Chama AsyncTask para enviar
//            new ApiRequestTask(QrcodeActivity.this, url, jsonDoacao).execute();
//
//            Toast.makeText(QrcodeActivity.this, "Obrigado por sua ajuda!", Toast.LENGTH_LONG).show();
//
//            Intent landingIntent = new Intent(QrcodeActivity.this, LandingPageActivity.class);
//            landingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(landingIntent);
//            finish();
//        });
//    }
//
//    private void gerarQrCode(String payload) {
//        try {
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            Bitmap bitmap = barcodeEncoder.encodeBitmap(payload, BarcodeFormat.QR_CODE, 800, 800);
//            ivQrCode.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String gerarPixPayload(String chavePix, String valor, String descricao) {
//        String merchantName = "DOADOR".toUpperCase().replaceAll("[^\\p{ASCII}]", "");
//        String merchantCity = "SAO PAULO".toUpperCase().replaceAll("[^\\p{ASCII}]", "");
//
//        valor = valor.replace(",", ".");
//
//        String gui = "BR.GOV.BCB.PIX";
//        String merchantAccountInfo = "00" + String.format("%02d", gui.length()) + gui
//                + "01" + String.format("%02d", chavePix.length()) + chavePix;
//        String merchantAccountInfoLength = String.format("%02d", merchantAccountInfo.length());
//
//        String merchantCategoryCode = "52040000";
//        String transactionCurrency = "5303986";
//        String transactionAmount = "54" + String.format("%02d", valor.length()) + valor;
//        String countryCode = "5802BR";
//        String merchantNameField = "59" + String.format("%02d", merchantName.length()) + merchantName;
//        String merchantCityField = "60" + String.format("%02d", merchantCity.length()) + merchantCity;
//
//        String txid = "***";
//        String txidField = "05" + String.format("%02d", txid.length()) + txid;
//        String additionalDataField = "62" + String.format("%02d", txidField.length()) + txidField;
//
//        String payloadSemCRC = "00" + "02" + "01"
//                + "26" + merchantAccountInfoLength + merchantAccountInfo
//                + merchantCategoryCode
//                + transactionCurrency
//                + transactionAmount
//                + countryCode
//                + merchantNameField
//                + merchantCityField
//                + additionalDataField;
//
//        String payloadParaCRC = payloadSemCRC + "6304";
//        String crc = calcularCRC16(payloadParaCRC);
//
//        return payloadSemCRC + "63" + "04" + crc;
//    }
//
//    private String calcularCRC16(String text) {
//        int polynomial = 0x1021;
//        int crc = 0xFFFF;
//
//        byte[] bytes = text.getBytes();
//
//        for (byte b : bytes) {
//            crc ^= (b << 8) & 0xFFFF;
//            for (int i = 0; i < 8; i++) {
//                if ((crc & 0x8000) != 0) {
//                    crc = ((crc << 1) ^ polynomial) & 0xFFFF;
//                } else {
//                    crc = (crc << 1) & 0xFFFF;
//                }
//            }
//        }
//
//        return String.format("%04X", crc);
//    }
//}


package Projeto.semear;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class QrcodeActivity extends AppCompatActivity {

    private TextView tvNome, tvCpfCnpj, tvTelefone, tvEmail, tvValor;
    private ImageView ivQrCode;
    private Button btnVoltar, btnFinalizar;
    private TextView tvQrcodeLink;
    private ImageButton btnCopyLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_page);

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

        Intent intent = getIntent();
        String nome = intent.getStringExtra("nome");
        String cpfCnpj = intent.getStringExtra("cpfCnpj");
        String telefone = intent.getStringExtra("telefone");
        String email = intent.getStringExtra("email");
        String valor = intent.getStringExtra("valor");

        tvNome.setText(nome);
        tvCpfCnpj.setText(cpfCnpj);
        tvTelefone.setText(telefone);
        tvEmail.setText(email);
        tvValor.setText(valor);

        String chavePix = "rodriguesbmb@outlook.com";
        String descricao = "Doação via app";
        String valorNumerico = valor.replaceAll("[^\\d,\\.]", "").replace(",", ".");

        String payload = gerarPixPayload(chavePix, valorNumerico, descricao);
        gerarQrCode(payload);
        tvQrcodeLink.setText(payload);

        btnCopyLink.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Link QR Code", payload);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(QrcodeActivity.this, "Link copiado para a área de transferência", Toast.LENGTH_SHORT).show();
        });

        btnVoltar.setOnClickListener(v -> finish());

        btnFinalizar.setOnClickListener(v -> {
            // Enviar dados do doador para o backend
            JSONObject jsonDoador = new JSONObject();
            try {
                jsonDoador.put("Nome", nome);
                jsonDoador.put("Telefone", telefone);
                jsonDoador.put("CPF", cpfCnpj);
                jsonDoador.put("Email", email);

                // URL do backend (para emulador usar 10.0.2.2 em vez de localhost)
                String url = "http://10.0.2.2:5189/api/Doador";

                new CadastrarDoadorTask().execute(url, jsonDoador.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(QrcodeActivity.this, "Erro ao preparar dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CadastrarDoadorTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            String jsonInputString = params[1];

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        return response.toString();
                    }
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Error") || result.startsWith("Exception")) {
                Toast.makeText(QrcodeActivity.this, "Erro ao cadastrar: " + result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(QrcodeActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

                // Redirecionar para a tela principal após o cadastro
                Intent landingIntent = new Intent(QrcodeActivity.this, LandingPageActivity.class);
                landingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(landingIntent);
                finish();
            }
        }
    }

    private void gerarQrCode(String payload) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(payload, BarcodeFormat.QR_CODE, 800, 800);
            ivQrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String gerarPixPayload(String chavePix, String valor, String descricao) {
        String merchantName = "DOADOR".toUpperCase().replaceAll("[^\\p{ASCII}]", "");
        String merchantCity = "SAO PAULO".toUpperCase().replaceAll("[^\\p{ASCII}]", "");

        valor = valor.replace(",", ".");

        String gui = "BR.GOV.BCB.PIX";
        String merchantAccountInfo = "00" + String.format("%02d", gui.length()) + gui
                + "01" + String.format("%02d", chavePix.length()) + chavePix;
        String merchantAccountInfoLength = String.format("%02d", merchantAccountInfo.length());

        String merchantCategoryCode = "52040000";
        String transactionCurrency = "5303986";
        String transactionAmount = "54" + String.format("%02d", valor.length()) + valor;
        String countryCode = "5802BR";
        String merchantNameField = "59" + String.format("%02d", merchantName.length()) + merchantName;
        String merchantCityField = "60" + String.format("%02d", merchantCity.length()) + merchantCity;

        String txid = "***";
        String txidField = "05" + String.format("%02d", txid.length()) + txid;
        String additionalDataField = "62" + String.format("%02d", txidField.length()) + txidField;

        String payloadSemCRC = "00" + "02" + "01"
                + "26" + merchantAccountInfoLength + merchantAccountInfo
                + merchantCategoryCode
                + transactionCurrency
                + transactionAmount
                + countryCode
                + merchantNameField
                + merchantCityField
                + additionalDataField;

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