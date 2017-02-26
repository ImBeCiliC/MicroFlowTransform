/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




import java.io.File;
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
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;


public class ParseBpmnTest {
	
	protected static BpmnModelInstance modelInstance;
	
	public static void findElementById() {
		// get the start event of the process by id
		StartEvent startEvent = modelInstance.getModelElementById("startEvent");
		System.out.println(startEvent.getName());
	}
  
  	public static void main(String [] args){
  		File file = new File("/MicroFlowTransform/camunda/process.bpmn");
  		modelInstance = Bpmn.readModelFromFile(file);
  		findElementById();
	}
}
