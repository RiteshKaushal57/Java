public class DEMO6 {
    public static void main(String[] args) {
        
        String s1 = "Hello";
        String s2 = "Hello";

        String s3 = new String("World");
        String s4 = new String("World");

        System.out.println(s1 == s2);
        System.out.println(s3 == s4); // Flalse
        System.out.println(s3.equals(s4)); //true
    }
}
