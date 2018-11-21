/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radiantsilverlabs.com.games.lowlevel;
import android.graphics.Bitmap;
import radiantsilverlabs.com.games.R;
import radiantsilverlabs.com.games.androidspecifics.*;
import android.util.Log;


import java.util.*;
//#ifdef JAVASE
//import java.awt.*;
//#endif
//import java.awt.event.*;
//import javax.swing.*;
//import javax.swing.event.*;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.image.*;

//import javax.imageio.*;
//import javax.swing.*;
/**
 *
 * @author Gaz
 */
public class HAL {

    public static String IP=null;
    public HAL()
    {
        //System.out.println("HAL object made.");
        //if (IP==null)
        //    IP= grabIP();
    }

    public static boolean CANVAS_LOGGING=false;//WARNING, adds all sys outs to vector to print (debug)
    public static final int BLACK=0;
    public static final int WHITE=1;
    Color colour;
    public void setColor(Graphics g, Color colour_)
    {

      //  colour=colour_;
      //  g.setColor(colour_);
    }

     public void setColor(Graphics g, int c)
     {
         g.setColor(c);
         //Color cl = new Color(c);
         //g.setColor(cl);
     }
    Color transparent;
    public void setColor(Graphics g, int red, int green, int blue, int alpha)
    {

        //if (transparent==null)
        //{
        //    transparent=new Color(red,green,blue,alpha);
        ///}

        //g.setColor(transparent);
    }
    public void resetTransparenctColour()
    {
        transparent=null;
    }

    public void setColor(Graphics g, Color c, int alpha)
    {
       // if (transparent==null)
       // {
        //     transparent=new Color(c.getRed(),c.getGreen(),c.getBlue(),alpha);
       // }

        //g.setColor(transparent);
    }

    public int getColor()
    {
        return 0xFFFFFF;
    }

    public void drawString(Graphics g, int x, int y,String s)
    {
        g.drawString(s,x,y);
    }

    public void drawRect(Graphics g, int x, int y, int WIDTH, int HEIGHT)
    {
        g.drawRect(x,y, WIDTH, HEIGHT);
    }

    public void fillRect(Graphics g, int x, int y, int WIDTH, int HEIGHT)
    {
        g.fillRect(x,y, WIDTH, HEIGHT);
    }

    public void drawRoundRect(Graphics g, int x, int y, int WIDTH, int HEIGHT)
    {
       g.drawRoundRect(x,y, WIDTH, HEIGHT,5,5); //      ..FIX ME LATER
       // g.drawRect(x,y, WIDTH, HEIGHT);

    }

    public void fillRoundRect(Graphics g, int x, int y, int WIDTH, int HEIGHT,boolean alpha)
    {
        g.fillRoundRect(x,y, WIDTH, HEIGHT,5,5,alpha);
    }

    public void drawTriangle(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3)
    {
        Polygon poly = new Polygon();
        poly.addPoint(x1, y1);
        poly.addPoint(x2, y2);
        poly.addPoint(x3, y3);
        g.drawPolygon(poly);
    }
    public void fillTriangle(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3)
    {
        Polygon poly = new Polygon();
        poly.addPoint(x1, y1);
        poly.addPoint(x2, y2);
        poly.addPoint(x3, y3);
        g.fillPolygon(poly);
    }

    public void drawCircle(Graphics g, int x, int y,int width, int height)
    {
         g.drawArc(x, y,width, height, 1, 360);
    }
    public void fillCircle(Graphics g, int x, int y,int width, int height)
    {
         g.fillArc(x, y,width, height, 1, 360);
    }

    Bitmap tempImage;
    public Bitmap loadImage(String path, int id)//String path)
    {
        try {
            _("LOADIMAGE: Attempting to load: "+id);
            tempImage = ActivityGammon.me.createImage(path,id);// Image.createImage("/back1.png");//Icon("airblue.gif").getImage();//getImage(getDocumentBase(), getParameter("logo"));

            // tempImage = ( new javax.swing.ImageIcon(getClass().getResource(path)).getImage() );
        } catch (Exception e) {
            _E("error loading image ("+id+") "+e.getMessage());
        }
        return tempImage;
    }

    public void bg(Graphics g, int c, int WIDTH, int HEIGHT)
    {
        setColor(g,c);
        fillRect(g,0,0,WIDTH,HEIGHT);
    }

    public void bg(Graphics g, Color c, int WIDTH, int HEIGHT)
    {
        setColor(g,c);
        fillRect(g,0,0,WIDTH,HEIGHT);
    }

    public void drawImage(Graphics g, Bitmap b, int x, int y)//, ImageObserver observer)
    {
       // _("DRAWIMAGE..."+g+"      "+b);
        g.drawImage(b, x - (b.getWidth() / 2), y - (b.getHeight() / 2));
    }













    public static int LINES_THAT_FIT_VERTICALLY=30;//allow to scale. todo
    public static Vector systemOuts=new Vector(0);
    public static void _(String s)
    {
        if (CANVAS_LOGGING)
        {
            systemOuts.add(s);
            if (systemOuts.capacity()>LINES_THAT_FIT_VERTICALLY)
            {
                systemOuts.remove(0);
            }
        }
       // System.out.println(s);
        Log.d("MyApp",s);
    }

    public static final String ERROR_STRING="****ERROR**** ";
    public static void _E(String s)
    {
        if (CANVAS_LOGGING)
        {
            systemOuts.add(ERROR_STRING+s);
        }
      //  CustomCanvas.sfxError.playSound();
        System.out.println(ERROR_STRING+s);
    }

    private static final Random randomizer = new Random();
    // get random val between min and max
    public static final int getRand(int min, int max) {
        int r = Math.abs(randomizer.nextInt());
        return (r % ((max - min + 1))) + min;
    }

    public static void SLEEP(long snoozeFor)
    {
        try
        {
            Thread.sleep(snoozeFor);
        }
        catch(Exception e)
        {
            _E("Insomnia!");
        }
    }


  
  

}

