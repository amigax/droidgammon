package radiantsilverlabs.com.games.androidspecifics;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import radiantsilverlabs.com.games.lowlevel.CustomCanvas;

class GameThread extends Thread  {
    /*
    * State-tracking constants
    */
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;

    private float x;
    private float y;

    private static final int SPEED = 100;
    private boolean dRight;
    private boolean dLeft;
    private boolean dUp;
    private boolean dDown;

    public int mCanvasWidth;
    public int mCanvasHeight;

    private long mLastTime;
    private Bitmap mSnowflake;

    /** Message handler used by thread to post stuff back to the GameView */
    private Handler mHandler;

    /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
    private int mMode;
    /** Indicate whether the surface has been created & is ready to draw */
    private boolean mRun = false;
    /** Handle to the surface manager object we interact with */
    private SurfaceHolder mSurfaceHolder;
    Context  mContext;
    /**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 06/01/12
 * Time: 23:42
 * To change this template use File | Settings | File Templates.
 */
public GameThread(SurfaceHolder surfaceHolder, Context context,
        Handler handler) {
        // get handles to some important objects
        mSurfaceHolder = surfaceHolder;
mHandler = handler;
mContext = context;

// x = 10;
//y = 10;

//mSnowflake = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.snowflake);
}




/**
 * Starts the game, setting parameters for the current difficulty.
 */
public void doStart() {
synchronized (mSurfaceHolder) {
        // Initialize game here!

        // x = 10;
        // y = 10;

        mLastTime = System.currentTimeMillis() + 100;
setState(STATE_RUNNING);
}
        }

/**
 * Pauses the physics update & animation.
 */
public void pause() {
synchronized (mSurfaceHolder) {
        if (mMode == STATE_RUNNING)
        setState(STATE_PAUSE);
}
        }

//GAZ METHODS
public Bitmap createImage(String p, int resource) {
        // TODO Auto-generated method stub
        System.out.println("Bitmap.createImage: "+p+" resource int:"+resource);
return BitmapFactory.decodeResource(mContext.getResources(), resource);

}
      public CustomCanvas customcanvas;
@Override
public void run() {
        while (mRun) {
            //System.out.println("running");
        Canvas c = null;
try {
        c = mSurfaceHolder.lockCanvas(null);

if (customcanvas==null)
        {
            customcanvas = new CustomCanvas();// create game here so it gets a workign non null canvas.
//gameScreen.run();
}


synchronized (mSurfaceHolder) {
        if (mMode == STATE_RUNNING)
        {
        updateGame();
}
        doDraw(c);
    customcanvas.paint(new Graphics(c)); //SPEED THIS UP VERY SLOW
//gameScreen.cycle();


try {
        this.sleep(30);
} catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
}

        /*moveStopper++;
        if (moveStopper>5)
        {
            moveStopper=0;
            gameScreen.goingleft=false;
            gameScreen.goingright=false;
        }*/
        }
        } finally {
        // do this in a finally so that if an exception is thrown
        // during the above, we don't leave the Surface in an
        // inconsistent state
        if (c != null) {
        mSurfaceHolder.unlockCanvasAndPost(c);
}
        }
        }
        }

        int moveStopper=0;

/**
 * Used to signal the thread whether it should be running or not.
 * Passing true allows the thread to run; passing false will shut it
 * down if it's already running. Calling start() after this was most
 * recently called with false will result in an immediate shutdown.
 *
 * @param b true to run, false to shut down
 */
public void setRunning(boolean b) {
        mRun = b;
}

/**
 * Sets the game mode. That is, whether we are running, paused, in the
 * failure state, in the victory state, etc.
 *
 * @see #setState(int, CharSequence)
 * @param mode one of the STATE_* constants
 */
public void setState(int mode) {
synchronized (mSurfaceHolder) {
        setState(mode, null);
}
        }

/**
 * Sets the game mode. That is, whether we are running, paused, in the
 * failure state, in the victory state, etc.
 *
 * @param mode one of the STATE_* constants
 * @param message string to add to screen or null
 */
public void setState(int mode, CharSequence message) {
synchronized (mSurfaceHolder) {
        mMode = mode;
}
        }

/* Callback invoked when the surface dimensions change. */
public void setSurfaceSize(int width, int height) {
// synchronized to make sure these all change atomically
synchronized (mSurfaceHolder) {
        mCanvasWidth = width;
mCanvasHeight = height;
}
        }

/**
 * Resumes from a pause.
 */
