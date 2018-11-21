/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package radiantsilverlabs.com.games.gamelogic;
import java.util.Vector;

import radiantsilverlabs.com.games.androidspecifics.ActivityGammon;
import radiantsilverlabs.com.games.lowlevel.*;
import radiantsilverlabs.com.games.androidspecifics.Color;
import radiantsilverlabs.com.games.androidspecifics.Graphics;
import java.util.Enumeration;
import java.util.NoSuchElementException;
/**
 *
 * @author Gaz
 */
public class Board
{
    //Colour constants
    public static int BOARD_COLOUR=0x000000;
    public static int BAR_COLOUR=CustomCanvas.BACKGROUND_COLOUR;//0x291609;
 //CHANGED OFR ANDROID TO INT   public static Color board_colour, bar_colour;
 public static int board_colour, bar_colour;
    // -- game variables
    public int matchPoints;
    public Vector spikes;
    Player whitePlayer, blackPlayer;
    public static Die die1, die2;
    HAL hal = new HAL();
    // -- pre-calc'd and temp vals
    String printme="";
    public static int whoseTurnIsIt=Player.WHITE;//so when it says roll to see who goes
    //first, white should roll their one die then black
    public static int BORDER;//the gap around the board
    public static int BAR;   //the bar in the middle of the board
    public static int PIECE_CONTAINER;//piece contrainers are them things to the sides that hold the pieces
    public static int WIDTH_MINUS_BORDERS;//

    public Board()
    {
        _("Board made");
        // make spikes, players, pieces etc
        makeAllGameVars();
        makeColourObjects(false);

        //how they decide who goes first in backgammon?
        //both players roll one die to decide who goes first
        //the highest rolling playyer goes first and starts
        //his game with the roll he made plus the one his
        //opponent made.

        // set up the board for a new game
        initialiseBoard(BOARD_NEW_GAME);

        
        ///DEBUG STUFF:
        ////////////////////////////////////////////////////////
        // TEST - to check if game behaves correctly when all pieces are in their home area:
        // put all white/black pieces in their container:
        //initialiseBoard(DEBUG_BOARD_WHITE_PIECES_IN_THEIR_HOME);
        //initialiseBoard(DEBUG_BOARD_BLACK_PIECES_IN_THEIR_HOME);
        // result? containers should paint green once initial rolls are done.
        ////////////////////////////////////////////////////////
        
    }

    public static void makeColourObjects(boolean forceRecreation)
    {
        //if (board_colour==null || forceRecreation)
        {
         //ANDROID CHANGE   board_colour= new Color(BOARD_COLOUR);
            board_colour= BOARD_COLOUR;
        }
       // if (bar_colour==null || forceRecreation)
        {
            bar_colour=BAR_COLOUR;//ANDROID CHANGE = new Color(BAR_COLOUR);
        }
    }

    private void makeAllGameVars()
    {
        _("making players");
        whitePlayer =new Player(Player.WHITE,"name");
        blackPlayer =new Player(Player.BLACK,"name");

        _("making spikes.");
        spikes = new Vector();
        for (int i=1; i<25; i++)
        {
            spikes.add(new Spike(i));
        }
        _("spikes done.");

        _("making dice");
        die1 = new Die();
        die2 = new Die();

    }


    public static boolean paintSpikeBlack=true;
    
    public void paint(Graphics g, int WIDTH, int HEIGHT)
    {
        methodNOW="";
        hal.setColor(g,Color.BLACK);

        //Calc some stuff
        BORDER=WIDTH/64;
        BAR=BORDER*2;
        PIECE_CONTAINER=0;//turn off piece containers -theyre drawn on panel now.
        WIDTH_MINUS_BORDERS=WIDTH-(BORDER*2);

        //draw the board:
        // outline:
        hal.setColor(g, CustomCanvas.BACKGROUND_COLOUR);//board_colour);
        
        hal.fillRect(g,BORDER,BORDER,WIDTH-BORDER*2,HEIGHT-BORDER*2);
        hal.setColor(g, Color.BLACK);
        hal.drawRect(g,BORDER,BORDER,WIDTH-BORDER*2,HEIGHT-BORDER*2);
        //draw piece containers
        ////hal.drawRect(g,BORDER,BORDER,PIECE_CONTAINER,HEIGHT-BORDER*2);
        ////hal.drawRect(g,WIDTH-BORDER-PIECE_CONTAINER,BORDER,PIECE_CONTAINER,HEIGHT-BORDER*2);
        //bar between 2 halves
        hal.setColor(g,bar_colour);
        hal.fillRect(g,(BORDER+WIDTH_MINUS_BORDERS/2)-BAR/2,BORDER,BAR,HEIGHT-BORDER*2);
        hal.setColor(g, Color.BLACK);
        hal.drawRect(g,(BORDER+WIDTH_MINUS_BORDERS/2)-BAR/2,BORDER,BAR,HEIGHT-BORDER*2);

        //spikes
        Enumeration e = spikes.elements();
        int i=0;
        while (e.hasMoreElements())
        {
           Spike spike = (Spike) e.nextElement();
           spike.paint(g,WIDTH,HEIGHT,""+i);
           i++;
        }

        
        
        //draw dice
        paintDice(g,WIDTH,HEIGHT);

        //draw the potential moves for whoevers go it is
        if (CustomCanvas.numberOfFirstRollsDone>1)//ie if game started.
        {
           
            if (gameComplete)
            {
                _("gameComplete");
            }
            else
            {
                if (!CustomCanvas.pieceOnMouse)////// IF THERE IS NO PIECE ON MOUSE //////////////////
                {
                    //SPECIAL CASE: PIECES ON THE BAR NEED TO BE MOVED FIRST/////
                    // if we have bits on the bar we need to deal with:
                   if (whoseTurnIsIt==Player.WHITE && CustomCanvas.theBarWHITE.size()>0)
                   {
                      drawPotentialMovesFromBar(g);
                   } else
                   if (whoseTurnIsIt==Player.BLACK && CustomCanvas.theBarBLACK.size()>0)
                   {
                      drawPotentialMovesFromBar(g);
                   }
                   else
                   {
                        // ORDINARY MOVE
                        //if no piece is stuck to mouse then just show up the potential
                        //moves as the mouse is hovered over each spike
                        drawPotentialMoves(g);
                   }
                }
                else /////////////////////IF THERE IS A PIECE ON MOUSE
                {
                    //simply keep these updatign as theyre still options
                   if (whoseTurnIsIt==Player.WHITE && CustomCanvas.theBarWHITE.size()>0)
                   {
                      drawPotentialMovesFromBar(g);
                   } else
                   if (whoseTurnIsIt==Player.BLACK && CustomCanvas.theBarBLACK.size()>0)
                   {
                      drawPotentialMovesFromBar(g);
                   }
                   else
                   {
                        //if there is a piece stuck on the mouse then pulsate the copied
                        //versions of the potential moves from before it was stuck on,
                        //this simply allows the player to move the piece around and still
                        //see the potential moves for the piece they are "holding" currently
                         keepPotentialSpikesPulsing();
                   }
                     
                }
            }
            
        }

       

        if (gameComplete)
            {
                _("gameComplete!");
            }
            else
            {
             ///////////////////////calculate potential moves goes here
            //////////REMOVED THIS AS AN EXPERMIENT 26TH JAN 127AM
                if (/*calculatePotentialNumberOfMoves && */(!die1HasBeenUsed || !die2HasBeenUsed))//EXPERMENTAL IE WONT BOTHER IF DICE ARE USED
                {
                    
                    calculatePotentialMoves(false);

                    //////////
                }
                /////////////////////////////////////////////////////
            }

        //CHECK IF ITS GAME OVER?
        if (CustomCanvas.whitePiecesSafelyInContainer.size()==15)
        {
            gameComplete=true;
            gameCompleteString="White has won the game!";
            CustomCanvas.showRollButton=false;
        }
        if (CustomCanvas.blackPiecesSafelyInContainer.size()==15)
        {
            gameComplete=true;
            gameCompleteString="Black has won the game!";
            CustomCanvas.showRollButton=false;
        }

        if (gameComplete || CustomCanvas.whiteResigned || CustomCanvas.blackResigned)
        {
            if (playGameOverSound)
            {
                ActivityGammon.gameover.start();;//CustomCanvas.sfxGameOver.playSound();
                _("PLAYING GAME OVER SOUND*************************");
                playGameOverSound=false;
            }
            CustomCanvas.tellPlayers(gameCompleteString);
        }


       
        
        
    }

    private void paintDice(Graphics g, int WIDTH,int HEIGHT)
    {
        if (CustomCanvas.showDice)
        {
            int diex=-1;
            int diey=-1;
            diex=(BORDER+((WIDTH/4)*3))+Die.DIE_WIDTH;
            diey=((BORDER+(HEIGHT/2))-Die.DIE_HEIGHT);
            if (!die1HasBeenUsed)
            {
                die1.paint(g, diex, diey);
            }
            else
            {

                //if this die cannot be seen, then set its value to zero so its no longer taken into account
                //in logic to look for potential moves etc.
                die1.disable();
            }

            diex+=Die.DIE_WIDTH+CustomCanvas.TINY_GAP;//gap between dice
            if (!die2HasBeenUsed)
            {
                die2.paint(g, diex, diey);
            }
            else
            {

                //if this die cannot be seen, then set its value to zero so its no longer taken into account
                //in logic to look for potential moves etc.
                die2.disable();
            }
        }
    }

    public static boolean pickingPieceUpFromBar;
    //when piece is on the bar this should display the options for it
    private void drawPotentialMovesFromBar(Graphics g)
    {
        // this was copied from drawPotentialMoves/////////
        allowPieceToStickToMouse=false;// make this false right away since its decided in this method, but could still be true from last time.
        getRidOfLittleDice(); // kind of intensive?
        detectIfPiecesAreHome();//sets the right bools if pieces are home

        boolean cantGetOfBarWithDie1=false;
        boolean cantGetOfBarWithDie2=false;

        if (CustomCanvas.showDice==false)
        {
           // _("dice not yet rolled so no potential moves.");
            return;
        }
        ////////

        if (!die1HasBeenUsed)
        {
            
            if (!canWeGetOffTheBarWithThisDie(die1,1))//this tells us if there are options (and stores in spikesAllowedToMoveToFromBar)and draws them graphically to player if so
            {
                ///dont set this yet as they might have optiosn when they get off the bar die1HasBeenUsed=true;
                //leaving it unset results in correct behaviour
                //however if they cant get off the bar at all then we set both dies to used since they #d be stuck on the bar otherwise
                //so we keep a flag of it til bottom
                cantGetOfBarWithDie1=true;
                _("NO OPTIONS FOR GETTING OFF BAR WITH DIE1");
                ////CustomCanvas.tellPlayers("No options with Die 1 ("+die1.getValue()+")");
            }
            else
            {
                //there are optiosn for getting off generation in here for the cpu player
                _("DIE1:");
                getOffTheBarOptions();
            }

        }
        else
        {
            cantGetOfBarWithDie1=true;
        }
        if (!die2HasBeenUsed)
        {
            
            if (!canWeGetOffTheBarWithThisDie(die2,2))//this tells us if there are optiosn (and stores in spikesAllowedToMoveToFromBar)and draws them graphically to player if so
            {
                //dont set this yet as they might have optiosn when they get off the bar  die2HasBeenUsed=true;
                //leaving it unset results in correct behaviour
                //however if they cant get off the bar at all then we set both dies to used since they #d be stuck on the bar otherwise
                //so we keep a flag of it til bottom
                cantGetOfBarWithDie2=true;
                _("NO OPTIONS FOR GETTING OFF BAR WITH DIE2");
               ///// CustomCanvas.tellPlayers("No options with Die 2 ("+die2.getValue()+")");
            }
            else
            {
                _("DIE2:");
                //there are optiosn for getting off generation in here for the cpu player
                getOffTheBarOptions();
            }

        }
        else
        {
            cantGetOfBarWithDie2=true;
        }


        if (cantGetOfBarWithDie1 && cantGetOfBarWithDie2)//die1HasBeenUsed && die2HasBeenUsed)
        {
            _("NO OPTIONS FROM BAR NEXT TURN!!!!!!!!!!!!!!1");
            die1HasBeenUsed=true;
            die2HasBeenUsed=true;
            CustomCanvas.turnOver();
        }
        ////////
    }

