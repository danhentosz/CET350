/*
.Program6, Bounce: Newton's Third     (CannonVSBall.java).
.Created By:                                             .
- Nathaniel Dehart   (DEH5850@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu),                 .
- Daniel Hentosz,    (HEN3883@calu.edu).                 .
.Last Revised: April 13th, 2021.              (4/13/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
Description:
	Makes use of java's <awt> library to create an interactive GUI, which contains:
		- a canvas panel    (see Ball() and BulletBounce()),
		- a control panel   (see BulletBounce()),
		- a container frame (see BulletBounce()),
		- a dropdown menu   (see BulletBounce).

	By interacting with this GUI's controls, the user can:
		- run, pause, reset, or quit the program (via dropdown menu),
		- change the size and speed of a ball that bounces along the screen (via two submenus 'sizes' and 'speeds'),
		- change the gravity of the environment a cannon ball (physics object) embodies (via dropdown menu 'environments),
		- change the inital velocity of a physics projectile (left scrollbar),
		- change the inital angle of a physics projectile (right scrollBar).
		
	Alternatively, by clicking on the canvas panel, the user can:
		- hold left mouse click (mouse 1) and drag to create a rectangle,
			+ upon release, the rectangle is added to the screen,
				* the ball bouncing around within the canvas panel will ricochet off of this rectangle,
				* if a physics object collides with the rectangle, it will be deleted,
				* if the rectangle contained any other, smaller rectangles entirely, they will be deleted,
		- click mouse 1 twice once on a rectangle to delete it (and all others under it) from the canvas,
		- click on the graphical representation of a cannon once, to launch a new physics object,
			+ if the physics object leaves the screen (and assuredly cannot return), the player is allowed to launch another,
			+ if the physics object collides with the cannon, the 'ball' gains a point (and the canvas resets),
			+ if the physics object collides with the 'ball', the player gains a point (and the canvas resets).
			
	For more implementation details, see the class header at BulletBounce().
*/




// Packages the program into a folder called "CannonVSBall",
// When compiling this file via javac, intended command notation is "javac -d . CannonVSBall.java",
// - intended run notation is "java CannonVSBall.CannonVSBall" (contains main() method of this file).
package CannonVSBall;



// Imports components required for Frame(), Runnable(), and various action listeners. 
// ...lang.Thread() is also imported,
// ...image.BufferedImage() also is imported for use in Ball().
import java.awt.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.Thread;
import java.util.Vector;

/*
The CannonVSBall() Class:
	Description:
		- serves as a container class for main() (see block comment below),
	Extends:
		- N/A.
	Implements:
		- N/A.
	Preconditions:
		- shared with main() (see below),
	Postconditions:
		- Constructor returns an instance of BulletBounce().
*/
public class CannonVSBall {
	
	
	/*
	The main() method:
		Description: 
			- Opens a new instance of BulletBounce(). 
		Preconditions:
			N/A: method ignores values in <args>[].
		Postconditions:
			- Creates a new instance of BulletBounce, <gui>,
				+ from here, implemented methods loop until the program is exited.
	*/
	public static void main(String[] args) {
		BulletBounce gui = new BulletBounce();
		return;
	}
}



/*
The BulletBounce() Class:
	Description:*
		- serves as a container window (Frame()) for an instance of Ball() (and several <...awt...> GUI components),
		- allows interface with Ball() via several UI elements(),
			+ Run,  Pause          - Menu/Hotkey (Toggles between running and pausing Ball()'s iterations),
			+ Reset                - Menu        (Resets the position/state of all objects on Ball()'s canvas),
			+ Quit                 - Menu        (Exits the program (functionality is the same as clicking the [x] at the upper right),
			+ Speed Controls       - Menu        (Controls Ball()'s current iteraton speed; Extra Slow ... Extra Fast),
			+ Size Controls        - Menu        (Controls Ball()'s "Ball" size; Extra Small ... Extra Large),
			+ Environment Controls - Menu        (Sets the current force of gravity on Ball()'s main physics object),
			+ Velocity Controls    - Scroll      (Sets the inital velocity of Ball()'s main physics object; higher = faster).
			+ Angle Controls       - Scroll      (Sets the inital angle of Ball()'s main physics object; higher = higher).
	    * - this class also hosts functionality from Ball(). See that class's header comment for more informatjon.
	Implements:*
		- WindowListener()     - for window related events (open, close, etc...),
		- ComponentListener()  - for certain java.awt... components (Button(), ScrollBar()),
		- ActionListener()     - for action(s) taken by the user's computer's peripherals,
		- AdjustmentListener() - for adjustment(s) to certain components (ScrollBar()),
		- ItemListener()       - for user interaction with certain kinds of MenuItem() subclasses,
		- Runnable()           - for the use of Thread() throughout instances of BulletBounce(),
		* - this class also hosts listeners from Ball(). See that class's header comment for more informatjon.
	Preconditions:
		- N/A.
	Postconditions:
		- Constructor returns an instance of BulletBounce(),
			+ internal methods (makeSheet(), initComponents(), sizeScreen(), start()) are also ran.
				- start() begins an internal loop, which can only be terminated by user input (clicking on "Quit" or [x]).
*/
class BulletBounce implements ActionListener, WindowListener, ComponentListener, AdjustmentListener, ItemListener, Runnable
{
	// Defines <serialVersionUID>, a universal identifier for this frame class's
	// instances.
	// - this variable is FINAL, and cannot be changed.
	static final long serialVersionUID = 10L;
	
	// Defines two Panel() objects, which serve as sub-containers for components under Frame().
	private Panel control_panel; // - holds control components,
	private Panel ball_panel;    // - holds Ball().
	
	// Defines mutable versions of the frame <width>, <height>, and <center>.
	private int winWidth = 640;
	private int winHeight = 400;
	private int oldWinWidth;
	private int oldWinHeight;
	
	// Defines <INIT_SIZE>, which is carried onto objW (the mutable component
	// width).
	private final int INIT_SIZE = 40;

	// Defines the starting size for Ball's shape.
	private int size = 40;
	
	private final int SB_ANGLE_MIN = 90;
	private final int SB_ANGLE_MAX = 180;

	// Defines <SB_ANGLE>, the starting value of the ScrollBar("Angle").
	private final int SB_ANGLE = (SB_ANGLE_MAX + SB_ANGLE_MIN) / 2;
	
	// Defines the minimum, maximum, and current speed values which can be held by a
	// ScrollBar() instance (defined in initComponents()).
	private final int SB_MIN_VELOCITY = 100;
	private final int SB_MAX_VELOCITY = 600;

	// Defines <SB_VELOCITY>, the starting value of the ScrollBar("Velocity").
	private final int SB_VELOCITY = (SB_MIN_VELOCITY + SB_MAX_VELOCITY) / 2;

	// Defines <SB_VIS>, a constant added to the width of ScrollBar() instances.
	private final int SB_VIS = 10;


	// Defines <radioButton> a string which passes the pressed radio-button-checkbox to Ball() and BulletBounce().
	private String radioButton = "";

	// Defines delay, a default value transformed by the current value of <sbSpeed>.
	private int delay = 25;

	// Defines the mutable velocity value, sbVelocity. 
	private int sbVelocity = SB_VELOCITY;

	// Defines the mutable time (milliseconds) and time (seconds) values.
	// - these values are rough estimates, and are mostly used for staggering updates inside of run().
	private int time_ms = 0;
	private int time_s  = 0;

	// Defines <screenReset>, a boolean that tells run() when the reset button is pressed. 
	private boolean screenReset = false;

	// Defines the main Frame() object.
	private Frame BulletFrame;
	
	// Defines the main menubar.
	private MenuBar menuBar;

	// Defines <menuControl>, and it's associated menu items.
	private Menu menuControl;
	private MenuItem Run;
	private MenuItem Pause;
	private MenuItem Reset;
	private MenuItem Quit;

	// Defines <menuSize>, it's submenus, and their menu items.
	private Menu menuSize;
	private Menu menuSizeSelector;
	private CheckboxMenuItem sizeXSmall;
	private CheckboxMenuItem sizeSmall;
	private CheckboxMenuItem sizeMedium;
	private CheckboxMenuItem sizeLarge;
	private CheckboxMenuItem sizeXLarge;
	private Menu menuSpeed;
	private CheckboxMenuItem speedXSlow;
	private CheckboxMenuItem speedSlow;
	private CheckboxMenuItem speedMedium;
	private CheckboxMenuItem speedFast;
	private CheckboxMenuItem speedXFast;
		
	// Defines <menuEnvironment>, and it's associated menu items.
	private Menu menuEnvironment;
	private CheckboxMenuItem mercury;
	private CheckboxMenuItem venus;
	private CheckboxMenuItem earth;
	private CheckboxMenuItem earthsMoon;
	private CheckboxMenuItem mars;
	private CheckboxMenuItem jupiter;
	private CheckboxMenuItem saturn;
	private CheckboxMenuItem uranus;
	private CheckboxMenuItem neptune;
	private CheckboxMenuItem pluto;


	// Defines <ball>, which serves as this Frame()'s implementation of a canvas
	// (via an instance of a class that extends to Canvas()).
	private Ball ball;


	// Defines two control booleans, which are used as logic/loop controls later in the program.
	private boolean isRunning = true; // controls whether or not BulletBounce()'s thread iterates,
	private boolean isPaused = true;  // controls whether or not BulletBounce()'s Ball() updates.

	// Defines <thread>, which is used to run continuous code after this class is instantiated. 
	private Thread thread;

	// Defines an instance of GridBagConstraints(), which is used in tandem with <control_panel>.
	GridBagConstraints gbc;


	// Defines two instances of Label(), which indicate which scrollbar is for Ball Speed, and which is for Ball Size.
	private Label velocityLabel;
	private Label angleLabel;
	
	// Defines misc. labels used for the ball and player's scores, the current time, and the current UI message.
	private Label ballLabel;
	private Label cannonLabel;
	private Label timeLabel;
	private Label messageLabel;
	
	
	// Defines two scrollbars (which allow the user to change the velocity and speed of the physics object within Ball()).
	private Scrollbar velocityBar;
	private Scrollbar angleBar;

	// Defines various text fields, which display the ball's score, player's score, and the current time elapsed. 
	private TextField ballScoreField;
	private TextField cannonScoreField;
	private TextField timeField;
	
	// Defines integers to hold the ball and player's score values. 
	private int ballScore = 0;
	private int cannonScore = 0;

