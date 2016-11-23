package nl.groenier.android.firstsurfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Martijn on 22/11/2016.
 */

public class SmartFloorView extends SurfaceView implements SurfaceHolder.Callback {

    private PanelThread _thread;
    private Paint paint = new Paint();

    private int canvasWidth;
    private int canvasHeight;

    private int gridPointDistanceLengthOfFloor;
    private int gridPointDistanceWidthOfFloor;

    private float quantityTagPointsLenghtOfFloor = 200;
    private float quantityTagPointsWidthOfFloor = 150;

    private Bitmap image_left_foot = BitmapFactory.decodeResource(getResources(), R.drawable.shoe_sole_left);
    private int bitmapCenterX = image_left_foot.getWidth() / 2;
    private int bitmapCenterY = image_left_foot.getHeight() / 2;

    private List<CanvasObject> canvasObjectList = new ArrayList<>();

    private Random random = new Random();

    public SmartFloorView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    public SmartFloorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        setWillNotDraw(false); //Allows us to use invalidate() to call onDraw()

        Canvas canvas = surfaceHolder.lockCanvas();
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        surfaceHolder.unlockCanvasAndPost(canvas);

        gridPointDistanceLengthOfFloor = Math.round(canvasWidth / quantityTagPointsLenghtOfFloor);
        gridPointDistanceWidthOfFloor = Math.round(canvasHeight / quantityTagPointsWidthOfFloor);

        _thread = new PanelThread(getHolder(), this); //Start the thread that
        _thread.setRunning(true);                     //will make calls to
        _thread.start();                              //onDraw()
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            _thread.setRunning(false);                //Tells thread to stop
            _thread.join();                           //Removes thread from mem.
        } catch (InterruptedException e) {}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void updateAllCanvasObjects(Canvas canvas) {

        Iterator<CanvasObject> iter = canvasObjectList.iterator();

        while (iter.hasNext()) {
            CanvasObject canvasObject = iter.next();

            if(canvasObject.getX() < canvasWidth && canvasObject.getY() < canvasHeight) {
                updateCanvasObject(canvasObject);
            } else {
                iter.remove();
            }
        }

    }

    private void updateCanvasObject(CanvasObject canvasObject) {
        canvasObject.setX(canvasObject.getX() + gridPointDistanceLengthOfFloor);
        canvasObject.setY(canvasObject.getY() + gridPointDistanceWidthOfFloor);
        canvasObject.setOrientation(canvasObject.getOrientation() + 5);
    }

    public void populateCanvas(int quantity) {
        for(int i = 0; i < quantity; i++) {
            canvasObjectList.add(new CanvasObject(random.nextInt(500),random.nextInt(500),random.nextInt(360)));
        }
    }

    public void clearCanvas() {
        canvasObjectList.clear();
    }

    public void reDrawCanvas() {
        postInvalidate();
    }

    class PanelThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private SmartFloorView _panel;
        private boolean _run = false;


        public PanelThread(SurfaceHolder surfaceHolder, SmartFloorView panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }


        public void setRunning(boolean run) { //Allow us to stop the thread
            _run = run;
        }


        @Override
        public void run() {
            Canvas c;
            while (_run) {     //When setRunning(false) occurs, _run is
                c = null;      //set to false and loop ends, stopping thread
                try {
                    _thread.sleep(24);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("sleep", "caught! ");
                }

                try {

                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {

                        c.drawColor(Color.WHITE);

                        for (CanvasObject item : canvasObjectList) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(item.getOrientation(), bitmapCenterX, bitmapCenterY);
                            matrix.postTranslate(item.getX(), item.getY());
                            c.drawBitmap(image_left_foot,matrix,paint);
                        }

                        updateAllCanvasObjects(c);

                        reDrawCanvas();

                    }
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

}