    private void getOffTheBarOptions()
    {
         //there are options for getting off the bar
        Enumeration e = spikesAllowedToMoveToFromBar.elements();
        _("OFF THE BAR OPTIONS:");
        while(e.hasMoreElements())
        {
            Spike spike = (Spike) e.nextElement();
            _("SPIKE:"+spike.getSpikeNumber()+" using ROLL OF "+spike.get_stored_die().getValue());
        }
        //grab the first spike for later:
        Spike destinationSpike = (Spike) spikesAllowedToMoveToFromBar.firstElement();

        //travel to piece on bar first
        Vector theBarPieces=null;
        if (whoseTurnIsIt==Player.WHITE)
        {
            _("FOR WHITE:");
            theBarPieces=CustomCanvas.theBarWHITE;
        }
        else
        {
            _("FOR BLACK:");
            theBarPieces=CustomCanvas.theBarBLACK;
        }
        Enumeration weepingDonkey = theBarPieces.elements();
        while(weepingDonkey.hasMoreElements())
        {
            _("White pieces on bar potential pick ups:");
            Piece piece = (Piece)weepingDonkey.nextElement();
            _("piece:"+piece.getColour());
        }
        if (CustomCanvas.pieceOnMouse)
        {
            _("Destination PLACE ON AVAIL SPIKE:"+destinationSpike.getSpikeNumber());
            if (destinationSpike.getType()==Spike.STALECTITE)
            {
               // _("STALECTITE dest");
                 setBotDestination(destinationSpike.x3-destinationSpike.TRIANGLE_WIDTH/2,destinationSpike.y2-destinationSpike.TRIANGLE_HEIGHT/2,"PLACE FROM BAR ONTO AVAIL SPIKES A");
                  //  Bot.destX=destinationSpike.x3-destinationSpike.TRIANGLE_WIDTH/2;
                  //  Bot.destY=destinationSpike.y2-destinationSpike.TRIANGLE_HEIGHT/2;
                   // CustomCanvas.desinationsFromWhichSource="Bot F";
            }else
            if (destinationSpike.getType()==Spike.STALECMITE)
            {
                   //  _("STALECMITE dest");
                setBotDestination(destinationSpike.x3-destinationSpike.TRIANGLE_WIDTH/2,destinationSpike.y2+destinationSpike.TRIANGLE_HEIGHT/2,"PLACE FROM BAR ONTO AVAIL SPIKES B");

                   // Bot.destX=destinationSpike.x3-destinationSpike.TRIANGLE_WIDTH/2;
                    //Bot.destY=destinationSpike.y2+destinationSpike.TRIANGLE_HEIGHT/2;
                    //CustomCanvas.desinationsFromWhichSource="Bot F";
            }
             
        }
        else
        {
            _("DESTINATION FOR BOT, PIECE ON BAR......");
            //pick first piece on bar
            Piece p = (Piece) theBarPieces.firstElement();
            setBotDestination(p.collision_x+Piece.PIECE_DIAMETER/2,p.collision_y+Piece.PIECE_DIAMETER/2,"DESTINATION FOR BOT, PIECE ON BAR......");

           // Bot.destX=p.collision_x+Piece.PIECE_DIAMETER/2;
           // Bot.destY=p.collision_y+Piece.PIECE_DIAMETER/2;
        }
        

    }

    // given the die this method will add spieks to spikesAllowedToMoveToFromBar
    //for current player so that the spikes available will flash and be ready to have a piece added to them
    private boolean canWeGetOffTheBarWithThisDie(Die die, int whichDie)
    {
        //_("canWeGetOffTheBarWithThisDie:"+whichDie);
        // work out the hoem spikes depending on whose go it is
        int SPIKE_A=0;
        int SPIKE_B=1;
        int SPIKE_C=2;
        int SPIKE_D=3;
        int SPIKE_E=4;
        int SPIKE_F=5;
       
        if (whoseTurnIsIt==Player.WHITE)
        {
            SPIKE_A=18;
            SPIKE_B=19;
            SPIKE_C=20;
            SPIKE_D=21;
            SPIKE_E=22;
            SPIKE_F=23;
            
        }
        int numberOfOptions=0;
        // can we get off the bar with this die??
        Enumeration e = spikes.elements();
        while (e.hasMoreElements())
        {
            Spike spike = (Spike) e.nextElement();
          //  _("checking spike:"+spike.getSpikeNumber());
            //if this spike is one of the home area spikes
            if (spike.getSpikeNumber()==SPIKE_A || spike.getSpikeNumber()==SPIKE_B || spike.getSpikeNumber()==SPIKE_C ||
                spike.getSpikeNumber()==SPIKE_D || spike.getSpikeNumber()==SPIKE_E || spike.getSpikeNumber()==SPIKE_F)
            {
                // work out if any of our spikes are viable:
                boolean anyViableSpike=false;
                if (spike.pieces.size()==0)
                {
                    anyViableSpike=true;
                    //its empty so its viable
                   // _("VIABLE DUE TO BEING EMPTY: "+spike.spikeName);
                } else
                if (doesThisSpikeBelongToPlayer(spike, whoseTurnIsIt))
                {
                    anyViableSpike=true;
                    //its already ours so its viable
                    //_("VIABLE DUE TO BEING OUR: "+spike.spikeName);
                } else if (spike.pieces.size()==1)
                {
                    anyViableSpike=true;
                    //it only has one enemy piece so its viable
                   // _("VIABLE DUE TO HAVING ONLY ONE ENEMY PIECE: "+spike.spikeName);
                }

                boolean yesItWillWork=false;
                //different logic here for each player.
                if (whoseTurnIsIt==Player.BLACK)/////////////BLACK LOGIC
                {
                    if (die.getValue()==((spike.getSpikeNumber()+1)))
                    {
                        yesItWillWork=true;
                    }
                }
                else///////////////////////////////////////WHITE logic
                {
                    if ( (25-die.getValue()) == (spike.getSpikeNumber()+1)) // last spike in black home
                    {
                        //_("AVAIL:"+spike.getSpikeNumber());
                        yesItWillWork=true;
                    }
                }
                

               // _("die.getValue():"+die.getValue()+" (spike.getSpikeNumber()+1):"+(spike.getSpikeNumber()+1)+" (spike.getSpikeNumber()+1)-minusme):"+((spike.getSpikeNumber()+1)-minusme));
                // see if a die roll can get us there.
                if (anyViableSpike && yesItWillWork)
                {
                    //_("Using DIE:"+whichDie+" it would be valid to get off bar and onto spike:"+(spike.getSpikeNumber()+1));
                    spike.flash(whichDie);

                    numberOfOptions++;

                    if (!spikesAllowedToMoveToFromBar.contains(spike))
                    {
                        spike.store_this_die(die);//bit of a hack here : just so we have a record of which die it will use
                        spikesAllowedToMoveToFromBar.add(spike);
                    }
                    pickingPieceUpFromBar=true;
                }


            }
        }
        if (numberOfOptions>0)
            return true;
        else
            return false;
    }

    public static Vector spikesAllowedToMoveToFromBar=new Vector(6);


    //called after game over when we return to the main meu to make sure all
    //vars are cleaned up properly
    public void RESET_ENTIRE_GAME_VARS()
    {
        playGameOverSound=true;
        gameComplete=false;
        gameCompleteString="ERROR - NO ONE HAS WON THE GAME YOU SHOULD NOT TO SEE THIS";
        HUMAN_VS_COMPUTER=false;
        spikesAllowedToMoveToFromBar=new Vector(6);
        pickingPieceUpFromBar=false;
        allowPieceToStickToMouse=false;
          allWhitePiecesAreHome=false;
    allBlackPiecesAreHome=false;
    calculatePotentialNumberOfMoves=true; // at the start of using a new dice we work out if the player has potential moves
    potentialNumberOfMoves=0;
    noMovesAvailable=false; // this gets set to true when no moves at all are available.
    pulsateWhiteContainer=false;
    pulsateBlackContainer=false;
    movePhase=0;
    die1HasBeenUsed=false;
    die2HasBeenUsed=false;

       highlightPieceContainerAsOption=false;

    checkAbleToGetIntoPieceContainerWHITE=false;
     checkAbleToGetIntoPieceContainerBLACK=false;
    whichDieGetsUsToPieceContainer=-1;

      listBotsOptions=false;
    botOptions="<<NONE YET>>";
     botDestinations= new Vector(6);
thereAreOptions=false;
    SPtheMoveToMake=null;//stores the move they will make

     ROBOT_DESTINATION_MESSAGE="";
     spikesWeCanMovePiecesToo=null;
  moveAPieceToMe=null;
 canWeMoveAPieceToThisSpike=null;
    }

    public static boolean playGameOverSound=true;
     public static boolean gameComplete=false;
   public static String gameCompleteString="ERROR - NO ONE HAS WON THE GAME YOU SHOULD NOT TO SEE THIS";

    public static boolean HUMAN_VS_COMPUTER=false;//human is white, computer is black

    public static final int DIE1=1;//these variables are simply for passing over to the spike when it flashes
    public static final int DIE2=2;//so it knows which die is carrying out its potential move to tell the player
    public static final int DIE1AND2=3;
    String debugstr="";
    //simply pulses the spikes while theyre not null
    private void keepPotentialSpikesPulsing()
    {
        boolean debugmessages=false;
        if (debugmessages)
        {
            _("keepPotentialSpikesPulsing");
        }
        debugstr="";
        if (copy_of_reachableFromDie1!=null)
        {
            pulsatePotentialSpike(copy_of_reachableFromDie1,DIE1);
            debugstr="die1";
        }
        else
        {
            //HAL._E("keepPotentialSpikesPulsing ... copy_of_reachableFromDie1 is null.");
        }
        //////
        if (copy_of_reachableFromDie2!=null)
        {
            pulsatePotentialSpike(copy_of_reachableFromDie2,DIE2);
            debugstr+=", die2";
        }
        else
        {
            /////HAL._E("copy_of_reachableFromDie2 is null.");
        }
        /////
        if (copy_of_reachableFromBothDice!=null)
        {
            pulsatePotentialSpike(copy_of_reachableFromBothDice,DIE1AND2);
            debugstr+=", bothdice";
        }
        else
        {
            /////HAL._E("copy_of_reachableFromBothDice is null.");
        }
        if (debugmessages)
        {
            _("debugstr:"+debugstr);
        }
    }

    // this is always the current x and y vals of the mouse pointer
    public static int mouseHoverX,mouseHoverY;

    // controls whether the piece should follow the mouse when clicked on by player
    // (based on if there are potential moves to be made)
    public static boolean allowPieceToStickToMouse=false;

    // we take copies of the "pulsating" spikes (ie those that are valid spikes that
    // the current player could move a piece to) - we take these copies so that
    // when the player picks up a piece (ie it becomes stuck to the mouse pointer)
    // and moves it around, we can still show the pulsating valid spikes that he
    // can drop this piece onto. Without these copies as soon as the pointer
    // stopped hovering over the current spike (like it would if player was placing
    // a piece) the pulsating indication of valid options would vanish.
    public static Spike copy_of_reachableFromDie1;
    public static Spike copy_of_reachableFromDie2;
    public static Spike copy_of_reachableFromBothDice;
    private void clearCopiedPulsatingSpikes()
    {
        _("clearCopiedPulsatingSpikes");
        copy_of_reachableFromDie1=null;
        copy_of_reachableFromDie2=null;
        copy_of_reachableFromBothDice=null;

        
    }

    //simply nullifies the string that tells the spike to show its little dice
    //this is shown to user when they have potential moves.
    private void getRidOfLittleDice()
    {
        //_("get rid of little dice.");
        //go thru all spikes and clear whichDie to null so it doesnt still show the dice that
        //would have carried out the potential move (shouldnt they be linked to these 3 spikes? not sure yet)
        Enumeration e = spikes.elements();
        while (e.hasMoreElements())
        {
            Spike s = (Spike)e.nextElement();
            s.whichDiei=-1;////whichDie=null;
        }
    }