public void unpause() {
// Move the real time clock up to now
synchronized (mSurfaceHolder) {
        mLastTime = System.currentTimeMillis() + 100;
}
        setState(STATE_RUNNING);
}

        /**
         * Handles a key-down event.
         *
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true
         */

        boolean doKeyDown(int keyCode, KeyEvent msg) {

        System.out.print("DOKEYDOWN "+keyCode);
boolean handled = false;
synchronized (mSurfaceHolder) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_P){
        dRight = true;
//gameScreen.keyPressed(Graphics.RIGHT);
handled = true;
}
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT|| keyCode == KeyEvent.KEYCODE_O){
        dLeft = true;
//gameScreen.keyPressed(Graphics.LEFT);
handled = true;
}
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP|| keyCode == KeyEvent.KEYCODE_Q){
        dUp = true;
//gameScreen.keyPressed(Graphics.UP);
handled = true;
}
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN|| keyCode == KeyEvent.KEYCODE_A){
        dDown = true;
//gameScreen.keyPressed(Graphics.DOWN);
handled = true;
}
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ){

        //_("editor");
//gameScreen.keyPressed(Graphics.KEY_NUM4);
handled = true;
}
        return handled;
}
        }
public boolean onTrackballEvent(MotionEvent me) {
       return false;// return thread.doTrackballEvent(me);
}

public boolean doTrackballEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        //_("PAUSE");
//MainMenu.gamePlay=false;
//MainMenu.mainMenu=true;
//game.pauseMe();



return true;
case MotionEvent.ACTION_UP:

        return true;
case MotionEvent.ACTION_CANCEL:

        return true;
case MotionEvent.ACTION_MOVE:
        // they stopped doign it.
        //	game.keyReleased(Device.KEY_LEFT);
        //game.keyReleased(Device.KEY_RIGHT);
        //game.keyReleased(Device.KEY_UP);
        //game.keyReleased(Device.KEY_DOWN);


       // T._("MOVE TRACKIE");


if (mContext != null) {
        float x = event.getX() * event.getXPrecision();
float y = event.getY() * event.getYPrecision();

if (x<0)
        {
        //LEFT
       // gameScreen.keyPressed(Graphics.LEFT);

}
        else
        {
        //RIGHT
      //  gameScreen.keyPressed(Graphics.RIGHT);
}
        if (y<0)
        {
        //UP
      //  gameScreen.keyPressed(Graphics.UP);
}
        else
        {
        //DOWN
      //  gameScreen.keyPressed(Graphics.DOWN);
}
        }
        return true;
}


        return true;//super.onTrackballEvent(event);
}


        /**
         * Handles a key-up event.
         *
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true if the key was handled and consumed, or else false
         */
        boolean doKeyUp(int keyCode, KeyEvent msg) {
        boolean handled = false;
synchronized (mSurfaceHolder) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
        dRight = false;
//gameScreen.keyReleased(Graphics.RIGHT);
handled = true;
}
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
        dLeft = false;
//gameScreen.keyReleased(Graphics.LEFT);
handled = true;
}
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
        dUp = false;
//gameScreen.keyReleased(Graphics.UP);
handled = true;
}
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
        dDown = false;
//gameScreen.keyReleased(Graphics.DOWN);
handled = true;
}
        return handled;
}
        }

/**
 * Draws the ship, fuel/speed bars, and background to the provided
 * Canvas.
 */
private void doDraw(Canvas canvas) {
        // empty canvas
        canvas.drawARGB(255, 255, 0, 0);

    canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

//canvas.drawBitmap(mSnowflake, x, y++, new Paint());
//if (y>mCanvasHeight)
//	y=0;
}
    /**
     * Updates the game.
     */
    private void updateGame() {
        //// <DoNotRemove>
        long now = System.currentTimeMillis();
        // Do nothing if mLastTime is in the future.
        // This allows the game-start to delay the start of the physics
        // by 100ms or whatever.
        if (mLastTime > now)
            return;
        double elapsed = (now - mLastTime) / 1000.0;
        mLastTime = now;
        //// </DoNotRemove>

        /*
        * Why use mLastTime, now and elapsed?
        * Well, because the frame rate isn't always constant, it could happen your normal frame rate is 25fps
        * then your char will walk at a steady pace, but when your frame rate drops to say 12fps, without elapsed
        * your character will only walk half as fast as at the 25fps frame rate. Elapsed lets you manage the slowdowns
        * and speedups!
        */

        /*  if (dUp)
                  y -= elapsed * SPEED;
              if (dDown)
                  y += elapsed * SPEED;
              if (y < 0)
                  y = 0;
              else if (y >= mCanvasHeight - mSnowflake.getHeight())
                  y = mCanvasHeight - mSnowflake.getHeight();
              if (dLeft)
                  x -= elapsed * SPEED;
              if (dRight)
                  x += elapsed * SPEED;
              if (x < 0)
                  x = 0;
              else if (x >= mCanvasWidth - mSnowflake.getWidth())
                  x = mCanvasWidth - mSnowflake.getWidth();*/
    }
}
