package transformation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.Script;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

public class MicroFlow {
	static int nullcounter = 0;
	public static void main(String[] args) {
  		File file = new File("../MicroFlowTransform/microFlow/Data/"+args[0]);
  		createCollectionFromBpmn(file, args);
	}
	
	public static Collection<SequenceFlow> createCollectionFromBpmn(File file, String [] args){
		String[] filen = file.getName().split("\\.");
		String filename = filen[0] + ".json";
		String jsonString = null;
		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
		
		
		ModelElementType endEventInit = modelInstance.getModel().getType(EndEvent.class);
		Collection<ModelElementInstance> endEventInstance = modelInstance.getModelElementsByType(endEventInit);
		EndEvent [] endEvent = endEventInstance.toArray(new EndEvent[0]);
		
		Collection<SequenceFlow> sequenceFlowInstance = endEvent[0].getParentElement().getChildElementsByType(SequenceFlow.class);
		SequenceFlow [] sequenceFlowArray = sequenceFlowInstance.toArray(new SequenceFlow[0]);
		
		String [][] result = createStringArray(sequenceFlowArray, modelInstance);
		
		jsonString = createStartString(sequenceFlowArray, endEvent);
		jsonString += createConstraints(result, args);
		System.out.println(jsonString);
		
		writeToFile(jsonString, filename);
		return sequenceFlowInstance;
	}
	
	//Creates the StartString for the JSON-File
	//
	// Wenn mehrere EndEvents eintreffen k�nnen alle vorknoten als endServiceType reinschreiben
	// realisierung durch das durchgehen aller vorknoten des EndEvent-Arrays und String Verkettung
	public static String createStartString(SequenceFlow [] sequenceFlowArray, EndEvent [] endEvent){
		String startString = null;
		
		if(sequenceFlowArray[0].getSource().getName() == null){
			startString = "{ \"startServiceType\":\""+sequenceFlowArray[0].getSource().getId()+"\"," + System.lineSeparator();
		} else {
			startString = "{ \"startServiceType\":\""+sequenceFlowArray[0].getSource().getName()+"\"," + System.lineSeparator();
		}
		
		Collection<EndEvent> constraintEnd = endEvent[0].getParentElement().getChildElementsByType(EndEvent.class);
		EndEvent [] constraintEndArray = constraintEnd.toArray(new EndEvent[0]);
		for(int i = 0; i < constraintEndArray.length; i++){
			if(constraintEndArray[i].getName() == null){
				startString += "\"endServiceType\":\"" + constraintEndArray[i].getId() + "\"," + System.lineSeparator();
			} else {
				startString += "\"endServiceType\":\"" + constraintEndArray[i].getName() + "\"," + System.lineSeparator();
			}
		}
		startString += "\"constraints\":[" + System.lineSeparator();
		return startString;
	}