    // this handles:
    // detecting if the pieces for each team are in their home area
    // will throw an error if all pieces in play (or on piece container) arent equals to 15 for both players
    private void detectIfPiecesAreHome()
    {
         //RESET THESE HERE?
        highlightPieceContainerAsOption=false;
             pulsateWhiteContainer=false;
             pulsateBlackContainer=false;


        //detect if all the pieces are in the "home" side of the board,
        //and if so make the piece container a different colour. (piece containers are painted in
        //green when this is true) - this also takes into accoutn when pieces are already safely in the piece container
        // TODO Optimise this so its only called once each time not constantly *******
        if (calculateAmountOfPiecesInHomeArea(Player.WHITE)+ CustomCanvas.whitePiecesSafelyInContainer.size()==15)
        {
            allWhitePiecesAreHome=true;
        }
        else
        {
            allWhitePiecesAreHome=false;
        }
        if ((calculateAmountOfPiecesInHomeArea(Player.BLACK)+ CustomCanvas.blackPiecesSafelyInContainer.size())==15)
        {
            allBlackPiecesAreHome=true;
        }
        else
        {
            allBlackPiecesAreHome=false;
        }
        //allWhitePiecesAreHome=piecesInHomeSide(Player.WHITE);
        //allBlackPiecesAreHome=piecesInHomeSide(Player.BLACK);
        //_("allWhitePiecesAreHome:"+allWhitePiecesAreHome);
        //_("allBlackPiecesAreHome:"+allBlackPiecesAreHome);


       // this checks if 15 pieces are in play, and otherwise throws an error, it takes pieces on board +
        //pieces in piece container and looks for 15 as the result, most of the time the pieces on board will equal 15 and piece container
             // size will naturally be zero until near the end of the game when pieces are being put away
        int piecesOnBoard=calculateAmountOfPiecesOnBoard(Player.WHITE);
        if ((piecesOnBoard + CustomCanvas.whitePiecesSafelyInContainer.size() + CustomCanvas.theBarWHITE.size())!=15)//
        {
            HAL._E("PIECES NOT EQUAL TO 15 FOR WHITE its "+piecesOnBoard);
        }
        piecesOnBoard=calculateAmountOfPiecesOnBoard(Player.BLACK);
        if ((piecesOnBoard + CustomCanvas.blackPiecesSafelyInContainer.size() + CustomCanvas.theBarBLACK.size())!=15)
        {
            HAL._E("PIECES NOT EQUAL TO 15 FOR BLACK its "+piecesOnBoard);
        }
        ////
    }

    //indicates when the pieces are all in their home section, and thus we indicate
    // to the player they can put them into their container now.
    public static boolean allWhitePiecesAreHome;
    public static boolean allBlackPiecesAreHome;
    public static boolean calculatePotentialNumberOfMoves=true; // at the start of using a new dice we work out if the player has potential moves
    static int potentialNumberOfMoves=0;
    static public boolean noMovesAvailable=false; // this gets set to true when no moves at all are available.

    // gives us meaningful commands for the robot mostly
    public static final int LOOKING_FOR_SPIKE_TO_PLACE_PIECE=1;
    public static final int LOOKING_FOR_PIECE_TO_PICK_UP=2;
    static int movePhase;

    public static String methodNOW="";
    public static boolean pulsateWhiteContainer, pulsateBlackContainer;
    /*
     * This method draws an indicator to show the player potential moves
     * for the piece they currentlby have the mouse hovered over
     * there can be up to 3 potential moves: die1, die2, die1+die2
     */
    private void drawPotentialMoves(Graphics g)
    {

        boolean debugmessages=false; // this one can be very handy for debugging, too verbose once you know it works
                                     // see the other messages below that might be stubbed out too
        if (debugmessages)
        {
            //_("drawPotentialMoves");
        }
        allowPieceToStickToMouse=false;// make this false right away since its decided in this method, but could still be true from last time.
        getRidOfLittleDice(); // kind of intensive?
        detectIfPiecesAreHome();//sets the right bools if pieces are home
                
        ///CALCULATE POTENTIAL MOVES WAS HERE---

        if (CustomCanvas.showDice==false)
        {
           // _("dice not yet rolled so no potential moves.");
            return;
        }

        //find out if the spike they have their mouse over is theirs
        //That is does it already contain one of their Pieces (to potentially move)
        Spike currentSpikeHoveringOver = doesThisSpikeBelongToPlayer();
       /* if (Bot.dead && currentSpikeHoveringOver==null)
        {
            _("No spike that contains players pieces is being hovered over.");
            return;
        }*/
        //yes this spike contains some of the players pieces, lets determin
        //the potential moves the player can make.

       
        //DIE ONE//////
        //check if die roll ONE results in a valid potential move
        //ie is future spike already theirs or empty, if so tell player
        //its an option graphically. (by making it pulse) otherwise do nothing.
        boolean die1AnOption = checkValidPotentialMove(currentSpikeHoveringOver, die1.getValue(), 1);//1 indicates this is die1 for piece container code

        //this boolean is used to keep track of whether we let a piece stick to mouse, so we need to keep a track on if die1
        //is an option (since if it is we want that piece to be able to be picked up) but we do further checks below
        //so this "stillAnOption" variable gets updated below and used at the bottom to update canWeStickPieceToMouse
        boolean die1StillAnOption=false;//set to false first so we know if its true its been updated below

        //if die1 would yield a potential valid spike to land on AND if the
        //player has not used die1 this turn yet.
        if (die1AnOption && !die1HasBeenUsed)
        {
            copy_of_reachableFromDie1=null;//Set to null here so we know if its valid by the end it true is

           int potentialSpikeIndex = -1;
           if (whoseTurnIsIt==Player.BLACK)
           {
               potentialSpikeIndex = currentSpikeHoveringOver.getSpikeNumber()+die1.getValue();
           }
           else
           {
                potentialSpikeIndex = currentSpikeHoveringOver.getSpikeNumber()-die1.getValue();
           }
            //only for the case when a piece can go into the piece container
            if (highlightPieceContainerAsOption)//<-- this can get set by checkValidPotentialMove when the situation is right
            {
                if (potentialSpikeIndex==FIRST_SPIKE-1 && whoseTurnIsIt==Player.WHITE)
                {
                    //_("yes "+potentialSpikeIndex+" is a valid option DIE1 TO GET ONTO PIECE WHITE CONTAINER");
                    pulsateWhiteContainer=true;
                    die1StillAnOption=true;
                     copy_of_reachableFromDie1=null;//EXPERIMENT-YES THIS WORKS // STOPS OLD SPIKES FLASHING IN PICE CONTAINER CIRCUMSTANCES
                }
                else
                if (potentialSpikeIndex==LAST_SPIKE+1 && whoseTurnIsIt==Player.BLACK)
                {
                    _("yes "+potentialSpikeIndex+" is a valid option DIE1 TO GET ONTO PIECE BLACK CONTAINER");
                    pulsateBlackContainer=true;
                    die1StillAnOption=true;
                     copy_of_reachableFromDie1=null;//EXPERIMENT-YES THIS WORKS // STOPS OLD SPIKES FLASHING IN PICE CONTAINER CIRCUMSTANCES
                }
                 else
                {
                   //EXPERIMENTAL JAN 21 1212PM - IF ITS THE CONTAINER THAT IS VALID THEN I GUESS THIS CANNOT BE. (SHOULD STOP IT GETTING REEMBERED)
                   /// copy_of_reachableFromDie1=null;
                }
                //this is not an error since it might be a normal spike
                /*else
                {
                    HAL._E("Error highlightPieceContainerAsOption is true but potentialSpikeIndex ("+potentialSpikeIndex+") is neither white or blacks piece container");
                }*/

            }
           ///NO NEED FOR ELSE HERE EXPERIMENTAL 21JAN 1018AM else // normal situation. ie a spike is the option
            {
                if (potentialSpikeIndex>=FIRST_SPIKE && potentialSpikeIndex<=LAST_SPIKE)
                {
                     Spike reachableFromDie1 = (Spike) spikes.elementAt(potentialSpikeIndex);
                     if (debugmessages)
                     {
                        _("yes "+potentialSpikeIndex+" is a valid option DIE1");
                     }
                     //graphically indicate the spike that is a valid move using die1s value:
                     pulsatePotentialSpike(reachableFromDie1,DIE1);

                     //copy this so we can keep it pulsating during placing of piece
                     //and not just when the point is hovered over this spike
                     copy_of_reachableFromDie1=reachableFromDie1;
                     if (debugmessages)
                     {
                        _("copy_of_reachableFromDie1 set up.");
                     }
                     die1StillAnOption=true;
                }
            }
// die1StillAnOption=true;
             
        }
        else//its not a potential move
        {
            //EXPERIMENTAL JAN 14TH 2010 [seems to work remove this line]
            //make this null if we know for certain its not a potential move or ot gets remembered
            copy_of_reachableFromDie1=null;
        }

        //do DIE2 in same way
        boolean die2AnOption = checkValidPotentialMove(currentSpikeHoveringOver, die2.getValue(), 2);//2 indicates this is die2 for piece container code

        //this boolean is used to keep track of whether we let a piece stick to mouse, so we need to keep a track on if die1
        //is an option (since if it is we want that piece to be able to be picked up) but we do further checks below
        //so this "stillAnOption" variable gets updated below and used at the bottom to update canWeStickPieceToMouse
        boolean die2StillAnOption=false;//set to false first so we know if its true its been updated below

         //if die2 would yield a potential valid spike to land on AND if the
        //player has not used die2 this turn yet.
        if (die2AnOption && !die2HasBeenUsed)
        {
            copy_of_reachableFromDie2=null;//Set to null here so we know if its valid by the end it true is

             ////////int potentialSpikeIndex = -1;//currentSpikeHoveringOver.getSpikeNumber()+die2.getValue();
             int potentialSpikeIndex = -1;
             if (whoseTurnIsIt==Player.BLACK)
             {
                potentialSpikeIndex = currentSpikeHoveringOver.getSpikeNumber()+die2.getValue();
             }
             else
             {
                potentialSpikeIndex = currentSpikeHoveringOver.getSpikeNumber()-die2.getValue();
             }
              //only for the case when a piece can go into the piece container
            if (highlightPieceContainerAsOption)//<-- this can get set by checkValidPotentialMove when the situation is right
            {
                if (potentialSpikeIndex==FIRST_SPIKE-1 && whoseTurnIsIt==Player.WHITE)
                {
                     _("yes "+potentialSpikeIndex+" is a valid option DIE2 TO GET ONTO PIECE WHITE CONTAINER");
                    pulsateWhiteContainer=true;
                    die2StillAnOption=true;
                    copy_of_reachableFromDie2=null;//EXPERIMENT- YES IT WORKS
                }
                else
                if (potentialSpikeIndex==LAST_SPIKE+1 && whoseTurnIsIt==Player.BLACK)
                {
                     _("yes "+potentialSpikeIndex+" is a valid option DIE2 TO GET ONTO PIECE BLACK CONTAINER");
                    pulsateBlackContainer=true;
                    die2StillAnOption=true;
                    copy_of_reachableFromDie2=null;//EXPERIMENT-YES IT WORKS
                }
                else
                {
                   //EXPERIMENTAL JAN 21 1212PM - IF ITS THE CONTAINER THAT IS VALID THEN I GUESS THIS CANNOT BE. (SHOULD STOP IT GETTING REEMBERED)
                    ///////copy_of_reachableFromDie2=null;
                }
                /*else
                {
                    HAL._E("Error B highlightPieceContainerAsOption is true but potentialSpikeIndex is neither white or blacks piece container");
                }*/

            }
            ///NO NEED FOR ELSE HERE EXPERIMENTAL 21JAN 1018AM else // normal situation. ie a spike is the option
            {
                if (potentialSpikeIndex>=FIRST_SPIKE && potentialSpikeIndex<=LAST_SPIKE)
                {
                     Spike reachableFromDie2 = (Spike) spikes.elementAt(potentialSpikeIndex);
                     if (debugmessages)
                     {
                        _("yes "+potentialSpikeIndex+" is a valid option DIE2");
                     }
                     //graphically indicate the spike that is a valid move using die1s value:
                     pulsatePotentialSpike(reachableFromDie2,DIE2);

                     //copy this so we can keep it pulsating during placing of piece
                     //and not just when the point is hovered over this spike
                     copy_of_reachableFromDie2=reachableFromDie2;
                     if (debugmessages)
                     {
                        _("copy_of_reachableFromDie2 set up.");
                     }
                     die2StillAnOption=true;
                }
            }
            // die2StillAnOption=true;
        }
        else//its not a potential move
        {
            //EXPERIMENTAL JAN 14TH 2010 [seems to work remove this line]
            //make this null if we know for certain its not a potential move or ot gets remembered
            copy_of_reachableFromDie2=null;
        }


        //and DIE1 + DIE2
        boolean bothDiceAnOption = checkValidPotentialMove(currentSpikeHoveringOver, die1.getValue()+die2.getValue(), 3);//3 indicates this is die1+die2 for piece container code

        //this boolean is used to keep track of whether we let a piece stick to mouse, so we need to keep a track on if die1
        //is an option (since if it is we want that piece to be able to be picked up) but we do further checks below
        //so this "stillAnOption" variable gets updated below and used at the bottom to update canWeStickPieceToMouse
        boolean bothDiceStillAnOption=false;//set to false first so we know if its true its been updated below


        //if die1+die2 would yield a potential valid spike to land on AND if the
        //player has not used die1 OR die2 this turn yet.
        //AND ***IMPORTANTLY IF die1 OR die2 are valid options, since the player needs to be able to take each
        //turn and not simply add them together (therefore making an invalid move by combining their rolls)
        //this is not how backgammon works.
        if (bothDiceAnOption && (!die1HasBeenUsed) && (!die2HasBeenUsed)
            && (die1AnOption || die2AnOption) //***
                )
        {
            copy_of_reachableFromBothDice=null;//Set to null here so we know if its valid by the end it true is

             //////////int potentialSpikeIndex = currentSpikeHoveringOver.getSpikeNumber()+(die1.getValue()+die2.getValue());
             int potentialSpikeIndex = -1;
             if (whoseTurnIsIt==Player.BLACK)
             {
                potentialSpikeIndex = currentSpikeHoveringOver.getSpikeNumber()+(die1.getValue()+die2.getValue());
             }
             else
             {
                potentialSpikeIndex = currentSpikeHoveringOver.getSpikeNumber()-(die1.getValue()+die2.getValue());
             }

              //only for the case when a piece can go into the piece container
            if (highlightPieceContainerAsOption)//<-- this can get set by checkValidPotentialMove when the situation is right
            {
                if (potentialSpikeIndex==FIRST_SPIKE-1 && whoseTurnIsIt==Player.WHITE)
                {
                    _("yes "+potentialSpikeIndex+" is a valid option DIE1+DIE2 TO GET ONTO PIECE WHITE CONTAINER");
                    pulsateWhiteContainer=true;
                    bothDiceStillAnOption=true;
                    copy_of_reachableFromBothDice=null;//EXPERIMENTAL yes this works
                }
                else
                if (potentialSpikeIndex==LAST_SPIKE+1 && whoseTurnIsIt==Player.BLACK)
                {
                    _("yes "+potentialSpikeIndex+" is a valid option DIE1+DIE2 TO GET ONTO PIECE BLACK CONTAINER");
                    pulsateBlackContainer=true;
                    bothDiceStillAnOption=true;
                    copy_of_reachableFromBothDice=null;//EXPERIMENTAL yes this works
                }
                else
                {
                   //EXPERIMENTAL JAN 21 1212PM - IF ITS THE CONTAINER THAT IS VALID THEN I GUESS THIS CANNOT BE. (SHOULD STOP IT GETTING REEMBERED)
                    ///copy_of_reachableFromBothDice=null;
                }
                /*else
                {
                    HAL._E("Error B highlightPieceContainerAsOption is true but potentialSpikeIndex is neither white or blacks piece container");
                }*/

            }
            ///NO NEED FOR ELSE HERE EXPERIMENTAL 21JAN 1018AM else // normal situation. ie a spike is the option
            {
                if (potentialSpikeIndex>=FIRST_SPIKE && potentialSpikeIndex<=LAST_SPIKE)
                {
                     Spike reachableFromBothDice = (Spike) spikes.elementAt(potentialSpikeIndex);
                     if (debugmessages)
                     {
                        _("yes "+potentialSpikeIndex+" is a valid option BOTH DICE");
                     }
                     //graphically indicate the spike that is a valid move using die1s value:
                     pulsatePotentialSpike(reachableFromBothDice,DIE1AND2);

                     //copy this so we can keep it pulsating during placing of piece
                     //and not just when the point is hovered over this spike
                     copy_of_reachableFromBothDice=reachableFromBothDice;
                     if (debugmessages)
                     {
                        _("copy_of_reachableFromBothDice set up.");
                     }
                     bothDiceStillAnOption=true;

                }
            }
             //bothDiceStillAnOption=true;
        }
        else//its not a potential move
        {
            //EXPERIMENTAL JAN 14TH 2010 [seems to work remove this line]
            //make this null if we know for certain its not a potential move or ot gets remembered
            copy_of_reachableFromBothDice=null;
        }

        //by this point we know whether we have our options (decided above for pulsating the correct
        //spikes, so we also know whether we should allow a piece to stick to the mouse, ie only if that
        //piece is relevant and can be moved etc 
        if (die1StillAnOption || die2StillAnOption || bothDiceStillAnOption)
        {
            //the spike they are currently hovering over has options, thus if
            //the player clicks on a piece on this spike it will stick to the mouse
            //point and move with it, until its either placed back or placed
            //somewhere new
            allowPieceToStickToMouse=true;
            if (debugmessages)
            {
                //_("allowPieceToStickToMouse since: die1StillAnOption:"+die1StillAnOption+" die2StillAnOption:"+die2StillAnOption+" bothDiceStillAnOption:"+bothDiceStillAnOption);
            }

             //grab the piece here we wish to move///
               /*      if (CustomCanvas.robotmove)
                 {
                 
                 
                    CustomCanvas.bot.click();
                     CustomCanvas.tellRobot(false,null);
                }*/
        }
        else
        {
            allowPieceToStickToMouse=false;
        }
    }

