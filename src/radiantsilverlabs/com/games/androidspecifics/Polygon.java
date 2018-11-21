package radiantsilverlabs.com.games.androidspecifics;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 06/01/12
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */




import android.graphics.Canvas;

import android.graphics.Paint;



/**

 * Minimum Polygon class for Android.

 */
/**
 * Minimum Polygon class for Android.
 */
public class Polygon {

    // Polygon coodinates.
    public int[] polyY = new int[3];
    public int[] polyX = new int[3];

    // Number of sides in the polygon.
    private int polySides;

    /**
     * Default constructor.
     ///* @param px Polygon y coods.
     //* @param py Polygon x coods.
     //* @param ps Polygon sides count.
     */


    public Polygon() {

    }


    public Polygon( int[] px, int[] py, int ps ) {

        polyX = px;
        polyY = py;
        polySides = ps;
    }

    /**
     * Checks if the Polygon contains a point.
     * @see "http://alienryderflex.com/polygon/"
     * @param x Point horizontal pos.
     * @param y Point vertical pos.
     * @return Point is in Poly flag.
     */
    public boolean contains( int x, int y ) {

        boolean oddTransitions = false;
        for( int i = 0, j = polySides -1; i < polySides; j = i++ ) {
            if( ( polyY[ i ] < y && polyY[ j ] >= y ) || ( polyY[ j ] < y && polyY[ i ] >= y ) ) {
                if( polyX[ i ] + ( y - polyY[ i ] ) / ( polyY[ j ] - polyY[ i ] ) * ( polyX[ j ] - polyX[ i ] ) < x ) {
                    oddTransitions = !oddTransitions;
                }
            }
        }
        return oddTransitions;
    }

    /**
     * Checks if the Polygon contains a point.
     * @see "http://alienryderflex.com/polygon/"
    // * @param x Point horizontal pos.
    // * @param y Point vertical pos.
     * @return Point is in Poly flag.
     */
    public boolean contains( float fx, float fy ) {
        int x=Float.floatToIntBits(fx);
        int y=Float.floatToIntBits(fy);
        boolean oddTransitions = false;
        for( int i = 0, j = polySides -1; i < polySides; j = i++ ) {
            if( ( polyY[ i ] < y && polyY[ j ] >= y ) || ( polyY[ j ] < y && polyY[ i ] >= y ) ) {
                if( polyX[ i ] + ( y - polyY[ i ] ) / ( polyY[ j ] - polyY[ i ] ) * ( polyX[ j ] - polyX[ i ] ) < x ) {
                    oddTransitions = !oddTransitions;
                }
            }
        }
        return oddTransitions;
    }

        int index;
    public void addPoint(int x, int y) {

        //System.out.println("ADD POINT");
        polyX[index] = x;
        polyY[index] = y;
        index++;
        polySides = 2;
                    // System.out.println("POINT ADDED");
    }
}