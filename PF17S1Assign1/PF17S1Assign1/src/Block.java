
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
