package com.sirma.itt.javacourse.netgui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A simple GUI calculator application.
 * 
 * @author vtsonev
 */
public class Calculator {
	/**
	 * Constructs the calculator creating its GUI part.
	 */
	public Calculator() {
		new CalculatorGUI();
	}

	private float current = 0;
	private String currentOperator = "";
	private float currentOperand = 0;
	private final String[] keyOperators = { "+", "-", "*", "/", "=", ".", "CE",
			"Bck" };

	/*
	 * Back-end part
	 */
	/**
	 * Sets the new current value taking the current operand and operator set by
	 * the GUI, passed to the method.
	 * 
	 * @param operator
	 *            is the operator
	 * @param operand
	 *            is the operand to calculate
	 */
	private void calcualte(String operator, float operand) {
		switch (operator) {
			case "+":
				current += operand;
				break;
			case "-":
				current -= operand;
				break;
			case "*":
				current *= operand;
				break;
			case "/":
				current /= operand;
				break;
			default:
				break;
		}
	}

	/*
	 * Front-end part
	 */
	/**
	 * The inner GUI class drawing the front-end part of the calculator.
	 */
	class CalculatorGUI extends JFrame {
		/**
		 * Comment for serialVersionUID.
		 */
		private static final long serialVersionUID = 1L;

		private JPanel keypadHolder;
		private JLabel display;
		private boolean waitingOperand = false;
		private boolean editableDisplay = true;

		/**
		 * Constrcuts the GUI and makes it visible on the screen.
		 */
		public CalculatorGUI() {
			setTitle("GUI calculator");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// default layout
			setLayout(null);
			// create the display and the keypad
			createDisplay();
			createKeypad();
			// set the size of the window
			setSize(new Dimension(display.getWidth() + 8,
					(display.getHeight() + keypadHolder.getHeight()) + 42));
			// add the keypad and the display to the calculator window
			getContentPane().add(display);
			getContentPane().add(keypadHolder);
			setResizable(false);
			setVisible(true);
		}

		/**
		 * Creates and draws the display of the calculator.
		 */
		private void createDisplay() {
			display = new JLabel(Float.toString(current));
			display.setBounds(0, 0, 300, 64);
			display.setBorder(BorderFactory.createLoweredBevelBorder());
			display.setFont(new Font("monspaced", Font.PLAIN, 20));
			display.setHorizontalAlignment(JLabel.CENTER);
		}

		/**
		 * Creates the GUI part of the calculator keypad.
		 */
		private void createKeypad() {
			keypadHolder = new JPanel();
			keypadHolder.setBounds(0, 70, 300, 256);
			keypadHolder.setLayout(new GridLayout(5, 5, 4, 4));
			/*
			 * The following loop creates and places 18 buttons in the
			 * keypadHolder panel. Sets the text, font and and size of each one
			 * and adds it an event listener with a certain behaviour according
			 * to the button text. The main logic of the click events is inside
			 * the try-catch block.
			 */
			for (int i = 0; i < 18; i++) {
				String buttonText = "";
				if (i <= 9) {
					buttonText = Integer.toString(i);
				} else {
					buttonText = keyOperators[i - 10];
				}

				final JButton temp = new JButton(buttonText);
				temp.setSize(100, 100);
				temp.setFont(new Font("Arial", 1, 16));
				/*
				 * Adds an event listener to each newly created button.
				 */
				temp.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							Float.parseFloat(temp.getText());
							// if the program flow continues here, the button
							// text is a number

							// remove the inital 0.0 from the screen before
							// doing anything
							if (("0.0".equals(display.getText()))
									|| (!editableDisplay)) {
								display.setText("");
								editableDisplay = true;
							}
							display.setText(display.getText() + temp.getText());
							if (!waitingOperand) {
								current = Float.parseFloat(display.getText());
							} else {
								currentOperand = Float.parseFloat(display
										.getText());
							}
						} catch (NumberFormatException nfe) {
							// if the exception is catched, the button doesn't
							// have a number for a text

							// resets the screen and the temp values
							if ("CE".equals(temp.getText())) {
								editableDisplay = true;
								current = 0;
								currentOperand = 0;
								currentOperator = "";
								display.setText(Float.toString(current));
								return;
							} else if ("Bck".equals(temp.getText())) {
								// backspace
								if (("0.0".equals(display.getText()))
										|| (!editableDisplay)) {
									return;
								}
								if (display.getText().length() == 1) {
									current = 0;
									display.setText(Float.toString(current));
									return;
								}
								display.setText(display.getText().substring(0,
										display.getText().length() - 1));
								currentOperand = Float.parseFloat(display
										.getText());
								return;
							} else if ("=".equals(temp.getText())) {
								// calculation
								editableDisplay = false;
								calcualte(currentOperator, currentOperand);
								display.setText(Float.toString(current));
								current = 0;
								currentOperand = 0;
								currentOperator = "";
								waitingOperand = false;
								return;
							} else if (".".equals(temp.getText())) {
								// print a decimal point on the display that
								// will be recognized by the parser
								if (!display.getText().contains(".")) {
									display.setText(display.getText()
											+ temp.getText());
								}
								return;
							}
							// if all of the upper conditions are skipped, the
							// button is a mathematical opreator
							current = Float.parseFloat(display.getText());
							display.setText("0.0");
							currentOperator = temp.getText();
							waitingOperand = true;
						}
					}
				});
				keypadHolder.add(temp);
			}
		}
	}

}
