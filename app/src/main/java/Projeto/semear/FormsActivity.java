package Projeto.semear;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FormsActivity extends AppCompatActivity {

    EditText input1, input2, email1, editTextTextMultiLine, editTextTextMultiLine2;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forms); // Altere se o nome do XML for outro

        // Vinculando os elementos da interface com os IDs do layout
        input1 = findViewById(R.id.input1); // Nome
        input2 = findViewById(R.id.input2); // Último nome
        email1 = findViewById(R.id.email1); // Endereço e E-mail
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine); // Skills
        editTextTextMultiLine2 = findViewById(R.id.editTextTextMultiLine2); // Interesses
        button3 = findViewById(R.id.button3); // Botão Enviar

        // Evento ao clicar no botão
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeCompleto = input1.getText().toString() + " " + input2.getText().toString();
                String email = email1.getText().toString();
                String skills = editTextTextMultiLine.getText().toString();
                String interesses = editTextTextMultiLine2.getText().toString();

                // Exibindo os dados em um Toast
                Toast.makeText(FormsActivity.this,
                        "Nome: " + nomeCompleto + "\nEmail: " + email +
                                "\nSkills: " + skills + "\nInteresses: " + interesses,
                        Toast.LENGTH_LONG
                ).show();

            }
        });
    }
}
