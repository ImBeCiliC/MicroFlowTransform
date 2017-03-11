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
		bpmnString += createBPMN(rebuildedString);
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
			}
		}
		
		return rebuildedString.toArray(new String[rebuildedString.size()]);
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
	
	protected static String createBPMN(String[] rebuildedString){
		String result = "";
		String idTask = "_1-";
		int idCounterTask = 0;
		String idSequenceFlow = "_2-";
		int idCounterSequence = 0;
		result += "<semantic:process isExecutable=\"false\" id=\""+ idTask +"\">" + System.lineSeparator()
					+ "\t<semantic:startEvent name=\"\" id=\"StartProcess\">" + System.lineSeparator()
					+ "\t\t<semantic:outgoing>"+idSequenceFlow + idCounterSequence+"</semantic:outgoing>" + System.lineSeparator()
					+ "\t</semantic:startEvent>" + System.lineSeparator();
		for(int i = 0; i < rebuildedString.length; i++){
			if(rebuildedString[i] != null){
				result += "\t<semantic:task completionQuantity=\"1\" isForCompensation=\"false\" startQuantity=\"1\" name=\""+ rebuildedString[i] +"\" id=\""+ idTask + idCounterTask++ +"\">" + System.lineSeparator()
							+ "\t\t<semantic:incoming>"+ idSequenceFlow + (idCounterSequence++) + "</semantic:incoming>" + System.lineSeparator()
							+ "\t\t<semantic:outgoing>"+ idSequenceFlow + idCounterSequence +"</semantic:outgoing>" + System.lineSeparator()
							+ "\t</semantic:task>" + System.lineSeparator();
			}
		}
		result +="\t<semantic:endEvent name=\"\" id=\"EndProcess\">" + System.lineSeparator()
					+ "\t\t<semantic:incoming>"+idSequenceFlow + idCounterSequence+"</semantic:incoming>" + System.lineSeparator()
					+ "\t</semantic:endEvent>" + System.lineSeparator();
		
		int idCounterSequenceFlow = 0;
		result += "\t<semantic:sequenceFlow sourceRef=\"StartProcess\" targetRef=\""+ idSequenceFlow + idCounterSequenceFlow++ + "\" name=\"\" id=\"" + idTask + idCounterTask++ +"\"/>" + System.lineSeparator();
		for(int i = 0; i < idCounterSequence - 1; i++){
			result += "\t<semantic:sequenceFlow sourceRef=\"" + idSequenceFlow + idCounterSequenceFlow++ + "\" targetRef=\""+ idSequenceFlow + idCounterSequenceFlow + "\" name=\"\" id=\"" + idTask + idCounterTask++ +"\"/>" + System.lineSeparator();
		}
		result += "\t<semantic:sequenceFlow sourceRef=\"" + idSequenceFlow + idCounterSequenceFlow++ 
					+"\" targetRef=\"EndProcess\" name=\"\" id=\"" + idTask + idCounterTask++ +"\"/>" + System.lineSeparator()
					+"</semantic:process>" + System.lineSeparator();
		return result;
	}
}
