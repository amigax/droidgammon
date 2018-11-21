/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radiantsilverlabs.com.games.lowlevel;
//import WindowResizeDetector.WindowResizeEvent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import radiantsilverlabs.com.games.androidspecifics.*;
import android.graphics.drawable.shapes.Shape;
import android.view.KeyEvent;
import radiantsilverlabs.com.games.R;
//import radiantsilverlabs.com.games.androidspecifics.*;
import radiantsilverlabs.com.games.androidspecifics.DrawView;
import radiantsilverlabs.com.games.androidspecifics.Graphics;
import radiantsilverlabs.com.games.androidspecifics.MouseEvent;
import radiantsilverlabs.com.games.gamelogic.*;
//import java.awt.image.BufferStrategy;
//import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
//import javax.swing.JFrame;
/**
 *
 * @author Gaz
 */
public class CustomCanvas // implements MouseListener, MouseMotionListener, KeyListener//, WindowResizeListener
{
    public static final String VERSION="www.radiantsilverlabs.com [v0.4.5]";
    public static final boolean RELEASE_BUILD=false;//set to true for release
    public static boolean SOUND_ON=true;
    public static boolean showCollisions=false; // debug
    boolean PAINT_STATE=false;                  //debug
    public static final String DEBUG_HEADER="Backgammon (DEBUG MODE):";
        
    // possible states:
    public static final int SPLASH_SCREEN=0;
    public static final int OPTIONS_SCREEN_LOCAL_OR_NETWORK=1;// local or network?
    public static final int OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN=2;//play human or cpu?
    public static final int OPTIONS_SCREEN_NETWORK_USERNAME_OF_OPPONENT=3;
    public static final int GAME_IN_PROGRESS=4;
    public static final int NETWORKING_ENTER_NAME=5;
    public static final int NETWORKING_LOBBY=6;
    // intro menu
    //splash->
    //      'local play' or-> 'player against a computer or human?'
    //      'network play'?-> 'enter username of opponent'
    
    int typeOfPlay=-1;
    public static final int NETWORK_PLAY=1;
    public static final int LOCAL_PLAY=2;
    int typeOfOpponent=-1;
    public static final int CPU=1;
    public static final int HUMAN=2;

    // -- constants
    public static int PANEL_COLOUR=0x000000;
    public static int BACKGROUND_COLOUR=0x993300;
    public static int ROLL_BUTTON_COLOUR=0xffcc66;
    public static final int ORANGE = 0xFF9900;
    public static Color panel_colour, background_colour, roll_button_colour;
    public static final int SPLASH_COUNTER=50;
    // -- turn on and off anti aliasing
    public static boolean ANTI_ALIAS=true;
    public static final int PANEL_SIZE_FRACTION=5; // adjust me to change ratio:
    //this simply means the panel will represent one x-th of the available screen,
    // ergo if PANEL_SIZE_FRACTION is 5, it uses 1/5 of the space avail and the game
    // uses the other 4/5
    public static int ULTIMATE_WIDTH  =-1;// this is the width of the entire canvas (ie not just board itself but also panel etc)
    public static int ULTIMATE_HEIGHT =-1;//

    boolean INFO=false;    // 'about box' toggle
    HAL hal = new HAL();   // Hardware Abstraction Layer
    public static int state;             // current state
    String stateString;
    int PANEL_WIDTH=0;
    Graphics g;
    public Bot bot = new Bot(this); // make a robotic player who can move mouse etc, for demo and test automation and cpu player

    /////j2se specific vars
    /*JFrame jFrame;
    // Acquiring the current Graphics Device and Graphics Configuration
    GraphicsEnvironment   graphEnv     = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice        graphDevice  = graphEnv.getDefaultScreenDevice();
    GraphicsConfiguration graphicConf  = graphDevice.getDefaultConfiguration();
    Graphics2D g;
    BufferStrategy bufferStrategy;   */
    /////////////////

    /*public void windowResized(WindowResizeEvent e) {
System.out.println("WINDOW RESIZED! "+e);

//horrible hack to fix graphics, basically its flash liek mad unless i set ignorerepaint, that makes it fine but then it ignores
// OS calls to repaint the window so stays white when resized, the window event listwener now detects OS calls to window changed size
// allowing us to unset ignore repaint for a few ticks and then set it again so it should look ok after that agt the new size.
//
ignorePaintsCounter=0;


//setIgnoreRepaint(true);
}     */
    public static int ignorePaintsCounter=0;

    /* This class is used basically for calling the right paint methods
     * based on state, these paint due to this class being a subclass of canvas.
    */
   public CustomCanvas()//JFrame jFrame_)
    {
        _("CustomCanvas made.");
        bot.start();
       
        // j2se specifics
       // jFrame=jFrame_;
        //addMouseListener(this);
       // addMouseMotionListener( this );
       // addKeyListener( this );
        //set icon in corner
       // jFrame.setIconImage(hal.loadImage("/icon.gif"));
        /////

//jFrame.setResizable(false);


      //  setTheme(theme);
        makeColourObjects(false);

        loadCustomFonts();
        loadImages();

        //load sounds
        _("Loading Sounds");

  
          

      /*  sfxmouseClick = new Sound("/mouseclick.wav");
        sfxDiceRoll   = new Sound("/diceroll.wav");
        sfxDoubleRolled = new Sound("/whoosh.wav");
        sfxError=new Sound("/error.wav");
        sfxNoMove=new Sound("/nomove.wav");
        sfxPutPieceInContainer=new Sound("/pieceputaway.wav");
        sfxGameOver=new Sound("/gameover.wav");
        sfxKilled=new Sound("/killed.wav");
        sfxdouble=new Sound("/double.wav");
        sfxResign=new Sound("/resign.wav");
        _("Sounds loaded.");        */
       // requestFocus();  // get focus for keys
       // setIgnoreRepaint(true);//this is the key to it not flickering on my desktop

 
        
 }
    
    
    private int getWidth()
    {
                        // _("WIDTH IS "+ DrawView.WIDTH);
        return DrawView.WIDTH;
    }
    private int getHeight()
    {
       // _("HEIGHT IS "+DrawView.HEIGHT);
        return DrawView.HEIGHT;
    }
    
public static boolean NETWORK_GAME_IN_PROCESS;
public static Sound sfxmouseClick, sfxDiceRoll, sfxDoubleRolled,sfxError,sfxNoMove,sfxPutPieceInContainer, sfxGameOver, sfxKilled;
public static Sound sfxdouble, sfxResign;
// paint depending on state
 public void paint(Graphics g)//Graphics g)
 {
    // create the buffering strategy which will allow AWT
// to manage our accelerated graphics
//jFrame.createBufferStrategy(2);
//bufferStrategy = jFrame.getBufferStrategy();
  // Graphics2D g_ =null;
 //  g_= (Graphics2D)bufferStrategy.getDrawGraphics();
////

////
      // g = (Graphics2D) bufferStrategy.getDrawGraphics();


        

    handleMouse();


   // doubleBuffering(1); // pass 1 in to start dbl buffering
   // if (ANTI_ALIAS)
   // {
   //     doAntiAliasing();
   // }

    //we refresh these on each paint loop so it always scales no matter what
    //WIDTH and HEIGHT are used to pass into board so everything scales
    //nice. the panel to the side represents 1/xth of the avail space.
    ULTIMATE_WIDTH=getWidth();//whole canvas
    ULTIMATE_HEIGHT=getHeight();
    // whole game canvas:
    WIDTH  = (getWidth()/PANEL_SIZE_FRACTION)*(PANEL_SIZE_FRACTION-1);
    PANEL_WIDTH=(getWidth()/PANEL_SIZE_FRACTION)-Board.BORDER;
    HEIGHT = getHeight();

//checkOffscreenImage();
  //  Graphics g = mImage.getGraphics();

    
    






    //all painting:
    //if (ANTI_ALIAS)
    //{
        paintSwitch(g);//paint with the anti alias Graphics object
    //}
   // else
   /// {
    //    //(gNoAntiAliasing);//paint with normal Graphics object
    //}
   


 
        //g.dispose();
	//bufferStrategy.show();

    if (drawPointer)
    {

        //I STUCK THIS IN HERE AS A TEST IT WASNT ACTUALLY HERE:
        //_("DRAW POINTER "+pointerX+" X "+pointerY);
        hal.drawImage(g, pointer, pointerX, pointerY+6);//this 6 lines it up


        ////if (numberOfFirstRollsDone>1)//ie game started
        {

            if (NETWORK_GAME_IN_PROCESS)
            {
                //System.out.println("NETWORK_GAME_IN_PROCESS "+pointerX+" , "+pointerY);
                  hal.drawImage(g, pointer, pointerX, pointerY+6);//this 6 lines it up
                       Board.mouseHoverX=pointerX;//e.getX();
                          Board.mouseHoverY=pointerY;//e.getY();
            }
            else
            {

                if (Bot.FULL_AUTO_PLAY)
                {
                    hal.drawImage(g, pointer, Bot.x, Bot.y+6);//this 6 lines it up
                    Board.mouseHoverX=Bot.x;//e.getX();
                    Board.mouseHoverY=Bot.y;//e.getY();
                }
                else
                {
                    if ( Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.WHITE)
                    {
                         Main.hideMousePointer(false);

                         //REMOVED TO TEST NETWORKING
                         /*
                       hal.drawImage(g, pointer, pointerX, pointerY+6, this_);//this 6 lines it up
                       Board.mouseHoverX=pointerX;//e.getX();
                          Board.mouseHoverY=pointerY;//e.getY();*/
                    }else
                    if ( Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.BLACK)
                    {
                       /// _("bot.dead set to false");
                        Main.hideMousePointer(true);
                        Bot.dead=false;
                        hal.drawImage(g, pointer, Bot.x, Bot.y+6);//this 6 lines it up
                        Board.mouseHoverX=Bot.x;//e.getX();
                        Board.mouseHoverY=Bot.y;//e.getY();
                    }else
                    {
                         Main.hideMousePointer(false);
                         /* hal.drawImage(g, pointer, pointerX, pointerY+6, this_);//this 6 lines it up
                          Board.mouseHoverX=pointerX;//e.getX();
                          Board.mouseHoverY=pointerY;//e.getY();*/
                    }
                }
            }
            
        }

        /*if ((Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.WHITE))//Bot.dead)
        {
            hal.drawImage(g_, pointer, pointerX, pointerY+6, this_);//this 6 lines it up
        }
        else
        {
              hal.drawImage(g_, pointer, Bot.x, Bot.y+6, this_);//this 6 lines it up
        }*/
        /*if (Bot.FULL_AUTO_PLAY)
        {
            hal.drawImage(g_, pointer, Bot.x, Bot.y+6, this_);//this 6 lines it up
            desinationsFromWhichSource="Bot X";
        } else
        if ( Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.WHITE)
        {
            //draw human pointer
            hal.drawImage(g_, pointer, pointerX, pointerY+6, this_);//this 6 lines it up
            desinationsFromWhichSource="Human X";
        } else
        if ( Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.BLACK)
        {
            hal.drawImage(g_, pointer, Bot.x, Bot.y+6, this_);//this 6 lines it up
            desinationsFromWhichSource="Bot X1";
        } else
        {
            //draw human pointer
            hal.drawImage(g_, pointer, pointerX, pointerY+6, this_);//this 6 lines it up
            desinationsFromWhichSource="Human X1";
        }*/
        

    }


  
 //doubleBuffering(2); // pass 2 in to start dbl buffering
   
    
 }

