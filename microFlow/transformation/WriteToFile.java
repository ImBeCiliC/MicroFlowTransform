package transformation;

import java.io.IOException;
import java.io.PrintWriter;

public class WriteToFile {
	public static void writeToFile(String s, String filename){
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.print(s);
			writer.close();
			
		} catch(IOException e) {
			System.out.println("Couldn't transform from BPMN to JSON");
		}
	}
}
