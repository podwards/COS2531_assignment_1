class MyMath
{
	public static int max(int a, int b, int c)
    {
        return Math.max(Math.max(a,b),c);
    }
    public static int max(int a, int b, int c, int d)
    {
        return Math.max(Math.max(Math.max(a,b),c),d);
    }
    public static int max(int [] A)
    {
        int max = 0;
        for (int a : A)
        {
        	max = Math.max(a, max);
        }
        return max;
    }
    
    public static int min(int [] A)
    {
        int min = 0;
        for (int a : A)
        {
        	min = Math.min(a, min);
        }
        return min;
    }
    
    public static int maxInBounds(int [] A, int lowerBound, int upperBound)
    {
        int max = 0;
        int[] bounds = {lowerBound, upperBound};
        
        //bounds = (lowerBound > upperBound) ? [upperBound, lowerBound] : [lowerBound, upperBound];
        for (int i = min(bounds); i < max(bounds) + 1; i++)
        {
        	max = Math.max(A[i], max);
        }
        return max;
    }
}

