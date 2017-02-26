package transformation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.Script;
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
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

public class MicroFlow {
	public static void main(String[] args) {
  		File file = new File("../MicroFlowTransform/microFlow/Data/TavelBooking.bpmn");
  		System.out.println("test");
  		createCollectionFromBpmn(file);
	}
	
	public static Collection<ModelElementInstance> createCollectionFromBpmn(File file){
		String[] filen = file.getName().split("\\.");
		String filename = filen[0] + ".json";
		String jsonString = null;
		BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
		
		ModelElementType endEventInit = modelInstance.getModel().getType(EndEvent.class);
		Collection<ModelElementInstance> endEventInstance = modelInstance.getModelElementsByType(endEventInit);
		EndEvent [] endEvent = endEventInstance.toArray(new EndEvent[0]);
		
		ModelElementType sequenceFlow = modelInstance.getModel().getType(SequenceFlow.class);
		Collection<ModelElementInstance> sequenceFlowInstance = modelInstance.getModelElementsByType(sequenceFlow);
		SequenceFlow [] sequenceFlowArray = sequenceFlowInstance.toArray(new SequenceFlow[0]);
		
		jsonString = createStartString(sequenceFlowArray, endEvent);
		
		for(int i = 0; i < sequenceFlowInstance.size(); i++){
			if(sequenceFlowArray[i].getTarget().getName() == null){
				//System.out.println("Skipped");
			} else {
				jsonString += "{ \"type\":\"RequiredNode\"," + System.lineSeparator() +
						"\"target\":\"" + sequenceFlowArray[i].getTarget().getName() + "\"}," + System.lineSeparator();
			}
		}
		jsonString += "]};";
		
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
		System.out.println(startString);
		return startString;
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