    //returns true if all of the pieces are in their home side, returns false otherwise.
    //checks the player that is passed in.
   /* private boolean piecesInHomeSide(int player)
    {
        int homeAreaStartSpike=0;
        int homeAreaEndSpike=0;
        int numberOfPiecesInHomeArea=0;

        if (player==Player.WHITE)
        {
            //THESE ARE CORRECT, they need to start at 1
            homeAreaStartSpike  = 1;//WHITE_HOME_START_SPIKE;//white home area is top right, 0,1,2,3,4,5
            homeAreaEndSpike    = 6;//WHITE_HOME_END_SPIKE;
           // _("piecesInHomeSide checking white");
        } else
        if (player==Player.BLACK)
        {
            homeAreaStartSpike  = 18;//BLACK_HOME_START_SPIKE;//black home area is bot right, 18,19,20,21,22,23
            homeAreaEndSpike    = 24;//BLACK_HOME_END_SPIKE;
           // _("piecesInHomeSide checking black");
        } else
        {
            HAL._E("piecesInHomeSide received an invalid player to check.");
        }

        int numberOfPiecesOnBoard=0;
        //so grab the list of spikes,
        Enumeration e = spikes.elements();
        while (e.hasMoreElements())
        {
            Spike spike = (Spike) e.nextElement();



            // check if its in "home" area:
            if (spike.position>=homeAreaStartSpike && spike.position<=homeAreaEndSpike)
            {
                //_("checkign spike: "+spike.position);
                //yes it is, so grab it and check its pieces. if there are 15
                //in this area then its time to let them start putting them in the piece container. (ie return true)
                //otherwise return false
                Enumeration e2 = spike.pieces.elements();
                while (e2.hasMoreElements())
                {
                    //check if there are 15 pieces in this home area
                    Piece piece =(Piece) e2.nextElement();
                    if (piece.colour == player)
                    {
                        numberOfPiecesInHomeArea++;
                    }
                    
                }
            }
            else {
                //_("skipped spike.position:"+spike.position+" since its not in range.");
            }
        }
        
        if (numberOfPiecesInHomeArea==15)
        {
            return true;
            //_("all pieces for player are in home area");
        }
        else
        {
           // _("not all pieces are in home only counted: "+numberOfPiecesInHomeArea);
        }
        return false;
    }*/

    private int calculateAmountOfPiecesOnBoard(int player)
    {
        int piecesOnBoard=0;
        Enumeration e = spikes.elements();
        while (e.hasMoreElements())
        {
            Spike spike = (Spike) e.nextElement();
            if (spike.pieces.size()>0)
            {
                Piece p = (Piece) spike.pieces.firstElement();
                if (p.colour==player)
                {
                    piecesOnBoard+=spike.pieces.size();
                }
            }
        }
        return piecesOnBoard;
    }

    private int calculateAmountOfPiecesInHomeArea(int player)
    {
        int piecesinHomeArea=0;

        int homeAreaStart=-1;
        int homeAreaEnd=-1;
        if (player==Player.WHITE)
        {
            homeAreaStart=0;
            homeAreaEnd=5;
        }
        else if (player==Player.BLACK)
        {
            homeAreaStart=18;
            homeAreaEnd=23;
        }
        else
        {
            HAL._E("colour not defined in calculateAmountOfPiecesInHomeArea!");
        }

        Enumeration e = spikes.elements();
        int spikePos=0;
        while (e.hasMoreElements())
        {
            Spike spike = (Spike) e.nextElement();
            if (spike.pieces.size()>0 && spikePos>=homeAreaStart && spikePos<=homeAreaEnd)
            {
                Piece p = (Piece) spike.pieces.firstElement();
                if (p.colour==player)
                {
                    piecesinHomeArea+=spike.pieces.size();
                    if (player==Player.WHITE)
                    {
                       // _("found "+spike.pieces.size()+" WHITE pieces on spike "+spikePos);
                    }

                }
            }
            
            spikePos++;
        }
        return piecesinHomeArea;
    }

    //these flags indicate if the player has took their go yet for that dice
    //if the dice are combined then they are both set to false in one go.
    public static boolean die1HasBeenUsed, die2HasBeenUsed;

    //takes in a spike and a die value,
    //this is to indicate to player its a potential move they can make
    //whichDice tells the spike which die would allow this potential move, so it an be displayed to player
    private void pulsatePotentialSpike(Spike spike, int whichDice)
    {
        //this makes the spike colour pulse nicely to indicate its an option
        spike.flash(whichDice);
    }


