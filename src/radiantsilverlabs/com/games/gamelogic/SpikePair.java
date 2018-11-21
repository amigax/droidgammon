/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package radiantsilverlabs.com.games.gamelogic;

/**
 *
 * @author Gaz
 */
public class SpikePair {

    public SpikePair(Spike pickMyPiece_,Spike dropPiecesOnMe_)
    {
        pickMyPiece=pickMyPiece_;
        dropPiecesOnMe=dropPiecesOnMe_;
    }
    //purely for the bot play
    public Spike pickMyPiece;
    public Spike dropPiecesOnMe;
}
