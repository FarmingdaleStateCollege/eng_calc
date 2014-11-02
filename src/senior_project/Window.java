///////////////////////////////////////////////////////////
//
// Senior Project: Integration Application
// Written By: Tyler Estro
// SUNY Farmingdale (Fall 2014)
//
// File Name: Window.java
// Purpose: The main window of the application.  Contains
//			all functionality of the application and all
//			visible GUI components.
//			
///////////////////////////////////////////////////////////

package senior_project;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.sourceforge.jeval.*;

public class Window extends JFrame {
	
	private JLabel funcLabel;
	private JLabel lowerLabel;
	private JLabel upperLabel;
	private JLabel nLabel;
	private JLabel errorDisplay;
	private JLabel equationImage;
	private JTextField funcTextField;
	private JTextField lowerTextField;
	private JTextField upperTextField;
	private JTextField nTextField;
	private JComboBox nComboBox;
	private JPanel methodPanel;
	private JPanel equationImagePanel;
	private JRadioButton methodButton1;
	private JRadioButton methodButton2;
	private JRadioButton methodButton3;
	private JButton inputHelpButton;
	private JButton integrateButton;
	private ButtonGroup methodButtonGroup;
	private String methodSelection = "Method 1";
	// default selection for determining interval length/number
	private String nSelection = "n";
	private String solvedValue;
	private String upperLimit;
	private String lowerLimit;
	private String userFunc;
	private String nValue;
	private String errorString;
	private JFrame thisFrame = this;

	// function to gather data from all text fields. will handle errors here
	private void getData() {
		upperLimit = upperTextField.getText();
		lowerLimit = lowerTextField.getText();
		userFunc = funcTextField.getText();
		nValue = nTextField.getText();
	}
	
	// replaces the X variable with the appropriate value then evaluates the function
	// also replaces x, E, PI with valid jEval syntax
	private double evalFunc(String num, String func) throws Exception {
		Evaluator jEval = new Evaluator();
		System.out.println(func);
		func = func.replace("x", num);
		System.out.println(func);
		func = func.replace("E", "#{E}");
		System.out.println(func);
		func = func.replace("PI", "#{PI}");
		System.out.println(func);
		func = jEval.evaluate(func);
		return jEval.getNumberResult(func);
	}	
	
	// trapezoidal integration. when 'n' is given, it uses equal segments.
	// when 'i'(h) is given, it first calculates the sum of the remainder
	// segment (determined with the modulus operator) then adds the remaining equal
	// segments. 
	private void integrateTrapezoid(String func) throws Exception {
		double a = Double.parseDouble(lowerLimit);
		double b = Double.parseDouble(upperLimit);
		double n = Double.parseDouble(nValue);
		double h;
		double val;
		if (nSelection == "n") {
			double xi;
			val = 0;
			h = (b - a) / n;
			System.out.println("h: " + h);
			for(int i = 0;i <= n;i++) {
				xi = a + (i * h);
				if(i == 0 || i == n) {
					val += evalFunc(String.valueOf(xi), userFunc);
					System.out.println("f(" + xi + ") = " + 
					evalFunc(String.valueOf(xi), userFunc));
				} else {
					val += 2 * evalFunc(String.valueOf(xi), userFunc);
					System.out.println("2f(" + xi + ") = " + 2 * 
							evalFunc(String.valueOf(xi), userFunc));
				}
			}
			System.out.println("(" + h + " / 2) * " + val);
			val = (h / 2) * val;	
		} else {
			val = 0;
			// n is the value from the nTextField box, which is the desired length
			// of the intervals in this case.s
			h = n;
			System.out.println("h = " + h);
			// using modulus to check for an extra segment at the end
			if((n % h) > 0) {
				System.out.println("Adding extra segment");
				val += ((n % h) / 2) * ( evalFunc(String.valueOf(b - (n % h)), userFunc)
						+ evalFunc(String.valueOf(b), userFunc));
				h -= n % h;
			}
			n = (b - a) / h;
			System.out.println("n = " + n);
			double k = a;
			for(int i = 1;i <= n;i++) {
				val += (h/2) * (evalFunc(String.valueOf(k), userFunc) + 
						evalFunc(String.valueOf(k + h), userFunc));
				System.out.println(h + "/2 * [f( " + k + ") + f(" + (k+h) + ")]");
				k += h;
			}
		}
		solvedValue = String.valueOf(val);
		System.out.println("Answer: " + solvedValue);		
	}
	
