import java.util.Stack;
public class BlockPile {
	Stack<Block> stack;
	Block temp;
	int height, position;
	
	public BlockPile(int position)
	{
		this.stack = new Stack<Block>();
		this.height = 0;
		this.position = position;
	}
	
	public void push(Block block)
	{
		this.stack.push(block);
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
		return temp;
	}
	
	public int search(Block block)
	{
		return this.stack.search(block);
	}
	
	public boolean empty()
	{
		return this.stack.empty();
	}

}
