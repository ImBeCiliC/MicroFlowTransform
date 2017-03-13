package transformation;

import java.io.File;

import javax.swing.JFileChooser;

public class OpenFile {
	// Declare fileChooser
	JFileChooser fileChooser = new JFileChooser();
	int validation = 0;
	
	/* Checks if a File is choosen if yes return selected File
	 * if no File is choosen print that no file is choosen
	*/
	public File Picked() throws Exception{
		File file = null;
		
		if(validation == JFileChooser.APPROVE_OPTION){
			return fileChooser.getSelectedFile();
		} else {
			System.out.println("No File selected!");
		}
		return file;
	}
}