	public Window() {
		//integral display is for testing the solved value
		JLabel integralDisplay = new JLabel();
		
		// adding the equationExample image as a JLabel icon within a JPanel
		equationImagePanel = new JPanel();
		equationImagePanel.setLayout(new BorderLayout());
		equationImage = new JLabel();
		equationImagePanel.add(equationImage);
		equationImage.setIcon(new ImageIcon("equationExample.png"));
		add(equationImagePanel);
		
		funcLabel = new JLabel("Input Function: f(x) =");
		add(funcLabel);
		funcTextField = new JTextField(25);
		add(funcTextField);
		
		inputHelpButton = new JButton(new AbstractAction("Function Input Help") {
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedImage inputHelpImage = ImageIO.read(new File("inputHelp.png"));
					JLabel imageLabel = new JLabel(new ImageIcon(inputHelpImage));
					JOptionPane inputHelp = new JOptionPane();
					inputHelp.showMessageDialog(thisFrame, imageLabel, 
							"Function Input Help", JOptionPane.PLAIN_MESSAGE, null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}					
			}			
		});
		add(inputHelpButton);
		
		lowerLabel = new JLabel("Lower limit (a):");
		add(lowerLabel);	
		lowerTextField = new JTextField(4);
		add(lowerTextField);
		
		upperLabel = new JLabel("Upper limit (b):");
		add(upperLabel);
		upperTextField = new JTextField(4);
		add(upperTextField);
		
		// drop down list for determining the type of interval selection
		// when changing nComboStrings, change the ActionListener event below as well
		String[] nComboStrings = { "Interval Number (n)", "Interval Length (i)" };
		nComboBox = new JComboBox(nComboStrings);
		nComboBox.setSelectedIndex(0);
		nComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String val;
				val = (String) nComboBox.getSelectedItem();
				if(val == "Interval Number (n)")
					nSelection = "n";
				else
					nSelection = "i";
				System.out.println(nSelection);
			}
		});
		add(nComboBox);
		
		// nLabel = new JLabel("Enter n:");
		// add(nLabel);
		nTextField = new JTextField(4);
		add(nTextField);
		
		// building the JPanel that includes the method radio button groups
		methodPanel = new JPanel();
		methodPanel.setBackground(Color.blue);
		
		// building all the buttons that will be included in the method panel
		// using subclasses to set methodSelection variable
		methodButton1 = new JRadioButton(new AbstractAction("Trapezoidal") {
			public void actionPerformed(ActionEvent e) {
				methodSelection = e.getActionCommand();
			}			
		});
		// default selection
		methodButton1.setSelected(true);
		
		methodButton2 = new JRadioButton(new AbstractAction("Method 2") {
			public void actionPerformed(ActionEvent e) {
				methodSelection = e.getActionCommand();
			}			
		});
		
		methodButton3 = new JRadioButton(new AbstractAction("Method 3") {
			public void actionPerformed(ActionEvent e) {
				methodSelection = e.getActionCommand();
			}			
		});
		
		integrateButton = new JButton(new AbstractAction("Perform Integration") {
			public void actionPerformed(ActionEvent e) {
				getData();
				errorDisplay.setText("");
				if(Double.parseDouble(lowerLimit) >= Double.parseDouble(upperLimit)) {
					errorString = "The lower limit (a) can not exceed to upper limit (b).";
					errorDisplay.setText(errorString);
				}
				else if (nSelection == "i" && (Double.parseDouble(nValue) >
						(Double.parseDouble(upperLimit) - Double.parseDouble(lowerLimit)))) {
					errorString = "The interval length (i) can not be less than the domain. "
					+ "[upper limit (b) - lower limit (a)]";
					errorDisplay.setText(errorString);
				}
				else {
					try {
						integrateTrapezoid(userFunc);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					integralDisplay.setText(solvedValue);
				}
			}
		});
		
		methodButtonGroup = new ButtonGroup();
		// adding radio buttons to methodPanel so only one method can be selected
		methodButtonGroup.add(methodButton1);
		methodButtonGroup.add(methodButton2);
		methodButtonGroup.add(methodButton3);
		methodPanel.add(methodButton1);
		methodPanel.add(methodButton2);
		methodPanel.add(methodButton3);
		methodPanel.add(integrateButton);		
		add(methodPanel);

		// test variable
		add(integralDisplay);
		// error messages
		errorDisplay = new JLabel();
		errorDisplay.setForeground(Color.RED);
		add(errorDisplay);
		
		setSize(600, 300);
		setTitle("Integration Tool by Tyler Estro");
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
		
		// temporary layout
		setLayout(new FlowLayout());
	}
}
