package radiantsilverlabs.com.games.androidspecifics;

import android.graphics.*;
import android.graphics.drawable.shapes.Shape;
import android.graphics.Paint.*;
                           import android.graphics.Color;
import radiantsilverlabs.com.games.lowlevel.HAL;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 04/01/12
 * Time: 22:42
 * To change this template use File | Settings | File Templates.
 */
public class Graphics {




    public void setColor(Color c)
    {

    }


   /* public void getClip()
    {

    }*/



    private final Path tmppath = new Path();

    private void drawPolygon(Object graphics, int[] xPoints, int[] yPoints, int nPoints) {
        if (nPoints <= 1) {
            return;
        }
        this.tmppath.rewind();
        this.tmppath.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
           // HAL._("TRIANGLE POINT "+i+"");
            this.tmppath.lineTo(xPoints[i], yPoints[i]);
        }

        paint.setStyle(Style.STROKE);
        canvas.drawPath(this.tmppath, paint);
    }


    private void fillPolygon(Object graphics, int[] xPoints, int[] yPoints, int nPoints) {
        if (nPoints <= 1) {
            return;
        }
        this.tmppath.rewind();
        this.tmppath.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            this.tmppath.lineTo(xPoints[i], yPoints[i]);
        }

        paint.setStyle(Style.FILL);
        canvas.drawPath(this.tmppath, paint);
    }








    
    public void drawPolygon(Polygon p)
    {
       drawPolygon(this,p.polyX,p.polyY,3);        //ONLY FOR TRIANGLES RIGHT NOW - 3
    }

    public void fillPolygon(Polygon p)
    {
                fillPolygon(this,p.polyX,p.polyY,3);
    }

   public void drawImage(Bitmap b, int x, int y) {
       canvas.drawBitmap(b, x, y, paint);
   }
    public void drawImage(Image img, int x, int y, int anchor) {

    }
    public Shape getClip()
    {
             return null;
    }


    public void setColor(int r , int g, int b)
    {
        //System.out.println("setColor()");
        int col = Color.argb(255,r, g, b);
        paint.setColor(col);
    }


    public void drawRect(int x, int y, int w, int h, String desc)
    {
        //System.out.println("drawRect()");
        //paint.setColor(icolour);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(x, y, (x+w)-1, (y+h)-1, paint);
        if (desc==null)
            return;
        //canvas.drawRect(new Rect(x,y,w,h), paint);
        canvas.drawText(desc, x,y, paint);
    }

    /*public int getColor()
    {
        return icolour;
    }
      */
    /*Rect rect;
    public int getClipWidth()
    {
        rect = canvas.getClipBounds();
        return rect.width();
    }
    public int getClipHeight()
    {
        rect = canvas.getClipBounds();
        return rect.height();
    }
    public int getClipX()
    {
        rect = canvas.getClipBounds();
        return rect.left;
    }
    public int getClipY()
    {
        rect = canvas.getClipBounds();
        return rect.top;
    }   */

    public int getClipX() {
        return this.canvas.getClipBounds().left;
    }

    public int getClipY() {
        return this.canvas.getClipBounds().top;
    }

    public int getClipWidth() {
        return this.canvas.getClipBounds().width();
    }

    public int getClipHeight() {
        return this.canvas.getClipBounds().height();
    }
    public int getColor()
    {
        return this.paint.getColor() & 0x00FFFFFF;
    }

    public void setColor( int color )
    {
        this.paint.setColor( 0xFF000000 | color );
    }

    public void fillRect( int x, int y, int width, int height )
    {
        Style before = paint.getStyle();
        paint.setStyle(Paint.Style.FILL);
        // TODO : do something to the paint to make it fill!!
        this.canvas.drawRect( x, y, x+width, y+height, this.paint );
        paint.setStyle(before);

}

    public void fillRoundRect( int x, int y, int width, int height, int rx, int ry , boolean alpha) {

                                       if (alpha)
                                       {
                                           paint.setAlpha(127);
                                       }

        Style before = paint.getStyle();
        paint.setStyle(Paint.Style.FILL);
        this.canvas.drawRoundRect( new RectF( x, y, x+width, y+height ), rx, ry, this.paint );
        paint.setStyle(before);
        paint.setAlpha(255);
    }

    public static final int UP=0;
    public static final int DOWN=1;
    public static final int LEFT=2;
    public static final int RIGHT=3;
    public static final int TOP=4;
    public static final int BOTTOM=5;
    public static final int HCENTER=6;
    public static final int VCENTER=7;
    public static final int FIRE=8;

    public static final int KEY_NUM0=0;
    public static final int KEY_NUM1=1;
    public static final int KEY_NUM2=2;
    public static final int KEY_NUM3=3;
    public static final int KEY_NUM4=4;
    public static final int KEY_NUM5=5;
    public static final int KEY_NUM6=6;
    public static final int KEY_NUM7=7;
    public static final int KEY_NUM8=8;
    public static final int KEY_NUM9=9;
    android.graphics.Canvas canvas;
    Paint paint ;
    Color color;
    int icolour;
    public Graphics(Canvas c)
    {
        //System.out.println("Creating Graphics()...");
        if (c==null)
        {
            System.out.println("FATAL ERROR IN CREATION OF GRAPHICS canvas is null");
        }
        else
        {
            //System.out.println("Graphics() created successfully.");
        }
        //paint = new Paint();
        color = new Color();
        canvas=c;
        paint=new Paint();
        paint.setAlpha(255);
    }



    public Paint getPaint()
    {
        return paint;
    }
    public void drawLine(int x1, int y1, int x2, int y2) {
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void drawRect(int x, int y, int width, int height) {
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        Style before = paint.getStyle();
        paint.setStyle(Paint.Style.STROKE);
        this.canvas.drawRoundRect( new RectF( x, y, x+width, y+height ), 5, 5, this.paint );
        paint.setStyle(before);
    }



    public void fillArc( int x, int y, int width, int height, int startAngle, int arcAngle )
    {
        
        Style before = paint.getStyle();
        paint.setStyle(Paint.Style.FILL);
        // TODO : do something to the paint to make it fill!!
        this.canvas.drawArc( new RectF( x, y, x+width, y+height ), startAngle, arcAngle, true, this.paint );
        paint.setStyle(before);
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {

        this.canvas.drawArc( new RectF( x, y, x+width, y+height ), startAngle, arcAngle, true, this.paint );
       // System.out.println("no implementation for fillArc");
    }

    /*public void fillRect(int x, int y, int width, int height) {
      //  System.out.println("fillrect "+paint+"      "+canvas+"       ");
        paint.setStyle(Paint.Style.FILL);//HACKDD
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {

       //Sys("no implementation for fillRoundRect");
        paint.setStyle(Paint.Style.FILL);//HACKDD
        canvas.drawRoundRect(new RectF(x, y, x + width, y + height),x,y, paint); //PROB NOT RIGHT ALSO FIX NEWWWW
    }

    public void setColor(int RGB) {
        //paint.setColor(0xff000000 | RGB);
        // TODO AndroidFont.paint cannot be static
        paint.setColor(0xff000000 | RGB);
    }              */
    public void fillTriangle(int i, int j, int k, int l, int m, int n) {
        // TODO Auto-generated method stub
        //X._("filltriangle");
        //canvas.drawp


    }
    public void drawString(String s, int x, int y)
    {
        canvas.drawText(s, x,y, paint);
    }
    public void drawString(String s, int x, int y, int position)
    {
        canvas.drawText(s, x,y, paint);
    }
    public void setClip(int x, int y, int w, int h)
    {
        //System.out.println("setClip()");
    //    canvas.clipRect(x,y,x+w,y+h, Region.Op.REPLACE);

        canvas.clipRect(x,y,x+w,y+h, Region.Op.REPLACE);
    }


    public void drawImage(Bitmap b,int posx, int posy, int position)
    {
        //System.out.println("drawImage()");
        //if (canvas==null)
        //	System.out.println("canvas is null");
        //if (b==null)
        //	System.out.println("bitmap is null");
        //if (paint==null)
        //	paint = new Paint();
        canvas.drawBitmap(b, posx, posy, paint);
    }
    public int getTranslateX()
    {
        System.out.println("getTranslateX()");
        return 0;

    }
    public int getTranslateY()
    {
        System.out.println("getTranslateY()");
        return 0;

    }
    public void translate(int x, int y)
    {
        System.out.println("translate()");
    }



    public void drawRegion(Bitmap src, int x_src, int y_src, int width,
                           int height, int transform, int x_dst, int y_dst, int anchor) {
        // may throw NullPointerException, this is ok

        Rect srcRect = new Rect(x_src, y_src, x_src + width, y_src + height);
        Rect dstRect = new Rect(x_dst, y_dst, x_dst + width, y_dst + height);
        canvas.drawBitmap(src, srcRect, dstRect, paint);
        // return to saved
// TODO
//        g.setTransform(savedT);
    }

}
