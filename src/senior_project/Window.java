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
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Window extends JFrame {
	
	private JLabel funcLabel;
	private JLabel lowerLabel;
	private JLabel upperLabel;
	private JLabel intervalLabel;
	private JLabel errorDisplay;
	private JLabel equationImage;
	private JLabel methodImage;
	private JTextField funcTextField;
	private JTextField lowerTextField;
	private JTextField upperTextField;
	private JTextField intervalTextField;
	private JCheckBox graphCheckBox;
	private JComboBox nComboBox;
	private JPanel methodPanel;
	private JPanel equationImagePanel;
	private JPanel methodImagePanel;
	private JRadioButton methodButton1;
	private JRadioButton methodButton2;
	private JRadioButton methodButton3;
	private JButton inputHelpButton;
	private JButton integrateButton;
	private ButtonGroup methodButtonGroup;
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
        chart.setTitle("Definite Integral Graph");
        chart.removeLegend();
        // creating a jpeg of the chart
        int width = 640; // Width of the image
        int height = 480; // Height of the image 
        File lineChart = new File( "LineChart.jpeg" ); 
        ChartUtilities.saveChartAsJPEG(lineChart , chart, width ,height);
	}

	// function to gather data from all text fields. will handle errors here
	private void getData() {
		upperLimit = upperTextField.getText();
		lowerLimit = lowerTextField.getText();
		userFunc = funcTextField.getText();
		intervalValue = intervalTextField.getText();
	}
	
	// this method checks if a String is equivalent to a positive whole number
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
	
	// this method checks if a String can be parsed as a double 
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
				//lineSeries.add(num,xi);
				//barSeries.add(num,xi);
				
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
	
		//integral display is for testing the solved value
		JLabel integralDisplay = new JLabel();
		
		// adding the equationExample image as a JLabel icon within a JPanel
		equationImagePanel = new JPanel();
		equationImagePanel.setLayout(new BorderLayout());
		equationImage = new JLabel();
		equationImagePanel.add(equationImage);
		equationImage.setIcon(new ImageIcon("equation_image.png"));
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
					intervalSelection = "n";
				else
					intervalSelection = "i";
			}
		});
		add(nComboBox);
		
		// nLabel = new JLabel("Enter n:");
		// add(nLabel);
		intervalTextField = new JTextField(6);
		add(intervalTextField);
		
		// building the JPanel that includes the method radio button groups
		methodPanel = new JPanel();
		
		// will probably move the generation of all icons into a method and make them 
		// global variables.
		ImageIcon trapIcon = new ImageIcon("trapezoidal_image.png");
		ImageIcon meth2Icon = new ImageIcon("method2_image.png");
		ImageIcon meth3Icon = new ImageIcon("method3_image.png");
		
		//this image is shown based on what method of integration is selected
		methodImagePanel = new JPanel();
		methodImagePanel.setLayout(new BorderLayout());
		methodImage = new JLabel();
		methodImagePanel.add(methodImage);
		methodImage.setIcon(trapIcon);
		add(methodImagePanel);
		
		// these are the JRadioButtons that are used for integration method selection
		methodButton1 = new JRadioButton(new AbstractAction("Trapezoidal") {
			public void actionPerformed(ActionEvent e) {
				methodSelection = e.getActionCommand();
				methodImage.setIcon(trapIcon);
			}			
		});
		// default selection
		methodButton1.setSelected(true);
		
		methodButton2 = new JRadioButton(new AbstractAction("Method 2") {
			public void actionPerformed(ActionEvent e) {
				methodSelection = e.getActionCommand();
				methodImage.setIcon(meth2Icon);
			}			
		});
		
		methodButton3 = new JRadioButton(new AbstractAction("Method 3") {
			public void actionPerformed(ActionEvent e) {
				methodSelection = e.getActionCommand();
				methodImage.setIcon(meth3Icon);
			}			
		});
	
		// creating graphCheckButton to determine whether a graph will be plotted
		final JCheckBox graphCheckBox = new JCheckBox("Plot Graph");	
		
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
						integrateTrapezoid(userFunc);
						generateGraph();
						//	generateXYGraph();	
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					integralDisplay.setText(solvedValue);
				}
			}
		});
		
		methodButtonGroup = new ButtonGroup();
		methodButtonGroup.add(methodButton1);
		methodButtonGroup.add(methodButton2);
		methodButtonGroup.add(methodButton3);
		add(methodButton1);
		add(methodButton2);
		add(methodButton3);		
		//add(methodPanel);
		
		add(integrateButton);
		add(graphCheckBox);

		// test variable
		add(integralDisplay);
		// error messages
		errorDisplay = new JLabel();
		errorDisplay.setForeground(Color.RED);
		add(errorDisplay);
		
		setSize(600, 520);
		setTitle("Integration Tool by Tyler Estro");
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
		
		// temporary layout
		setLayout(new FlowLayout());
	}
}
