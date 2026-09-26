package Practice;
public class DEMO3 {
    public static void main(String[] args) {
        
        Student s1 = new Student();
        s1.name= "RK";
        Student.school= "apk";
        Student s2 = new Student();
        System.out.println(s1.name);
        System.out.println(s1.school);
        
    }

    
}

class Student{
    String name;
    final String school= "APK";

    
}