public class DEMO6 {
    public static void main(String[] args) {
        
        String s1 = "Hello";
        String s2 = "Hello";

        String s3 = new String("World");
        String s4 = new String("World");

        System.out.println(s1 == s2); // True bcz s1,s2 are stores in string pool.
        System.out.println(s3 == s4); // Flalse
        System.out.println(s3.equals(s4)); //true

        Dog dog = new Dog("Tommy");
    }
}

class Animal {

    String name;

    Animal(String name) {
        System.out.println("Animal constructor" + name);
        this.name = name;
    }
}

class Dog extends Animal {

    Dog(String name) {
        super(name);
        System.out.println("Dog constructor" + name);
    }
}