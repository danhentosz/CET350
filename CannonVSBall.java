/*
.Program6, Bounce: Newton's Third     (CannonVSBall.java).
.Created By:                                             .
- Daniel Hentosz,    (HEN3883@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu).                 .
.Last Revised: April 13th, 2021.              (4/13/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
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

	// Defines the starting size for Ball's shape.
	private int size = 40;
	
	private final int SB_ANGLE_MIN = 90;
	private final int SB_ANGLE_MAX = 180;

	// Defines <SB_ANGLE>, the starting value of the ScrollBar("Angle").
	private final int SB_ANGLE = (SB_ANGLE_MAX + SB_ANGLE_MIN) / 2;
	
	// Defines the minimum, maximum, and current speed values which can be held by a
	// ScrollBar() instance (defined in initComponents()).
	private final int SB_MIN_VELOCITY = 100;
	private final int SB_MAX_VELOCITY = 500;

	// Defines <SB_VELOCITY>, the starting value of the ScrollBar("Velocity").
	private final int SB_VELOCITY = (SB_MIN_VELOCITY + SB_MAX_VELOCITY) / 2;

	// Defines <SB_VIS>, a constant added to the width of ScrollBar() instances.
	private final int SB_VIS = 10;


	// Defines <radioButton> a string which passes the pressed radio-button-checkbox to Ball().
	private String radioButton = "";



	// Defines delay, a default value transformed by the current value of <sbSpeed>.
	private int delay = 25;

	private int sbVelocity    = SB_VELOCITY;

	private int speed = 20;

	private int time_ms = 0;
	private int time_s  = 0;

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
	
	private Label ballLabel = new Label  ("Ball Score  ");
	private Label cannonLabel = new Label("Player Score");
	private Label timeLabel=new Label("Time");
	private Label messageLabel = new Label("                              ");
	
	
	// Defines two scrollbars (which allow the user to change the size and speed of the Ball within Ball()).
	private Scrollbar velocityBar;
	private Scrollbar angleBar;

	private TextField ballScoreField = new TextField("0");
	private TextField cannonScoreField = new TextField("0");
	private TextField timeField=new TextField("0");
	
	private int ballScore = 0;
	private int cannonScore = 0;

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
		menuControl.addSeparator();
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
		
		
		sizeXSmall.addItemListener(this);
		sizeSmall.addItemListener(this);
		sizeMedium.addItemListener(this);
		sizeLarge.addItemListener(this);
		sizeXLarge.addItemListener(this);



		
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
		
		
		speedXSlow.addItemListener(this);
		speedSlow.addItemListener(this);
		speedMedium.addItemListener(this);
		speedFast.addItemListener(this);
		speedXFast.addItemListener(this);
		
		
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
		
		
		mercury.addItemListener(this);
		venus.addItemListener(this);
		earth.addItemListener(this);
		earthsMoon.addItemListener(this);
		mars.addItemListener(this);
		jupiter.addItemListener(this);
		saturn.addItemListener(this);
		uranus.addItemListener(this);
		neptune.addItemListener(this);
		pluto.addItemListener(this);
		
		
		// Sets radio-button default values.
		sizeMedium.setState(true);
		speedMedium.setState(true);
		earth.setState(true);
		
		// Sets the constant value(s) associated with the speed scrollBar(),
		// - also changes the background color of this component to gray.
		velocityBar = new Scrollbar(Scrollbar.HORIZONTAL);
		velocityBar.setMaximum(SB_MAX_VELOCITY + SB_VIS);
		velocityBar.setMinimum(SB_MIN_VELOCITY);
		velocityBar.setValue(sbVelocity);
		velocityBar.setBackground(Color.gray);


		// Sets the constant value(s) associated with the size scrollBar(),
		// - also changes the background color of this component to gray.
		angleBar = new Scrollbar(Scrollbar.HORIZONTAL);
		angleBar.setMaximum(SB_ANGLE_MAX + SB_VIS);
		angleBar.setMinimum(SB_ANGLE_MIN);
		angleBar.setValue(SB_ANGLE);
		angleBar.setBackground(Color.gray);
		
		
		ballScoreField.setEnabled(false);
		cannonScoreField.setEnabled(false);
		timeField.setEnabled(false);
	
		ballLabel.setBackground(Color.lightGray);
		cannonLabel.setBackground(Color.lightGray);
		timeLabel.setBackground(Color.lightGray);
		messageLabel.setBackground(Color.lightGray);

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
	int temp=0;
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
			
			// Sleeps for <delay>, which serves as an interpretation of the current <ball> speed.
			if (!isPaused)
			{
				time_ms += 1;

				if ((time_ms % (int)(delay/2)) == 0)
				{
					ball.updateBall();
				}
				ball.updateProjectile();
				
				if (ball.hasMessage()) {
					messageLabel.setText(ball.getMessage());
				}
				if (ball.ballWasHit()) {
					cannonScore++;
					cannonScoreField.setText(Integer.toString(cannonScore));
					ball.reset();
				} else if (ball.cannonWasHit()) {
					ballScore++;
					ballScoreField.setText(Integer.toString(ballScore));
					ball.reset();
				}
				
				if((time_ms % 350) == 0)
				{
					time_s += 1;
					timeField.setText(Integer.toString(time_s));
					time_ms -= 350;
				}

				ball.repaint();
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
	

 @Override
 public void itemStateChanged(ItemEvent e)
 {
	CheckboxMenuItem dummy = (CheckboxMenuItem)e.getSource();
	radioButton = dummy.getLabel();
	
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
		else dummy.setState(false);;

	}
	
	else if (dummy == speedXSlow || dummy == speedSlow || dummy == speedMedium || dummy == speedFast || dummy == speedXFast)
	{
		// Changes the current game speed.
		delay = speedRule(radioButton);
		speedXSlow.setState(false);
		speedSlow.setState(false);
		speedMedium.setState(false);
		speedFast.setState(false);
		speedXFast.setState(false);
		dummy.setState(true);

	}
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
	return; 
 };


	public int speedRule(String key)
	{
		int value = -1;
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
		return value;
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
		
		System.out.println(sb.getValue());
		
		if (sb == angleBar) {
			ball.setCannonAngle(SB_ANGLE_MAX - sb.getValue() + SB_ANGLE_MIN);
		} else if (sb == velocityBar) {
			ball.setProjectileVel(sb.getValue() * .005f);
		}

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
		
		if(source == Run)
		{
			isPaused = false;
			Run.setEnabled(false);
			Pause.setEnabled(true);
		}
		else if(source == Pause)
		{
			isPaused = true;
			Run.setEnabled(true);
			Pause.setEnabled(false);
		}
		else if(source == Reset)
		{
			// Reset function goes here.
		}		
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



class PhysicsObject
{
	private float velocityX = 0;
	private float velocityY = 0;
	
	private float posX = 0;
	private float posY = 0;
	private float width = 0;
	private float height = 0;
	
	private PhysicsObject lastHorCollision = null;
	private PhysicsObject lastVerCollision = null;
	
	private boolean isStatic = false;
	
	public PhysicsObject(Rectangle bounds, boolean isStatic) {
		posX = (float)bounds.getX();
		posY = (float)bounds.getY();
		width = (float)bounds.getWidth();
		height = (float)bounds.getHeight();
		this.isStatic = isStatic;
	}
	
	public PhysicsObject(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public boolean contains(Point p) {
		if ((posX < p.getX()) 		  &&
			(posX + width > p.getX()) &&
			(posY < p.getY())		  &&
			(posY + height > p.getY())) {
			return true;
		} else {
			return false;
		}
	}
	public boolean contains(PhysicsObject object) {
		if ((posX <= object.getX()) 							&&
			(posX + width >= object.getX() + object.getWidth()) &&
			(posY <= object.getY())								&&
			(posY + height >= object.getY() + object.getHeight())) {
			return true;
		} else {
			return false;
		}
	}
	public boolean intersects(PhysicsObject object) {
		if ((posX + width >= object.getX())				&&
			(posX <= object.getX() + object.getWidth()) &&
			(posY + height >= object.getY()) 			&&
			(posY <= object.getY() + object.getHeight())) {
			return true;
		} else {
			return false;
		}
	}
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
	public void setVelocityX(float x) {
		velocityX = x;
	}
	public void setVelocityY(float y) {
		velocityY = y;
	}
	public float getVelocityX() {
		return velocityX;
	}
	public float getVelocityY() {
		return velocityY;
	}
	public void setLocationX(float horPos) {
		posX = horPos;
	}
	public void setLocationY(float verPos) {
		posY = verPos;
	}
	public void addLocationX(float value) {
		posX += value;
	}
	public void addLocationY(float value) {
		posY += value;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public void setHorCollision(PhysicsObject collision) {
		lastHorCollision = collision;
	}
	// Returns null if there was no collision
	public PhysicsObject getLastHorCollision() {
		return lastHorCollision;
	}
	// Returns null if there was no collision
	public void setVerCollision(PhysicsObject collision) {
		lastVerCollision = collision;
	}
	public PhysicsObject getLastVerCollision() {
		return lastVerCollision;
	}
	public boolean isStatic() {
		return isStatic;
	}
}

class PhysicsLayer
{
	private Vector<PhysicsLayer> touchingLayers = new Vector<PhysicsLayer>();
	
	private Vector<PhysicsObject> colliders = new Vector<PhysicsObject>();
	
	public PhysicsLayer() {
		
	}
	public void addTouchingLayer(PhysicsLayer layer) {
		touchingLayers.add(layer);
	}
	public void removeTouchingLayer(PhysicsLayer layer) {
		touchingLayers.remove(layer);
	}
	public void addPhysicsObject(PhysicsObject object) {
		colliders.add(object);
	}
	public void removePhysicsObject(PhysicsObject object) {
		colliders.remove(object);
	}
	public void updatePhysics() {
		for (PhysicsObject collider : colliders) {
			
			collider.setHorCollision(null);
			collider.setVerCollision(null);
			
			if (!collider.isStatic()) {
				
				handleHorizontalCollisions(collider);
				if (collider.getLastHorCollision() == null) {
					collider.addLocationX(collider.getVelocityX());
				}
				
				handleVerticalCollisions(collider);
				if (collider.getLastVerCollision() == null) {
					collider.addLocationY(collider.getVelocityY());
				}
			}
		}
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
	private synchronized void handleHorizontalCollisions(PhysicsObject movingObj)
	{
		// 
		float closestBoundPosition = Float.POSITIVE_INFINITY;
		
		for (int i = 0; i < touchingLayers.size(); i++)
		{
			// Iterates a specific comparison over every Rectangle() in <rects>.
			for (PhysicsObject other : touchingLayers.elementAt(i).colliders)
			{					
				if (movingObj != other)
				{
					// This wad of conditionals determines whether or not a position is currently the closest possible to the given collision.
					// If so, the <rect> of this iteration's X value becomes the new <closesetBoundPosition>.
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
		
		return;
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
	private synchronized void handleVerticalCollisions(PhysicsObject movingObj)
	{
		// 
		float closestBoundPosition = Float.POSITIVE_INFINITY;
		
		for (int i = 0; i < touchingLayers.size(); i++)
		{
			// Iterates a specific comparison over every Rectangle() in <rects>.
			for (PhysicsObject other : touchingLayers.elementAt(i).colliders)
			{
				if (movingObj != other)
				{
					// This wad of conditionals determines whether or not a position is currently the closest possible to the given collision.
					// If so, the <rect> of this iteration's Y value becomes the new <closesetBoundPosition>.
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
	
		return;
	}
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
	
	private final int CANNON_PIVOT_SIZE = 40;
	
	private final int CANNON_WIDTH = 20;
	private final int CANNON_LENGTH = 80;
	
	// Distance between the cannon's pivot and the edge of the screen
	private final int CANNON_SPACING = 10;
	
	private final int BOUND_WIDTH = 10;
	
	// Defines <BOUNCE_SPEED>, which is the internal stepping value for the primary vector object inside of Ball().
	private final int BOUNCE_SPEED = 1;

	
	private final float PIXELS_PER_METER = 0.002f; // Find a good value once the projectile is implemented


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
	
	// Defines two toggle-booleans, which control whether or not the mouse is currently active, and whether or not it is affecting <dragBox>.
	private boolean dragBoxActive = false;
	private boolean mouseActive = false;
	private boolean cannonHit = false;
	private boolean ballHit = false;
	private boolean clickedOnce = false;
	private boolean comingBack = false;
	private boolean messageFlag = false;
	private boolean queuedShot = false;
	
	// A flag that is set to true after an instance of the projectile leaves the cannon's bounding box initially
	private boolean canHitCannon = false;
	
	private String message = "";

	// Defines components used for doublebuffering.
	private Image buffer; // - actual buffer image, <buffer>,
	private Graphics g;   // - replacement graphical component, <g>.

	private Polygon cannon = new Polygon();
	
	private Rectangle cannonPivot = new Rectangle(0, 0, CANNON_PIVOT_SIZE, CANNON_PIVOT_SIZE);
	
	private int cannonAngle = 0;
	
	private Point cannonEndPos = new Point();
	
	private float gravity = 1;
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
		
		// Sets the current ball width and height (simplified to 'size' by GUIBounceII()).
		ball.setWidth(ballSize);
		ball.setHeight(ballSize);
		
		ball.setVelocityX(BOUNCE_SPEED);
		ball.setVelocityY(BOUNCE_SPEED);
		
		// Adds relevant listeners to Ball(), before exiting the constructor.
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		ballLayer.addPhysicsObject(ball);
		
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
		
		screenBounds.addPhysicsObject(northBound);
		screenBounds.addPhysicsObject(southBound);
		screenBounds.addPhysicsObject(westBound);
		screenBounds.addPhysicsObject(eastBound);
		
		cannonLayer.addPhysicsObject(cannonHitBox);
		
		ballLayer.addTouchingLayer(screenBounds);
		ballLayer.addTouchingLayer(boxLayer);
		ballLayer.addTouchingLayer(cannonLayer);
		screenBounds.addTouchingLayer(ballLayer);
		screenBounds.addTouchingLayer(boxLayer);
		
		// Returns, exiting the function.
		return;
	}
	
	
	public void reset() {
		for (int i = rects.size() - 1; i > -1; i--) {
			boxLayer.removePhysicsObject(rects.elementAt(i));
			rects.remove(i);
		}
		ballHit = false;
		cannonHit = false;
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
		destroyProjectile();
		
		setMessage("");
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
			- Updates the position of the cannon relative to the screen dimensions 
			  + sets up the polygon that represents the cannon
	*/
	private void updateCannon() {
		// changes the cannon pivot's X and Y positions.
		cannonPivot.x = screenWidth  - CANNON_SPACING - CANNON_PIVOT_SIZE / 2;
		cannonPivot.y = screenHeight - CANNON_SPACING - CANNON_PIVOT_SIZE / 2;
		
		// Resets the cannon's current positional values (allows for a proper transform).
		cannon.reset();
		
		double radAngle = cannonAngle * (float)Math.PI / 180;
		
		double horValue = Math.cos(radAngle);
		double verValue = Math.sin(radAngle);
		
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
	
		cannonHitBox.setLocationX((int)cannonPivot.getX() - CANNON_LENGTH);
		cannonHitBox.setLocationY((int)cannonPivot.getY() - CANNON_LENGTH);
		
		// Adjusts the pivot's position.
		cannonPivot.x -= CANNON_PIVOT_SIZE / 2;
		cannonPivot.y -= CANNON_PIVOT_SIZE / 2;
	}
	
	
	/*
	The setCannonAngle() method:
	 
	*/
	public void setCannonAngle(int angle) {
		
		cannonAngle = angle;
		updateCannon();
		repaint();
	}
	
	
	public String getMessage() {
		messageFlag = false;
		return message;
	}
	
	
	public boolean hasMessage() {
		return messageFlag;
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
	
	
	public boolean cannonWasHit() {
		return cannonHit;
	}
	
	
	public boolean ballWasHit() {
		return ballHit;
	}
	
	public void setProjectileVel(float vel) {
		projectileVel = vel;
	}
	
	public void updateProjectile() {
		if (queuedShot) {
			queuedShot = false;
			tryFireCannon();
		}
		if (projectile != null) {
			
			// Set the new velocity of the projectile
			projectile.setVelocityY(projectile.getVelocityY() + gravity);
			
			// The screenBounds layer is reused for the projectile
			screenBounds.updatePhysics();
			
			if (!(canHitCannon || cannonHitBox.intersects(projectile))) {
				canHitCannon = true;
				screenBounds.addTouchingLayer(cannonLayer);
			}
			
			if (projectile.getLastHorCollision() != null) {
				if (projectile.getLastHorCollision() == ball) {
					ballHit = true;
					destroyProjectile();
				} else if (projectile.getLastHorCollision() == cannonHitBox) {
					cannonHit = true;
					destroyProjectile();
				} else {
					int i = rects.indexOf(projectile.getLastHorCollision());
					if (i != -1) {
						boxLayer.removePhysicsObject(rects.elementAt(i));
						rects.remove(i);
						destroyProjectile();
					}
				}
			} else if (projectile.getLastVerCollision() != null) {
				if (projectile.getLastVerCollision() == ball) {
					ballHit = true;
					destroyProjectile();
				} else if (projectile.getLastVerCollision() == cannonHitBox) {
					cannonHit = true;
					destroyProjectile();
				} else {
					int i = rects.indexOf(projectile.getLastVerCollision());
					if (i != -1) {
						boxLayer.removePhysicsObject(rects.elementAt(i));
						rects.remove(i);
						destroyProjectile();
					}
				}
			}
			
			// Ensures that the projectile was not destroyed due to a collision
			if (projectile != null) {
				
				// Checks if the projectile is out of bounds
				if (projectile.getY() > screenHeight 			  		  ||
					projectile.getX() + projectile.getWidth() < 0 		  ||
					projectile.getX() > screenWidth) {
					
					destroyProjectile();
					setMessage("It's not coming back");
					
				} else if (projectile.getY() + projectile.getHeight() < 0) {
					
					if (!comingBack) {
						float temp = -projectile.getVelocityY() / gravity;
						temp = (float)(projectile.getX() + (Math.sqrt((projectile.getHeight() - projectile.getY()) / gravity) - temp) * projectile.getVelocityX());
						// Check if coming back
						if (temp + projectile.getWidth() > 0 && temp < screenWidth - 1) {
							comingBack = true;
							setMessage("It's coming back!");
						} else {
							destroyProjectile();
							setMessage("It's not coming back");
						}
					}
				} else {
					comingBack = false;
				}
			}
		}
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
	public void updateBall()
	{
		ballLayer.updatePhysics();
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
	
	
	private void tryFireCannon() {
		if (projectile == null) {
			projectile = new PhysicsObject(false);
			projectile.setLocationX((float)cannonEndPos.getX() - CANNON_WIDTH / 2);
			projectile.setLocationY((float)cannonEndPos.getY() - CANNON_WIDTH / 2);
			projectile.setWidth(CANNON_WIDTH);
			projectile.setHeight(CANNON_WIDTH);
			projectile.setVelocityX(projectileVel * (float)Math.cos(cannonAngle * Math.PI / 180f));
			projectile.setVelocityY(projectileVel * -(float)Math.sin(cannonAngle * Math.PI / 180f));
			screenBounds.addPhysicsObject(projectile);
			if (canHitCannon) {
				canHitCannon = false;
				screenBounds.removeTouchingLayer(cannonLayer);
			}
			setMessage("");
		}
	}
	
	
	private void setMessage(String message) {
		messageFlag = true;
		this.message = message;
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

	public void setGrav(float value)
	{
		gravity = value;
		return;
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
		
		if (projectile != null) {
			g.setColor(Color.black);
			g.fillOval((int) projectile.getX(), (int) projectile.getY(), (int) projectile.getWidth(), (int) projectile.getHeight());
		}
		
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
		if (mouseActive) {
			if (cannon.contains(m1)) {
				queuedShot = true;
				clickedOnce = false;
			} else {
				if (clickedOnce) {
					clickedOnce = false;
					tryRemoveRect(m1);
				} else {
					clickedOnce = true;
				}
			}
		}

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
	public void mouseMoved(MouseEvent e) {
		clickedOnce = false;
		return;
	}
}