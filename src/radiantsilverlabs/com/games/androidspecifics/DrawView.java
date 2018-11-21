package radiantsilverlabs.com.games.androidspecifics;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 04/01/12
 * Time: 22:14
 * To change this template use File | Settings | File Templates.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import radiantsilverlabs.com.games.gamelogic.Board;
import radiantsilverlabs.com.games.lowlevel.CustomCanvas;
import radiantsilverlabs.com.games.lowlevel.HAL;
import radiantsilverlabs.com.games.lowlevel.Main;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback,  OnTouchListener {
    private static final String TAG = "DrawView";

    List<Point> points = new ArrayList<Point>();
    Paint paint = new Paint();

    public static Canvas canvas;
    public static int WIDTH;
    public static int HEIGHT;
    private GameThread thread;
    Graphics g;
   /// private SurfaceHolder mSurfaceHolder;
    public DrawView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.setOnTouchListener(this);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);





        // create thread only; it's started in surfaceCreated()
        thread = new GameThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                // Use for pushing back messages.
            }
        });

       // m = new Main();
       // m.main();



    }
    public static CustomCanvas customcanvas;
    @Override
    public void onDraw(Canvas canvas_) {
        System.out.println("ondraw "+canvas_);


        WIDTH=getWidth();
        HEIGHT=getHeight();

        if (canvas_==null)
        {
                        System.out.println("WARNING CANVAS IS NULL");

        }
        else
        {

        }
        canvas=canvas_;
        if (g==null)
        {
            g = new Graphics(canvas);
          //  customcanvas = new CustomCanvas();
        }
       // customcanvas.paint(g);

             //   for (Point point : points) {
            //canvas.drawCircle(point.x, point.y, 5, paint);
            // Log.d(TAG, "Painting: "+point);
        //}
    }

    /*public boolean onTouch(View view, MotionEvent event) {
        // if(event.getAction() != MotionEvent.ACTION_DOWN)
        // return super.onTouchEvent(event);
        Point point = new Point();
        point.x = event.getX();
        point.y = event.getY();
        points.add(point);
        invalidate();
        Log.d(TAG, "point: " + point);
        return true;
    }                */

   // public static int WIDTH=0;
   // public static int HEIGHT=0;

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        _("SIZE OF SURFACE: "+width+" x "+height);
        WIDTH=width;
        HEIGHT=height;
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    int editorcounter=0;
    int action, touchX, touchY;
    
    long lastTouched=0;
    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub

                           // STOP BUTTONS PRESSING AGAIN
        //ONLY DO IF SOME TIME HAS PASSED SINCE LASTTIME
        if (System.currentTimeMillis()-100 >lastTouched)
        {
            
        }
        else
        {
            return true;
        }
        
        
        action = arg1.getAction();
        touchX = (int)arg1.getX();
        touchY = (int)arg1.getY();
        System.out.println("JUST TOUCHED["+action+"] @ "+touchX+","+touchY);
        lastTouched=System.currentTimeMillis();

        CustomCanvas.pointerX=touchX;
        CustomCanvas.pointerY=touchY;
        Board.mouseHoverX=touchX;
        Board.mouseHoverY=touchY;


        thread.customcanvas.mouseClickedX(touchX,touchY,CustomCanvas.LEFT_MOUSE_BUTTON);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        /*  //////EDITOR
if (touchX<=35 && touchY<=20)
{
editorcounter++;
if (editorcounter>30)
{
    _("EDITOR");
    thread.gameScreen.keyPressed(Graphics.KEY_NUM4);
    editorcounter=0;
}
}
else
{
editorcounter=0;
}///////////////////


/////UP IN EDITOR
if (T.isIntersectingRect(touchX, touchY, 1, 1, Designer.designerUpX, Designer.designerUpY, Designer.designerUpW, Designer.designerUpH))
{
_("TOUCH UP IN DESIGNER");
thread.gameScreen.keyPressed(Graphics.UP);
return true;
}
/////DOWN IN EDITOR
if (T.isIntersectingRect(touchX, touchY, 1, 1, Designer.designerDownX, Designer.designerDownY, Designer.designerDownW, Designer.designerDownH))
{
_("TOUCH DOWN IN DESIGNER");
thread.gameScreen.keyPressed(Graphics.DOWN);
return true;
}

/////LEFT IN EDITOR
if (T.isIntersectingRect(touchX, touchY, 1, 1, Designer.designerLeftX, Designer.designerLeftY, Designer.designerLeftW, Designer.designerLeftH))
{
_("TOUCH LEFT IN DESIGNER");
thread.gameScreen.keyPressed(Graphics.LEFT);
return true;
}

/////////////////
/////RIGHT IN EDITOR
if (T.isIntersectingRect(touchX, touchY, 1, 1, Designer.designerRightX, Designer.designerRightY, Designer.designerRightW, Designer.designerRightH))
{
_("TOUCH RIGHT IN DESIGNER");
thread.gameScreen.keyPressed(Graphics.RIGHT);
return true;
}

/////FIRE IN EDITOR
if (T.isIntersectingRect(touchX, touchY, 1, 1, Designer.designerFireX, Designer.designerFireY, Designer.designerFireW, Designer.designerFireH))
{
_("TOUCH FIRE IN DESIGNER");
thread.gameScreen.keyPressed(Graphics.FIRE);
return true;
}

//TOUCH CURSOR (0)
if (T.isIntersectingRect(touchX, touchY, 1, 1, World.EDcursorX, World.EDcursorY, World.TILE_WIDTH, World.TILE_HEIGHT))
{
_("TOUCHED CURSOR IN DESIGNER");
thread.gameScreen.keyPressed(Graphics.KEY_NUM0);
return true;
}


if (touchX<=getWidth()/2 && touchY>=HEIGHT/2)
{
GameScreen.touchedScreenLeft=true;
GameScreen.touchedScreenRight=false;
GameScreen.hudcounter=0;
}
else if (touchX>=getWidth()/2 && touchY>=HEIGHT/2)
{
GameScreen.touchedScreenLeft=false;
GameScreen.touchedScreenRight=true;
GameScreen.hudcounter=0;
}

//LEFT
if (touchX<GameScreen.arrowleft.getWidth() && touchY>getHeight()-GameScreen.arrowleft.getHeight())
{
if (action ==MotionEvent.ACTION_DOWN)
{
    T._("LDOWN");


    thread.gameScreen.goingright=false;
    thread.gameScreen.goingleft=true;
}
if (action ==MotionEvent.ACTION_UP)
{
    T._("LUP");
    thread.gameScreen.goingright=false;
    thread.gameScreen.goingleft=false;
}
}
//RIGHT
if (touchX>getWidth()-GameScreen.arrowright.getWidth() && touchY>getHeight()-GameScreen.arrowright.getHeight())
{
if (action ==MotionEvent.ACTION_DOWN)
{
    T._("RDOWN");

    thread.gameScreen.goingright=true;
    thread.gameScreen.goingleft=false;
}
if (action ==MotionEvent.ACTION_UP)
{
    T._("RUP");

    thread.gameScreen.goingright=false;
    thread.gameScreen.goingleft=false;
}
}
// JUMP LEFT
if (touchX>0 && touchX<GameScreen.arrowright.getWidth() && touchY>getHeight()-GameScreen.arrowright.getHeight()*2 && touchY<getHeight()-GameScreen.arrowright.getHeight() )
{
T._("JUMPL");
thread.gameScreen.keyPressed(Graphics.UP);
}
// JUMP RIGHT
if (touchX>GameScreen.WIDTH-GameScreen.arrowright.getWidth() && touchY>getHeight()-GameScreen.arrowright.getHeight()*2 && touchY<getHeight()-GameScreen.arrowright.getHeight() )
{
T._("JUMPR");
thread.gameScreen.keyPressed(Graphics.UP);
}

// FIRE LEFT
if (touchX>GameScreen.arrowright.getWidth() && touchX<GameScreen.arrowright.getWidth()+GameScreen.arrowfire.getWidth() && touchY>getHeight()-GameScreen.arrowright.getHeight()  )
{
T._("FIREL");
//thread.gameScreen.keyPressed(Graphics.FIRE);
}
// FIRE RIGHT
if (touchX>GameScreen.WIDTH-(GameScreen.arrowright.getWidth()+GameScreen.arrowfire.getWidth()) && touchX<GameScreen.WIDTH-GameScreen.arrowright.getWidth() && touchY>getHeight()-GameScreen.arrowright.getHeight()  )
{
T._("FIRER");
//thread.gameScreen.keyPressed(Graphics.FIRE);
}

        */
        return true;
    }



    // wrapper around system out to console
    private static void _(String s)
    {
        HAL._("DrawView{}:" + s);
    }
}





class Point {
    float x, y;

    @Override
    public String toString() {
        return x + ", " + y;
    }
}