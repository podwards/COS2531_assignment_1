import java.util.Stack;

/**
 * Returns a BlockPile object that can then be used to store Block objects. 
 * 
 * The purpose is to make it easy to keep track of what gets put where. The 
 * height of the pile is kept up to date whenever a block gets added or removed
 * and the the search method is useful for the RobotControl to check whether a
 * desired block is in the pile.
 *
 * @param  size  the position of the pile.
 * @return      the BlockPile object without a defined position.
 */

public class BlockPile {
	Stack<Block> stack;
	Stack<Integer> sizeStack;
	Block temp;
	private int height, position;
	
	/** 
	 * This method initialises the pile. It creates a stack with nothing in it
	 * and so it has a zero height. Because the stack object I've implemented
	 * can't search for Blocks of a certain height (at least, I couldn't work 
	 * it out), the sizeStack object is used to search for blocks of a 
	 * certain size, and the stack and sizeStack are kept up to date together
	 * to allow the stack object to effectively be searched for a block of
	 * a specified size.
	 * 
	 * @param position	the position of the pile (1, 9 or 10).
	 */
	
	public BlockPile(int position)
	{
		this.stack = new Stack<Block>();
		this.sizeStack = new Stack<Integer>();
		this.height = 0;
		this.position = position;
	}
	
	/**
	 * Basically a wrapper for the stack objects own push method. The size stack
	 * is unfortunately necessary as I can't work out how to get the Stack to search
	 * using some sort of custom compare method.
	 * 
	 * @param block		the block that's getting added to this pile
	 */
	public void push(Block block)
	{
		this.stack.push(block);
		this.sizeStack.push(block.getSize()); /* TODO: remove this, work out how the search
											     method works for Stacks */
		this.height += block.getSize();
	}
	/**
	 * Basically a wrapper for the stack objects own peek method. It allows us to check
	 * what's on top of the pile. Currently unused, consider removing.
	 */
	public Block peek()
	{
		return this.stack.peek();
	}
	
	/**
	 * Basically a wrapper for the stack objects own pop method. The size stack
	 * is unfortunately necessary as I can't work out how to get the Stack to search
	 * using some sort of custom compare method. For now, both the Block stack and 
	 * Integer stack need to be used.
	 * 
	 * @return temp		the block that was at the top of the pile, and is no longer in the
	 * 					stack
	 */
	public Block pop()
	{
		temp = this.stack.pop();
		this.height -= temp.getSize();
		this.sizeStack.pop();
		System.out.format("Removing block %d, now %d to %d%n", temp.getSize(), this.sizeSearch(1), 1);
		return temp;
	}
	
	
	/**
	 * Finds how many spots down the block of a certain size is.
	 * 
	 * @param size		the desired size of a block
	 * @return			an integer. -1 if there's no block of this size, otherwise the 1-based 
	 * 					position from the top of the stack where the block is located.
	 */  
	public int sizeSearch(int size)
	{
		return this.sizeStack.search(size);
	}
	
	/**
	 * Getter method to get the current height of the block
	 * @return this.height		the current height, it's been updated withs push's and pop's
	 */
	public int getHeight()
	{
		return this.height;
	}

}
