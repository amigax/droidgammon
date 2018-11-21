/*
*/

package radiantsilverlabs.com.games.lowlevel;
import radiantsilverlabs.com.games.gamelogic.Board;
import radiantsilverlabs.com.games.gamelogic.Player;
import java.io.*;
import java.net.*;


public class GameNetworkServer implements Runnable {

    CustomCanvas canvas;
    public GameNetworkServer(CustomCanvas canvas_)
    {
        canvas=canvas_;
    }
  private static int port=4444, maxConnections=0;
  // Listen for incoming connections and handle them
  public void run() {
    int i=0;

    try{
      ServerSocket listener = new ServerSocket(port);
      Socket server;

      _("waiting for connections..");
      while((i++ < maxConnections) || (maxConnections == 0)){
        doComms connection;
        server = listener.accept();
        _("connection accepted!");
        CustomCanvas.NETWORK_GAME_IN_PROCESS=true;
     /////   Main.frame.setTitle( Main.frame.getTitle() +" Online game in progress. (You are server, client connected to you on port "+port+")" );
        doComms conn_c= new doComms(server,canvas);
        Thread t = new Thread(conn_c);
        t.start();

         Bot.dead=true;
         CustomCanvas.state=CustomCanvas.GAME_IN_PROGRESS;
CustomCanvas.I_AM_SERVER=true;
NetworkChatClient.KEEP_LOBBY_GOING=false;
      }
    } catch (IOException ioe) {
      System.out.println("IOException on socket listen: " + ioe);
      ioe.printStackTrace();
      System.out.println("THIS INSTANCE WILL NOT ACCEPT CONNECTIONS NOW.");

      //HACKY FIX TO AVOID ISSUES DURING DEV
      ///port=4445;
      ///run();
    }
  }
   /// HAND GENERATED STUFF BELOW HERE:
    public static void _(String s) {System.out.println(s);}
    private void _E(Exception e) {System.out.println("ERROR::"+e.getMessage());}
}
class doComms implements Runnable {
    private Socket server;
    private String line,input;

    CustomCanvas customCanvas;
    doComms(Socket server,CustomCanvas customCanvas_) {
      this.server=server;
      customCanvas=customCanvas_;
    }
    public static boolean click, updateDieRollRemotely;
    public static String D1remoteDieRoll,D2remoteDieRoll;

    boolean write=true;
    int lastMouseX, lastMouseY;
    public void run () {
      input="";
      try {

        // Get input from the client
        DataInputStream in = new DataInputStream (server.getInputStream());
        PrintStream out = new PrintStream(server.getOutputStream());

        while((line = in.readLine()) != null && !line.equals(".")) {
          input=input + line;

          

          out.println("I got:" + line);
          System.out.println("SERVER: received this from client:" + line);
D1remoteDieRoll=null;
D2remoteDieRoll=null;
          if (line.contains("DIE1"))
          {
              //MEANS THEY GOT SENT: "CLICK AND DIE1 @"+CustomCanvas.D1lastDieRoll_toSendOverNetwork+" X:"+CustomCanvas.pointerX+" Y:"+CustomCanvas.pointerY);//userInput);
              D1remoteDieRoll=line.substring(line.indexOf("@")+1,line.indexOf("@")+2);
              System.out.println("D1remoteDieRoll received as: "+D1remoteDieRoll);
              line = line.substring(17,line.length());
              click=true;
              updateDieRollRemotely=true;
          } else
              if (line.contains("DIE2"))
          {
              //MEANS THEY GOT SENT: "CLICK AND DIE2 @"+CustomCanvas.D1lastDieRoll_toSendOverNetwork+" X:"+CustomCanvas.pointerX+" Y:"+CustomCanvas.pointerY);//userInput);
              D2remoteDieRoll=line.substring(line.indexOf("@")+1,line.indexOf("@")+2);
              System.out.println("D2remoteDieRoll received as: "+D2remoteDieRoll);
              line = line.substring(17,line.length());
              click=true;
              updateDieRollRemotely=true;
          }
          else
          if (line.contains("CLICK"))
          {
              //MEANS CLIENT HAS SENT "CLICK X:"+CustomCanvas.pointerX+" Y:"+CustomCanvas.pointerY)
              line = line.substring(6,line.length());
              click=true;
          }
line=line.trim();
System.out.println("parse coords from "+line);
          String X = line.substring(line.indexOf("X:")+2,line.indexOf( " ")).trim();
          //System.out.println("X is:"+X);
          String Y = line.substring(line.indexOf("Y:")+2,line.length()).trim();
          //System.out.println("Y is:"+Y);
          CustomCanvas.pointerX=Integer.parseInt(X);
          CustomCanvas.pointerY=Integer.parseInt(Y);

          if (click)
          {
            customCanvas.mouseClickedX(CustomCanvas.pointerX,CustomCanvas.pointerY,CustomCanvas.LEFT_MOUSE_BUTTON);
            click=false;
          }

          try {
                Thread.sleep(10);
            }catch(Exception e){}
        }

        // Now write to the client
    /*     boolean notSame=false;
        while (write)
        {
            /////////////////////////////////
            // this code is used in ClientEmulatorNetworking, GameNetworkServer
            // for testing, but if this is improved, improve there too
            notSame=true;
           if (PitchCanvas.mouseX==lastMouseX && PitchCanvas.mouseY==lastMouseY)
            {
                notSame=false;
            }

            if (notSame)
            {
                out.println(PitchCanvas.mouseX+","+PitchCanvas.mouseY);
                lastMouseX=PitchCanvas.mouseX;
                lastMouseY=PitchCanvas.mouseY;
            }
            else
            {
                out.println("FROM SERVER XXXXX");//keep alive connection - FIX THIS.
            }
            try {
                Thread.sleep(10);
            }catch(Exception e){}
            /////////////////////////////////
        }*/
       // System.out.println("Overall message is:" + input);
       // out.println("Overall message is:" + input);

        server.close();
      } catch (IOException ioe) {
        System.out.println("IOException on socket listen (already a server running on this ip and port???) : " + ioe);//HAPPENS WHEN YOU TRY TO RUN 2 SERVERS ON ONE MACHINE.
        /////ioe.printStackTrace();
      }
    }


  

}

