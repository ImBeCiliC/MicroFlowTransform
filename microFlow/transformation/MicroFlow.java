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
  		File file = new File("../MicroFlowTransform/microFlow/Data/CallActivity.bpmn");
  		createCollectionFromBpmn(file);
	}
	
	public static Collection<SequenceFlow> createCollectionFromBpmn(File file){
		String[] filen = file.getName().split("\\.");
		String filename = filen[0] + ".json";
		String jsonString = null;
		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
		
		
		ModelElementType endEventInit = modelInstance.getModel().getType(EndEvent.class);
		Collection<ModelElementInstance> endEventInstance = modelInstance.getModelElementsByType(endEventInit);
		EndEvent [] endEvent = endEventInstance.toArray(new EndEvent[0]);
		
		Collection<SequenceFlow> sequenceFlowInstance = endEvent[0].getParentElement().getChildElementsByType(SequenceFlow.class);
		SequenceFlow [] sequenceFlowArray = sequenceFlowInstance.toArray(new SequenceFlow[0]);
		
		ModelElementType callActivityInit = modelInstance.getModel().getType(CallActivity.class);
		Collection<ModelElementInstance> callActivityInstance = modelInstance.getModelElementsByType(callActivityInit);
		CallActivity [] callActivity = callActivityInstance.toArray(new CallActivity[0]);
		
		//System.out.println(callActivity[0].getAttributeValue("calledElement").toString());
		String [][] result = createStringArray(sequenceFlowArray, modelInstance);
		
		jsonString = createStartString(sequenceFlowArray, endEvent);
		
		for(int i = 0; i < nullcounter - 1; i++){
			jsonString += "\t{ \"type\":\"BeforeNode\"," + System.lineSeparator() +
					"\t  \"target\":\"" + result[i][0] + "\"}," + System.lineSeparator() +
					"\t  \"constraint\":\"" + result[i + 1][0] + "\"}," + System.lineSeparator();
			//System.out.println(sequenceFlowArray[i].getTarget().getName());			
		}
		jsonString += "]};";
		System.out.println(jsonString);
		
		writeToFile(jsonString, filename);
		return sequenceFlowInstance;
	}
	
	//Creates the StartString for the JSON-File
	//
	//TODO Wenn mehrere EndEvents eintreffen können alle vorknoten als endServiceType reinschreiben
	//realisierung durch das durchgehen aller vorknoten des EndEvent-Arrays und String Verkettung
	public static String createStartString(SequenceFlow [] sequenceFlowArray, EndEvent [] endEvent){
		String startString = null;
		
		startString = "{ \"startServiceType\":\""+sequenceFlowArray[0].getSource().getId()+"\"," + System.lineSeparator();
		
		for(int i = 0; i < endEvent.length; i++){
			if(endEvent[i].getName() == null){
				startString += "\"endServiceType\":\"" + endEvent[i].getId() + "\"," + System.lineSeparator();
			} else {
				startString += "\"endServiceType\":\"" + endEvent[i].getName() + "\"," + System.lineSeparator();
			}
		}
		
		startString += "\"constraints\":[" + System.lineSeparator();
		//System.out.println(startString + " Start String");
		return startString;
	}

	public static String[][] createStringArray(SequenceFlow[] sequenceFlowArray, BpmnModelInstance modelInstance){
		String [][] temp = new String [100][2];
		String [][] result = new String [100][2];
		FlowNode [] flow = new FlowNode[50];
		int count = 0;
		for(int i = sequenceFlowArray.length - 1; i >= 0; i--){
			if(sequenceFlowArray[i].getTarget().getElementType().getTypeName() != "callActivity"){
				temp[count][0] = sequenceFlowArray[i].getTarget().getName();
				if(sequenceFlowArray[i].getSource().getElementType().getTypeName() == "parallelGateway" && sequenceFlowArray[i].getSource().getOutgoing().size() > 1){
					temp[count][1] = "p";
				}
				count++;
			} else {
				String called = sequenceFlowArray[i].getTarget().getAttributeValue("calledElement");
				Process process = modelInstance.getModelElementById(called);
				Collection<SequenceFlow> subSequence = process.getChildElementsByType(SequenceFlow.class);
				SequenceFlow [] subSequenceArray = subSequence.toArray(new SequenceFlow[0]);
				for(int n = subSequence.size() - 1; n >= 0; n--){
					temp[count][0] = subSequenceArray[n].getTarget().getName();
					//System.out.println(result[count][0]);
					count++;
				}
			}
		}
		for(int i = 0; i < count; i++){
			//System.out.println(temp[i][0] + " ||| " + temp[i][1]);
			if(temp[i][0] != null){
				result[nullcounter++][0] = temp[i][0];
				result[nullcounter][1] = temp[i][1];
			}
		}
		for(int i = 0; i < nullcounter; i++){
			//System.out.println(result[i][0]);	
		}
		return result;
		
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