	/*
	The BulletBounce() constructor:
		Description: 
			- calls several subfunctions to initalize a BulletBounce() object. 
		Preconditions:
			N/A.
		Postconditions:
			- Returns an initalized instance of BulletBounce(), which will be running it's main loop (see start() and run() for details).
	*/
	public BulletBounce()
	{
		// Instantiates the main frame object.
		BulletFrame = new Frame();
		
		// Sets the Frame()'s layout to null.
		BulletFrame.setLayout(new BorderLayout());
		
		// Makes the Frame() visible.
		BulletFrame.setVisible(true);
		
		// Initalizes this Frame()'s components*
		// *this also sizes some components which require dimensions for initalization. See initComponents() for details.
		try {
			initComponents();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Calls makeSheet() to determine some component/screen sizes.
		makeSheet();
		
		// Exits the constructor, and finishes initalization with the start() method.
		start();
	}


	/*
	The start() method:
		Description: 
			- Starts the current thread, checking to see if it exists in the process (this prevents the thread from somehow being overwritten, if this method is ran twice.
		Preconditions:
			N/A.
		Postconditions:
			- Starts the main loop (see run() for details).
	*/
	private void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}



	/*
	The makeSheet() method:
		Description: 
			- Calculates various mutable values used for component functionalty and resizing. 
		Preconditions:
			- N/A.
		Postconditions:
			- Mutates some mutable values inside of BulletBounce(), mostly through the use of Ball()*
			* other components can be dynamically resized on their own, due to utilizing layout managers.
	*/
	private void makeSheet()
	{
		// Makes sure that <ball> is instantiated before continuing.
		if (ball != null)
		{
			// Attempts to resize the canvas using <ball_panel>'s size.
			if (!ball.tryResizeCanvas(ball_panel.getWidth(), ball_panel.getHeight())) {
				BulletFrame.setSize(oldWinWidth, oldWinHeight);
				BulletFrame.validate();
				winWidth = oldWinWidth;
				winHeight = oldWinHeight;
			}
		}
		
		// Returns, ending the function.
		return;
	}



