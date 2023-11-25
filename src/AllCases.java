import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
public class AllCases {
    private static List<String> outputList;
    public static void main(String[] args) throws IOException {
        File input=new File("resources/RoomToConvert");
        File[] files = input.listFiles();
        if (files!=null){
            for(File file: files){
                outputList =new ArrayList<>();
                List<String> inputList= Files.readAllLines(file.toPath());
                for (String inputString: inputList){
                    recursiveCreate(inputString);
                }
                File output=new File("resources/RoomList/"+file.getName());
                Files.write(output.toPath(),outputList);
            }
        }
    }
    public static void recursiveCreate(String string){
        if(string.contains(" ")){
            recursiveCreate(string.replaceFirst(" ",""));
            recursiveCreate(string.replaceFirst(" ","-"));
        } else if (string.contains(".")) {
            recursiveCreate(string.replaceFirst("\\.",""));
            recursiveCreate(string.replaceFirst("\\.","-"));
            outputList.add(string);
        } else{
            System.out.println(string);
            outputList.add(string);
        }
    }

}
