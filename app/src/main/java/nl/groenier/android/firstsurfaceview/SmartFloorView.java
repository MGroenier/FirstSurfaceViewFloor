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

    private Bitmap leftFootImage = BitmapFactory.decodeResource(getResources(), R.drawable.shoe_sole_left);

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
        canvas.drawColor(Color.WHITE);

        for (CanvasObject item : canvasObjectList) {
            Matrix matrix = new Matrix();
            matrix.postRotate(item.getOrientation(), leftFootImage.getWidth()/2, leftFootImage.getHeight()/2);
            matrix.postTranslate(item.getX(), item.getY());
            canvas.drawBitmap(leftFootImage,matrix,paint);
        }

        updateAllCanvasObjects();

        super.onDraw(canvas);
    }

    private void updateAllCanvasObjects() {
        for(CanvasObject item : canvasObjectList){
            updateCanvasObject(item);
        }

        Iterator<CanvasObject> iter = canvasObjectList.iterator();

        while (iter.hasNext()) {
            CanvasObject object = iter.next();

            int x = object.getX();
            int y = object.getY();

            if(!(x < 300 && y < 300)) {
                iter.remove();
            }
        }

    }

    private void updateCanvasObject(CanvasObject canvasObject) {
        int x = canvasObject.getX();
        int y = canvasObject.getY();
        int orientation = canvasObject.getOrientation();

        int transX = 1;
        int transY = 1;
        int transOrientation = 5;

        canvasObject.setX(x + transX);
        canvasObject.setY(y + transY);
        canvasObject.setOrientation(orientation + transOrientation);
    }

    public void populateCanvas(int quantity) {
        for(int i = 0; i < quantity; i++) {
            canvasObjectList.add(new CanvasObject(random.nextInt(200),random.nextInt(200),random.nextInt(360)));
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

                        //Insert methods to modify positions of items in onDraw()
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
