/*
.Program4, G.U.I. Bounce Program                   (Bounce.java).
.Created By:                                             .
- Daniel Hentosz,    (HEN3883@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu).                 .
-Group 7
.Last Revised: March 16th, 2021.                (3/16/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
	Makes use of java's <awt> library to create a GUI,
		- this GUI controls a object which will bounce around the frame.
		
	The user can:
		- change the object to a circle or a square,
		- Select a speed at which the object move's,
		- change the size of the object,
		- select rather or not the objects previous location is shown(tail or no tail),
		-and chose rather the object moves, or does not move.
		
*/

//Our program package.
package Bounce;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.Thread;

public class Bounce extends Frame
		implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable {
	static final long serialVersionUID = 10L;
	private final int BUTTON_H = 20;// Button height.
	private final int INIT_SIZE = 21;
	private final int MAX_SIZE = 100;
	private final int MIN_SIZE = 10;
	private final int SB_SPEED = 50;
	private final int SB_VIS = 10;
	private final int SB_HEIGHT = BUTTON_H;

	private int width = 640;// Initial frame width.
	private int height = 400;// Initial frame height.
	private int buttonW = 50;// Initial button width.
	private int buttonS;// Button spacing.
	private int buttonHS = 5;// Button height spacing.
	private int center;
	private int sbMinSpeed = 1;
	private int sbMaxSpeed = 100 + SB_VIS;
	private int sbSpeed = SB_SPEED;
	private int sbW;
	private int objW = INIT_SIZE;
	private int delay = 16; // Change this to be a calculation based on "sbSpeed"

	private Objc theObj;

	private boolean isRunning = true;
	private boolean isPaused = true;
	private boolean hasTail = true;

	private int winWidth;// Initial frame width.
	private int winHeight;// Initial frame height.
	private int winLeft;// Left side of the frame.
	private int winTop;// Top of the frame.
	private int screenWidth;// Width of the rendering area.
	private int screenHeight;// Height of the rendering area.

	private Insets insets;// Insets of the frame.

	private Thread thread;

	// Our buttons:
	private Button startButton;
	private Button shapeButton;
	private Button tailButton;
	private Button clearButton;
	private Button quitButton;

	private Label speedLabel = new Label("Speed", Label.CENTER);
	private Label sizeLabel = new Label("Size", Label.CENTER);

	private Scrollbar sizeBar;
	private Scrollbar speedBar;

	public Bounce() {
		setLayout(null);// Use a null frame layout.
		setVisible(true);// Make the frame visible.
		makeSheet();// Determines the sizes for the sheet.
		try {
			initComponents();// Try to initialize the components.
		} catch (Exception e) {
			e.printStackTrace();
		}
		sizeScreen();// Size the items on the screen.
		start();
	}

	private void start() {
		delay = sbMaxSpeed - speedBar.getValue() + 2;
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	// Used to size the objects.
	private void sizeScreen() {
		startButton.setLocation(center - 2 * (buttonW + buttonS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		shapeButton.setLocation(center - (buttonW + buttonS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		tailButton.setLocation(center - buttonW / 2, screenHeight + buttonHS + insets.top);
		clearButton.setLocation(center + (buttonW + buttonS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		quitButton.setLocation(center + 2 * (buttonW + buttonS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		speedBar.setLocation(insets.left + buttonS, screenHeight + buttonHS + insets.top);
		speedLabel.setLocation(insets.left + buttonS, screenHeight + buttonHS + BUTTON_H + insets.top);
		sizeBar.setLocation(winWidth - sbW - insets.right - buttonS, screenHeight + buttonHS + insets.top);
		sizeLabel.setLocation(winWidth - sbW - insets.right - buttonS, screenHeight + buttonHS + BUTTON_H + insets.top);

		startButton.setSize(buttonW, BUTTON_H);
		shapeButton.setSize(buttonW, BUTTON_H);
		tailButton.setSize(buttonW, BUTTON_H);
		clearButton.setSize(buttonW, BUTTON_H);
		quitButton.setSize(buttonW, BUTTON_H);
		sizeBar.setSize(sbW, SB_HEIGHT);
		sizeLabel.setSize(sbW, BUTTON_H);
		speedLabel.setSize(sbW, BUTTON_H);
		speedBar.setSize(sbW, SB_HEIGHT);

		theObj.setBounds(insets.left, insets.top, screenWidth, screenHeight);
	}

	// Used to calculate new frame information and recalculation of the frame sizes.
	public void makeSheet() {
		insets = getInsets();
		// The new screen width will be the window with minus the insets of left and
		// right.
		screenWidth = winWidth - insets.left - insets.right;
		// Calculating the screen height.
		screenHeight = winHeight - insets.top - insets.bottom - 2 * (BUTTON_H + buttonHS);
		setSize(winWidth, winHeight);// Set the frame size.
		center = screenWidth / 2;// Determines the center of the screen.
		buttonW = screenWidth / 11;// Determine the width of the buttons.
		sbW = buttonW * 2;
		buttonS = buttonW / 4;// Determine the button spacing.
		this.setBackground(Color.lightGray);// Set the background color.
	}

	// Used to add the objects.
	public void initComponents() throws Exception, IOException {
		startButton = new Button("Run");
		shapeButton = new Button("Circle");
		tailButton = new Button("No Tail");
		clearButton = new Button("Clear");
		quitButton = new Button("Quit");

		speedBar = new Scrollbar(Scrollbar.HORIZONTAL);
		speedBar.setMaximum(sbMaxSpeed + SB_VIS);
		speedBar.setMinimum(sbMinSpeed);
		speedBar.setValue(sbSpeed);
		speedBar.setBackground(Color.gray);

		sizeBar = new Scrollbar(Scrollbar.HORIZONTAL);
		sizeBar.setMaximum(MAX_SIZE + SB_VIS);
		sizeBar.setMinimum(MIN_SIZE);
		sizeBar.setValue(INIT_SIZE);
		sizeBar.setBackground(Color.gray);

		theObj = new Objc(objW, screenWidth, screenHeight);
		theObj.setBackground(Color.white);

		this.add(startButton);
		this.add(shapeButton);
		this.add(tailButton);
		this.add(clearButton);
		this.add(quitButton);
		this.add(speedBar);
		this.add(sizeBar);
		this.add(speedLabel);
		this.add(sizeLabel);
		this.add(theObj);

		speedBar.addAdjustmentListener(this);
		sizeBar.addAdjustmentListener(this);
		startButton.addActionListener(this);
		shapeButton.addActionListener(this);
		tailButton.addActionListener(this);
		clearButton.addActionListener(this);
		quitButton.addActionListener(this);

		this.addComponentListener(this);
		this.addWindowListener(this);
		this.setPreferredSize(new Dimension(width, height));
		this.setMinimumSize(getPreferredSize());
		this.setBounds(winLeft, winTop, width, height);

		speedBar.setEnabled(true);
		speedBar.setVisible(true);

		validate();
	}

	// Used to close the window when the user exits, or the quit button is pressed.
	public void stop() {
		isRunning = false;

		startButton.removeActionListener(this);
		shapeButton.removeActionListener(this);
		tailButton.removeActionListener(this);
		clearButton.removeActionListener(this);
		quitButton.removeActionListener(this);

		speedBar.removeAdjustmentListener(this);
		sizeBar.removeAdjustmentListener(this);

		this.removeComponentListener(this);
		this.removeWindowListener(this);

		dispose();
		System.exit(0);
	}

	@Override
	public void run() {
		// Continues to loop as long as the run flag is true.
		while (isRunning) {
			if (!isPaused) {
				theObj.updatePhysics();
				theObj.repaint();
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int scrollVal;
		Scrollbar sb = (Scrollbar) e.getSource();
		scrollVal = sb.getValue();

		if (sb == speedBar) {
			delay = sbMaxSpeed - speedBar.getValue() + 2;
		} else if (sb == sizeBar) {
			scrollVal = (scrollVal / 2) * 2 + 1;
			theObj.updateSize(scrollVal);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == startButton) {
			if (isPaused) {
				isPaused = false;
				startButton.setLabel("Stop");
			} else {
				isPaused = true;
				startButton.setLabel("Run");
			}
		} else if (source == shapeButton) {
			if (shapeButton.getLabel() == "Circle") {
				shapeButton.setLabel("Square");
				theObj.setRect(false);
			} else {
				shapeButton.setLabel("Circle");
				theObj.setRect(true);
			}
			theObj.repaint();
		} else if (source == tailButton) {
			if (hasTail) {
				hasTail = false;
				theObj.setTail(false);
				tailButton.setLabel("Tail");
			} else {
				hasTail = true;
				theObj.setTail(true);
				tailButton.setLabel("No Tail");
			}
		} else if (source == clearButton) {
			theObj.clear();
			theObj.repaint();
		} else { // Quit button
			stop();
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		winWidth = getWidth();
		winHeight = getHeight();
		makeSheet();
		theObj.reSize(screenWidth, screenHeight);
		sizeScreen();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		stop();
	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}

class Objc extends Canvas {
	static final long serialVersionUID = 11L;

	private int screenWidth;
	private int screenHeight;
	private int size;
	private int lastSize;
	private int x, y;
	private int oldX, oldY;
	private boolean isRect;
	private boolean clearFlag;
	private boolean hasTail;
	private boolean wasRect;
	private Point velocity;

	Objc(int objSize, int width, int height) {
		screenWidth = width;
		screenHeight = height;
		size = objSize;
		isRect = true;
		clearFlag = false;
		x = screenWidth / 2;
		y = screenHeight / 2;
		velocity = new Point(1, 1);
		hasTail = true;
		// checkOverlap();
		repaint();
	}

	public void setTail(boolean hasTail) {
		this.hasTail = hasTail;
		lastSize = size;
		wasRect = isRect;
		oldX = x;
		oldY = y;
	}

	public void setRect(boolean isRect) {
		this.isRect = isRect;
	}

	// Updates the position of the object
	public void updatePhysics() {
		// Update the position
		x += velocity.x;
		y += velocity.y;

		// Check for boundaries
		if ((velocity.x > 0 && x + size / 2 > screenWidth - 4) || (velocity.x < 0 && x - size / 2 < 2))
			velocity.x = -velocity.x;
		if ((velocity.y > 0 && y + size / 2 > screenHeight - 4) || (velocity.y < 0 && y - size / 2 < 3))
			velocity.y = -velocity.y;
	}

	@Override
	public void update(Graphics g) {
		if (clearFlag) {
			clearFlag = false;
			super.paint(g);
			g.setColor(Color.red);
			g.drawRect(0, 1, screenWidth - 1, screenHeight - 2);
		}

		// Draws over the last draw of the Bounce Object
		if (!hasTail) {
			g.setColor(Color.white);
			if (wasRect)
				g.fillRect(oldX - lastSize / 2, oldY - lastSize / 2, lastSize + 1, lastSize + 1);
			else
				g.fillOval(oldX - lastSize / 2 - 1, oldY - lastSize / 2 - 1, lastSize + 2, lastSize + 2);
			lastSize = size;
			wasRect = isRect;
			oldX = x;
			oldY = y;
		}

		if (isRect) {
			g.setColor(Color.lightGray);
			g.fillRect(x - size / 2, y - size / 2, size, size);
			g.setColor(Color.black);
			g.drawRect(x - size / 2, y - size / 2, size, size);
		} else {
			g.setColor(Color.lightGray);
			g.fillOval(x - size / 2, y - size / 2, size, size);
			g.setColor(Color.black);
			g.drawOval(x - size / 2, y - size / 2, size, size);
		}
	}

	public void reSize(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		checkOverlap();
		repaint();
	}

	// Checks if the bounce object has a position beyond any of the boundaries and
	// resolve it
	// If the object is beyond a boundary then it will also set its velocity
	// accordingly
	public void checkOverlap() {
		if (x + size / 2 > screenWidth - 4) {
			velocity.x = -1;
			x = screenWidth - 3 - size / 2;
		} else if (x - size / 2 < 2) {
			velocity.x = 1;
			x = size / 2 + 1;
		}
		if (y + size / 2 > screenHeight - 4) {
			velocity.y = -1;
			y = screenHeight - 3 - size / 2;
		} else if (y - size / 2 < 3) {
			velocity.y = 1;
			y = size / 2 + 2;
		}
	}

	public void updateSize(int size) {
		this.size = size;
		checkOverlap();
		repaint();
	}

	public void clear() {
		clearFlag = true;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.red);
		g.drawRect(0, 1, screenWidth - 1, screenHeight - 2);
		update(g);
	}
}
