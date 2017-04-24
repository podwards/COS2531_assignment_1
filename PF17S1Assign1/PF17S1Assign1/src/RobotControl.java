import java.util.Arrays;

class RobotControl
 {
   private Robot robot;
   private Block load, nullBlock;
   private BlockPile currentPile;
   private BlockPile[] piles;
   private int[] heights;
   private int armHeight, armWidth, armDepth, grabberHeight;
   private int sourcePos, tempPos, targetPos;
   
   /* This method sets the initial positions of things like the height of the 
    * arm, the grabber height, depth of grabber etc. It also creates all the
    * BlockPiles where all the blocks can be placed. 
    * 
    * @param r 	The Robot object that this controls
    */ 
   public RobotControl(Robot r)
   {
       robot = r;
	   
	   // setup the initial crane positions
       armHeight = 2; // the height of the arm of the Robot
       armWidth = 1; /* the width of the arm of the Robot, and the lateral position
       				  of the grabber and whatever load it has.*/
       armDepth = 0; // the depth of the grabber arm
       grabberHeight = 1; /* the effective height of the grabber which includes
       						 the size of the load*/
       piles = new BlockPile[11]; /* this array is used to find the correct
       								      pile based on the current position of
       								      the arm (w).*/
       
       // make the piles where the Block's are stored
       int[] pile_positions = {1,9,10};
       for (int p: pile_positions)
       {
    	   piles[p] = new BlockPile();
       }	 
       
       // save the class variables
       
       tempPos = 9;
       targetPos = 1;
       sourcePos = 10;
       
       
       nullBlock = new Block(0); /* this is basically a place holder for the 
       							    load.*/
       load = nullBlock;
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
	   
	   /* positions labeled 0-10, I'll follow same convention here. This array
	    * is looked at by the resetArmMethod and updated by the pickAtPosition
	    * and dropAtPosition methods whenever blocks are moved.*/
	   heights = new int[11]; 
	   	   
	   for (int i = 0; i < barHeights.length; i++)
	   {
		   // bars get placed from position 2
		   heights[i+2] = barHeights[i];
	   }
	   heights[targetPos] = 0; // nothing at target yet
	   heights[tempPos] = 0; // or the temp pile
	   heights[sourcePos] = 0; // these get updated with push and pops on the BlockPile
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
		   piles[sourcePos].push(load); // This is called when everything is at source
	   }	 
	   heights[sourcePos] = piles[sourcePos].getHeight();
   }
   
   /**
    * Prints status information of the current parameters of the robot arm.
    */
   private void printStatus()
   {
	   //System.out.format("Status: H = %d W = %d D = %d G= %d%n", h, w, d, grabberH);
   }
   
   /**
    * Changes the height of the robot arm by looping according to the change 
    * specified
    * 
    * @param dH		the change in height from the current state.
    */
   private void changeArmHeight(int dH)
   {
	   if (dH < 0) {
		   for(int i = 0; i < dH*-1; i++)
		   {
			   robot.down();
		   }
	   }
	   else {
		   for(int i = 0; i < dH; i++)
		   {
			   robot.up();
		   }
	   }
	   armHeight += dH;
	   grabberHeight += dH;
	   printStatus();
   }
   
   /**
    * Changes the width of the robot arm by looping according to the change 
    * specified
    * 
    * @param dW		the change in width from the current state.
    */
   private void changeArmWidth(int dW)
   {
	   if (dW < 0) {
		   for(int i = 0; i < dW*-1; i++)
		   {
			   robot.contract();
		   }
	   }
	   else {
		   for(int i = 0; i < dW; i++)
		   {
			   robot.extend();
		   }
	   }
	   armWidth += dW;
	   printStatus();
   }
   
   /**
   * Changes the width of depth of the grabber on the robot arm by looping according
   * to the change specified. In addition to the changing the depth of the arm, 
   * update the grabberH which is used to set the clearance height of the arm.
   * 
   * @param dD		the change in depth from the current state.
   */
   private void changeArmDepth(int dD)
   {
	   // if dD is positive, the grabber is moving up
	   if (dD < 0) {
		   for(int i = 0; i < dD*-1; i++)
		   {
			   robot.lower();
		   }
	   }
	   else {
		   for(int i = 0; i < dD; i++)
		   {
			   robot.raise();
		   }
	   }
	   armDepth += dD;
	   grabberHeight += dD;
	   printStatus();
   }

   /**
    *  This decides what the clearance height of the grabber needs to be by looking
    *  at the heights array which is maintained with all block movements
    *  
    *  @param fromPos 			the integer position where the robot arm is currently
    *  @param toPos 			the integer position where the robot arm is going to next.
    *  
    *  @return     				the maximum height (bars or BlockPiles) between fromPos and
    *  							toPos.
    */
   private int getClearanceHeight(int fromPos, int toPos)
   { 
	   int clearance = MyMath.maxInBounds(heights, fromPos, toPos);
	   return clearance;
   }
   
   /**
   * Changes the depth of the grabber and then maybe the height of the robot 
   * arm based on the what the requiredHeight is for the next move
   * 
   * @param requiredHeight 		the maximum height bar or block pile between the current
   * 						    and next position of the robot arm
   */
   private void resetArmHeight(int requiredHeight)
   {
	   // try get clearance by increasing grabber height first, then try arm height
	   //System.out.format("Grabber height = %d%n", grabberH);
	   
	   // Find the  total required increase in grabber height
	   int dG = requiredHeight - grabberHeight; 
	   int dD, dH;
	   
	   // See if we can change the height purely by changing the depth parameter
	   if (dG <= -armDepth)
	   {
		   dD = dG;
		   dH = 0; // can get by moving grabber only, no need to change arm height 
	   }
	   else 
	   {
		   dD = -armDepth; // reduce the depth to zero
		   dH = dG - dD; // and make up the difference with arm height
	   }
	   	   
	   changeArmDepth(dD);
	   changeArmHeight(dH);

   }
   
   /**
    * This method firstly ensures that the arm is set to an appropriate height
    * before changing the width of the arm to get to position p.
    * 
    * @param p 		an integer position value for where the arm needs to reach.
    */
   private void armToPosition(int p)
   {
	   // later add optional arguments for height, for now use max of 14
	   
	   // ensure that the arm is elevated before moving
	   int requiredHeight = getClearanceHeight(armWidth, p);
	   resetArmHeight(requiredHeight);
	   
	   // calculate the necessary change in position
	   int dW = p - armWidth; 
	   changeArmWidth(dW);
		
   }
   
   /**
    * Takes the top Block from the BlockPile at the current position and set it to 
    * the load variable
    */
   private void pickAtPosition()
   {
	   // Select the pile we're picking from
	   currentPile = piles[armWidth];
	   
	   // Ensure the picker is at the correct height
	   resetArmHeight(currentPile.getHeight());
	   
	   robot.pick();
	   load = currentPile.pop(); // The BlockPile class is basically a Stack wrapper.
	   grabberHeight -= load.getSize(); // the grabber can hit more things now.
	   heights[armWidth] = currentPile.getHeight(); // the pile is now shorter.
	  
   }
   
   /**
    * Firstly, drop the grabber to the height of the grabber (which includes the
    * current load. Then it takes the Block from the load variable and sets it 
    * on top of the BlockPile at the current position. Sets the load variable 
    * to the nullBlock.
    */
   private void dropAtPosition()
   {
	   // Select the pile we're dropping to
	   currentPile = piles[armWidth];
	   
	   // Ensure the picker is at the correct height
	   resetArmHeight(currentPile.getHeight());
	   
	   robot.drop();                        // this releases the load from the robot,
	   currentPile.push(load);              // places the load onto the pile,
	   grabberHeight += load.getSize();     // and then adjusts the effective height 
	   load = nullBlock;  				    // of the grabber height for a null load.
	   heights[armWidth] = currentPile.getHeight(); 
   }
   
  /**
   * This function moves the picker from wherever it is to the provided fromPos, picks up a
   * block, moves it to the provided toPos, and drops it there.
   * 
   * @param fromPos		an integer describing where in piles to find the desired pile to move 
   * 					a block from
   * @param toPos    	an integer describing where in piles to find the desired pile to move 
   * 					a block to
   */
   private void moveBlock(int fromPos, int toPos)
   {
	   System.out.format("moving block from %d to %d%n", fromPos, toPos);
	   armToPosition(fromPos);
       pickAtPosition();
       armToPosition(toPos);
       dropAtPosition();
   }
   
   /**
    * Using a for loop and lots of helper functions, move each block from the
    * source position to the target position.
    * 
    * @param blockHeights 		the array of blockHeights as passed by the user.
    */
   private void moveBlocksFromSourceToStackSimple(int [] blockHeights)
   {
	   for (int i = 0; i < blockHeights.length; i++)
	   {
		   moveBlock(sourcePos, targetPos);
	   }   
   }
   
   /**
    * This is just a helper method. Use this when you
    *     - you need to ensure that the robot is above a source pile without caring which, OR
    *     - picker is above a source pile, but you want it to move to the other one.
    * 
    * It asks 'am I at the source position already?', 
    * if it is, it returns the alternate source position (tempPos). If armWidth is
    * for any other position (targetPos or tempPos), defaulting to the sourcePos
    * is guaranteed to be what we need within the usage of this function.
    * 
    * @return        the other position that could be a source of blocks
    */
   private int otherSource()
   {
	   // 
	   return (armWidth == sourcePos) ? tempPos : sourcePos; 
   }
      
   
   /**
    *  This function looks at the pile that we're in, and then shifts it to the
    *  'other' source pile, which is the original source or maybe the temp pile.
    */
   private void shiftTopBlock()
   {
	   moveBlock(armWidth, otherSource());
	   armToPosition(otherSource());
   }
   
   /**
    * Locates a pile with a block of the desired size and moves there.
    * 
    * @param size   the desired size of a block.
    */
   private void findBlockOfSize(int size)
   {
	   // If the source pile doesn't have it, the temp pile will!
	   int blockPos = (piles[sourcePos].sizeSearch(size) > 0) ? sourcePos : tempPos;	   
	   
	   // Move to the place where we know there's a block of the right size.
 	   armToPosition(blockPos);
   }
 	   
   /**
    * This determines how many blocks we need to shift from a pile to get to the block
    * we want.
    * 
    * @param 	  size
    * @return     the depth of the block in the pile
    */
   private int getDepthOfBlock(int size)
   {
 	   currentPile = piles[armWidth];
	   return currentPile.sizeSearch(size);
   }
   
   /**
    * This shifts blocks from one pile to another enough times so that the desired block
    * is on top.
    * 
    * @param size  the size of the block we need
    */
   private void uncoverBlock(int size)
   {
       for (int i = 1; i < getDepthOfBlock(size); i++)
 	   {
 	       shiftTopBlock();      
 	   }
   }
  
   /**
    * Implements the searchAndMove method by iterating through the required array.
    * 
    * @param required	the order that blocks need to be stacked at the target pile.
    */
   private void moveBlocksInOrder(int required[])
   {
	   for (int size: required)
	   {
		   findBlockOfSize(size);
		   uncoverBlock(size);
	 	   moveBlock(armWidth, targetPos);	  
	   }
   } 
   
   /** This function implements the recursive solution for of the Hanoi Towers problem to shift
    *  the top nBlocks of blocks from a source pile to a target pile, with an auxiliary pile
    *  available to hold blocks to ensure that no blocks are placed on top of a smaller block.
    *  
    *  
    *  
    * Below is an example for 4 blocks in the initial source pile
    *                   
    *                     1 
    *                     2 
    *                     3
    * _____    _____    __4__   Start: target is position 1, source is position 10,
    *   1        9        10           and auxiliary is position 9
    *   T        A        S
    * 
    *                 
    *            1        
    *            2       
    * _____    __3__    __4__   the first shift: since we're moving the n-1 top blocks
    *   1        9        10 			to position 9, position 1 effectively functions
    *   A		 T        S		        as the auxiliary pile.  
    * 
    *
    *            1        
    *            2       
    * __4__    __3__    _____   the second move: just moves the bottom block of source
    *   1        9        10                     to the target pile
    * 
    * 
    *   1 
    *   2 
    *   3
    * __4__    _____    _____   the final shift: moves the pile from position 9  
    *   1        9        10                     (functional source) onto the 
    *   T        S        A						 block at the target position
    *   
    *   @param nBlocks    the number of blocks that need to be shifted from the source pile
    *   @param source     an integer for the position of the source pile
    *   @param source     an integer for the position of the auxiliary pile
    *   @param target     an integer for the position of the target pile
    */
   private void shift(int nBlocks, int source, int auxiliary, int target)
   {

	   if (nBlocks > 0) // Because if nBlocks is 0, we're not moving anything
	   {
		   // Move the top n-1 blocks of the source pile
		   shift(nBlocks - 1, source, target, auxiliary);
		   /* Move the the remaining block of the pile, which we can assume by construction
		      is the largest block not yet in the target pile. */
		   moveBlock(source, target);
		   /* Once that's done, it's just a matter of moving the entire pile at the auxillary
		    * position on top of the source pile. */
		   shift(nBlocks - 1, auxiliary, source, target);
	   }
   }
   

   public void control(int barHeights[], int blockHeights[], int required[], boolean ordered)
   {
   
	   setHeights(barHeights);
	   setBlockPositions(blockHeights);  
	   
	   
	   if (ordered) 
	   {   // This gets run for part E
		   shift(blockHeights.length, sourcePos, tempPos, targetPos);
	   }
	   else if (required.length == 0)
	   {   // For parts A-C
		   moveBlocksFromSourceToStackSimple(blockHeights);  
	   }
	   else 
	   {   // This gets run for part D
		   moveBlocksInOrder(required); 
	   }
   }
 
}  

