/*
.Program5, Bouncing Ball II           (BouncingBall.java).
.Created By:                                             .
- Daniel Hentosz,    (HEN3883@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu).                 .
.Last Revised: March 25th, 2021.              (3/25/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
	For more implementation details, see the class header at GUIBounce().
		
*/

// Packages the program into a folder called "BouncingBall",
// When compiling this file via javac, intended command notation is "javac -d . BouncingBall.java",
// - intended run notation is "java BouncingBall.BouncingBall" (contains main() method of this file).
package BouncingBall;


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
public class BouncingBall
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

	// Defines imutable versions of the frame <WIDTH> and <HEIGHT>,
	// - <center> also is defined (mutable).
	private final int WIDTH = 640;
	private final int HEIGHT = 400;
	private int winWidth  = WIDTH;
	private int winHeight = HEIGHT;
	private int center;

	// Our Layout manager.
	private GridBagLayout gbl;
	private GridBagConstraints gbc;

	private double colweight[] = { 1, 1, 1, 1, 1};
	private double rowweight[] = { 1, 1};
	private int width[]        = { 1, 1, 1, 1, 1};
	private int height[]       = { 1, 1, 1, 1, 1};
	

	
	private Button btn_run;
	private Button btn_pause;
	private Button btn_quit;
	
	private Panel controls  = new Panel();
	private Panel canvas   = new Panel();
	
	private Thread thread = null;
	private boolean is_running = false;
	
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
		makeSheet();
		
		try {
			initComponents();
		} catch (Exception e) {e.printStackTrace();}
		
		// sets each component's layout manager.
		setLayout(new BorderLayout());

		
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		
		gbl.rowHeights    = height;
		gbl.columnWidths  = width;
		gbl.columnWeights = colweight;
		gbl.rowWeights    = rowweight;
		
		this.pack();
		controls.setLayout(gbl);

		controls.setVisible(true);
		this.add("South", controls);
		
		canvas.setLayout(null);
		
		setVisible(true); // - makes the current window visible.
		
		sizeScreen();
		
		start();
		
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
		
		btn_run   = new Button("Run");
		controls.add(btn_run);
		
		btn_pause = new Button("Pause");
		controls.add(btn_pause);
		
		btn_quit  = new Button("Quit");
		controls.add(btn_quit);
		

		
		this.addComponentListener(this);
		this.addWindowListener(this);		

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
		this.setBounds(20, 20, WIDTH, HEIGHT);
		controls.setSize(WIDTH, HEIGHT/4);

		/*
		// Constrains <btn_confirm> ("OK") to the left side of the screen (beside <txtbx_input>).
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbl.setConstraints(btn_run, gbc);
		
		// Constrains <lbl_1> ("Source:") to the left side of the screen (with relevant offsets).
		gbc.gridy = 6;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(lbl_1, gbc);
		this.add(lbl_1); */
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
		//delay = sbMaxSpeed - speedBar.getValue() + 2;
		
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
			- Iterates so long as <is_running> is true, and calls <canvObj> depending on whether or not the program is currently paused.
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- The program is terminated alongside this function returning (see stop() for details).
	*/
	@Override
	public void run() {
		// Iterates so long as <isRunning> is true.
		while (is_running) {

			// Lets the thread sleep for one tick, allowing for interupts.
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		is_running = false;

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
		winWidth  = getWidth();
		winHeight = getHeight();
		
		// Calls makeSheet() to update internal offsets/values.
		makeSheet();
		
		
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
