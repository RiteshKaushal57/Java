package Project;

public class BankAccountProject {
    public static void main(String[] args) {

        BankAccount bankAccount1 = new BankAccount("Ritesh Kaushal", 1000000);
        BankAccount bankAccount2 = new BankAccount("Kamini Kaushal", 10000000);

        bankAccount2.transfer(23782, bankAccount1);
        
    }
}

class BankAccount{
    String name;
    private  double bankBalance;

    BankAccount(String name, double bankBalance){
        this.name= name;
        this.bankBalance= bankBalance;
    }

    void deposit( double deposit){
        if(deposit > 0){
            bankBalance += deposit;
            System.out.println("You deposited " + deposit + ". " + "Your bank balance is " + bankBalance);
            printDetails();
        }else {
            System.err.println("Add money greater than 0");
        }
    }

    void withdrawal (double withdrawal){
        if(withdrawal <= this.bankBalance && withdrawal > 0){
            this.bankBalance -= withdrawal;
            System.out.println("You withdrew " + withdrawal + ". " + "Your bank balance is " + bankBalance);
            printDetails();
            
        }else{
            System.err.println("Insufficient funds or add appropriate amount.");
        }
    }

    void transfer(double money, BankAccount receiverBankAccount ){
        if(money <= this.bankBalance && money > 0){
            this.bankBalance -= money;
            receiverBankAccount.bankBalance += money;
            System.out.println("The " + money + " is transferred to " + receiverBankAccount.name);
            System.out.println("Your bank balance is " + this.bankBalance);
        }else{
            System.err.println("Transfer failed");
        }
    }


    void printDetails(){
        System.out.println("Customer name is " + name + ". " + "Bank balance is " + bankBalance + ".");
    }
}