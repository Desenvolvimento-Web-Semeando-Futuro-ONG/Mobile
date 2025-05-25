package Projeto.semear;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BoaldadeActivity extends AppCompatActivity {

    Button buttonFormulario2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boa_idade_page); // Substitua com o nome correto do layout se for diferente

        buttonFormulario2 = findViewById(R.id.button_formulario2);

        buttonFormulario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoaldadeActivity.this, FormularioActivity.class);
                startActivity(intent);
            }
        });

    }
}
