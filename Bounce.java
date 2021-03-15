/*
.Program4, G.U.I. Bounce Program     (Bounce\Bounce.java).
.Created By:                                             .
- Daniel Hentosz,    (HEN3883@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu).                 .
.Last Revised: March 16th, 2021.              (3/16/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
	Makes use of java's <awt> library to create a GUI,
		- this GUI controls a object which will bounce around the frame.
		
	The user can:
		- change the object to a circle or a square,
		- Select a speed at which the object move's,
		- change the size of the object,
		- select whether or not the objects previous location is shown(tail or no tail),
		-and chose rather the object moves, or does not move.
	
	For more implementation details, see the class header at GUIBounce().
		
*/

// Packages the program into a folder called "Bounce",
// When compiling this file via javac, intended command notation is "javac -d . Bounce,java",
// - intended run notation is "java Bounce.Bounce" (contains main() method of this file).
package Bounce;


// Imports components required for Frame(), Runnable(), and various action listeners. 
// ...lang.Thread() is also imported.
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.Thread;


/*
The Bounce() Class:
	Description:
		- serves as a container class for main() (see block comment below),
	Extends:
		- N/A.
	Implements:
		- N/A.
	Preconditions:
		- shared with main() (see below),
	Postconditions:
		- Constructor returns an instance of Bounce().
*/
public class Bounce
{
	/*
	The main() method:
		Description: 
			- Opens a new instance of GUIBounce(). 
		Preconditions:
			N/A: method ignores values in <args>[].
		Postconditions:
			- Creates a new instance of GUIBounce, <gui>,
				+ from here, implemented methods loop until the program is exited.
	*/
	public static void main(String[] args)
	{
		GUIBounce gui = new GUIBounce();
		return;
	}
}



/*
The GUIBounce() Class:
	Description:
		- serves as a container window (Frame()) for an instance of Objc(),
		- allows interface with Objc() via several UI elements(),
			+ Speed Controls   - Scroll (Controls Objc()'s current iteraton speed; higher = faster),
			+ Run     / Stop   - Button (Toggles between running and pausing Objc()'s iterations),
			+ Circle  / Square - Button (Toggles the vector-shape inside of Objc() between a circle and a square),
			+ No Tail / Tail   - Button (Toggles whether or not the vector-shape inside of Objc() leaves a drawn tail on the canvas),
			+ Clear            - Button (Clears the current canvas inside of Objc()),
			+ Quit Button      - Button (Exits the program (functionality is the same as clicking the [x] at the upper right),
			+ Size Controls    - Scroll (Controls Objc()'s vector-shape's current size on the screen; higher = larger).
		- 
	Extends:
		- Frame(), from java.awt...
	Implements:
		- WindowListener()     - for window related events (open, close, etc...),
		- ComponentListener()  - for certain java.awt... components (Button(), ScrollBar()),
		- ActionListener()     - for action(s) taken by the user's computer's peripherals,
		- AdjustmentListener() - for adjustment(s) to certain components (ScrollBar()),
		- Runnable()           - for the use of Thread() throughout instances of GUIBounce().
	Preconditions:
		- N/A.
	Postconditions:
		- Constructor returns an instance of GUIBounce(),
			+ internal methods (makeSheet(), initComponents(), sizeScreen(), start()) are also ran.
				- start() begins an internal loop, which can only be terminated by user input (clicking on "Quit" or [x]).
*/
class GUIBounce extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable {
	// Defines <serialVersionUID>, a universal identifier for this frame class's instances.
	// - this variable is FINAL, and cannot be changed.
	static final long serialVersionUID = 10L;
	
	
	// Defines <BUTTON_H>, the height of all buttons within GUIBounce().
	private final int BUTTON_H = 20;
	
	// Defines <INIT_SIZE>, which is carried onto objW (the mutable component width).
	private final int INIT_SIZE = 21;
	
	// Defines the MAX and MIN size for Objc's shape, <MAX_SIZE> and <MIN_SIZE>.
	private final int MAX_SIZE = 100;
	private final int MIN_SIZE = 10;
	
	// Defines <SB_SPEED>, the starting value of the ScrollBar("Speed").
	private final int SB_SPEED = 50;
	
	// Defines <SB_VIS>, a constant added to the width of ScrollBar() instances.
	private final int SB_VIS = 10;
	private final int SB_HEIGHT = BUTTON_H;

	// Defines mutable versions of the frame <width>, <height>, and <center>.
	private final int width = 640;
	private final int height = 400;
	private int center;
	
	// Defines the mutable button width (changes with horizontal resizing).
	private int buttonW = 50;

	// Defines the mutable button width spacing  (changes with horizontal resizing).
	private int buttonWS;
	
	// Defines the mutable button height spacing (changes with vertical resizing).
	private int buttonHS = 5;

