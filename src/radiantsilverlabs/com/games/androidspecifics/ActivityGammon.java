package radiantsilverlabs.com.games.androidspecifics;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import radiantsilverlabs.com.games.R;
import radiantsilverlabs.com.games.lowlevel.Bot;
import radiantsilverlabs.com.games.lowlevel.CustomCanvas;
import radiantsilverlabs.com.games.lowlevel.Main;

public class ActivityGammon extends Activity {
    DrawView drawView;

    public static ActivityGammon me;
                           Context context;
    
    public static MediaPlayer diceroll;
    public static MediaPlayer doublerolled;
    public static MediaPlayer error;
    public static MediaPlayer gameover;
    public static MediaPlayer killed;
    public static MediaPlayer mouseclick;
    public static MediaPlayer nomove;
    public static MediaPlayer pieceputaway;
    public static MediaPlayer resign;
    public static MediaPlayer whoosh;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set full screen view
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

      ///  SurfaceHolder holder = getHolder();


        context= this.getApplicationContext();
        me=this;

        drawView = new DrawView(this);
        setContentView(drawView);
        drawView.requestFocus();



        diceroll = MediaPlayer.create(this, R.raw.diceroll);

        doublerolled =MediaPlayer.create(this, R.raw.dbl);
        error =MediaPlayer.create(this, R.raw.error);
        gameover =MediaPlayer.create(this, R.raw.gameover);
        killed =MediaPlayer.create(this, R.raw.killed);
        mouseclick =MediaPlayer.create(this, R.raw.mouseclick);
        nomove =MediaPlayer.create(this, R.raw.nomove);
        pieceputaway =MediaPlayer.create(this, R.raw.pieceputaway);
        resign =MediaPlayer.create(this, R.raw.resign);
        whoosh =MediaPlayer.create(this, R.raw.whoosh);


       /* new Thread(new Runnable() {
            boolean isRunning = true;
            public void run() {
                long cycleTime = System.currentTimeMillis();
                while(isRunning) {


                    try {
                        c = holder.lockCanvas(null);
                        synchronized (holder) {
                            map.onDraw(c);
                        }
                    } finally {

                        if (c != null) {
                            holder.unlockCanvasAndPost(c);
                        }
                    }



                    System.out.println("repaint.");
                    //DrawView.customcanvas.//Main.canvas.paint();
                    cycleTime = cycleTime + Main.FRAME_DELAY;
                    long difference = cycleTime - System.currentTimeMillis();
                    try {
                        Thread.sleep(Math.max(0, difference));
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();         */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        
        //MAKE MENU SIMPLY FULL AUTO PLAY FOR NOW
        Bot.FULL_AUTO_PLAY=   !Bot.FULL_AUTO_PLAY;
        
        //CustomCanvas.DEBUG_CONSOLE=     !CustomCanvas.DEBUG_CONSOLE;
        //WE DONT NEED MENU IN THIS CASE
        /*
          menu.add(Menu.NONE, ParserType.ANDROID_SAX.ordinal(),
                  ParserType.ANDROID_SAX.ordinal(), R.string.android_sax);
          menu.add(Menu.NONE, ParserType.SAX.ordinal(), ParserType.SAX.ordinal(),
                  R.string.sax);
          menu.add(Menu.NONE, ParserType.DOM.ordinal(), ParserType.DOM.ordinal(),
                  R.string.dom);
          menu.add(Menu.NONE, ParserType.XML_PULL.ordinal(),
                  ParserType.XML_PULL.ordinal(), R.string.pull);
                  */
        return true;
    }

    public Bitmap createImage(String p, int resource) {
        // TODO Auto-generated method stub
        System.out.println("Bitmap.createImage: "+p+" resource int:"+resource);
        return BitmapFactory.decodeResource(context.getResources(), resource);

    }
}