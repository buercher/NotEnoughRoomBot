import java.io.IOException;
public class Main {
    public static void main(String[] args) throws IOException {
        EPFL.scrap();
        System.out.println("EPFL done");
        FLEP.scrap();
        System.out.println("FLEP done");
        toJson.makeFile();
        System.out.println("Json Made");
    }
}