 /*
  * The pointer is a wrapper over the real pointer, this is because at times we want the computer to control the mouse
  */
 private void handleMouse()
 {
    /* if (!Bot.TAKES_OVER_MOUSE)//only true when we use the Robot class. (ie testing never release)
     {
         if (!Bot.FULL_AUTO_PLAY)
         {
                if (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.WHITE)
                {
                    // IE: its a human vs computer game
                    Board.mouseHoverX=pointerX;//e.getX();
                    Board.mouseHoverY=pointerY;//e.getY();
                    //desinationsFromWhichSource="Human A";
                } else
                {
                    
                    Board.mouseHoverX=Bot.x;//pointerX;//e.getX();
                    Board.mouseHoverY=Bot.y;//pointerY;//e.getY();
                    //desinationsFromWhichSource="Human B";
                }

         }
         else if (!Bot.dead)
         {
                    Board.mouseHoverX=Bot.x;//e.getX();
                    Board.mouseHoverY=Bot.y;//e.getY();
                    desinationsFromWhichSource="Bot A";
                    Bot.currentMouseX=Board.mouseHoverX;
                    Bot.currentMouseY=Board.mouseHoverY;
                    pointerX=Bot.x;
                    pointerX=Bot.y;
         }
     }*/
 }

public static String desinationsFromWhichSource;//for debug window
 // implements double buffering, phase is 1 or 2, 1 is called before
 // painting and 2 is called after. Any other phase is erroneous
 private void doubleBuffering(int phase)
 {
    if (phase==1)
    {
        //START DBL BUFFERING
      //  this.createBufferStrategy(2); // must be after we are visible!
       // bufferStrategy = this.getBufferStrategy();
       // g = (Graphics2D)bufferStrategy.getDrawGraphics();

    } else
    if (phase==2)
    {
 

        //END DBL BUFFERING

        //bufferStrategy.show();
    
    } else
    {
        HAL._E("doubleBuffering() phase was invalid "+phase);
    }
 }
  private Bitmap mImage;
private void checkOffscreenImage() {
   // Dimension d = getSize();
   // if (mImage == null || mImage.getWidth(null) != d.width
   //     || mImage.getHeight(null) != d.height) {
   //   mImage = createImage(d.width, d.height);
   // }
  }
 //this will simply suggest anti aliasing and set up g2 as the graphics object to use
 private void doAntiAliasing()
 {
      //g2 = (Graphics2D)g;
      //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 }

 // calls a different paint method based on the current state
 private void paintSwitch(Graphics g)
 {
     /*if (CustomCanvas.numberOfFirstRollsDone>1)//ie if game started.
    {
     //hide roll button while they move their pieces.
     showRollButton=false;
    // _("hiding show roll button");
    }*/

     hal.bg(g,Color.WHITE,ULTIMATE_WIDTH,ULTIMATE_HEIGHT);

    ///////the state machine////////
    switch(state)
    {
        case SPLASH_SCREEN:///////////////////////////////////////
            stateString="SPLASH_SCREEN";
            paint_SPLASH_SCREEN(g);
            break;
        case OPTIONS_SCREEN_LOCAL_OR_NETWORK://///////////////////
            stateString="OPTIONS_SCREEN_LOCAL_OR_NETWORK";
            paint_OPTIONS_SCREEN_LOCAL_OR_NETWORK(g," Local Play ","Network Play","Please select");
            // make buttons glow if hovered over//
            glowButton(Board.mouseHoverX,Board.mouseHoverY);
            break;
        case OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN://///////////
            /*note since 2 states require the same thing, (a question with 2
             options) We simply re-use this state's paint method and pass in the
             right strings to make it work*/
            stateString="OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN";
            // ie option 1, option 2, question
            paint_OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN(g,"Computer"," Human  ","Play against");
            // make buttons glow if hovered over//
            glowButton(Board.mouseHoverX,Board.mouseHoverY);
            break;
        case GAME_IN_PROGRESS:///////////////////////////////////
            stateString="POST_SPLASH_SCREEN";
            paint_POST_SPLASH_SCREEN(g);
            break;
        case NETWORKING_ENTER_NAME:///////////////////////////////
            stateString="NETWORKING_ENTER_NAME";
            paint_NETWORKING_ENTER_NAME(g);
            break;
        case NETWORKING_LOBBY:
            stateString="NETWORKING_LOBBY";
            paint_NETWORKING_LOBBY(g);
            break;
        default://///////////////////////////////////////////////
            HAL._E("Warning state in paint unrecognised!");
            break;
    }
    ////////////////////////////////
    drawExtras(g); //draw extra hud stuff
   //_("stateString:"+stateString);

    //paint_NETWORKING_LOBBY(g);
 }

 //draws any of the extras: 
 //debug info, about box, messages to players etc
 private void drawExtras(Graphics g)
 {
    if (PAINT_STATE) // purely for debugging, the state is painted in the corner
    {
        paintState(g);
    }
    if (INFO) //draws the about box
    {
        paintAboutBox(g);
    }
    if (HAL.CANVAS_LOGGING) // again for debugging, paints sys outs on canvas
    {
        paintStringsToCanvas(g);
    }
    if(showPlayerMessage)//showMeFor++<SHOW_ME_LIMIT)  //paint messages to players
    {
       
         //all of this in aid of a loop that lasts for x amount of seconds not a cpu dependent tick,
          //could be a bit over the top for what im doign (todo optimise?)
        playerMessageTimePassedLong=System.currentTimeMillis()-playerMessageSetTimeLong;
         //_("playerMessageTimePassedLong:"+playerMessageTimePassedLong);

         /*if (playerMessageTimePassedLong>FIFTY_SECONDS)//so we dont have a mad long getting bigger and bigger
         {
             playerMessageTimePassedLong=TEN_SECONDS;//so it doesnt bring message back
             playerMessageSetTimeLong=System.currentTimeMillis()-TEN_SECONDS;
         }*/
        //_("playerMessageTimePassedLong:"+playerMessageTimePassedLong);
        if(playerMessageTimePassedLong<SHOW_ME_LIMIT )  //paint messages to players
        {
            
            paintMessageToPlayers(g);
        }
        else
        {
            if (state==GAME_IN_PROGRESS)
                showPlayerMessage=false;
        }

        
    }
    if (Bot.dead==false)
    {
      if (paintRobotMessages)
      {
          //all of this in aid of a loop that lasts for x amount of seconds not a cpu dependent tick,
          //could be a bit over the top for what im doign (todo optimise?)
        robotMessageTimePassedLong=System.currentTimeMillis()-robotMessageSetTimeLong;
        // _("robotMessageTimePassedLong:"+robotMessageTimePassedLong);

         if (robotMessageTimePassedLong>FIFTY_SECONDS)//so we dont have a mad long getting bigger and bigger
         {
             robotMessageTimePassedLong=TEN_SECONDS;//so it doesnt bring message back
             robotMessageSetTimeLong=System.currentTimeMillis()-TEN_SECONDS;
         }
        if(/*showMeForROBOT++*/robotMessageTimePassedLong<SHOW_ME_LIMIT )  //paint messages to players
        {
            paintRobotMessage(g);
        }
      }
       
        
    }
    if (DEBUG_CONSOLE)
    {
        paintDebugBox(g);
    }
 }
 public static long FIFTY_SECONDS=50000L;
         public static long TEN_SECONDS=10000L;
static long robotMessageTimePassedLong;
static long playerMessageTimePassedLong;
static long robotMessageSetTimeLong;
 public static int TRANSPARENCY_LEVEL=100;
 //paints the about box
 private void paintAboutBox(Graphics g)
 {
    infoCounter++;
    if (infoCounter>SPLASH_COUNTER)
    {
        infoCounter=0;
        INFO=false;
    }

    hal.setColor(g, 0,0,0,TRANSPARENCY_LEVEL);
    hal.fillRoundRect(g, WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2,true);
    hal.setColor(g, Color.WHITE);
    hal.drawRoundRect(g, WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2);

    int xabout=WIDTH/2;
    int yabout=(HEIGHT/4)+TINY_GAP;

    //paint the about box
    printme="Forumosa Backgammon ("+VERSION+")";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme="- www.forumosa.com -";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme="Keys:    ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme="q = quit ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme="t = theme";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme=" ";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
    printme="Developed by www.garethmurfin.co.uk";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
 }

 //paints the about box
 private void paintDebugBox(Graphics g)
 {
    infoCounter++;
    if (infoCounter>SPLASH_COUNTER)
    {
        infoCounter=0;
        INFO=false;
    }

    hal.setColor(g, 0,0,0,125);
    x=10;
    y=10;
    hal.fillRoundRect(g, x, y, WIDTH/2, HEIGHT-40,true);
    hal.setColor(g, Color.YELLOW);
    hal.drawRoundRect(g, x, y, WIDTH/2, HEIGHT-40);

    x+=5;
    
    //int xabout=WIDTH/2;
    //int yabout=(HEIGHT/4)+TINY_GAP;
    y+=TINY_GAP;
    //paint the about box
    printme="Backgammon ("+VERSION+") DEBUG CONSOLE";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();

    printme="TIME_DELAY_BETWEEN_CLICKS:"+Bot.TIME_DELAY_BETWEEN_CLICKS;
    //Highlighter to indicate if its on this option
    if (debugMenuPos==0) {hal.drawRoundRect(g, x, y, fontwhite.stringWidth(printme+" "), fontwhite.getHeight());}
    //option itself, 
    fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();


    printme="ROBOT_DELAY_AFTER_CLICKS:"+Bot.ROBOT_DELAY_AFTER_CLICKS;
    //Highlighter to indicate if its on this option
    if (debugMenuPos==1) {hal.drawRoundRect(g, x, y, fontwhite.stringWidth(printme+" "), fontwhite.getHeight());}
    //option itself,
    fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();

     printme="paintRobotMessages:"+paintRobotMessages;
    //Highlighter to indicate if its on this option
    if (debugMenuPos==2) {hal.drawRoundRect(g, x, y, fontwhite.stringWidth(printme+" "), fontwhite.getHeight());}
    //option itself,
    fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();

    printme="FULL_AUTO_PLAY:"+Bot.FULL_AUTO_PLAY;
    //Highlighter to indicate if its on this option
    if (debugMenuPos==3) {hal.drawRoundRect(g, x, y, fontwhite.stringWidth(printme+" "), fontwhite.getHeight());}
    //option itself,
    fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();





    y+=10;
    printme="Q = QUIT";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="P = PAUSE (bot dead? "+Bot.dead+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="D = DEBUG CONSOLE ("+DEBUG_CONSOLE+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="T = THEME ("+themeName+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="C = COLLISIONS ("+showCollisions+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="L = CANVAS LOGGING ("+HAL.CANVAS_LOGGING+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="S = SOUND ("+SOUND_ON+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="X = TEST SOUND";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="J = JUMP TO DESTINATION ("+Bot.JUMP_DIRECT_TO_DEST+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="F = FULL_AUTO_PLAY ("+Bot.FULL_AUTO_PLAY+")";fontwhite.drawString(g, printme,x,y,0);//y+=fontblack.getHeight();


    y+=5;
    printme=Board.ROBOT_DESTINATION_MESSAGE;

    y=drawMeWrapped(g,x,y,printme,fontwhite,"",false,false,false,true,WIDTH/2,false);
    if (robotMoveDesc.length()<20)//avoid printing textual things, just moves.
    {
        printme="Bot is thinking:"+robotMoveDesc;fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    }
    if (Board.listBotsOptions && Board.botOptions.length()>4)//avoid printing textual things, just moves.
    {
        printme="Alternatives:";fontwhite.drawString(g, printme,x,y,0);/////y+=fontblack.getHeight();
        //Graphics g,int y, String wrapMe, CustomFont font,String newLineChar,boolean backdrop,boolean scrollbar,boolean outline,boolean justifyleft)
        printme=Board.botOptions;
        y=drawMeWrapped(g,x,y,printme,fontwhite,"",false,false,false,true,WIDTH/2,false);
        //printme=Board.botOptions;fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();

    }
    printme="BAR:W("+theBarWHITE.size()+"),B("+theBarBLACK.size()+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();
    printme="DIE Used?:("+Board.die1HasBeenUsed+"),("+Board.die2HasBeenUsed+")";fontwhite.drawString(g, printme,x,y,0);y+=fontblack.getHeight();


    
 }

 int debugMenuPos=0;
public static final int DEBUG_OPTION_TIME_DELAY_BETWEEN_CLICKS=0;
public static final int DEBUG_OPTION_ROBOT_DELAY_AFTER_CLICKS=1;
public static final int DEBUG_OPTION_paintRobotMessages=2;
public static final int DEBUG_OPTION_FULL_AUTO_PLAY=3;
        public static final int LAST_DEBUG_OPTION=3;

public static final int DEBUGLEFT=1;
public static final int DEBUGRIGHT=2;

 private void debugOptionChanged(int direction)
 {
     switch(debugMenuPos)
     {
         //TIME_DELAY_BETWEEN_CLICKS
         case DEBUG_OPTION_TIME_DELAY_BETWEEN_CLICKS:
             if (direction==DEBUGLEFT)
             {
                 Bot.TIME_DELAY_BETWEEN_CLICKS-=10;
                 _("TIME_DELAY_BETWEEN_CLICKS:"+Bot.TIME_DELAY_BETWEEN_CLICKS);
             }
             if (direction==DEBUGRIGHT)
             {
                 Bot.TIME_DELAY_BETWEEN_CLICKS+=10;
                 _("TIME_DELAY_BETWEEN_CLICKS:"+Bot.TIME_DELAY_BETWEEN_CLICKS);
             }
             break;
         //DEBUG_OPTION_ROBOT_DELAY_AFTER_CLICKS
         case DEBUG_OPTION_ROBOT_DELAY_AFTER_CLICKS:
             if (direction==DEBUGLEFT)
             {
                 Bot.ROBOT_DELAY_AFTER_CLICKS-=10;
                 _("ROBOT_DELAY_AFTER_CLICKS:"+Bot.ROBOT_DELAY_AFTER_CLICKS);
             }
             if (direction==DEBUGRIGHT)
             {
                 Bot.ROBOT_DELAY_AFTER_CLICKS+=10;
                 _("ROBOT_DELAY_AFTER_CLICKS:"+Bot.ROBOT_DELAY_AFTER_CLICKS);
             }
             break;
             //DEBUG_OPTION_paintRobotMessages
         case DEBUG_OPTION_paintRobotMessages:
             if (direction==DEBUGLEFT)
             {
                 paintRobotMessages=!paintRobotMessages;
                 _("paintRobotMessages:"+paintRobotMessages);
             }
             if (direction==DEBUGRIGHT)
             {
                 paintRobotMessages=!paintRobotMessages;
                 _("paintRobotMessages:"+paintRobotMessages);
             }
             break;
             //TIME_DELAY_BETWEEN_CLICKS
         case DEBUG_OPTION_FULL_AUTO_PLAY:
             if (direction==DEBUGLEFT)
             {
                 Bot.FULL_AUTO_PLAY=!Bot.FULL_AUTO_PLAY;
                 _("FULL_AUTO_PLAY:"+Bot.FULL_AUTO_PLAY);
             }
             if (direction==DEBUGRIGHT)
             {
                 Bot.FULL_AUTO_PLAY=!Bot.FULL_AUTO_PLAY;
                 _("FULL_AUTO_PLAY:"+Bot.FULL_AUTO_PLAY);
             }
             break;
         default:
             HAL._E("UNKNOWN DEBUG OPTION CHANGED:"+debugMenuPos);
             break;
     }
 }

 //paints the state - for debugging.
 private void paintState(Graphics g)
 {
     fontblack.drawString(g,stateString,20,20,0);
 }

 int infoCounter=0;
 Bitmap splashScreenLogo,splashScreenLogoSmall;
 Bitmap op,admin;
 public static Bitmap pointer;
 public static int WIDTH;
 public static int HEIGHT;
 public static final boolean drawPointer=true;

 public static int pointerX;
 public static int pointerY;

 //loads all images needed
 private void loadImages()
 {
     _("Attempting to loadImages()");
     if (splashScreenLogo==null)
     {
         splashScreenLogo      = hal.loadImage("/presentslogo.gif", R.drawable.presentslogo);
         splashScreenLogoSmall = hal.loadImage("/presentslogosmall.gif", R.drawable.presentslogosmall);
         pointer               = hal.loadImage("/pointer.png", R.drawable.pointer);
         op                    = hal.loadImage("/op.png", R.drawable.op);
         admin                 =  hal.loadImage("/admin.png", R.drawable.admin);
     }
     else
     {
         _("Images already pre-cached...");
     }
 }

 ///////// ALL PAINT STATE METHODS //////////////////////

 int y;
 int x;
 int splashCounter;

 private void paint_SPLASH_SCREEN(Graphics g) {
               _("paint_SPLASH_SCREEN");
     hal.bg(g,Color.WHITE,ULTIMATE_WIDTH,ULTIMATE_HEIGHT);
     hal.drawImage(g,splashScreenLogo,(ULTIMATE_WIDTH/2),(ULTIMATE_HEIGHT/2));
     hal.setColor(g,Color.BLACK);

     if (showCollisions)
     {
         int ydebug=10;
         int xdebug=10;
         fontblack.drawString(g, DEBUG_HEADER,xdebug,ydebug,0);ydebug+=fontblack.getHeight();
         fontblack.drawString(g, VERSION,xdebug,ydebug,0);ydebug+=fontblack.getHeight();
         fontblack.drawString(g, splashCounter+"/"+SPLASH_COUNTER,xdebug,ydebug,0);ydebug+=fontblack.getHeight();
     }
     if (splashCounter++ > SPLASH_COUNTER)
     {
        _("Splash done.");

         //SKIP THIS SINCE NETWORKIGN ISNT FINISHE DYET
       // state=OPTIONS_SCREEN_LOCAL_OR_NETWORK;

         //USE THIS FOR NOW TIL NETWORKING IS CODED AND WORKING.
         state=OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN;     typeOfPlay=LOCAL_PLAY;
        
     }

     
 }

 //prefs button x,y width and height
 int prefw=20;
 int prefh=20;
 int prefx=ULTIMATE_WIDTH-(Board.BORDER+prefw+TINY_GAP/2);
 int prefy=Board.BORDER;

 //Color greyColour=new Color(0x2f343a);//for prefs icon
 Board board;
 public static int TINY_GAP=5;//when we need a tiny gap
 private void paint_POST_SPLASH_SCREEN(Graphics g)
 {
      hal.bg(g, BACKGROUND_COLOUR, ULTIMATE_WIDTH, ULTIMATE_HEIGHT);//paint entire background
      if (board==null)
      {
          board = new Board();
      }
      hal.setColor(g,Color.WHITE);

      //paint board and its containing parts
      board.paint(g,WIDTH,HEIGHT);
      //paint the message panel to the right with players name etc
      hal.setColor(g,PANEL_COLOUR);
      hal.fillRect(g,WIDTH,Board.BORDER,PANEL_WIDTH,HEIGHT-(Board.BORDER*2));

      //draw the preferences button
      prefw=20;
      prefh=20;
      prefx=ULTIMATE_WIDTH-(Board.BORDER+prefw+TINY_GAP/2);
      prefy=Board.BORDER;

   /*  //draw a circle with an 'i' inside.
      hal.setColor(g, Color.BLUE);
      hal.fillCircle(g, prefx, prefy, prefw, prefh);
      hal.setColor(g, Color.WHITE);
      hal.drawCircle(g, prefx, prefy, prefw, prefh);
      fontwhite.drawString(g, "i", prefx+4, prefy+2,0);
      //////////
      if (showCollisions)
      {
          hal.setColor(g, Color.RED);
          hal.drawRect(g, prefx, prefy, prefw, prefh);
      }     */

      //draw panel text:
      int xpos=WIDTH+TINY_GAP;

      //draw the piece container
      int heightOf3LinesOfText=(fontwhite.getHeight()*3)+(Board.BORDER*2)+TINY_GAP;
      int containerSubSize=HEIGHT/70;
      int containerWidth=PANEL_WIDTH/3;
      int topOfPieceContainer=HEIGHT-((containerSubSize*15)+heightOf3LinesOfText);



       if (Board.allBlackPiecesAreHome)
       {
           hal.setColor(g, Color.GREEN);
           if (Board.pulsateBlackContainer)
           {
               hal.setColor(g, Color.YELLOW);//dra piece container yellow when its an option
           }
       }
       else
       {
            hal.setColor(g, Color.WHITE);
       }
      //draw black players piece container
      drawPieceContainer(g, xpos, topOfPieceContainer, containerWidth,
              containerSubSize,heightOf3LinesOfText, Player.BLACK);

      heightOf3LinesOfText=(fontwhite.getHeight()*3)+Board.BORDER+TINY_GAP;
      topOfPieceContainer=heightOf3LinesOfText;

      if (Board.allWhitePiecesAreHome)
       {
           hal.setColor(g, Color.GREEN);
           if (Board.pulsateWhiteContainer)
           {
               hal.setColor(g, Color.YELLOW);//dra piece container yellow when its an option
           }
       }
       else
       {
            hal.setColor(g, Color.WHITE);
       }
      //draw white players piece container
      drawPieceContainer(g, xpos, topOfPieceContainer, containerWidth,
              containerSubSize,heightOf3LinesOfText,Player.WHITE);


      int pieceOnBarY=(HEIGHT/2)-Piece.PIECE_DIAMETER;
      //Draw pieces on the bar//////////////
      Enumeration eW = theBarWHITE.elements();
      while (eW.hasMoreElements())
      {
          Piece p = (Piece)eW.nextElement();
          p.paint(g,(WIDTH/2)-Piece.PIECE_DIAMETER/2,pieceOnBarY-=Piece.PIECE_DIAMETER);
      }
      pieceOnBarY=(HEIGHT/2);
      Enumeration eB = theBarBLACK.elements();
      while (eB.hasMoreElements())
      {
          Piece p = (Piece)eB.nextElement();
          p.paint(g,(WIDTH/2)-Piece.PIECE_DIAMETER/2,pieceOnBarY+=Piece.PIECE_DIAMETER);
      }
      ///////////////////////////////////

     drawHUDtext(g,xpos);

     
     //draw one more outline here to cover up spike edges
     hal.setColor(g,Color.BLACK);
     hal.drawRect(g,Board.BORDER,Board.BORDER,WIDTH-Board.BORDER*2,HEIGHT-Board.BORDER*2);
 }
 //for collisions.
 public static int whiteContainerX;
 public static int whiteContainerY;
 public static int whiteContainerWidth;
 public static int whiteContainerHeight;
 public static int blackContainerX;
 public static int blackContainerY;
 public static int blackContainerWidth;
 public static int blackContainerHeight;

 //draws the little holder where the pieces go
 private void drawPieceContainer(Graphics g, int xpos, int topOfPieceContainer,
            int containerWidth, int containerSubSize, int heightOf3LinesOfText,
            int player
         )
 {

     int piecesOnContainer=0;
     if (player==Player.WHITE)
     {
        piecesOnContainer=whitePiecesSafelyInContainer.size();
     }else
     if (player==Player.BLACK)
     {
         piecesOnContainer=blackPiecesSafelyInContainer.size();
     }
     int myX=WIDTH+((PANEL_WIDTH/4)-(containerWidth/2));
     int myY=topOfPieceContainer;
     for (int i=0; i<15; i++)
     {

         //simply draws the containers green if players have all their pieces in the home section
         //and therefore the piece containers are 'live' and ready for action

         myY=myY+containerSubSize;
         if (i<piecesOnContainer)
         {
             int originalColor = hal.getColor();
             hal.setColor(g,ORANGE);

             hal.fillRect(g, myX, myY, containerWidth , containerSubSize );
             hal.setColor(g,originalColor);
         }
         hal.drawRect(g, myX, myY, containerWidth , containerSubSize );
     }
     //update collision data
     if (player==Player.WHITE)
     {
         whiteContainerX=myX;
         whiteContainerY=myY-(containerSubSize*14);
         whiteContainerWidth=containerWidth;
         whiteContainerHeight=containerSubSize*15;
         if (showCollisions)
         {
             hal.setColor(g,Color.RED);
             hal.drawRect(g, whiteContainerX, whiteContainerY,whiteContainerWidth,whiteContainerHeight );
         }
     }
     else
     if (player==Player.BLACK)
     {
         blackContainerX=myX;
         blackContainerY=myY-(containerSubSize*14);
         blackContainerWidth=containerWidth;
         blackContainerHeight=containerSubSize*15;
         if (showCollisions)
         {
             hal.setColor(g,Color.RED);
             hal.drawRect(g, blackContainerX, blackContainerY,blackContainerWidth,blackContainerHeight );
         }
     }
     else
     {
         HAL._E("drawPieceContainer has been given incorrect player number!");
     }

 }

 String printme;//reused for many text that is printed

 //use these to detect if the roll button was clicked.
 int rollButtonX;
 int rollButtonY;
 int rollButtonW;
 int rollButtonH;

 public static final String STAR="*";
 //draw all of the text on the panel
 private void drawHUDtext(Graphics g,int xpos)
 {
     int ypos=Board.BORDER+TINY_GAP;
     //draw black players score at top
     printme="White";// ("+board.getBlackPlayer().name+")";
     if (board.whoseTurnIsIt==Player.WHITE)
     {
         printme+="*";
     }
     fontwhite.drawString(g, printme,  xpos, ypos, 0);ypos+=fontwhite.getHeight();

     printme="Pips: "+calculatePips(Player.WHITE);/*board.getBlackPlayer().pips*/;   fontwhite.drawString(g, printme,  xpos, ypos, 0);ypos+=fontwhite.getHeight();
    /////////// printme="Score: "+board.getBlackPlayer().score;   fontwhite.drawString(g, printme,  xpos, ypos, 0);ypos+=fontwhite.getHeight();
     //draw white players score at bot
     ypos=HEIGHT-9-(Board.BORDER*2)-(fontwhite.getHeight()*2);
     printme="Brown";// ("+board.getWhitePlayer().name+")";
     if (board.whoseTurnIsIt==Player.BLACK)
     {
         printme+="*";
     }
     fontwhite.drawString(g, printme,  xpos, ypos, 0);ypos+=fontwhite.getHeight();
     printme="Pips: "+calculatePips(Player.BLACK);/*board.getWhitePlayer().pips;*/   fontwhite.drawString(g, printme,  xpos, ypos, 0);ypos+=fontwhite.getHeight();
    /////////// printme="Score: "+board.getWhitePlayer().score;   fontwhite.drawString(g, printme,  xpos, ypos, 0);ypos+=fontwhite.getHeight();

     int xposTmp=-1;
     ypos=(HEIGHT/2)-((fontwhite.getHeight()*4)/2);
     printme="Match Points: "+board.matchPoints;
     int widthOfPrintMe=(fontwhite.stringWidth(printme));
     xposTmp=(WIDTH+PANEL_WIDTH/2)-((widthOfPrintMe/2)+TINY_GAP);
     //fontwhite.drawString(g, printme, xposTmp, ypos, 0);ypos+=fontwhite.getHeight();

     hal.setColor(g, ROLL_BUTTON_COLOUR);

     //---- draw buttons
     ///////// double button
     printme="Double";
     widthOfPrintMe=(fontwhite.stringWidth(printme));
     xposTmp=(WIDTH+PANEL_WIDTH/2)-((widthOfPrintMe/2)+TINY_GAP);
     hal.setColor(g, ROLL_BUTTON_COLOUR);
     ypos+=10;
     //hal.drawRoundRect(g, xposTmp-10, ypos, widthOfPrintMe+20, (fontwhite.getHeight()) );
     //fontwhite.drawString(g, printme, xposTmp , ypos+1, 0);         ////
     doubleX=xposTmp-10;
     doubleY=ypos;
     doubleWidth=widthOfPrintMe+20;
     doubleHeight=(fontwhite.getHeight()) ;
     if (showCollisions)
      {
          hal.setColor(g, Color.RED);
          hal.drawRect(g, doubleX, doubleY, doubleWidth, doubleHeight);
      }

     //draw the 'Roll' button
     ///////// roll button (on board itself (could be either side)
     printme=""+Die.rollString;//either says roll or 'roll to see who goes first' ..
     widthOfPrintMe=(fontwhite.stringWidth(printme));
     //we place the roll button on the appropriate side of the board
     //depending on whose turn it is

     /*if (leftSide)
     {
        xposTmp=(WIDTH/4)-widthOfPrintMe/2;
     }
     else
     {
        xposTmp=((WIDTH/4)*3)-widthOfPrintMe/2;
     }*/

     //only show roll button when required
     if (CustomCanvas.showRollButton)
     {
         //draw in centre:
         xposTmp=((WIDTH/2))-widthOfPrintMe/2;
         ypos+=18;
         hal.setColor(g, ROLL_BUTTON_COLOUR);
         hal.fillRoundRect(g, xposTmp-10, ypos, widthOfPrintMe+20, (fontwhite.getHeight()),true );



//numberOfFirstRollsDone here in this condition allows the bot to take its opening roll even though it doesnt know
         //whoseTurnitIs yet, FULL_AUTO_PLAY allows bot to fully control game, and
         //Board.whoseTurnIsIt==Player.WHITE means the bot doesnt react when its your go if youre a human (human is always white right now)
         /*if (!Bot.FULL_AUTO_PLAY )
         {
             if (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.WHITE && numberOfFirstRollsDone==0)
             {
                 desinationsFromWhichSource="Human B";
             }
             else if (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.BLACK && numberOfFirstRollsDone==0 )
             {
                 Bot.destX=(xposTmp-10)+(widthOfPrintMe+20)/2;
                 Bot.destY=ypos+(fontwhite.getHeight())/2;
                 desinationsFromWhichSource="Bot B";
             }

         }*/
         //_("numberOfFirstRollsDone:"+numberOfFirstRollsDone);
         if (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.BLACK || Bot.FULL_AUTO_PLAY  || numberOfFirstRollsDone==1 )
         {
             if (Board.NOT_A_BOT_BUT_A_NETWORKED_PLAYER && !RemotePlayer.clickRoll)
             {
                 _("WAITING FOR USER TO CLICK ROLL DICE REMOTELY");
             }
             else
             {
                Board.setBotDestination((xposTmp-10)+(widthOfPrintMe+20)/2,ypos+(fontwhite.getHeight())/2,"PRESS ROLL BUTTON");
             }
         }

          //.destX=(xposTmp-10)+(widthOfPrintMe+20)/2;
          //       Bot.destY=ypos+(fontwhite.getHeight())/2;



         /////for collisions
         rollButtonX=xposTmp-10;
         rollButtonY=ypos;
         rollButtonW=widthOfPrintMe+20;
         rollButtonH=(fontwhite.getHeight());
         //////////
         if (CustomCanvas.showCollisions)
        {

            hal.setColor(g,Color.RED);
            hal.drawRect(g,rollButtonX, rollButtonY,rollButtonW, rollButtonH);
        }

         fontblack.drawString(g, printme, xposTmp , ypos+1, 0);ypos+=fontwhite.getHeight();
     ////


     }
     else
     {
         //still knock y down so other buttons draw inline.
         ypos+=fontwhite.getHeight();
     }

     ///////// resign button
     printme="Resign";
     widthOfPrintMe=(fontwhite.stringWidth(printme));
     xposTmp=(WIDTH+PANEL_WIDTH/2)-((widthOfPrintMe/2)+TINY_GAP);
     hal.setColor(g, ROLL_BUTTON_COLOUR);
     ///ypos+=10;

     if (CustomCanvas.showRollButton)
      {
          ypos-=18;//jeep resign button on same y pos
      }
     hal.drawRoundRect(g, xposTmp-10, ypos, widthOfPrintMe+20, (fontwhite.getHeight()) );
     fontwhite.drawString(g, printme, xposTmp , ypos+1, 0);

     resignX=xposTmp-10;
      resignY=ypos;
      resignWidth=widthOfPrintMe+20;
      resignHeight=(fontwhite.getHeight());

     ////

      if (showCollisions)
      {
          hal.setColor(g, Color.RED);
          hal.drawRect(g, resignX, resignY, resignWidth, resignHeight);
      }
 }

 //returns the current pip count doe the player passed in.
 private int calculatePips(int player)
 {
     int pips=0;

     /*pips is the amount of dots on the die it would take to get off the board, so to count them you go through the spikes
      * counting the number of pieces of that colour on the spike, then multiply that by the amount of spikes it is away from the
      * end of the board (INCLUDING the one to get onto the pice container), add these all up . as an example the starting pip count is 167 because:
      * 2 pieces on spike 0 (23 steps from end) * 2 = 48
      * 5 pieces on spike 11 (13 steps from end)*5=65
      * 3 pieces on spike 16 (8 steps from end) *3 = 24
      * 6 pieces on spike 18 (5 steps from the end) *6 =30
      * total is 167
      */
     if ( board==null)
     {                            //ANDROID ONE HITS NULL HERE WHEN WE GO TO GAME OVER SO FINISH
                                       HAL._("GAME OVER YEH?");
        // ActivityGammon.me.finish();
         return 0;
     }
     
     Enumeration e = board.spikes.elements();

     int spikeCounter=0;
     if (player==Player.WHITE)
     {
         //works for white logic is simple
        for (int i=0; i<24;i++)
         {
             Spike spike = (Spike)board.spikes.elementAt(i);
             if (spike.getAmountOfPieces(player)>0)
             {
                 pips+=(i+1)*spike.getAmountOfPieces(player);
             }
         }
     }
     else
     {
         //logic for black takes some thinking about!
         int j=0;
        for (int i=23; i>=0;i--)
         {
             Spike spike = (Spike)board.spikes.elementAt(i);
             if (spike.getAmountOfPieces(player)>0)
             {
                 pips+=(j+1)*spike.getAmountOfPieces(player);
             }
             j++;
         }
     }



    /* while (e.hasMoreElements())
     {
        Spike spike = (Spike) e.nextElement();
        int amountOfPieces = spike.getAmountOfPieces(player);
        if (amountOfPieces>0)
        {
            int distanceThisSpikeIsFromEnd=spikeCounter;
             if (player==Player.WHITE)
             {
                 distanceThisSpikeIsFromEnd=24-spikeCounter;
             }else
             if (player==Player.BLACK)
             {
                 distanceThisSpikeIsFromEnd=24-spikeCounter;
             }

            pips+=(amountOfPieces*distanceThisSpikeIsFromEnd);
            _(Board.playerStr(player)+":"+ amountOfPieces+" on spike "+spikeCounter+" ("+distanceThisSpikeIsFromEnd+" steps from end) * "+amountOfPieces+" ="+(amountOfPieces*distanceThisSpikeIsFromEnd));

        }
        spikeCounter++;
     }*/
     return pips;
 }

 //for glowy buttons
 public static final int GLOW_INCREMENTER=15;
 boolean glowA, glowB;

 // simply sets glow to true if the mouse is over the button
 // glow is a boolean used to make the button glow when pointer is over it.
 private void glowButton(int x, int y)
 {
     if (x>=buttonxA && x<=buttonxA+buttonwA)
     {
        if (y>=buttonyA && y<=buttonyA+buttonhA)
        {
             ///_("glow button");
             glowA=true;
        }
     }
     if (x>=buttonxB && x<=buttonxB+buttonwB)
     {
        if (y>=buttonyB && y<=buttonyB+buttonhB)
        {
             ///_("glow button");
             glowB=true;
        }
     }

     if (glowA || glowB)
     {
         glowCounter+=GLOW_INCREMENTER;
         if (glowCounter>255)
         {
             if (glowCounter>355)
             {
                glowCounter=GLOW_INITIAL_VALUE;
             }
         }
     }
 }

 boolean buttonPressed;
 // this deals with touching the 'virtual' buttons
 // a mouse event is passed in to grab the x,y values from
 private boolean touchedButton(int x,int y)
 {
     buttonPressed=false;
     switch(state)
     {
         ///////////////////////////////////////////
         case OPTIONS_SCREEN_LOCAL_OR_NETWORK:
             //check buttons (local player, network play)
             checkAndDealWithTopButtonPressed_localplay(x,y);
             checkAndDealWithBotButtonPressed_networkplay(x,y);

            
          break;
         //////////////////////////////////////
         case OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN:
             //check buttons (computer player, human player)
             checkAndDealWithTopButtonPressed_computerPlayer(x,y);
             
             checkAndDealWithTopButtonPressed_humanPlayer(x,y);
         break;
         //////////////////////////////////////
         case GAME_IN_PROGRESS:
             //CHECK IF ROLL DICE BUTTON WAS PRESSED AND DEAL WITH IT////////
             //tellRobot(true,"dddd");
             if (showRollButton)
             {
               /*  if (showRollButton)
                {
                    tellRobot(true,"Click on roll dice button");
                }*/
                checkAndDealWithRollDiceButton(x,y);


             } else
             {
                 //ROBOT LOOK FOR RELEVANT SPIKES..


             }

             //Other in game buttons go here, like double up, resign etc.
             break;
             ///////////////////////////////////////////////////
     }

     if (!buttonPressed)
     {
         //_("No button pressed, x  >= "+buttonxA+" x <= "+(buttonxA+buttonwA));
     }
     return buttonPressed;
 }

 //works out if the bottom button is pressed (in this state the 'computer player' button)
 //and deals with it
 private void checkAndDealWithTopButtonPressed_computerPlayer(int x,int y)
 {
     if (x>=buttonxA && x<=buttonxA+buttonwA)
     {
        if (y>=buttonyA && y<=buttonyA+buttonhA)
        {
             _("Selected COMPUTER on OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN");
             buttonPressed=true;
             typeOfOpponent=CPU;//REMOVE ME???? TODOOOO

             //if (typeOfOpponent==CPU)
             //{
                 Board.HUMAN_VS_COMPUTER=true;
                 Bot.dead=false;//give him life
                 _("CPU OPPONENT PRIMED.");
            // }

             state=GAME_IN_PROGRESS;

             if (typeOfPlay==LOCAL_PLAY)
             {
                _("Selected LOCAL play against CPU");
             }
             else
             {
                 _("Selected NETWORK play against CPU");

             }
        }
     }
 }

 //works out if the bottom button is pressed (in this state the 'human player' button)
 //and deals with it
 private void checkAndDealWithTopButtonPressed_humanPlayer(int x,int y)
 {
     //check if bottom button is pressed (human player)
     if (x>=buttonxB && x<=buttonxB+buttonwB)
     {
        if (y>=buttonyB && y<=buttonyB+buttonhB)
        {
             _("Selected HUMAN on OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN");
             buttonPressed=true;
             typeOfOpponent=HUMAN;
             state=GAME_IN_PROGRESS;

              Board.HUMAN_VS_COMPUTER=false;
              _("THE WEAKLING WOULD RATHER FACE A HUMAN.");

             if (typeOfPlay==LOCAL_PLAY)
             {
                _("Selected LOCAL play against HUMAN");
             }
             else
             {
                 _("Selected NETWORK play against HUMAN");
             }
        }
     }
 }

 //works out if the bottom button is pressed (in this state the 'network play' button)
 //and deals with it
 private void checkAndDealWithBotButtonPressed_networkplay(int x,int y)
 {
     //check if bottom button is pressed (NETWORK)
     if (x>=buttonxB && x<=buttonxB+buttonwB)
     {
        if (y>=buttonyB && y<=buttonyB+buttonhB)
        {
             _("Selected NETWORK PLAY on OPTIONS_SCREEN_LOCAL_OR_NETWORK");
             typeOfPlay=NETWORK_PLAY;
             buttonPressed=true;

             state=NETWORKING_ENTER_NAME;
             ///state=OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN;
             ///HAL._E("NETWORK PLAY IS NOT YET IMPLEMENTED!");
             ///robotmove=true;
             ///System.exit(0);

        }
     }
 }

 //works out if the top button is pressed (in this state the 'local play' button)
 //and deals with it
 private void checkAndDealWithTopButtonPressed_localplay(int x,int y)
 {
     if (x>=buttonxA && x<=buttonxA+buttonwA)
     {
        if (y>=buttonyA && y<=buttonyA+buttonhA)
        {
             _("Selected LOCAL PLAY on OPTIONS_SCREEN_LOCAL_OR_NETWORK");
             typeOfPlay=LOCAL_PLAY;
             buttonPressed=true;

             state=OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN;
             robotmove=true;
        }
     }
 }

    MediaPlayer diceroll;
    
 //detects if the roll dice button has been pressed, and if so, reacts
 //accordingly
 private void checkAndDealWithRollDiceButton(int x,int y)//MouseEvent e)
 {
     if (x>=rollButtonX && x<=rollButtonX+rollButtonW)
     {
        if (y>=rollButtonY && y<=rollButtonY+rollButtonH)
        {
            _("Roll Dice button clicked.");
            Board.die1HasBeenUsed=false;
            Board.die2HasBeenUsed=false;
            showDice=true;//show the die now theyve clicked roll.
          /////////////////////////////////////////TRUN OFF IN ANDROID FOR NOW  sfxDiceRoll.playSound();


            ActivityGammon.diceroll.start();
            
            
            ////////IF OPENING DICE ROLLS
            //if numberOfFirstRollsDone is less than 2
            //we know its the very start of the game, each player
            //gets to roll one die - highest roll indicates who
            //takes the first go.
            if (numberOfFirstRollsDone<=1)
            {
                _("OPENING ROLL (numberOfFirstRollsDone:"+numberOfFirstRollsDone+")");
                //ie the one each you get to see how starts.
                dealWithOpeningRolls();
                  //kick start the bot


            } else
            //////////////////////////////
            //ORDINARY ROLLS/////////////
            {
                _("ORDINARY ROLL");
                dealWithOrdinaryRolls();
            }

            buttonPressed=true;//just to print out to us it was pressed.
        }
        //////////////////p//////////////
     }
  }


 //deals with an ordinary roll, that is sets the 2 die values to new random ones
 private void dealWithOrdinaryRolls()
 {
     _("----------- dealWithOrdinaryRolls -----------");
     if (Board.whoseTurnIsIt==Player.WHITE)
     {
         _("white will roll both die now.");
         // note we pass in null in here which tells it to roll both die for us directly
         playerRolls(Player.WHITE, null,false);

     } else
     if (Board.whoseTurnIsIt==Player.BLACK)
     {
         _("black will roll both die now.");
         // note we pass in null in here which tells it to roll both die for us directly
         playerRolls(Player.BLACK,null,false);

     } else
     {
         HAL._("dealWithOrdinaryRolls does not know whoseTurnIsIt!");
     }

 }

 //deals with the implementation details of the opening rolls, that is each player
 //gets one roll to decide who goes first, then the winner takes both of these values
 //as their opening move.
 private void dealWithOpeningRolls()
 {
     _("----------- dealWithOpeningRolls -----------");
     numberOfFirstRollsDone++;
    _("first roll. "+numberOfFirstRollsDone);

    // WHITE rolls first to see who starts
    if (numberOfFirstRollsDone==1)
    {
        playerRolls(Player.WHITE, board.die1,true);
    } else //THEN black rolls his try to see who goes first
    if (numberOfFirstRollsDone==2)
    {
        playerRolls(Player.BLACK, board.die2,true);

        //check who was higher and therefore goes first
        if (blacksFirstRollVal>whitesFirstRollVal)
        {
            playerWonRollOff(Player.BLACK);
        } else
        if (whitesFirstRollVal>blacksFirstRollVal)
        {
            playerWonRollOff(Player.WHITE);
        } else
        if (blacksFirstRollVal==whitesFirstRollVal)
        {
            _("INITIAL ROLLS:BOTH PLAYERS ROLLED THE SAME! RE-ROLL.");
            tellPlayers("Both players rolled the same! Re-roll.");
            numberOfFirstRollsDone=0;
            blacksFirstRollVal=-1;
            whitesFirstRollVal=-1;
            ActivityGammon.doublerolled.start();
                        //this leaves it in a state where it simply allows this cycle to
            //repeat until the die rolls are different.
        }
    }
 }

 //does some basic stuff regarding a player winning a roll off
 //and makes sure that board.whoseTurnIsIt is updated with the right value
 private void playerWonRollOff(int player)
 {
     if (player==Player.BLACK)
     {
        _("BLACK won the roll off: "+blacksFirstRollVal+" to "+whitesFirstRollVal);
        tellPlayers("Black won the roll off: "+blacksFirstRollVal+" to "+whitesFirstRollVal);
        board.whoseTurnIsIt=Player.BLACK;
        //theyve rolled now time to move their pieces
     } else
     if (player==Player.WHITE)
     {
        _("WHITE won the roll off: "+whitesFirstRollVal+" to "+blacksFirstRollVal);
        tellPlayers("White won the roll off: "+whitesFirstRollVal+" to "+blacksFirstRollVal);
        board.whoseTurnIsIt=Player.WHITE;
        //theyve rolled now time to move their pieces
     }
     else
     {
         HAL._E("playerWonRollOff received an invalid player colour "+player);
     }
    if (CustomCanvas.numberOfFirstRollsDone>1)//ie if game started.
    {
     //hide roll button while they move their pieces.
     showRollButton=false;



     _("hiding show roll button");
    }
 }

 public static int D1lastDieRoll_toSendOverNetwork;
 public static int D2lastDieRoll_toSendOverNetwork;

 final String whiteStr="White";
 final String blackStr="Black";
 String tempStr;
 public static boolean someoneRolledADouble=false;
 public static int doubleRollCounter=0;//this tracks how many rolls a player has had after rolling a double,
                         //ie, we want them to have 4 rolls if thats the case and not 2

 //forces doubles on ordinary rolls, PURELY for checking the doubles implementation is bug free
 //this should never be true unless debugging.
 public static boolean DEBUG_FORCE_DOUBLES_ON_ORDINARY_ROLLS=false;

 // deals with a player rolling a dice, accepts an int representing either
 // BLACK or WHITE, die is the die which the player should roll.
 //openingRolls is passed in as true if the rolls are opening ones.
 // note that:
 // if Die is null it means that its an ordinary roll (not an opening roll) and we simply do 2 rolls for that player
 //accessing the dice objects directly, since we really want them to roll simulatenously so to speak
 private void playerRolls(int player, Die die,boolean openingRolls)
 {
     if (player==Player.WHITE)
     {
         if (openingRolls)
         {   // an opening roll.
             int val = die.roll();

             //if (doComms.updateDieRollRemotely)
             {
                 System.out.println("updateDieRollRemotely");
                 if (I_AM_CLIENT)
                 {
                     D1lastDieRoll_toSendOverNetwork=val;
                     GameNetworkClient.SENDCLICK_AND_DIEVALUE1=true;//tells it to send a click over network
                 }
                 if (I_AM_SERVER)
                 {
                     val=Integer.parseInt(doComms.D1remoteDieRoll);
                     die.setValue(val);
                 }
             }


             _("White rolled:"+val);
             whitesFirstRollVal=val;

             tellPlayers("White's opening roll "+whitesFirstRollVal);
         }
         else
         {
             //ordinary roll.
             int val  = board.die1.roll();
             D1lastDieRoll_toSendOverNetwork=val;
             GameNetworkClient.SENDCLICK_AND_DIEVALUE1=true;//tells it to send a click over network
             int val2 = board.die2.roll();
             D2lastDieRoll_toSendOverNetwork=val2;
             GameNetworkClient.SENDCLICK_AND_DIEVALUE2=true;//tells it to send a click over network
             /*if (DEBUG_FORCE_DOUBLES_ON_ORDINARY_ROLLS)
             {
                 board.die1.setValue(val2);//now die one has same val as die2
                 val  = board.die1.getValue();
                 HAL._E("DEBUG warning, forcing doubles - you should not see this message in ordinary play");
             }*/
             _("####################################White rolled:"+val+", "+val2);
             tellPlayers("White rolled:"+val+"-"+val2);

             if (val==val2)
             {
                 _("White Double!");
                 tellPlayers("White rolled:"+val+"-"+val2+" (Double)");
                 someoneRolledADouble=true;
                 doubleRollCounter=0;
                 ActivityGammon.diceroll.start();//  sfxDoubleRolled.playSound();
             }
             showRollButton=false;//dont show it now theyve just rolled.
         }
     } else
     if (player==Player.BLACK)
     {
         if (openingRolls)
         {   //its an opening roll.
             int val = die.roll();
            D1lastDieRoll_toSendOverNetwork=val;
             GameNetworkClient.SENDCLICK_AND_DIEVALUE1=true;//tells it to send a click over network
             _("Black rolled:"+val);
             blacksFirstRollVal=val;
             tellPlayers("Black's opening roll "+blacksFirstRollVal);
         }
         else
         {
             //ordinary roll.
             int val  = board.die1.roll();
             D1lastDieRoll_toSendOverNetwork=val;
             GameNetworkClient.SENDCLICK_AND_DIEVALUE1=true;//tells it to send a click over network
             int val2 = board.die2.roll();
             D2lastDieRoll_toSendOverNetwork=val2;
             GameNetworkClient.SENDCLICK_AND_DIEVALUE2=true;//tells it to send a click over network
             /*if (DEBUG_FORCE_DOUBLES_ON_ORDINARY_ROLLS)
             {
                 board.die1.setValue(val2);//now die one has same val as die2
                 val  = board.die1.getValue();
                 HAL._E("DEBUG warning, forcing doubles - you should not see this message in ordinary play");
             }*/
             _("#####################################################Black rolled:"+val+", "+val2);
             tellPlayers("Black rolled:"+val+"-"+val2);

             if (val==val2)
             {
                 _("Black Double!");
                 tellPlayers("Black rolled:"+val+"-"+val2+" (Double)");
                 someoneRolledADouble=true;
                 doubleRollCounter=0;
                 ActivityGammon.diceroll.start();//sfxDoubleRolled.playSound();
             }

             showRollButton=false;//dont show it now theyve just rolled.

         }
     }
     else
     {
         HAL._E("playerRolls() received an invalid player colour.");
     }
     board.calculatePotentialNumberOfMoves=true;
 }

 public static int numberOfFirstRollsDone=0;//when this hits 2 we know they have both rolled their initial roll
 int whitesFirstRollVal=-1;//keep their initial roll to compare them
 int blacksFirstRollVal=-1;
 public static boolean showRollButton=true;//false when not needed

 //things to clear to restart a game
 /*public void resetVarsGame()
 {
     _("R-E-S-E-T--V-A-R-S!");
    numberOfFirstRollsDone=0;
    whitesFirstRollVal=-1;
    blacksFirstRollVal=-1;
 }*/

 //clears the potential spikes used for highlighting possible moves,
 //once cleared they are recreated as needed.
 private static void clearPotentialSpikes()
 {
     _("clearPotentialSpikes");
     //Clear all the copy spikes so the valid options vanish
     Board.copy_of_reachableFromDie1=null;
     Board.copy_of_reachableFromDie2=null;
     Board.copy_of_reachableFromBothDice=null;
 }


 public static boolean showDice;
 //this needs to be called when swapping turns form one player to another
 //to ensure things behave correctly.
 public static void resetVarsTurn()
 {
     _("resetVarsTurn");

     //so it doesnt think dice have been used anymore
     Board.die1HasBeenUsed=false;
     Board.die2HasBeenUsed=false;

     clearPotentialSpikes();

     //make sure roll button gets redrawn
     showRollButton=true;
     showDice=false;//dont draw til the next player clicks roll.

     Board.calculatePotentialNumberOfMoves=true;//so they get calc'd at start of each go.
     someoneRolledADouble=false;
     doubleRollCounter=0;


 }

 //for screens with 2 buttons this is button 1
 int buttonxA, buttonyA;
 int buttonwA, buttonhA;
 //and button 2
 int buttonxB, buttonyB;
 int buttonwB, buttonhB;

 public static final int GLOW_INITIAL_VALUE=125;
 int glowCounter=GLOW_INITIAL_VALUE;

 ////boolean robotmove=true;

 private void paint_OPTIONS_SCREEN_LOCAL_OR_NETWORK(Graphics g, String buttonAstr, String buttonBstr, String question)
 {

     ////
     printme=question;//"Please select";
     int widthOfPrintMe=(fontblack.stringWidth(printme));
     int xposTmp=0;
     int ypos =(ULTIMATE_HEIGHT/2)-fontblack.getHeight()*5;

     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     fontblack.drawString(g, printme, xposTmp , ypos+1, 0);
     ////

     ypos+=fontblack.getHeight()*2;

     ////
     printme=buttonAstr;//" Local Play ";
     widthOfPrintMe=(fontblack.stringWidth(printme));

     //---- draw buttons
     ///////// 'local' button

     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));

