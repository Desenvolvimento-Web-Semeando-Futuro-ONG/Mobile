package Projeto.semear;//package Projeto.semear;
public class ApiRequestTask extends android.os.AsyncTask<String, Void, ApiRequestTask.Result> {
    public interface Listener {
        void onResult(int statusCode, String response);
    }

    private final String method;
    private final String url;
    private final String jsonData;
    private final Listener listener;

    public ApiRequestTask(String method, String url, String jsonData, Listener listener) {
        this.method = method;
        this.url = url;
        this.jsonData = jsonData;
        this.listener = listener;
    }


    public static class Result {
        int statusCode;
        String response;
        Result(int code, String resp) {
            statusCode = code;
            response = resp;
        }
    }

    @Override
    protected Result doInBackground(String... ignored) {
        java.net.HttpURLConnection connection = null;
        try {
            java.net.URL urlObj = new java.net.URL(url);
            connection = (java.net.HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            if (jsonData != null) {
                try (java.io.OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int code = connection.getResponseCode();
            java.io.InputStream is = (code < 400) ? connection.getInputStream() : connection.getErrorStream();

            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is, "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            return new Result(code, response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(500, "Erro: " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (listener != null) listener.onResult(result.statusCode, result.response);
    }
}
