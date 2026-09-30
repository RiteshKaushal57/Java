public class DEMO5 {
    public static void main(String[] args) {

        UpiPayment u1 = new UpiPayment();
        u1.processPayment();

        CardPayment c1 = new CardPayment();
        c1.processPayment();
        
    }
}

interface PaymentMethod{
    void processPayment();
    
}

class UpiPayment implements PaymentMethod{
    
    @Override 
    public void processPayment(){
        System.out.println("This is UpiPayment class.");
    }
}

class CardPayment implements PaymentMethod{

    @Override 
    public void processPayment(){
        System.out.println("This is CardPayment class.");
    }
}