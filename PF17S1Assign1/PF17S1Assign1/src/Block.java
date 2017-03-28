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
    int size, position, top;
    
    public Block(int size)
    {
    	this.position = 10; // default start position
    	this.size = size;
    }
    
    public void setPosition(int p)
    {
    	this.position = p;
    }
    
    public void setTop(int t)
    {
    	this.top = t;
    }
    
}
