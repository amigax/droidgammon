/*
*/

package radiantsilverlabs.com.games.lowlevel;
import radiantsilverlabs.com.games.gamelogic.Board;
import radiantsilverlabs.com.games.gamelogic.Player;
import java.io.*;
import java.net.*;


public class GameNetworkClient implements Runnable {


  public void run() {
      System.out.println("GameNetworkClient thread kicked off.");
    connectToSocket("");
  }


   private void connectToSocket(String ip)
 {
     ip="localhost";
     Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
_("connecting to socket");
        try {
            echoSocket = new Socket(ip, 4444);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: "+ip);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: "+ip);
            System.exit(1);
        }

	BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
	String userInput;
try{
    System.out.println("CONNECTED!");
	//while ((userInput = stdIn.readLine()) != null) {

     Bot.dead=true;
     CustomCanvas.state=CustomCanvas.GAME_IN_PROGRESS;
CustomCanvas.NETWORK_GAME_IN_PROCESS=true;
CustomCanvas.I_AM_CLIENT=true;
NetworkChatClient.KEEP_LOBBY_GOING=false;
    //// Main.frame.setTitle( Main.frame.getTitle() +" Online game in progress. (Connected to "+ip+" as client)" );

    boolean go=true;
    while(go)
    {
        //if (Board.whoseTurnIsIt==Player.WHITE)
        {


            if (SENDCLICK && CustomCanvas.pointerX!=0 && CustomCanvas.pointerY!=0)
            {
                // this tells us to send the click the client just made
                out.println("CLICK X:"+CustomCanvas.pointerX+" Y:"+CustomCanvas.pointerY);//userInput);
                System.out.println("CLIENT: receiving echo from server: " + in.readLine());
                SENDCLICK=false;
            }
            if (SENDCLICK_AND_DIEVALUE1 && CustomCanvas.pointerX!=0 && CustomCanvas.pointerY!=0)
            {
                // this tells us to send the click the client just made AND the result of the local die roll
                out.println("CLICK AND DIE1 @"+CustomCanvas.D1lastDieRoll_toSendOverNetwork+" X:"+CustomCanvas.pointerX+" Y:"+CustomCanvas.pointerY);//userInput);
                System.out.println("CLIENT: receiving echo from server: " + in.readLine());
                SENDCLICK_AND_DIEVALUE1=false;
            } else
            if (SENDCLICK_AND_DIEVALUE2 && CustomCanvas.pointerX!=0 && CustomCanvas.pointerY!=0)
            {
                // this tells us to send the click the client just made AND the result of the local die roll
                out.println("CLICK AND DIE2 @"+CustomCanvas.D2lastDieRoll_toSendOverNetwork+" X:"+CustomCanvas.pointerX+" Y:"+CustomCanvas.pointerY);//userInput);
                System.out.println("CLIENT: receiving echo from server: " + in.readLine());
                SENDCLICK_AND_DIEVALUE2=false;
            }
            else
            {
                 out.println("X:"+CustomCanvas.pointerX+" Y:"+CustomCanvas.pointerY);//userInput);
                 System.out.println("CLIENT: receiving echo from server: " + in.readLine());
            }
        }
            try {
                Thread.sleep(10);
            }catch(Exception e){}
	//}
    }
	out.close();
	in.close();
	stdIn.close();
	echoSocket.close();
        System.out.println("disconnected");
}
catch(Exception e)
{
    _("error in socket connection: "+e.getMessage());
}
 }

   public static boolean SENDCLICK_AND_DIEVALUE1=false;
   public static boolean SENDCLICK_AND_DIEVALUE2=false;
   public static boolean SENDCLICK=false;

   /// HAND GENERATED STUFF BELOW HERE:
    public static void _(String s) {System.out.println(s);}
    private void _E(Exception e) {System.out.println("ERROR::"+e.getMessage());}
}


  



