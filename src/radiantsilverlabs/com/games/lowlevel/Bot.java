/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radiantsilverlabs.com.games.lowlevel;
import radiantsilverlabs.com.games.gamelogic.Board;
import radiantsilverlabs.com.games.gamelogic.Player;
import radiantsilverlabs.com.games.androidspecifics.Robot;
import radiantsilverlabs.com.games.androidspecifics.InputEvent;


/**
 *
 * @author Gaz
 */
public class Bot extends Thread {

    Robot robot;
    public static int currentMouseX;
    public static int currentMouseY;
    CustomCanvas canvas;
    public Bot(CustomCanvas canvas_)
    {
        _("Bot born.");
        canvas=canvas_;
        try
        {
            robot = new Robot();
        }
        catch(Exception e)
        {
            HAL._E("ERROR making robot "+e.getMessage());
        }
        _("Mouse coords:"+currentMouseX+", "+currentMouseY);
        
    }

  public static boolean JUMP_DIRECT_TO_DEST=false;//jump directly to destination with no movement
    boolean click=false;
    // tells the bot to click when it next reaches its destination
    public void click()
    {
       
        click=true;
    }

    public static boolean NETWORK_OPPONENT=false;

    public static boolean dead=true;
    public static int x=0;
    public static int y=0;
    int addMeX;
    int addMeY;
    public static int destX;
    public static int destY;
    boolean READY2CLICK=true;
    public static boolean STOPCLICKING=false;
    long clickedTime;
    boolean ALLOW_RIGHT_CLICK_TO_JOG_BOT=true;

    boolean sameDestClickThreeTimes;//to fix the bug where sometimes it isnt allowed to place'
    int lastX, lastY;
    int sameDestCounter;
    public static  long TIME_DELAY_BETWEEN_CLICKS=1000;
    public static  long ROBOT_DELAY_AFTER_CLICKS=100;

