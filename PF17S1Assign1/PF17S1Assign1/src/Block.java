/**
 * Returns a Block object that can then be placed in a BlockPile and moved 
 * around by a Robot via a RobotControl object. 
 * 
 * The purpose is to make it easy to keep track of what gets put where. When
 * it gets placed somewhere, it's position and top are updated appropriately, 
 * and the BlockPile object can take information from the block to update it's
 * own height.
 *
 * @param  size  the height of the block itself.
 * @return      the Block object without a defined position.
 */


public class Block {
    private int size, position, top;
    
    public Block(int size)
    {
    	this.position = 10; // default start position
    	this.size = size;
    }
        
    /**
     * Set the position that the block is now at.
     * 
     * @param p    an integer. Either 1,9 or 10 (target, temp or source)
     */
    public void setPosition(int p)
    {
    	this.position = p;
    }
    
    /**
     * Set the top of the block. This is the effective height of the block,
     * or, the sum of the heights of this block and all blocks below it at it's
     * new position.
     * 
     * @param t		The sum of the heights of this block and all blocks below it.
     */
    public void setTop(int t)
    {
    	this.top = t;
    }
    /**
     * Getter method for size
     * 
     * @return size		the size of the block
     */
    public int getSize()
    {
    	return this.size;
    }
    
    /**
     * Getter method for position
     * 
     * @return position		the position of the block
     */
    public int getPosition()
    {
    	return this.position;
    }
    
    /**
     * Getter method for top
     * 
     * @return top		the top of the block
     */
    public int getTop()
    {
    	return this.top;
    }
    
}
