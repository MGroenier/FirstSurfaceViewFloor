package nl.groenier.android.firstsurfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Martijn on 22/11/2016.
 */

public class SmartFloorView extends SurfaceView implements SurfaceHolder.Callback {

    private PanelThread _thread;
    private Paint paint = new Paint();

    private int x = 0;
    private int y = 0;

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
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
//        canvas.drawCircle(100,100,50,paint);

        canvas.drawBitmap(leftFootImage,matrix,paint);

        super.onDraw(canvas);
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
                try {
                    currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                c = null;      //set to false and loop ends, stopping thread

                try {

                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {

                        int transX = 1;
                        int transY = 1;
//            int transX = 1;
//            int transY = 1;
                        int transOrientation = 5;

                        x += transX;
                        y += transY;

                        matrix.postTranslate(x, y);

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