     /////
     //make button glow if pointer is over it
     if (glowA)
     {
         if (glowCounter<255)
         {



            /////////////FIX THIS!!ANDROID//hal.setColor(g,Color.rgb(glowCounter,0,0) );
         }
         else
         {
          //FIXTHIS///ANDROID  hal.setColor(g, Color.rgb(255,0,0));
         }
         hal.fillRoundRect(g, xposTmp-10, ypos, widthOfPrintMe+20, (fontblack.getHeight()),true );
         glowA=false;
     }
     hal.setColor(g, Color.BLACK);
     hal.drawRoundRect(g, xposTmp-10, ypos, widthOfPrintMe+20, (fontblack.getHeight()) );


     //if (robotmove)
    // {
         robotMoveDesc="Bot Loaded. Answer: ("+question+") ";
         Board.setBotDestination((xposTmp-10)+(widthOfPrintMe+20)/2,ypos+(fontblack.getHeight()/2),"Bot Loaded. Answer: ("+question+") ");

        // Bot.destX=(xposTmp-10)+(widthOfPrintMe+20)/2;
       //  Bot.destY=ypos+(fontblack.getHeight()/2);
         /////bot.click();
         tellRobot(false,robotMoveDesc);
        // desinationsFromWhichSource="Bot C";

     //}
     /////

