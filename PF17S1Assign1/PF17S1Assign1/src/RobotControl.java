import java.util.Arrays;

class RobotControl
 {
   private Robot r;
   private Block[] blocks;
   private Block load;
   private BlockPile targetPile, tempPile, sourcePile, currentPile;
   private int[] barHeights;
   private int h, w, d, grabberH, maxHeightArm, maxHeightGrabber; 
   private int sourcePos, tempPos, targetPos;
   
   public RobotControl(Robot r)
   {
	   // setup the crane positions
       this.r = r;
       this.h = 2;
       this.w = 1;
       this.d = 1;
       this.grabberH = 1;
       
       this.maxHeightArm = 14;
       this.maxHeightGrabber = 14;
    		  
       
       // make the piles where the Block's are stored
       this.targetPile = new BlockPile(1);
       this.tempPile = new BlockPile(9);
       this.sourcePile = new BlockPile(10);
       
       // save the class variables
       
       this.tempPos = 9;
       this.targetPos = 1;
       this.sourcePos = 10;
       
       this.load = null;
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
	   // This call is when everything is at source (10)
	   int nBlocks = blockHeights.length;
	      
	   for (int h: blockHeights)
	   {
		   load = new Block(h);
		   this.sourcePile.push(load);
	   }	 
   }
      
   private void armToSource()
   {
	   // later add optional arguments for height, for now use max of 14
	   
	   // ensure that the arm is elevated before moving
	   int dH = this.maxHeightArm - this.h;
	   int dW = 10 - this.w; 
	   int dD = -this.d;
	   this.changeD(dD);
	   this.changeH(dH);
	   this.changeW(dW);
		
   }
   
   private void armToTarget()
   {
	   // later add optional arguments for height, for now use max of 14

	   int dH = this.maxHeightArm - this.h;
	   int dW = 1 - this.w; 
	   this.changeH(dH);
	   this.changeW(dW);
		
   }
   
   private void pickAtPosition()
   {
	   if (this.w == 10) // position is source
	   {   
		   currentPile = sourcePile;
	   }else if (this.w == 9) // position is temp pile
	   {
		   currentPile = tempPile;
	   }else if (this.w == 1)
	   {
		   currentPile = targetPile;
	   }else
	   {
		   // raise an error
	   }
	   
	   int pileHeight = currentPile.height;
	   int drop = pileHeight - this.grabberH; //
	   
	   this.changeD(drop); // drop using the picker
	   r.pick();
	   this.load = currentPile.pop();
	   this.changeD(-drop); // return to previous height
   }
   
   private void dropAtPosition()
   {
	   if (this.w == 10) // position is source
	   {   
		   currentPile = sourcePile;
	   }else if (this.w == 9) // position is temp pile
	   {
		   currentPile = tempPile;
	   }else if (this.w == 1)
	   {
		   currentPile = targetPile;
	   }else
	   {
		   // raise an error
	   }
	   
	   int pileHeight = currentPile.height;
	   int drop = pileHeight + load.size - this.grabberH;
	   this.changeD(drop);
	   
	   r.drop();
	   currentPile.push(this.load);
	   this.load = null;
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
	   
	   this.setBlockPositions(blockHeights);  
	   this.barHeights = barHeights;
	   
	   for (int height: blockHeights)
	   {
	       this.armToSource();
	       this.pickAtPosition();
	       this.armToTarget();
	       this.dropAtPosition();
	   }
	  
   }
 
}  

