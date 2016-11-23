package nl.groenier.android.firstsurfaceview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SmartFloorView mSmartFloorView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSmartFloorView = (SmartFloorView) findViewById(R.id.smart_floor_view);
        mButton = (Button) findViewById(R.id.button_start_redraw);

        mSmartFloorView.populateCanvas(60);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSmartFloorView.clearCanvas();
                mSmartFloorView.populateCanvas(60);
                mSmartFloorView.reDrawCanvas();
            }
        });

    }

}
