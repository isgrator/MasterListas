package org.imgracian.masterlistas;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

public class InicioSesionActivity extends AppCompatActivity {

    private FirebaseAnalytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        MobileAds.initialize(this,"ca-app-pub-5594373787368665~1826593970");

        analytics = FirebaseAnalytics.getInstance(this);


    }

    public void loguearCheckbox(View v) {
        CheckBox recordarme= (CheckBox) findViewById(R.id.recordarme);
        String s = getResources().getString(R.string.texto_recordar)+": " +
                (recordarme.isChecked() ? getResources().getString(R.string.si) : getResources().getString(R.string.no));
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    public void mostrarContraseña(View v) {
        EditText contraseña = (EditText) findViewById(R.id.contraseña);
        CheckBox mostrar = (CheckBox) findViewById(R.id.mostrar_contraseña);
        if (mostrar.isChecked()) {
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_NORMAL);
        } else {
            contraseña.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void acceder (View view){
        Bundle param = new Bundle();
        param.putString("elemento", "boton inicio sesion MasterListas");
        param.putInt("pulsacion_ms",34);
        analytics.logEvent("butt_acceder", param);
        Intent intent = new Intent(this, ListasActivity.class);
        startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void borrarCampos(View view){
        EditText usuario = (EditText) findViewById(R.id.usuario);
        EditText contraseña = (EditText) findViewById(R.id.contraseña);
        usuario.setText("");
        contraseña.setText("");
        usuario.requestFocus();
    }
}
