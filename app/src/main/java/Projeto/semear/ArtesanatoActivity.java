package Projeto.semear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ArtesanatoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artesanato_page); // Certifique-se de que o nome do XML está correto

        Button buttonFormulario3 = findViewById(R.id.button_formulario3);

        buttonFormulario3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArtesanatoActivity.this, FormularioActivity.class); // Substitua por sua Activity de formulário
                startActivity(intent);

            }
        });

    }
}
