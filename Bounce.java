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
		- and chose rather the object moves, or does not move.
	
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
	
	
	// Defines <BUTTON_H>, the HEIGHT of all buttons within GUIBounce().
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

	// Defines imutable versions of the frame <WIDTH> and <HEIGHT>,
	// - <center> also is defined (mutable).
	private final int WIDTH = 640;
	private final int HEIGHT = 400;
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
	private Objc canvObj;

	private boolean isRunning = true;
	private boolean isPaused  = true;
	private boolean hasTail   = true;
	
	
	// Defines temporary (lowercase) forms of the current screen size,
	// - unlike the <final> definitions above, these are mutable (default values are <WIDTH> and <HEIGHT>.
	private int winWidth  = WIDTH;
	private int winHeight = HEIGHT;
	
	// Defines mutable placeholder variables for other measurement(s) used in makeSheet().
	private int winLeft;       // Left side of the frame.
	private int winTop;        // Top of the frame.
	private int screenWidth;   // Width of the rendering area.
	private int screenHeight;  // Height of the rendering area.

	// Creates an uninitalized Insets() instance, which is used to hold the insets of any instance of GUIBounce().
	private Insets insets;
	
	// Defines <thread>, which will execute a loop in tandem with run().
	private Thread thread;

	// Defines empty button() references which will become GUI components of the program.
	private Button runButton;
	private Button shapeButton;
	private Button tailButton;
	private Button clearButton;
	private Button quitButton;

	// Defines two labels for ScrollBar() instances (defined just below).
	private Label speedLabel = new Label("Speed", Label.CENTER);
	private Label sizeLabel = new Label("Size", Label.CENTER);

	// Defines two ScrollBar() instances, which 
	private Scrollbar sizeBar;
	private Scrollbar speedBar;




	/*
	The GUIBounce() constructor:
		Description: 
			- calls several subfunctions to initalize a GUIBounce() object. 
		Preconditions:
			N/A.
		Postconditions:
			- Returns an initalized instance of GUIBounce(), which will be running it's main loop (see start() and run() for details).
	*/
	public GUIBounce() {
		setLayout(null);  // - sets the layout to null,
		setVisible(true); // - makes the current window visible.
		
		// Calls makeSheet() to calibrate various size-adjacent variables (see makeSheet() for details).
		makeSheet();
		
		// Tries to initalize components* (adds them as listeners, populates them with default text),
		// - see initComponents() for implementation details.
		// * prints an (unlikely) error message, if initComponents() fails. 
		try {
			initComponents();
		} catch (Exception e) {e.printStackTrace();}
		
		
		// Sizes all items to fit the screen,
		// - see screenSize() for implementation details.
		sizeScreen();
		
		// Finally calls start(), which begins this class's main loop. 
		start();
		
		// returns, ending the function.
		return;
	}



	/*
	The makeSheet() method:
		Description: 
			- Calculates various mutable values used for component functionalty and resizing. 
		Preconditions:
			- N/A.
		Postconditions:
			- Mutates various mutable values inside of GUIBounce(), and also resets the background color.
	*/
	public void makeSheet() {

		// Fetches <inserts> from the Frame() super function getInserts().
		insets = getInsets();
		
		
		// Sets the current frame size to <win...> values within GUIBounce().
		setSize(winWidth, winHeight);
		
		// Calculates <screenWidth> (used by <canvObj>) to account for inset constants.
		screenWidth = winWidth - insets.left - insets.right;
		
		// - The same process as above is preformed for <screenHeight>.
		screenHeight = winHeight - insets.top - insets.bottom - 2 * (BUTTON_H + buttonHS);
		
		
		center = screenWidth / 2;   // - calculates the center of the screen,
		buttonW = screenWidth / 11; // - calculates the width of each Button(),
		buttonWS = buttonW / 4;     // - calculates the width of each button's whitespace,
		sbW = buttonW * 2;          // - calculates the width of each ScrollBar().
		
		// Sets the Frame()'s background (per redraw) to a solid color.
		this.setBackground(Color.lightGray);
		
		// returns, ending the function.
		return;
	}



	/*
	The initComponents() method:
		Description: 
			- Instantiates all component objects used by GUIBounce(),
				+ also adds all components to their relevant listeners. 
		Preconditions:
			- Must be called after makeSheet().
		Postconditions:
			- Populates all object-associated variables with their proper values, and adds them to implemented listeners of GUIBounce().
	*/
	public void initComponents() throws Exception, IOException {
		
		// Initalizes <canvObj>, giving it a screen-size, default vector-shape width, and setting it's background color,
		// - also adds <canvObj> to GUIBounce().
		canvObj = new Objc(objW, screenWidth, screenHeight);
		canvObj.setBackground(Color.white);
		this.add(canvObj);
		
		// Initalizes <speedBar>, giving it max/min values, setting it's initial value, and setting it's background color in the process.
		// - also adds <speedBar> to GUIBounce(), and adds it to the adjustment listener.
		speedBar = new Scrollbar(Scrollbar.HORIZONTAL);
		speedBar.setMaximum(sbMaxSpeed + SB_VIS);
		speedBar.setMinimum(sbMinSpeed);
		speedBar.setValue(sbSpeed);
		speedBar.setBackground(Color.gray);
		this.add(speedBar);
		speedBar.addAdjustmentListener(this);
		speedBar.setEnabled(true);
		speedBar.setVisible(true);
		// - also adds <speedBar>'s associated label.
		this.add(speedLabel);
		
		
		// Initalizes <runButton>, giving it a label before adding it to GUIBounce() and the Action Listener.
		runButton = new Button("Run");
		this.add(runButton);
		runButton.addActionListener(this);
		
		
		// Initalizes <shapeButton>, giving it a label before adding it to GUIBounce() and the Action Listener.
		shapeButton = new Button("Circle");
		this.add(shapeButton);
		shapeButton.addActionListener(this);
		
		// Initalizes <tailButton>, giving it a label before adding it to GUIBounce() and the Action Listener.
		tailButton = new Button("No Tail");
		this.add(tailButton);
		tailButton.addActionListener(this);
		
		// Initalizes <clearButton>, giving it a label before adding it to GUIBounce() and the Action Listener.
		clearButton = new Button("Clear");
		this.add(clearButton);
		clearButton.addActionListener(this);
		
		// Initalizes <quitButton>, giving it a label before adding it to GUIBounce() and the Action Listener.
		quitButton = new Button("Quit");
		this.add(quitButton);
		quitButton.addActionListener(this);


		// Initalizes <sizeBar>, giving it max/min values, setting it's initial value, and setting it's background color in the process.
		// - also adds <sizeBar> to GUIBounce(), and adds it to the adjustment listener.
		sizeBar = new Scrollbar(Scrollbar.HORIZONTAL);
		sizeBar.setMaximum(MAX_SIZE + SB_VIS);
		sizeBar.setMinimum(MIN_SIZE);
		sizeBar.setValue(INIT_SIZE);
		sizeBar.setBackground(Color.gray);
		this.add(sizeBar);
		sizeBar.addAdjustmentListener(this);
		sizeBar.setEnabled(true);
		sizeBar.setVisible(true);
		// - also adds <sizeBar>'s associated label.
		this.add(sizeLabel);


		// Makes misc. adjustments to GUIBounce(), including setting it's minimum and prefered sizes, and it's current bounds. 
		this.addComponentListener(this);
		this.addWindowListener(this);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMinimumSize(getPreferredSize());
		this.setBounds(winLeft, winTop, WIDTH, HEIGHT);


		// Calls the Frame() super function validate(), before ending the function.
		validate();
		
		// Returns, ending the function.
		return;
	}



	/*
	The sizeScreen() method:
		Description: 
			- Uses constants defined in makeSheet() to position/initalize various objects across GUIBounce's window. 
		Preconditions:
			- Must be called after makeSheet() and initComponents().
		Postconditions:
			- Manually positions (and sizes) all components nessesary for a functional instance of GUIBounce.
	*/
	private void sizeScreen() {
		
		// Sets the bounds of <canvObj>, which positions it relative to the upper left corner of the screen.
		canvObj.setBounds(insets.left, insets.top, screenWidth, screenHeight);


		// Positions <speedBar> relative to the lower left corner of the screen,
		// - also resizes <speedBar> using internal constants. 
		speedBar.setLocation
		(
			insets.left + buttonWS,              // Component X-Position*
			screenHeight + buttonHS + insets.top // Component Y-Position*
		);
		speedBar.setSize(sbW, SB_HEIGHT);
		// * this pattern is repeated for all subsiquent .setLocation() calls. 
		
		
		// Positions <speedLabel> relative to lower left the of the screen (below <speedbar>),
		// - also resizes <speedLabel> using internal constants. 
		speedLabel.setLocation
		(
			insets.left + buttonWS,
			screenHeight + buttonHS + BUTTON_H + insets.top
		);
		speedLabel.setSize(sbW, BUTTON_H);
		
		
		// Positions <runButton> relative to the far-left center of the screen,
		// - also resizes <runButton> using internal constants. 
		runButton.setLocation
		(
			center - 2 * (buttonW + buttonWS) - buttonW / 2,
			screenHeight + buttonHS + insets.top
		);
		runButton.setSize(buttonW, BUTTON_H);
		
		
		// Positions <shapeButton> relative to the left center of the screen,
		// - also resizes <shapeButton> using internal constants. 
		shapeButton.setLocation
		(
			center - (buttonW + buttonWS) - buttonW / 2,
			screenHeight + buttonHS + insets.top
		);
		shapeButton.setSize(buttonW, BUTTON_H);
		
		
		// Positions <tailButton> relative to the center of the screen,
		// - also resizes <tailButton> using internal constants. 
		tailButton.setLocation
		(
			center - buttonW / 2,
			screenHeight + buttonHS + insets.top
		);
		tailButton.setSize(buttonW, BUTTON_H);
		
		
		// Positions <clearButton> relative to the right center of the screen,
		// - also resizes <clearButton> using internal constants. 
		clearButton.setLocation
		(
			center + (buttonW + buttonWS) - buttonW / 2,
			screenHeight + buttonHS + insets.top
		);
		clearButton.setSize(buttonW, BUTTON_H);
		
		
		// Positions <quitButton> relative to the far-right center of the screen,
		// - also resizes <quitButton> using internal constants. 
		quitButton.setLocation
		(
			center + 2 * (buttonW + buttonWS) - buttonW / 2,
			screenHeight + buttonHS + insets.top
		);
		quitButton.setSize(buttonW, BUTTON_H);


		// Positions <sizeBar> relative to the lower right corner of the screen,
		// - also resizes <sizeBar> using internal constants. 
		sizeBar.setLocation
		(
			winWidth - sbW - insets.right - buttonWS,
			screenHeight + buttonHS + insets.top
		);
		sizeBar.setSize(sbW, SB_HEIGHT);
		
		
		// Positions <sizeLabel> relative to the lower right corner of the screen (below <sizeBar>),
		// - also resizes <sizeLabel> using internal constants. 
		sizeLabel.setLocation
		(
			winWidth - sbW - insets.right - buttonWS,
			screenHeight + buttonHS + BUTTON_H + insets.top
		);
		sizeLabel.setSize(sbW, BUTTON_H);
		
		// Returns, ending the function.
		return;
	}



	/*
	The start() method:
		Description: 
			- Finishes setup for a new instance of GUIBounce(), 
				+ also creates a new <thread> to handle execution.
		Preconditions:
			- Must be ran at after makeSheet(), initComponents(), and sizeScreen(). 
		Postconditions:
			- Initalizes, then starts <thread>, after setting the current <delay> to a default value.
	*/
	private void start() {
		// uses a constant formula to set the current ObjC() speed. 
		delay = sbMaxSpeed - speedBar.getValue() + 2;
		
		// Creates and starts a new thread (wrapped with an IF statement, just in case <thread> already is populated).
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		
		// returns, ending the function.
		return;
	}


	/*
	The run() method:
		Description: 
			- Iterates so long as <isRunning> is true, and calls <canvObj> depending on whether or not the program is currently paused.
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- The program is terminated alongside this function returning (see stop() for details).
	*/
	@Override
	public void run() {
		// Iterates so long as <isRunning> is true.
		while (isRunning) {
			
			// Updates <canvObj> whenever <isPaused> is false.
			if (!isPaused) {
				canvObj.updatePhysics();
				canvObj.repaint();
				
				// Sleeps for <delay>, which serves as an interpretation of the current <canvObj> speed.
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Lets the thread sleep for one tick, allowing for interupts.
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Returns, ending the function.
		return;
	}


	/*
	The stop() method:
		Description: 
			- Serves as a destructor for GUIBounce(), and a terminator for it's internal loop (see run()).
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- Frees memory taken up by GUIBounce()'s member variables and listeners, before returning.
	*/
	public void stop() {
		
		// Terminates the run() loop above.
		isRunning = false;
		
		
		// Removes all current member variable objects from their respective listeners.
		runButton.removeActionListener(this);
		shapeButton.removeActionListener(this);
		tailButton.removeActionListener(this);
		clearButton.removeActionListener(this);
		quitButton.removeActionListener(this);
		speedBar.removeAdjustmentListener(this);
		sizeBar.removeAdjustmentListener(this);
		this.removeComponentListener(this);
		this.removeWindowListener(this);

		// Calls cleanup functions, before ending the function.
		dispose();
		System.exit(0);
		
		// Returns, ending the function.
		return;
	}




	/*
	The adjustmentValueChanged() method:
		Description: 
			- Listens for AdjustmentEvents, and changes <speedBar> or <sizeBar> depending on their origin.
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- Properly changes the value(s) of either ScrollBar() object.
	*/
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		// Defines a temporary variable, <scrollVal>
		int scrollVal;
		
		// Points towards the ScrollBar() object that caused the AdjustmentEvent.
		Scrollbar sb = (Scrollbar) e.getSource();
		
		// Fetches the value associated with the AdjustmentEvent.
		scrollVal = sb.getValue();


		// Assigns the value(s) to their respective ScrollBar() instance, by comparing <sb> against either object.
		if (sb == speedBar) {
			delay = sbMaxSpeed - speedBar.getValue() + 2;
		} else if (sb == sizeBar) {
			scrollVal = (scrollVal / 2) * 2 + 1;
			canvObj.updateSize(scrollVal);
		}
	}



	/*
	The actionPerformed() method:
		Description: 
			- Listens for ActionEvents, and alters components of GUIBounce() depending on their source.
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- Properly alters internal variables, labels, or variables of <canvObj> depending on the button pressed.
	*/
	@Override
	public void actionPerformed(ActionEvent e) {
		// Fetches the source of the ActionEvent (stored in a generic Object variable).
		Object source = e.getSource();

		// Checks to see if <runButton> was pressed.
		if (source == runButton)
		{
			// Toggles <isPaused> and assigns <runButton> a relevant label.
			if (isPaused) {
				isPaused = false;
				runButton.setLabel("Stop");
			} else {
				isPaused = true;
				runButton.setLabel("Run");
			}
		}
		// Checks to see if <shapeButton> was pressed.
		else if (source == shapeButton)
		{
			// Toggles the current vector-shape within <canvObj>, and assigns a relevant label to <shapeButton>.
			if (shapeButton.getLabel() == "Circle") {
				shapeButton.setLabel("Square");
				canvObj.setRect(false);
			} else {
				shapeButton.setLabel("Circle");
				canvObj.setRect(true);
			}
			canvObj.repaint();
		}
		// Checks to see if <tailButton> was pressed.
		else if (source == tailButton)
		{
			// Toggles <hasTail>, passing the value to <canvObj> while assigning a relevant label to <tailButton>.
			if (hasTail) {
				hasTail = false;
				canvObj.setTail(false);
				tailButton.setLabel("Tail");
			} else {
				hasTail = true;
				canvObj.setTail(true);
				tailButton.setLabel("No Tail");
			}
		}
		// Checks to see if the <clearButton> was pressed.
		else if (source == clearButton)
		{
			// Calls functions inside of <canvObj> to clear and repaint the screen, if so.
			canvObj.clear();
			canvObj.repaint();
		}
		// Calls stop(), as the only Button left to compare to is <quitButton>
		else
		{
			stop();
		}
		
		// Returns, ending the function.
		return;
	}
	
	
	
	/*
	The componentResized() method:
		Description: 
			- Listens for ComponentEvents, and handles resizing the screen when they occur.
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- Calls several methods related to resizing the screen, before returning.
	*/
	@Override
	public void componentResized(ComponentEvent e) {
		// Updates <win...> variables to their new values.
		winWidth = getWidth();
		winHeight = getHeight();
		
		// Calls makeSheet() to update internal offsets/values.
		makeSheet();
		
		// Passes nessesary values from makeSheet() to <canvObj>.
		canvObj.reSize(screenWidth, screenHeight);
		
		// Calls the Frame() super function sizeScreen(), before ending the function.
		sizeScreen();
		
		// Returns, ending the function.
		return;
	}



	/*
	The windowClosing() method:
		Description: 
			- Terminates the program whenever the windowClosing() window event occurs.
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- Calls stop(), which assists in destructing GUIBounce().
	*/
	@Override
	public void windowClosing(WindowEvent e) {
		stop();
		return;
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




/*
The Objc() Class:
	Description:
		- Serves as an extension canvas() containing one vector-shape,
			+ this shape travels in a diagonal line until colliding with a screen-edge.
	Extends:
		- Canvas(), for use in tandem with GUIBounce().
	Implements:
		- N/A.
	Preconditions:
		- int <objSize> - inital size of Objc()'s vector-shape,
		- int <WIDTH>   - this instance's screenwidth,
		- int <HEIGHT>  - this instance's screenheight.
	Postconditions:
		- Constructor returns an instance of Objc().
*/
class Objc extends Canvas
{
	// Defines <serialVersionUID>, a universal identifier for this canvas class's instances.
	// - this variable is FINAL, and cannot be changed.
	static final long serialVersionUID = 11L;
	
	// Defines a constant, <BOUNCE_SPEED>*
	// * the distance (in pixels) that an instance of Objc()'s vector-shape will travel per updatePhysics() call.
	private final int BOUNCE_SPEED = 2;


	// Defines <screen...> variables which store the current alloted height/width an instance of Objc() has to render onto.
	private int screenWidth;
	private int screenHeight;
	
	// Defines various components that are used to construct a vector-shape within Objc() instances.
	private int x, y;        // Current X and Y positions,
	private int oldX, oldY;  // The X and Y positions of the last iteration,
	private Point velocity;  // The current vector-shape movement directions (Down-Right by default),
	private int size;        // The vector-shape's current size,
	private int lastSize;    // The vector-shape's last size.
	private boolean isRect;  // Whether or not the vector-shape is currently a rectangle,
	private boolean wasRect; // The state of the vector-shape's shape on the last iteration,
	private boolean hasTail; // Whether or not the vector-shape is meant to have a rendered tail.

	
	// Defines <clearFlag>, which is used to determine whether or not Objc() needs to be cleared next iteration.
	private boolean clearFlag;
	
	
	/*
	The Objc() constructor:
		Description: 
			- initalizes variables to their default values, mostly relative to <WIDTH> and <HEIGHT>. 
		Preconditions:
			- int <objSize> - inital size of Objc()'s vector-shape,
			- int <WIDTH>   - this instance's screenwidth,
			- int <HEIGHT>  - this instance's screenheight.
		Postconditions:
			- Returns an initalized instance of Objc().
	*/
	Objc(int objSize, int WIDTH, int HEIGHT)
	{
		// Stores the window's width and height.
		screenWidth = WIDTH;
		screenHeight = HEIGHT;
		
		// Stores the current vector-shape size.
		size = objSize;
		x = WIDTH/2;    // Sets the current X-Position to be the center of the screen,
		y = HEIGHT/2;   // Does the same as above with the current Y-Position.
		hasTail = true; // Gives the vector-shape a tail by default, 
		isRect = true;  // Makes the vector-shape a rectangle by default.
		// Sets the current velocity vector to (BOUNCE_SPEED, BOUNCE_SPEED).
		velocity = new Point(BOUNCE_SPEED, BOUNCE_SPEED);
		
		
		// Prevents the screen from being cleared until this flag is set again.
		clearFlag = false;
		
		// Returns, ending the function.
		return;

	}


	/*
	The setTail() method:
		Description: 
			- Changes this.<hasTail> to the value <hasTail>, and updates other member variables. 
		Preconditions:
			- bool <hasTail> - the new value for this.<hasTail>.
		Postconditions:
			- Changes this.<hasTail>, then returns.
	*/
	public void setTail(boolean hasTail) {
		
		// Sets this.<hasTail> to <hasTail>.
		this.hasTail = hasTail;
		
		// Updates <lastSize> to match the current size.
		lastSize = size;
		
		// Updates <wasRect> to match it's current counterpart.
		wasRect = isRect;
		
		// Updates <old...> to match their current counterparts.
		oldX = x;
		oldY = y;
		
		// Returns, ending the function.
		return;
	}



	/*
	The setRect() method:
		Description: 
			- Changes this.<isRect> to the value <isRect>. 
		Preconditions:
			- bool <isRect> - the new value for this.<isRect>.
		Postconditions:
			- Changes this.<isRect>, then returns.
	*/
	public void setRect(boolean isRect) {
		// Sets this.<hasTail> to <hasTail>.
		this.isRect = isRect;
		
		// Returns, ending the function.
		return;
	}



	/*
	The updatePhysics() method:
		Description: 
			- Increases <x> and <y>, before checking to see if Objc()'s vector shape would collide into anything. 
		Preconditions:
			- N/A.
		Postconditions:
			- Moves/adjusts Objc()'s vector shape's position, then calls checkOverlap().
	*/
	public void updatePhysics() {
		// Updates the vector-shape's position.
		x += velocity.x;
		y += velocity.y;
		
		// Calls checkOverlap(), which handles collision.
		checkOverlap();
		
		// Returns, ending the function.
		return;
	}


	/*
	The update() method:
		Description: 
			- Acts as the iterator for Objc(), which draws/clears the screen. 
		Preconditions:
			- Graphics <g> - Graphics compontent provided by Canvas().
		Postconditions:
			- Draws features onto the canvas and optionally clears the screen.
	*/
	@Override
	public void update(Graphics g)
	{
		// Clears the screen if <clearFlag> is set to true.
		if (clearFlag) {
			clearFlag = false;
			super.paint(g);
			g.setColor(Color.red);
			g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
		}

		// Draws over the last draw of vector-shape, assuming <hasTail> is false.
		if (!hasTail) {
			// Changes <g>'s current color.
			g.setColor(Color.white);
			
			// Draws an oval/rectangle, depending on what shape vector-shape was last.
			if (wasRect)
				g.fillRect(oldX - lastSize / 2, oldY - lastSize / 2, lastSize + 1, lastSize + 1);
			else
				g.fillOval(oldX - lastSize / 2 - 1, oldY - lastSize / 2 - 1, lastSize + 2, lastSize + 2);
		}
			
			
		// Updates <lastSize> to match the current size.
		lastSize = size;
		
		// Updates <wasRect> to match it's current counterpart.
		wasRect = isRect;
		
		// Updates <old...> to match their current counterparts.
		oldX = x;
		oldY = y;
		
		// Draws a circle or a rectangle, depending on whether <isRect> is true or not. 
		if (isRect)
		{
			g.setColor(Color.lightGray);
			g.fillRect(x - size / 2, y - size / 2, size, size);
			g.setColor(Color.black);
			g.drawRect(x - size / 2, y - size / 2, size, size);
		}
		else
		{
			g.setColor(Color.lightGray);
			g.fillOval(x - size / 2, y - size / 2, size, size);
			g.setColor(Color.black);
			g.drawOval(x - size / 2, y - size / 2, size, size);
		}
		
		// Returns, ending the function.
		return;
	}



	/*
	The reSize() method:
		Description: 
			- Handles resizing of the space alloted to Objc().
		Preconditions:
			- int <screenWidth>  - the screen's new width,
			- int <screenHeight> - the screen's new height. 
		Postconditions:
			- Updates this.<screen...> variables, then calls checkOverlap() and repaint() to update the screen.
	*/
	public void reSize(int screenWidth, int screenHeight) {
		
		// Stores the new screen width and height.
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		// Calls functions for vector-shape repositioning and cleanup.
		checkOverlap();
		repaint();
		
		// Returns, ending the function.
		return;
	}

	/*
	The checkOverlap() method:
		Description: 
			- Repositions/bounces vector-shape, when the shape or current screen size changes.
		Preconditions:
			- N/A.
		Postconditions:
			- Repositions/reflects the current vector-shape if it's coordinates are outside the bounds set by <screen...> variables.
	*/
	public void checkOverlap() {
		// Checks to see if the current vector-shape is in excess of the right side of the screen,
		// - reflects the current velocity vector x (negative), if so.
		if (x + size / 2 > screenWidth - 4) {
			velocity.x = -BOUNCE_SPEED;
			x = screenWidth - 3 - size / 2;
		}
		// Otherwise checks to see if the current vector-shape is in excess of the left side of the screen,
		// - reflects the current velocity vector x (positive), if so.
		else if (x - size / 2 < 2) {
			velocity.x = BOUNCE_SPEED;
			x = size / 2 + 1;
		}
		// Checks to see if the current vector-shape is in excess of the bottom of the screen,
		// - reflects the current velocity vector y (negative), if so.
		if (y + size / 2 > screenHeight - 4) {
			velocity.y = -BOUNCE_SPEED;
			y = screenHeight - 3 - size / 2;
		}
		// Otherwise checks to see if the current vector-shape is in excess of the top of the screen,
		// - reflects the current velocity vector y (positive), if so.
		else if (y - size / 2 < 2) {
			velocity.y = BOUNCE_SPEED;
			y = size / 2 + 1;
		}
		
		// Returns, ending the function.
		return;
	}



	/*
	The updateSize() method:
		Description: 
			- Changes the this.<size> of the current vector shape (replaced by <size>.
		Preconditions:
			- int <size> - the value that will be replacing this.<size>.
		Postconditions:
			- Replaces this.<size> with <size>, then checks for overlaps and repaints the canvas.
	*/
	public void updateSize(int size) {
		this.size = size;
		checkOverlap();
		repaint();
		
		// Returns, ending the function.
		return;
	}



	/*
	The clear() method:
		Description: 
			- sets <clearFlag> to true, allowing for a clear to occur in update() next iteration.
		Preconditions:
			- N/A.
		Postconditions:
			- sets <clearFlag> to true, allowing for a clear to occur in update() next iteration.
	*/
	public void clear() {
		clearFlag = true;
		
		// Returns, ending the function.
		return;
	}



	/*
	The paint() method:
		Description: 
			- Overwrites Canvas().paint(), and uses it to draw a default screen border instead.
		Preconditions:
			- Graphics <g> - Graphics compontent provided by Canvas().
		Postconditions:
			- draws the screen border, before updating <g> and returning.
	*/
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.red);
		g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
		update(g);
		
		// Returns, ending the function.
		return;
	}
}