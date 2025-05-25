package Projeto.semear;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiRequestTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private Exception exception;
    private String apiUrl;
    private JSONObject postData; // Dados a serem enviados no corpo da requisição (JSON)

    public ApiRequestTask(Context context, String apiUrl, JSONObject postData) {
        this.context = context;
        this.apiUrl = apiUrl;
        this.postData = postData;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = "";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Envia o JSON no corpo da requisição
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(postData.toString());
            writer.flush();
            writer.close();
            os.close();

            // Lê a resposta
            int responseCode = connection.getResponseCode();
            InputStream inputStream;
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();

            response = stringBuilder.toString();

        } catch (Exception e) {
            this.exception = e;
            Log.e("API_ERROR", "Erro na requisição", e);
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (exception != null) {
            Toast.makeText(context, "Erro ao acessar API: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("API_ERROR", "Erro no onPostExecute", exception);
        } else {
            Toast.makeText(context, "Resposta da API recebida!", Toast.LENGTH_SHORT).show();
            Log.d("RespostaAPI", result);

            // Aqui você pode tratar o resultado, por exemplo extrair o token JWT do JSON:
            try {
                JSONObject jsonResponse = new JSONObject(result);
                if (jsonResponse.has("token")) {
                    String token = jsonResponse.getString("token");
                    Log.d("TOKEN_JWT", token);
                    // Salve o token em SharedPreferences, variável global, etc. conforme sua necessidade
                }
            } catch (Exception ex) {
                Log.e("API_ERROR", "Erro ao processar JSON de resposta", ex);
            }
        }
    }
}