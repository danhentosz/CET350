/*
.Program6, Bounce: Newton's Third     (CannonVSBall.java).
.Created By:                                             .
- Daniel Hentosz,    (HEN3883@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu).                 .
.Last Revised: April 13th, 2021.              (4/13/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:

Itemized project to do list:
- Menu Objects:
A standard .awt Menu system which contains values for pausing, unpausing, changing the gravity, resetting and quitting the program.

Structure:
	[Control] < - holds checkboxes
		[run     'ctrl + R']  < - grey out when running
		[pause   'ctrl + P']  < - gray out when paused
		[restart]
		[quit]
		
	[Size]
		[size] < - holds checkboxes
			[#1] < - smallest
			[#2]
			[#3]
			[#4]
			[#5] < - largest
		[speed] < - holds checkboxes
			[#1] < - slowest
			[#2]
			[#3]
			[#4]
			[#5] < - fastest
		
	[Environment] < - holds checkboxes (based on the solar system)
			[Mercury]
			[Venus]
			[Earth]
			[Earth's Moon]
			[Mars]
			[Jupiter]
			[Saturn]
			[Uranus]
			[Neptune]
			[Pluto]

^ Menu also should include seperator bars.

	+ Macros:
	A set of keyboard shortcuts which can be entered by the user to accomplish various tasks otherwise possible via the dropdown menus.

	+ Simplifications:
	Some features from the previous two programs will be reset here.
	* Size will become a radio selection with five values,
	* Speed will become a radio selection with five values,
	* Pause will become a togglable radio button,
	* quit will become a selectable radio button.

- Cannon Object:
A container which holds display information for a cannony polygon, and creates instances of a "Cannon Ball".

	+ Cannon Angle:
	Add a scrollBar() that controls the angle of the Cannon() polygon.
	* default values are 0 to 90 degrees (converted from radians).

	+ Cannon Velocity:
	Add a scrollBar() that controls the velocity (also called 'strength' in some programs) of the cannon's projectile.
	* default values are 100 to 1200 ft/sec.

	+ Cannon Activation:
	Add a feature within the canvas() that cancels rectangle drawing, and fires the cannon when the mouse is clicked inside of it's polygon.

	+ Cannon Projectile Object:
	Add a clone of 'ball' that is drawn to the screen using methods similar to Ball().
	
	+ Cannon Projectile Motion:
	Add code to handle the cannon object's motion in a real-life approximating manner.

	+ Cannon Projectile Collisions:
	Add code to handle the cannon object colliding with what can be defined as the 'ground', (y >= screensize).

- New Collisions:
Add handlers for whenever the ball collides with:
* a cannon projectile, - (both are destroyed, ball bounces)
* the cannon.          - (both are destroyed)

Add handlers for whenever a rectangle collides with:
* the cannon ball.     - (rectangle is destroyed)

Add handlers for whenever a the cannon ball collides with:
* the cannon.          - (both are destroyed)



Since this code must reset the game in either instance,
* collisions should either be calculated seperately from the ball object in some instances, and:
* collision code should return a value to the Frame() containing everything, to reset accordingly.

- Reset Function:
The game should have a soft destructor that resets the current state of the application, preserving only:
* The player's score,
* The ball's score.
If a hard reset is requested by the professor, this function should take a parameter to preserve scores or not (defaults to false).

- Score System:
Two text counters which list the player, and ball's scores. Increment only by one, but could be stored as longs to ensure there is not a (reasonable) score overflow.

- Status Indicator:
Add a small text field which tells the user what event(s) have occured within the game. Events listed include:
* Ball Scores a Point,
* Player Scores a Point,
* Ball Leaves and does not come back.
A secondary indicator will also display the current program runtime (simulated).

- Changes to the Click Handler:
Add a check that ensures only left clicks are able to insert/delete rectangles, and fire the cannon (MOUSE-1 is the ID).

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
import java.util.Queue;

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
			- Opens a new instance of GUIBounceII(). 
		Preconditions:
			N/A: method ignores values in <args>[].
		Postconditions:
			- Creates a new instance of GUIBounceII, <gui>,
				+ from here, implemented methods loop until the program is exited.
	*/
	public static void main(String[] args) {
		BulletBounce gui = new BulletBounce();
		return;
	}
}



