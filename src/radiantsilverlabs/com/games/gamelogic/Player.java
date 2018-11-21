/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radiantsilverlabs.com.games.gamelogic;
import radiantsilverlabs.com.games.lowlevel.*;
/**
 *
 * @author Gaz
 */
public class Player {

    public String name;
    public int pips;
    public int score;
    int colour;
    Player(int colour_, String name_)
    {
        colour=colour_;
        name=name_;
        _("Player made: "+name+" :: "+printColour());
    }
    public static final int WHITE=0;
    public static final int BLACK=1;
    
    public int getColour()
    {
        return colour;
    }

    public String printColour()
    {
        if (getColour()==WHITE)
            return "WHITE";
        else if(getColour()==BLACK)
            return "BLACK";
        else
        {
            
            HAL._E("PRINT COLOUR NEITHER B OR W");
            return "ERROR NO COLOUR";
        }
    }

    private void _(String s)
    {
        HAL._("Player{}:"+s);
    }
}
