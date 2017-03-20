import java.util.Collections;

 class RobotControl
 {
   private Robot r;
   private int[] blockTops, blockPositions, blockHeights;
   private int h, w, d, nBlocks; 
   
   public RobotControl(Robot r)
   {
       this.r = r;
       this.h = 2;
       this.w = 1;
       this.d = 1;
   }
   
   private void printStatus()
   {
	   System.out.format("Status: H = %d W = %d D = %d%n", this.h, this.w, this.d);
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
	   this.printStatus();
   }
   
      
   private void setBlockPositions(int[] blockHeights)
   {
	   // This call is when everything is at source
	   int nBlocks = blockHeights.length;
	   int[] blockTops = new int[nBlocks];
	   int[] blockPositions = new int[nBlocks];
	   blockTops[0] = blockHeights[0];
	   for (int i = 1; i < nBlocks; i++)
	   {
		   blockTops[i] = blockHeights[i] + blockTops[i-1];
		   blockPositions[i] = 10;
	   }
	   this.blockPositions = blockPositions;
	   this.blockTops = blockTops;
	   this.nBlocks = nBlocks;
			 
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
	   int blockIdx = 0;
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
	   int heightOfBlock = this.blockTops[blockIdx];
	   int drop = heightOfBlock - this.h + 2; //
	   System.out.format("Dropping by %d to %d%n", drop, heightOfBlock);
	   this.changeD(drop);
	   r.pick();
   }
   
   private void dropAtPosition()
   {
	   int blockIdx = this.topBlockAtPosition();
	   int heightOfBlock = this.blockTops[blockIdx];
	   int blockSize = this.blockHeights[blockIdx];
	   int drop = heightOfBlock - this.h + blockSize;
	   System.out.format("Dropping by %d%n", drop);
	   this.changeD(drop);
	   this.blockPositions[blockIdx] = 1;
	   this.blockTops[blockIdx] -= drop;
	   r.drop();
   }
   
   
   public void control(int barHeights[], int blockHeights[], int required[], boolean ordered)
   {

	   
	   
	   // The first past can be solved easily  with out any arrays as the height of bars and blocks are fixed.
	   // Use the method r.up(), r.down(), r.extend(), r.contract(), r.raise(), r.lower(), r.pick(), r.drop()
	   // The code below will cause first arm to be moved up, the second arm to the right and the third to be lowered. 
	   
	   // things to keep track of:
	   //     - h, w, d
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
	   
	   this.armToSource();
	   this.pickAtPosition();
	   this.armToTarget();
	   this.dropAtPosition();
	   this.armToSource();
	   this.pickAtPosition();
	   this.armToTarget();
	   this.dropAtPosition();
	   

	 
	   
	   
	   
	   
	   // Make a method which reads all of the bar heights
	   // Make a method that keeps track of all of the block positions
	   
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

