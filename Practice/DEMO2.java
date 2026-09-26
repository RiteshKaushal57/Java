package Practice;
public class DEMO2 {
    public static void main(String[] args) {

        
        int x = 5;
        System.out.println("Before: " + x);

        changeValue(x);

        System.out.println("After: " + x);
    }

    static void changeValue(int num) {
        num = 100;
    }

}




/*
Create a class Product with fields name, price, quantity. Add a method calculateTotalValue() that returns price * quantity. Create 3 products, find and print the one with the highest total value.


        Product product1 = new Product("A", 3, 4);
        Product product2 = new Product("B", 4, 7);
        Product product3 = new Product("C", 7, 2);

        Product[] productArray = {product1, product2, product3};

        Product highestProduct = productArray[0];
        double highestValue = productArray[0].calculateTotalValue();

        for (int i = 1; i < productArray.length; i++) {
            double currentValue = productArray[i].calculateTotalValue();
            if (currentValue > highestValue) {
                highestValue = currentValue;
                highestProduct = productArray[i];
            }
        }

        System.out.println("Highest value product is " + highestProduct.name + " with total value " + highestValue);
    

class Product {
    String name;
    int price;
    int quantity;

    Product(String name, int price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    double calculateTotalValue(){
        return price * quantity;
    }
}
Create a class Circle with field radius. Add two methods: calculateArea() and calculatePerimeter(). Create an object, call both methods, and print results.

Circle circleArea = new Circle(7);
        Circle circleParameter = new Circle(7);
         circleArea.calculateArea();
         circleParameter.calculateParameter();


class Circle {
    int radius;

    Circle(int radius){
        this.radius= radius;
    }

    void calculateArea(){
        double area = (int)(3.141 * radius * radius);
        System.out.println("Area of the cricle is " + area);
    }

    void calculateParameter(){
        double parameter = (int)(2 * 3.141 * radius);
        System.out.println("parameter of the cricle is " + parameter);
    }

}


 * 
 * Create a class Movie with fields title, genre, rating. Create an array of 5
 * Movie objects, then write a loop to print only the movies with rating above
 * 7.
 * 
 * 
 * Movie movie1 = new Movie("A", "A", 3);
 * Movie movie2 = new Movie("B", "B", 2);
 * Movie movie3 = new Movie("C", "C", 7);
 * Movie movie4 = new Movie("D", "D", 9);
 * Movie movie5 = new Movie("E", "E", 4);
 * 
 * Movie movieArray [] = {movie1, movie2, movie3, movie4, movie5};
 * 
 * for (int i = 0; i < movieArray.length; i++) {
 * if(movieArray[i].rating > 7){
 * System.out.println(movieArray[i].title);
 * }
 * }
 * 
 * class Movie {
 * String title;
 * String genre;
 * int rating;
 * 
 * Movie(String title, String genre, int rating){
 * this.title= title;
 * this.genre= genre;
 * this.rating= rating;
 * }
 * 
 * }
 * 
 * 
 * 
 * class BankAccount{
 * String accountHolder;
 * int balance;
 * 
 * BankAccount(String accountHolder, int balance){
 * this.accountHolder= accountHolder;
 * this.balance= balance;
 * }
 * 
 * // What was asked — method itself prints
 * void showBalance(){
 * System.out.println("Bank balance of " + accountHolder + " is " + balance);
 * }
 * }
 * 
 * BankAccount riteshAccount= new BankAccount("Rtesh Kaushal", 100000000);
 * BankAccount kaminiAccount= new BankAccount("Kamini Kaushal", 100000000);
 * 
 * riteshAccount.showBalance();
 * kaminiAccount.showBalance();
 * 
 * 
 * 
 * Student s1 = new Student("Ritesh Kaushal", 28, 01);
 * Student s2 = new Student("Kamini Kaushal", 30, 02);
 * 
 * s1.print();
 * s2.print();
 * 
 * }
 * 
 * // Correct name convention is:
 * 1. Student
 * 2. MyCar
 * 3. BookDetails
 * }
 * 
 * 
 * class Student{
 * String name;
 * int age;
 * int rollNo;
 * 
 * Student (String name, int age, int rollNo){
 * this.name = name;
 * this.age= age;
 * this.rollNo= rollNo;
 * }
 * 
 * void print(){
 * System.out.println("Hi. My name is " + name + "." + " I am " + age +
 * " years old. " + "My roll number is " + rollNo + ".");
 * }
 * 
 * 
 * 
 * Create a class Employee with fields name, salary, department. Create 3
 * objects representing 3 different employees, store them in an array of type
 * Employee[], and print all their details using a loop.
 * 
 * class Employee{
 * String name;
 * int salary;
 * String department;
 * 
 * Employee(String name, int salary, String department){
 * this.name= name;
 * this.salary= salary;
 * this.department= department;
 * }
 * }
 * 
 * 
 * Employee employee1= new Employee("Ritesh", 10000000, "Developer");
 * Employee employee2= new Employee("Arjun", 10000000, "Trader");
 * Employee employee3= new Employee("Kamini", 10000000, "Agriculture");
 * 
 * Employee employeeArray []= new Employee[3];
 * employeeArray[0]= employee1;
 * employeeArray[1]= employee2;
 * employeeArray[2]= employee3;
 * 
 * for (int i = 0; i < employeeArray.length; i++) {
 * System.out.println(employeeArray[i].name);
 * System.out.println(employeeArray[i].salary);
 * System.out.println(employeeArray[i].department);
 * }
 * 
 * Create a class Rectangle with fields length and width. Add a method
 * calculateArea() that returns the area. Create an object and call the method
 * to print the area.
 * 
 * class Rectangle {
 * int length;
 * int width;
 * 
 * Rectangle(int length, int width){
 * this.length= length;
 * this.width= width;
 * }
 * 
 * int calculateArea(){
 * return length * width;
 * }
 * 
 * }
 * 
 * Rectangle rectangle1 = new Rectangle(5, 5);
 * System.out.println(rectangle1.calculateArea());
 * 
 */