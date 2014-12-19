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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.sourceforge.jeval.*;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;


public class Window extends JFrame {
	
	private JLabel funcLabel;
	private JLabel lowerLabel;
	private JLabel upperLabel;
	private JLabel errorDisplay;
	private JLabel equationImage;
	private JLabel titleLabel;
	private JLabel trapLabel;
	private JLabel simpsonLabel;
	private JLabel rombergLabel;
	private JLabel solutionLabel;
	private JLabel answerLabel;
	private JTextField funcTextField;
	private JTextField lowerTextField;
	private JTextField upperTextField;
	private JTextField intervalTextField;
	private JCheckBox graphCheckBox = new JCheckBox("Plot Graph");
	private JComboBox intervalComboBox;
	private JPanel equationImagePanel;
	private JButton inputHelpButton;
	private JButton integrateButton;
	private JPanel trapTab;
	private JPanel simpsonTab;
	private JPanel rombergTab;
	private JTabbedPane methodTabbedPane;	
	private String intervalSelection = "n";
	private String solvedValue;
	private String upperLimit;
	private String lowerLimit;
	private String userFunc;
	private String intervalValue;
	private String errorString;
	private String[] intervalComboStrings; 	
	private JFrame thisFrame = this;
	private final double EPSILON = 1E-14;
	private final String xHolder = "1000";
	private Evaluator jEval = new Evaluator();
	DefaultCategoryDataset lineData = new DefaultCategoryDataset();	
    DefaultCategoryDataset barData = new DefaultCategoryDataset();
    DefaultCategoryDataset negBarData = new DefaultCategoryDataset();
	
	
	// Method that handles setting up the graph
	public void generateGraph() throws IOException {        
        BarRenderer barRender = new BarRenderer();
        barRender.setSeriesPaint(0, Color.blue);
        BarRenderer negBarRender = new BarRenderer();
        negBarRender.setSeriesPaint(0, Color.red);
        LineAndShapeRenderer lineRender = new LineAndShapeRenderer();     
        lineRender.setSeriesPaint(0, Color.black);
        CategoryPlot plot = new CategoryPlot();
        plot.setDataset(barData);
        plot.setRenderer(barRender);
        plot.setDataset(1, negBarData);
        plot.setRenderer(1, negBarRender);
        plot.setDataset(2, lineData);
        plot.setRenderer(2, lineRender);
        CategoryAxis axis = new CategoryAxis();
        axis.setCategoryMargin(0);
        // removing tick labels/marks because they overlap when there are a lot of labels
        axis.setTickMarksVisible(false);
        axis.setTickLabelsVisible(false);
        plot.setDomainAxis(axis);	
        plot.setRangeAxis(new NumberAxis());
        plot.mapDatasetToRangeAxis(2, 0);
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        //chart
        JFreeChart chart = new JFreeChart(plot);
        chart.removeLegend();
        // creating a jpeg of the chart
        int width = 640; // Width of the image
        int height = 480; // Height of the image 
        File lineChart = new File( "graph.jpeg" ); 
        ChartUtilities.saveChartAsJPEG(lineChart , chart, width ,height);
        // creating the pop-up window (JOptionPane) containing the graph
		try {
			BufferedImage graphImage = ImageIO.read(lineChart);
			JLabel graphLabel = new JLabel(new ImageIcon(graphImage));
			JOptionPane.showMessageDialog(thisFrame, graphLabel, 
					"Definite Integral Graph", JOptionPane.PLAIN_MESSAGE, null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// Method to gather data from all TextFields
	private void getData() {
		upperLimit = upperTextField.getText();
		lowerLimit = lowerTextField.getText();
		userFunc = funcTextField.getText();
		intervalValue = intervalTextField.getText();
	}
	
	// Method to check if a String is equivalent to a positive whole number
	public static boolean isWholeNumber(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }				
		int length = str.length();
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '.') 
				return false;
		}
		if (Integer.parseInt(str) <= 0) 
			return false;
		return true;
	}
	
	// Method to check if a String can be parsed as a Double 
    private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // this method checks if the user defined function is valid and replaces
    // some chars to make things easier on the user
    private boolean isValidFunction() {
    	userFunc = userFunc.replace("x", xHolder);
    	userFunc = userFunc.replace("X", xHolder);
    	userFunc = userFunc.replace("E", "#{E}");
    	userFunc = userFunc.replace("PI", "#{PI}");
    	try {
    		jEval.evaluate(userFunc);
    	} catch (EvaluationException e) {
    		return false;
    	} 
    	return true;
    }	
	