    boolean highlightPieceContainerAsOption;
    //takes in spike the player is currently hovering over with mouse
    //and also a die roll, and returns true if the potential spike (ie currentSpike + dieRoll=potentialSpike)
    //is able to be moved to.
   boolean checkAbleToGetIntoPieceContainerWHITE;
    boolean checkAbleToGetIntoPieceContainerBLACK;
    public static int whichDieGetsUsToPieceContainer=-1;
    //whichDieIsThis is passed in which indicates which die roll will be used in this potential move
    // 1 is die 1
    // 2 is die 2
    // 3 is die1+die2
    // these are used simply to update whichDieGetsUsToPieceContainer as it doesnt know otherwise
    private boolean checkValidPotentialMove(Spike currentSpike, int dieRoll, int whichDieIsThis)
    {
        // can the die roll produce a valid move?
        boolean yesThatsValid=false;
        
        //if the current spike theyre hovering over does contain
        //one or more of their pieces (belongs to them)
        if (currentSpike!=null)
        {
            ///////////////calculate Future spikes we can move to:
            ///work out if  die roll has any moves:

            //check if the spike reachable by die1's roll is available to
            //move a piece to, ie, EMPTY or occupied by this players pieces already
            Spike reachableFromDie=null;
            String notAnOptionReason="";//if its not an option this contains why

            ///---
            //we need to work out if we are going clockwise around the board or
            //not. If its whites turn then yes we are going clock wise, otherwise
            //no its anti clockwise. 

             //variables we need
            int potentialSpike=0;
            boolean withinLimits=false;
            boolean clockwise=false;// blacks pieces move anticlockwise, white clockwise

             checkAbleToGetIntoPieceContainerWHITE=false;
             checkAbleToGetIntoPieceContainerBLACK=false;


             boolean checkAbleToGetIntoPieceContainer=false; // this gets set to black or whites one
            if (whoseTurnIsIt==Player.WHITE)
            {
                clockwise=true;
                if (allWhitePiecesAreHome)//for when they want to be able to get into piece container
                {
                    checkAbleToGetIntoPieceContainerWHITE=allWhitePiecesAreHome;
                    checkAbleToGetIntoPieceContainer=checkAbleToGetIntoPieceContainerWHITE;

                }
            }
            else if (whoseTurnIsIt==Player.BLACK)
            {
                clockwise=false;
                if (allBlackPiecesAreHome)//for when they want to be able to get into piece container
                {
                    checkAbleToGetIntoPieceContainerBLACK=allBlackPiecesAreHome;
                     checkAbleToGetIntoPieceContainer=checkAbleToGetIntoPieceContainerBLACK;
                }
            }
            else
            {
                HAL._E("whoseTurnIsIt is invalid");
            }

            //now check if we were to take one of those pieces, would we be able to place it?
            if (clockwise) // whites parts go left so we deduct the roll
            {
                potentialSpike=currentSpike.getSpikeNumber()-dieRoll;
            }
            else// blacks parts go right so we add the roll
            {
                potentialSpike=currentSpike.getSpikeNumber()+dieRoll;
            }

             //work out the number of the potential spike
             if (checkAbleToGetIntoPieceContainer)
             {
                 //SPECIAL CASE
                 //if they are able to get into the piece container (ie theyre pieces are all in their homeside)
                 //then we need to consider the piece container as a potential spike (even tho its off board hence why we add/minus
                 //1 here) so depending on whose turn it is and where the potential move would be to we can return that it is
                 //within limits here in this special case
                 //check one further in each direction here since the piece contaienrs are also valid landing points in this circumstance
                 if (potentialSpike==FIRST_SPIKE-1 && whoseTurnIsIt==Player.WHITE && checkAbleToGetIntoPieceContainerWHITE)
                 {
                     // this would be an ideal way to get the piece safely into the piece collector
                     withinLimits=true;
                      //_("WHITE checkAbleToGetIntoPieceContainer: potentialSpike:"+potentialSpike+" is considered valid");
                 }
                 if (potentialSpike==LAST_SPIKE+1 && whoseTurnIsIt==Player.BLACK && checkAbleToGetIntoPieceContainerBLACK)
                 {
                     // this would be an ideal way to get the piece safely into the piece collector
                     withinLimits=true;
                     //_("BLACK checkAbleToGetIntoPieceContainer: potentialSpike:"+potentialSpike+" is considered valid");
                 }
                    
                 
             }
             //////////////else//not we cant yet get nito piece container (ie our pieces aret all home) so we need to keep it on board strictly
             {
                if (potentialSpike>=FIRST_SPIKE && potentialSpike<=LAST_SPIKE)//keep it on the board.
                {
                    withinLimits=true;
                }
                /*else
                {
                    withinLimits=false;
                }*/

             }
            
            
            if (withinLimits)//spikes.capacity())//so we dont grab a spike after 24
            {
                if (checkAbleToGetIntoPieceContainer)
                {
                    if (potentialSpike==FIRST_SPIKE-1 && whoseTurnIsIt==Player.WHITE)
                    {
                        highlightPieceContainerAsOption=true;

                        whichDieGetsUsToPieceContainer=whichDieIsThis;
                       // _("WHITE CAN PUT THIS PIECE IN THEIR CONTAINER USING DIE:"+whichDieGetsUsToPieceContainer+" whose value is "+dieRoll);
                        return true;
                    }
                    if (potentialSpike==LAST_SPIKE+1 && whoseTurnIsIt==Player.BLACK)
                    {
                        highlightPieceContainerAsOption=true;
                        
                         whichDieGetsUsToPieceContainer=whichDieIsThis;
                        // _("BLACK CAN PUT THIS PIECE IN THEIR CONTAINER USING DIE:"+whichDieGetsUsToPieceContainer+" whose value is "+dieRoll);
                        return true;
                    }
                }
                reachableFromDie = (Spike) spikes.elementAt(potentialSpike);

                ////IS SPIKE EMPTY? 
                if (  isThisSpikeEmpty(reachableFromDie) )
                {
                    //_("RETURN TRUE SINCE ITS EMPTY - ignoreEmptySpikes"+ignoreEmptySpikes);
                    yesThatsValid=true;//spike is empty
                    return yesThatsValid;
                }
                else
                {
                    notAnOptionReason="[Spike is not empty] OR WE IGNORE EMPTIES";
                }
                ////
                ////DOES SPIKE BELONG TO US ALREADY?
                if ( doesThisSpikeBelongToPlayer(reachableFromDie, whoseTurnIsIt))
                {
                   // _("yes "+reachableFromDie.spikeName+" already belongs to this player");
                   //yes, it already belongs to them
                   yesThatsValid=true;
                   return yesThatsValid;
                }
                else
                {
                    notAnOptionReason="[Spike doesnt belong to them]";
                }
                /////
            }
            else
            {
                notAnOptionReason="[Would be out of range.]";
            }
            if (yesThatsValid)
            {
                 //_("Yes Spike "+potentialSpike+" is a valid option.");
            }
            else
            {
                //_("No Spike "+potentialSpike+" is NOT a valid option because:"+notAnOptionReason);
            }
        }
        return yesThatsValid;
    }




    //returns the Spike the player has their mouse over currently IFF it
    //actually belongs to them (that is, already contains one of their pieces)
    //otherwise returns null.
    private Spike doesThisSpikeBelongToPlayer()
    {

        //get the Spike object that the players mouse is currently over
        Spike hoverSpike = grabSpikeHoveringOver();

        // so now we know what spike the player is hovering over, the 2 die values
        // and whose go it is, so we work out what moves can be made and
        // indicate them to the player visually.

        boolean containsOneOfTheirPieces=false;
        //first check, does the current spike (ie one theyre hovering over)
        // contain one or more of their pieces (in order to show them potential moves)
        if(hoverSpike!=null)
        {
           containsOneOfTheirPieces=doesThisSpikeBelongToPlayer(hoverSpike,whoseTurnIsIt);
        }

        if (containsOneOfTheirPieces)
        {
            return hoverSpike;
        }
        else
        {
            return null;
        }
    }

    //this returns a Spike object representing the spike that the players
    //mouse pointer is currently hovering over. If its not over any then
    //it returns null
    private Spike grabSpikeHoveringOver()
    {
        //check which spike we are hovering over if any
        Enumeration e = spikes.elements();
        String hoveringOver=null;
        Spike currentSpike=null;
        Spike hoverSpike=null;
        while (e.hasMoreElements())
        {
           currentSpike = (Spike) e.nextElement();
           if (mouseHoverX>currentSpike.collision_x && mouseHoverX<currentSpike.collision_x+currentSpike.TRIANGLE_WIDTH )
           {
               if (mouseHoverY > currentSpike.collision_y && mouseHoverY < currentSpike.collision_y + currentSpike.TRIANGLE_HEIGHT)
               {
                    hoveringOver=currentSpike.spikeName;
                    hoverSpike=currentSpike;
               }
           }
        }

        int hoveringOveri=-1;
        if (hoveringOver != null)
        {
            //grab spike number and casts to int
            try{
                hoveringOveri=Integer.parseInt(hoveringOver);
            }
            catch(Exception ex)
            {
                HAL._E("Problem parsing the spikes name to an int.. "+ex);
            }
        }
        //_("Hovering over spike: "+hoveringOveri);
        return hoverSpike;
    }

    //returns true if the spike is empty
    //false if not.
    // ALSO RETURNS TRUE IF THERE IS ONLY ONE ENEMY PIECE ON THE SPIKE
    private boolean isThisSpikeEmpty(Spike checkme)
    {
        //return true if empty
        if (checkme.pieces.size()==0)
        {
            // _("yes this spike is empty");
            return true;
        }
        //return true if theres only one enemy piece on this spike
        if (checkme.pieces.size()==1)
        {
            //ACTUALLY DOES IT MATTER WHOSE PIECE IT IS? I GUESS NOT RETURN TRUE;
            
            /*Piece piece=((Piece)checkme.pieces.firstElement());
            int enemyColour=-1;
            if (whoseTurnIsIt==Player.WHITE)
            {
                enemyColour=Player.BLACK;
            } else
            if (whoseTurnIsIt==Player.BLACK)
            {
                enemyColour=Player.WHITE;
            } else {HAL._E("whoseTurnIsIt invalid...");}

            if (piece.getColour()==enemyColour)
            {
                return true;
            }*/
            // _("yes this spike is empty");
            return true;
        }
        //////

        return false;
    }

    //returns true if the spike passed in contains a piece belonging to
    //the player colour passed in. returns false if not.
    private boolean doesThisSpikeBelongToPlayer(Spike checkme, int playerColour)
    {
         Enumeration piecesE = checkme.pieces.elements();//grab spikes pieces
         while(piecesE.hasMoreElements())
         {
             //grab pieces
             Piece aPiece = (Piece) piecesE.nextElement();
             //does one belong to current player?
             if(aPiece.colour==playerColour)
             {
                //_("Yes spike "+checkme.spikeName+" contains one of your pieces."+playerStr(whoseTurnIsIt));
                return true;
                //no need to keep checking.
             }
            
        }
         //_("--NO spike "+checkme.spikeName+" DOES NOT CONTAIN one of your pieces."+playerStr(whoseTurnIsIt));
          
        return false;
    }

    public static final int BOARD_NEW_GAME=0;
    public static final int DEBUG_BOARD_WHITE_PIECES_IN_THEIR_HOME=1;
    public static final int DEBUG_BOARD_BLACK_PIECES_IN_THEIR_HOME=2;
    //add both players pieces to necc spikes
    //in order to initialise a new game
    // modes are specified above, the only real one is BOARD_NEW_GAME, but we have debug ones also
    // for testing (different backgammon rules might exist so if so we can add new starting positions easily
    //here too).
    public void initialiseBoard(int mode)
    {
         /*why?
           this is for testing conditions without having to play the full game, otherwise I would need to run the game
         * and move all of the whites to their home manually to test etc, which is time consuming.
         */
      

        switch(mode)
        {
            case BOARD_NEW_GAME:
                _("mode: BOARD_NEW_GAME");
                 initialiseBoardForNewGame();
                break;
            case DEBUG_BOARD_WHITE_PIECES_IN_THEIR_HOME:
                _("mode: DEBUG_BOARD_WHITE_PIECES_IN_THEIR_HOME");
                // now we simply move all of the black pieces from where they are and add them into their home area
                //randomly.
                movePiecesToHome(Player.WHITE);
                break;
            case DEBUG_BOARD_BLACK_PIECES_IN_THEIR_HOME:
                _("mode: DEBUG_BOARD_BLACK_PIECES_IN_THEIR_HOME");
                // now we simply move all of the white pieces from where they are and add them into their home area
                //randomly.
                movePiecesToHome(Player.BLACK);
                break;
            default: HAL._E("Board.initialiseBoard received an invalid mode!");break;
        }
        
    }

    public static final int WHITE_HOME_START_SPIKE=0;//white home area is top right, 0,1,2,3,4,5
    public static final int WHITE_HOME_END_SPIKE=5;
    public static final int BLACK_HOME_START_SPIKE=18;//black home area is bot right, 18,19,20,21,22,23
    public static final int BLACK_HOME_END_SPIKE=23;



    //takes all pieces of the player passed in and moves them into their home area dropping them in on random spikes
    //for testing only.
    public void movePiecesToHome(int player)
    {
        int homeAreaStartSpike=0;
        int homeAreaEndSpike=0;
        int numberOfPiecesInHomeArea=0;
        Player father=null;

        if (player==Player.WHITE)
        {
            homeAreaStartSpike  = WHITE_HOME_START_SPIKE;//white home area is top right, 0,1,2,3,4,5
            homeAreaEndSpike    = WHITE_HOME_END_SPIKE;
            father=whitePlayer;
            _("movePiecesToHome  white");
        } else
        if (player==Player.BLACK)
        {
            homeAreaStartSpike  = BLACK_HOME_START_SPIKE;//black home area is bot right, 18,19,20,21,22,23
            homeAreaEndSpike    = BLACK_HOME_END_SPIKE;
            father=blackPlayer;
            _("movePiecesToHome  black");
        } else
        {
            HAL._E("piecesInHomeSide received an invalid player to check.");
        }

        //Remove all pieces from spikes of player passed in:////
        Enumeration e1 = spikes.elements();
        while (e1.hasMoreElements())
        {
            Spike spike = (Spike) e1.nextElement();
            Enumeration e2 = spike.pieces.elements();
            while(e2.hasMoreElements())
            {
                Piece piece = (Piece) e2.nextElement();
                if (piece.colour==player)
                {
                    spike.removePiece(piece);
                }
            }
        }
        //////////////////////////////////////////
        //add 15 pieces of correct colour to the home area, in random positions
        for (int i=0; i<15; i++)
        {
            int random = HAL.getRand(homeAreaStartSpike, homeAreaEndSpike-1);
            Spike spike = (Spike) spikes.elementAt(random);
            spike.addPiece(new Piece(father));
        }
    }

