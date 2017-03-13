package transformation;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JRadioButtonMenuItem;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;

import java.awt.Color;
import javax.swing.KeyStroke;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class GUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 170, 135);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		final JRadioButton rdbtnBpmnToJson = new JRadioButton("BPMN to JSON");
		rdbtnBpmnToJson.setSelected(true);
		rdbtnBpmnToJson.setBounds(6, 7, 131, 23);
		frame.getContentPane().add(rdbtnBpmnToJson);
		
		final JRadioButton rdbtnWorkflowToBpmn = new JRadioButton("Workflow to BPMN");
		rdbtnWorkflowToBpmn.setBounds(6, 33, 131, 23);
		frame.getContentPane().add(rdbtnWorkflowToBpmn);
		
		//Group the radio Buttons
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnWorkflowToBpmn);
		group.add(rdbtnBpmnToJson);		
		
		JButton btnChooseFile = new JButton("Choose File");
		btnChooseFile.setBounds(6, 63, 141, 23);
		frame.getContentPane().add(btnChooseFile);
		
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				OpenFile of = new OpenFile();
				
				try{
					if(rdbtnBpmnToJson.isSelected()){
						of.fileChooser.setDialogTitle("Choose BPMN File");
						BPMNToMicroFlow.createCollectionFromBpmn(of.Picked());
					}
					if(rdbtnWorkflowToBpmn.isSelected()){
						of.fileChooser.setDialogTitle("Choose Workflow Log File");
						MicroFlowToBPMN.controller(of.Picked());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
