import java.util.Stack;
public class BlockPile {
	Stack<Block> stack;
	Stack<Integer> sizeStack;
	Block temp;
	int height, position;
	
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
		this.sizeStack.push(block.size); // TODO: remove this, work out how the search
										 // method works for Stacks
		this.height += block.size;
		block.top = this.height; // whenever a block is added to the stack, it needs this height
		block.position = this.position;
	}
	
	public Block peek()
	{
		return this.stack.peek();
	}
	
	public Block pop()
	{
		temp = this.stack.pop();
		this.height -= temp.size;
		this.sizeStack.pop();
		System.out.format("Removing block %d, now %d to %d%n", temp.size, this.sizeSearch(1), 1);
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

}
