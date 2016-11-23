package nl.groenier.android.firstsurfaceview;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Martijn on 23/11/2016.
 */

public class CanvasObject {

    private int x;
    private int y;
    private int orientation;

    public CanvasObject(int x, int y, int orientation) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation % 360 ;
    }

}