    //puts the pieces where they need to be to initialise a new game of backgammon
    public void initialiseBoardForNewGame()
    {
        _("initialiseBoardForNewGame");
        for (int i=0; i<24; i++)
        {
            _("#### Dealing with Spike number "+i);
            Spike tempSpike=null;
            try
            {
                tempSpike = (Spike)spikes.elementAt(i);
            }
            catch(Exception e)
            {
               HAL._E("grabbing a spike-problem doing spikes.elementAt("+i+"): "+e.getMessage());
            }

            int playeri=Player.BLACK;
            if (getWhitePlayer()==null)
            {
               HAL._E("getWhitePlayer returned a null player");
            }
            if (getBlackPlayer()==null)
            {
                HAL._E("getBlackPlayer returned a null player");
            }
            switch(i)
            {
                //DEFINE HERE WHICH SPIKES NEED WHAT TO BE INITIALISED
                //TO A NEW GAME.
                /////////////////////////////////////////////////////
                case 0: //SPIKE ONE
                    //add 2 white pieces to first pin
                    for (int j=0; j<2; j++)
                    {
                        Piece addMe = new Piece(getPlayer(playeri));
                        tempSpike.addPiece(addMe);
                        _("Add white piece to spike "+i);
                    }
                break;
                case 5: //SPIKE SIX
                    playeri=Player.WHITE;
                    //add 6 pieces to first pin
                    for (int j=0; j<5; j++)
                    {
                        Piece addMe = new Piece(getPlayer(playeri));
                        tempSpike.addPiece(addMe);
                        _("Add black piece to spike "+i);
                    }
                break;
                case 7: //SPIKE EIGHT
                    playeri=Player.WHITE;
                    //add 3 pieces to first pin
                    for (int j=0; j<3; j++)
                    {
                        Piece addMe = new Piece(getPlayer(playeri));
                        tempSpike.addPiece(addMe);
                        _("Add black piece to spike "+i);
                    }
                break;
                case 11: //SPIKE EIGHT
                    playeri=Player.BLACK;
                    //add 3 pieces to first pin
                    for (int j=0; j<5; j++)
                    {
                        Piece addMe = new Piece(getPlayer(playeri));
                        tempSpike.addPiece(addMe);
                        _("Add white piece to spike "+i);
                    }
                break;
                case 12: //SPIKE THIRTEEN
                    playeri=Player.WHITE;
                    //add 3 pieces to first pin
                    for (int j=0; j<5; j++)
                    {
                        Piece addMe = new Piece(getPlayer(playeri));
                        tempSpike.addPiece(addMe);
                        _("Add black piece to spike "+i);
                    }
                break;
                case 16: //SPIKE SEVENTINE
                    playeri=Player.BLACK;
                    //add 3 pieces to first pin
                    for (int j=0; j<3; j++)
                    {
                        Piece addMe = new Piece(getPlayer(playeri));
                        tempSpike.addPiece(addMe);
                        _("Add white piece to spike "+i);
                    }
                break;
                case 18: //SPIKE SEVENTINE
                    playeri=Player.BLACK;
                    //add 3 pieces to first pin
                    for (int j=0; j<5; j++)
                    {
                        Piece addMe = new Piece(getPlayer(playeri));
                        tempSpike.addPiece(addMe);
                        _("Add white piece to spike "+i);
                    }
                break;
                case 23: //SPIKE TWENTY FOUR
                    playeri=Player.WHITE;
                    //add 3 pieces to first pin
                    for (int j=0; j<2; j++)
                    {
                        Piece addMe = new Piece(getPlayer(playeri));
                        tempSpike.addPiece(addMe);
                        _("Add black piece to spike "+i);
                    }
                break;
            }
        }
    }

    public Player getPlayer(int col)
    {
        switch(col)
        {
            case Player.WHITE:
                return getWhitePlayer();
            case Player.BLACK:
                return getBlackPlayer();
            default:
                HAL._E("getPlayer did not receive a valid player colour "+col);
                return null;

        }
    }
    public Player getWhitePlayer()
    {
        
        if (whitePlayer==null)
        {
            HAL._E("getWhitePlayer() is returning null.");
        }
        return whitePlayer;
    }

    public Player getBlackPlayer()
    {
       
        if (blackPlayer == null) {
            HAL._E("getBlackPlayer() is returning null.");
        }
        return blackPlayer;
    }

    //takes a piece and its current spike and a new spike and returns
    //true if this piece can be moved from old spike to new spike
    //will catry out the move on ly if doit is true
    private boolean movePiece(Piece p, Spike oldSpile, Spike newSpike, boolean doit)
    {
        return true;
    }

    private static void _(String s)
    {
        HAL._("Board{}:"+s);
    }

    private String playerStr()
    {
         switch(whoseTurnIsIt)
        {
            case Player.WHITE:
                return "WHITE";
            case Player.BLACK:
                return "BLACK";
            default:
                HAL._E("playerStr did not receive a valid  whoseTurnIsIt "+whoseTurnIsIt);
                return null;

        }
    }

    public static String playerStr(int i)
    {
         switch(i)
        {
            case Player.WHITE:
                return "WHITE";
            case Player.BLACK:
                return "BLACK";
            default:
                HAL._E("playerStr did not receive a valid  i "+i);
                return null;

        }
    }
    //same but with nicer capitilisation
    private String playerStrLC(int i)
    {
         switch(i)
        {
            case Player.WHITE:
                return "White";
            case Player.BLACK:
                return "Black";
            default:
                HAL._E("playerStr did not receive a valid  i "+i);
                return null;

        }
    }



    //takes in spike and also a die roll, and returns the first potential spike (ie currentSpike + dieRoll=potentialSpike)
    //which is able to be moved to.
   
