package senior_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.sourceforge.jeval.*;

public class Window extends JFrame {
	
	private JLabel funcLabel;
	private JLabel lowerLabel;
	private JLabel upperLabel;
	private JLabel nLabel;
	private JTextField funcTextField;
	private JTextField lowerTextField;
	private JTextField upperTextField;
	private JTextField nTextField;
	private JComboBox nComboBox;
	private JPanel methodPanel;
	private JRadioButton methodButton1;
	private JRadioButton methodButton2;
	private JRadioButton methodButton3;
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

	// function to gather data from all text fields. will handle errors here
	private void getData() {
		upperLimit = upperTextField.getText();
		lowerLimit = lowerTextField.getText();
		userFunc = funcTextField.getText();
		nValue = nTextField.getText();
	}
	
	// replaces the X variable with the appropriate value then evaluates the function
	private double evalFunc(String num, String func) throws Exception {
		Evaluator jEval = new Evaluator();
		String val;
		val = func.replace("x", num);
		val = jEval.evaluate(val);
		return jEval.getNumberResult(val);
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
					System.out.println("f(" + xi + ") = " + evalFunc(String.valueOf(xi), userFunc));
				} else {
					val += 2 * evalFunc(String.valueOf(xi), userFunc);
					System.out.println("2f(" + xi + ") = " + 2 * evalFunc(String.valueOf(xi), userFunc));
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
		
		funcLabel = new JLabel("Input Function:");
		add(funcLabel);
		funcTextField = new JTextField(25);
		add(funcTextField);
		
		lowerLabel = new JLabel("Input lower limit:");
		add(lowerLabel);	
		lowerTextField = new JTextField(4);
		add(lowerTextField);
		
		upperLabel = new JLabel("Input upper limit:");
		add(upperLabel);
		upperTextField = new JTextField(4);
		add(upperTextField);
		
		// drop down list for determining the type of interval selection
		// when changing these values, change corresponding values in integrationButton
		// ActionListener method
		String[] nComboStrings = { "Input Desired Interval Number (n)", 
				"Input Desired Interval Length (i)" };
		nComboBox = new JComboBox(nComboStrings);
		nComboBox.setSelectedIndex(0);
		nComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String val;
				val = (String) nComboBox.getSelectedItem();
				if(val == "Input Desired Interval Number (n)")
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
		methodButton1 = new JRadioButton(new AbstractAction("Method 1") {
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
				try {
					integrateTrapezoid(userFunc);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			integralDisplay.setText(solvedValue);
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
		
		setSize(600, 300);
		setTitle("Senior Project: Integration Application by Tyler Estro");
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
		
		// temporary layout
		setLayout(new FlowLayout());
	}
}