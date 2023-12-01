import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class AllCases {

    private static List<String> outputList;
    private static int inputLength=0;
    private static int outputLength=0;

    private AllCases(){}

    public static void main(String[] args) throws IOException {
        File roomChecking = new File("resources/RoomList");
        if (!roomChecking.exists()) {
            if (!roomChecking.mkdir()) {
                throw new IOException("Failed to create folder '" + roomChecking.getPath()+"'");
            }
        }
        Map<Integer,String> count= new TreeMap<>();
        Map<Integer,String> count2= new TreeMap<>();
        Map<Double,String> count3= new TreeMap<>();
        File input=new File("resources/RoomToConvert");
        File[] files = input.listFiles();
        if (files!=null){
            for(File file: files){

                outputList =new ArrayList<>();

                List<String> inputList= Files.readAllLines(file.toPath());

                inputLength+=inputList.size();
                for (String inputString: inputList){
                    recursiveDash(inputString);
                }
                outputLength+=outputList.size();
                count.put(outputList.size(),file.getName());
                count2.put(inputList.size(),file.getName());
                count3.put((double)outputList.size()/(double)inputList.size(),file.getName());
                File output=new File("resources/RoomList/"+file.getName());
                Files.write(output.toPath(),outputList);
            }
        }
        System.out.println("Nombre de salles avant: "+inputLength);
        System.out.println("Nombre de salles après: "+outputLength);
        System.out.println(count);
        System.out.println(count2);
        System.out.println(count3);
    }

    public static void recursiveDash(String string){
        if(string.contains("-")){
            recursiveDash(string.replaceFirst("-",""));
        }
        recursivePoint(string);
    }

    public static void recursivePoint(String string){
        if (string.contains(".")) {
            recursivePoint(string.replaceFirst("\\.",""));
            recursivePoint(string.replaceFirst("\\.","-"));
        }
        recursiveUnderScore(string);

    }
    public static void recursiveUnderScore(String string){
        if (string.contains("_")) {
            recursiveUnderScore(string.replaceFirst("_",""));
            recursiveUnderScore(string.replaceFirst("_","-"));
        }
        recursiveSpace(string);
    }

    public static void recursiveSpace(String string){
        if(string.contains(" ")){
            recursiveSpace(string.replaceFirst(" ",""));
            recursiveSpace(string.replaceFirst(" ","-"));
        }
        else{
            outputList.add(string);
        }
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
    }
}
