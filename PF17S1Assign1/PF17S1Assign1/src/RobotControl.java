import java.util.Arrays;

class RobotControl
 {
   private Robot r;
   private Block[] blocks;
   private Block load, nullBlock;
   private BlockPile targetPile, tempPile, sourcePile, currentPile;
   private BlockPile[] piles;
   private int[] heights;
   private int h, w, d, grabberH, maxHeightArm, maxHeightGrabber; 
   private int sourcePos, tempPos, targetPos;
   
   public RobotControl(Robot r)
   {
	   // setup the crane positions
       this.r = r;
       this.h = 2;
       this.w = 1;
       this.d = 0;
       this.grabberH = 1;
       
       this.maxHeightArm = 14;
       this.maxHeightGrabber = 14;
    		  
       this.piles = new BlockPile[11];
       
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
       
       
       nullBlock = new Block(0);
       this.load = nullBlock;
   }
   
   private void printStatus()
   {
	   //System.out.format("Status: H = %d W = %d D = %d G= %d%n", this.h, this.w, this.d, this.grabberH);
   }
   
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
   
   private void setBlockPositions(int[] blockHeights)
   {
	   // This call is when everything is at source (10)
	      
	   for (int h: blockHeights)
	   {
		   load = new Block(h);
		   piles[this.sourcePos].push(load);
	   }	 
	   this.heights[this.sourcePos] = piles[this.sourcePos].height;
   }
   
   private int getClearanceHeight()
   {
	   return MyMath.max(this.heights);
   }
      
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
   
   private void armToPosition(int p)
   {
	   // later add optional arguments for height, for now use max of 14
	   
	   // ensure that the arm is elevated before moving
	   this.resetArmHeight();
	   int dW = p - this.w; 
	   this.changeW(dW);
		
   }
   
   private void pickAtPosition(int p)
   {
	   currentPile = this.piles[p];
	   
	   int pileHeight = currentPile.height;
	   int drop = pileHeight - this.grabberH; //
	   
	   this.changeD(drop); // drop using the picker
	   System.out.println("picking...");
	   r.pick();
	   this.load = currentPile.pop();
	   this.grabberH -= this.load.size;
	   this.heights[p] = currentPile.height;
	   
   }
   
   private void dropAtPosition(int p)
   {
	   currentPile = this.piles[p];
	   
	   int drop = this.grabberH - currentPile.height;
	   System.out.format("Dropping by %d%n", drop);
	   this.changeD(-drop);
	   
	   r.drop();
	   currentPile.push(this.load);
	   this.grabberH += this.load.size;
	   this.load = this.nullBlock;
	   this.heights[p] = currentPile.height;

   }
   
   private void setHeights(int barHeights[])
   {
	   heights = new int[11]; // positions labeled 1-10, I'll follow same convention here
	   for (int i = 1; i < barHeights.length; i++)
	   {
		   // assumes bars get placed from 2, may not be true, but doesn't matter
		   heights[i+1] = barHeights[i];
	   }
	   heights[1] = 0; // nothing at target yet
	   heights[9] = 0;
	   heights[10] = 0; // these get updated with push and pops
   }
   
   private void moveBlocksFromSourceToStackSimple(int [] blockHeights)
   {
	   for (int h: blockHeights)
	   {
	       this.armToPosition(10);
	       this.pickAtPosition(10);
	       this.armToPosition(1);
	       this.dropAtPosition(1);
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
   
   private void shiftTopBlock()
   {
	   System.out.format("shifting from %d%n", this.w);
	   this.pickAtPosition(this.w);
	   this.moveToDifferentSourcePile();
	   this.dropAtPosition(this.w);
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
	   
	   System.out.println("ere?");
	   this.pickAtPosition(this.w);
	   this.armToPosition(this.targetPos);
	   System.out.format("dropping at %d%n", this.w);
	   this.dropAtPosition(this.w);
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
	   
	   if (required.length == 0) 
	       {this.moveBlocksFromSourceToStackSimple(blockHeights);}
	   
	   this.moveBlocksInOrder(blockHeights, required);
	   
	   
	  
   }
 
}  

