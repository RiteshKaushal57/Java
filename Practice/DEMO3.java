package Practice;
public class DEMO3 {
    public static void main(String[] args) {

        Student s1 = new Student();
        s1.name = "RK";
        Student s2 = new Student();
        System.out.println(s1.name);
        System.out.println(s1.school);

        Student1 s = new Student1("R");
        
    }

}

class Student {
    String name;
    final String school = "APK";
}

class Student1 {
    String name;

    Student1(String name) {
        this.name = name;
    }
}
