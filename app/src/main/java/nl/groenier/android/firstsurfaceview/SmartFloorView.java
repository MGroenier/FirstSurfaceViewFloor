package nl.groenier.android.firstsurfaceview;

import android.content.Context;
import android.content.Intent;
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

import java.util.Iterator;

import static nl.groenier.android.firstsurfaceview.MainActivity.objectList;

/**
 * Created by Martijn on 22/11/2016.
 */

public class SmartFloorView extends SurfaceView implements SurfaceHolder.Callback {

    private PanelThread _thread;
    private Paint paint = new Paint();

    private int x = 0;
    private int y = 0;
    private int orientation = 0;

    Matrix matrix = new Matrix();
    private Bitmap leftFootImage = BitmapFactory.decodeResource(getResources(), R.drawable.shoe_sole_left);

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

        for (CanvasObject item : MainActivity.objectList) {
//            canvas.drawCircle(item.getX(), item.getY(), radius, paint);

            Matrix matrix = new Matrix();

            // rotate around (0,0)
//            matrix.postRotate(90);

            // or, rotate around x,y
            // NOTE: coords in bitmap-space!
            matrix.postRotate(item.getOrientation(), leftFootImage.getWidth()/2, leftFootImage.getHeight()/2);
//            matrix.postRotate(item.getOrientation(), footImage.getWidth()/2, 0);

            int xTranslate = 10;
            int yTranslate = 10;
            matrix.postTranslate(item.getX(), item.getY());

            canvas.drawBitmap(leftFootImage,matrix,paint);
        }

        updateAllCanvasObjects();

        super.onDraw(canvas);
    }

    private void updateAllCanvasObjects() {
        for(CanvasObject item : objectList){
            updateCanvasObject(item);
        }

        Iterator<CanvasObject> iter = objectList.iterator();

        while (iter.hasNext()) {
            CanvasObject object = iter.next();

            int x = object.getX();
            int y = object.getY();

            if(x < 1650 && y < 900) {
                //postInvalidate();
            }
            else {
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
                    _thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d("sleep", "caught! ");
                }

                try {

                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {

                        //Insert methods to modify positions of items in onDraw()
                        postInvalidate();

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
