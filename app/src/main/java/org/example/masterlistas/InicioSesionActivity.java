package org.example.masterlistas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class InicioSesionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
    }

    public void loguearCheckbox(View v) {
        CheckBox recordarme= (CheckBox) findViewById(R.id.recordarme);
        String s = getResources().getString(R.string.texto_recordar)+": " +
                (recordarme.isChecked() ? getResources().getString(R.string.si) : getResources().getString(R.string.no));
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}
