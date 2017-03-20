import java.util.Arrays;
import java.util.Collections;

 class RobotControl
 {
   private Robot r;
   private int[] blockTops, blockPositions, blockHeights;
   private int h, w, d, nBlocks, grabberH, currentLoad; 
   
   public RobotControl(Robot r)
   {
       this.r = r;
       this.h = 2;
       this.w = 1;
       this.d = 1;
       this.grabberH = 1;
       this.currentLoad = -1; // i.e no block being carried.
   }
   
   private void printStatus()
   {
	   System.out.format("Status: H = %d W = %d D = %d G= %d%n", this.h, this.w, this.d, this.grabberH);
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
	   // This call is when everything is at source
	   int nBlocks = blockHeights.length;
	   int[] blockTops = new int[nBlocks];
	   int[] blockPositions = new int[nBlocks];
	   blockPositions[0] = 10;
	   blockTops[0] = blockHeights[0];
	   for (int i = 1; i < nBlocks; i++)
	   {
		   blockTops[i] = blockHeights[i] + blockTops[i-1];
		   blockPositions[i] = 10;
	   }
	   this.blockPositions = blockPositions;
	   this.blockTops = blockTops;
	   this.nBlocks = nBlocks;
	   System.out.format("positions: %s%n", Arrays.toString(this.blockPositions));
	   System.out.format("tops: %s%n", Arrays.toString(this.blockTops));

			 
   }
      
   
   private void armToSource()
   {
	   // later add optional arguments for height, for now use max of 14
	   
	   // ensure that the arm is elevated before moving
	   int dH = 14 - this.h;
	   int dW = 10 - this.w; 
	   int dD = -this.d;
	   this.changeD(dD);
	   this.changeH(dH);
	   this.changeW(dW);
		
   }
   
   private void armToTarget()
   {
	   // later add optional arguments for height, for now use max of 14
	   int dH = 14 - this.h;
	   int dW = 1 - this.w; 
	   this.changeH(dH);
	   this.changeW(dW);
		
   }
   
   private int blockHeightAtPosition()
   {
	   int heightOfBlock = 0;
	   for (int i = 0; i < this.nBlocks; i++)
	   {
	       if (this.blockPositions[i] == this.w) {
	    	   heightOfBlock = Math.max(heightOfBlock, this.blockTops[i]);
	       }
	    	   
	   }
	   return heightOfBlock;
   }
   
   private int topBlockAtPosition()
   {
	   // Todo: combine this with blockHeightAtPosition, leaving to keep things
	   //       explicit.
	   int blockIdx = -1;
	   int maxHeight = 0;
	   for (int i = 0; i < this.nBlocks; i++)
	   {
	       if (this.blockPositions[i] == this.w) {
	    	   if (this.blockTops[i] > maxHeight)
	    	   {
	    		   maxHeight = this.blockTops[i];
	    		   blockIdx = i;
	    	   }
	       }
	   }
	   return blockIdx;
   }
   
   private void pickAtPosition()
   {
	   int blockIdx = this.topBlockAtPosition();
	   if (blockIdx == -1)
	   {
		   System.err.format("There's nothing to pick up!");
	   }
	   this.currentLoad = blockIdx; 
	   int heightOfBlock = this.blockTops[blockIdx];
	   int drop = heightOfBlock - this.grabberH; //
	   System.out.format("Taking block %d%n", blockIdx);
	   System.out.format("Dropping by %d to %d%n", drop, heightOfBlock);
	   this.changeD(drop);
	   r.pick();
	   this.changeD(-drop);

   }
   
   private void dropAtPosition()
   {
	   int blockIdx = this.topBlockAtPosition();
	   int heightOfPile;
	   if (blockIdx == -1)
	   {
		   heightOfPile = 0;
	   }
	   else
	   {
		   heightOfPile = this.blockTops[blockIdx];
	   }
	   int droppedBlock = this.currentLoad;
	   System.out.format("Top block is %d and it's height is %d%n", blockIdx, heightOfPile);
	   System.out.format("%s%n", Arrays.toString(this.blockHeights));
	   int heightOfBlock = this.blockHeights[droppedBlock];
	   System.out.format("Dropping a %d block from %d onto a pile %d high%n", heightOfBlock, this.grabberH, heightOfPile);
	   int drop = heightOfBlock + heightOfPile - this.grabberH;
	   this.changeD(drop);
	   
	   this.blockPositions[droppedBlock] = 1;
	   this.blockTops[droppedBlock] = heightOfBlock + heightOfPile;
	   r.drop();
	   this.currentLoad = -1;
   }
   
   
   public void control(int barHeights[], int blockHeights[], int required[], boolean ordered)
   {
 
	   
	   // things to keep track of:
	   //     - h, w, d, grabberH (height of arm, expansion width, drop of grabber, and the
	   //       the grabber height)
	   //     - currentLoad
	   
	   // Use a series of calls to get blocks and move them:
	   //    - armToSource: this will move arm ensuring that crane doesn't hit bars to
	   //                   the source position.
	   //    - armToTemp: similar
	   //    - armToTarget: similar
	   //    - lowerToPosition: this will lower the arm of the crane such that the 
	   //                       arm stops at the drop point considering the currentLoad
	   
	   this.printStatus();
	   this.blockHeights = blockHeights;
	   this.setBlockPositions(blockHeights);
	   
	   // move the top block to the target
	   
	   for (int i = 0; i < this.nBlocks; i++)
	   {  
		   this.armToSource();
		   this.pickAtPosition();
		   this.armToTarget();
		   this.dropAtPosition();
	   }
	   
	   
	   // Part B requires you to access the array barHeights passed as argument as the robot arm must move
	   // over the bars
	   
	     
	   
	   
	   // The third part requires you to access the arrays barHeights and blockHeights 
	   // as the heights of bars and blocks are allowed to vary through command line arguments
	   

	   
	   
	   // The fourth part allows the user  to specify the order in which bars must 
	   // be placed in the target column. This will require you to use the use additional column
	   // which can hold temporary values
	   

	   
	   
	   
	   // The last part requires you to write the code to move from source column to target column using
	   // an additional temporary column but without placing a larger block on top of a smaller block 
	   
   }
 
}  

