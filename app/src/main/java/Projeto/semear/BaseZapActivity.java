package Projeto.semear;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public abstract class BaseZapActivity extends AppCompatActivity {

    protected void configurarBotaoWhatsApp() {
        FloatingActionButton btnWhatsApp = findViewById(R.id.btn_whatsapp);
        if (btnWhatsApp != null) {
            btnWhatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    abrirWhatsApp();
                }
            });
        }
    }

    private void abrirWhatsApp() {
        String numero = "5581996675144"; // DDI + DDD + número
        String mensagem = "Olá, gostaria de entrar em contato para saber uma informação!";
        String url = "https://wa.me/" + numero + "?text=" + Uri.encode(mensagem);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setPackage("com.whatsapp");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }
}
