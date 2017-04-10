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
	
	/* 
	 * This method initialises the pile. It creates a stack with nothing in it
	 * and so it has a zero height. Because the stack object I've implemented
	 * can't search for Blocks of a certain height (at least, I couldn't work 
	 * it out), the sizeStack object is used to search for blocks of a 
	 * certain size, and the stack and sizeStack are kept up to date together
	 * to allow the stack object to effectively be searched for a block of
	 * a specified size.
	 */
	
	public BlockPile(int position)
	{
		this.stack = new Stack<Block>();
		this.sizeStack = new Stack<Integer>();
		this.height = 0;
		this.position = position;
	}
	
	public void push(Block block)
	{
		this.stack.push(block);
		this.sizeStack.push(block.getSize()); // TODO: remove this, work out how the search
										 // method works for Stacks
		this.height += block.getSize();
		block.setTop(this.height); // whenever a block is added to the stack, it needs this height
		block.setPosition(this.position);
	}
	
	public Block peek()
	{
		return this.stack.peek();
	}
	
	public Block pop()
	{
		temp = this.stack.pop();
		this.height -= temp.getSize();
		this.sizeStack.pop();
		System.out.format("Removing block %d, now %d to %d%n", temp.getSize(), this.sizeSearch(1), 1);
		return temp;
	}
	
	public int sizeSearch(int size)
	{
		return this.sizeStack.search(size);
	}
	
	public boolean empty()
	{
		return this.stack.empty();
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public int getPosition()
	{
		return this.position;
		
	}

}