	/*
	The initComponents() method:
		Description: 
			- Instantiates all component objects used by BulletBounce(),
				+ also adds all components to their relevant listeners. 
		Preconditions:
			- Must be called after makeSheet().
		Postconditions:
			- Populates all object-associated variables with their proper values, and adds them to implemented listeners of BulletBounce().
	*/
	private void initComponents() throws Exception, IOException {
		
		// Initalizes the menu bar.
		menuBar = new MenuBar();
		
		// Initalizes <menuBar>'s first submenu, controls,
		// - this codeblock also initalizes each associated menuitem,
		// - this codeblock also adds a seperator between 'run, pause' and 'reset, quit'.
		menuControl = new Menu("Controls");
		Run         = menuControl.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
		Pause       = menuControl.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
		menuControl.addSeparator();
		Reset       = menuControl.add(new MenuItem("Restart"));
		Quit        = menuControl.add(new MenuItem("Quit"));
		menuBar.add(menuControl);
		
		// Initalizes <menuBar>'s second submenu, size. 
		menuSize = new Menu("Size");
		
		// Initalizes <menuSize>'s first submenu, sizes,
		// - this codeblock also initalizes each associated menuitem.
		menuSizeSelector = new Menu("Sizes");
		sizeXSmall       = new CheckboxMenuItem("Extra Small");
		menuSizeSelector.add(sizeXSmall);
		sizeXSmall.addItemListener(this);
		
		sizeSmall        = new CheckboxMenuItem("Small");
		menuSizeSelector.add(sizeSmall);
		sizeSmall.addItemListener(this);
		
		sizeMedium       = new CheckboxMenuItem("Medium");
		menuSizeSelector.add(sizeMedium);
		sizeMedium.addItemListener(this);
		
		sizeLarge        = new CheckboxMenuItem("Large");
		menuSizeSelector.add(sizeLarge);
		sizeLarge.addItemListener(this);
		
		sizeXLarge       = new CheckboxMenuItem("Extra Large");
		menuSizeSelector.add(sizeXLarge);
		sizeXLarge.addItemListener(this);
		menuSize.add(menuSizeSelector);
		
		

		

		// Initalizes <menuSize>'s second submenu, speeeds,
		// - this codeblock also initalizes each associated menuitem.
		menuSpeed  = new Menu("Speeds");
		speedXSlow       = new CheckboxMenuItem("Extra Slow");
		menuSpeed.add(speedXSlow);
		speedXSlow.addItemListener(this);
		
		speedSlow        = new CheckboxMenuItem("Slow");
		menuSpeed.add(speedSlow);
		speedSlow.addItemListener(this);
		
		speedMedium       = new CheckboxMenuItem("Medium");
		menuSpeed.add(speedMedium);
		speedMedium.addItemListener(this);
		
		speedFast        = new CheckboxMenuItem("Fast");
		menuSpeed.add(speedFast);
		speedFast.addItemListener(this);
		
		speedXFast       = new CheckboxMenuItem("Extra Fast");
		menuSpeed.add(speedXFast);		
		speedXFast.addItemListener(this);
		
		menuSize.add(menuSpeed);
		menuBar.add(menuSize);
	

		
		
		// Initalizes <menuBar>'s last submenu, environments,
		// - this codeblock also initalizes each associated menuitem.
		menuEnvironment = new Menu("Environments");
		mercury         = new CheckboxMenuItem("Mercury");
		menuEnvironment.add(mercury);
		mercury.addItemListener(this);
		
		venus           = new CheckboxMenuItem("Venus");
		menuEnvironment.add(venus);
		venus.addItemListener(this);
		
		earth           = new CheckboxMenuItem("Earth");
		menuEnvironment.add(earth);
		earth.addItemListener(this);
		
		earthsMoon      = new CheckboxMenuItem("Earth's Moon");
		menuEnvironment.add(earthsMoon);
		earthsMoon.addItemListener(this);
		
		mars            = new CheckboxMenuItem("Mars");
		menuEnvironment.add(mars);
		mars.addItemListener(this);
		
		jupiter         = new CheckboxMenuItem("Jupiter");
		menuEnvironment.add(jupiter);
		jupiter.addItemListener(this);
		
		saturn          = new CheckboxMenuItem("Saturn");
		menuEnvironment.add(saturn);
		saturn.addItemListener(this);
		
		uranus          = new CheckboxMenuItem("Uranus");
		menuEnvironment.add(uranus);
		uranus.addItemListener(this);
		
		neptune         = new CheckboxMenuItem("Neptune");
		menuEnvironment.add(neptune);
		neptune.addItemListener(this);
		
		pluto           = new CheckboxMenuItem("Pluto");
		menuEnvironment.add(pluto);
		pluto.addItemListener(this);
		menuBar.add(menuEnvironment);
		
		
		// Sets each radio-button set's default values.
		sizeMedium.setState(true);
		speedMedium.setState(true);
		earth.setState(true);
		Pause.setEnabled(false);
		
		
		// Initalizes the program's information labels. 
		velocityLabel = new Label("Velocity", Label.CENTER);
		angleLabel    = new Label("Angle", Label.CENTER);
		ballLabel     = new Label  ("Ball Score");
		cannonLabel   = new Label("Player Score");
		timeLabel     = new Label("Time");
		messageLabel  = new Label("");
		ballScoreField   = new TextField("0");
		cannonScoreField = new TextField("0");
		timeField        = new TextField("0");
		
		
		// Sets the constant value(s) associated with the velocity scrollBar(),
		// - also changes the background color of this component to gray.
		velocityBar = new Scrollbar(Scrollbar.HORIZONTAL);
		velocityBar.setMaximum(SB_MAX_VELOCITY + SB_VIS);
		velocityBar.setMinimum(SB_MIN_VELOCITY);
		velocityBar.setValue(sbVelocity);
		velocityBar.setBackground(Color.gray);


		// Sets the constant value(s) associated with the angle scrollBar(),
		// - also changes the background color of this component to gray.
		angleBar = new Scrollbar(Scrollbar.HORIZONTAL);
		angleBar.setMaximum(SB_ANGLE_MAX + SB_VIS);
		angleBar.setMinimum(SB_ANGLE_MIN);
		angleBar.setValue(SB_ANGLE);
		angleBar.setBackground(Color.gray);
		
		// Disables each of the cosmetic text fields (as they are not meant to have text entered into them).
		ballScoreField.setEnabled(false);
		cannonScoreField.setEnabled(false);
		timeField.setEnabled(false);
		
		
		// Sets each label's color to light gray.
		ballLabel.setBackground(Color.lightGray);
		cannonLabel.setBackground(Color.lightGray);
		timeLabel.setBackground(Color.lightGray);
		messageLabel.setBackground(Color.lightGray);


		// Initalizes <gbl> and <gbc>, which are used together to constrain components within BulletBounce().
		GridBagLayout gbl = new GridBagLayout();
		gbc               = new GridBagConstraints();


		// Initalizes the col/row/width/height dimensions used for <gbl>,
		// - since a set of GridBagConstraints are used alongside these values, they all get a weight of 1.
		double colweight[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		double rowweight[] = { 1, 1 };
		int width[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		int height[] = { 1, 1 };

		// Assigns the templates defined above to <gbl>.
		gbl.rowHeights    = height;
		gbl.columnWidths  = width;
		gbl.columnWeights = colweight;
		gbl.rowWeights    = rowweight;
		
		// - initalizes both of Frame()'s UI panels
		control_panel = new Panel(gbl);
		ball_panel    = new Panel();
		ball_panel.setLayout(new BorderLayout(0, 0));
		
		// Sets some constraints which are shared by all elements of <control_panel>.
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		
		
		// Moves on to individual component addition (see addToControlPanel() for details).
		addToControlPanel(velocityBar,   1,0,3,1);
		addToControlPanel(velocityLabel, 1,1,3,1);
		addToControlPanel(angleBar,   12,0,3,1);
		addToControlPanel(angleLabel, 12,1,3,1);
		addToControlPanel(ballScoreField, 5,0,1,1);
		addToControlPanel(ballLabel,      5,1,1,1);
		addToControlPanel(cannonScoreField, 10,0,1,1);
		addToControlPanel(cannonLabel,      10,1,1,1);
		addToControlPanel(timeField, 7,0,2,1);
		addToControlPanel(timeLabel, 7,1,2,1);
		addToControlPanel(messageLabel, 1,2,5,1);
		
		// Adds components to their relevant listeners.
		velocityBar.addAdjustmentListener(this);
		angleBar.addAdjustmentListener(this);
		
		// Adds action listeners to any regular MenuItem() instances. 
		Run.addActionListener(this);	
		Pause.addActionListener(this);
		Reset.addActionListener(this);
		Quit.addActionListener(this);
		

		// Adds listeners (and either Panel()) to Frame().
		BulletFrame.addComponentListener(this);
		BulletFrame.addWindowListener(this);
		BulletFrame.setPreferredSize(new Dimension(winWidth, winHeight));
		BulletFrame.setMinimumSize(BulletFrame.getPreferredSize());

		BulletFrame.setMenuBar(menuBar);
		BulletFrame.add(control_panel, BorderLayout.SOUTH);
		BulletFrame.add(ball_panel, BorderLayout.CENTER);

		// Sets either Panel()'s background color to the relevant value.
		control_panel.setBackground(Color.lightGray);
		ball_panel.setBackground(Color.white);

		// Sets the background color of both Label() objects to something matching the control Panel().
		velocityLabel.setBackground(Color.lightGray);
		angleLabel.setBackground(Color.lightGray);


		// Makes both Panel() instances visible.
		control_panel.setVisible(true);
		ball_panel.setVisible(true);


		// Enables speedBar(), and makes it visible. 
		velocityBar.setEnabled(true);
		angleBar.setVisible(true);
		
		// Calls the inherited function validate(), which reduces the available space within Frame().
		BulletFrame.validate();

		// Finally initalizes <ball>, since the screen is populated enough for it to safely be instantiated. 
		ball = new Ball(size, ball_panel.getWidth(), ball_panel.getHeight());
		ball_panel.add("Center", ball);
		// Sets Ball()'s default values.
		ball.setCannonAngle(SB_ANGLE_MAX - SB_ANGLE + SB_ANGLE_MIN);
		ball.setGrav(ball.gravRule("Earth"));
		ball.setProjectileVel(velocityBar.getValue() * .005f);

		// Validates the Frame() again, before returning.
		BulletFrame.validate();
		
		// Returns, ending the function.
		return;
	}



	/*
	The addToControlPanel() method:
		Description: 
			- A simple, macro-like function that obfuscates the process of adding a component to <control_panel>.
		Preconditions:
			- <component> : must be a valid awt Component() class,
			- <gridx>     : must be a valid integer, (component position, x)
			- <gridy>     : must be a valid integer, (component position, y)
			- <width>     : must be a valid integer, (component width)
			- <height>    : must be a valid integer. (component height)
		Postconditions:
			- Appends component to <control_panel>, feeding the integer value(s) to <gbc> beforehand.
	*/
	private void addToControlPanel(Component component, int gridx, int gridy, int width, int height) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		control_panel.add(component,gbc);
		
		// Returns, ending the function.
		return;
	}
	
	

	/*
	The stop() method:
		Description: 
			- Serves as a destructor for BulletBounce(), and a terminator for it's internal loop (see run()).
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- Frees memory taken up by BulletBounce()'s member variables and listeners, before returning.
	*/
	public void stop() {
		
		// Terminates the active <thread>'s loop (see run() for details).
		isRunning = false;

		// calls a seperate method for stipping <ball> of it's listeners (see removeListeners() below).
		ball.removeListeners();

		// Finally, removes any listeners still attached to the Frame().
		BulletFrame.removeComponentListener(this);
		BulletFrame.removeWindowListener(this);

		// Disposes of the size radio button's listeners.
		sizeXSmall.removeItemListener(this);
		sizeSmall.removeItemListener(this);
		sizeMedium.removeItemListener(this);
		sizeLarge.removeItemListener(this);
		sizeXLarge.removeItemListener(this);
		
		// Disposes of the speed radio button's listeners.
		speedXSlow.removeItemListener(this);
		speedSlow.removeItemListener(this);
		speedMedium.removeItemListener(this);
		speedFast.removeItemListener(this);
		speedXFast.removeItemListener(this);
		
		// Disposes of the environment radio button's listeners.
		mercury.removeItemListener(this);
		venus.removeItemListener(this);
		earth.removeItemListener(this);
		earthsMoon.removeItemListener(this);
		mars.removeItemListener(this);
		jupiter.removeItemListener(this);
		saturn.removeItemListener(this);
		uranus.removeItemListener(this);
		neptune.removeItemListener(this);
		pluto.removeItemListener(this);
		
		
				
		// Disposes of either scrollbar's listeners.
		velocityBar.removeAdjustmentListener(this);
		angleBar.removeAdjustmentListener(this);
		
		// Disposes of the control menu's listeners.
		Run.removeActionListener(this);	
		Pause.removeActionListener(this);
		Reset.removeActionListener(this);
		Quit.removeActionListener(this);
		
		
		// Calls cleanup functions, before ending the function.
		BulletFrame.dispose();
		System.exit(0);
		
		// Returns, ending the function.
		return;
	}
	
	

	/*
	The run() method:
		Description: 
			- Iterates so long as <isRunning> is true, and calls <ball> depending on whether or not the program is currently paused.
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- The program is terminated alongside this function returning (see stop() for details).
	*/
	@Override
	public void run() {
		// Iterates so long as <isRunning> is true.
		while (isRunning) {
			// Lets the thread sleep for one tick, allowing for interupts.
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Updates the game's elements whenever <isPaused> is false.
			if (!isPaused)
			{
				// Increments the milliseconds counter.
				time_ms += 1;

				// Updates the Ball() object, depending on the internal value of <delay>.
				if ((time_ms % (int)(delay/2)) == 0)
				{
					ball.updateBall();
				}
				// Updates Ball()'s primary physic's object.
				ball.updateProjectile();
				
				// Displays any messages that Ball() has stored from previous iterations. 
				if (ball.hasMessage()) {
					messageLabel.setText(ball.getMessage());
				}
				// Checks to see if the gamestate must be reset, and increments points accordingly. 
				if (ball.ballWasHit()) {
					cannonScore++;
					cannonScoreField.setText(Integer.toString(cannonScore));
					ball.setMessage("You scored a point!");
					ball.reset();
				} else if (ball.cannonWasHit()) {
					ballScore++;
					ballScoreField.setText(Integer.toString(ballScore));
					ball.setMessage("The ball scored a point...");
					ball.reset();
				}
				
				// Increments <time_s> whenever an arbitrary value has been reached,
				// - this value is roughly accurate to the passing of real time, but is not meant to be equivalent. 
				if((time_ms % 350) == 0)
				{
					time_s += 1;
					timeField.setText(Integer.toString(time_s));
					time_ms = 0;
				}
				
				// Finally, repaints the screen. 
				ball.repaint();
			}
			// Otherwise, repaints the screen whenever the reset button has been pressed. 
			else if (screenReset)
			{
				ball.repaint();
				screenReset = false;
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
The itemStateChanged() method:
	Description: 
		- handles selections of any CheckboxMenuItem() instances.
	Preconditions:
		- the start() method must have had been ran already. 
	Postconditions:
		- Passes string values to various methods (see methods with the suffix 'rule'.
*/
 @Override
 public void itemStateChanged(ItemEvent e)
 {
	// Stores the currently selected checkbox in a dummy value.
	CheckboxMenuItem dummy = (CheckboxMenuItem)e.getSource();
	
	// Fetches that Checkbox's current text.
	radioButton = dummy.getLabel();
	
	
	// Compares the selected value against the submenu 'sizes'.
	if (dummy == sizeXSmall || dummy == sizeSmall || dummy == sizeMedium || dummy == sizeLarge || dummy == sizeXLarge)
	{
		// Changes the ball object's current size, if able.
		if(ball.tryUpdateSize(ball.sizeRule(radioButton)))
		{
			sizeXSmall.setState(false);
			sizeSmall.setState(false);
			sizeMedium.setState(false);
			sizeLarge.setState(false);
			sizeXLarge.setState(false);
			dummy.setState(true);
		}
		// Reverts the menu selection, otherwise.
		else dummy.setState(false);;

	}
	
	// Compares the selected value against the submenu 'speeds'.
	else if (dummy == speedXSlow || dummy == speedSlow || dummy == speedMedium || dummy == speedFast || dummy == speedXFast)
	{
		// Changes the current game speed, if able.
		delay = speedRule(radioButton);
		speedXSlow.setState(false);
		speedSlow.setState(false);
		speedMedium.setState(false);
		speedFast.setState(false);
		speedXFast.setState(false);
		dummy.setState(true);

	}
	// Compraes the selected value against the submenu 'environment'. 
	else if (dummy == mercury || dummy == venus || dummy == earth || dummy == earthsMoon || dummy == mars || dummy == jupiter ||
	dummy == saturn || dummy == uranus || dummy == neptune || dummy == pluto)
	{
		// Sets the ball object's current gravity.
		ball.setGrav(ball.gravRule(radioButton));
		mercury.setState(false);
		venus.setState(false);
		earth.setState(false);
		earthsMoon.setState(false);
		mars.setState(false);
		jupiter.setState(false);
		saturn.setState(false);
		uranus.setState(false);
		neptune.setState(false);
		pluto.setState(false);
		dummy.setState(true);
	};
	
	// Returns, exiting the function.
	return; 
 };

	/*
	The speedRule() method:
		Description: 
			- Associates string values with numeric speeds.
		Preconditions:
			- String <key> - must be a valid string value. 
		Postconditions:
			- Returns the relevant integer value for any given CheckboxMenuItem relating to speed.
	*/
	private int speedRule(String key)
	{
		// Defines a temporary value (defaults to -1 for easy errorhandling).
		int value = -1;
		
		// Checks to see if the value passed to this method matches a switch case.
		switch(key)
		{
			case("Extra Slow"):
				value = 50;
				break;
			case("Slow"):
				value = 25;
				break;
			case("Medium"):
				value = 15;
				break;
			case("Fast"):
				value = 10;
				break;
			case("Extra Fast"):
				value = 4;
				break;
		}
		// Returns value, regardless of whether or not a match was made.
		return value;
	};


	/*
	The adjustmentValueChanged() method:
		Description: 
			- Handles changes to either ScrollBar() instance within the Frame().
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- Alters relevant values within BulletBounce(), or, Ball() (depending on the bar that was changed).
	*/
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		// Stores the current Scrollbar() being changed via getSource().
		Scrollbar sb = (Scrollbar) e.getSource();
		
		// Checks to see whether or not <sb> is the angleBar.
		if (sb == angleBar) {
			// Tries to change the cannon's current angle, if so.
			ball.setCannonAngle(SB_ANGLE_MAX - sb.getValue() + SB_ANGLE_MIN);
			
		// Otherwise, changes ball()'s current velocity.
		} else if (sb == velocityBar) {
			ball.setProjectileVel(sb.getValue() * .005f);
		}

		// Returns, ending the function.
		return;
	}



	/*
	The actionPerformed() method:
		Description: 
			- Handles interaction with any of the four MenuItem() instances held within <menuBar>.
		Preconditions:
			- the start() method must have had been ran already.
		Postconditions:
			- Enables or Disables iteration of <ball>, resets <ball>, or otherwise terminates the program if quit was selected.
	*/
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Stores the source of the event via getSource().
		Object source = e.getSource();
		
		// Checks to see if run was pressed (only is properly true whenever run is enabled).
		if(source == Run)
		{
			// If so, the main loop is allowed to properly iterate, and run is toggled off (pause is toggled on).
			isPaused = false;
			ball.setPaused(isPaused);
			Run.setEnabled(false);
			Pause.setEnabled(true);
		}
		// Otherwise, checks to see if paused was pressed (only is properly true whenever pause is enabled).
		else if(source == Pause)
		{

			// If so, the main loop is paused, and run is toggled on (pause is toggled off).
			isPaused = true;
			ball.setPaused(isPaused);
			Run.setEnabled(true);
			Pause.setEnabled(false);
		}
		
		// Otherwise, checks to see if reset was pressed (only is properly true whenever reset is enabled).
		else if(source == Reset)
		{
			ball.reset();
			screenReset = true;
		}
		// Otherwise, quit must have had been selected. Stop() is called, which terminates the program.		
		else if(source == Quit)
		{
			stop();
		}
		
		// Returns, ending the function.
		return;
	}
	



	/*
	The componentResized() method:
		Description: 
			- Handles user interaction with the Frame() object's screen borders (and limits the screen's minimum and maximum sizes).
		Preconditions:
			- the start() method must have had been ran already.
		Postconditions:
			- Calls MakeSheet(), which determines most of whether or not the screen can successfully be resized. 
	*/
	@Override
	public void componentResized(ComponentEvent e) {
		
		// Stores the old width and height, in case the current resize is unsuccessful.
		oldWinWidth = winWidth;
		oldWinHeight = winHeight;
		
		// Fetches the potential new width and height, before calling makeSheet().
		winWidth = BulletFrame.getWidth();
		winHeight = BulletFrame.getHeight();
		makeSheet();
		
		// Returns, ending the function.
		return;
	}



	/*
	The windowClosing() method:
		Description: 
			- Handles the first steps of an instance of BulletBounce() being terminated (from the user clicking on the Frame()'s [x]).
		Preconditions:
			- the start() method must have had been ran already.
		Postconditions:
			- Calls stop(), beginning the termination process. 
	*/
	@Override
	public void windowClosing(WindowEvent e) {
		stop();
		
		// Returns, ending the function.
		return;
	}

	// Below are overwritten (but unimplemented) methods of this class's implemented listeners.
	public void componentMoved(ComponentEvent e)  {return;}
	public void componentShown(ComponentEvent e)  {return;}
	public void componentHidden(ComponentEvent e) {return;}
	public void windowOpened(WindowEvent e)       {return;}
	public void windowClosed(WindowEvent e)       {return;}
	public void windowIconified(WindowEvent e)    {return;}
	public void windowDeiconified(WindowEvent e)  {return;}
	public void windowActivated(WindowEvent e)    {return;}
	public void windowDeactivated(WindowEvent e)  {return;}
}





/*
The PhysicsObject() class:
	Description: 
		- Acts as a wrapper for scalar value(s), which construct custom rectangles. Also contains various collision macro functions.
	Preconditions:
		- see constructors.
	Postconditions:
		- Passes string values to various methods (see methods with the suffix 'rule'.
*/
class PhysicsObject
{
	// Defines the default velocity vector components for each PhysicsObject() instance,
	// - used when updating certain PhysicsObject() instances which need to 'move' across a canvas.
	private float velocityX = 0;
	private float velocityY = 0;
	
	// Defines the default X, Y, Width, and Height of each PhysicsObject() instance,
	// - used to construct a crude Rectangle() replica for collision code with PhysicsLayer() instances. 
	private float posX = 0;
	private float posY = 0;
	private float width = 0;
	private float height = 0;
	
	// Defines two object pointers, representing the object's last horizontal and vertical collisions. 
	private PhysicsObject lastHorCollision = null;
	private PhysicsObject lastVerCollision = null;
	
	// Defines isStatic, which is an internal boolean used for determining whether or not an instance moves.
	private boolean isStatic = false;
	
	
	/*
	The PhysicsObject() constructor(S):
		Description: 
			- Creates a new PhysicsObject, taking either a fully created rectangle, or, a single boolean value.
		Preconditions (maximum of 2):
			- (1) Rectangle <bounds>   - the X, Y, Width, and Height values to be copied into this Object, - optional
			- (2) boolean   <isStatic> - determines whether or not this instance is to be kept still.
		Postconditions:
			- Returns a partially (or fully) instantiated instance of PhysicsObject().
	*/
	public PhysicsObject(Rectangle bounds, boolean isStatic)
	{
		// Transposes values from <bounds> as default values.
		posX = (float)bounds.getX();
		posY = (float)bounds.getY();
		width = (float)bounds.getWidth();
		height = (float)bounds.getHeight();
		
		// Transposes isStatic as a default value.
		this.isStatic = isStatic;
		
		// Returns, ending the function.
		return;
	}
	public PhysicsObject(boolean isStatic) {
		// Transposes isStatic as a default value.
		this.isStatic = isStatic;
		
		// Returns, ending the function.
		return;
	}

	
	
	/*
	The contains() method:
		Description: 
			- Determines whether or not an arguement is within the given PhysicsObject() instance.
		Preconditions (maximum of 1):
			- (1) Point              <p> - the point to be compared against,
			- (1) PhysicsObject <object> - the PhysicsObject to be compared against.
		Postconditions:
			- Returns <true> when the arguement given is inside the PhysicsObject(), and <false> otherwise.
	*/
	public boolean contains(Point p)
	{
		boolean value = false;
		if ((posX < p.getX()) 		  &&
			(posX + width > p.getX()) &&
			(posY < p.getY())		  &&
			(posY + height > p.getY()))
			value =  true;
		return value;
	}
	public boolean contains(PhysicsObject object)
	{
		boolean value = false;
		if ((posX <= object.getX()) 							&&
			(posX + width >= object.getX() + object.getWidth()) &&
			(posY <= object.getY())								&&
			(posY + height >= object.getY() + object.getHeight()))
			value = true;
		
		return value;
	}
	
	/*
	The intersects() method:
		Description: 
			- Determines whether or not an arguement is partially within the given PhysicsObject() instance.
		Preconditions (maximum of 1):
			- (1) PhysicsObject <object> - the PhysicsObject to be compared against.
		Postconditions:
			- Returns <true> when the arguement given overlaps the PhysicsObject(), and <false> otherwise.
	*/
	public boolean intersects(PhysicsObject object)
	{
		boolean value = false;
		if ((posX + width >= object.getX())				&&
			(posX <= object.getX() + object.getWidth()) &&
			(posY + height >= object.getY()) 			&&
			(posY <= object.getY() + object.getHeight()))
			value = true;
		
		return value;
	}
	
	
	
	/*
	The METHOD BLOCK: GET:
		Description: 
			- Methods with the 'get' prefix fetch variables described above,
			- due to the repetetive nature of these definitions, they have been clumped together. 
		Preconditions:
			- N/A.
		Postconditions:
			- Return relevant components (see each method's suffix).
	*/
	public float getX() {
		return posX;
	}
	public float getY() {
		return posY;
	}
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public float getVelocityX() {
		return velocityX;
	}
	public float getVelocityY() {
		return velocityY;
	}
	// Honorary 'get' method, with boolean 'is...' notation, instead.
	public boolean isStatic() {
		return isStatic;
	}
	public PhysicsObject getLastHorCollision() {
		return lastHorCollision;
	}
	public PhysicsObject getLastVerCollision() {
		return lastVerCollision;
	}
	
	/*
	The METHOD BLOCK: SET:
		Description: 
			- Methods with the 'set' prefix overwrite variables described above,
			- due to the repetetive nature of these definitions, they have been clumped together. 
		Preconditions (maximum of 1):
			- (1) <...> - the value to overwrite the given component (see method's suffix for typing). 
		Postconditions:
			- Overwrites the relevant component (see each method's suffix).
	*/
	public void setVelocityX(float x) {
		velocityX = x;
	}
	public void setVelocityY(float y) {
		velocityY = y;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public void setLocationX(float horPos) {
		posX = horPos;
	}
	public void setLocationY(float verPos) {
		posY = verPos;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public void setHorCollision(PhysicsObject collision) {
		lastHorCollision = collision;
	}
	public void setVerCollision(PhysicsObject collision) {
		lastVerCollision = collision;
	}
	
	/*
	The METHOD BLOCK: ADD:
		Description: 
			- Methods with the 'add' prefix combine their <value> with their named suffix (via addition),
			- due to the repetetive nature of these definitions, they have been clumped together. 
		Preconditions:
			- N/A.
		Postconditions:
			- Combine <value> with their relevant values via addition (see each method's suffix).
	*/
	public void addLocationX(float value) {
		posX += value;
	}
	public void addLocationY(float value) {
		posY += value;
	}
}




/*
The PhysicsLayer() class:
	Description: 
		- Acts as a wrapper for vector instances, which relate to the physics calculations meant to be done with PhysicsObject() instances,
		- Each instance of PhysicsLayer() can hold another layer (which it can be 'touching'). 
	Preconditions:
		- (N/A) see constructors.
	Postconditions:
		- Returns an EMPTY instance of PhysicsLayer().
*/
class PhysicsLayer
{
	// Defines both of the Vector "Layers" used by PhysicsLayer(),
	// - by default, both of these Vectors are empty.
	private Vector<PhysicsLayer> touchingLayers = new Vector<PhysicsLayer>();	
	private Vector<PhysicsObject> colliders     = new Vector<PhysicsObject>();
	
	// This constructor is left blank, as PhysicsLayer() is used as a container object.
	public PhysicsLayer() {return;}
	
	
	/*
	The METHOD BLOCK: ADD:
		Description: 
			- Methods with the 'add' prefix append instances by reference to their given layer (see method suffix),
			- due to the repetetive nature of these definitions, they have been clumped together. 
		Preconditions (at maximum 1):
			- (1) <...> - the value to be appended to the suffix layer.
		Postconditions:
			- Appends the <...> instance by reference to the given layer (see method suffix).
	*/
	public void addTouchingLayer(PhysicsLayer layer)
	{
		touchingLayers.add(layer);
	}
	public void addPhysicsObject(PhysicsObject object)
	{
		colliders.add(object);
	}

	/*
	The METHOD BLOCK: REMOVE:
		Description: 
			- Methods with the 'remove' prefix remove instances by reference from their given layer (see method suffix),
			- due to the repetetive nature of these definitions, they have been clumped together. 
		Preconditions (at maximum 1):
			- (1) <...> - the value to be removed from the suffix layer.
		Postconditions:
			- Removes the <...> instance by reference from the given layer (see method suffix).
	*/
	public void removeTouchingLayer(PhysicsLayer layer)
	{
		touchingLayers.remove(layer);
	}
	public void removePhysicsObject(PhysicsObject object)
	{
		colliders.remove(object);
	}
	
	
	/*
	The updatePhysics() method:
		Description: 
			- Iterates over every instance inside of <colliders>, comparing their contents against <touchingLayers>,
			- most of the collision math is handled in handleHorizontalCollisons() and handleVerticalCollisions() respectively.
		Preconditions:
			- N/A.
		Postconditions:
			- Informs objects of what they will collide with (if anything), and moves PhysicsObject() instances inside of <colliders> along their velocity vectors. 
	*/
	public void updatePhysics()
	{
		// Iterates over every member of <colliders>
		for (PhysicsObject collider : colliders)
		{
			// Resets the current entry, <collider>'s collision memory.
			collider.setHorCollision(null);
			collider.setVerCollision(null);
			
			// Shifts the object (if it is not static).
			if (!collider.isStatic())
			{
				// Moves in the X and Y directions, if nothing is ran into.
				handleHorizontalCollisions(collider);
				if (collider.getLastHorCollision() == null)
					collider.addLocationX(collider.getVelocityX());
				handleVerticalCollisions(collider);
				if (collider.getLastVerCollision() == null)
					collider.addLocationY(collider.getVelocityY());
			}
		}
	}
	
	/*
	The handleHorizontalCollisions() method:
		Description: 
			- Uses a predetermined logic gate to assess whether or not two objects on differening layers have collided on the Y axis. 
			* this function is synchronized, for use in threads.
		Preconditions:
			<PhysicsObject> : must be a valid Physics Object. (object to be compared against collision layers)
		Postconditions:
			- Updates the collision value(s) of movingObj().
	*/
	private synchronized void handleHorizontalCollisions(PhysicsObject movingObj)
	{
		// Sets a default constant related to this function (see conditionals below). 
		float closestBoundPosition = Float.POSITIVE_INFINITY;
		
		// Repeats this process for as many touching layers as the physics layer has.
		for (int i = 0; i < touchingLayers.size(); i++)
		{
			// Itterates a specific comparison over every PhysicsObject() in <touchingLayers>.
			for (PhysicsObject other : touchingLayers.elementAt(i).colliders)
			{					
				if (movingObj != other)
				{
					// This wad of conditionals determines whether or not a position is currently the closest possible to the given collision.
					// If so, the <other> of this iteration's X value becomes the new <HorCollison>.
					if(
						(other.getX() > movingObj.getX() + movingObj.getWidth() / 2)                                              		 &&
						(other.getX() - (movingObj.getX() + movingObj.getWidth()) < closestBoundPosition)                         		 &&
						(movingObj.getY() < other.getY() + other.getHeight() && movingObj.getY() + movingObj.getHeight() > other.getY()) &&
						(movingObj.getX() + movingObj.getWidth() + movingObj.getVelocityX() >= other.getX())) {
							
						movingObj.setHorCollision(other);
						other.setHorCollision(movingObj);
						movingObj.setLocationX(other.getX() - movingObj.getWidth() - 1);
						
					} else if(
						(other.getX() + other.getWidth() < movingObj.getX() + movingObj.getWidth() / 2)                            		 &&
						(movingObj.getX() - (other.getX() + other.getWidth()) < closestBoundPosition)                         			 &&
						(movingObj.getY() < other.getY() + other.getHeight() && movingObj.getY() + movingObj.getHeight() > other.getY()) &&
						(movingObj.getX() + movingObj.getVelocityX() <= other.getX() + other.getWidth())) {
								
						movingObj.setHorCollision(other);
						other.setHorCollision(other);
						movingObj.setLocationX(other.getX() + other.getWidth() + 1);
					}
				}
			}
		}
		
		// Returns, ending the function.
		return;
	}
	
	/*
	The handleVerticalCollisions() method:
		Description: 
			- Uses a predetermined logic gate to assess whether or not two objects on differening layers have collided on the Y axis. 
			* this function is synchronized, for use in threads.
		Preconditions:
			<PhysicsObject> : must be a valid Physics Object. (object to be compared against collision layers)
		Postconditions:
			- Updates the collision value(s) of movingObj().
	*/
	private synchronized void handleVerticalCollisions(PhysicsObject movingObj)
	{
		// Sets a default constant related to this function (see conditionals below). 
		float closestBoundPosition = Float.POSITIVE_INFINITY;
		
		for (int i = 0; i < touchingLayers.size(); i++)
		{
			// Itterates a specific comparison over every PhysicsObject() in <touchingLayers>.
			for (PhysicsObject other : touchingLayers.elementAt(i).colliders)
			{
				if (movingObj != other)
				{
					// This wad of conditionals determines whether or not a position is currently the closest possible to the given collision.
					// If so, the <other> of this iteration's Y value becomes the new <VerCollison>.
					if(
						(other.getY() > movingObj.getY() + movingObj.getHeight() / 2)                                           	   &&
						(other.getY() - (movingObj.getY() + movingObj.getHeight()) < closestBoundPosition)                      	   &&
						(movingObj.getX() < other.getX() + other.getWidth() && movingObj.getX() + movingObj.getWidth() > other.getX()) &&
						(movingObj.getY() + movingObj.getHeight() + movingObj.getVelocityY() >= other.getY())) {
					
						movingObj.setVerCollision(other);
						other.setVerCollision(movingObj);
						movingObj.setLocationY(other.getY() - movingObj.getHeight() - 1);
						
					} else if (
						(other.getY() + other.getHeight() < movingObj.getY() + movingObj.getHeight() / 2)                        	   &&
						(movingObj.getY() - (other.getY() + other.getHeight()) < closestBoundPosition)                      		   &&
						(movingObj.getX() < other.getX() + other.getWidth() && movingObj.getX() + movingObj.getWidth() > other.getX()) &&
						(movingObj.getY() + movingObj.getVelocityY() <= other.getY() + other.getHeight())) {
						
						movingObj.setVerCollision(other);
						other.setVerCollision(movingObj);
						movingObj.setLocationY(other.getY() + other.getHeight() + 1);
					}
				}
			}
		}
		
		// Returns, ending the function.
		return;
	}
}



/*
The Ball() Class:
	Description:
		- serves as a canvas and logic center to display within BulletBounce(),
	    - by clicking on the canvas panel, the user can:
		- hold mouse button 1 (left click), and drag to create a rectangle,
			+ upon releasing left click, the rectangle is added to the screen,
				* the ball bouncing around within the canvas panel will ricochet off of this rectangle,
				* if the rectangle contained any other, smaller rectangles entirely, they will be deleted,
		- click the mouse once on a rectangle to delete it from the canvas,
		- click mouse button 1 (left click) on the cannon's graphics to fire a physics object.
			
	Extends:
		- Canvas(), from java.awt...
	Implements:*
		- MouseListener()        - for tracking clicking and general mouse movement,
		- MouseMotionListener()  - for tracking movement without clicking, and clicking + dragging.
	Preconditions:
		- N/A.
	Postconditions:
		- Constructor returns an instance of Ball().
*/
class Ball extends Canvas implements MouseListener, MouseMotionListener {
	// Defines <serialVersionUID>, a universal identifier for this canvas class's instances.
	// - this variable is FINAL, and cannot be changed.
	static final long serialVersionUID = 11L;
	
	
	// Defines various constants related to instantiation of the cannon's object(s). 
	private final int CANNON_PIVOT_SIZE = 40; // - radius of the pivot,
	private final int CANNON_WIDTH = 20;      // - width of the barrel,
	private final int CANNON_LENGTH = 80;     // - length of the barrel,
	private final int CANNON_SPACING = 10;    // - distance from the right of the screen.
	
	// Defines the width of each 'bounding box' PhysicsObject() instance.
	private final int BOUND_WIDTH = 10;
	
	// Defines <BOUNCE_SPEED>, which is the internal stepping value for the primary vector object inside of Ball().
	private final int BOUNCE_SPEED = 1;

	// Defines the gravity offset, which modulates the constant values defined below.
	private final float PIXELS_PER_METER = 0.002f;

	
	// Defines the gravity values associated with each environment available within the program,
	// - each value is multiplied by the constant PIXELS_PER_METER.
	private final float MERCURY    = 3.70f 	* PIXELS_PER_METER;
	private final float VENUS      = 8.87f 	* PIXELS_PER_METER;
	private final float EARTH      = 9.81f 	* PIXELS_PER_METER;
	private final float EARTHSMOON = 1.62f 	* PIXELS_PER_METER;
	private final float MARS       = 3.72f 	* PIXELS_PER_METER;
	private final float JUPITER    = 24.79f * PIXELS_PER_METER;
	private final float SATURN     = 10.44f * PIXELS_PER_METER;
	private final float URANUS     = 8.87f 	* PIXELS_PER_METER;
	private final float NEPTUNE    = 11.15f * PIXELS_PER_METER;
	private final float PLUTO      = 0.62f 	* PIXELS_PER_METER;


	// Defines mutable integers that hold the current screen width and screen height.
	private int screenWidth;
	private int screenHeight;
	
	// Defines tow Point() instances, which hold the mouse click and mouse dragged location(s), for use with creating rectangles. 
	private Point m1 = new Point();
	private Point m2 = new Point();
	
	// Defines the bounding box that represents this class's primary vector shape, <ball>.
	private PhysicsObject ball = new PhysicsObject(false);
	private PhysicsObject projectile = null;
	
	
	private PhysicsObject cannonHitBox = new PhysicsObject(new Rectangle(0, 0, CANNON_PIVOT_SIZE / 2 + CANNON_LENGTH, 
																			   CANNON_PIVOT_SIZE / 2 + CANNON_LENGTH), true);
	
	private PhysicsObject southBound = new PhysicsObject(true);
	private PhysicsObject northBound = new PhysicsObject(true);
	private PhysicsObject westBound  = new PhysicsObject(true);
	private PhysicsObject eastBound  = new PhysicsObject(true);
	
	// Defines the rectangle used to represent the area the mouse is currently drug over (values from this rect can also become a solid rectangle).
	private PhysicsObject dragBox = new PhysicsObject(true);
	
	private PhysicsLayer boxLayer     = new PhysicsLayer();
	private PhysicsLayer ballLayer    = new PhysicsLayer();
	private PhysicsLayer screenBounds = new PhysicsLayer();
	private PhysicsLayer cannonLayer  = new PhysicsLayer();
	
	// Defines a Vector which stores all collision rectangles currently instantiated onto Ball().
	private Vector<PhysicsObject> rects = new Vector<PhysicsObject>();
	
	// Defines togglebooleans, which track various binary states.
	private boolean dragBoxActive = false; // whether or not the dragBox is active,
	private boolean mouseActive   = false; // whether or not the canvas has mouse focus,
	private boolean cannonHit     = false; // whether or not the cannon was hit,
	private boolean ballHit       = false; // whether or not the ball was hit,
	private boolean clickedOnce   = false; // whether or not the player has clicked once already (for double-click-removal),
	private boolean comingBack    = false; // whether or not the main physics object will be returning to the screen,
	private boolean messageFlag   = false; // whether or not this instance of Ball() has a pending message,
	private boolean queuedShot    = false; // whether or not a shot from the cannon is queued,
	private boolean notMouseOne   = true;  // if a mouse button other than mouse one has been pressed,
	private boolean programPaused = true;  // if the program currently is paused or not.
	private boolean canHitCannon = false;  // whether or not a newly instanced cannon projectile can damage the cannon yet.
	
	// Defines a temp string, which holds messages to be sent by this instance of Ball().
	private String message = "";

	// Defines components used for doublebuffering.
	private Image buffer; // - actual buffer image, <buffer>,
	private Graphics g;   // - replacement graphical component, <g>.


	// Defines a polygon and rectangle (bounding box for an oval) related to the cannon object.
	private Polygon cannon = new Polygon();
	private Rectangle cannonPivot = new Rectangle(0, 0, CANNON_PIVOT_SIZE, CANNON_PIVOT_SIZE);
	
	// Defines a copy of the cannonAngle that Ball() retains.
	private int cannonAngle = 0;
	
	// Defines the end position for the cannon's projectile, which is used to determine whether or not a cannonball will re-enter the screen.
	private Point cannonEndPos = new Point();
	
	// Defines the current gravity value to be applied to the Y velocity of the cannon's projectile.
	private float gravity = 1;
	
	// Defines the inital projectile velocity meant for the cannon's projectile.
	private float projectileVel = 1;

	/*
	The Ball() method:
		Description: 
			- Initalizes a new instance of Ball(), with various starting values.
		Preconditions:
			- the constructor (Ball()) has already been ran,
			<ballSize>     : must be a valid integer value, (<ball>'s initial size)
			<screenWidth>  : must be a valid integer value, (screen's width)
			<screenHeight> : must be a valid integer value. (screen's height)
		Postconditions:
			- Returns an initalized instance of Ball() (with relevant, appended listeners).
	*/
	Ball(int ballSize, int screenWidth, int screenHeight)
	{
		
		// Sets the current screen width and height.
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		// Instantiates <buffer> so that it can be used later in paint(). 
		buffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		
		// Randomizes the x and y position(s) of <ball>, as per assignment instructions.
		//ball.setLocationX((int) (Math.random() * (screenWidth - ballSize - 4)) + 2);
		//ball.setLocationY((int) (Math.random() * (screenHeight - ballSize - 4)) + 2);
		ball.setLocationX(25);
		ball.setLocationY(25);
		
		// Sets the current ball width and height (simplified to 'size' by BulletBounce()).
		ball.setWidth(ballSize);
		ball.setHeight(ballSize);
		
		ball.setVelocityX(BOUNCE_SPEED);
		ball.setVelocityY(BOUNCE_SPEED);
		
		// Adds relevant listeners to Ball(), before exiting the constructor.
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		// Appends <ball> to the <ballLayer>.
		ballLayer.addPhysicsObject(ball);
		
		// Sets various constant values related to Ball()'s screen boundaries.
		northBound.setLocationX(0);
		northBound.setLocationY(-BOUND_WIDTH);
		southBound.setLocationX(0);
		southBound.setLocationY(screenHeight - 1);
		westBound.setLocationX(-BOUND_WIDTH);
		westBound.setLocationY(0);
		eastBound.setLocationX(screenWidth - 1);
		eastBound.setLocationY(0);
		
		northBound.setWidth(screenWidth);
		northBound.setHeight(BOUND_WIDTH);
		southBound.setWidth(screenWidth);
		southBound.setHeight(BOUND_WIDTH);
		westBound.setWidth(BOUND_WIDTH);
		westBound.setHeight(screenHeight);
		eastBound.setWidth(BOUND_WIDTH);
		eastBound.setHeight(screenHeight);
		
		
		// Appends each finished bound onto the screenBounds layer.
		screenBounds.addPhysicsObject(northBound);
		screenBounds.addPhysicsObject(southBound);
		screenBounds.addPhysicsObject(westBound);
		screenBounds.addPhysicsObject(eastBound);
		
		// Adds the cannon's hitbox to it's own layer.
		cannonLayer.addPhysicsObject(cannonHitBox);
		
		// Links relevant layers together.
		ballLayer.addTouchingLayer(screenBounds);
		ballLayer.addTouchingLayer(boxLayer);
		ballLayer.addTouchingLayer(cannonLayer);
		screenBounds.addTouchingLayer(ballLayer);
		screenBounds.addTouchingLayer(boxLayer);
		
		// Returns, exiting the function.
		return;
	}
	


	/*
	The reset() method:
		Description: 
			- flushes any mutable values that have not been set by the user, reverting the program to (nearly) defaults.
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- Effectively resets the game (does not effect score).
	*/	
	public void reset() {
		// Flushes each entry <rects>
		for (int i = rects.size() - 1; i > -1; i--) {
			boxLayer.removePhysicsObject(rects.elementAt(i));
			rects.remove(i);
		}
		// Resets contextual boolean values.
		ballHit = false;
		cannonHit = false;
		
		// Resets the ball's current position, and randomizes it's velocity. 
		ball.setLocationX(25);
		ball.setLocationY(25);
		if (Math.random() > 0.5f)
			ball.setVelocityX(-1);
		else 
			ball.setVelocityX(1);
		if (Math.random() > 0.5f)
			ball.setVelocityY(-1);
		else
			ball.setVelocityY(1);
		
		// Destroys the current projectile instance.
		destroyProjectile();
		
		// Returns, ending the function.
		return;
	}


	/*
	The removeListeners() method:
		Description: 
			- Removes listeners from Ball(), so that it can be destructed properly when the program terminates.
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- The instance of Ball() is stripped of it's implemented listeners. 
	*/
	public void removeListeners()
	{
		// Removes relevant listeners from Ball(), before returning.
		this.removeMouseListener(this);
		this.removeMouseMotionListener(this);
		
		// Returns, ending the function.
		return;
	}


	/*
	The tryAddRect() method:
		Description: 
			- Attempts to add <dragBox> to <rects>, 
			* this function is synchronized, for use in threads.
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- Appends (if valid) <dragBox> to <rects>,
			  + if <dragBox> is not of a valid size, calls tryRemoveRect() instead.
	*/
	private synchronized void tryAddRect()
	{
		// Defines <valid>, a temporary boolean value used for input validation (see below).
		boolean valid = true;
		
		// Begins iterating through several checks to see if the drag box has valid coordinates to become a rectangle.
		// Checks if the new rect set by <dragBox> will intersect the ball
		// - also checks to see if the <dragBox> will intersect any edges of the screen (including Ball()'s border).
		// - if not, iteration continues.
		if (
			!ball.intersects(dragBox)         					    &&
			!cannonHitBox.intersects(dragBox) 						&&
			(projectile == null || !dragBox.intersects(projectile)) &&
			!(
			dragBox.getX() > screenWidth - 1  ||
			dragBox.getX() < 1                || 
			dragBox.getY() > screenHeight - 1 ||
			dragBox.getY() < 1 ))
		{
			// Iterates through each entry within <rects>, flagging <valid> as false whenever one of them entirely contains <dragBox>.
			for (PhysicsObject rect : rects)
			{
				if (rect.contains(dragBox))
					valid = false;
			}
			
			// If <valid> is true afterwards, a removal function is called. 
			if (valid)
			{
				
				// Removes any rectangles that are contained entirely within <dragBox>.
				for (int i = rects.size() - 1; i > -1; i--)
				{ 
					if (dragBox.contains(rects.elementAt(i))) {
						boxLayer.removePhysicsObject(rects.elementAt(i));
						rects.remove(i);
					}
				}
				
				// Afterward, adds the dragbox to <rects>.
				rects.add(dragBox);
				dragBox = null;
				boxLayer.addPhysicsObject(rects.lastElement());
			}
		}
		
		// Returns, ending the function.
		return;
	}
	
	
	/*
	The updateCannon() method:
		Description: 
			- Updates the position of object(s) related to the cannon object.
		Preconditions:
			- N/A.
		Postconditions:
			- Rebuilds the polygon and oval associated with the cannon object.
	*/
	private void updateCannon() {
		// changes the cannon pivot's X and Y positions.
		cannonPivot.x = screenWidth  - CANNON_SPACING - CANNON_PIVOT_SIZE / 2;
		cannonPivot.y = screenHeight - CANNON_SPACING - CANNON_PIVOT_SIZE / 2;
		
		// Resets the cannon's current positional values (allows for a proper transform).
		cannon.reset();
		
		// Gets the radian that the current cannon angle equals.
		double radAngle = cannonAngle * (float)Math.PI / 180;
		
		// Convers that radian value into horizontal and vertical values, using pythagorean theorem.
		double horValue = Math.cos(radAngle);
		double verValue = Math.sin(radAngle);
		
		// Transposes the cannon's end positions. 
		cannonEndPos.x = (int)(cannonPivot.getX() + horValue * CANNON_LENGTH);
		cannonEndPos.y = (int)(cannonPivot.getY() - verValue * CANNON_LENGTH);
		
		// Adds the lower left point (when angled towards the upper left quadrant)
		cannon.addPoint(
		(int)(cannonPivot.getX() + Math.round(verValue * -CANNON_WIDTH / 2)),
		(int)(cannonPivot.getY() + Math.round(horValue * -CANNON_WIDTH / 2)));
		
		// Adds the upper left point
		cannon.addPoint(
		(int)(cannonEndPos.getX() + Math.round(verValue * -CANNON_WIDTH / 2)),
		(int)(cannonEndPos.getY() + Math.round(horValue * -CANNON_WIDTH / 2)));
		
		// Adds the upper right point
		cannon.addPoint(
		(int)(cannonEndPos.getX() + Math.round(verValue * CANNON_WIDTH / 2)),
		(int)(cannonEndPos.getY() + Math.round(horValue * CANNON_WIDTH / 2)));
		
		//Adds the lower right point
		cannon.addPoint(
		(int)(cannonPivot.getX() + Math.round(verValue * CANNON_WIDTH / 2)),
		(int)(cannonPivot.getY() + Math.round(horValue * CANNON_WIDTH / 2)));
	
		// Alters the cannon's hitbox to match the current iteration's coordinates. 
		cannonHitBox.setLocationX((int)cannonPivot.getX() - CANNON_LENGTH);
		cannonHitBox.setLocationY((int)cannonPivot.getY() - CANNON_LENGTH);
		
		// Adjusts the pivot's position.
		cannonPivot.x -= CANNON_PIVOT_SIZE / 2;
		cannonPivot.y -= CANNON_PIVOT_SIZE / 2;
	}



	/*
	The METHOD BLOCK: SET:
		Description: 
			- Methods with the 'set' prefix overwrite variables described above,
			- due to the repetetive nature of these definitions, they have been clumped together. 
		Preconditions (maximum of 1):
			- (1) <...> - the value to overwrite the given component (see method's suffix for typing). 
		Postconditions:
			- Overwrites the relevant component (see each method's suffix).
	*/	
	public void setCannonAngle(int angle) {
		
		cannonAngle = angle;
		updateCannon();
		repaint();
	}
	public void setPaused(boolean value)
	{
		programPaused = value;
	};
	public void setProjectileVel(float vel) {
		projectileVel = vel;
	}	
	public void setMessage(String message) {
		messageFlag = true;
		this.message = message;
	}
	public void setGrav(float value)
	{
		gravity = value;
		return;
	}




	/*
	The METHOD BLOCK: GET:
		Description: 
			- Methods with the 'get' prefix fetch variables described above,
			- due to the repetetive nature of these definitions, they have been clumped together. 
		Preconditions:
			- N/A.
		Postconditions:
			- Return relevant components (see each method's suffix).
	*/
	public String getMessage() {
		messageFlag = false;
		return message;
	}
	public int getBallSize()
	{
		// Returns the current ball size (casted as an integer), ending the function.
		return (int) ball.getWidth();
	}
	// Honorary 'get' methods, with 'has..., was...' boolean naming syntax.
	public boolean hasMessage() {
		return messageFlag;
	}
	public boolean cannonWasHit() {
		return cannonHit;
	}
	public boolean ballWasHit() {
		return ballHit;
	}



	/*
	The setDragBox() method:
		Description: 
			- Sets the current position of <dragBox>, and whether or not rendering of the drag box is enabled.
		Preconditions:
			- the constructor (Ball()) has already been ran,
			<x>      : must be a valid integer value, (box's x position) 
			<y>      : must be a valid integer value, (box's y position)
			<width>  : must be a valid integer value, (box's width)
			<height> : must be a valid integer value. (box's height)
		Postconditions:
			- Updates the current position (and rendering status) of <dragBox>,
			  + also calls repaint().
	*/
	private void setDragBox(int x, int y, int width, int height)
	{
		// Updates the state of <dragBoxActive>,
		dragBoxActive = true;
		
		if (dragBox == null) {
			dragBox = new PhysicsObject(new Rectangle(x, y, width, height), true);
		} else {
			// Updates the bounds of <dragBox>,
			dragBox.setLocationX(x);
			dragBox.setLocationY(y);
			dragBox.setWidth(width);
			dragBox.setHeight(height);
		}
		
		// Calls repaint() after the box has been updated, before returning.
		repaint();
		
		// Returns, ending the function.
		return;
	}


	/*
	The tryRemoveRect() method:
		Description: 
			- Removes all rectangles that happen to contain <point> within <rects>,
			* this function is synchronized, for use in threads.
		Preconditions:
			- the constructor (Ball()) has already been ran,
			<point> : must be a valid Point() instance. (point of deletion)
		Postconditions:
			- Removes all members of <rects> which contain <point>,
			  + also calls repaint().
	*/
	private synchronized void tryRemoveRect(Point point) {
		// Iterates backwards through <rects>, removing any entries that contain <point> along the way.
		for (int i = rects.size() - 1; i > -1; i--) {
			if (rects.elementAt(i).contains(point)) {
				boxLayer.removePhysicsObject(rects.elementAt(i));
				rects.remove(i);
			}
		}
		// Calls repaint(), before returning.
		repaint();
		
		// Returns, ending the function.
		return;
	}
	
	
	

	
	/*
	The updateProjectile() method:
		Description: 
			- Applies velocity direction(s) onto <projectile>, checking for any potential collisions along the way.
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- Updates <projectile>'s position within Ball(),
			  + also conditionally changes <velocity>, depending on potential collisions,
			  + conditionally resets the game, depending on collisions. 
	*/
	public void updateProjectile() {
		
		// Determines whether or not a new projectile should be spawned before iteration.
		if (queuedShot){
			tryFireCannon();	
			queuedShot = false;
		}
		
		// Executes only if a projectile currently exists.
		if (projectile != null)
		{
			// Set the new velocity of the projectile
			projectile.setVelocityY(projectile.getVelocityY() + gravity);
			
			// The screenBounds layer is reused for the projectile
			screenBounds.updatePhysics();
			
			PhysicsObject horizontalCollision = projectile.getLastHorCollision();
			PhysicsObject verticalCollision   = projectile.getLastVerCollision();
			
			
			
		
			// Checks if the projectile is out of bounds
			if (projectile.getY() > screenHeight 			  		  ||
				projectile.getX() + projectile.getWidth() < 0 		  ||
				projectile.getX() > screenWidth) {
				// Destroys it, if so.
				destroyProjectile();
				setMessage("The cannon ball is not coming back...");
			
			}
			// Otherwise, tells the player that the ball is still within X bounds (just not Y).
			else if (projectile.getY() + projectile.getHeight() < 0) {
				
				if (!comingBack)
				{
					float endXValue = (float)(projectile.getX() + Math.sqrt((projectile.getHeight() -projectile.getY() - projectile.getVelocityY()) / gravity) * projectile.getVelocityX());
					// Check if coming back
					if (endXValue + projectile.getWidth() > 0 && endXValue < screenWidth - 1) {
						comingBack = true;
						setMessage("The cannon ball is in the air!");
					} 
					// Otherwise, destroys the projectile (like above).
					else {
						destroyProjectile();
						setMessage("The cannon ball is not coming back...");
					}
				}
			} else comingBack = false;
			
			
			
			// Resets whether or not the cannonball can hit the cannon once it exits the cannon's inital hitbox.
			if (!(canHitCannon || cannonHitBox.intersects(projectile)))
			{
				canHitCannon = true;
				screenBounds.addTouchingLayer(cannonLayer);
			}
			
			// Checks to see if a horiziontal collision occured. 
			if (horizontalCollision != null)
			{
				// If it was the ball, reacts accordingly.
				if (horizontalCollision == ball)
				{
					ballHit = true;
					destroyProjectile();
				}
				// If it was the cannon, reacts accordingly.
				else if (horizontalCollision == cannonHitBox)
				{
					cannonHit = true;
					destroyProjectile();
				}
				// Otherwise, checks to see what exactly the cannon hit. If it was a rectangle, that rectangle (and the cannon ball) are deleted.
				else
				{
					int i = rects.indexOf(projectile.getLastHorCollision());
					if (i != -1)
					{
						boxLayer.removePhysicsObject(rects.elementAt(i));
						rects.remove(i);
						destroyProjectile();
					}
				}
			} 
			// Repeats the checks made above, but for Y components. 
			else if (verticalCollision != null)
			{
				if (verticalCollision == ball)
				{
					ballHit = true;
					destroyProjectile();
				}
				else if (verticalCollision == cannonHitBox)
				{
					cannonHit = true;
					destroyProjectile();
				}
				else
				{
					int i = rects.indexOf(projectile.getLastVerCollision());
					if (i != -1)
					{
						boxLayer.removePhysicsObject(rects.elementAt(i));
						rects.remove(i);
						destroyProjectile();
					}
				}
			}	
		}
		return;
	}


	/*
	The updateBall() method:
		Description: 
			- Applies velocity direction(s) onto <ball>, checking for any potential collisions along the way.
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- Updates <ball>'s position within Ball(),
			  + also conditionally changes <velocity>, depending on potential collisions.
	*/
	public void updateBall()
	{
		// Updates the ball layer's physics.
		ballLayer.updatePhysics();
		
		// Handles the resulting collisions accordingly. 
		if (ball.getLastHorCollision() != null) {
			ball.setVelocityX(-(int)ball.getVelocityX());
			
			// Checks if the ball hit the cannon
			if (ball.getLastHorCollision() == cannonHitBox) {
				cannonHit = true;
			}
		}
		if (ball.getLastVerCollision() != null) {
			ball.setVelocityY(-(int)ball.getVelocityY());
			
			// Checks if the ball hit the cannon
			if (ball.getLastVerCollision() == cannonHitBox) {
				cannonHit = true;
			}
		}
		
		// Returns, ending the function.
		return;
	}
	
	/*
	The updateBall() method:
		Description: 
			- Degrades the current projectile object into a nullpointer, if able.
		Preconditions:
			- N/A.
		Postconditions:
			- Degrades the current projectile object into a nullpointer, if able.
	*/
	private void destroyProjectile() {
		if (projectile != null) {
			screenBounds.removePhysicsObject(projectile);
			projectile = null;
		}
	}

	/*
	The tryResizeCanvas() method:
		Description: 
			- Attempts to resize Ball() to the alloted screenspace, after validating through hasSizeViolation().
		Preconditions:
			- the constructor (Ball()) has already been ran,
			<screenWidth>  : must be a valid integer value, (new screen's width)
			<screenHeight> : must be a valid integer value. (new screen's height)
		Postconditions:
			- Resizes the screen if no resize violations are found (returns true),
			  + otherwise, does not resize the screen (returns false).
	*/
	public boolean tryResizeCanvas(int screenWidth, int screenHeight)
	{
		
		// Defines <value>, the return value of this function.
		boolean value = true;
		
		// Stores the current screen width and height (old values).
		int oldWidth = this.screenWidth;
		int oldHeight = this.screenHeight;
		
		// Fetches the potential new screen width and heights (new values).
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		updateCannon();

		// Checks to see if the new screen dimensions would cause a size violation.
		if (hasSizeViolation())
		{
			// If so, the screen is not resized.
			this.screenWidth = oldWidth;
			this.screenHeight = oldHeight;
			value = false;
			
			updateCannon();
		}
		
		// Otherwise, the screen is resized, and the graphics buffer is reset.
		if (value)
		{
			buffer.flush();
			buffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
			
			northBound.setWidth(screenWidth);
			southBound.setWidth(screenWidth);
			westBound.setHeight(screenHeight);
			eastBound.setHeight(screenHeight);
			
			southBound.setLocationY(screenHeight - 1);
			eastBound.setLocationX(screenWidth   - 1);
		}
		
		// Returns value, ending the function.
		return value;
	}
	
	
	/*
	The tryFireCannon() method:
		Description: 
			- Attempts to spawn a new instance of the projectile physics object
		Preconditions:
			N/A.
		Postconditions:
			- Spawns a new instance of the projectile physics object (cannon ball), if able.
	*/
	private void tryFireCannon() {
		// Only continues if a projectile doesn't exist.
		if (projectile == null)
		{
			// Creates a new projectile instance, and injects it with relevant default values.
			projectile = new PhysicsObject(false);
			projectile.setLocationX((float)cannonEndPos.getX() - CANNON_WIDTH / 2);
			projectile.setLocationY((float)cannonEndPos.getY() - CANNON_WIDTH / 2);
			projectile.setWidth(CANNON_WIDTH);
			projectile.setHeight(CANNON_WIDTH);
			projectile.setVelocityX(projectileVel * (float)Math.cos(cannonAngle * Math.PI / 180f));
			projectile.setVelocityY(projectileVel * -(float)Math.sin(cannonAngle * Math.PI / 180f));
			
			// Appends the finished projectile to a physics layer.
			screenBounds.addPhysicsObject(projectile);
			
			// Resets whether or not the cannonball can hit the cannon, if it can. 
			if (canHitCannon) {
				canHitCannon = false;
				screenBounds.removeTouchingLayer(cannonLayer);
			}
			// Clears the message buffer with a blank string.
			setMessage("");
		}
	}
	

	/*
	The tryUpdateSize() method:
		Description: 
			- Attempts to update the size of <ball> on the screen, after validating through hasSizeViolation().
		Preconditions:
			- the constructor (Ball()) has already been ran,
			<size> : must be a valid integer value. (<ball>'s new size)
		Postconditions:
			- Resizes <ball> if no resize violations are found (returns true),
			  + otherwise, does not resize <ball> (returns false).
	*/
	public boolean tryUpdateSize(int size)
	{
		
		// Defines <value>, the return value of this function.
		boolean value = true;
		
		// Stores the old X and Y values, for use during the resizing process.
		float oldX = ball.getX();
		float oldY = ball.getY();
		
		// Stores the old size of the ball.
		float oldSize = ball.getWidth();

		// Forces the potential new value(s) to be even.
		ball.setHeight((size / 2) * 2);
		ball.setWidth(ball.getHeight());
		
		// Repositions the ball using the old values defined above,
		// - this is done to make it seem as though the ball is growing from the center.
		ball.setLocationX(ball.getX() + (oldSize - ball.getWidth()) / 2);
		ball.setLocationY(ball.getY() + (oldSize - ball.getHeight()) / 2);

		// Checks to see if the new ball size would cause a size violation.
		if (hasSizeViolation())
		{
			// if so, all of the old value(s) replace what would have been the current ones.
			ball.setLocationX(oldX);
			ball.setLocationY(oldY);
			ball.setWidth(oldSize);
			ball.setHeight(oldSize);
			value = false;
		}
		
		// Otherwise, repaint() is called, before the function returns.
		if(value)
			repaint();

		// Returns <value>, ending the function.
		return value;
	}

	/*
	The sizeRule() method:
		Description: 
			- Associates string values with numeric sizes.
		Preconditions:
			- String <key> - must be a valid string value. 
		Postconditions:
			- Returns the relevant integer value for any given CheckboxMenuItem relating to size.
	*/
	public int sizeRule(String key)
	{
		int value = -1;
		switch(key)
		{
			case("Extra Small"):
				value = 5;
				break;
			case("Small"):
				value = 20;
				break;
			case("Medium"):
				value = 40;
				break;
			case("Large"):
				value = 60;
				break;
			case("Extra Large"):
				value = 85;
				break;
		}
		return value;
	};



	/*
	The gravRule() method:
		Description: 
			- Associates string values with numeric gravities.
		Preconditions:
			- String <key> - must be a valid string value. 
		Postconditions:
			- Returns the relevant integer value for any given CheckboxMenuItem relating to environment.
	*/
	public float gravRule(String key)
	{
		float value = -1;
		switch(key)
		{
			case("Mercury"):
				value = MERCURY;
				break;
			case("Venus"):
				value = VENUS;
				break;
			case("Earth"):
				value = EARTH;
				break;
			case("Earth's Moon"):
				value = EARTHSMOON;
				break;
			case("Mars"):
				value = MARS;
				break;
			case("Jupiter"):
				value = JUPITER;
				break;
			case("Saturn"):
				value = SATURN;
				break;
			case("Uranus"):
				value = URANUS;
				break;
			case("Neptune"):
				value = NEPTUNE;
				break;
			case("Pluto"):
				value = PLUTO;
				break;
		}
		return value;
	};


	/*
	The hasSizeViolation() method:
		Description: 
			- Determines whether or not elements within Ball() can be resized without errors.
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- Returns true whenever all components occupy acceptable space,
			  + otherwise returns false.
	*/
	private boolean hasSizeViolation()
	{
		// Defines <value>, the return value of this function.
		boolean value = false;

		// Checks to see whether or not the ball is in an invalid location,
		// If so, <value> == true.
		if(
		(ball.getX() + ball.getWidth() > screenWidth - 2)   ||
		(ball.getY() < 1)                              ||
		(ball.getY() + ball.getHeight() > screenHeight - 2) ||
		(ball.getY() < 1))
			value = true;
		
		// Otherwise, checks to see if any rectangle(s) would collide with the ball, or the screen width/height,
		// If so, <value> == true.
		else
		{
			for (PhysicsObject rect : rects)
			{
				if(
				(rect.intersects(ball))     						||
				(rect.getX() + rect.getWidth() > screenWidth - 2)   ||
				(rect.getX() < 1)                              		||
				(rect.getY() + rect.getHeight() > screenHeight - 2) ||
				(rect.getY() < 1) 									||
				(cannonHitBox.intersects(rect)))
					value = true;
			}
		}

		// Returns <value>, ending the function.
		return value;
	}


	/*
	The update() method:
		Description: 
			- Overwritten version of update(), which passes a named graphics instance to paint().
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- Calls paint(), before returning.
	*/
	@Override
	public void update(Graphics cg) {
		paint(cg);
		return;
	}


	/*
	The paint() method:
		Description: 
			- Draws all objects currently within Ball() onto Ball()'s canvas buffer,
			  + displays the buffer from the previous frame on the current, overwritting that buffer for the next paint() call.
		Preconditions:
			- the constructor (Ball()) has already been ran,
			<cg> : must be a valid Graphics() instance. (value from update())
		Postconditions:
			- Draws all objects within Ball() to the screen.
	*/
	@Override
	public synchronized void paint(Graphics cg) {
		// Removes the default graphics component, if it happens to exist.
		if (g != null)
			g.dispose();
		
		// Replaces the default graphics component with a typecast of <buffer>.
		g = buffer.getGraphics();

		// Draws the screen's background.
		g.setColor(Color.white);
		g.fillRect(0, 0, screenWidth, screenHeight);

		// Draws the screen's red border.
		g.setColor(Color.red);
		g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);

		// Draws each rectangle within <rects> onto the screen.
		for (PhysicsObject rect : rects)
		{
			g.setColor(Color.orange);
			g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
			g.setColor(Color.black);
			g.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
		}

		// Draws <ball> onto the screen.
		g.setColor(Color.lightGray);
		g.fillOval((int) ball.getX(), (int) ball.getY(), (int) ball.getWidth(), (int) ball.getHeight());
		g.setColor(Color.black);
		g.drawOval((int) ball.getX(), (int) ball.getY(), (int) ball.getWidth(), (int) ball.getHeight());
		
		// Draws the projectile onto the screen.
		if (projectile != null) {
			g.setColor(Color.black);
			g.fillOval((int) projectile.getX(), (int) projectile.getY(), (int) projectile.getWidth(), (int) projectile.getHeight());
		}
		
		// Draws the cannon (and it's pivot) onto the screen.
		g.setColor(Color.blue);
		g.fillPolygon(cannon);
		
		g.setColor(Color.black);
		g.fillOval((int) cannonPivot.getX(), (int) cannonPivot.getY(), (int) cannonPivot.getWidth(), (int) cannonPivot.getHeight());

		// Draws the dragbox onto the screen (if <dragBoxActive>).
		if (dragBoxActive)
		{
			g.setColor(Color.black);
			g.drawRect((int) dragBox.getX(), (int) dragBox.getY(), (int) dragBox.getWidth(), (int) dragBox.getHeight());
		}

		// Draws <buffer> to the screen (which holds the events from the previous frame of iteration).
		cg.drawImage(buffer, 0, 0, null);
		
		// Returns, ending the function.
		return;
		
	}
	

	/*
	The mouseClicked() method:
		Description: 
			- Calls tryRemoveRect() whenever the mouse is clicked.
		Preconditions:
			<e> : must be a valid MouseEvent() instance. (value from MouseListener())
		Postconditions:
			- Calls tryRemoveRect() whenever the mouse is clicked.
	*/
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// Checks to see if the left mouse button has been pressed,
		// - the function returns with no changes if this check comes up <false>.
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			// Checks to see if the mouse is active, next.
			if (mouseActive)
			{
				// If so, checks to see if the cannon was clicked,
				if (cannon.contains(m1))
				{
					// If so, a shot is queued for validation.
					if(!programPaused)
						queuedShot = true;
					clickedOnce = false;
				}
				else
				{
					// Otherwise, the <clickedOnce> boolean is set or reset,
					// - if reset, tryRemoveRect() is called.
					if (clickedOnce)
					{
						clickedOnce = false;
						tryRemoveRect(m1);
					} else
						clickedOnce = true;
				}
			}
		}
		// Resets the x position of the current mouse click.
		m1.x = -1;
		
		// Returns, ending the function.
		return;
	}


	/*
	The mousePressed() method:
		Description: 
			- Updates the current <m1> position.
		Preconditions:
			<e> : must be a valid MouseEvent() instance. (value from MouseListener())
		Postconditions:
			- Updates the current <m1> position.
	*/
	@Override
	public void mousePressed(MouseEvent e)
	{
		// Checks to see if the left mouse button has been pressed,
		// - the function returns with no changes if this check comes up <false>.
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			if (mouseActive)
			{
				m1 = e.getPoint();
				notMouseOne = false;
			}
		} else notMouseOne = true;
			
		
		// Returns, ending the function.
		return;
	}


	/*
	The mouseReleased() method:
		Description: 
			- Calls tryAddRect() whenever <mouseActive> is true, alongside associated variable cleanup. 
		Preconditions:
			<e> : must be a valid MouseEvent() instance. (value from MouseListener())
		Postconditions:
			- Calls tryAddRect() whenever <mouseActive> is true, alongside associated variable cleanup. 
	*/
	@Override
	public void mouseReleased(MouseEvent e)
	{

		// Checks to see if the left mouse button has been pressed,
		// - the function returns with no changes if this check comes up <false>.
		if (e.getButton() == MouseEvent.BUTTON1)
			// Tries to add a rectangle, but only if the 'box' created by m1 and m2 meets basic rectangle criteria. 
			if (mouseActive && m2.x > -1 && (m1.x - m2.x > 1 || m2.x - m1.x > 1) && ( m1.y - m2.y > 1 || m2.y - m1.y > 1))
				tryAddRect();
		
		dragBoxActive = false;
		m2.x = -1;
		repaint();
		
		// Returns, ending the function.
		return;
	}


	/*
	The mouseEntered() method:
		Description: 
			- Calls toggles <mouseActive> when this method is called. 
		Preconditions:
			<e> : must be a valid MouseEvent() instance. (value from MouseListener())
		Postconditions:
			- Calls toggles <mouseActive> when this method is called. 
	*/
	@Override
	public void mouseEntered(MouseEvent e)
	{
		mouseActive = true;
		
		// Returns, ending the function.
		return;
	}


	/*
	The mouseExited() method:
		Description: 
			- Calls toggles <mouseActive> when this method is called. 
		Preconditions:
			<e> : must be a valid MouseEvent() instance. (value from MouseListener())
		Postconditions:
			- Calls toggles <mouseActive> when this method is called. 
	*/
	@Override
	public void mouseExited(MouseEvent e)
	{
		mouseActive = false;
		
		// Returns, ending the function.
		return;
	}


	/*
	The mouseDragged() method:
		Description: 
			- Updates the current position of <dragBox> so long as a mouse button is pressed. 
		Preconditions:
			<e> : must be a valid MouseEvent() instance. (value from MouseMotionListener())
		Postconditions:
			- Calls toggles <mouseActive> when this method is called. 
	*/
	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(!notMouseOne)
		{
			m2 = e.getPoint();
			setDragBox(Math.min(m1.x, m2.x), Math.min(m1.y, m2.y), Math.abs(m1.x - m2.x), Math.abs(m1.y - m2.y));
		}
		// Returns, ending the function.
		return;
	}

	// Overwrites, but does not (meaningfully) define mouseMoved(). Resets <mouseMoved> when called.
	@Override
	public void mouseMoved(MouseEvent e) {
		clickedOnce = false;
		return;
	}
}