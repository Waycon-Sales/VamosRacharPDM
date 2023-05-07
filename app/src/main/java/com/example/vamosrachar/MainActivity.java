package com.example.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText valorTotal, totalPessoas;
    private TextView valorPraCada;
    private FloatingActionButton btnCompartilhar, btnFalar;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        valorTotal = findViewById(R.id.txValorTotal) ;
        totalPessoas = findViewById(R.id.txTotalPessoas);
        btnCompartilhar = findViewById(R.id.floatingCompartilhar);
        btnFalar = findViewById(R.id.floatingFalar);
        valorPraCada = findViewById(R.id.tvValorPraCada);
        textToSpeech = new TextToSpeech(this, this);

        btnFalar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(valorPraCada.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto = "A conta total de "+valorTotal.getText().toString()+" dividida para "+totalPessoas.getText().toString()+" deu "+valorPraCada.getText().toString()+" para cada pessoa";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, texto);
                startActivity(Intent.createChooser(intent, "Compartilhar via"));
            }
        });



        valorTotal.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculo();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });

        totalPessoas.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               calculo();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });



    }

    public double valorTotalTratado(){
        String valor = valorTotal.getText().toString();
        valor = valor.replace(',', '.');
        if(valor.equals("") || valor.equals(null)){
                valor = "0";
        }
        Double valorT = Double.parseDouble(valor);
        return valorT;
    }

    public double totalPessoasTratado(){
            String valor = totalPessoas.getText().toString();
            valor = valor.replace(',', '.');
            if (valor.equals("") || valor.equals(null)) {
                valor = "1";
            }
            Double valorP = Double.parseDouble(valor);
            return valorP;
    }

    public void calculo(){
        try {
            Double valTot = valorTotalTratado();
            Double totalPessoas = totalPessoasTratado();

            if (totalPessoas <= 0) {
                Toast.makeText(MainActivity.this, "Não é possivel efetuar a divisão", Toast.LENGTH_SHORT).show();
            } else {
                DecimalFormat df = new DecimalFormat("#.##"); // Cria um DecimalFormat para formatar o valor
                Double result = Double.valueOf(df.format(valTot / totalPessoas));
                valorPraCada.setText(result.toString().replace(".", ","));
            }
        }catch(Exception e){
            Log.i("Error", "tratamento de exception"+e.getMessage());
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("pt", "BR"));

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Lingua não suportada", Toast.LENGTH_SHORT).show();
                Log.e("TTS", "Language not supported");
            }
        } else {
            Toast.makeText(this, "Erro ao inicializar fala", Toast.LENGTH_SHORT).show();
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}