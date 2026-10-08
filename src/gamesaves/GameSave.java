package gamesaves;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class GameSave {
    public void
    public void Save(){
    Path path = Paths.get("filename.txt");
    String content = "Hello, World!\nWelcome to Java file handling.";

        try {
            // Creates the file and writes the text
            Files.writeString(path, content);
            System.out.println("File created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}