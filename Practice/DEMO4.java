
public class DEMO4 {
    public static void main(String[] args) {

    }
}

class Outer{

    int numOuter= 0;
    static int numOuterStatic = 1;


    class Inner{
        int num = 0;

        void print(){
            System.out.println(numOuter);
        }
    }

    static class InnerStatic{
        
        void print(){
            System.out.println(numOuterStatic);
        }
    }
}

/*
 * EngineeringStudent es1 = new EngineeringStudent("Rk", 28);
 * es1.name= "Rk";
 * es1.isEngineeringStudent= true;
 * es1.printStudentDetails();
 * class Student{
 * String name;
 * int age;
 * 
 * Student(String name, int age) {
 * this.name= name;
 * this.age= age;
 * }
 * }
 * 
 * class EngineeringStudent extends Student{
 * boolean isEngineeringStudent= true;
 * 
 * EngineeringStudent(String name, int age){
 * super(name, age);
 * }
 * 
 * public void printStudentDetails(){
 * System.out.println("My name is " + name + ". " +
 * "I am a engineering student.");
 * }
 * }
 * 
 * 
 * BankAccount bankAccount = new BankAccount();
 * bankAccount.deposit(100);
 * bankAccount.withdraw(95);
 * 
 * 
 * class BankAccount{
 * private double balance;
 * 
 * public void deposit(double deposit){
 * this.balance = this.balance + deposit;
 * bankBalance();
 * }
 * 
 * //Getters
 * public void withdraw(double withdraw){
 * if(withdraw > this.balance){
 * System.out.println("Insufficient funds.");
 * }else{
 * this.balance = this.balance - withdraw;
 * System.out.println("Withdrawal amount is " + withdraw + ". " +
 * "Your balance is " + this.balance);
 * }
 * }
 * 
 * public double bankBalance(){
 * System.out.println(balance);
 * return balance;
 * }
 * }
 */
