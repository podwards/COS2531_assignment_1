
 class RobotControl
 {
   private Robot r;
   
   public RobotControl(Robot r)
   {
       this.r = r;
   }

   public void control(int barHeights[], int blockHeights[], int required[], boolean ordered)
   {

	   
	   
	   // The first past can be solved easily with out any arrays as the height of bars and blocks are fixed.
	   // Use the method r.up(), r.down(), r.extend(), r.contract(), r.raise(), r.lower(), r.pick(), r.drop()
	   // The code below will cause first arm to be moved up, the second arm to the right and the third to be lowered. 
	   
	   r.up();  	// move the first arm up by one unit
	   r.extend();	// move the second arm to the right by one unit
	   r.lower();	// lowering the third arm by one unit
	   
	   
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