    public static boolean FULL_AUTO_PLAY=false;//Plays everything via the bot so you can watch it all
long clickedTimeINDECISIVE;
public static boolean SPECIALCASE=false;//used when we want to force it not to retrn out of loop (for opening rolls we need this for bot to roll his own die since we dont know whoseTurnItIs yet)
public static boolean TAKES_OVER_MOUSE=false;
private void tick()
    {

        if (dead || Board.gameComplete)
            return;

        // this results in waiting until we know whose turn it is
        //so the bot doesnt take over until the roll off has been completed.
        // unless FULL_AUTO_PLAY is true
        /*if (SPECIALCASE)
        {
            _("special case let bot control.");
        }
        else
        {*/
        //if the following ocndition is true the bot will not process any commands
            //if (!FULL_AUTO_PLAY //this means the cpu plays the cpu and presses all buttons on all screens (testing/demo)
            //&& Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.WHITE //human is white-if im white and hes black, when its my turn he does nothing til its his turn again
            //&& !(CustomCanvas.numberOfFirstRollsDone==0) // this allows the cpu to do his own roll off, even tho he doesnt know whpseTurnItIs yet
            //)
            //{
                //_("i process nothing");
                //process no bot commands.
            //    return;
            //}
       // }
        if (FULL_AUTO_PLAY)
        {

        }
        else
        {
            if (Board.HUMAN_VS_COMPUTER && CustomCanvas.numberOfFirstRollsDone==1)
            {
            }else
            if ( (Board.HUMAN_VS_COMPUTER && Board.whoseTurnIsIt==Player.WHITE) ) // ie dont take whites go
            {
                return;
            }
        }

        addMeX=Main.getWindowXpos();
        addMeY=Main.getWindowYpos()+20;//WORK OUT WHY IT NEEDS 15

        if (x==destX && y==destY && x!=0 && y!=0 )
        {
            long differencex =  System.currentTimeMillis()-clickedTime;


            

           

            long difference =  System.currentTimeMillis()-clickedTime;
            if (difference>TIME_DELAY_BETWEEN_CLICKS &&!STOPCLICKING)
            {
                READY2CLICK=true;
                _("DEST REACHED. destX:"+destX+" destY:"+destY);
            }
           

            
            if (READY2CLICK)
            {
                // Bot.SPECIALCASE=false;//forget any special cases now
 
               // clickedTime = System.currentTimeMillis();

                if (lastX==destX && lastY==destY)
            {

                sameDestCounter++;
                if(sameDestCounter>3)
                {
                    _("SAME DEST FIXER~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    canvas.mouseClickedX(destX, destY, CustomCanvas.RIGHT_MOUSE_BUTTON);
                        clickedTime = System.currentTimeMillis();
                        READY2CLICK=false;
                }
            }else
            {
                sameDestCounter=0;
            }
                 lastX=destX;
            lastY=destY;


              
                {

                    if (TAKES_OVER_MOUSE)
                    {
                        robot.delay(100);
                        clickedTime = System.currentTimeMillis();
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        //click=false;
                         robot.delay(100);
                    }
                    else
                    {
                        try{
                            Thread.sleep(25);
                        }
                        catch(Exception e)
                        {
                            HAL._E("insomnia!");
                        }
                        //clickedTime = System.currentTimeMillis();
                        canvas.mouseClickedX(destX, destY, CustomCanvas.LEFT_MOUSE_BUTTON);
                        clickedTime = System.currentTimeMillis();
                        READY2CLICK=false;
                       /*try{
                        Thread.sleep(5);
                       }
                       catch(Exception e)
                       {
                           HAL._E("insomnia!");
                       }*/
                       //  click=false;
                    }
                }
                   
                  
               //READY2CLICK=false;
            }
        }
//System.out.print("tick");
        if (JUMP_DIRECT_TO_DEST)
        {
            x=destX;
            y=destY;
            if (TAKES_OVER_MOUSE)
            {
                robot.mouseMove(x+addMeX, y+addMeY);
            }
        }
        else
        {
            try{
                Thread.sleep(1);
            }
            catch(Exception e)
            {
                HAL._E("insomnia!");
            }
              // need a wait here so it doesnt move on until after the click.
                if (x<destX)
                {
                    x++;
                    //System.out.print("x++");
                    if (TAKES_OVER_MOUSE)
                     {
                        robot.mouseMove(x+addMeX, y+addMeY);
                    }
                }
                if (x>destX)
                {
                    x--;
                    //System.out.print("x--");
                    if (TAKES_OVER_MOUSE)
                 {
                    robot.mouseMove(x+addMeX, y+addMeY);
                    }
                }
                if (y<destY)
                {
                    y++;
                    //System.out.print("y++");
                    if (TAKES_OVER_MOUSE)
                     {
                        robot.mouseMove(x+addMeX, y+addMeY);
                    }
                }
                if (y>destY)
                {
                    y--;
                    //System.out.print("y--");
                    if (TAKES_OVER_MOUSE)
                    {
                        robot.mouseMove(x+addMeX, y+addMeY);
                    }
                }
        }
      



    }
int destinationCounter;
   /* public void forceClick()
    {
         robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                     click=false;
                    robot.delay(1000);

    }*/

    //saves the screenshot when it crashes for later diagnosis
    private void createScreenshot()
    {
    }

    boolean isRunning = true;

      public void run() {
        long cycleTime = System.currentTimeMillis();
        while(isRunning) {
            //updateGameState();

            tick();
           // cycleTime = cycleTime + Main.FRAME_DELAY;/////+ Main.FRAME_DELAY;
            long difference = cycleTime - System.currentTimeMillis();

            try {
             Thread.sleep(Math.max(0, difference));
            }
            catch(InterruptedException e) {
             e.printStackTrace();
            }
         }
      }

    private void _(String s)
    {
        HAL._("Bot{}:"+s);
    }

   
}
