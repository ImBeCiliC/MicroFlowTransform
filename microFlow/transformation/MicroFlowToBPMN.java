package transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MicroFlowToBPMN {
	public static void controller(File file) throws FileNotFoundException{
		String bpmnString = "";
		bpmnString += createStartString();
		String[] rebuildedString = rebuildString(file);
		System.out.println(bpmnString);	
	}
	public static String[] rebuildString(File file) throws FileNotFoundException{
		String [] temp = new String[100];
		List<String> rebuildedString = new LinkedList<String>();
		@SuppressWarnings("resource")
		Scanner input = new Scanner(file);
		int count = 0;
		while(input.hasNext()){
			temp[count] = input.nextLine();
			count++;
		}
		for(int i = 0; i < count; i++){
			if(!temp[i].equals(temp[i + 1])){
				rebuildedString.add(temp[i]);
				System.out.println(temp[i]);
			}
		}
		return rebuildedString.toArray(temp);
	}
	
	public static String createStartString(){
		return	"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>" + System.lineSeparator()
				+ "<semantic:definitions id=\"_1276276944297\""
				+ "targetNamespace=\"http://www.trisotech.com/definitions/_1276276944297\""
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" "
				+ "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" "
				+ "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" "
				+ "xmlns:semantic=\"http://www.omg.org/spec/BPMN/20100524/MODEL\">" + System.lineSeparator();
	}
}