     /////for collision of button
     buttonxA=xposTmp-10;
     buttonyA=ypos;
     buttonwA=widthOfPrintMe+20;
     buttonhA=(fontblack.getHeight());
     if (showCollisions)
     {
         hal.setColor(g, Color.RED);
         hal.drawRect(g, buttonxA, buttonyA, buttonwA, buttonhA);
     }
     /////

     fontblack.drawString(g, printme, xposTmp , ypos+1, 0);

     ////

     ///////
     printme="or";
     ypos +=(fontblack.getHeight()*2);
     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     fontblack.drawString(g, printme, xposTmp , ypos+1, 0);
     //////

     ///////// 'network' button
     printme=buttonBstr;//"Network Play";
     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     hal.setColor(g, Color.BLACK);
     ypos+=fontblack.getHeight()*2;

     /////
     //make button glow if pointer is over it
     if (glowB)
     {
         if (glowCounter<255)
         {
          ///FIX ME FOR ANDROID  hal.setColor(g, Color.rgb(0, 0, glowCounter));
         }
         else
         {
           //FIX ME FOR ANDORID hal.setColor(g, Color.rgb(0,0,255));
         }

         hal.fillRoundRect(g, xposTmp-10, ypos, widthOfPrintMe+20, (fontblack.getHeight()) ,true);
         glowB=false;
     }
     hal.setColor(g, Color.BLACK);
     hal.drawRoundRect(g, xposTmp-10, ypos, widthOfPrintMe+20, (fontblack.getHeight()) );
     fontblack.drawString(g, printme, xposTmp , ypos+1, 0);
     ////

     /////for collision of button
     buttonxB=xposTmp-10;
     buttonyB=ypos;
     buttonwB=widthOfPrintMe+20;
     buttonhB=(fontblack.getHeight());
     if (showCollisions)
     {
        hal.setColor(g, Color.RED);
        hal.drawRect(g, buttonxB, buttonyB, buttonwB, buttonhB);
     }
     /////

     //draw a little version of the logo in the bottom right
     hal.drawImage(g, splashScreenLogoSmall, ULTIMATE_WIDTH-((splashScreenLogoSmall.getWidth()/2)+20), ULTIMATE_HEIGHT-splashScreenLogoSmall.getHeight());
 }

 public static boolean robotmove=true;
 static String robotMoveDesc="Bot loaded.";

 private void paint_OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN(Graphics g, String buttonAstr, String buttonBstr, String question)
 {
     //reuse an existing method, they both simply have 2 buttons on them
     paint_OPTIONS_SCREEN_LOCAL_OR_NETWORK(g,buttonAstr,buttonBstr,question);
 }

 public String readStringFromWeb(String url)
    {
        String full="";
        String inputLine="";
        try
        {
        URL yahoo = new URL(url);//"http://www.alphasoftware.org/backgammon/news.txt");
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));


        while ((inputLine = in.readLine()) != null)
        {
            System.out.println(inputLine);
            if (inputLine!=null)
                full+=inputLine;
        }
        in.close();
        }
        catch(Exception e)
        {
            System.out.println("exception in getting news "+e.getMessage());
        }
        System.out.println("return news:"+full);
        return full;
    }

  public static String serverIP = null;
 private void paint_NETWORKING_ENTER_NAME(Graphics g)
 {
     if (serverIP==null)
     {
         _("GRABBING SERVER IP");
         serverIP=readStringFromWeb("http://www.alphasoftware.org/backgammon/serverip.txt");
          _(" SERVER IP:"+serverIP);
     }

     printme="Enter your name:";
     int widthOfPrintMe=(fontblack.stringWidth(printme));
     int xposTmp=0;
     int ypos =(ULTIMATE_HEIGHT/2)-fontblack.getHeight()*5;

     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     fontblack.drawString(g, printme, xposTmp , ypos+1, 0);
     ypos+=fontblack.getHeight()*2;


     printme="Enter your name:";
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     hal.drawRect(g, xposTmp, ypos, fontblack.stringWidth(printme), fontblack.getHeight());

     printme=NetworkChatClient.nick;
     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     fontblack.drawString(g, printme, xposTmp , ypos+1, 0);

     printme="Chat Server Address: "+serverIP;//NetworkChatClient.theURL;
     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     y=ULTIMATE_HEIGHT-(fontblack.getHeight()+25);
     fontblack.drawString(g, printme, xposTmp , y, 0);

 }

 int xScroll=0;
 boolean flip;
 int paraYoffset=0;
 int OUTLINE_FOR_CHAT_BOXES=0;
 Vector playerPositions;
 private void paint_NETWORKING_LOBBY(Graphics g)
 {
     TRANSPARENCY_LEVEL=255;
   // hal.setColor(g,0);
    hal.setColor(g, 0,0,0,TRANSPARENCY_LEVEL);
    hal.fillRect(g,0,0,ULTIMATE_WIDTH,ULTIMATE_HEIGHT);
     int SMALLGAP=5;



     x=SMALLGAP;
     y=SMALLGAP+fontblack.getHeight()*2;

     int BORDER=10;
     int WIDTH_OF_MESSAGE_TEXT  = ((ULTIMATE_WIDTH-ULTIMATE_WIDTH/6)-BORDER);
     int HEIGHT_OF_MESSAGE_TEXT = ((ULTIMATE_HEIGHT-ULTIMATE_HEIGHT/8)-(BORDER+y+SMALLGAP))+2;
     int WIDTH_OF_USERLIST  = ULTIMATE_WIDTH-(WIDTH_OF_MESSAGE_TEXT+BORDER+SMALLGAP);
     int HEIGHT_OF_USERLIST = HEIGHT_OF_MESSAGE_TEXT+1;
     int WIDTH_OF_ENTERTEXT_BOX=WIDTH_OF_MESSAGE_TEXT;
     int HEIGHT_OF_ENTERTEXT_BOX=ULTIMATE_HEIGHT-(HEIGHT_OF_MESSAGE_TEXT+BORDER+y+SMALLGAP);

     int HEIGHT_OF_TOPIC_AND_NEWS_BOX=ULTIMATE_HEIGHT-(HEIGHT_OF_MESSAGE_TEXT+HEIGHT_OF_ENTERTEXT_BOX+SMALLGAP*5);


     /*y+=fontblack.getHeight();
     if (NetworkChatClient.news!=null)
     {
        fontblack.drawString(g, ""+NetworkChatClient.news, x+SMALLGAP , y+(SMALLGAP*2)-2, 0);
     }*/
     y=SMALLGAP;

     ///////////////////////////////////////////

     x=x+WIDTH_OF_MESSAGE_TEXT+SMALLGAP;
     //info box top right (amount of users and ops)
     hal.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
     hal.fillRoundRect(g, x, y, WIDTH_OF_USERLIST, HEIGHT_OF_TOPIC_AND_NEWS_BOX,true);
     hal.setColor(g, OUTLINE_FOR_CHAT_BOXES);
     hal.drawRoundRect(g, x, y, WIDTH_OF_USERLIST, HEIGHT_OF_TOPIC_AND_NEWS_BOX);
     fontblack.drawString(g, ""+NetworkChatClient.userList.size()+" users", x+SMALLGAP , y+(SMALLGAP*2)-2, 0);
     ////////////////////////////////////////////

     x=SMALLGAP;
     y=SMALLGAP+fontblack.getHeight()*2;

     Shape s = g.getClip();
     g.setClip(x-2, y+3, WIDTH_OF_MESSAGE_TEXT+5, HEIGHT_OF_MESSAGE_TEXT);

     //message text
   //  hal.setColor(g, 0xFFFFFF);
     hal.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
     hal.fillRoundRect(g, x, y, WIDTH_OF_MESSAGE_TEXT, HEIGHT_OF_MESSAGE_TEXT,true);
     hal.setColor(g, OUTLINE_FOR_CHAT_BOXES);
     hal.drawRoundRect(g, x, y, WIDTH_OF_MESSAGE_TEXT, HEIGHT_OF_MESSAGE_TEXT);


     int listY=y+SMALLGAP;
     int topofChatBox=listY;

     Enumeration e = null;
     if (NetworkChatClient.messageText!=null)
     {
         y=SMALLGAP+fontblack.getHeight()*2;
         y+=paraYoffset;//scrolls it
         e = NetworkChatClient.messageText.elements();
         flip=false;
         while (e.hasMoreElements())
         {
             if (y>(ULTIMATE_HEIGHT-topofChatBox-HEIGHT_OF_ENTERTEXT_BOX)+fontblack.getHeight())
             {
                 //scroll

                //y=y-fontblack.getHeight();
                 paraYoffset--;//smooth scrolling
                 // System.out.println("scroll "+y);
             }
           // y=y+paraYoffset;
             int ydiff=y;
             int yorig=y;
             String message = (String) e.nextElement();
             //fontblack.drawString(g, message, x+SMALLGAP , listY, 0); listY+=fontblack.getHeight();

             //System.out.println("print message: "+message);
             y=drawMeWrapped(g,x,y,message,fontblack,"/",false,false,false,true,WIDTH_OF_ENTERTEXT_BOX-15,false);

            // y-=fontblack.getHeight();
             flip=!flip;
             if (flip)
             {
                 ydiff=y-ydiff;
                 //hal.setColor(g,0xFFFFFF);
                 hal.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
                 hal.fillRoundRect(g,x+1,yorig-1,WIDTH_OF_ENTERTEXT_BOX-1,ydiff,true);
                 y=yorig;
                y=drawMeWrapped(g,x,y,message,fontblack,"/",false,false,false,true,WIDTH_OF_ENTERTEXT_BOX-15,false);
             }
             else
             {

                 ydiff=y-ydiff;
               //  hal.setColor(g,0xd5d5d5);
                 hal.setColor(g, 100,100,100,TRANSPARENCY_LEVEL);
                 hal.fillRoundRect(g,x+1,yorig-1,WIDTH_OF_ENTERTEXT_BOX-1,ydiff,true);
                 y=yorig;
                y=drawMeWrapped(g,x,y,message,fontblack,"/",false,false,false,true,WIDTH_OF_ENTERTEXT_BOX-15,false);

             }



             /*ydiff=y-ydiff;
             hal.setColor(g,0xff0000);
             hal.fillRect(g, x+1, y+fontblack.getHeight(), WIDTH_OF_ENTERTEXT_BOX-1, ydiff);
             y=yorig;

             y=drawMeWrapped(g,x,y,message,fontblack,"/",false,false,false,true,WIDTH_OF_ENTERTEXT_BOX-15,false);*/
             //y-=fontblack.getHeight();
         }
     }
//g.setClip(s);
   //  g.setClip(clipX, clipY, clipW, clipH);
     x=SMALLGAP;
     y=SMALLGAP;
     // header for topic and live news/////////draw here so it covers over scrolled text from messages
     //hal.setColor(g, 0xFFFFFF);
     hal.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
     hal.fillRoundRect(g, x, y, WIDTH_OF_MESSAGE_TEXT, HEIGHT_OF_TOPIC_AND_NEWS_BOX,true);
     hal.setColor(g, OUTLINE_FOR_CHAT_BOXES);
     hal.drawRoundRect(g, x, y, WIDTH_OF_MESSAGE_TEXT, HEIGHT_OF_TOPIC_AND_NEWS_BOX);

     fontblack.drawString(g, NetworkChatClient.topic, x+SMALLGAP , y+(SMALLGAP*2)-2, 0);


     x+=WIDTH_OF_MESSAGE_TEXT+SMALLGAP;
      y=2+SMALLGAP+fontblack.getHeight()*2;

     //user list
     //hal.setColor(g, 0xFFFFFF);
     hal.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
     hal.fillRoundRect(g, x, y, WIDTH_OF_USERLIST, HEIGHT_OF_USERLIST,true);
     hal.setColor(g, OUTLINE_FOR_CHAT_BOXES);
     hal.drawRoundRect(g, x, y, WIDTH_OF_USERLIST, HEIGHT_OF_USERLIST);

     listY=y+SMALLGAP;
     e = NetworkChatClient.userList.elements();
     playerPositions=new Vector();
     while (e.hasMoreElements())
     {
         String user = (String) e.nextElement();
         int xval=0;
         if (user.equals("ChanServ"))
         {
             hal.drawImage(g, op,x+SMALLGAP+3 , listY+fontblack.getHeight()/2);
             xval=x+3+SMALLGAP*2;
             fontblack.drawString(g, user,  xval, listY, 0); listY+=fontblack.getHeight();
         }
         else if (user.equals("Admin"))
         {
             hal.drawImage(g, admin,x+SMALLGAP+3 , listY+fontblack.getHeight()/2);
             xval=x+3+SMALLGAP*2;
             fontblack.drawString(g, user, xval , listY, 0); listY+=fontblack.getHeight();
         }
         else
         {
             xval=x+SMALLGAP ;
            fontblack.drawString(g, user, xval, listY, 0); listY+=fontblack.getHeight();
         }
         //so we know if we clicked on one
         playerPositions.addElement(new PlayerPos(xval,listY-fontblack.getHeight(),fontblack.stringWidth(user),fontblack.getHeight(),user));
     }



     x=SMALLGAP;
     y+=HEIGHT_OF_USERLIST+SMALLGAP+2;

     //enter text box
    // hal.setColor(g, 0xFFFFFF);
     hal.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
     hal.fillRoundRect(g, x, y, WIDTH_OF_ENTERTEXT_BOX, HEIGHT_OF_ENTERTEXT_BOX,true);
     hal.setColor(g, OUTLINE_FOR_CHAT_BOXES);
     hal.drawRoundRect(g, x, y, WIDTH_OF_ENTERTEXT_BOX, HEIGHT_OF_ENTERTEXT_BOX);

     if (chatText!=null)
     {
        //fontblack.drawString(g, chatText, x+5 , y+5, 0); listY+=fontblack.getHeight();
         drawMeWrapped(g,x,y,chatText,fontblack,"",false,false,false,true,WIDTH_OF_ENTERTEXT_BOX,false);

         //hal.fillRect(g,x,y-1,1,fontblack.getHeight());
     }

     ////2 buttons in bottom right
     x=x+WIDTH_OF_MESSAGE_TEXT+SMALLGAP;
     //button 1
     //hal.setColor(g, 0xFFFFFF);
     hal.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
     hal.fillRoundRect(g, x, y, WIDTH_OF_USERLIST, (HEIGHT_OF_ENTERTEXT_BOX-SMALLGAP)/2,true);
     hal.setColor(g, OUTLINE_FOR_CHAT_BOXES);
     hal.drawRoundRect(g, x, y, WIDTH_OF_USERLIST, (HEIGHT_OF_ENTERTEXT_BOX-SMALLGAP)/2);
     printme="Options";
     fontblack.drawString(g, printme, (x)+(fontblack.stringWidth(printme)/2) , y+(SMALLGAP*2)-3, 0);

     y+=(HEIGHT_OF_ENTERTEXT_BOX+SMALLGAP)/2;
     //button 2
   //  hal.setColor(g, 0xFFFFFF);
     hal.setColor(g, 255,255,255,TRANSPARENCY_LEVEL);
     hal.fillRoundRect(g, x, y, WIDTH_OF_USERLIST, (HEIGHT_OF_ENTERTEXT_BOX-SMALLGAP)/2,true);
     hal.setColor(g, OUTLINE_FOR_CHAT_BOXES);
     hal.drawRoundRect(g, x, y, WIDTH_OF_USERLIST, (HEIGHT_OF_ENTERTEXT_BOX-SMALLGAP)/2);
     printme="Leave";
     fontblack.drawString(g, printme, (x)+15+(fontblack.stringWidth(printme)/2) , y+(SMALLGAP*2)-3, 0);
     /////////////////////////


     /*printme="Enter your name:";
     int widthOfPrintMe=(fontblack.stringWidth(printme));
     int xposTmp=0;
     int ypos =(ULTIMATE_HEIGHT/2)-fontblack.getHeight()*5;

     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     fontblack.drawString(g, printme, xposTmp , ypos+1, 0);
     ypos+=fontblack.getHeight()*2;


     printme="Enter your name:";
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     hal.drawRect(g, xposTmp, ypos, fontblack.stringWidth(printme), fontblack.getHeight());

     printme=NetworkChatClient.nick;
     widthOfPrintMe=(fontblack.stringWidth(printme));
     xposTmp=(ULTIMATE_WIDTH/2)-((widthOfPrintMe/2));
     fontblack.drawString(g, printme, xposTmp , ypos+1, 0);*/


     if (showChallengeWindow)
     {
         hal.setColor(g, 0,0,0,TRANSPARENCY_LEVEL);
        hal.fillRoundRect(g, WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2,true);
        hal.setColor(g, Color.WHITE);
        hal.drawRoundRect(g, WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2);

        int xabout=WIDTH/2;
        int yabout=(HEIGHT/4)+TINY_GAP;

        //paint the about box
        printme="Challenge this player?";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme=personToChallenge;fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();
        printme="IP:";fontwhite.drawString(g, printme,xabout-fontwhite.stringWidth(printme)/2,yabout,0);yabout+=fontblack.getHeight();

     }
 }

 //paint helpers///////////////
 private void bg(Color col, Graphics g)
 {
    hal.fillRect(g,0,0,WIDTH,HEIGHT);
 }

 // gets called each frame to repaint
 public void update(Graphics g) {
     _("update");
   // paint(g);
 }

 // wrapper around system out to console
 private static void _(String s)
 {
    HAL._("CustomCanvas{}:"+s);
 }