	// replaces the number that replaced x with the desired number to evaluate with
    // then evaluates the function at that value
	private double evalFunc(String num, String func) throws Exception {
		func = userFunc.replace(xHolder, num);
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
		double val = 0;
		double h, num;
		if (intervalSelection == "n") {
			double xi;
			int n = Integer.parseInt(intervalValue);
			h = (b - a) / n;
			for(int i = 0;i <= n;i++) {
				xi = a + (i * h);
				num = evalFunc(String.valueOf(xi), userFunc);
				if (graphCheckBox.isSelected()) {
					lineData.addValue(num, "f(x)", String.valueOf(xi));
					if (i > 0) {
						if (num >= 0) {
							barData.addValue(num, "f(x)", String.valueOf(xi));
							negBarData.addValue(0, "f(x)", String.valueOf(xi));
						} else {
							barData.addValue(0, "f(x)", String.valueOf(xi));
							negBarData.addValue(num, "f(x)", String.valueOf(xi));
						}
					}
				}
				if(i == 0 || i == n) {
					val += num;
				} else {
					val += 2 * num;
				}
			}
			val = (h / 2) * val;
		} else {						
			double i = Double.parseDouble(intervalValue);
			boolean uneven = false;
			h = (b - a);
			double aX = (h % 1);
			double bX = b;
			double numX = 0;
			// using modulus to check for an extra segment at the end
			if((h % i) > 0) {
				aX = (h % i);
				bX = b;
				numX = (aX / 2) * (evalFunc(String.valueOf(bX - aX), userFunc)
						+ evalFunc(String.valueOf(bX), userFunc));
				val += numX;
				b -= (h % i);
				uneven = true;
			}
			// using constant EPSILON to compare doubles since doubles are not precise
			for(double j = a;Math.abs(b-j + EPSILON) >= i;j+=i) {
				num = (i/2) * (evalFunc(String.valueOf(j), userFunc) + 
						evalFunc(String.valueOf(j + i), userFunc));
				val += num;
				lineData.addValue(evalFunc(String.valueOf(j+i), userFunc), "f(x)",
						String.valueOf((j + i)));
				if (evalFunc(String.valueOf(j + i), userFunc) >= 0) {
					barData.addValue(evalFunc(String.valueOf(j + i), userFunc),
							"f(x)", String.valueOf(j + i));
					negBarData.addValue(0, "f(x)", String.valueOf(j + i));
				} else {
					barData.addValue(0, "f(x)", String.valueOf(j + i));
					negBarData.addValue(evalFunc(String.valueOf(j + i), userFunc), 
							"f(x)", String.valueOf(j + i));
				}												
			}
			// had to add this value at the end due to limitations in the graphing setup
			if(uneven == true && graphCheckBox.isSelected()) {
				lineData.addValue(evalFunc(String.valueOf(bX), userFunc), "f(x)", 
						String.valueOf(bX));
				if (evalFunc(String.valueOf(bX), userFunc) >= 0) {
					barData.addValue(evalFunc(String.valueOf(bX), userFunc), "f(x)", 
							String.valueOf(bX));
					negBarData.addValue(0, "f(x)", String.valueOf(bX));
				} else {
					barData.addValue(0, "f(x)", String.valueOf(bX));
					negBarData.addValue(evalFunc(String.valueOf(bX), userFunc), "f(x)", 
							String.valueOf(bX));
				}	
			}
		}
		solvedValue = String.valueOf(val);
	}
	
	private void integrateSimpson(String func) throws Exception {
		double a = Double.parseDouble(lowerLimit);
		double b = Double.parseDouble(upperLimit);
		double val = 0;
		double h, xi, num;
		int n = Integer.parseInt(intervalValue);
		h = (b - a) / n;
		for (int i = 0; i <= n;i++) {
			xi = a + (i * h);
			num = evalFunc(String.valueOf(xi), userFunc);
			if (graphCheckBox.isSelected()) {
				lineData.addValue(num, "f(x)", String.valueOf(xi));
				if (i > 0) {
					if (num >= 0) {
						barData.addValue(num, "f(x)", String.valueOf(xi));
						negBarData.addValue(0, "f(x)", String.valueOf(xi));
					} else {
						barData.addValue(0, "f(x)", String.valueOf(xi));
						negBarData.addValue(num, "f(x)", String.valueOf(xi));
					}
				}
			}
			if (i == 0 || i == n) {
				val += num;
			} else if (i % 2 == 0) {
				val += (2 * num);				
			} else {
				val += (4 * num);
			}
		}		
		val = (h / 3) * val;
		solvedValue = String.valueOf(val);
	}	
	
