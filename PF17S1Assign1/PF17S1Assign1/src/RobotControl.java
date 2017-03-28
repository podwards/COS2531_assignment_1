import java.util.Arrays;

class RobotControl
 {
   private Robot r;
   private Block load, nullBlock;
   private BlockPile targetPile, tempPile, sourcePile, currentPile;
   private BlockPile[] piles;
   private int[] heights;
   private int h, w, d, grabberH, maxHeightArm, maxHeightGrabber; 
   private int sourcePos, tempPos, targetPos;
   
   /* This method sets the initial positions of things like the height of the 
    * arm, the grabber height, depth of grabber etc. It also creates all the
    * BlockPiles where all the blocks can be placed. 
    * 
    * @param r 	The Robot object that this controls
    */ 
   public RobotControl(Robot r)
   {
	   // setup the crane positions
       this.r = r; // TODO: remove this?
       this.h = 2; // the height of the arm of the Robot
       this.w = 1; /* the width of the arm of the Robot, and the lateral position
       				  of the grabber and whatever load it has.*/
       this.d = 0; // the depth of the grabber arm
       this.grabberH = 1; /* the effective height of the grabber which includes
       						 the size of the load*/
       this.maxHeightArm = 14; // TODO: remove this?
       this.maxHeightGrabber = 14; // TODO: remove this?
    		  
       this.piles = new BlockPile[11]; /* this array is used to find the correct
       								      pile based on the current position of
       								      the arm (this.w).*/
       
       // make the piles where the Block's are stored
       int[] pile_positions = {1,9,10};
       for (int p: pile_positions)
       {
    	   this.piles[p] = new BlockPile(p);
       }	 
       
       // save the class variables
       
       this.tempPos = 9;
       this.targetPos = 1;
       this.sourcePos = 10;
       
       
       nullBlock = new Block(0); /* this is basically a place holder for the 
       							    load.*/
       this.load = nullBlock;
   }
   
   /**
    * Creates the heights array and updates it for the bars if any were 
    * supplied by the user.
    * 
    * @param barHeights		the array passed from the user which contains the height 
    * 						of each bar that could be in the way.
    */
   private void setHeights(int barHeights[])
   {
	   
	   /* positions labeled 1-10, I'll follow same convention here. This array
	    * is looked at by the resetArmMethod and updated by the pickAtPosition
	    * and dropAtPosition methods whenever blocks are moved.*/
	   this.heights = new int[11]; 
	   	   
	   for (int i = 1; i < barHeights.length; i++)
	   {
		   // assumes bars get placed from 2, may not be true, but doesn't matter
		   this.heights[i+1] = barHeights[i];
	   }
	   this.heights[1] = 0; // nothing at target yet
	   this.heights[9] = 0; // or the temp pile
	   this.heights[10] = 0; // these get updated with push and pops on the BlockPile
   }
   
   /**
    * Creates the Block Objects and adds them to the Source pile
    * 
    * @param blockHeights	the array passed from the user which contains the height 
    * 						of each block to be used
    */
   private void setBlockPositions(int[] blockHeights)
   {
	   for (int h: blockHeights)
	   {
		   load = new Block(h);
		   piles[this.sourcePos].push(load); // This is called when everything is at source
	   }	 
	   this.heights[this.sourcePos] = piles[this.sourcePos].height;
   }
   
   private void printStatus()
   {
	   //System.out.format("Status: H = %d W = %d D = %d G= %d%n", this.h, this.w, this.d, this.grabberH);
   }
   
   /*
    * Changes the width of the robot arm by looping according to the change 
    * specified
    * 
    * @param dW		the change in width from the current state.
    */
   private void changeH(int dH)
   {
	   if (dH < 0) {
		   for(int i = 0; i < dH*-1; i++)
		   {
			   r.down();
		   }
	   }
	   else {
		   for(int i = 0; i < dH; i++)
		   {
			   r.up();
		   }
	   }
	   this.h += dH;
	   this.grabberH += dH;
	   this.printStatus();
   }
   private void changeW(int dW)
   {
	   if (dW < 0) {
		   for(int i = 0; i < dW*-1; i++)
		   {
			   r.contract();
		   }
	   }
	   else {
		   for(int i = 0; i < dW; i++)
		   {
			   r.extend();
		   }
	   }
	   this.w += dW;
	   this.printStatus();
   }
   
   /*
   * Changes the width of depth of the grabber on the robot arm by looping according
   * to the change specified. In addition to the changing the depth of the arm, 
   * update the grabberH which is used to set the clearance height of the arm.
   * 
   * @param dD		the change in depth from the current state.
   */
   private void changeD(int dD)
   {
	   // if dD is positive, the grabber is moving up
	   if (dD < 0) {
		   for(int i = 0; i < dD*-1; i++)
		   {
			   r.lower();
		   }
	   }
	   else {
		   for(int i = 0; i < dD; i++)
		   {
			   r.raise();
		   }
	   }
	   this.d += dD;
	   this.grabberH += dD;
	   this.printStatus();
   }

   /*
    *  This decides what the clearance height of the grabber needs to be by looking
    *  at the heights array which is maintained with all block movements.
    */
   private int getClearanceHeight()
   {
	   return MyMath.max(this.heights);
   }
   
   /*
   * Changes the depth of the grabber and then maybe the height of the robot 
   * arm based on the what the highest object is. It doesn't yet consider where
   * the arm is going to be moving, so will potentially move higher than needed
   * but never lower, so that wherever the robot moves the arm and load, there
   * are no collisions.
   */
   private void resetArmHeight()
   {
	   // try get clearance by increasing grabber height first, then try arm height
	   System.out.format("Grabber height = %d%n", this.grabberH);
	   int dG = getClearanceHeight() - this.grabberH; // total increase in grabber height
	   int dD, dH;
	   if (dG <= -this.d)
	   {
		   dD = dG;
		   dH = 0; // can get by moving grabber
	   }
	   else
	   {
		   dD = -this.d;
		   dH = dG - dD;
	   }
	   
	   System.out.format("dD = %d dH  %d%n", dD, dH);
	   
	   this.changeD(dD);
	   this.changeH(dH);

   }
   
   /*
    * This method firstly ensures that the arm is set to an appropriate height
    * before changing the width of the arm to get to position p.
    * 
    * @param p 		an integer position value for where the arm needs to reach.
    */
   private void armToPosition(int p)
   {
	   // later add optional arguments for height, for now use max of 14
	   
	   // ensure that the arm is elevated before moving
	   this.resetArmHeight();
	   int dW = p - this.w; 
	   this.changeW(dW);
		
   }
   
   /*
    * Takes the top Block from the BlockPile at the current position and set it to 
    * the load variable
    */
   private void pickAtPosition()
   {
	   currentPile = this.piles[this.w];
	   
	   int pileHeight = currentPile.height;
	   int drop = pileHeight - this.grabberH; //
	   
	   this.changeD(drop); // drop using the picker
	   System.out.println("picking...");
	   r.pick();
	   this.load = currentPile.pop();
	   this.grabberH -= this.load.size;
	   this.heights[this.w] = currentPile.height;
	   
   }
   
   /*
    * Firstly, drop the grabber to the height of the grabber (which includes the
    * current load. Then it takes the Block from the load variable and sets it 
    * on top of the BlockPile at the current position. Sets the load variable 
    * to the nullBlock.
    */
   private void dropAtPosition()
   {
	   currentPile = this.piles[this.w];
	   
	   int drop = this.grabberH - currentPile.height;
	   System.out.format("Dropping by %d%n", drop);
	   this.changeD(-drop);
	   
	   r.drop();
	   currentPile.push(this.load);
	   this.grabberH += this.load.size;
	   this.load = this.nullBlock;
	   this.heights[this.w] = currentPile.height;
   }
   
   /*
    * Using a for loop and lots of helper functions, move each block from the
    * source position to the target position.
    * 
    * @param blockHeights 		the array of blockHeights as passed by the user.
    */
   private void moveBlocksFromSourceToStackSimple(int [] blockHeights)
   {
	   for (int i = 0; i < blockHeights.length; i++)
	   {
	       this.armToPosition(this.sourcePos);
	       this.pickAtPosition();
	       this.armToPosition(this.targetPos);
	       this.dropAtPosition();
	   }   
   }
   
   private void moveToDifferentSourcePile()
   {
	   if (this.w == this.sourcePos)
	   {
		   this.armToPosition(this.tempPos);
	   }else if (this.w == this.tempPos)
	   {
		   this.armToPosition(this.sourcePos);
	   }else
	   {
		   this.armToPosition(this.sourcePos);
	   }
   }
   /*
    *  This function looks at the pile that we're in, and then shifts it to the
    *  'other' source pile, which is the original source or maybe the temp pile.
    *  This shifting of blocks is used by the searchAndMove method to try and 
    *  locate the correct block to send to the target.
    */
   private void shiftTopBlock()
   {
	   System.out.format("shifting from %d%n", this.w);
	   this.pickAtPosition();
	   this.moveToDifferentSourcePile();
	   this.dropAtPosition();
	   this.moveToDifferentSourcePile();
   }
   
   private void searchAndMove(int size)
   {
	   currentPile = this.piles[this.w];
	   int digTo = currentPile.sizeSearch(size);
	   if (digTo==-1)
	   { 
		   this.moveToDifferentSourcePile(); // let's try recursive later...
	   }
	   
	   currentPile = this.piles[this.w];
	   digTo = currentPile.sizeSearch(size);
	   
	   for (int i = 1; i < digTo; i++)
	   {
	       this.shiftTopBlock();      
	   }
	   
	   this.pickAtPosition();
	   this.armToPosition(this.targetPos);
	   System.out.format("dropping at %d%n", this.w);
	   this.dropAtPosition();
	   this.moveToDifferentSourcePile();
   }
   
   private void moveBlocksInOrder(int blockHeights[], int required[])
   {
	   for (int size: required)
	   {
		   System.out.format("Start search for %d%n", size);
		   this.searchAndMove(size);
	   }
   }

   public void control(int barHeights[], int blockHeights[], int required[], boolean ordered)
   {
   
	   this.setHeights(barHeights);
	   this.setBlockPositions(blockHeights);  
	   
	   if (required.length == 0) // This gets run for parts A-C
	       {this.moveBlocksFromSourceToStackSimple(blockHeights);}
	   else // This gets run for parts D and E
	   	   {this.moveBlocksInOrder(blockHeights, required);}
	   
	   
	  
   }
 
}  