GameNetworkClient client;
 public static final int LEFT_MOUSE_BUTTON=0;
 public static final int RIGHT_MOUSE_BUTTON=1;
 /*
  * left click is used for everything, apart from cancelling which is done with
  * right button, for instance once a piece is stuck to the pointer right click
  * will return it to its original position (cancel the move)
  */
 //////// INPUT METHODS ////////////////
 public void mouseClicked(MouseEvent e)
 {

     if (state==NETWORKING_ENTER_NAME)
     {
         if (e.getY()>ULTIMATE_HEIGHT-50)
         {
             NetworkChatClient.theURL=NetworkChatClient.localURL;
             System.out.println("swapped to local url");
         }
         return;
     }
     if (state==NETWORKING_LOBBY)
     {
         //check who they clicked on
         Enumeration ee = playerPositions.elements();
         while (ee.hasMoreElements())
         {

             PlayerPos pos = (PlayerPos) ee.nextElement();
             if (e.getX()>=pos.x && e.getX()<=pos.x+pos.width &&
                 e.getY()>=pos.y && e.getY()<=pos.y+pos.height
                     )
             {
                 _("Clicked on:"+pos.name);
                 showChallengeWindow=true;
                 personToChallenge=pos.name;
                 String theirIP=personToChallenge.substring( personToChallenge.indexOf("@")+1,personToChallenge.length() );
                 _("IP TO CONNECT TO:"+theirIP);

//                 connectToSocket(theirIP);
                 client = new GameNetworkClient();
                  Thread t = new Thread(client);
                    t.start();
             }
         }
         return;
     }

     //so our mouse doesnt influence anything
    if (Bot.FULL_AUTO_PLAY || (!Bot.dead && Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.BLACK) )
    {
        //_("mouse wont respond");
    }
    else
    {
         _("mouseClicked "+e.getX()+","+e.getY());
   //      GameNetworkClient.SENDCLICK=true;//tells it to send a click over network
         int buttonPressed=-1;
         if (e.getButton()==e.BUTTON1)
        {
             buttonPressed=LEFT_MOUSE_BUTTON;
         }
         if (e.getButton()==e.BUTTON3)
        {
         buttonPressed=RIGHT_MOUSE_BUTTON;
         }
         mouseClickedX(e.getX(),e.getY(),buttonPressed);
    }

 }



 boolean showChallengeWindow;
 String personToChallenge;

 public void mouseClickedX(int x, int y, int buttonPressed)
 {
    splashCounter=SPLASH_COUNTER+1; //turn off splash if its on



    if (buttonPressed==LEFT_MOUSE_BUTTON)//e.getButton()==e.BUTTON1)
    {
        if (Board.gameComplete)
        {
            board.RESET_ENTIRE_GAME_VARS();
            RESET_ENTIRE_GAME_VARS();
            splashCounter=0;
            state=SPLASH_SCREEN;
        }
        //_("LEFT BUTTON PRESSED");
        /////// CustomCanvas.sfxmouseClick.playSound();
    }
    if (buttonPressed==RIGHT_MOUSE_BUTTON)//e.getButton()==e.BUTTON3)
    {
        _("RIGHT BUTTON PRESSED");
        unstickPieceFromMouse();
        if (board!=null)
        {
            board.calculatePotentialNumberOfMoves=true;
            board.thereAreOptions=false;
        }
        return; //do nothing else with right click
    }

    switch(state)
    {
        case OPTIONS_SCREEN_LOCAL_OR_NETWORK:
            //_("OPTIONS_SCREEN_LOCAL_OR_NETWORK");
             touchedButton(x,y);
             return;
        case OPTIONS_SCREEN_LOCAL_COMPUTER_OR_HUMAN:
             touchedButton(x,y);
             return;
        case GAME_IN_PROGRESS:
             touchedButton(x,y);

             if (showRollButton)
             {
                 _("respond to no clicks as the roll button is up");

                 //only thign that will respond is the about window
                 //brings up the about window.
                checkIfPrefsButtonClickedOn(x,y);
                 return;
             }
            //Making a move//////////
            //to move a piece the player simply once clicks
            //on a piece they wish to move - then if it has valid moves -
            //it attaches to the mouse pointer and can be placed either
            //on one of the valid potential spikes- or returned to where it
            //was initially by right clicking (and no move has been used)
            checkIfPieceClickedOn(x,y);//detects what piece (if any was clicked on)

            //once a piece is stuck to the pointer, we place it on a spike
            //IFF that spike is one of its valid moves.
            if (CustomCanvas.numberOfFirstRollsDone>1)//ie if game started.
            {
                checkIfSpikeClickedOn(x,y);//detects what spike (if any was clicked on)
                checkIfPieceContainerClickedOn(x,y);
                checkIfDoubleClickedOn(x,y);
                checkIfResignClickedOn(x,y);
                if (Board.die2HasBeenUsed || Board.die1HasBeenUsed)
                 {

                    // board.calculatePotentialMoves(true);//EXPERIMENT, SEE WE ARE USING OLD DIE ROLL SINCE THIS HAPPENS FIRTS
                 }


                // Here we check if both dice have been used so we can move onto next players turn:
                //UNLESS someone rolled a double
              /*  if (someoneRolledADouble && doubleRollCounter<=3)
                {
                    _("Player is still enjoying his double round so dont move on. x doubleRollCounter:"+doubleRollCounter);
                   board.calculatePotentialNumberOfMoves=true;//so they get calc'd at start of each go.
                }
                else //no double rolled (or double rolls just ended) so proceed normally
                {
                    someoneRolledADouble=false;//reset these here since we know its not a double.
                    doubleRollCounter=0;


                }*/
               ///// someoneRolledADouble=false;//reset these here since we know its not a double.
               ///////     doubleRollCounter=0;


                //if both dice used move to next turn
                    if (Board.die1HasBeenUsed && Board.die2HasBeenUsed)
                    {
                        _("GO TO NEW TURN AA");
                        turnOver();

                    }
            }

         return;
    }
 }

 private void RESET_ENTIRE_GAME_VARS()
 {
     numberOfFirstRollsDone=0;
     Board.whoseTurnIsIt=Player.WHITE;
      someoneRolledADouble=false;
 doubleRollCounter=0;//this tracks how many rolls a player has had after rolling a double,
  numberOfFirstRollsDone=0;//when this hits 2 we know they have both rolled their initial roll
  whitesFirstRollVal=-1;//keep their initial roll to compare them
  blacksFirstRollVal=-1;
  showRollButton=true;//false when not needed
  resetVarsTurn();
 theBarWHITE = new Vector(4);//the bar holds pieces that get killed
 theBarBLACK = new Vector(4);//the bar holds pieces that get killed

 //these store the pieces that have been sent to the container, when all are in that player wins.
 whitePiecesSafelyInContainer=new Vector(15);
 blackPiecesSafelyInContainer=new Vector(15);

   originalSpikeForPieceSelected=null;
barPieceStuckOnMouse=false;
 pieceOnMouse=false;//is true when a piece is stuck to mouse
  pieceStuckToMouse=null;//this is simply a copy of whatever piece (if any) is stuck to mouse
   //tells them essentials
 showMeFor=0;

 message2Players=""+VERSION;
 board=null;
 Die.initialRollText="Roll to see who goes first";// (white roll)";
 Board.gameComplete=false;
                whiteResigned=false;
                blackResigned=false;

                //so it doesnt continuing playin on its own
Board.HUMAN_VS_COMPUTER=false;
Bot.dead=true;
 }

 public static void turnOver()
 {
     _("---- THIS TURN IS OVER ----");
                        if (Board.whoseTurnIsIt==Player.WHITE)
                        {
                            Board.whoseTurnIsIt=Player.BLACK;
                            _("BLACKS TURN");
                            tellPlayers("Black's turn to roll.");

                        }
                        else
                        {
                            Board.whoseTurnIsIt=Player.WHITE;
                            _("WHITES TURN");
                            tellPlayers("White's turn to roll.");

                        }
                        resetVarsTurn(); // so a turn can start a fresh.
 }
 //this method will "unstick" a piece from the mouse by flagging the piece itself
 //as no longer stuck, it is used when right clicking to cancel a move
 //but also once a piece has actually been moved.
 private void unstickPieceFromMouse()
 {
      if (pieceStuckToMouse!=null)
        {
            //_("piece unstuck.");
            pieceStuckToMouse.unstickFromMouse();
        }
        pieceOnMouse=false;
        barPieceStuckOnMouse=false;



if (board==null)
{
    _("board null");
    return;
}

         board.SPtheMoveToMake=null;//reset the move to make once a move is made or right click

        //experimental,
       // board.calculatePotentialMoves(true);
        board.calculatePotentialNumberOfMoves=true;//so they get calc'd at start of each go.

        Board.spikesAllowedToMoveToFromBar=new Vector(4);//RESET THIS HERE?
pieceStuckToMouse=null;/////////////////////<-will this stop it stickign ot pointer?pieceStuckToMouse
 }

 //checks if the prefs button is pressed and deals with it if so
 private void checkIfPrefsButtonClickedOn(int x,int y)
 {
     if (x>=prefx && x<=prefx+prefw)
     {
         if (y>=prefy && y<=prefy+prefh)
         {
             _("Prefs button clicked.");
             INFO=!INFO;
             if (INFO)
             {
                 tellPlayers("About");
             }
         }
     }
 }

 int doubleX;
 int doubleY;
 int doubleWidth;
 int doubleHeight;
 int resignX;
 int resignY;
 int resignWidth;
 int resignHeight;
