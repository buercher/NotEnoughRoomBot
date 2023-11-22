import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        MainEPFL.main();
        System.out.println("EPFL done");
        MainFLEP.main();
        System.out.println("FLEP done");
        toJson.toJson();
        System.out.println("Json Made");
    }
}
