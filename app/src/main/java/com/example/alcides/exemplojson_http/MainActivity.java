package com.example.alcides.exemplojson_http;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void lerJSON(View view) {
        if (checkInternetConection()) {
            progressDialog = ProgressDialog.show(this, "", "Baixando dados");
            new DownloadJson().execute("http://mfpledon.com.br/receita.json");
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Sem conexão. Verifique.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkInternetConection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void mostrarJSON(String strjson) {
        ((TextView) findViewById(R.id.dados)).setText(strjson);
        String data = "";
        try {
            JSONObject objRaiz = new JSONObject(strjson);
            JSONArray jsonArray = objRaiz.optJSONArray("receita");
            JSONObject jsonObject = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String nome = jsonObject.optString("nome");
                String cpf = jsonObject.optString("cpf");
                String rg = jsonObject.optString("rg");
                String datanasc = jsonObject.optString("datanasc");
                String sexo = jsonObject.optString("sexo");
                Double salario = jsonObject.optDouble("salario");

                data += " \n Nome:" + nome + ", CPF: " + cpf + ", RG: " + rg
                        + ", Dt. nascimento: " + datanasc + ", sexo: " + sexo + ", salário: R$ " + salario + "\n";
                jsonObject = null;
            }
            ((TextView) findViewById(R.id.dados)).setText(data);
        } catch (JSONException e) {
            ((TextView) findViewById(R.id.dados)).setText(e.getMessage() + "\n\n" + data + "\n\n");
        } finally {
            progressDialog.dismiss();
        }
    }

    private class DownloadJson extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadJSON(params[0]);
            } catch (IOException e) {
                return "Erro";
            }
        }

        // onPostExecute exibe o resultado do AsyncTask
        @Override
        protected void onPostExecute(String result) {
            if(result.equals("Erro")) {
                progressDialog.dismiss();
                ((TextView) findViewById(R.id.dados)).setText("\nAlgum erro aconteceu. Revisar a URL solicitada, verifique a conexão com a Internet etc.");
                return;
            }
            mostrarJSON(result);
        }

        private String downloadJSON(String myurl) throws IOException {
            InputStream is = null;
            String respostaHttp = "Erro";
            HttpURLConnection conn = null;
            InputStream in = null;
            ByteArrayOutputStream bos = null;
            try {
                URL u = new URL(myurl);
                conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(4000); // 4 segundos de timeout
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                in = conn.getInputStream();
                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
                }
                respostaHttp = bos.toString("UTF-8");
            } catch (Exception ex) {
                respostaHttp = "Erro";
            } finally {
                if (in != null) in.close();
            }
            return respostaHttp;
        }
    }
}


