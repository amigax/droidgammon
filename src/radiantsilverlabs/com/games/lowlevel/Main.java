/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radiantsilverlabs.com.games.lowlevel;
import android.graphics.Canvas;

//import java.awt.*;
//import javax.swing.*;
//import javax.swing.*;
//import java.awt.Toolkit;
//import java.awt.Dimension;
import java.util.*;
import java.util.zip.*;
//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.*;
import java.io.*;
//import javax.swing.event.*;
//import javax.swing.*;
import java.net.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.image.*;
import java.io.*;
//import javax.imageio.*;
//import javax.swing.*;
/**
 *
 * @author Gaz
 */
public class Main
{
     public static final boolean CENTRALISE_ON_SECOND_MONITOR=false;//for testing on my 2 monitr setup.
     public static final int FRAME_DELAY = 50;//50;//20; // 20ms. implies 50fps (1000/20) = 50
     public static CustomCanvas canvas;
    // public static JFrame frame;



     public void main()//String[] args)
     {

         _("Main called, Backgammon starting.");
    /*    frame = new JFrame();
        
        
        _("insetY:"+insetY+", insetX"+insetX);   */
        canvas = new CustomCanvas(); // create our canvas object that has custom rendering in it.
         /*
        frame.getContentPane().add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
        frame.setSize(810, 500);       */
        Thread gameThread = new Thread(new ThreadLoop());
        gameThread.setPriority(Thread.MIN_PRIORITY);
        gameThread.start(); // start Game processing.
     //   centralise(frame);

     //   frame.setVisible(true); // start AWT painting.
     //   frame.setTitle("Forumosa Backgammon "+CustomCanvas.VERSION);
        _("Backgammon visible.");


       // Insets insets = frame.getInsets();
        insetY=0;//insets.top;
        insetX=0;//insets.left;

        windowX=0;//frame.getLocationOnScreen().x;
        windowY=0;//frame.getLocationOnScreen().y;



     }

     static boolean isHidden;
     public static void hideMousePointer(boolean hide)
     {
         if (hide && !isHidden)
         {
            isHidden=true;
            int[] pixels = new int[16 * 16];
            //Image image = Toolkit.getDefaultToolkit().createImage( new MemoryImageSource(16, 16, pixels, 0, 16));
            //Cursor transparentCursor =  Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
            //frame.setCursor(transparentCursor);
         }
         else if (!hide && isHidden)
         {
             isHidden=false;
            // frame.setCursor(null);
         }
     }

     public static int insetY;
     public static int insetX;

     public static final int WINDOWY_MINUS=50;
     //centralise the frame window
 /*    private static void centralise(JFrame frame)
     {
        // get dimensions of the screen for centreing window later
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
	// Centre the window
        if (CENTRALISE_ON_SECOND_MONITOR)
        {
            frame.setLocation((screenDim.width),(screenDim.height/8)-WINDOWY_MINUS);
        }
        else
        {
            frame.setLocation((screenDim.width/2)-frame.getWidth()/2,(screenDim.height/2)-(frame.getHeight()/2)-WINDOWY_MINUS);
        }
     }  */
     static boolean windowMoved=true;
     static int windowX;
     static int windowY;
     public static int getWindowXpos()
     {
           
              return windowX;
          
     }
     public static int getWindowYpos()
     {
        
          return windowY;
     }


     private static void updateGameState() {
      // Game logic here
     }

     private static void _(String s)
     {
        HAL._("Main{}:"+s);
     }
}