private void checkIfDoubleClickedOn(int x,int y)
{
     int myX=doubleX;
     int myY=doubleY;
     int myWidth=doubleWidth;
     int myHeight=doubleHeight;
     if (x>=myX && x<(myX+myWidth))
    {
        if (y>myY && y<(myY+myHeight))
        {
            _("DOUBLE CLICKED ON!");
            ActivityGammon.doublerolled.start();//sfxdouble.playSound();
            if (Board.whoseTurnIsIt==Player.WHITE)
            {
                tellPlayers("White just doubled.");
            }
            else
            {
                tellPlayers("Black just doubled.");
            }
        }
     }
}
private void checkIfResignClickedOn(int x,int y)
{
     int myX=resignX;
     int myY=resignY;
     int myWidth=resignWidth;
     int myHeight=resignHeight;
     if (x>=myX && x<(myX+myWidth))
    {
        if (y>myY && y<(myY+myHeight))
        {
            _("RESIGN CLICKED ON!");
            ActivityGammon.resign.start();//sfxResign.playSound();
            if (Board.whoseTurnIsIt==Player.WHITE)
            {
                Board.gameComplete=true;
                whiteResigned=true;
                Board.gameCompleteString="White has resigned. Black has won!";
            }
            else
            {
                Board.gameComplete=true;
                blackResigned=true;
                Board.gameCompleteString="Black has resigned. White has won!";
            }
        }
     }
}
public static boolean whiteResigned;
public static boolean blackResigned;
 private void checkIfPieceContainerClickedOn(int x,int y)
{
     int myX=0;
     int myY=0;
     int myWidth=0;
     int myHeight=0;
     String clickedOnText="NONE";
     if (Board.whoseTurnIsIt==Player.WHITE)
     {
         myX=whiteContainerX;
         myY=whiteContainerY;
         myWidth=whiteContainerWidth;
         myHeight=whiteContainerHeight;
         clickedOnText="WHITE CONTAINER CLICKED ON";
     }
     else
     if (Board.whoseTurnIsIt==Player.BLACK)
     {
         myX=blackContainerX;
         myY=blackContainerY;
         myWidth=blackContainerWidth;
         myHeight=blackContainerHeight;
         clickedOnText="BLACK CONTAINER CLICKED ON";
     }
     else
     {
         HAL._E("checkIfPieceContainerClickedOn knows not whom click upon it!");
     }

    if (x>=myX && x<(myX+myWidth))
    {
        if (y>myY && y<(myY+myHeight))
        {
            _(""+clickedOnText);
            //if container is yellow, ie it knows its a potential option, AND we have a piece on the mouse
            //then we simply let it go into the piece container.
            if (Board.pulsateWhiteContainer && pieceOnMouse && Board.whoseTurnIsIt==Player.WHITE)
            {
                _("WHITE put in container");
                //remove from spike
                //add to container vector
                int correctDie=Board.whichDieGetsUsToPieceContainer;
                boolean pieceWillGoToContainer=true;
                placePieceRemoveOldOneAndSetDieToUsed(correctDie,pieceWillGoToContainer);
                //continue
            }else
            //if container is yellow, ie it knows its a potential option, AND we have a piece on the mouse
            //then we simply let it go into the piece container.
            if (Board.pulsateBlackContainer && pieceOnMouse && Board.whoseTurnIsIt==Player.BLACK)
            {
                _("BLACK put in container");
                //remove from spike
                //add to container vector
                int correctDie=Board.whichDieGetsUsToPieceContainer;
                boolean pieceWillGoToContainer=true;
                placePieceRemoveOldOneAndSetDieToUsed(correctDie,pieceWillGoToContainer);
                //continue
            }
        }
    }
}


 //indicates if this mouse click has been on a spike
 private void checkIfSpikeClickedOn(int x,int y)
 {
    // grab the spikes, loop thru them checking to
    // see if the user clicked on that spike
    if (board==null)
    {
        _("game not ready. (splash still up)");
        return;
    }


    Enumeration spikes_e = board.spikes.elements();
    while(spikes_e.hasMoreElements())
    {
        Spike spike = (Spike) spikes_e.nextElement();
        if (spike.userClickedOnThis(x,y))
        {
             _("Spike was clicked on ("+spike.getSpikeNumber()+")");

             /* REMOVING A PIECE FROM ONE SPIKE AND ADDING IT TO ANOTHER.
              * When the player has a piece stuck to their mouse pointer
              * and the valid potential spikes are pulsating, we have copies
              * of them valid spikes stored as
              * board.copy_of_reachableFromDie1, board.copy_of_reachableFromDie2,
              * and board.copy_of_reachableFromBothDice.
              * So we compare the number of the spike they just clicked on to the
              * potential spikes the piece can go to, if they match then we know the
              * player has placed a piece from one spike to another spike. So we remove it
              * from the initial spike and add it to the new one, as shown below:
              */

              //this isnt needed at this point (is it?)
            /////  if (board.copy_of_reachableFromDie1==null)
            ///////  {   _("copy_of_reachableFromDie1 not ready yet.");
            ///////      return;
             ////// }
             // find out if this is a valid spiek to go to from bar
             if (barPieceStuckOnMouse)
             {
                 _("barPieceStuckOnouse spikesAllowedToMoveToFromBar.size()"+Board.spikesAllowedToMoveToFromBar.size());
                 Enumeration e = Board.spikesAllowedToMoveToFromBar.elements();
                 while (e.hasMoreElements())
                 {

                     Spike sp = (Spike) e.nextElement();
                     _("checkign spike:"+sp.getSpikeNumber());
                     if (spike.getSpikeNumber()==sp.getSpikeNumber())
                     {
                         _("YES WE CAN DROP OFF AT THIS SPIKE "+sp.getSpikeNumber());
                         //remove piece from bar
                         if (Board.whoseTurnIsIt==Player.WHITE)
                         {
                             _("WHITE PIECE REMOVED FROM BAR");
                             theBarWHITE.remove(pieceStuckToMouse);
                             //IF this spike contains an enemy piece Kill it
                             if (sp.getAmountOfPieces(Player.BLACK)==1)
                             {
                                 _("WHITE KILLED A BLACK WHILE GETTING OFF BAR");
                                 Piece piece = (Piece)sp.pieces.firstElement();
                                 theBarBLACK.add(piece);///add this piece to the bar
                                 sp.removePiece(piece); //and remove from spike
                                 ActivityGammon.killed.start();// sfxKilled.playSound();

                             }
                         }
                         if (Board.whoseTurnIsIt==Player.BLACK)
                         {
                             _("BLACK PIECE REMOVED FROM BAR");
                             theBarBLACK.remove(pieceStuckToMouse);

                              //IF this spike contains an enemy piece Kill it
                             if (sp.getAmountOfPieces(Player.WHITE)==1)
                             {
                                  Piece piece = (Piece)sp.pieces.firstElement();
                                 theBarWHITE.add(piece);///add this piece to the bar
                                 _("BLACK KILLED A WHITE WHILE GETTING OFF BAR");
                                 sp.removePiece((Piece)sp.pieces.firstElement());
                                 ActivityGammon.killed.start();//sfxKilled.playSound();
                             }
                         }
                         _("PLACED ON SPIKE");



                         //add it to the spike clicked on
                         sp.addPiece(pieceStuckToMouse);
                         //and make sure nothing is stuck to mouse by finalising move like this
                         _("UNSTUCK");
                         unstickPieceFromMouse();
                         // USE UP THE CORRECT DIE
                         Die theDieThatGotUsHere = sp.get_stored_die();

                         // Here we check if both dice have been used so we can move onto next players turn:
                         //UNLESS someone rolled a double
                        if (someoneRolledADouble && doubleRollCounter<=3)
                        {
                            _("Player is still enjoying his double round so dont move on. y");
                           board.calculatePotentialNumberOfMoves=true;//so they get calc'd at start of each go.
                           _("DONT USE UP DICE SINCE ITS A DOUBLE XXX");
                        }
                        else
                        {
                             if (theDieThatGotUsHere.getValue()==Board.die1.getValue())
                             {
                                 _("DIE1 USED GETTING OFF BAR "+Board.die1.getValue());
                                 Board.die1HasBeenUsed=true;

                             } else
                             {
                                 _("DIE2 USED GETTING OFF BAR "+Board.die2.getValue());
                                 Board.die2HasBeenUsed=true;

                             }
                             _("CORRECT DIE USED UP.");
                        }
                         //done getting off bar

                         if (someoneRolledADouble)
                         {
                             _("doubleRollCounter incremented!");
                            doubleRollCounter++;//increment this here to keep a track fi thi was a dbl
                         }

                     }
                     else
                     {
                         //_("NO WE CANT DROP OFF AT THIS SPIKE "+sp.getSpikeNumber());
                     }
                 }
             }


             //DIE1 MOVE
             if (pieceStuckToMouse!=null && board.copy_of_reachableFromDie1!=null && spike.getSpikeNumber()==board.copy_of_reachableFromDie1.getSpikeNumber())
             {
                 _("clicked on valid potential spike (die1)");

                 placePieceRemoveOldOneAndSetDieToUsed(1,false);


                 //set the old potential spike to null since they are no longer valid
                 //EXPERIMENTAL!
                 //////////// board.copy_of_reachableFromDie1=null;
                  ///////////// board.copy_of_reachableFromBothDice=null;



     //YES THIS IS NEEDED
                   return;//EXPERMINETAL so it doesnt do any more checks since we are using this die
             }

             //this isnt needed at this point (is it?)
           ///   if (board.copy_of_reachableFromDie2==null)
             // {   _("copy_of_reachableFromDie2 not ready yet.");
             //     return;
             // }

             //if (board.copy_of_reachableFromDie2==null){HAL._E("board.copy_of_reachableFromDie2 was null");}

             //DIE2 MOVE
             if (pieceStuckToMouse!=null && board.copy_of_reachableFromDie2!=null && spike.getSpikeNumber()==board.copy_of_reachableFromDie2.getSpikeNumber())
             {
                 _("clicked on valid potential spike (die2)");

                 placePieceRemoveOldOneAndSetDieToUsed(2,false);


                 //set the old potential spike to null since they are no longer valid
                 //EXPERIMENTAL!
                 //////// board.copy_of_reachableFromDie2=null;
                 ///////////  board.copy_of_reachableFromBothDice=null;

                 //YES THIS IS NEEDED
                   return;//EXPERMINETAL so it doesnt do any more checks since we are using this die
             }

             //this isnt needed at this point (is it?)
            //  if (board.copy_of_reachableFromBothDice==null)
              //{   _("copy_of_reachableFromBothDice not ready yet.");
            //      return;
            //  }

             //DIE1 + DIE2 MOVE
             if (pieceStuckToMouse!=null && board.copy_of_reachableFromBothDice!=null && spike.getSpikeNumber()==board.copy_of_reachableFromBothDice.getSpikeNumber())
             {
                 _("clicked on valid potential spike (die1+die2)");

                 placePieceRemoveOldOneAndSetDieToUsed(3,false);


                 //set the old potential spike to null since they are no longer valid
                 //EXPERIMENTAL!
                 ///////// board.copy_of_reachableFromDie1=null;
                 //////// board.copy_of_reachableFromDie2=null;
                 ////////  board.copy_of_reachableFromBothDice=null;
             }
        }
    }
 }

 public static Vector theBarWHITE = new Vector(4);//the bar holds pieces that get killed
 public static Vector theBarBLACK = new Vector(4);//the bar holds pieces that get killed

 //these store the pieces that have been sent to the container, when all are in that player wins.
 public static Vector whitePiecesSafelyInContainer=new Vector(15);
 public static Vector blackPiecesSafelyInContainer=new Vector(15);
 // removes piece from the spike it came from, adds it to the new one just clicked on, and sets the die that did this to used
 // dieToSetUnused requires 1 or 2 (representing die 1 or die 2), OR 3 (3 IS BOTH DICE)
 //pieceWillGoToContainer is used ONLY when we are removig a piece from a spike and then adding it to the PIECE CONTAINER, in all other
 //situations its simply removing from one spike and adding to another
 private void placePieceRemoveOldOneAndSetDieToUsed(int dieToSetUnused, boolean pieceWillGoToContainer)
 {
     _("placePieceRemoveOldOneAndSetDieToUsed dieToSetUnused:"+dieToSetUnused);// board.copy_of_reachableFromDie1:"+board.copy_of_reachableFromDie1+" board.copy_of_reachableFromDie1.pieces.size():"+board.copy_of_reachableFromDie1.pieces.size());
     if (pieceStuckToMouse==null)
     {
         HAL._E("pieceStuckToMouse was null somehow");
     }
        //remove piece from its current spike
         originalSpikeForPieceSelected.removePiece(pieceStuckToMouse);




     if (dieToSetUnused==1)
     {
        if (pieceWillGoToContainer)
        {
            if (Board.whoseTurnIsIt==Player.WHITE)
            {
                whitePiecesSafelyInContainer.add(pieceStuckToMouse);
                 _("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:"+whitePiecesSafelyInContainer.size());
                ActivityGammon.pieceputaway.start();//sfxPutPieceInContainer.playSound();
            }else
            if (Board.whoseTurnIsIt==Player.BLACK)
            {
                blackPiecesSafelyInContainer.add(pieceStuckToMouse);
                 _("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:"+whitePiecesSafelyInContainer.size());
                ActivityGammon.pieceputaway.start();//sfxPutPieceInContainer.playSound();
            } else {HAL._E("whoseTurnIsIt is invalid here.");}
        }
        else
        {

             //// SPECIAL CONDITION - WAS A PIECE KILLED?////////////////////
             if (Board.whoseTurnIsIt==Player.WHITE && board.copy_of_reachableFromDie1.getAmountOfPieces(Player.BLACK)>0)
             {

                    _("WHITE KILLED A BLACK");
                    Piece firstPiece = (Piece)board.copy_of_reachableFromDie1.pieces.firstElement();
                    board.copy_of_reachableFromDie1.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromDie1.addPiece(pieceStuckToMouse);
                    theBarBLACK.add(firstPiece); // add it to the BAR
                 ActivityGammon.killed.start();//sfxKilled.playSound();

             } else
             if (Board.whoseTurnIsIt==Player.BLACK && board.copy_of_reachableFromDie1.getAmountOfPieces(Player.WHITE)>0)
             {

                    _("BLACK KILLED A WHITE");
                    Piece firstPiece = (Piece)board.copy_of_reachableFromDie1.pieces.firstElement();
                    board.copy_of_reachableFromDie1.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromDie1.addPiece(pieceStuckToMouse);
                    theBarWHITE.add(firstPiece); // add it to the BAR
                 ActivityGammon.killed.start();// sfxKilled.playSound();

             } else
             ///////////////////////////////////
             {
                    //NORMAL CONDITION
                     //add it to the spike user just clicked on
                     board.copy_of_reachableFromDie1.addPiece(pieceStuckToMouse);
             }
        }


         //so player cant use die one again
         //(and it wont come up as a potential valid option)
         Board.die1HasBeenUsed=true;
         _("die1HasBeenUsed A.");
     }
     else
     if (dieToSetUnused==2)
     {
         if (pieceWillGoToContainer)
        {
            if (Board.whoseTurnIsIt==Player.WHITE)
            {
                whitePiecesSafelyInContainer.add(pieceStuckToMouse);
                 _("whitePiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:"+whitePiecesSafelyInContainer.size());
                ActivityGammon.pieceputaway.start();//sfxPutPieceInContainer.playSound();
            }else
            if (Board.whoseTurnIsIt==Player.BLACK)
            {
                blackPiecesSafelyInContainer.add(pieceStuckToMouse);
                 _("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:"+whitePiecesSafelyInContainer.size());
                ActivityGammon.pieceputaway.start();//  sfxPutPieceInContainer.playSound();
            } else {HAL._E("whoseTurnIsIt is invalid here.");}
        }
        else
        {
            //// SPECIAL CONDITION - WAS A PIECE KILLED?////////////////////
             if (Board.whoseTurnIsIt==Player.WHITE && board.copy_of_reachableFromDie2.getAmountOfPieces(Player.BLACK)>0)
             {

                    _("WHITE KILLED A BLACK");
                    Piece firstPiece = (Piece)board.copy_of_reachableFromDie2.pieces.firstElement();
                    board.copy_of_reachableFromDie2.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromDie2.addPiece(pieceStuckToMouse);
                    theBarBLACK.add(firstPiece); // add it to the BAR
                 ActivityGammon.killed.start();// sfxKilled.playSound();

             } else
             if (Board.whoseTurnIsIt==Player.BLACK && board.copy_of_reachableFromDie2.getAmountOfPieces(Player.WHITE)>0)
             {

                    _("BLACK KILLED A WHITE");
                    Piece firstPiece = (Piece)board.copy_of_reachableFromDie2.pieces.firstElement();
                    board.copy_of_reachableFromDie2.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromDie2.addPiece(pieceStuckToMouse);
                    theBarWHITE.add(firstPiece); // add it to the BAR
                 ActivityGammon.killed.start();// sfxKilled.playSound();

             } else
             ///////////////////////////////////
             {
                    //NORMAL CONDITION
                     //add it to the spike user just clicked on
                     board.copy_of_reachableFromDie2.addPiece(pieceStuckToMouse);
             }
        }
         //so player cant use die one again
         //(and it wont come up as a potential valid option)
         Board.die2HasBeenUsed=true;
         _("die2HasBeenUsed AA.");
     }
     else
     if (dieToSetUnused==3)
     {
            if (pieceWillGoToContainer)
            {
                if (Board.whoseTurnIsIt==Player.WHITE)
                {
                    whitePiecesSafelyInContainer.add(pieceStuckToMouse);
                     _("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:"+whitePiecesSafelyInContainer.size());
                    ActivityGammon.pieceputaway.start();//sfxPutPieceInContainer.playSound();
                }else
                if (Board.whoseTurnIsIt==Player.BLACK)
                {
                    blackPiecesSafelyInContainer.add(pieceStuckToMouse);
                     _("blackPiecesSafelyInContainer HAS HAD ONE ADDED TO IT, NEW SIZE:"+whitePiecesSafelyInContainer.size());
                    ActivityGammon.pieceputaway.start();//sfxPutPieceInContainer.playSound();
                } else {HAL._E("whoseTurnIsIt is invalid here.");}
            }
            else
            {
               //// SPECIAL CONDITION - WAS A PIECE KILLED?////////////////////
             if (Board.whoseTurnIsIt==Player.WHITE && board.copy_of_reachableFromBothDice.getAmountOfPieces(Player.BLACK)>0)
             {

                    _("WHITE KILLED A BLACK");
                    Piece firstPiece = (Piece)board.copy_of_reachableFromBothDice.pieces.firstElement();
                    board.copy_of_reachableFromBothDice.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromBothDice.addPiece(pieceStuckToMouse);
                    theBarBLACK.add(firstPiece); // add it to the BAR
                 ActivityGammon.killed.start();//sfxKilled.playSound();

             } else
             if (Board.whoseTurnIsIt==Player.BLACK && board.copy_of_reachableFromBothDice.getAmountOfPieces(Player.WHITE)>0)
             {

                    _("BLACK KILLED A WHITE");
                    Piece firstPiece = (Piece)board.copy_of_reachableFromBothDice.pieces.firstElement();
                    board.copy_of_reachableFromBothDice.removePiece(firstPiece);//remove that piece and
                    board.copy_of_reachableFromBothDice.addPiece(pieceStuckToMouse);
                    theBarWHITE.add(firstPiece); // add it to the BAR
                    ActivityGammon.killed.start();//sfxKilled.playSound();

             } else
             ///////////////////////////////////
             {
                    //NORMAL CONDITION
                     //add it to the spike user just clicked on
                     board.copy_of_reachableFromBothDice.addPiece(pieceStuckToMouse);
             }
            }
          //so player cant use die one OR die two again
          //(and it wont come up as a potential valid option)
          Board.die1HasBeenUsed=true;
          Board.die2HasBeenUsed=true;

           _("die1HasBeenUsed B.");
            _("die2HasBeenUsed B.");

     }
     else
     {
         HAL._E("ERROR CANT TELL WHICH DICE TO SET AS UNUSED. dieToSetUnused:"+dieToSetUnused);
     }
     //and make sure nothing is stuck to mouse by finalising move like this
         unstickPieceFromMouse();

     if (someoneRolledADouble)
     {

         // this logic was hard to understand when mixed so i duplicated it here due to the subtle diffs
         switch (dieToSetUnused)
         {
             case 1:
                 doubleRollCounter++;
                 _("someoneRolledADouble DIE 1 doubleRollCounter:"+doubleRollCounter);
                 if (doubleRollCounter<=1)
                 {
                     _("dont hide die yet as it was a double");
                    Board.die1HasBeenUsed=false;// so it doesnt vanish
                 }
                 if (doubleRollCounter>=4)
                 {
                     _("double round done.1");
                     //ADDED TO FIX DOUBLES ISSUE 243PM JAN 21
                    Board.die1HasBeenUsed=true;//so they dont vanish
                    Board.die2HasBeenUsed=true;
                    someoneRolledADouble=false;
                 }
                 break;
             case 2:
                 doubleRollCounter++;
                 _("someoneRolledADouble DIE2 doubleRollCounter:"+doubleRollCounter);
                 if (doubleRollCounter<=3)
                 {
                     _("dont hide die yet as it was a double");
                     Board.die2HasBeenUsed=false;//so it doesnt vanish
                 }
                 if (doubleRollCounter>=4)
                 {
                     _("double round done.2");
                     //ADDED TO FIX DOUBLES ISSUE 243PM JAN 21
                    Board.die1HasBeenUsed=true;//so they dont vanish
                    Board.die2HasBeenUsed=true;
                    someoneRolledADouble=false;
                 }
                 break;
             case 3:
                 doubleRollCounter++;doubleRollCounter++;//2 dice used in a roll like this
                 _("someoneRolledADouble BOTH DIE doubleRollCounter:"+doubleRollCounter);
                 _("dont hide die yet as it was a double");
                  Board.die1HasBeenUsed=false;//so they dont vanish
                Board.die2HasBeenUsed=false;
                if (doubleRollCounter>=4)
                 {
                     _("double round done.3");
                     Board.die1HasBeenUsed=true;//so they do vanish
                     _("die1HasBeenUsed C.");
                    Board.die2HasBeenUsed=true;
                    someoneRolledADouble=false;
                }
                 break;
             default:
                 HAL._E("placePieceRemoveOldOneAndSetDieToUsed error in die number");
                 break;
         }

     }
 }



 //when the user clicks on a piece and it sticks to the mouse we hold it in
 //pieceStuckToMouse, this variable below is the spike that holds pieceStuckToMouse
 //we use it so we can add/remove the pieceStuckToMouse from this spike if its
 //placed onto a new one.
 Spike originalSpikeForPieceSelected;