/*
The BulletBounce() Class:
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

	// Defines the MAX and MIN size for Ball's shape, <MAX_SIZE> and <MIN_SIZE>.
	private final int MAX_SIZE = 100;
	private final int MIN_SIZE = 10;

	private int size = INIT_SIZE;

	// Defines <SB_ANGLE>, the starting value of the ScrollBar("Angle").
	private final int SB_ANGLE = 50;

	// Defines <SB_VELOCITY>, the starting value of the ScrollBar("Velocity").
	private final int SB_VELOCITY = 50;

	// Defines <SB_VIS>, a constant added to the width of ScrollBar() instances.
	private final int SB_VIS = 10;



	private final int MERCURY    = 1;
	private final int VENUS      = 1;
	private final int EARTH      = 1;
	private final int EARTHSMOON = 1;
	private final int MARS       = 1;
	private final int JUPITER    = 1;
	private final int SATURN     = 1;
	private final int URANUS     = 1;
	private final int NEPTUNE    = 1;
	private final int PLUTO      = 1;




	// Defines delay, a default value transformed by the current value of <sbSpeed>.
	private int delay = 16;

	// Defines the minimum, maximum, and current speed values which can be held by a
	// ScrollBar() instance (defined in initComponents()).
	private int sbMinAngle = 0;
	private int sbMaxAngle = 90 + SB_VIS;
	private int sbAngle    = SB_ANGLE;


	// Defines the minimum, maximum, and current speed values which can be held by a
	// ScrollBar() instance (defined in initComponents()).
	private int sbMinVelocity = 100;
	private int sbMaxVelocity = 1600 + SB_VIS;
	private int sbVelocity    = SB_VELOCITY;

	private int speed = 20;

	private Frame BulletFrame;
	private MenuBar menuBar;

	private Menu menuControl;
	private MenuItem Run;
	private MenuItem Pause;
	private MenuItem Reset;
	private MenuItem Quit;

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
	private boolean isRunning = true; // controls whether or not GUIBounceII()'s thread iterates,
	private boolean isPaused = true;  // controls whether or not GUIBounceII()'s Ball() updates.

	// Defines <thread>, which is used to run continuous code after this class is instantiated. 
	private Thread thread;

	// Defines an instance of GridBagConstraints(), which is used in tandem with <control_panel>.
	GridBagConstraints gbc;


	// Defines two instances of Label(), which indicate which scrollbar is for Ball Speed, and which is for Ball Size.
	private Label velocityLabel = new Label("Velocity", Label.CENTER);
	private Label angleLabel  = new Label("Angle", Label.CENTER);

	// Defines two scrollbars (which allow the user to change the size and speed of the Ball within Ball()).
	private Scrollbar velocityBar;
	private Scrollbar angleBar;



	public BulletBounce()
	{
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




	private void start() {
		delay = speed + 2;
		
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
			- Mutates some mutable values inside of GUIBounceII(), mostly through the use of Ball()*
			* other components can be dynamically resized on their own, due to utilizing layout managers.
	*/
	private void makeSheet()
	{
		// Makes sure that <ball> is instantiated before continuing.
		if (ball != null)
		{
			// Attempts to resize the canvas using <ball_panel>'s size,
			// 
			if (!ball.tryResizeCanvas(ball_panel.getWidth(), ball_panel.getHeight())) {
				BulletFrame.setSize(oldWinWidth, oldWinHeight);
				BulletFrame.validate();
				winWidth = oldWinWidth;
				winHeight = oldWinHeight;
			}
		}
		
		return;
	}



	/*
	The initComponents() method:
		Description: 
			- Instantiates all component objects used by GUIBounceII(),
				+ also adds all components to their relevant listeners. 
		Preconditions:
			- Must be called after makeSheet().
		Postconditions:
			- Populates all object-associated variables with their proper values, and adds them to implemented listeners of GUIBounceII().
	*/
	private void initComponents() throws Exception, IOException {
		
		menuBar = new MenuBar();
		
		// Initalizes each control button (with proper labels)
		menuControl = new Menu("Controls");
		Run         = menuControl.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
		Pause       = menuControl.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
		Reset       = menuControl.add(new MenuItem("Reset"));
		Quit        = menuControl.add(new MenuItem("Quit"));
		menuBar.add(menuControl);
		
		menuSize = new Menu("Size");
		
		menuSizeSelector = new Menu("Sizes");
		sizeXSmall       = new CheckboxMenuItem("Extra Small");
		menuSizeSelector.add(sizeXSmall);
		sizeSmall        = new CheckboxMenuItem("Small");
		menuSizeSelector.add(sizeSmall);
		sizeMedium       = new CheckboxMenuItem("Medium");
		menuSizeSelector.add(sizeMedium);
		sizeLarge        = new CheckboxMenuItem("Large");
		menuSizeSelector.add(sizeLarge);
		sizeXLarge       = new CheckboxMenuItem("Extra Large");
		menuSizeSelector.add(sizeXLarge);
		menuSize.add(menuSizeSelector);

		menuSpeed  = new Menu("Speeds");
		speedXSlow       = new CheckboxMenuItem("Extra Slow");
		menuSpeed.add(speedXSlow);
		speedSlow        = new CheckboxMenuItem("Slow");
		menuSpeed.add(speedSlow);
		speedMedium       = new CheckboxMenuItem("Medium");
		menuSpeed.add(speedMedium);
		speedFast        = new CheckboxMenuItem("Fast");
		menuSpeed.add(speedFast);
		speedXFast       = new CheckboxMenuItem("Extra Fast");
		menuSpeed.add(speedXFast);		
		menuSize.add(menuSpeed);
		menuBar.add(menuSize);
		
		
		menuEnvironment = new Menu("Environments");
		mercury         = new CheckboxMenuItem("Mercury");
		menuEnvironment.add(mercury);
		venus           = new CheckboxMenuItem("Venus");
		menuEnvironment.add(venus);
		earth           = new CheckboxMenuItem("Earth");
		menuEnvironment.add(earth);
		earthsMoon      = new CheckboxMenuItem("Earth's Moon");
		menuEnvironment.add(earthsMoon);
		mars            = new CheckboxMenuItem("Mars");
		 menuEnvironment.add(mars);
		jupiter         = new CheckboxMenuItem("Jupiter");
		menuEnvironment.add(jupiter);
		saturn          = new CheckboxMenuItem("Saturn");
		menuEnvironment.add(saturn);
		uranus          = new CheckboxMenuItem("Uranus");
		menuEnvironment.add(uranus);
		neptune         = new CheckboxMenuItem("Neptune");
		menuEnvironment.add(neptune);
		pluto           = new CheckboxMenuItem("Pluto");
		menuEnvironment.add(pluto);
		menuBar.add(menuEnvironment);
		
		
		// Sets the constant value(s) associated with the speed scrollBar(),
		// - also changes the background color of this component to gray.
		velocityBar = new Scrollbar(Scrollbar.HORIZONTAL);
		velocityBar.setMaximum(sbMaxVelocity + SB_VIS);
		velocityBar.setMinimum(sbMinVelocity);
		velocityBar.setValue(sbVelocity);
		velocityBar.setBackground(Color.gray);


		// Sets the constant value(s) associated with the size scrollBar(),
		// - also changes the background color of this component to gray.
		angleBar = new Scrollbar(Scrollbar.HORIZONTAL);
		angleBar.setMaximum(sbMaxAngle + SB_VIS);
		angleBar.setMinimum(sbMinAngle);
		angleBar.setValue(sbAngle);
		angleBar.setBackground(Color.gray);


		// Initalizes <gbl> and <gbc>, which are used together to constrain components within GUIBounceII().
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
		
		// Adds components to their relevant listeners.
		velocityBar.addAdjustmentListener(this);
		angleBar.addAdjustmentListener(this);
		
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
			- Serves as a destructor for GUIBounceII(), and a terminator for it's internal loop (see run()).
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- Frees memory taken up by GUIBounceII()'s member variables and listeners, before returning.
	*/
	public void stop() {
		
		// Terminates the active <thread>'s loop (see run() for details).
		isRunning = false;

		// calls a seperate method for stipping <ball> of it's listeners (see removeListeners() below).
		ball.removeListeners();

		// Finally, removes any listeners still attached to the Frame().
		BulletFrame.removeComponentListener(this);
		BulletFrame.removeWindowListener(this);

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
			
			/* Sleeps for <delay>, which serves as an interpretation of the current <ball> speed.
			if (!isPaused) {
				ball.updatePhysics();
				ball.repaint();
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
			
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
	
	
 @Override
 public void itemStateChanged(ItemEvent e)
 {
	return; 
 };



	/*
	The adjustmentValueChanged() method:
		Description: 
			- Handles changes to either ScrollBar() instance within the Frame().
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- Alters relevant values within GUIBounceII(), or, Ball() (depending on the bar that was changed).
	*/
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		// Stores the current Scrollbar() being changed via getSource().
		Scrollbar sb = (Scrollbar) e.getSource();


		// Returns, ending the function.
		return;
	}



	/*
	The actionPerformed() method:
		Description: 
			- Handles interaction with any of the three Button() instances held within this extension of Frame().
		Preconditions:
			- the start() method must have had been ran already.
		Postconditions:
			- Enables or Disables iteration of <ball>, or otherwise terminates the program if the quit Button() was selected.
	*/
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Stores the source of the event via getSource().
		Object source = e.getSource();
		
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
			- Handles the first steps of an instance of GUIBounceII() being terminated (from the user clicking on the Frame()'s [x]).
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

	// Below are overwritten (but unimplemented) methods of the Frame() superclass
	// and it's listeners.
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
The Ball() Class:
	Description:
		- serves as a canvas and logic center to display within GUIBounceII(),
	    - by clicking on the canvas panel, the user can:
		- hold mouse button 1 (left click), and drag to create a rectangle,
			+ upon releasing left click, the rectangle is added to the screen,
				* the ball bouncing around within the canvas panel will ricochet off of this rectangle,
				* if the rectangle contained any other, smaller rectangles entirely, they will be deleted,
		- click the mouse once on a rectangle to delete it from the canvas.
			
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
	
	// Defines <BOUNCE_SPEED>, which is the internal stepping value for the primary vector object inside of Ball().
	private final int BOUNCE_SPEED = 1;

	// Defines mutable integers that hold the current screen width and screen height.
	private int screenWidth;
	private int screenHeight;
	
	// Creates two Point() instances, which handle the velocity and leftover velocity of Ball()'s primary vector shape.
	private Point velocity = new Point(BOUNCE_SPEED, BOUNCE_SPEED);
	private Point leftoverVel = new Point();
	private Point oldVel = new Point();
	
	// Defines tow Point() instances, which hold the mouse click and mouse dragged location(s), for use with creating rectangles. 
	private Point m1 = new Point();
	private Point m2 = new Point();
	
	// Defines the bounding box that represents this class's primary vector shape, <ball>.
	private Rectangle ball = new Rectangle();
	
	// Defines the rectangle used to represent the area the mouse is currently grug over (values from this rect can also become a solid rectangle).
	private Rectangle dragBox = new Rectangle();
	
	
	private float collisionPos;
	
	// Defines a Vector which stores all collision rectangles currently instantiated onto Ball().
	private Vector<Rectangle> rects = new Vector<Rectangle>();
	
	
	// Defines two toggle-booleans, which control whether or not the mouse is currently active, and whether or not it is affecting <dragBox>.
	private boolean dragBoxActive = false;
	private boolean mouseActive = false;

	// Defines components used for doublebuffering.
	private Image buffer; // - actual buffer image, <buffer>,
	private Graphics g;   // - replacement graphical component, <g>.



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
		ball.x = (int) (Math.random() * (screenWidth - ballSize - 4)) + 2;
		ball.y = (int) (Math.random() * (screenHeight - ballSize - 4)) + 2;
		
		// Sets the current ball width and height (simplified to 'size' by GUIBounceII()).
		ball.width = ballSize;
		ball.height = ballSize;
		
		// Adds relevant listeners to Ball(), before exiting the constructor.
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		// Returns, exiting the function.
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
		
		// Calls tryRemoveRect(), if the dragbox selection is too small to become a rectangle (failsafe).
		if (dragBox.getWidth() * dragBox.getHeight() <= 2)
		{
			tryRemoveRect(dragBox.getLocation());
		}
		// Otherwise, begins iterating through several checks to see if the drag box has valid coordinates to become a rectangle.
		else
		{
			
			// Checks if the new rect set by <dragBox> will intersect the ball
			// - also checks to see if the <dragBox> will intersect any edges of the screen (including Ball()'s border).
			// - if not, iteration continues.
			if (
			!dragBox.intersects(ball)         &&
			!(
			dragBox.getX() > screenWidth - 1  ||
			dragBox.getX() < 1                || 
			dragBox.getY() > screenHeight - 1 ||
			dragBox.getY() < 1 ))
			{
				// Iterates through each entry within <rects>, flagging <valid> as false whenever one of them entirely contains <dragBox>.
				for (Rectangle rect : rects)
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
						if (dragBox.contains(rects.elementAt(i)))
							rects.remove(i);
					}
					
					// Afterward, adds the dragbox to <rects>.
					rects.add(new Rectangle(dragBox));
				}
			}
		}
		
		// Returns, ending the function.
		return;
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
		
		// Updates the bounds of <dragBox>,
		dragBox.setBounds(x, y, width, height);
		
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
				rects.remove(i);
			}
		}
		// Calls repaint(), before returning.
		repaint();
		
		// Returns, ending the function.
		return;
	}



	/*
	The updatePhysics() method:
		Description: 
			- Applies velocity direction(s) onto <ball>, checking for any potential collisions along the way.
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- Updates <ball>'s position within Ball(),
			  + also conditionally changes <velocity>, depending on potential collisions.
	*/
	public void updatePhysics()
	{
		
		// Updates the leftover velocity point (used for <ball> speeds exceeding 1).
		leftoverVel.x = velocity.x;
		leftoverVel.y = velocity.y;
		
		// Loops until all values of the leftover movement point have been processed.
		while (leftoverVel.x != 0 || leftoverVel.y != 0) {
			
			// Handles decrementation of X (horizontal collisions).
			if (leftoverVel.x != 0)
			{
				// Updates another temporary value, the old velocity.
				oldVel.x = (int)leftoverVel.getX();
				
				// Checks to see if the returned float from getFirst...Collision() is finite,
				// - if not, the function failed to find a collision.
				if (Float.isFinite(collisionPos = getFirstHorizontalCollision(leftoverVel.x)))
				{
					// Checks to see if <ball> is currently moving to the right.
					if (leftoverVel.x > 0)
					{
						// If so, the leftover velocity point is decremented,
						// - afterward, <ball>'s position and <velocity> are updated to reflect momentum.
						leftoverVel.x -= (int)(collisionPos - (ball.getX() + ball.getWidth()));
						ball.x = (int)(collisionPos - ball.getWidth()) - 1;
						velocity.x = -BOUNCE_SPEED;
					} 
					
					// Otherwise, the ball must be moving to the left.
					else {
						// The leftover velocity point is decremented,
						// - afterward, <ball>'s position and <velocity> are updated to reflect momentum.
						leftoverVel.x -= (int)(collisionPos - ball.getX());
						ball.x = (int)collisionPos + 1;
						velocity.x = BOUNCE_SPEED;
					}
				}
				
				// Checks for parimeter collisions, in the event that <ball> collided with no rectangles.
				else
				{
					// Checks the right boundary of the screen first.
					if (ball.getX() + ball.getWidth() + leftoverVel.x > screenWidth - 2)
					{
						// If so, subtracts the overlap amount from the leftover velocity.
						leftoverVel.x = -(int)(leftoverVel.x - (ball.getX() + ball.getWidth() + leftoverVel.x - (screenWidth - 2)));
						
						// - afterward, places the ball's position at the edge of the wall that it collided with.
						ball.x = screenWidth - 2 - (int)ball.getWidth();
						velocity.x = -BOUNCE_SPEED;
					}
					// Checks the left boundary of the screen next.
					else if (ball.getX() + leftoverVel.x < 1)
					{
						// If so, subtracts the overlap amount from the leftover velocity.
						leftoverVel.x = -(int)(leftoverVel.x + (1 - (ball.getX() + leftoverVel.x)));
						
						// - afterward, places the ball's position at the edge of the wall that it collided with.
						ball.x = 1;
						velocity.x = BOUNCE_SPEED;
					}
					
					// Otherwise, the ball moves as normal along the X axis.
					else
					{
						ball.x += leftoverVel.getX();
						leftoverVel.x = 0;
					}
				}
				
				// This if acts as a failsafe, preventing the ball from becoming trapped if collision code somehow fails.
				if (Math.abs(oldVel.getX()) <= Math.abs(leftoverVel.getX())) {
					// Sets to 0 to prevent an infinite loop due to the ball being trapped
					leftoverVel.x = 0;
				}
			}
			
			// Handles decrementation of Y (vertical collisions).
			if (leftoverVel.getY() != 0)
			{
				// Updates another temporary value, the old velocity.
				oldVel.y = (int)leftoverVel.getY();
				
				// Checks to see if the returned float from getFirst...Collision() is finite,
				// - if not, the function failed to find a collision.
				if (Float.isFinite(collisionPos = getFirstVerticalCollision(leftoverVel.y)))
				{
					// Checks to see if <ball> is currently moving downwards.
					if (leftoverVel.y > 0)
					{
						// If so, the leftover velocity point is decremented,
						// - afterward, <ball>'s position and <velocity> are updated to reflect momentum.
						leftoverVel.y -= (int)(collisionPos - (ball.getY() + ball.getHeight()));
						ball.y = (int)(collisionPos - ball.getHeight()) - 1;
						velocity.y = -BOUNCE_SPEED;
					}
					
					// Otherwise, the ball must be moving upwards.
					else
					{
						// The leftover velocity point is decremented,
						// - afterward, <ball>'s position and <velocity> are updated to reflect momentum.
						leftoverVel.y -= (int)(collisionPos - ball.getY());
						ball.y = (int)collisionPos + 1;
						velocity.y = BOUNCE_SPEED;
					}
				}
				
				// Checks for parimeter collisions, in the event that <ball> collided with no rectangles.
				else {
					
					// Checks the bottom boundary of the screen first.
					if (ball.getY() + ball.getHeight() + leftoverVel.y > screenHeight - 2)
					{
						// If so, subtracts the overlap amount from the leftover velocity.
						leftoverVel.y = -(int)(leftoverVel.y - (ball.getY() + ball.getHeight() + leftoverVel.y - (screenHeight - 2)));
						
						// - afterward, places the ball's position at the edge of the wall that it collided with.
						ball.y = screenHeight - 2 - (int)ball.getHeight();
						velocity.y = -BOUNCE_SPEED;
					}
					
					// Checks the top boundary of the screen next.
					else if (ball.getY() + leftoverVel.y < 1) {
						
						// If so, subtracts the overlap amount from the leftover velocity.
						leftoverVel.y = -(int)(leftoverVel.y + (1 - (ball.getY() + leftoverVel.y)));
						
						// - afterward, places the ball's position at the edge of the wall that it collided with.
						ball.y = 1;
						velocity.y = BOUNCE_SPEED;
					}
					
					// Otherwise, the ball moves as normal along the Y axis.
					else
					{
						ball.y += leftoverVel.getY();
						leftoverVel.y = 0;
					}
				}
				
				
				// This if acts as a failsafe, preventing the ball from becoming trapped if collision code somehow fails.
				if (Math.abs(oldVel.getY()) <= Math.abs(leftoverVel.getY())) {
					// Sets to 0 to prevent an infinite loop due to the ball being trapped
					leftoverVel.y = 0;
				}
			}
		}
		
		// Returns, ending the function.
		return;
	}
	
	
	
	/*
	The getFirstHorizontalCollision() method:
		Description: 
			- Uses a predetermined logic gate to return the closest X collision with <ball> within <rects>. 
			* this function is synchronized, for use in threads.
		Preconditions:
			- the constructor (Ball()) has already been ran,
			<horVel> : must be a valid integer value. (immutable horizontal velocity)
		Postconditions:
			- Returns the closest X collisions found around <ball>,
			  + otherwise, returns infinity instead.
	*/
	private synchronized float getFirstHorizontalCollision(final int horVel)
	{
		// 
		float closestBoundPosition = Float.POSITIVE_INFINITY;
		
		// Uses a different preset of values when <ball> is moving to the right.
		if (horVel > 0) {
			
			// Itterates a specific comparison over every Rectangle() in <rects>.
			for (Rectangle rect : rects)
			{
				// This wad of conditionals determines whether or not a position is currently the closest possible to the given collision.
				// If so, the <rect> of this iteration's X value becomes the new <closesetBoundPosition>.
				if(
				(rect.getX() > ball.getX() + ball.getWidth() / 2)                                              &&
				(rect.getX() - (ball.getX() + ball.getWidth()) < closestBoundPosition)                         &&
				(ball.getY() < rect.getY() + rect.getHeight() && ball.getY() + ball.getHeight() > rect.getY()) &&
				(ball.getX() + ball.getWidth() + horVel >= rect.getX()))
					closestBoundPosition = (int)rect.getX();
				
			}
		}
		
		// Uses a different preset of values when <ball> is moving to the left.
		else
		{
			
			// Itterates a specific comparison over every Rectangle() in <rects>.
			for (Rectangle rect : rects)
			{
				// This wad of conditionals determines whether or not a position is currently the closest possible to the given collision.
				// If so, the <rect> of this iteration's X value becomes the new <closesetBoundPosition>.
				if(
				(rect.getX() + rect.getWidth() < ball.getX() + ball.getWidth() / 2)                            &&
				(ball.getX() - (rect.getX() + rect.getWidth()) < closestBoundPosition)                         &&
				(ball.getY() < rect.getY() + rect.getHeight() && ball.getY() + ball.getHeight() > rect.getY()) &&
				(ball.getX() + horVel <= rect.getX() + rect.getWidth()))
					closestBoundPosition = (int)(rect.getX() + rect.getWidth());
			}
		}
		
		// Returns <closestBoundPosition>, ending the function.
		return closestBoundPosition;
	}


	
	/*
	The getFirstVerticalCollision() method:
		Description: 
			- Uses a predetermined logic gate to return the closest Y collision with <ball> within <rects>. 
			* this function is synchronized, for use in threads.
		Preconditions:
			- the constructor (Ball()) has already been ran,
			<verVel> : must be a valid integer value. (immutable horizontal velocity)
		Postconditions:
			- Returns the closest Y collisions found around <ball>,
			  + otherwise, returns infinity instead.
	*/
	private synchronized float getFirstVerticalCollision(final int verVel) {
		float closestBoundPosition = Float.POSITIVE_INFINITY;
		
		// Uses a different preset of values when <ball> is moving upwards.
		if (verVel > 0) 
		{
			
			// Itterates a specific comparison over every Rectangle() in <rects>.
			for (Rectangle rect : rects)
			{
				
				// This wad of conditionals determines whether or not a position is currently the closest possible to the given collision.
				// If so, the <rect> of this iteration's Y value becomes the new <closesetBoundPosition>.
				if(
				(rect.getY() > ball.getY() + ball.getHeight() / 2)                                           &&
				(rect.getY() - (ball.getY() + ball.getHeight()) < closestBoundPosition)                      &&
				(ball.getX() < rect.getX() + rect.getWidth() && ball.getX() + ball.getWidth() > rect.getX()) &&
				(ball.getY() + ball.getHeight() + verVel >= rect.getY()))
					closestBoundPosition = (int)rect.getY();

			}
		}
		
		// Uses a different preset of values when <ball> is moving downwards.
		else
		{
			
			// Itterates a specific comparison over every Rectangle() in <rects>.
			for (Rectangle rect : rects)
			{
				
				// This wad of conditionals determines whether or not a position is currently the closest possible to the given collision.
				// If so, the <rect> of this iteration's Y value becomes the new <closesetBoundPosition>.
				if(
				(rect.getY() + rect.getHeight() < ball.getY() + ball.getHeight() / 2)                        &&
				(ball.getY() - (rect.getY() + rect.getHeight()) < closestBoundPosition)                      &&
				(ball.getX() < rect.getX() + rect.getWidth() && ball.getX() + ball.getWidth() > rect.getX()) &&
				(ball.getY() + verVel <= rect.getY() + rect.getHeight()))
					closestBoundPosition = (int)(rect.getY() + rect.getHeight());
		
			}
		}
		
		// Returns <closestBoundPosition>, ending the function.
		return closestBoundPosition;
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

		// Checks to see if the new screen dimensions would cause a size violation.
		if (hasSizeViolation())
		{
			// If so, the screen is not resized.
			this.screenWidth = oldWidth;
			this.screenHeight = oldHeight;
			value = false;
		}
		
		// Otherwise, the screen is resized, and the graphics buffer is reset.
		if(value)
		{
			buffer.flush();
			buffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		}
		
		// Returns value, ending the function.
		return value;
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
		int oldX = ball.x;
		int oldY = ball.y;
		
		// Stores the old size of the ball.
		int oldSize = ball.width;

		// Forces the potential new value(s) to be even.
		ball.height = (size / 2) * 2;
		ball.width = ball.height;
		
		// Repositions the ball using the old values defined above,
		// - this is done to make it seem as though the ball is growing from the center.
		ball.x += (oldSize - ball.width) / 2;
		ball.y += (oldSize - ball.height) / 2;

		// Checks to see if the new ball size would cause a size violation.
		if (hasSizeViolation())
		{
			// if so, all of the old value(s) replace what would have been the current ones.
			ball.x = oldX;
			ball.y = oldY;
			ball.width = oldSize;
			ball.height = oldSize;
			value = false;
		}
		
		// Otherwise, repaint() is called, before the function returns.
		if(value)
			repaint();

		// Returns <value>, ending the function.
		return value;
	}


	/*
	The tryUpdateSize() method:
		Description: 
			- Returns <ball>'s current size.
		Preconditions:
			- the constructor (Ball()) has already been ran.
		Postconditions:
			- Returns <ball>'s current size.
	*/
	public int getBallSize()
	{
		// Returns the current ball size (casted as an integer), ending the function.
		return (int) ball.getWidth();
	}


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
		(ball.x + ball.width > screenWidth - 2)   ||
		(ball.x < 1)                              ||
		(ball.y + ball.height > screenHeight - 2) ||
		(ball.y < 1))
			value = true;
		
		// Otherwise, checks to see if any rectangle(s) would collide with the ball, or the screen width/height,
		// If so, <value> == true.
		else
		{
			for (Rectangle rect : rects)
			{
				if(
				(rect.intersects(ball))                   ||
				(rect.x + rect.width > screenWidth - 2)   ||
				(rect.x < 1)                              ||
				(rect.y + rect.height > screenHeight - 2) ||
				(rect.y < 1))
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
		for (Rectangle rect : rects)
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
		if (mouseActive)
			tryRemoveRect(m1);

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
	public void mousePressed(MouseEvent e) {
		if (mouseActive)
			m1 = e.getPoint();
		
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
	public void mouseReleased(MouseEvent e) {
		if (mouseActive && m2.x > -1)
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
		m2 = e.getPoint();
		setDragBox(Math.min(m1.x, m2.x), Math.min(m1.y, m2.y), Math.abs(m1.x - m2.x), Math.abs(m1.y - m2.y));
		
		// Returns, ending the function.
		return;
	}

	// Overwrites, but does not define mouseMoved() (unimplemented).
	@Override
	public void mouseMoved(MouseEvent e) {return;}
}