	private void integrateRomberg(String func) throws Exception {
		double a = Double.parseDouble(lowerLimit);
		double b = Double.parseDouble(upperLimit);
		int n = Integer.parseInt(intervalValue);
		int n2 = 0;
		double h = 0;
		double r[][] = new double[n+1][n+1];
		r[0][0] = ( (.5) * ((b-a)/1) * (evalFunc(String.valueOf(a), userFunc) +
				evalFunc(String.valueOf(b), userFunc)) );
		for(int i=1;i <= n;i++) {
			n2 = (int) Math.pow(2, i); // number of segments
			h = (b-a) / n2; // segment length
			for(int j = 0;j <= i;j++) {
				if(j == 0) {
					for(double k = 0;Math.abs(b-k + EPSILON) >= h;k+=h) {
						if(k == 0) 
							r[i][0] += evalFunc(String.valueOf(k), userFunc);
						else
							r[i][0] += 2.0 * evalFunc(String.valueOf(k), userFunc);
					}
					r[i][0] += evalFunc(String.valueOf(b), userFunc);					
					r[i][0] = (0.5) * h * r[i][0];
				}
				else
				{
				r[i][j] = ((Math.pow(4.0, j) * r[i][j-1]) - r[i-1][j-1]) / 
						(Math.pow(4.0,j) - 1.0);
				}
				solvedValue = String.valueOf(r[i][j]);
			}
		}
		if (graphCheckBox.isSelected()) {
			for (int i = 0;i <= n2; i++) {
				double xi, num;
				xi = a + (i * h);
				num = evalFunc(String.valueOf(xi), userFunc);
				if (graphCheckBox.isSelected()) {
					lineData.addValue(num, "f(x)", String.valueOf(xi));
					if (i > 0) {
						if (num >= 0) {
							barData.addValue(num, "f(x)", String.valueOf(xi));
							negBarData.addValue(0, "f(x)", String.valueOf(xi));
						} else {
							barData.addValue(0, "f(x)", String.valueOf(xi));
							negBarData.addValue(num, "f(x)", String.valueOf(xi));
						}
					}
				}
			}
		}
	}
	