public static boolean barPieceStuckOnMouse;
 //indicates if this mouse click has been on a piece
 private void checkIfPieceClickedOn(int x,int y)
 {
     if (pieceOnMouse)
     {
         //special case, if the player already has a piece stuck on the mouse dont let another
         //one go on, this causes an error in the game, best way is to simply leap out of
         //this method here if this is true
         _("pieceOnMouse special case ignore this piece click");
         return;
     }

     //check pieces on bar
     if (Board.pickingPieceUpFromBar)
     {
         Vector piecesOnTheBar=null;
         if (Board.whoseTurnIsIt==Player.WHITE)
         {
             piecesOnTheBar=theBarWHITE;
         } else
         if (Board.whoseTurnIsIt==Player.BLACK)
         {
             piecesOnTheBar=theBarBLACK;
         }
         Enumeration e = piecesOnTheBar.elements();
         while (e.hasMoreElements())
         {
             Piece p = (Piece) e.nextElement();
             if (p.userClickedOnThis(x, y))
             {
                 _("PIECE ON THE BAR CLICKED ON.");
                 p.stickToMouse();
                 pieceOnMouse=true;
                 barPieceStuckOnMouse=true;
                 pieceStuckToMouse=p;


             }
         }
     }

     //grab the spikes, loop thru them checking every single
    //piece to see if the user clicked on that piece
    Enumeration spikes_e = board.spikes.elements();
    while(spikes_e.hasMoreElements())
    {
        Spike spike = (Spike) spikes_e.nextElement();
        Enumeration pieces_e = spike.pieces.elements();
        while(pieces_e.hasMoreElements())
        {
            Piece piece = (Piece) pieces_e.nextElement();
            if (piece.userClickedOnThis(x, y))
            {
                // only allow picking up of OUR OWN pieces
                // AND check if we already have a piece or not.
                //this was a bug so hopefully fixed.
                if (board.allowPieceToStickToMouse && piece.getColour()==Board.whoseTurnIsIt && !pieceOnMouse) //And it has potential moves (i.e. not pointless to pick up)
                {
                    _("PICKED UP PIECE: "+Board.playerStr(piece.getColour()));
                    //if this piece has options then we allow it to stick to
                    //mouse, ie we allow player to pick it up..
                    piece.stickToMouse();
                    pieceOnMouse=true;
                    pieceStuckToMouse=piece;
                    originalSpikeForPieceSelected=spike;//keep a copy of this piece's original Spike (for removing the piece later if need be)
                }
                _("Piece was clicked on ("+piece+") board.allowPieceToStickToMouse:"+board.allowPieceToStickToMouse+" board.whoseTurnIsIt:"+board.whoseTurnIsIt);
            }
        }
    }
 }

 public static boolean pieceOnMouse=false;//is true when a piece is stuck to mouse
 public static Piece pieceStuckToMouse;//this is simply a copy of whatever piece (if any) is stuck to mouse

 public void mouseEntered(MouseEvent e)
 {

 }

 public void mouseExited(MouseEvent e)
 {

 }

 public void mousePressed(MouseEvent e)
 {

 }

 public void mouseReleased(MouseEvent e)
 {

 }

 public void mouseDragged(MouseEvent e)
 {
        _("mousedragged");
 }
public static boolean I_AM_CLIENT;
public static boolean I_AM_SERVER;
 public void mouseMoved(MouseEvent e)
 {
     //so our mouse doesnt influence anything
    if (Bot.FULL_AUTO_PLAY || (!Bot.dead && Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.BLACK) )
    {
        //_("mouse wont respond");
    }
    else
    {
        if (NETWORK_GAME_IN_PROCESS)
        {
            if (I_AM_CLIENT && Board.whoseTurnIsIt==Player.WHITE)
            {
                pointerX=e.getX();
                pointerY=e.getY();
            }
            if (I_AM_SERVER && Board.whoseTurnIsIt==Player.BLACK)
            {
                pointerX=e.getX();
                pointerY=e.getY();
            }
        }

        //stick these bck in as local play was broke without--
        pointerX=e.getX();
                pointerY=e.getY();

        Board.mouseHoverX=pointerX;//e.getX();
        Board.mouseHoverY=pointerY;//e.getY();

    //Bot.currentMouseX=pointerX;
    //Bot.currentMouseY=pointerY;
    }
 }
 public static boolean DEBUG_CONSOLE=false;
 boolean PAUSED;
 boolean freeze;
 NetworkChatClient chatClient;
 boolean ignoreRepaints=true;
 public static String chatText="";
 //Respond to keypresses.
 public void keyPressed(KeyEvent e)
 {
    _("keyPressed");
   /*
    //TEXT ENTRY IN LOBBY
    if (state==NETWORKING_LOBBY)
    {
        if ( e.getKeyCode()==KeyEvent.VK_ENTER )
        {
            chatClient.send();
            chatText="";
        }
        String letter = ""+e.getKeyChar();
        chatText+=letter;
                return;
    }
    ////////////////////////

    //////////////NAME ENTRY////////
    if (state==NETWORKING_ENTER_NAME)
    {
        if ( e.getKeyCode()==KeyEvent.VK_ENTER )
        {
            state=NETWORKING_LOBBY;
            chatClient=new NetworkChatClient(this);

        }
        if ( (e.getKeyCode()==KeyEvent.VK_DELETE || e.getKeyCode()==KeyEvent.VK_BACK_SPACE || e.getKeyCode()==KeyEvent.VK_SHIFT || e.getKeyCode()==KeyEvent.VK_CAPS_LOCK)
                && NetworkChatClient.nick.length()>0)
        {
            NetworkChatClient.nick=NetworkChatClient.nick.substring(0, NetworkChatClient.nick.length()-1);
        }
        else
        {
            String letter = ""+e.getKeyChar();
            if ( letter.equals(" ") || NetworkChatClient.nick.length()>10 )
            {

            }
            else
            {
                NetworkChatClient.nick+=letter;
            }
        }

        return;
    }
    /////////////////////////////////




    if (e.getKeyCode()==KeyEvent.VK_F1)
    {

        ignoreRepaints=!ignoreRepaints;
         setIgnoreRepaint(ignoreRepaints);
         jFrame.setResizable(!ignoreRepaints);
         tellPlayers("F1 Pressed, ignoreRepaints is now "+ignoreRepaints);
         _("F1 Pressed, ignoreRepaints is now "+ignoreRepaints);
    }

    if (e.getKeyChar()=='q' || e.getKeyChar()=='Q')//QUIT
    {
        System.exit(0);
    }


    if (e.getKeyChar()=='f' || e.getKeyChar()=='F')//QUIT
    {
//setIgnoreRepaint(Bot.FULL_AUTO_PLAY);//this is the key to it not flickering on my desktop
        Bot.FULL_AUTO_PLAY=!Bot.FULL_AUTO_PLAY;
        Board.HUMAN_VS_COMPUTER=!Board.HUMAN_VS_COMPUTER;
        Bot.dead=!Bot.FULL_AUTO_PLAY;
        _("Bot.dead:"+Bot.dead);
        paintRobotMessages=Bot.FULL_AUTO_PLAY;
        _("FULL_AUTO_PLAY:"+Bot.FULL_AUTO_PLAY);
        if (Bot.FULL_AUTO_PLAY)
        {
            tellRobot(true,"Bot turned on.");
        }
        else
        {
            tellRobot(true,"Bot turned off.");
        }
    }
    if (e.getKeyChar()=='p' || e.getKeyChar()=='P')//QUIT
    {
        //PAUSE
        PAUSED=!PAUSED;
        Bot.dead=PAUSED;
        _("PAUSED:"+PAUSED);


    }



    if (e.getKeyChar()=='s' || e.getKeyChar()=='S')//QUIT
    {
        //PAUSE
        SOUND_ON=!SOUND_ON;

        _("SOUND_ON:"+SOUND_ON);


    }
    if (DEBUG_CONSOLE && e.getKeyChar()=='x' || e.getKeyChar()=='X')//QUIT
    {


        sfxError.playSound();
       //// _("play windows test sound");
        ////sfxmouseClick.testSound();


    }
    if (DEBUG_CONSOLE && e.getKeyChar()=='j' || e.getKeyChar()=='J')//QUIT
    {


        Bot.JUMP_DIRECT_TO_DEST=!Bot.JUMP_DIRECT_TO_DEST;
        _("Bot.JUMP_DIRECT_TO_DEST:"+Bot.JUMP_DIRECT_TO_DEST);
       //// _("play windows test sound");
        ////sfxmouseClick.testSound();


    }


    if (!RELEASE_BUILD && e.getKeyChar()=='c' || e.getKeyChar()=='C')//DEBUG
    {
        showCollisions=!showCollisions;
        //PAINT_STATE=showCollisions;//!PAINT_STATE;
    }
    if (!RELEASE_BUILD && e.getKeyChar()=='l' || e.getKeyChar()=='L')//DEBUG
    {
        HAL.CANVAS_LOGGING=!HAL.CANVAS_LOGGING;
    }

    if (!RELEASE_BUILD && e.getKeyChar()=='d' || e.getKeyChar()=='D')//DEBUG
    {
        //showCollisions=!showCollisions;

        //

        // make sure home pieces can appear in all right places, including edge spikes
        //board.initialiseBoard(Board.DEBUG_BOARD_WHITE_PIECES_IN_THEIR_HOME);
        //board.initialiseBoard(Board.DEBUG_BOARD_BLACK_PIECES_IN_THEIR_HOME);

        //Bot.STOPCLICKING=!Bot.STOPCLICKING;
        //_("STOPCLICKING:"+Bot.STOPCLICKING);
        DEBUG_CONSOLE=!DEBUG_CONSOLE;
    }
    if (e.getKeyChar()=='t' || e.getKeyChar()=='T')
    {
        theme++;
        setTheme(theme);
    }    */
    /*if (e.getKeyChar()=='z' || e.getKeyChar()=='Z')
    {
        DEBUG_FORCE_DOUBLES_ON_ORDINARY_ROLLS=!DEBUG_FORCE_DOUBLES_ON_ORDINARY_ROLLS;
        _("DEBUG_FORCE_DOUBLES_ON_ORDINARY_ROLLS is "+DEBUG_FORCE_DOUBLES_ON_ORDINARY_ROLLS);
    }*/


    /*if (e.getKeyChar()=='f' || e.getKeyChar()=='F')
    {
        freeze=!freeze;
        _("freeze:"+freeze);
    }*/

     /*
    if (DEBUG_CONSOLE)
    {
        if(e.getKeyCode()==KeyEvent.VK_UP)
        {
           _("UP");
            debugMenuPos--;
           if (debugMenuPos<0)
               debugMenuPos=0;

        }
        if(e.getKeyCode()==KeyEvent.VK_DOWN)
        {
           _("DOWN");
           debugMenuPos++;
           if (debugMenuPos>LAST_DEBUG_OPTION)
               debugMenuPos=LAST_DEBUG_OPTION;

        }
        if(e.getKeyCode()==KeyEvent.VK_LEFT)
        {
           _("LEFT");
           debugOptionChanged(DEBUGLEFT);
        }
        if(e.getKeyCode()==KeyEvent.VK_RIGHT)
        {
           _("RIGHT");
           debugOptionChanged(DEBUGRIGHT);
        }



    }
                       */
 }

 public void keyReleased(KeyEvent e)
 {

 }

 public void keyTyped(KeyEvent e)
 {

 }

 //////////////////THEMES CODE/////////////////
 public static final int DEFAULT   = 0;
 public static final int METALIC   = 1;
 public static final int CLASSIC   = 2;
 public static final int FUNNYMAN  = 3;
 public static final int BUMBLEBEE = 4;
 public static final int MAX_THEMES=4;//this should always equals the last one
 int theme=DEFAULT;
 String themeName;
 boolean firstThemeSet=true;//so we dont tell players when the theme is set upon loading but we do othertimes
 //sets all colours in one go
 public void setTheme(int theme_)
 {


     theme=theme_;
     _("theme:"+theme);
     if (theme>MAX_THEMES)
     {
         theme=DEFAULT;
     }

     switch(theme)
     {
         case DEFAULT:  _("THEME SET TO DEFAULT");themeName="default";
                        if (!firstThemeSet)
                            tellPlayers("Theme set to "+themeName);
                        themecolours=defaultms;   break;
         case METALIC:  _("THEME SET TO METALIC");themeName="metalic";
                        if (!firstThemeSet)
                            tellPlayers("Theme set to "+themeName);
                        themecolours=metalic;     break;
         case CLASSIC:  _("THEME SET TO CLASSIC");themeName="classic";
                        if (!firstThemeSet)
                            tellPlayers("Theme set to "+themeName);
                        themecolours=classic;     break;
         case FUNNYMAN: _("THEME SET TO FUNNYMAN");themeName="funnyman";
                        if (!firstThemeSet)
                            tellPlayers("Theme set to "+themeName);
                        themecolours=funnyman;   break;
         case BUMBLEBEE:_("THEME SET TO BUMBLEBEE");themeName="bumblebee";
                        if (!firstThemeSet)
                            tellPlayers("Theme set to "+themeName);
                        themecolours=bumblebee; break;
         default: HAL._E("theme is out of range!");
     }
     firstThemeSet=false;
     //assigns each colour from the one specified
     for (int i=0; i<themecolours.length; i++)
     {
         switch(i)
         {
             case 0: BACKGROUND_COLOUR              =themecolours[i]; break;
             case 1: PANEL_COLOUR                   =themecolours[i]; break;
             case 2: ROLL_BUTTON_COLOUR             =themecolours[i]; break;
             case 3: Board.BOARD_COLOUR             =themecolours[i]; break;
             case 4: Board.BAR_COLOUR               =themecolours[i]; break;
             case 5: Spike.BLACK_SPIKE_COLOUR       =themecolours[i]; break;
             case 6: Spike.WHITE_SPIKE_COLOUR       =themecolours[i]; break;
             case 7: Piece.WHITE_PIECE_COLOUR       =themecolours[i]; break;
             case 8: Piece.BLACK_PIECE_COLOUR       =themecolours[i]; break;
             case 9: Piece.WHITE_PIECE_INNER_COLOUR =themecolours[i]; break;
             case 10: Piece.BLACK_PIECE_INNER_COLOUR=themecolours[i]; break;
             case 11: Die.DIE_COLOUR                =themecolours[i]; break;
             case 12: Die.DOT_COLOUR                =themecolours[i]; break;
             default: HAL._E("theme state error, should not exceed 12!");
         }
     }
     //force recreation of colour objects
     //we pass true into makeColourObjects to force them to remake themselves
     //with the new colour values and thus repaint with new theme
     CustomCanvas.makeColourObjects(true);
     Board.makeColourObjects(true);
     Spike.makeColourObjects(true);
     Piece.makeColourObjects(true);
     Die.makeColourObjects(true);
     _("Theme is loaded now and working.");
 }

 // make colour objects
 public static void makeColourObjects(boolean forceRecreation)
 {
     if (panel_colour==null || forceRecreation)
     {
         panel_colour=new Color();///(PANEL_COLOUR);
     }
     if (background_colour==null || forceRecreation)
     {
         background_colour=new Color();//BACKGROUND_COLOUR);

     }
     if (roll_button_colour==null || forceRecreation)
     {
         roll_button_colour= new Color();//ROLL_BUTTON_COLOUR);
     }
 }

 //public static ImageObserver this_;
 public static CustomFont fontwhite, fontblack;
 // prepare the customfont
 private void loadCustomFonts()
 {
     if (fontwhite==null)
     {
         boolean land=false;
         int gap=10;
         String path="/";
         int makeLettersCloserValue=3; // some confusion here, gap is not used, adjust this value to make letters closer or further apart.
         int GAP=3;//REAL GAP VAL, REMOVE REDUNDANT ONES (TODO)- (lower value the bigger gap)
         try
         {
             hal._("loading fonts:");
             fontwhite = CustomFont.getFont( hal.loadImage(path+"whitefont.png",R.drawable.whitefont), CustomFont.SIZE_SMALL,    CustomFont.STYLE_PLAIN, land,32,93,GAP,gap,true);
             if (fontwhite==null)
             {
                 HAL._E("-- fontwhite image is null");
             }
             fontblack = CustomFont.getFont( hal.loadImage(path+"blackfont.png",R.drawable.blackfont), CustomFont.SIZE_SMALL,    CustomFont.STYLE_PLAIN, land,32,93,GAP,gap,true);
             if (fontblack==null)
             {
                 HAL._E("-- fontblack image is null");
             }
         }
         catch(Exception e)
         {
             HAL._E("== error loading fonts "+e.getMessage());
         }
      }
     else
     {
         _("Fonts already pre-cached...");
     }
 }

 //for debugging, paints sytem.out to screen
 public void paintStringsToCanvas(Graphics g)
 {
     Enumeration e = HAL.systemOuts.elements();
     int x=3;
     int y=3;
     while (e.hasMoreElements())
     {
         String printthis = (String)e.nextElement();
         fontwhite.drawString(g, printthis, x, y, 0);y+=fontwhite.getHeight();
     }
 }
static boolean showPlayerMessage=true;
 static long playerMessageSetTimeLong=System.currentTimeMillis();//thjis keeps the version on screen for a few secs at the start
 //sets the vars to allow a message to be shown to the player in bottom right for
 //a while
 public static void tellPlayers(String s)
 {
    // _("tellPlayers:"+s);
     showPlayerMessage=true;
     playerMessageSetTimeLong=System.currentTimeMillis();
     showMeFor=0;
     message2Players=s;
 }
 static int showMeForROBOT;
 public static void robotExplain(String s)
 {
     /////////s+=":"+Board.methodNOW;
     //_("robotExplain:"+s);
     showMeForROBOT=0;
     robotMessageSetTimeLong=System.currentTimeMillis();
     robotMoveDesc=s;
 }
 //tells them essentials
 static int showMeFor=0;
 public static final long SHOW_ME_LIMIT=3000;//how long  show player message 1.5 sec
 static String message2Players=""+VERSION;

 int messageWidth, messageHeight;
 int messagex,messagey;
 //paint the mssage to the players
 public void paintMessageToPlayers(Graphics g)
 {
     //hal.setColor(g, Color.BLACK);
     hal.setColor(g, 0,0,0,TRANSPARENCY_LEVEL);

     


     if (Board.gameComplete)
     {
         messageWidth=fontwhite.stringWidth(message2Players+"  ");
     messagex=(WIDTH/2)-messageWidth/2;
     messageHeight=fontwhite.getHeight();
     messagey=(HEIGHT/2)-messageHeight/2;
     
          hal.fillRoundRect(g,messagex,messagey,messageWidth,messageHeight,true);
         hal.setColor(g, Color.WHITE);
         hal.drawRoundRect(g,messagex,messagey,messageWidth,messageHeight);
         //draw message in middle of screen
        fontwhite.drawString(g, message2Players, messagex+7, messagey+1, 0);
     }
     else
     {
         messageWidth=fontwhite.stringWidth(message2Players+"  ");
     messagex=10;
     messagey=HEIGHT-(fontwhite.getHeight()+TINY_GAP);
     messageHeight=fontwhite.getHeight();
          hal.fillRoundRect(g,messagex,messagey,messageWidth,messageHeight,true);
         hal.setColor(g, Color.WHITE);
         hal.drawRoundRect(g,messagex,messagey,messageWidth,messageHeight);
         //draw message in bottom left.
         fontwhite.drawString(g, message2Players, messagex+7, messagey+1, 0);
     }
 }

 public static boolean paintRobotMessages;
 //paint the robot message
 public void paintRobotMessage(Graphics g)
 {
    

     //hal.setColor(g, Color.BLACK);
     hal.setColor(g, 0,0,0,TRANSPARENCY_LEVEL);
     
     messageWidth=fontwhite.stringWidth(robotMoveDesc+"  ");
     messagex=WIDTH-(messageWidth+10);
     messagey=10;//(fontwhite.getHeight()+TINY_GAP);
     messageHeight=fontwhite.getHeight();

     hal.fillRoundRect(g,messagex,messagey,messageWidth,messageHeight,true);
     hal.setColor(g, Color.RED);
     hal.drawRoundRect(g,messagex,messagey,messageWidth,messageHeight);
     fontwhite.drawString(g, robotMoveDesc, messagex+7, messagey+1, 0);
 }

 /////////////////////ADJUST COLOURS HERE ////////////////////////////
  //themes: specify colours for colour themes
 public static int themecolours[];//this gets assigned in constructor
 // DEFAULT VALUES (ms xp backgammon colours)
 public static int defaultms[] = {
                BACKGROUND_COLOUR,
                PANEL_COLOUR,
                ROLL_BUTTON_COLOUR,
                Board.BOARD_COLOUR,
                Board.BAR_COLOUR,
                Spike.BLACK_SPIKE_COLOUR,
                Spike.WHITE_SPIKE_COLOUR,
                Piece.WHITE_PIECE_COLOUR,
                Piece.BLACK_PIECE_COLOUR,
                Piece.WHITE_PIECE_INNER_COLOUR,
                Piece.BLACK_PIECE_INNER_COLOUR,
                Die.DIE_COLOUR,
                Die.DOT_COLOUR
                };

 public static int metalic[] = {
                /*BACKGROUND_COLOUR*/               0xffffff,
                /*PANEL_COLOUR*/                    0x828284,
                /*ROLL_BUTTON_COLOUR*/              0xffffff,
                /*Board.BOARD_COLOUR*/              0x9b9b9b,
                /*Board.BAR_COLOUR*/                0x8b898c,
                /*Spike.BLACK_SPIKE_COLOUR*/        0xc7c8cd,
                /*Spike.WHITE_SPIKE_COLOUR*/        0xa3a4a8,
                /*Piece.WHITE_PIECE_COLOUR*/        0xedf0f5,
                /*Piece.BLACK_PIECE_COLOUR*/        0x1a1a22,
                /*Piece.WHITE_PIECE_INNER_COLOUR*/  0xffffff,
                /*Piece.BLACK_PIECE_INNER_COLOUR*/  0xffffff,
                /*Die.DIE_COLOUR*/                  0x807875,
                /*Die.DOT_COLOUR*/                  0xe5e0da
                };

