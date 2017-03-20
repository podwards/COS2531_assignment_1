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
}

