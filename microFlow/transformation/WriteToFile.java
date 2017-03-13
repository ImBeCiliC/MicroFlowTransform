package transformation;

import java.io.IOException;
import java.io.PrintWriter;

public class WriteToFile {
	public static void writeToFile(String s, String filename, int identifier) throws Exception{
		OpenFile of = new OpenFile();
		of.validation = of.fileChooser.showSaveDialog(null);
		try{
			if(identifier == 0){
				PrintWriter writer = new PrintWriter(of.Picked()+".BPMN", "UTF-8");
				writer.print(s);
				writer.close();
			} else {
				PrintWriter writer = new PrintWriter(of.Picked()+".JSON", "UTF-8");
				writer.print(s);
				writer.close();
			}
		} catch(IOException e) {
			System.out.println("Couldn't transform from BPMN to JSON");
		}
	}
}