	public static String[][] createStringArray(SequenceFlow[] sequenceFlowArray, BpmnModelInstance modelInstance){
		String [][] temp = new String [100][2];
		String [][] result = new String [100][2];
		int count = 0;
		for(int i = sequenceFlowArray.length - 1; i >= 0; i--){
			/*
			if(sequenceFlowArray[i].getTarget().getElementType().getTypeName() == "callActivity"){
				String called = sequenceFlowArray[i].getTarget().getAttributeValue("calledElement");
				Process process = modelInstance.getModelElementById(called);
				Collection<SequenceFlow> callSubSequence = process.getChildElementsByType(SequenceFlow.class);
				SequenceFlow [] callSubSequenceArray = callSubSequence.toArray(new SequenceFlow[0]);
				for(int n = callSubSequence.size() - 1; n >= 0; n--){
					temp[count][0] = callSubSequenceArray[n].getTarget().getName();
					count++;
				}
			} else if(sequenceFlowArray[i].getTarget().getElementType().getTypeName() == "subProcess") {
				Collection<SequenceFlow> subSequence = sequenceFlowArray[i].getTarget().getChildElementsByType(SequenceFlow.class);
				SequenceFlow [] subSequenceArray = subSequence.toArray(new SequenceFlow[0]);
				for(int j = subSequence.size() - 1; j >= 0; j--){
					temp[count][0] = subSequenceArray[j].getTarget().getName();
					count++;
				}
			} else {
				temp[count][0] = sequenceFlowArray[i].getTarget().getName();
				if(sequenceFlowArray[i].getSource().getElementType().getTypeName() == "parallelGateway" && sequenceFlowArray[i].getSource().getOutgoing().size() > 1){
					temp[count][1] = "p";
				} else if(sequenceFlowArray[i].getTarget().getElementType().getTypeName() == "exclusiveGateway") {
					temp[++count][1] = "e";
					count--;
				}
				count++;
			} */
			
			switch(sequenceFlowArray[i].getTarget().getElementType().getTypeName()){
				
				case "endEvent": 		break;
				case "startEvent": 		break;
			
				case "callActivity": 	String called = sequenceFlowArray[i].getTarget().getAttributeValue("calledElement");
										Process process = modelInstance.getModelElementById(called);
										Collection<SequenceFlow> callSubSequence = process.getChildElementsByType(SequenceFlow.class);
										SequenceFlow [] callSubSequenceArray = callSubSequence.toArray(new SequenceFlow[0]);
										for(int n = callSubSequence.size() - 1; n >= 0; n--){
											temp[count][0] = callSubSequenceArray[n].getTarget().getName();
											count++;
										}
										break;
										
				case "subProcess":		Collection<SequenceFlow> subSequence = sequenceFlowArray[i].getTarget().getChildElementsByType(SequenceFlow.class);
										SequenceFlow [] subSequenceArray = subSequence.toArray(new SequenceFlow[0]);
										for(int j = subSequence.size() - 1; j >= 0; j--){
											if(subSequenceArray[j].getTarget().getElementType().getTypeName() != "endEvent"){
												temp[count][0] = subSequenceArray[j].getTarget().getName();
												count++;
											}
										}
										break;
										
				case "parallelGateway":	temp[count][1] = "p";
										count++;
										break;
										
				case "exclusiveGateway": temp[count][1]	= "e";
										 count++;
										 break;
										 
				default:				temp[count][0] = sequenceFlowArray[i].getTarget().getName();
										count++;
										break;
			}
		}
		for(int i = 0; i < count; i++){
			
			if(temp[i][0] != null){
				//System.out.println(temp[i][0] + "||" + temp[i][1]);
				result[nullcounter][0] = temp[i][0];
				nullcounter++;
			} else if(temp[i][1] == "p" && temp[i + 1][1] == "p"){
				result[nullcounter][1] = "p";
				result[nullcounter + 1][1] = "p";
			} else if(temp[i][1] == "e"){
				result[nullcounter][1] = "e";
			}
			//System.out.println(temp[i][0] + "||" + temp[i][1]);
		}
		return result;
		
	}
	
	public static String createConstraints(String [][] stringArray, String [] args){
		String constraints = "";
		
		for(int i = 0; i < nullcounter - 1; i++){
			//System.out.println(stringArray[i][0]+ "|||" +stringArray[i][1]);
			if(stringArray[i][1] == "p" && stringArray[i + 1][1] == null){
				for(int j = 0; j < 2; j++){
					constraints += "\t{ \"type\":\"BeforeNode\"," + System.lineSeparator() +
							"\t  \"target\":\"" + stringArray[i + 1][0] + "\"}," + System.lineSeparator() +
							"\t  \"constraint\":\"" + stringArray[i - j][0] + "\"}," + System.lineSeparator();
				}
				
			} else if(stringArray[i][1] == "p" && stringArray[i + 1][1] != "e") {
				constraints += "\t{ \"type\":\"BeforeNode\"," + System.lineSeparator() +
						"\t  \"target\":\"" + stringArray[i + 1][0] + "\"}," + System.lineSeparator() +
						"\t  \"constraint\":\"" + stringArray[i - 1][0] + "\"}," + System.lineSeparator();
				
			} else if(stringArray[i][1] == "e"){
				constraints += "\t{ \"type\":\"BranchAfterExecution\"," + System.lineSeparator() +
						"\t  \"target\":\"" + stringArray[i][0] + "\"}," + System.lineSeparator() +
						"\t  \"constraint\":\"" + args[1] + "\"}," + System.lineSeparator();
				constraints += "\t{ \"type\":\"BeforeNode\"," + System.lineSeparator() +
						"\t  \"target\":\"" + stringArray[i + 1][0] + "\"}," + System.lineSeparator() +
						"\t  \"constraint\":\"" + stringArray[i][0] + "\"},"+ System.lineSeparator();
				
			} else if(stringArray[i + 1][1] == "e"){
				// Skip BranchAfterExecution decides if the node is selected
			} else {
				constraints += "\t{ \"type\":\"BeforeNode\"," + System.lineSeparator() +
						"\t  \"target\":\"" + stringArray[i + 1][0] + "\"}," + System.lineSeparator() +
						"\t  \"constraint\":\"" + stringArray[i][0] + "\"},"+ System.lineSeparator();
			}
		}
		// Delete the last comma in the String
		constraints = constraints.substring(0, constraints.length());
		constraints += "]};";
		return constraints;
		
	}
	
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