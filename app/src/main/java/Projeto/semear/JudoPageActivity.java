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
        setContentView(R.layout.judo_page); // ou o nome correto do layout

        Button buttonVoluntario = findViewById(R.id.button2);

        buttonVoluntario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JudoPageActivity.this, FormsActivity.class);
                startActivity(intent);
            }
        });
    }
}
