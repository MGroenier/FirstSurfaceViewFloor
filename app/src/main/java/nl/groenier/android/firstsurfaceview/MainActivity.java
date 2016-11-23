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

    private TextView mTextView;
    private SmartFloorView mSmartFloorView;
    private Button mButton;

    private int objectsToDraw = 10;
    private int lengthOfSleep = 100;

    public static List<CanvasObject> objectList = new ArrayList();

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text_view_intro);
        mSmartFloorView = (SmartFloorView) findViewById(R.id.smart_floor_view);
        mButton = (Button) findViewById(R.id.button_start_redraw);

        for(int i = 0; i < objectsToDraw; i++) {
            objectList.add(new CanvasObject(random.nextInt(200),random.nextInt(200),random.nextInt(360)));
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objectList.clear();
                for(int i = 0; i < objectsToDraw; i++) {
                    objectList.add(new CanvasObject(random.nextInt(200),random.nextInt(200),random.nextInt(360)));
                }
                mSmartFloorView.postInvalidate();
            }
        });



    }

}
