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

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


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
	private JLabel graphSolutionLabel;
	private JLabel graphAnswerLabel;
	private JTextField funcTextField;
	private JTextField lowerTextField;
	private JTextField upperTextField;
	private JTextField intervalTextField;
	private JCheckBox graphCheckBox;
	private JComboBox intervalComboBox;
	private JPanel equationImagePanel;
	private JButton inputHelpButton;
	private JButton integrateButton;
	private JPanel trapTab;
	private JPanel simpsonTab;
	private JPanel rombergTab;
	private JTabbedPane methodTabbedPane;	
	private String methodSelection = "Trapezoidal";
	// default selection for determining interval length/number
	private String intervalSelection = "n";
	private String solvedValue;
	private String upperLimit;
	private String lowerLimit;
	private String userFunc;
	private String intervalValue;
	private String errorString;
	private JFrame thisFrame = this;
	private final double EPSILON = 1E-14;
	private Evaluator jEval = new Evaluator();
	DefaultCategoryDataset lineData = new DefaultCategoryDataset();	
    DefaultCategoryDataset barData = new DefaultCategoryDataset();
    DefaultCategoryDataset negBarData = new DefaultCategoryDataset();
    XYSeries lineSeries = new XYSeries("line");
    XYSeries barSeries = new XYSeries("bar");
    XYSeries negBarSeries = new XYSeries("negBar");
	
	// graph xychart attempt
    // not going to use this method most likely. leaving it in for now
	public void generateXYGraph() throws IOException {        
	    NumberAxis domainAxis = new NumberAxis("");
	    domainAxis.setLowerBound(Double.parseDouble(lowerLimit));
	    domainAxis.setUpperBound(Double.parseDouble(upperLimit));
	    ValueAxis rangeAxis = new NumberAxis("");
	    
	    XYSeriesCollection dataBar = new XYSeriesCollection();
	    dataBar.addSeries(barSeries);
	    XYItemRenderer barRenderer = new XYBarRenderer(0);
	    XYPlot plot = new XYPlot(dataBar, domainAxis, rangeAxis, barRenderer);
	     
	    XYSeriesCollection dataLine = new XYSeriesCollection();
	    dataLine.addSeries(lineSeries);
	    XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, true); 
	    plot.setDataset(1, dataLine);
	    plot.setRenderer(1, lineRenderer);

	    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	    NumberAxis domain = (NumberAxis) plot.getDomainAxis();
	    domain.setTickUnit(new NumberTickUnit(0.5));     
	    domain.setVerticalTickLabels(true);	 
	    
        //chart
        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle("Definite Integral Graph");
        chart.removeLegend();
        // creating a jpeg of the chart
        int width = 640; // Width of the image 
        int height = 480; // Height of the image  
        File lineChart = new File( "LineChart2.jpeg" ); 
        ChartUtilities.saveChartAsJPEG(lineChart , chart, width ,height);
	}   
	
	// graphing with a CategoryPlot setup
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
        graphSolutionLabel = new JLabel("Solution:");
        graphAnswerLabel = new JLabel(solvedValue);
		try {
			BufferedImage graphImage = ImageIO.read(lineChart);
			JLabel graphLabel = new JLabel(new ImageIcon(graphImage));
			JOptionPane graphPane = new JOptionPane();
			graphPane.showMessageDialog(thisFrame, graphLabel, 
					"Definite Integral Graph", JOptionPane.PLAIN_MESSAGE, null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
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
    	// using replace to ease user input
    	// replaced x with that number because JEval is being difficult
    	userFunc = userFunc.replace("x", "0000000000001000000000000");
    	userFunc = userFunc.replace("X", "0000000000001000000000000");
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
		func = userFunc.replace("0000000000001000000000000", num);
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
		double h;
		double val;
		if (intervalSelection == "n") {
			double xi, num;
			int n = Integer.parseInt(intervalValue);
			val = 0;
			h = (b - a) / n;
			for(int i = 0;i <= n;i++) {
				xi = a + (i * h);
				num = evalFunc(String.valueOf(xi), userFunc);
				System.out.println("keke: " + xi);
				// these lines are for the generating an XYSeries graph
				lineSeries.add(num,xi);
				barSeries.add(num,xi);
				
				lineData.addValue(num, "f(x)", String.valueOf(xi));
				// needed to have the same number of categories for these bars to
				// line up properly. if func evals to positive then barData gets the
				// value while negbar gets 0. other way around if func is neg.
				if(num >= 0) {
					barData.addValue(num, "f(x)", String.valueOf(xi));
					negBarData.addValue(0, "f(x)", String.valueOf(xi));
				} else {
					barData.addValue(0, "f(x)", String.valueOf(xi));
					negBarData.addValue(num, "f(x)", String.valueOf(xi));										
				}
				if(i == 0 || i == n) {
					val += num;
					System.out.println("Val += " + "f(" + xi + ") = " + val);
				} else {
					val += 2 * num;
					System.out.println("Val += " + "2f(" + xi + ") = " + val);
				}
			}
			System.out.println("Val = " + (h/2) + " * " + val + " = " + 
					(h/2)*val);
			val = (h / 2) * val;
		} else {
			val = 0;						
			double i = Double.parseDouble(intervalValue);			
			h = (b - a);
			System.out.println("h = " + h);
			System.out.println("i = " + i);
			System.out.println("h % n = " + (h % i) );
			// using modulus to check for an extra segment at the end
			if((h % i) > 0) {
				System.out.println("Adding extra segment");
				val += ((h % i) / 2) * (evalFunc(String.valueOf(b - (h % i)), userFunc)
						+ evalFunc(String.valueOf(b), userFunc));
				System.out.println("Val += " + ((h%i)/2) + " * " + "(f(" + (b - h % i) + 
						") + f(" + b + ")) = " + val);				
				b -= (h % i);
			}
			System.out.println("i = " + i);
			System.out.println("a = " + a);
			System.out.println("b = " + b);	
			// using constant EPSILON to compare doubles since doubles are not precise
			for(double j = a;Math.abs(b-j + EPSILON) >= i;j+=i) {
				System.out.println("j = " + j);
				val += (i/2) * (evalFunc(String.valueOf(j), userFunc) + 
						evalFunc(String.valueOf(j + i), userFunc));
				System.out.println("Val += " + (i/2) + " * " + "f(" + j + ") + f( " +
						(j+i) + ") = " + val);
			}
		}
		solvedValue = String.valueOf(val);	
		System.out.println(solvedValue);
	}
	
	public Window() {
		// Label that displays the title of the Application
		titleLabel = new JLabel("Definite Integral Calculator");
		titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));
		
		// This font will be used in several other labels
		Font labelFont = new Font(titleLabel.getFont().getName(), Font.PLAIN, 15);

		// Sample images of each integration method
		ImageIcon trapImage = new ImageIcon("trapezoidal_image.png");
		ImageIcon simpsonImage = new ImageIcon("method2_image.png");
		ImageIcon rombergImage = new ImageIcon("method3_image.png");
		
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
					JOptionPane inputHelp = new JOptionPane();
					inputHelp.showMessageDialog(thisFrame, imageLabel, 
							"Function Input Help", JOptionPane.PLAIN_MESSAGE, null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
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
		String[] intervalComboStrings = { "Interval Number (n)", "Interval Length (i)" };
		intervalComboBox = new JComboBox(intervalComboStrings);
		intervalComboBox.setFont(labelFont);
		intervalComboBox.setSelectedIndex(0);
		intervalComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String val;
				val = (String) intervalComboBox.getSelectedItem();
				if(val == "Interval Number (n)")
					intervalSelection = "n";
				else
					intervalSelection = "i";
			}
		});				
		intervalTextField = new JTextField();		
		
		// creating graphCheckButton to determine whether a graph will be plotted
		JCheckBox graphCheckBox = new JCheckBox("Plot Graph");	
		
		// this button gets data from all the textfields, checks if it is accurate,
		// then performs integration if there are no errors.
		integrateButton = new JButton(new AbstractAction("Perform Integration") {
			public void actionPerformed(ActionEvent e) {
				getData();
				errorDisplay.setText("");
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
				else if (intervalSelection == "i" & !isDouble(intervalValue)) {
					errorString = "The interval number (i) must be a valid, positive decimal"
							+ " number or integer.";
					errorDisplay.setText(errorString);
				}
				else if (intervalSelection == "n" & !isWholeNumber(intervalValue)) {
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
						// lineSeries.clear();
						// barSeries.clear();						
						if(graphCheckBox.isSelected()) {
							integrateTrapezoid(userFunc);
							generateGraph();	
							generateXYGraph();
						} else {
							integrateTrapezoid(userFunc);
							answerLabel.setText(solvedValue);
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
