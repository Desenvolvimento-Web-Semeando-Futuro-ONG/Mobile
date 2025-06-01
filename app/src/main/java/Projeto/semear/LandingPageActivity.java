package Projeto.semear;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class LandingPageActivity extends BaseZapActivity {

    Button buttonDoar;
    ImageButton imageButtonJudo;
    ImageButton imageButtonBoaIdade;
    ImageButton imageButtonArtesanato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page); // certifique-se de que o XML esteja correto

        // Vincular elementos do layout
        buttonDoar = findViewById(R.id.button_doar);
        imageButtonJudo = findViewById(R.id.imageButton_judo_page);
        imageButtonBoaIdade = findViewById(R.id.imageButton_boa_idade);
        imageButtonArtesanato = findViewById(R.id.imageButton_artesanato);

        configurarBotaoWhatsApp();

        // Ações dos botões
        buttonDoar.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPageActivity.this, DoacaoActivity.class);
            startActivity(intent);
        });

        imageButtonJudo.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPageActivity.this, JudoPageActivity.class);
            startActivity(intent);
        });

        imageButtonBoaIdade.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPageActivity.this, BoaldadeActivity.class);
            startActivity(intent);
        });

        imageButtonArtesanato.setOnClickListener(v -> {
            Intent intent = new Intent(LandingPageActivity.this, ArtesanatoActivity.class);
            startActivity(intent);
        });

    }
}
