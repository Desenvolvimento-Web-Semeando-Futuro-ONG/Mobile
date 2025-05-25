package Projeto.semear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class JudoPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.judo_page); // Certifique-se de que o layout XML está com este nome

        // Conecta o botão pelo ID
        Button botaoFormulario = findViewById(R.id.button_formulario);

        // Define o clique para abrir a nova tela
        botaoFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JudoPageActivity.this, FormularioActivity.class);
                startActivity(intent);
            }
        });


    }
}