    // NOTE this is a duplicate of a method above with asome tweaks, dont confuse them
    private Spike checkValidPotentialMoveAndReturnThatSpike(Spike currentSpike, int dieRoll)
    {
        //_("checkValidPotentialMoveAndReturnThatSpike");
        // can the die roll produce a valid move?
        boolean yesThatsValid=false;

        //if the current spike theyre hovering over does contain
        //one or more of their pieces (belongs to them)
        if (currentSpike!=null)
        {
            ///////////////calculate Future spikes we can move to:
            ///work out if  die roll has any moves:

            //check if the spike reachable by die1's roll is available to
            //move a piece to, ie, EMPTY or occupied by this players pieces already
            Spike reachableFromDie=null;
            String notAnOptionReason="";//if its not an option this contains why

            ///---
            //we need to work out if we are going clockwise around the board or
            //not. If its whites turn then yes we are going clock wise, otherwise
            //no its anti clockwise.

            /*//ANTI-CLOCKWISE........
            boolean clockwise=false;
            int OFF_LIMITS=23;
            //work out the number of the potential spike
            int potentialSpike=currentSpike.getSpikeNumber()+dieRoll;
            boolean withinLimits=false;
            if (potentialSpike <= OFF_LIMITS)
            {
                withinLimits=true;
            }

            if (whoseTurnIsIt==Player.WHITE)
            {
                //CLOCKWISE...........
                clockwise=true;
                OFF_LIMITS=0;
                potentialSpike=currentSpike.getSpikeNumber()-dieRoll;

                if (potentialSpike >= OFF_LIMITS)
                {
                    //_("within limits");
                    withinLimits=true;
                }
                else
                {
                    withinLimits=false;
                }
               // _("potentialSpike:"+potentialSpike);
            }
            ///---*/
            boolean clockwise=false;
            int OFF_LIMITS=23;
            //work out the number of the potential spike
            int potentialSpike=currentSpike.getSpikeNumber()+dieRoll;
            boolean withinLimits=false;
            if (potentialSpike < OFF_LIMITS)
            {
                withinLimits=true;
            }

            if (whoseTurnIsIt==Player.WHITE)
            {
                //CLOCKWISE...........
                clockwise=true;
                OFF_LIMITS=0;
                potentialSpike=currentSpike.getSpikeNumber()-dieRoll;

                if (potentialSpike >= OFF_LIMITS)
                {
                    //_("within limits");
                    withinLimits=true;
                }
                else
                {
                    withinLimits=false;
                }
               // _("potentialSpike:"+potentialSpike);
            }
            ///---


            if (withinLimits)//spikes.capacity())//so we dont grab a spike after 24
            {
                reachableFromDie = (Spike) spikes.elementAt(potentialSpike);

                 ////IS SPIKE EMPTY?
                if (  isThisSpikeEmpty(reachableFromDie) )
                {
                    //_("RETURN TRUE SINCE ITS EMPTY ");
                    yesThatsValid=true;//spike is empty

                }
                else
                {
                    notAnOptionReason="[Spike is not empty]";
                }
                ////
                ////DOES SPIKE BELONG TO US ALREADY?
                if ( doesThisSpikeBelongToPlayer(reachableFromDie, whoseTurnIsIt))
                {
                    //_("yes "+reachableFromDie.spikeName+" already belongs to this player");
                   //yes, it already belongs to them
                   yesThatsValid=true;

                }
                else
                {
                    notAnOptionReason="[Spike doesnt belong to them]";
                }
                /////
            }
            else
            {
                notAnOptionReason="[Would be out of range.]";
            }
            if (yesThatsValid)
            {
                 //_("Yes Spike "+potentialSpike+" is a valid option.");
                if (currentSpike==reachableFromDie)
                {
                    _("RETURNING MYSELF.");
                }
                 return reachableFromDie;
            }
            else
            {
                //_("No Spike "+potentialSpike+" is NOT a valid option because:"+notAnOptionReason);
            }
        }
        return null;
    }



boolean wait;
    ////////////////
    ///////////////
    ////////NEW AI SHIT
    public static boolean listBotsOptions;
    public static String botOptions="<<NONE YET>>";
    Vector botDestinations;
public boolean thereAreOptions=false;
    public SpikePair SPtheMoveToMake;//stores the move they will make
public void calculatePotentialMoves(boolean FORCE)
{
    // boolean theyWantToPickUpAPiece=false;
               //  boolean theyWantToPlaceAPiece=false;
                 //if SPtheMoveToMake isnt null they have already chosen the spikes to pick up from and drop off at
                 if (FORCE || (!CustomCanvas.showRollButton && !CustomCanvas.pieceOnMouse) && !thereAreOptions )//&& SPtheMoveToMake==null))//&& !CustomCanvas.showRollButton)// && !die1HasBeenUsed  && !die2HasBeenUsed)
                 {
                 _("_______________________________________________RECALCULATE MOVES "+FORCE+" die1:"+die1HasBeenUsed+" die2:"+die2HasBeenUsed);
                     

                    theyWantToPickUpAPiece();//<- fills up spikePairs

                    Enumeration e = spikePairs.elements();
                    
                    while (e.hasMoreElements())
                    {
                        SpikePair sp = (SpikePair) e.nextElement();
                        //_("we can pick up from spike:"+sp.pickMyPiece.spikeName+" and drop off at spike:"+sp.dropPiecesOnMe.spikeName);
                        thereAreOptions=true;
                    }
                    boolean onlyOptionsAreWithNoExactDieRoll=true;
                    // if this is true by the time we get to "no options!" we know we need to let them take a special move and allow the
                    //die roll to wotkeven tho its to big

                    _("spikePairs size:"+spikePairs.size());

                    if (thereAreOptions)
                    {
                        listBotsOptions=true;//CustomCanvas.DEBUG_CONSOLE;
                        if (listBotsOptions)
                        {
                            botOptions="";
                            Enumeration ee = spikePairs.elements();
                            while (ee.hasMoreElements())
                            {

                                SpikePair sp=(SpikePair)ee.nextElement();

                                if (sp.dropPiecesOnMe.spikeName.equals(Spike.NOT_A_REAL_SPIKE_MINUS_99_STR) &&
                                    sp.pickMyPiece.spikeName.equals(Spike.NOT_A_REAL_SPIKE_MINUS_99_STR)
                                        )
                                {
                                    _("super special spike found ");
                                    /*this is when the die roll is too big to be exact but theyre putting pieces on container
                                     and it should be allowed.*/
                                }
                                /*else
                                {
                                    onlyOptionsAreWithNoExactDieRoll=false;
                                }*/

                                if (sp.dropPiecesOnMe.spikeName.equals(Spike.NOT_A_REAL_SPIKE_MINUS_99_STR))//ie its a fake spike, since its a piece container option
                                {
                                    botOptions+="->"+sp.pickMyPiece.spikeName+"->Container";
                                }
                                else
                                {
                                    botOptions+="->"+sp.pickMyPiece.spikeName+"->"+sp.dropPiecesOnMe.spikeName+" ";
                                }
                                
                            }

                        }
                        ///FORCE LAST OOPTION SPtheMoveToMake=(SpikePair)spikePairs.elementAt(spikePairs.size()-1);
                        //PICK ONE AT RANDOM
                        SPtheMoveToMake=(SpikePair)spikePairs.elementAt(HAL.getRand(0,spikePairs.size()-1));



                        if (SPtheMoveToMake.dropPiecesOnMe.spikeName.equals(Spike.NOT_A_REAL_SPIKE_MINUS_99_STR))//ie its a fake spike, since its a piece container option
                        {
                            //SPECIAL CONDITION, GO TO PIECE CONTAINER NOT SPIKE
                            _("SPECIAL CASE randomly chose to go to spike:"+SPtheMoveToMake.pickMyPiece.spikeName+" and drop off at CONTAINER");
                            CustomCanvas.tellRobot(true, "->"+SPtheMoveToMake.pickMyPiece.spikeName+"->Container");
                            Spike takeMyPiece = SPtheMoveToMake.pickMyPiece;
                            Piece firstPiece=((Piece)takeMyPiece.pieces.firstElement());
                          /* if (!Bot.FULL_AUTO_PLAY && HUMAN_VS_COMPUTER && whoseTurnIsIt==Player.WHITE)
                           {
                               CustomCanvas.desinationsFromWhichSource="Human D";
                           }
                           else{
                                    Bot.destX=firstPiece.collision_x+firstPiece.PIECE_DIAMETER/2;
                                      Bot.destY=firstPiece.collision_y+firstPiece.PIECE_DIAMETER/2;
                                      _("dest set "+Bot.destX+","+Bot.destY);
                                       CustomCanvas.desinationsFromWhichSource="Bot D";
                           }*/
                            setBotDestination(firstPiece.collision_x+firstPiece.PIECE_DIAMETER/2,firstPiece.collision_y+firstPiece.PIECE_DIAMETER/2,"TAKE A PIECE TO CONTAINER");

                            // Bot.destX=firstPiece.collision_x+firstPiece.PIECE_DIAMETER/2;
                                    //  Bot.destY=firstPiece.collision_y+firstPiece.PIECE_DIAMETER/2;
                        }
                        else
                        {

                            //NORMAL CONDITION
                             _("-randomly chose to go to spike:"+SPtheMoveToMake.pickMyPiece.spikeName+" and drop off at spike:"+SPtheMoveToMake.dropPiecesOnMe.spikeName);
                            CustomCanvas.tellRobot(true, "->"+SPtheMoveToMake.pickMyPiece.spikeName+"->"+SPtheMoveToMake.dropPiecesOnMe.spikeName);
                            Spike takeMyPiece = SPtheMoveToMake.pickMyPiece;
                            Piece firstPiece=((Piece)takeMyPiece.pieces.firstElement());
                            int goToX=firstPiece.collision_x+firstPiece.PIECE_DIAMETER/2;
                            int goToY=firstPiece.collision_y+firstPiece.PIECE_DIAMETER/2;

                            setBotDestination(goToX,goToY,"RANDOMLY CHOOSE A PIECE");
                            _("***************PIECE IM LOOKING FOR IS AT: "+goToX+","+goToY);
                        }

                       
                    }
                    else
                    {
                       /////////////// SPtheMoveToMake=null;
                        //////////thereAreOptions=false; //<-EXERIMENTED REMOVIN THITS JAN 26 131
                        _("NO OPTIONS!");

                        // OK NO POTENTIAL MOVES TO BE MADE HERE, NOW WHAT?
                        if (!die1HasBeenUsed)//if this is die 1 were dealing with
                        {
                            //SPECIAL CASE LARGE DIE ROLLS NEED TO BECOME VALID NOW. AS THEY NEED TO PUT PIECES AWAY
                            //so what we do is sneaky, reduce die value number (hiding it from players of course)
                            //which makes optiosn become available in this case.
                            if (whoseTurnIsIt==Player.WHITE && checkAbleToGetIntoPieceContainerWHITE)
                            {
                                
                                _("WHITE LOWERING THEVALUE OF DIE 1");
                                die1.setValue(die1.getValue()-1);
                            } else
                            if (whoseTurnIsIt==Player.BLACK && checkAbleToGetIntoPieceContainerBLACK)
                            {

                                _("BLACK LOWERING THEVALUE OF DIE 1");
                                die1.setValue(die1.getValue()-1);
                            }

                            else
                            {
                                //ORDINARY CASE
                                //use this die up so it can move onto next one
                                die1HasBeenUsed=true;
                                _("DISABLED DIE 1x");

                                CustomCanvas.tellPlayers("No option with Die 1 ("+die1.getValue()+")");
                                CustomCanvas.sfxNoMove.playSound();
                            }
                            
                           


                        }
                        else if (!die2HasBeenUsed)
                        {
                            //SPECIAL CASE LARGE DIE ROLLS NEED TO BECOME VALID NOW. AS THEY NEED TO PUT PIECES AWAY
                            //so what we do is sneaky, reduce die value number (hiding it from players of course)
                            //which makes optiosn become available in this case.
                            if (whoseTurnIsIt==Player.WHITE && checkAbleToGetIntoPieceContainerWHITE)
                            {

                                _("WHITE LOWERING THEVALUE OF DIE 2");
                                die2.setValue(die2.getValue()-1);
                            } else
                            if (whoseTurnIsIt==Player.BLACK && checkAbleToGetIntoPieceContainerBLACK)
                            {

                                _("BLACK LOWERING THEVALUE OF DIE 2");
                                die2.setValue(die2.getValue()-1);
                            }

                            else
                            {
                                //use this die up so it can move onto next go
                                die2HasBeenUsed=true;
                                _("DISABLED DIE 2x");
                                CustomCanvas.tellPlayers("No options available with Die 2 ("+die2.getValue()+")");
                                CustomCanvas.sfxNoMove.playSound();
                                //waitASec();
                                //it should move onto next players go NOW...

                                if (CustomCanvas.someoneRolledADouble)
                                {
                                    //even cancel a double go if theres no options for die2
                                    CustomCanvas.someoneRolledADouble=false;
                                    CustomCanvas.doubleRollCounter=3;
                                    _("NO OPTIONS SO CANCELLED DOUBLE TURN!");

                                }
                            }


                           
                        }




                        /*if (die1HasBeenUsed && die2HasBeenUsed)
                        {
                            
                         /////////EXPERMIENTAL BUT SOMETIMES IT DOESNT KNOW THE TURN IS OVER
                            //SO DICE DONT REAPPEAR
                            _("TURN OVER A.");
                             CustomCanvas.turnOver();
                        }*/

                       

                    }

                    
                 }
                 else
                 if (CustomCanvas.pieceOnMouse )//&& !CustomCanvas.showRollButton)
                 {
                    // theyWantToPlaceAPiece=true;
                     theyWantToPlaceAPiece();
                     thereAreOptions=false;
                 }
                 else
                 {
                     ///_("CONFUSED");
                 }
                //// calculatePotentialMoves( theyWantToPickUpAPiece, theyWantToPlaceAPiece);
                 // _("there are "+spikesWeCanMovePiecesToo.size()+" potential spikes to pick from (for pieces)");

                  if (die1HasBeenUsed && die2HasBeenUsed)
                {

                 /////////EXPERMIENTAL BUT SOMETIMES IT DOESNT KNOW THE TURN IS OVER
                    //SO DICE DONT REAPPEAR
                    _("TURN OVER A.");
                     CustomCanvas.turnOver();
                }

}

private void waitASec()
{
     ///////////////

                            _("--------------------------------------WAITING.");
                            try
                            {
                                Thread.sleep(10000);
                            }
                            catch(Exception wee)
                            {
                            }
                            wait=false;
                            _("-----------------------------------------DONE WAITING");

                        ///////////////
}

Vector spikePairs;
private void theyWantToPickUpAPiece()
{
    //_("theyWantToPickUpAPiece");

    spikePairs=new Vector(5);


            spikesWeCanMovePiecesToo=new Vector(4);
            moveAPieceToMe=null;

            int diceRoll=-1;
            //choose which die to use here
            if (!die1HasBeenUsed)
            {
                _("using DIE1 value ");
                diceRoll=die1.getValue();
            }
            else if (!die2HasBeenUsed)
            {
                _("using DIE2 value");
                diceRoll=die2.getValue();
            }

            //variables we need
            int potentialSpike=0;
            boolean clockwise=false;// blacks pieces move anticlockwise, white clockwise
boolean checkAbleToGetIntoPieceContainer=false;//this gets set according to whose go it is
            if (whoseTurnIsIt==Player.WHITE)
            {
                clockwise=true;
                checkAbleToGetIntoPieceContainerWHITE=allWhitePiecesAreHome;
                 checkAbleToGetIntoPieceContainer=checkAbleToGetIntoPieceContainerWHITE;
                 _("x checkAbleToGetIntoPieceContainerWHITE:"+checkAbleToGetIntoPieceContainerWHITE);
            }
            else if (whoseTurnIsIt==Player.BLACK)
            {
                clockwise=false;
                checkAbleToGetIntoPieceContainerBLACK=allBlackPiecesAreHome;
                checkAbleToGetIntoPieceContainer=checkAbleToGetIntoPieceContainerBLACK;
                _("x checkAbleToGetIntoPieceContainerBLACK:"+checkAbleToGetIntoPieceContainerBLACK);
            }
            else
            {
                HAL._E("whoseTurnIsIt is invalid");
            }

            if (diceRoll>0)
            {

                Enumeration e = spikes.elements();
                
                // go thru each spike and work where we have piece than can be picked up and moved
                // using that dice roll, when we find that spike we grab its first piece and use that dice roll
                // to pick up the piece and move it to the spike
                while(e.hasMoreElements())
               // Spike spike = (Spike) spikes.elementAt(HAL.getRand(0,spikes.size()-1));
               // if (spike!=null)
                {
                    Spike spike = (Spike) e.nextElement();
                    //check if we own it already (ie we have parts on it)
                    if (doesThisSpikeBelongToPlayer(spike,whoseTurnIsIt) && spike.pieces.size()>0)
                    {
                        //_(""+spike.spikeName+" belongs to us it has "+spike.pieces.size()+" of our parts" );

                        //so we have a spike that belongs to us for sure and has our pieces  on it
                        //now check if we were to take one of those pieces, would we be able to place it?
                        if (clockwise) // whites parts go left so we deduct the roll
                        {
                            potentialSpike=spike.getSpikeNumber()-diceRoll;
                        }
                        else// blacks parts go right so we add the roll
                        {
                            potentialSpike=spike.getSpikeNumber()+diceRoll;
                        }


                        ////SPECIAL CASE FOR PUTTING ON PIECE CONTAINER
                        //only for the case when a piece can go into the piece container
                        if (checkAbleToGetIntoPieceContainer)//highlightPieceContainerAsOption)//<-- this can get set by checkValidPotentialMove when the situation is right
                        {

                            /*theres one more conition here, they DONT NEED AN EXACT ROLL IF that roll is too high and would
                              end up being considered invalid, in this EXACT isntance they can use that roll to get onto piece container
                             *still
                             */
                            workOutWhichSpikesAreEmpty(Player.WHITE,spikes);


                            //IS THE DIE ROLL TOO BIG TO BE EXACT
                            /*if(whoseTurnIsIt==Player.WHITE && potentialSpike<FIRST_SPIKE-1)
                            {
                                _("yes this is too big to be exact WHITE potentialSpike:"+potentialSpike+" "+diceRoll);
                                Spike fakespikeA = new Spike(Spike.NOT_A_REAL_SPIKE_MINUS_99); 
                                Spike fakespikeB = new Spike(Spike.NOT_A_REAL_SPIKE_MINUS_99); 
                                spikePairs.add(new SpikePair(fakespikeA,fakespikeB));

                                //calculateIfThisDieRollIsToBigButAlsoValidForGettingOntoPieceContainer();
                            }
                            if(whoseTurnIsIt==Player.BLACK && potentialSpike>LAST_SPIKE+1)
                            {
                                _("yes this is too big to be exact BLACK potentialSpike:"+potentialSpike+" "+diceRoll);
                                Spike fakespikeA = new Spike(Spike.NOT_A_REAL_SPIKE_MINUS_99);
                                Spike fakespikeB = new Spike(Spike.NOT_A_REAL_SPIKE_MINUS_99);
                                spikePairs.add(new SpikePair(fakespikeA,fakespikeB));
                                //calculateIfThisDieRollIsToBigButAlsoValidForGettingOntoPieceContainer();
                            }*/
                            

                            if (potentialSpike>=FIRST_SPIKE-1 && potentialSpike<=LAST_SPIKE+1)//keep it on the board.BUT REMEMBER PIECE CONTAINERS
                            {
                                _("checkAbleToGetIntoPieceContainer is true - potentialSpike:"+potentialSpike);
                                // so we now know that the piece container is an option and we have a valid roll
                                // to get us there.
                                // buti n this case we cant grab a spiek to navigate to, and the piecesSafelyHome is just a vector
                                // but what we do is sneaky, we make a fake spikePair and tell the spikePair its fake
                                //so that it (the piece container, that is not a spike) is considered an option just like any normal spike.

                                if (potentialSpike==LAST_SPIKE+1 || potentialSpike==FIRST_SPIKE-1)//so we know for sure its destined for piece container
                                {
                                    _("PIECECONTAINER: MAKING A FAKE SPIKE, potentialspike is "+potentialSpike);
                                    // pass in -99 to make spike a very special one which is basically a piece container (see Spike constructor)
                                    canWeMoveAPieceToThisSpike = new Spike(Spike.NOT_A_REAL_SPIKE_MINUS_99); //(Spike) spikes.elementAt(potentialSpike);
                                    _("yes "+canWeMoveAPieceToThisSpike.spikeName+" IS A PIECE CONTAINER we can move to");
                                          spikePairs.add(new SpikePair(spike,canWeMoveAPieceToThisSpike));

                                }
                                /*else
                                {
                                    //this sets up the spike we use for moving piece to
                                    canWeMoveAPieceToThisSpike = (Spike) spikes.elementAt(potentialSpike);
                                    //if its empty or we own it, we can move to it (ADD IN HERE KILLING OTHER PLAYER)
                                    if (canWeMoveAPieceToThisSpike.pieces.size()==0 ||
                                            doesThisSpikeBelongToPlayer(canWeMoveAPieceToThisSpike,whoseTurnIsIt))
                                    {
                                        _("yes "+canWeMoveAPieceToThisSpike.spikeName+" we can move to");
                                          spikePairs.add(new SpikePair(spike,canWeMoveAPieceToThisSpike));

                                    }
                                }*/


                            }
                        }
                        ////////////////
                        ///////////////WE STILL NEED THIS DONE TOO I THINK EXPERIMENT, NOT AN ELSE AS FAR AS I KNOW else
                        //if it got here its not destined forp iece container
                        {

                            //so we grab the potential spike (based on the dieroll given) where we would place
                            //a piece from the ones that belong to us
                            if (potentialSpike>=FIRST_SPIKE && potentialSpike<=LAST_SPIKE)//keep it on the board.
                            {

                                /*FORMULATE SETS THE PICKER UPER AND THE ENDER, KEEP THESE SETS THATS THE
                                 KEY... */
                                //this sets up the spike we use for moving piece to
                                canWeMoveAPieceToThisSpike = (Spike) spikes.elementAt(potentialSpike);

                                //if its empty or we own it, we can move to it (ADD IN HERE KILLING OTHER PLAYER)
                                if (    canWeMoveAPieceToThisSpike.pieces.size()==0 ||
                                        canWeMoveAPieceToThisSpike.pieces.size()==/*0*/1 || //<-- 1 HERE SHOULD FIX IT SO IT ATTACKS
                                        doesThisSpikeBelongToPlayer(canWeMoveAPieceToThisSpike,whoseTurnIsIt))
                                {
                                    //_("yes "+canWeMoveAPieceToThisSpike.spikeName+" we can move to");
                                      spikePairs.add(new SpikePair(spike,canWeMoveAPieceToThisSpike));

                                }
                            }

                        }

                    }

                }

            } else
            {
                _(" warnign dice roll was under 0 - this indicates no options for this player");
            }
//            return null;
_("finished theyWantToPickUpAPiece");
}

private void calculateIfThisDieRollIsToBigButAlsoValidForGettingOntoPieceContainer()
{
}


//works out empty spikes in that players home area, for when die roll is too big but can be used to go into piece container,
//returns true if any empty spikes are found
int [] whiteSpikesHome = {1,1,1,1,1,1};// map of empty or not empty spikes
private boolean workOutWhichSpikesAreEmpty(int player,Vector spikes)
{

        Enumeration e = spikes.elements();
        int counter=0;
        int amountOfEmptySpikes=0;
      //  _("CHECKING "+playerStr(player)+" HOME AREA FOR EMPTIES");
        while(e.hasMoreElements())
        {
            Spike s = (Spike) e.nextElement();
            boolean spikeEmpty=false;
            
            spikeEmpty=true;
           
            if (s.getAmountOfPieces(player)==0 && player==Player.WHITE && counter>=0 && counter<=5)
            {
                //_("spike "+s.getSpikeNumber()+" is empty.");
                whiteSpikesHome[counter]=0;
                spikeEmpty=true;
                amountOfEmptySpikes++;
            }
            /*else
            if (s.getAmountOfPieces(player)==0 && player==Player.BLACK && counter>=23 && counter<=18)
            {
                _("spike "+s.getSpikeNumber()+" is empty.");
                spikeEmpty=true;
            }*/
            counter++;
            
        }
        if (amountOfEmptySpikes>0)
        {
            for (int i=0; i<whiteSpikesHome.length;i++)
            {
                //System.out.print(""+whiteSpikesHome[i]+",");
            }
            return true;
        }
        else
        {
            return false;
        }



}

public static String ROBOT_DESTINATION_MESSAGE="";
static boolean sameDest;
public static void setBotDestination(int x, int y, String desc)
{
    if (Bot.destX==x && Bot.destY==y)
    {
        sameDest=true;
    }
    else
    {
        sameDest=false;
    }
    if (sameDest)
    {
        return;//just so the message is not constantly printing
    }
    ROBOT_DESTINATION_MESSAGE="NEW BOT DEST: "+x+","+y+":"+desc;
    _(ROBOT_DESTINATION_MESSAGE);
    Bot.destX=x;
    Bot.destY=y;
}

private void theyWantToPlaceAPiece()
{
    if (CustomCanvas.barPieceStuckOnMouse)
    {
        //stops tem going on with normal case stuff will bar piece is put down
        //works briliantly!
        _("dont do anythign til we palce this");
        return;
    }
    if (SPtheMoveToMake==null)
    {
        _("DOUBLE RECALC.>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        thereAreOptions=false;

        

        return;
    }
    if (!Bot.FULL_AUTO_PLAY && HUMAN_VS_COMPUTER && whoseTurnIsIt==Player.WHITE)
    {
        return;
    }
    Spike dropOnMe = SPtheMoveToMake.dropPiecesOnMe; // gets the spike to drop pieces on
    if (dropOnMe!=null)
    {
        if (dropOnMe.spikeName.equals(Spike.NOT_A_REAL_SPIKE_MINUS_99_STR))
        {
            //SPECIAL CASE DROP ON CONTAINER
            int pieceContainerX=0;
            int pieceContainerY=0;
            int pieceContainerWidth=0;
            int pieceContainerHeight=0;
            //decide whose piece container the destination is:
            if (whoseTurnIsIt==Player.WHITE)
            {
                pieceContainerX=CustomCanvas.whiteContainerX;
                pieceContainerY=CustomCanvas.whiteContainerY;
                pieceContainerWidth=CustomCanvas.whiteContainerWidth;
                pieceContainerHeight=CustomCanvas.whiteContainerHeight;
            } else
            if (whoseTurnIsIt==Player.BLACK)
            {
                pieceContainerX=CustomCanvas.blackContainerX;
                pieceContainerY=CustomCanvas.blackContainerY;
                pieceContainerWidth=CustomCanvas.blackContainerWidth;
                pieceContainerHeight=CustomCanvas.blackContainerHeight;
            }else { HAL._E("errori n theywanttoplaceapiece, turn is invalid");}
            ///_("Piece container DEST set");
            setBotDestination(pieceContainerX+pieceContainerWidth/2,pieceContainerY+pieceContainerHeight/2,"PIECE CONTAINER DESTINATION");
           // Bot.destX=pieceContainerX+pieceContainerWidth/2;
            //Bot.destY=pieceContainerY+pieceContainerHeight/2;
            //CustomCanvas.desinationsFromWhichSource="Bot E";
        }
        else
        {
            //NORMAL CASE DROP ON SPIKE
            if (dropOnMe.getType()==Spike.STALECTITE)
            {
               // _("STALECTITE dest");
                setBotDestination(dropOnMe.x3-dropOnMe.TRIANGLE_WIDTH/2,dropOnMe.y2-dropOnMe.TRIANGLE_HEIGHT/2,"NORMAL CASE DROP ON SPIKE A");
                  //  Bot.destX=dropOnMe.x3-dropOnMe.TRIANGLE_WIDTH/2;
                   // Bot.destY=dropOnMe.y2-dropOnMe.TRIANGLE_HEIGHT/2;
                   // CustomCanvas.desinationsFromWhichSource="Bot F";
            }else
            if (dropOnMe.getType()==Spike.STALECMITE)
            {
                 setBotDestination(dropOnMe.x3-dropOnMe.TRIANGLE_WIDTH/2,dropOnMe.y2+dropOnMe.TRIANGLE_HEIGHT/2,"NORMAL CASE DROP ON SPIKE B");
                   //  _("STALECMITE dest");
                   // Bot.destX=dropOnMe.x3-dropOnMe.TRIANGLE_WIDTH/2;
                   // Bot.destY=dropOnMe.y2+dropOnMe.TRIANGLE_HEIGHT/2;
                   // CustomCanvas.desinationsFromWhichSource="Bot F";
            }
        }
    }
    else
    {
        HAL._E("DROP ON ME IS NULL.");
    }
    

    

}

public static boolean NOT_A_BOT_BUT_A_NETWORKED_PLAYER=false;

 public static final int LAST_SPIKE=23;// and last spike is 23
 public static final int FIRST_SPIKE=0;// first is zero
 Vector spikesWeCanMovePiecesToo;
 //
 /*public void calculatePotentialMoves(boolean theyWantToPickUpAPiece,boolean theyWantToPlaceAPiece)
    {
	//if there is no piece on the mouse, die available, and no roll button
	//they must want to pick up a piece
	if (theyWantToPickUpAPiece)
        {
            
	}
	else if (theyWantToPlaceAPiece)
	{


	}
	
}*/

 Spike moveAPieceToMe;
Spike canWeMoveAPieceToThisSpike;

//returns a vector of pieces that can be picked up and use validly
/*private Vector calculatePotentialValidPiecesToPickUp(int dieval)
{
        Vector potentialSpikes = new Vector();
	//_("calculatePotentialValidPiecesToPickUp");
        boolean leapOut=false;
        Enumeration e = spikes.elements();
        while(e.hasMoreElements() && !leapOut)
        {
              Spike spike = (Spike) e.nextElement();
              
              Spike pickUpMyPieces = checkValidPotentialMoveAndReturnThatSpike(spike,dieval );


              if (pickUpMyPieces!=null)
              {
                 if (pickUpMyPieces.pieces.size()>0)//A PIECE WE WANT TO CLICK ON.
                 {
                     //CustomCanvas.tellRobot(true, "calculatePotentialValidPiecesToPickUp die1");
                     //Piece firstPiece=((Piece)pickUpMyPieces.pieces.firstElement());
                     //Bot.destX=firstPiece.collision_x+firstPiece.PIECE_DIAMETER/2;
                     //Bot.destY=firstPiece.collision_y+firstPiece.PIECE_DIAMETER/2;
                     //////leapOut=true;
                     //dont leap out we want a vector fo them all
                     potentialSpikes.add(pickUpMyPieces);
                     
                 }
                 //else
                 //{
                     //_("DIE1 NO PIECE TO CLICK ON.");
                 //}
              }
        }
      //  _("returned "+potentialSpikes.size());
     //   listSpikes(potentialSpikes);
        return potentialSpikes;
}
*/
private void listSpikes(Vector potentialSpikes)
{
    Enumeration e = potentialSpikes.elements();
    while(e.hasMoreElements())
    {
        Spike spike = (Spike) e.nextElement();
        System.out.print(" spike:"+spike.spikeName+" ");
    }
}

//returns a vector of pieces that can be picked up and use valid
private void calculatePotentialValidSpikesToPlacePiece()
{
	_("calculatePotentialValidSpikesToPlacePiece");
}



}
