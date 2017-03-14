package transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class WorkFlowToBPMN {
	// Starts every task in the correct order
	public static void controller(File file) throws Exception{
		String bpmnString = "";
		String filename ="MicroFlowToBPMN.bpmn";
		bpmnString += createStartString();
		String[] rebuildedString = rebuildString(file);
		bpmnString += createBPMN(rebuildedString);
		System.out.println(bpmnString);
		WriteToFile.writeToFile(bpmnString, filename, 0);
	}
	
	// Reads the String from the File and minimize a group of same string to one string
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
	
	// First lines of the BPMN 
	public static String createStartString(){
		return	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator()
				+ "<bpmn:definitions id=\"Definition_1\" "
				+ "targetNamespace=\"http://bpmn.io/schema/bpmn\" "
				+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" "
				+ "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" "
				+ "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" "
				+ "xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\">" + System.lineSeparator();
	}
	
	// Generates the Core BPMN File
	protected static String createBPMN(String[] rebuildedString){
		// Variable Declaration
		String result = "";
		String idTask = "_1-";
		int idCounterTask = 0;
		String idSequenceFlow = "_2-";
		int idCounterSequence = 0;
		
		// Generating Process and StartEvent => default
		result += "<bpmn:process isExecutable=\"false\" id=\""+ idTask +"\">" + System.lineSeparator()
					+ "\t<bpmn:startEvent name=\"\" id=\"StartProcess\">" + System.lineSeparator()
					+ "\t\t<bpmn:outgoing>"+idSequenceFlow + idCounterSequence+"</bpmn:outgoing>" + System.lineSeparator()
					+ "\t</bpmn:startEvent>" + System.lineSeparator();
		
		// Generating Task from String Array
		for(int i = 0; i < rebuildedString.length; i++){
			if(rebuildedString[i] != null){
				result += "\t<bpmn:task completionQuantity=\"1\" "
							+ "isForCompensation=\"false\" startQuantity=\"1\" "
							+ "name=\""+ rebuildedString[i] +"\" id=\""+ idTask + idCounterTask++ +"\">" 
							+ System.lineSeparator()
							+ "\t\t<bpmn:incoming>"+ idSequenceFlow + (idCounterSequence++) 
							+ "</bpmn:incoming>" + System.lineSeparator()
							+ "\t\t<bpmn:outgoing>"+ idSequenceFlow + idCounterSequence 
							+"</bpmn:outgoing>" + System.lineSeparator()
							+ "\t</bpmn:task>" + System.lineSeparator();
			}
		}
		
		// Generating EndEvent => default
		result +="\t<bpmn:endEvent name=\"\" id=\"EndProcess\">" + System.lineSeparator()
					+ "\t\t<bpmn:incoming>"+idSequenceFlow + idCounterSequence+"</bpmn:incoming>" + System.lineSeparator()
					+ "\t</bpmn:endEvent>" + System.lineSeparator();
		
		// Generating SequenceFlow from counted Tasks
		int idCounterSequenceFlow = 0;
		int idCounterT = 0;
		result += "\t<bpmn:sequenceFlow sourceRef=\"StartProcess\" "
				+ "targetRef=\""+ idTask  + idCounterT + "\" name=\"\" "
				+ "id=\"" + idSequenceFlow + idCounterSequenceFlow++ +"\"/>" 
				+ System.lineSeparator();
		
		for(int i = 0; i < idCounterSequence - 1; i++){
			result += "\t<bpmn:sequenceFlow sourceRef=\"" + idTask  + idCounterT++ + "\" "
						+ "targetRef=\""+ idTask  + idCounterT + "\" name=\"\" "
						+ "id=\"" + idSequenceFlow + idCounterSequenceFlow++ +"\"/>" 
						+ System.lineSeparator();
		}
		result += "\t<bpmn:sequenceFlow sourceRef=\"" + idTask  + idCounterT++
					+"\" targetRef=\"EndProcess\" name=\"\" "
					+ "id=\"" + idSequenceFlow + idCounterSequenceFlow++ +"\"/>" 
					+ System.lineSeparator()
					+"</bpmn:process>" + System.lineSeparator();
		
		// Generating Diagram of the Workflow
		// Declaration of Coordinates
		int x = 200, y = 200;
		result += "<bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">" + System.lineSeparator()
					+ "\t<bpmndi:BPMNPlane id=\"BPMNPlane_1\" "
					+ "bpmnElement=\"" + idTask +"\">" + System.lineSeparator();
		
		// Generating StartEvent
		result += "\t\t<bpmndi:BPMNShape id=\"StartEvent_StartProcess\" "
					+ "bpmnElement=\"StartProcess\">" + System.lineSeparator()
					+ "\t\t\t<dc:Bounds x=\""+ (x + 50) +"\" y=\"" + (y+22) + "\" "
					+ "width=\"36\" height=\"36\"/>" + System.lineSeparator()
					+ "\t\t</bpmndi:BPMNShape>" + System.lineSeparator();
		// Generating Tasks
		for(int i=0; i < idCounterTask; i++){
			result += "\t\t<bpmndi:BPMNShape id=\"Task_"+ idTask + i +"\" "
						+ "bpmnElement=\"" + idTask + i + "\">" + System.lineSeparator()
						+ "\t\t\t<dc:Bounds x=\""+ x * (i + 2) +"\" y=\"" + y + "\" "
						+ "width=\"100\" height=\"80\"/>" + System.lineSeparator()
						+ "\t\t</bpmndi:BPMNShape>" + System.lineSeparator();
		}
		// Generating EndEvent
		result += "\t\t<bpmndi:BPMNShape id=\"EndEvent_EndProcess\" bpmnElement=\"EndProcess\">" + System.lineSeparator()
					+ "\t\t\t<dc:Bounds x=\""+ x * (idCounterTask + 2) +"\" y=\"" + (y+22) + "\" "
					+ "width=\"36\" height=\"36\"/>" + System.lineSeparator()
					+ "\t\t</bpmndi:BPMNShape>" + System.lineSeparator();
		// Generating Edge
		for(int i = 0; i < idCounterSequenceFlow; i++){
			result += "\t\t<bpmndi:BPMNEdge id=\"SequenceFlow"+ idSequenceFlow + i +"\" "
						+ "bpmnElement=\"" + idSequenceFlow + i + "\">" + System.lineSeparator();
			if(i == 0){
				result += "\t\t\t<di:waypoint xsi:type=\"dc:Point\" "
							+ "x=\"" + (x+36+50) + "\" y=\"" + (y+40) + "\"/>" + System.lineSeparator()
							+ "\t\t\t<di:waypoint xsi:type=\"dc:Point\" "
							+ "x=\"" + x * (i + 2) + "\" y=\"" + (y+40) + "\"/>" + System.lineSeparator();
			} else {
				result += "\t\t\t<di:waypoint xsi:type=\"dc:Point\" "
							+ "x=\"" + ((x * (i + 1)) + 100) + "\" y=\"" + (y+40) + "\"/>" + System.lineSeparator()
							+ "\t\t\t<di:waypoint xsi:type=\"dc:Point\" "
							+ "x=\"" + x * (i + 2) + "\" y=\"" + (y+40) + "\"/>" + System.lineSeparator();
			}
			result +=  "\t\t</bpmndi:BPMNEdge>" + System.lineSeparator();
		}
				
		// Close all Tags
		result += "\t</bpmndi:BPMNPlane>" + System.lineSeparator()
					+ "</bpmndi:BPMNDiagram>" + System.lineSeparator()
					+ "</bpmn:definitions>" + System.lineSeparator();
		return result;
	}
}
