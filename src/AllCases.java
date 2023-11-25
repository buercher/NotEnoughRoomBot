import SearchingRoom.FolderCreation;
import SearchingRoom.TestEPFL;
import SearchingRoom.TestFLEP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllCases {
    private static List<String> outputList;
    public static void main(String[] args) throws IOException {
        File input=new File("resources/RoomToConvert");
        File[] files = input.listFiles();
        if (files!=null){
            for(File file: files){
                outputList =new ArrayList<>();
                List<String> inputset= Files.readAllLines(file.toPath());
                for (String inputString: inputset){
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