	// Defines the minimum, maximum, and current speed values which can be held by a ScrollBar() instance (defined in initComponents()). 
	private int sbMinSpeed = 1;
	private int sbMaxSpeed = 100 + SB_VIS;
	private int sbSpeed = SB_SPEED;
	
	// Defines <sbW> the mutable ScrollBar() width.
	private int sbW;
	
	// Defines the <objW>, or mutable component width (default value is <INIT_SIZE>).
	private int objW = INIT_SIZE;
	
	// Defines delay, a default value transformed by the current value of <sbSpeed>.
	private int delay = 16;

	// Defines <Objc>, which serves as this Frame()'s implementation of a canvas (via an instance of a class that extends to Canvas()).
	private Objc theObj;

	private boolean isRunning = true;
	private boolean isPaused  = true;
	private boolean hasTail   = true;
	
	
	// Defines temporary (lowercase) forms of the current screen size,
	// - unlike the <final> definitions above, these are mutable. 
	private int winWidth  = width;// Initial frame width.
	private int winHeight = height;// Initial frame height.
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

	public GUIBounce() {
		setLayout(null);// Use a null frame layout.
		setVisible(true);// Make the frame visible.
		makeSheet();
		//makeSheet();// Determines the sizes for the sheet.
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
		startButton.setLocation(center - 2 * (buttonW + buttonWS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		shapeButton.setLocation(center - (buttonW + buttonWS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		tailButton.setLocation(center - buttonW / 2, screenHeight + buttonHS + insets.top);
		clearButton.setLocation(center + (buttonW + buttonWS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		quitButton.setLocation(center + 2 * (buttonW + buttonWS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		speedBar.setLocation(insets.left + buttonWS, screenHeight + buttonHS + insets.top);
		speedLabel.setLocation(insets.left + buttonWS, screenHeight + buttonHS + BUTTON_H + insets.top);
		sizeBar.setLocation(winWidth - sbW - insets.right - buttonWS, screenHeight + buttonHS + insets.top);
		sizeLabel.setLocation(winWidth - sbW - insets.right - buttonWS, screenHeight + buttonHS + BUTTON_H + insets.top);

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
		setSize(winWidth, winHeight);// Set the frame size.
		screenWidth = winWidth - insets.left - insets.right;
		// Calculating the screen height.
		screenHeight = winHeight - insets.top - insets.bottom - 2 * (BUTTON_H + buttonHS);
		setSize(winWidth, winHeight);// Set the frame size.
		center = screenWidth / 2;// Determines the center of the screen.
		buttonW = screenWidth / 11;// Determine the width of the buttons.
		sbW = buttonW * 2;
		buttonWS = buttonW / 4;// Determine the button spacing.
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
	
	// Below are overwritten (but unimplemented) methods of the Frame() superclass and it's listeners.
	@Override
	public void componentMoved(ComponentEvent e)  {return;}
	@Override
	public void componentShown(ComponentEvent e)  {return;}
	@Override
	public void componentHidden(ComponentEvent e) {return;}
	@Override
	public void windowOpened(WindowEvent e)       {return;}
	@Override
	public void windowClosed(WindowEvent e)       {return;}
	@Override
	public void windowIconified(WindowEvent e)    {return;}
	@Override
	public void windowDeiconified(WindowEvent e)  {return;}
	@Override
	public void windowActivated(WindowEvent e)    {return;}
	@Override
	public void windowDeactivated(WindowEvent e)  {return;}
}

class Objc extends Canvas {
	static final long serialVersionUID = 11L;
	
	private final int BOUNCE_SPEED = 2;

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
		x = width/2;
		y = height/2;
		velocity = new Point(BOUNCE_SPEED, BOUNCE_SPEED);
		hasTail = true;
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

		// Check for boundaries and "bounce"
		/*if ((velocity.x > 0 && x + size / 2 > screenWidth - 4) || (velocity.x < 0 && x - size / 2 < 2))
			velocity.x = -velocity.x;
		if ((velocity.y > 0 && y + size / 2 > screenHeight - 4) || (velocity.y < 0 && y - size / 2 < 2))
			velocity.y = -velocity.y;*/
		
		checkOverlap();
	}

	@Override
	public void update(Graphics g) {
		if (clearFlag) {
			clearFlag = false;
			super.paint(g);
			g.setColor(Color.red);
			g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
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
			velocity.x = -BOUNCE_SPEED;
			x = screenWidth - 3 - size / 2;
		} else if (x - size / 2 < 2) {
			velocity.x = BOUNCE_SPEED;
			x = size / 2 + 1;
		}
		if (y + size / 2 > screenHeight - 4) {
			velocity.y = -BOUNCE_SPEED;
			y = screenHeight - 3 - size / 2;
		} else if (y - size / 2 < 2) {
			velocity.y = BOUNCE_SPEED;
			y = size / 2 + 1;
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
		g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
		update(g);
	}
}
