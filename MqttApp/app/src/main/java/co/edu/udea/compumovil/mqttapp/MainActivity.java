package co.edu.udea.compumovil.mqttapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText ip;
    private Pattern pattern;
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = (EditText) findViewById(R.id.etIp);

    }

    public boolean validateIP(String ip){
        String regular_expression =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        pattern = Pattern.compile(regular_expression);
        matcher = pattern.matcher(ip);
        return matcher.matches();
    };

    public void connection(View v) {
        boolean isValid = validateIP(ip.getText().toString());
        if (isValid){
            String host = ip.getText().toString();
            String MQTTHOST = "tcp://" + host + ":1883";

            Toast.makeText(MainActivity.this, "Enviando", Toast.LENGTH_LONG).show();

            Intent in = new Intent(MainActivity.this,ConectionMqtt.class);
            in.putExtra("host",MQTTHOST);
            startActivity(in);
        }else {
            Toast.makeText(MainActivity.this, "Ingrese una IP valida", Toast.LENGTH_LONG).show();
            ip.setText("");
        }
    }

}