public class pruebaRecursiva {
    public static int pasos(int n)
    {
        if (n >= 8) {
            return 0;
        }
        
        System.out.println("-> " + n);
        return 1 + pasos(n + 1);
    }
    
    public static void main(String[] args) {
        
        System.out.println(pasos(1));
    }
}