	public Window() {
		// Label that displays the title of the Application
		titleLabel = new JLabel("Definite Integral Calculator");
		titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));
		
		// This font will be used in several other labels
		Font labelFont = new Font(titleLabel.getFont().getName(), Font.PLAIN, 15);

		// Sample images of each integration method
		ImageIcon trapImage = new ImageIcon("trapezoidal_image.png");
		ImageIcon simpsonImage = new ImageIcon("simpson_image.png");
		ImageIcon rombergImage = new ImageIcon("romberg_image.png");
		
		// Label and Panel containing the Image for the Trapezoidal integration method
		trapLabel = new JLabel(trapImage);		
		trapTab = new JPanel();
		trapTab.add(trapLabel);
		
		// Label and Panel containing the Image for Simpson's integration method
		simpsonLabel = new JLabel(simpsonImage);
		simpsonTab = new JPanel();
		simpsonTab.add(simpsonLabel);
		
		// Label and Panel containing the Image for Romberg's integration method
		rombergLabel = new JLabel(rombergImage);
		rombergTab = new JPanel();
		rombergTab.add(rombergLabel);
		
		// TabbedPane used to select method of integration
		methodTabbedPane = new JTabbedPane();
		methodTabbedPane.setFont(labelFont);
		methodTabbedPane.addTab( "Trapezoidal", trapTab );
		methodTabbedPane.addTab( "Simpson", simpsonTab );
		methodTabbedPane.addTab( "Romberg", rombergTab );

	    methodTabbedPane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	switch(methodTabbedPane.getSelectedIndex()) {
	        	default:
	        		intervalComboBox.setSelectedIndex(0);
	        		break;
	        	case 2:
	        		intervalComboBox.setSelectedIndex(2);
	        		break;
	        	}
	        }
	    });
	
		
		// Panel and Image for the sample equation image
		equationImagePanel = new JPanel();
		equationImagePanel.setLayout(new BorderLayout());
		equationImage = new JLabel();
		equationImagePanel.add(equationImage);
		equationImage.setIcon(new ImageIcon("equation_image.png"));
		
		// Label and Textfield for inputting user-defined functions
		funcLabel = new JLabel("Input Function: f(x)=");
		funcLabel.setFont(labelFont);
		funcTextField = new JTextField();
		
		// Button that accesses the Function Input Help image in a JOptionPane
		inputHelpButton = new JButton(new AbstractAction("Function Input Help") {
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedImage inputHelpImage = ImageIO.read(new File("inputHelp.png"));
					JLabel imageLabel = new JLabel(new ImageIcon(inputHelpImage));
					JOptionPane.showMessageDialog(thisFrame, imageLabel, 
							"Function Input Help", JOptionPane.PLAIN_MESSAGE, null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}					
			}			
		});
		
		// Labels and Textfields for inputting the upper and lower limits
		lowerLabel = new JLabel("Lower limit (a):");
		lowerLabel.setFont(labelFont);
		lowerTextField = new JTextField();		
		upperLabel = new JLabel("Upper limit (b):");	
		upperLabel.setFont(labelFont);
		upperTextField = new JTextField();
		
		// drop down list for determining the type of interval selection
		// when changing nComboStrings, change the ActionListener event below as well
		intervalComboStrings = new String[3];
		intervalComboStrings[0] = "Interval Number (n)";
		intervalComboStrings[1] = "Interval Length (i)";
		intervalComboStrings[2] = "Iterations (I)";
		intervalComboBox = new JComboBox(intervalComboStrings);
		intervalComboBox.setFont(labelFont);
		intervalComboBox.setSelectedIndex(0);
		intervalComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String val;
				val = (String) intervalComboBox.getSelectedItem();
				if(val == "Interval Number (n)")
					intervalSelection = "n";
				else if(val == "Interval Length (i)")
					intervalSelection = "i";
				else
					intervalSelection = "k";
			}
		});				
		intervalTextField = new JTextField();		
			
		// All error handling for input is done here before integration methods are called
		integrateButton = new JButton(new AbstractAction("Perform Integration") {
			public void actionPerformed(ActionEvent e) {
				getData();
				errorDisplay.setText("");
				answerLabel.setText("");
				if(lowerLimit.equals("") | upperLimit.equals("") | intervalValue.equals("") |
						userFunc.equals("")) {
					errorString = "All fields must be filled out.";
					errorDisplay.setText(errorString);
				}
				else if(!isValidFunction()) {
					errorString = "Function is invalid. See \"Function Input Help\""
							+ " for proper format.";
					errorDisplay.setText(errorString);				
				}
				else if(!isDouble(lowerLimit)) {
					errorString = "The lower limit (a) must be a valid decimal number"
							+ " or integer.";
					errorDisplay.setText(errorString);
				}
				else if(!isDouble(upperLimit)) {
					errorString = "The upper limit (a) must be a valid decimal number"
							+ " or integer.";
					errorDisplay.setText(errorString);
				}				
				else if(Double.parseDouble(lowerLimit) >= Double.parseDouble(upperLimit)) {
					errorString = "The lower limit (a) can not be equal to or exceed the"
							+ " upper limit (b).";
					errorDisplay.setText(errorString);
				}
				else if (methodTabbedPane.getSelectedIndex() == 0 &&
						intervalSelection == "k") {
					errorString = "Iterations (I) is not a valid selection for"
							+ " trapezoidal integration";
					errorDisplay.setText(errorString);
				}
				else if (methodTabbedPane.getSelectedIndex() == 1 &&
						(intervalSelection == "k" || intervalSelection == "i")) {
					errorString = "Only interval number (n) is a valid selection for"
							+ " Simpson integration";
					errorDisplay.setText(errorString);
				}
				else if (methodTabbedPane.getSelectedIndex() == 2 &&
						intervalSelection != "k") {
					errorString = "Only iterations (I) is a valid selection for"
							+ " Romberg integration";
					errorDisplay.setText(errorString);
				}
				else if (methodTabbedPane.getSelectedIndex() == 1 &&
						!isWholeNumber(intervalValue)) {
					errorString = "The interval number (n) must be an even whole"
							+ " number that is greater than 0 when using Simpson integration";
					errorDisplay.setText(errorString);
				}
				else if (methodTabbedPane.getSelectedIndex() == 1 &&
						(Integer.parseInt(intervalValue) % 2) > 0) {
					errorString = "The interval number (n) must be an even whole"
							+ " number that is greater than 0 when using Simpson integration";
					errorDisplay.setText(errorString);
				}
				else if (methodTabbedPane.getSelectedIndex() == 2 &&
						!isWholeNumber(intervalValue)) {
					errorString = "The number of iterations (I) must be a whole"
							+ " number between 1 and 6 when using Romberg integration";
					errorDisplay.setText(errorString);
				}
				else if (methodTabbedPane.getSelectedIndex() == 2 &&
						Integer.parseInt(intervalValue) < 1 |
						Integer.parseInt(intervalValue) > 6) {
					errorString = "The number of iterations (I) must be a whole"
							+ " number between 1 and 6 when using Romberg integration";
					errorDisplay.setText(errorString);
				}				
				else if (intervalSelection == "i" && !isDouble(intervalValue)) {
					errorString = "The interval number (i) must be a valid, positive decimal"
							+ " number or integer.";
					errorDisplay.setText(errorString);
				}
				else if (intervalSelection == "n" && !isWholeNumber(intervalValue)) {
					errorString = "The interval number (n) must be a whole number greater"
							+ " than zero.";
					errorDisplay.setText(errorString);
				}					
				else if (intervalSelection == "i" & Double.parseDouble(intervalValue) < 0) {
					errorString = "The interval number (i) must be a positive decimal"
							+ " number or integer.";
					errorDisplay.setText(errorString);
				}
				else if (intervalSelection == "i" & (Double.parseDouble(intervalValue) >
						(Double.parseDouble(upperLimit) - Double.parseDouble(lowerLimit)))) {
					errorString = "The interval length (i) can not be less than the domain. "
					+ "[upper limit (b) - lower limit (a)]";
					errorDisplay.setText(errorString);
				}			
				else {
					try {
						lineData.clear();
						barData.clear();
						negBarData.clear();						
						switch(methodTabbedPane.getSelectedIndex()) {
							case 0:
								integrateTrapezoid(userFunc);
								if(graphCheckBox.isSelected())
									generateGraph();	
								answerLabel.setText(solvedValue);
								break;
							case 1:
								integrateSimpson(userFunc);
								if(graphCheckBox.isSelected())
									generateGraph();
								answerLabel.setText(solvedValue);
								break;
							case 2:
								integrateRomberg(userFunc);
								if(graphCheckBox.isSelected())
									generateGraph();
								answerLabel.setText(solvedValue);
								break;
						}	
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		integrateButton.setFont(new Font(integrateButton.getFont().getName(), Font.PLAIN, 16));
		
		// Error Message Display Label
		errorDisplay = new JLabel();
		errorDisplay.setForeground(Color.RED);	
		
		// Labels that display the solution
		solutionLabel = new JLabel("Solution:");
		solutionLabel.setFont(new Font(solutionLabel.getFont().getName(), Font.BOLD, 15));
		answerLabel = new JLabel("");
		answerLabel.setFont(labelFont);
		
		// Main Frame General Settings
		setSize(760, 680);
		setTitle("Definite Integral Calculator by Tyler Estro");
		setDefaultCloseOperation(EXIT_ON_CLOSE);	
		
/////// Layout Manager ///////
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// Title Label
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridwidth = 2;
		c.gridx = 1;
		c.gridy = 0;
		add(titleLabel, c);		
		
		// TabbedPane for Integration Method Selection
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridx = 1;
		c.gridy = 1;
		add(methodTabbedPane, c);
		
		// Sample Equation Image Panel
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy= 2;
		c.gridwidth = 1;
		add(equationImagePanel, c);
		
		// Function Input TextField Label
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.insets = new Insets(0,4,0,0);
		add(funcLabel, c);
		
		// Function Input TextField
		c.gridx = 2;
		add(funcTextField, c);
		
		// Button to Access Input Help
		c.gridx = 3;
		add(inputHelpButton, c);
		
		// Upper Limit TextField Label
		c.gridx = 0;
		c.gridy = 3;
		c.weighty = .2;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.LINE_END;
		add(upperLabel, c);
		
		// Upper Limit TextField
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		add(upperTextField, c);
		
		// ComboBox to Select Interval Mode (i or n)
		c.gridx = 2;
		add(intervalComboBox, c);		
		
		// Interval Value TextField
		c.gridx = 3;
		add(intervalTextField, c);	
		
		// Lower Limit TextField Label
		c.gridx = 0;
		c.gridy = 4;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(lowerLabel, c);
		
		// Lower Limit TextField
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(lowerTextField, c);
		
		// Integrate Button
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 2;
		add(integrateButton, c);
		
		// Plot Graph Checkbox
		c.gridx = 3;
		c.gridwidth = 1;
		add(graphCheckBox, c);
		
		// Error Messages
		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 4;
		add(errorDisplay, c);		
				
		// Solution Display
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 7;
		add(solutionLabel, c);
		c.gridx = 2;
		add(answerLabel, c);		
	}
}
