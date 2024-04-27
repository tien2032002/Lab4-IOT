package com.example.demoiot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumid;

    LabeledSwitch btnLed, btnPump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTemp = findViewById(R.id.txtTemp);
        txtHumid = findViewById(R.id.txtHumid);
        btnLed = findViewById(R.id.btnLed);
        btnPump = findViewById(R.id.btnPump);

        btnLed.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true){
                    sendDataMQTT("tien2032002/feeds/button1", "1");
                }
                else {
                    sendDataMQTT("tien2032002/feeds/button1", "0");
                }
            }
        });

        btnPump.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true){
                    sendDataMQTT("tien2032002/feeds/button2", "1");
                }
                else {
                    sendDataMQTT("tien2032002/feeds/button2", "0");
                }
            }
        });

        startMQTT();
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
            Log.d("TEST", topic + "***send***" + msg.toString());
        }catch (MqttException e){
        }
    }

    public void startMQTT() {
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "***" + message.toString());
                if(topic.contains("temp")){
                    txtTemp.setText(message.toString() + "°C");
                }
                else if(topic.contains("humid")){
                    txtHumid.setText(message.toString() + "%");
                }
                else if(topic.contains("button1")){
                    if(message.toString().equals("1")){
                        btnLed.setOn(true);
                    }
                    else{
                        btnLed.setOn(false);
                    }
                }
                else if(topic.contains("button2")){
                    if(message.toString().equals("1")){
                        btnPump.setOn(true);
                    }
                    else{
                        btnPump.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}