public static int classic[] = {
                /*BACKGROUND_COLOUR*/               0x2c632a,
                /*PANEL_COLOUR*/                    0x002001,
                /*ROLL_BUTTON_COLOUR*/              0xfe1e1c,
                /*Board.BOARD_COLOUR*/              0xf4ebca,
                /*Board.BAR_COLOUR*/                0x245223,
                /*Spike.BLACK_SPIKE_COLOUR*/        0x99643c,
                /*Spike.WHITE_SPIKE_COLOUR*/        0xed974c,
                /*Piece.WHITE_PIECE_COLOUR*/        0xfefbf2,
                /*Piece.BLACK_PIECE_COLOUR*/        0x363b3f,
                /*Piece.WHITE_PIECE_INNER_COLOUR*/  0xffffff,
                /*Piece.BLACK_PIECE_INNER_COLOUR*/  0xffffff,
                /*Die.DIE_COLOUR*/                  0xfe1e1c,
                /*Die.DOT_COLOUR*/                  0xfffdfe
                };


public static int funnyman[] = {
                /*BACKGROUND_COLOUR*/               0x661913,
                /*PANEL_COLOUR*/                    0x210d0c,
                /*ROLL_BUTTON_COLOUR*/              0xffffff,
                /*Board.BOARD_COLOUR*/              0x9d581d,
                /*Board.BAR_COLOUR*/                0x490f0e,
                /*Spike.BLACK_SPIKE_COLOUR*/        0x290d0a,
                /*Spike.WHITE_SPIKE_COLOUR*/        0x6e1213,
                /*Piece.WHITE_PIECE_COLOUR*/        0x4e3113,
                /*Piece.BLACK_PIECE_COLOUR*/        0x841b25,
                /*Piece.WHITE_PIECE_INNER_COLOUR*/  0xffffff,
                /*Piece.BLACK_PIECE_INNER_COLOUR*/  0xffffff,
                /*Die.DIE_COLOUR*/                  0xffffff,
                /*Die.DOT_COLOUR*/                  0x791216
                };

public static int bumblebee[] = {
                /*BACKGROUND_COLOUR*/               0x202427,
                /*PANEL_COLOUR*/                    0x3a3a3a,
                /*ROLL_BUTTON_COLOUR*/              0xe4ff00,
                /*Board.BOARD_COLOUR*/              0x50555b,
                /*Board.BAR_COLOUR*/                0x545454,
                /*Spike.BLACK_SPIKE_COLOUR*/        0x030504,
                /*Spike.WHITE_SPIKE_COLOUR*/        0xe4ff00,
                /*Piece.WHITE_PIECE_COLOUR*/        0xb1995d,
                /*Piece.BLACK_PIECE_COLOUR*/        0x404443,
                /*Piece.WHITE_PIECE_INNER_COLOUR*/  0xffffff,
                /*Piece.BLACK_PIECE_INNER_COLOUR*/  0xffffff,
                /*Die.DIE_COLOUR*/                  0x000000,
                /*Die.DOT_COLOUR*/                  0xe4ff00
                };

 /////////////////////////////////////////////////////////////////////


///ROBOT STUFF

public static void tellRobot(boolean b,String s)
{
        if (s!=null)
        {
            robotExplain(s);
        }

    robotmove=b;
}

////WRAPPING ROUTINE
/*Vector textLinesForWrappingTMP;
    int lastColour;
    boolean allowScrollingDOWN,allowScrollingUP;
    String SPECIAL_END_SYMBOL="�����";// this signifiys to scroll bar the end is reached whislt being invisible to our customfont
    int scrollBarPos;
    int incrementSizeForScrollBarIndicator;
    public static final int WRAP_WIDTH_HACK_VAL=20; //15  //ensures that text doesnt go off edge
    // breaks down wrapMe into a vector and prints each line after each other making sure that the text wraps
    // properly.
    public int drawMeWrapped(Graphics g,int y, String wrapMe, CustomFont font,String newLineChar,boolean backdrop,boolean scrollbar,boolean outline,boolean justifyleft,int width)
    {
        //_("drawMeWrapped :: "+wrapMe);
        // TEXTS GET WRAPPED HERE.
        //////these texts need to be wrapped as they could be long
        textLinesForWrappingTMP=new Vector();                  //hack
        textLinesForWrappingTMP = separateText(wrapMe,(width)-WRAP_WIDTH_HACK_VAL,newLineChar,font);
        int stringHeight = y+font.getHeight();//y+paraYoffset;
        int yValueForOutline=y;//+paraYoffset;

        //_("paraYoffset:"+paraYoffset);
        int linesDrawn=0;

        int Xtmp=0;
        if (backdrop)//draw box behind text
        {
            g.fillRect(0,yValueForOutline-3,WIDTH,(font.getHeight()*textLinesForWrappingTMP.size())+2);
        }

        allowScrollingUP=true;




        for (int i = 0; i < textLinesForWrappingTMP.size(); i++) {
            if (stringHeight >= 20 && stringHeight < HEIGHT-30) // -30 here since we have top and bottom border, and want the very last line to show (was clipped)
            {
                //check if user is allowed to scroll up.
                if (i==0)
                {
                    // we are displaying the first line of the text, this indicates that we dont need to let user scroll up
                    allowScrollingUP=false;
                }

                printme=(String)textLinesForWrappingTMP.elementAt(i);
                if (justifyleft)
                {
                    Xtmp=10;//(WIDTH/2)-(font.stringWidth(printme)/2);
                }
                else
                {
                    Xtmp=(WIDTH/2)-(font.stringWidth(printme)/2);
                }

                //MAKE A PARAGRAPH IF DTECT <P> IN THE TEXT
                if (printme.indexOf("<P>")!=-1)
                {
                    //stringHeight+=10;_("<P>");
                    String bitbeforePara=printme.substring(0,printme.indexOf("<P>"));
                    String bitafterPara=printme.substring(printme.indexOf("<P>")+3,printme.length());
                    font.drawString( g, bitbeforePara,Xtmp,stringHeight, 0 );
                    stringHeight+=font.getHeight()+5;
                    printme=bitafterPara;
                }
                //shameless duplicating of code for lower case <p>
                if (printme.indexOf("<p>")!=-1)
                {
                    //stringHeight+=10;_("<P>");
                    String bitbeforePara=printme.substring(0,printme.indexOf("<p>"));
                    String bitafterPara=printme.substring(printme.indexOf("<p>")+3,printme.length());
                    font.drawString( g, bitbeforePara,Xtmp,stringHeight, 0 );
                    stringHeight+=font.getHeight()+5;
                    printme=bitafterPara;
                }

                font.drawString( g, printme,Xtmp,stringHeight, 0 );

                if (printme.indexOf("\n")!=-1)
                {
                    _("new line detected");
                }

                ///////_(linesDrawn+"/ stringHeight:"+stringHeight+" print line -->"+printme);

                linesDrawn++; // debug, to check we arent drawing lines off screen

                //check if the end of the text is reached and control users ability to scroll with bools.
                //so we dont let them keep scrolling
                if (printme.indexOf("THE END")!=-1 || printme.indexOf(SPECIAL_END_SYMBOL)!=-1)
                {
                    allowScrollingDOWN=false;
                }
                else
                {
                    allowScrollingDOWN=true;
                }
            }
            stringHeight+=(font.getHeight()-5);
        }

         if (textLinesForWrappingTMP.size()>0)
            incrementSizeForScrollBarIndicator=HEIGHT/(textLinesForWrappingTMP.size());//

        if (outline)// draw black outline
        {
            //lastColour=g.getColor(); // preserve the current colour set in graphics
            //g.setColor(0,0,0);
            //g.drawRect(-1,yValueForOutline-4,WIDTH+1,(font.getHeight()*textLinesForWrappingTMP.size())+2);
            //g.setColor(lastColour);
        }

        y=stringHeight;

        //draw scrollbar (
        //this is now done outside this methdo since we want it to paint over the header/footers
       

        return y;
    }*/

 Vector textLinesForWrappingTMP;
    int lastColour;
    boolean allowScrollingDOWN,allowScrollingUP;
    String SPECIAL_END_SYMBOL="::";// this signifiys to scroll bar the end is reached whislt being invisible to our customfont
    int scrollBarPos;
    int incrementSizeForScrollBarIndicator;
    public static  int WRAP_WIDTH_HACK_VAL=0; //15  //ensures that text doesnt go off edge
    // breaks down wrapMe into a vector and prints each line after each other making sure that the text wraps
    // properly.
    public int drawMeWrapped(Graphics g,int x, int y, String wrapMe, CustomFont font,String newLineChar,boolean backdrop,boolean scrollbar,boolean outline,boolean justifyleft, int width,boolean justifyRight)
    {
        if (wrapMe==null)
        {
            /*REMOVED4RELEASE*/_("drawMeWrapped received a null string");
        }
       // /*REMOVED4RELEASE*/_("drawMeWrapped :: "+wrapMe+" newLineChar:"+newLineChar);
        // TEXTS GET WRAPPED HERE.



			//////these texts need to be wrapped as they could be long
			textLinesForWrappingTMP=new Vector();                  //hack
			textLinesForWrappingTMP = separateTextNEW(wrapMe,/*getWidth()*/width-WRAP_WIDTH_HACK_VAL,getHeight(),newLineChar,font);

        int stringHeight = y;//+paraYoffset;
        int yValueForOutline=y;//+paraYoffset;

        ///*REMOVED4RELEASE*/_("paraYoffset:"+paraYoffset);
        int linesDrawn=0;

        int Xtmp=x;
        if (backdrop)//draw box behind text
        {
           // g.setColor(Constants.VODAFONE_RED);
           // g.fillRect(width/2,yValueForOutline-3,width,(font.getHeight()*(textLinesForWrappingTMP.size()+1))-7);
           // g.setColor(Constants.WHITE_COLOUR);
           // g.drawRect(width/2,yValueForOutline-3,width,(font.getHeight()*(textLinesForWrappingTMP.size()+1))-7);
        }

       // allowScrollingUP=true;




        for (int i = 0; i < textLinesForWrappingTMP.size(); i++) {
            if (stringHeight >= 0)//(header.getHeight()*2)-5 &&*/ stringHeight < HEIGHT) // -30 here since we have top and bottom border, and want the very last line to show (was clipped)
            {
                //check if user is allowed to scroll up.
                /*if (stringHeight>header.getHeight()*2)//i==0)
                {
                    // we are displaying the first line of the text, this indicates that we dont need to let user scroll up
                    allowScrollingUP=false;
                }*/

                printme=(String)textLinesForWrappingTMP.elementAt(i);
                if (justifyleft)
                {
                    Xtmp=x;//0;//(WIDTH/2)-(font.stringWidth(printme)/2);
                }
                else if (justifyRight)
                {
                   // printme=printme.trim();
                    //Xtmp=getWidth()-(Specifics.x_start_point_for_special_background+4+font.stringWidth(printme));//getWidth()-(x+font.stringWidth(printme))+x_start_point_for_special_background;//(x+width)-(x_start_point_for_special_background+(font.stringWidth(printme)));
                }
                else
                {
                    Xtmp=(WIDTH/2)-(font.stringWidth(printme)/2);
                }

              //  if (printme.indexOf(Constants.NEW_LINE)!=-1)
              //  {
              //      /*REMOVED4RELEASE*/_("new line detected");
              //  }
                //this is a bit of a hack but a legacy form the custom font days
                 //check if the end of the text is reached and control users ability to scroll with bools.
                //so we dont let them keep scrolling
                if (printme.indexOf(SPECIAL_END_SYMBOL)!=-1)
                {
                    allowScrollingDOWN=false;
                    /*REMOVED4RELEASE*/_("DONT ALLOW ASCROLL SINCE SPECIAL END SYMBOL DETECTED allowScrollingDOWN:"+allowScrollingDOWN);
                    //ok now remove the special end sybol so it doesnt print
                    printme=printme.substring(0,printme.indexOf(SPECIAL_END_SYMBOL));
                }
                else
                {
                    allowScrollingDOWN=true;
                }
                if (backdrop)
                {
                    //g.setColor(Constants.WHITE_COLOUR);
                }
                else
                {
                    //g.setColor(WHITE_COLOUR);
                }
                fontblack.drawString( g,printme,Xtmp,stringHeight,0);



             //   System.out.println(linesDrawn+"/ stringHeight:"+stringHeight+" print line -->"+printme);

                linesDrawn++; // debug, to check we arent drawing lines off screen


            }
            stringHeight+=(font.getHeight());//-5);
        }

         if (textLinesForWrappingTMP.size()>0)
         {
             ///*REMOVED4RELEASE*/_("charsOnScreenY:"+charsOnScreenY);
            //incrementSizeForScrollBarIndicator=SCROLLBAR_HEIGHT/( textLinesForWrappingTMP.size()-charsOnScreenY );//
         }
        if (outline)// draw black outline
        {
            //lastColour=g.getColor(); // preserve the current colour set in graphics
            //g.setColor(Constants.BLACK_COLOUR);
            //g.drawRect(-1,yValueForOutline-4,WIDTH+1,(font.getHeight()*textLinesForWrappingTMP.size())+2);
            //g.setColor(lastColour);
        }

        y=stringHeight;

        //draw scrollbar (
        //this is now done outside this methdo since we want it to paint over the header/footers
//        /*if (scrollbar)
//        {
//            //draw static bit
//            g.setColor(255,255,255);
//            g.fillRect(WIDTH-4,0,3,HEIGHT);
//            g.setColor(255,0,0);
//
//            int yPosTmp = - (incrementSizeForScrollBarIndicator * scrollBarPos);
//            g.fillRect(WIDTH-4,yPosTmp,3,incrementSizeForScrollBarIndicator);
//            ///*REMOVED4RELEASE*/_("yPosTmp:"+yPosTmp+" scrollBarPos:"+scrollBarPos+" incrementSizeForScrollBarIndicator:"+incrementSizeForScrollBarIndicator);
//
//
//        }*/

        return y;
    }

  public static Vector separateTextNEW(String string, int width, int height, String newLineChar, CustomFont font)
    {
       // _("separateTextNEW:"+string);

	//int linesThatCanFitOnScreen = (height/font.getHeight())+1;

        Vector lines = new Vector();
        String theText=string;
        String aline="";
        int startpoint=0;

        StringTokenizer st = new StringTokenizer(theText," ");
        String s=null;
		boolean fitsVertically=true;
		int verticalSpaceUsed=0;
        while (st.hasMoreElements() && fitsVertically)
        {
			if (verticalSpaceUsed>height)
			{
				//fitsVertically=false;
				//System.out.println("verticalSpaceUsed:"+verticalSpaceUsed);
			}
             s = st.nextToken();//if its not null s failed to get used last time
             s=s.trim();
            // T._(".word:"+s);
             if (s.equals("[p]") || s.equals("[br]") || s.equals("[br2]") || s.equals("[br][br]"))//<p>"))
             {
                 // [p] [br] [br][br] ALL WORK LIKE HTMLS <P>
                 // [br2] works like HTMLS <BR>

                 //_("NEW LINE >>>>");
                 lines.addElement(aline);
                 if (!s.equals("[br2]"))//dont add a empty line if its a br2
                 {
                    lines.addElement(" ");
					verticalSpaceUsed+=font.getHeight()+2;
                 }
                 aline="";
                 s=null;
             }
             else
             {
                     //_("word:"+s);
                    if (font.stringWidth(aline+" "+s)<width)
                    {
                        aline+=s+" ";
                        s=null;
                    }
                    else
                    {
                        //_("aNEW LINE:"+aline);
                        lines.addElement(aline);
						verticalSpaceUsed+=font.getHeight()+2;
                        aline="";
                    }
                    //if (s!=null)
                    {
                        if (!st.hasMoreElements() )
                        {
                            //if its finished just add last bit now to new line
                            if (s!=null && (!s.equals("[p]") ||  s.equals("[br2]") || s.equals("[br]") || s.equals("[br][br]")))
                            {
                                lines.addElement(aline+""+s);
								verticalSpaceUsed+=font.getHeight();
                                //_("bNEW LINE:"+aline);
                            }
                            else
                            {
                                lines.addElement(aline.trim());
								verticalSpaceUsed+=font.getHeight()+2;
                                 //_("cNEW LINE:"+aline);
                            }
                        }
                        else
                        {
                            if (s!=null)
                            {
                                if (s.equals("[p]")|| s.equals("[br]") ||  s.equals("[br2]") || s.equals("[br][br]"))
                                {
                                    lines.addElement(aline);
									verticalSpaceUsed+=font.getHeight();
                                    if (!s.equals("[br2]"))//dont add a empty line if its a br2
                                     {
                                        lines.addElement(" ");
										verticalSpaceUsed+=font.getHeight()+2;
                                     }
                                     aline="";

                                     s=null;

                                    // _(">>> new line");
                                }
                                else
                                {
                                    aline+=""+s+" ";
                                }
                            }
                        }
                    }
             }


        }
		//System.out.println("LINES-->"+lines);
//System.out.println("..lines:"+lines.capacity());
        return lines;
    }

}
