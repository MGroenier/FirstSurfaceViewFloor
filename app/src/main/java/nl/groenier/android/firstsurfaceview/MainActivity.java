package nl.groenier.android.firstsurfaceview;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    private SmartFloorView mSmartFloorView;
    private Button mButton;
    private TextView textView;
    JsonParser jsonParser = new JsonParser();

    private Socket mSocket;
    {
        try {
//            mSocket = IO.socket("http://10.0.2.2:3000");
            mSocket = IO.socket("http://10.20.0.243:3000");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSocket.on("chat message", onNewMessage);
        mSocket.connect();
        //attemptSend();

        mSmartFloorView = (SmartFloorView) findViewById(R.id.smart_floor_view);
        mButton = (Button) findViewById(R.id.button_start_redraw);
        textView = (TextView)findViewById(R.id.messages);

        mSmartFloorView.populateCanvas(59);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSmartFloorView.clearCanvas();
                //mSmartFloorView.populateCanvas(60);
                mSmartFloorView.reDrawCanvas();
            }
        });

    }

    private void attemptSend() {
        String message = "test-message-from-android-emulator";
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mSocket.emit("chat message", message);
    }

    private void addMessage(String message){
        textView.setText(textView.getText() + "\n" + message);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JsonObject data = jsonParser.parse(args[0].toString()).getAsJsonObject();

                    String id = data.get("id").getAsString();
                    String x = data.get("x").getAsString();
                    String y = data.get("y").getAsString();
                    String o = data.get("o").getAsString();

                    //addMessage(" id = " + id + " _____ " + " x = " + x + " _____ " + " y = " + y + " _____ " + " o = " + o);
                    mSmartFloorView.updateCanvasObject(Integer.parseInt(id),Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(o));

                }
            });
        }
    };

}
