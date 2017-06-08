package co.edu.udea.compumovil.mqttapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by admin on 7/06/2017.
 */

public class ConectionMqtt extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    //static String MQTTHOST = "tcp://192.168.1.52:1883";
    String topicSrt = "hello/world";
    boolean toggle = false;

    private String msn;
    private TextView getMessage;
    private Button btnDiscon;
    private Button btnConn;

    MqttAndroidClient client;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private YouTubePlayer player;

    //String clientId;
    //String MQTTHOST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conection);

        getMessage = (TextView) findViewById(R.id.tvMessage);
        btnDiscon = (Button) findViewById(R.id.btn_disconnect);
        btnConn = (Button) findViewById(R.id.btn_connect);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);

        String MQTTHOST = getIntent().getExtras().getString("host");
        String clientId = MqttClient.generateClientId();
        Log.d("HOSTTTCo", MQTTHOST);

        btnDiscon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnection(v);
            }
        });

        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(ConectionMqtt.this, "connected", Toast.LENGTH_LONG).show();
                    setSubscription();
                    showMessage();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Error", String.valueOf(asyncActionToken.getException()));
                    Log.d("Error", String.valueOf(exception.getMessage()));

                    Toast.makeText(ConectionMqtt.this, "No se pudo conectar" + asyncActionToken.getException(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public void showMessage() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                msn = new String(message.getPayload());
                getMessage.setText(msn);
                playOrPause();
                //pub();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void playOrPause(){
        Log.d(String.valueOf(toggle), "messageArrived: inicio");
        if(toggle){
            Log.d(String.valueOf(toggle), "messageArrived: true");
            player.play();
        }else {
            Log.d(String.valueOf(toggle), "messageArrived: false");
            msn = "Off";
            player.pause();
        }

        toggle = !toggle;
    }


    public void pub() {
        String topic = topicSrt;
        try {
            client.publish(topic, msn.getBytes(), 0, false);
            Toast.makeText(ConectionMqtt.this, "Publish message: " + msn, Toast.LENGTH_LONG).show();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription() {
       // client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        try {
            client.subscribe(topicSrt, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnection(View v) {
        Log.d("HOSTTTCoooCli", String.valueOf(client));
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(ConectionMqtt.this, "Disconnected", Toast.LENGTH_LONG).show();
                    Intent in = new Intent(ConectionMqtt.this,MainActivity.class);
                    startActivity(in);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Error Discount", String.valueOf(asyncActionToken.getException()));

                    Toast.makeText(ConectionMqtt.this, "Disconnect Error" + asyncActionToken.getException(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.cueVideo("kJQP7kiw5Fk"); // Plays https://www.youtube.com/watch?v=kJQP7kiw5Fk

            player = youTubePlayer;
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            //String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }
}
