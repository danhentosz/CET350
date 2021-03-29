/*
.Program5, Bouncing Ball II           (BouncingBall.java).
.Created By:                                             .
- Daniel Hentosz,    (HEN3883@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu).                 .
.Last Revised: March 30th, 2021.              (3/30/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
	Makes use of java's <awt> library to create a GUI,
		- this GUI controls a object which will bounce around the frame.
		
	The user can:
		- Select a speed at which the object moves,
		- change the size of the object,
		- and chose rather the object moves, or does not move.
	
	For more implementation details, see the class header at GUIBounceII().
		
*/

// Packages the program into a folder called "Bounce",
// When compiling this file via javac, intended command notation is "javac -d . Program5.java",
// - intended run notation is "java Bounce.Program5" (contains main() method of this file).
package BouncingBall;


// Imports components required for Frame(), Runnable(), and various action listeners. 
// ...lang.Thread() is also imported,
// ...image.BufferedImage() also is imported, for use in Objc.
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.Thread;
import java.util.Vector;
import java.util.Queue;

/*
The Program5() Class:
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
			- Opens a new instance of Bounce(). 
		Preconditions:
			N/A: method ignores values in <args>[].
		Postconditions:
			- Creates a new instance of Bounce, <gui>,
				+ from here, implemented methods loop until the program is exited.
	*/
	public static void main(String[] args)
	{
		Bounce gui = new Bounce();
		return;
	}
}



/*
The Bounce() Class:
	Description:
		- serves as a container window (Frame()) for an instance of Ball(),
		- allows interface with Ball() via several UI elements(),
			+ Speed Controls   - Scroll (Controls Ball()'s current iteraton speed; higher = faster),
			+ Run     / Stop   - Button (Toggles between running and pausing Ball()'s iterations),
			+ Circle  / Square - Button (Toggles the vector-shape inside of Ball() between a circle and a square),
			+ No Tail / Tail   - Button (Toggles whether or not the vector-shape inside of Ball() leaves a drawn tail on the canvas),
			+ Clear            - Button (Clears the current canvas inside of Ball()),
			+ Quit Button      - Button (Exits the program (functionality is the same as clicking the [x] at the upper right),
			+ Size Controls    - Scroll (Controls Ball()'s vector-shape's current size on the screen; higher = larger).
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
class Bounce extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, 
Runnable {
	// Defines <serialVersionUID>, a universal identifier for this frame class's instances.
	// - this variable is FINAL, and cannot be changed.
	static final long serialVersionUID = 10L;
	
	private Panel cntrlPanel;
	private Panel ballPanel;
	
	// Defines <INIT_SIZE>, which is carried onto objW (the mutable component width).
	private final int INIT_SIZE = 21;
	
	// Defines the MAX and MIN size for Ball's shape, <MAX_SIZE> and <MIN_SIZE>.
	private final int MAX_SIZE = 100;
	private final int MIN_SIZE = 10;
	
	// Defines <SB_SPEED>, the starting value of the ScrollBar("Speed").
	private final int SB_SPEED = 50;
	
	// Defines <SB_VIS>, a constant added to the width of ScrollBar() instances.
	private final int SB_VIS = 10;

	// Defines mutable versions of the frame <width>, <height>, and <center>.
	private int winWidth = 640;
	private int winHeight = 400;
	private int oldWinWidth;
	private int oldWinHeight;

	// Defines the minimum, maximum, and current speed values which can be held by a ScrollBar() instance (defined in initComponents()). 
	private int sbMinSpeed = 1;
	private int sbMaxSpeed = 100 + SB_VIS;
	private int sbSpeed = SB_SPEED;
	
	// Defines delay, a default value transformed by the current value of <sbSpeed>.
	private int delay = 16;

	// Defines <ball>, which serves as this Frame()'s implementation of a canvas (via an instance of a class that extends to Canvas()).
	private Ballc ball;

	private boolean isRunning = true;
	private boolean isPaused  = true;
	
	private Thread thread;

	// Our buttons:
	private Button startButton;
	private Button pauseButton;
	private Button quitButton;
	
	GridBagConstraints constraints;

	private Label speedLabel = new Label("Speed", Label.CENTER);
	private Label sizeLabel = new Label("Size", Label.CENTER);

	private Scrollbar sizeBar;
	private Scrollbar speedBar;

	/*
	The Bounce() constructor:
		Description: 
			- calls several subfunctions to initalize a Bounce() object. 
		Preconditions:
			N/A.
		Postconditions:
			- Returns an initalized instance of Bounce(), which will be running it's main loop (see start() and run() for details).
	*/
	public Bounce() {
		setLayout(new BorderLayout());
		setVisible(true);// Make the frame visible.
		try {
			initComponents();// Try to initialize the components.
		} catch (Exception e) {
			e.printStackTrace();
		}
		makeSheet();// Determines the sizes for the sheet.
		start();
	}

	private void start() {
		delay = sbMaxSpeed - speedBar.getValue() + 2;
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
			- Mutates various mutable values inside of Bounce(), and also resets the background color.
	*/
	public void makeSheet() {
		if (ball != null) {
			if (!ball.tryResizeCanvas(ballPanel.getWidth(), ballPanel.getHeight())) {
				this.setSize(oldWinWidth, oldWinHeight);
				validate();
				winWidth = oldWinWidth;
				winHeight = oldWinHeight;
			}
		}
	}

	/*
	The initComponents() method:
		Description: 
			- Instantiates all component objects used by Bounce(),
				+ also adds all components to their relevant listeners. 
		Preconditions:
			- Must be called after makeSheet().
		Postconditions:
			- Populates all object-associated variables with their proper values, and adds them to implemented listeners of GUIBounce().
	*/
	public void initComponents() throws Exception, IOException {
		constraints = new GridBagConstraints();
		
		startButton = new Button("Run");
		quitButton = new Button("Quit");
		pauseButton = new Button("Pause");
		
		pauseButton.setEnabled(false);
		
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
		
		cntrlPanel = new Panel(new GridBagLayout());
		ballPanel = new Panel();
		ballPanel.setLayout(new BorderLayout(0, 0));
		
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		
		addToControlPanel(speedBar, 0, 0);
		addToControlPanel(speedLabel, 0, 1);
		addToControlPanel(startButton, 1, 0);
		addToControlPanel(pauseButton, 2, 0);
		addToControlPanel(quitButton, 3, 0);
		addToControlPanel(sizeBar, 4, 0);
		addToControlPanel(sizeLabel, 4, 1);
		
		speedBar.addAdjustmentListener(this);
		sizeBar.addAdjustmentListener(this);
		startButton.addActionListener(this);
		pauseButton.addActionListener(this);
		quitButton.addActionListener(this);
		
		this.addComponentListener(this);
		this.addWindowListener(this);
		this.setPreferredSize(new Dimension(winWidth, winHeight));
		this.setMinimumSize(getPreferredSize());
		this.add(cntrlPanel, BorderLayout.SOUTH);
		this.add(ballPanel, BorderLayout.CENTER);
		
		cntrlPanel.setBackground(Color.lightGray);
		ballPanel.setBackground(Color.white);
		
		sizeLabel.setBackground(Color.lightGray);
		speedLabel.setBackground(Color.lightGray);
		
		cntrlPanel.setVisible(true);
		ballPanel.setVisible(true);
		
		speedBar.setEnabled(true);
		speedBar.setVisible(true);
		
		validate();
		
		// Instantiated after first "validate" so that the "ballPanel" will have already calculated its initial size
		ball = new Ballc(sizeBar.getValue(), ballPanel.getWidth(), ballPanel.getHeight());
		ballPanel.add("Center", ball);
		
		validate();
	}
	
	private void addToControlPanel(Component component, int gridx, int gridy) {
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		cntrlPanel.add(component, constraints);
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
		isRunning = false;
		
		startButton.removeActionListener(this);
		pauseButton.removeActionListener(this);
		quitButton.removeActionListener(this);
		
		speedBar.removeAdjustmentListener(this);
		sizeBar.removeAdjustmentListener(this);
		
		ball.removeListeners();
		
		this.removeComponentListener(this);
		this.removeWindowListener(this);
		
		dispose();
		System.exit(0);
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
		// Continues to loop as long as the run flag is true.
		while (isRunning) {
			if (!isPaused) {
				ball.updatePhysics();
				ball.repaint();
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
		Scrollbar sb = (Scrollbar) e.getSource();
		
		if (sb == speedBar) {
			delay = sbMaxSpeed - speedBar.getValue() + 2;
		} else if (sb == sizeBar) {
			// Checks if the size update was unsuccessful
			if (!ball.tryUpdateSize(sizeBar.getValue())) {
				sizeBar.setValue(ball.getBallSize());
			}
		}
	}

	/*
	The actionPerformed() method:
		Description: 
			- Listens for ActionEvents, and alters components of Bounce() depending on their source.
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- Properly alters internal variables, labels, or variables of <canvObj> depending on the button pressed.
	*/
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == startButton) {
			if (isPaused) {
				isPaused = false;
				startButton.setEnabled(false);
				pauseButton.setEnabled(true);
			}
		} else if (source == pauseButton) {
			if (isPaused) {
				pauseButton.setLabel("Pause");
			} else {
				pauseButton.setLabel("Unpause");
			}
			isPaused = !isPaused;
		} else { // Quit button
			stop();
		}
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
		oldWinWidth = winWidth;
		oldWinHeight = winHeight;
		winWidth = this.getWidth();
		winHeight = this.getHeight();
		// Check the size and position of the ball and determine if the new width and height are acceptable
		makeSheet();
	}

	/*
	The windowClosing() method:
		Description: 
			- Terminates the program whenever the windowClosing() window event occurs.
		Preconditions:
			- Can only be called after initalization. 
		Postconditions:
			- Calls stop(), which assists in destructing Bounce().
	*/
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







class Ballc extends Canvas implements MouseListener, MouseMotionListener {
	static final long serialVersionUID = 11L;
	
	private final int BOUNCE_SPEED = 1;

	private int screenWidth;
	private int screenHeight;
	
	
	private Rectangle ball = new Rectangle();
	private Rectangle dragBox = new Rectangle();
	private float collisionPos;
	private Point velocity_current    = new Point(BOUNCE_SPEED, BOUNCE_SPEED);
	

	

	
	private Vector<Rectangle> rects = new Vector<Rectangle>();
	
	private boolean dragBoxActive = false;
	private boolean mouseActive = false;
	private Point m1 = new Point();
	private Point m2 = new Point();
	
	// Defines components used for doublebuffering. 
	private Image buffer; // - actual buffer image, <buffer>,
	private Graphics g;   // - replacement graphical component, <g>.

	Ballc(int ballSize, int screenWidth, int screenHeight) {
		this.screenWidth  = screenWidth;
		this.screenHeight = screenHeight;
		
		buffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		
		
		ball.x = screenWidth - ballSize;
		ball.y = 0;
		
		// ball.x = (int)(Math.random() * (screenWidth - ballSize - 2)) + 1;
		// ball.y = (int)(Math.random() * (screenHeight - ballSize - 2)) + 1;
		ball.width = ballSize;
		ball.height = ballSize;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public void removeListeners() {
		this.removeMouseListener(this);
		this.removeMouseMotionListener(this);
	}
	
	private synchronized void tryAddRect() {
		if (dragBox.getWidth() * dragBox.getHeight() <= 2) {
			tryRemoveRect(dragBox.getLocation());
		} else {
			boolean valid = true;
			// Check if the new rect set by "dragBox" will intersect the ball
			if (!dragBox.intersects(ball)) {
				// If it will not intersect the ball then check if it is contained within any other rects
				for (Rectangle rect : rects) {
					if (rect.contains(dragBox)) {
						valid = false;
						break;
					}
				}
				// If it is still okay then continue
				if (valid) {
					// Remove any rects that are contained within this rect
					for (int i = rects.size() - 1; i > -1; i--) {
						if (dragBox.contains(rects.elementAt(i))) {
							rects.remove(i);
						}
					}
					// Add this rect to the Vector
					rects.add(new Rectangle(dragBox));
				}
			}
		}
	}
	
	private void setDragBox(int x, int y, int width, int height) {
		dragBoxActive = true;
		dragBox.setBounds(x, y, width, height);
		repaint();
	}
	
	private synchronized void tryRemoveRect(Point point) {
		// Moves backwards through the Vector so that the removal of elements does not mess up the indexing
		for (int i = rects.size() - 1; i > -1; i--) {
			if (rects.elementAt(i).contains(point)) {
				rects.remove(i);
			}
		}
		repaint();
	}

	// Updates the position of the object
	public void updatePhysics() {
		
		Point collision_mask_1 = new Point(0, 0);
		Point collision_mask_2 = new Point(0, 0);
		Point collision_mask_3 = new Point(0, 0);
		Point collision_mask_4 = new Point(0, 0);
		
		// Initalizes two counters, which are used to track X and Y velocity changes.
		int jx = 0;
		int jy = 0;
		
		// Initalizes two dummied values, which are used to represent <ball>'s position on the next frame.
		int new_x = ball.x + velocity_current.x;
		int new_y = ball.y + velocity_current.y;
		
		// Checks to see if the ball is going to exit the X bounds of the canvas.
		if (new_x + ball.width >= screenWidth   || new_x <= 0)
		{
			// If so, X velocity is reflected (and that change is reflected onto <new_x>).
			velocity_current.x *= -1;
			new_x += velocity_current.x;
			jx++;
		}
		
		
		// Checks to see if the ball is going to exit the Y bounds of the canvas.
		if (new_y + ball.height >= screenHeight || new_y <= 0)
		{
			// If so, Y velocity is reflected (and that change is reflected onto <new_y>).
			velocity_current.y *= -1;
			new_y += velocity_current.y;
			jy++;
		}
		
		// Check to see if the ball is going to collide with any member of <rects> on the X plane.
		for (Rectangle rect : rects)
		{
			
			if (rect.contains(new_x, new_y) || rect.contains(new_x + ball.width, new_y + ball.height))
			{
				collision_mask_1 = getCollisionDirection(new_x, new_y, rect.x, rect.y);
				collision_mask_2 = getCollisionDirection(new_x + ball.width, new_y + ball.height, rect.x, rect.y);
				collision_mask_3 = getCollisionDirection(new_x, new_y, rect.x + rect.width, rect.y + rect.height);
				collision_mask_4 = getCollisionDirection(new_x + ball.width, new_y + ball.height, rect.x + rect.width, rect.y + rect.height);
				
				collision_mask_1.x *= collision_mask_2.x;
				collision_mask_1.y *= collision_mask_2.y;
				collision_mask_1.x *= collision_mask_3.x;
				collision_mask_1.y *= collision_mask_3.y;
				collision_mask_1.x *= collision_mask_4.x;
				collision_mask_1.y *= collision_mask_4.y;
				
				velocity_current.x *= collision_mask_1.x;
				velocity_current.y *= collision_mask_1.y;
				
				jx ++;
				jy ++;
			};
		};
		
		if (jx <= 1)
			new_x = ball.x + velocity_current.x;
			ball.x = new_x;
		
		if (jy <= 1)
			new_y = ball.y + velocity_current.y;
			ball.y = new_y;
		
		
		return;
	}
	
	
	private Point getCollisionDirection(int cx, int cy, int tx, int ty)
	{
		Point value = new Point(0, 0);
		
		Point collision = new Point(cx, cy);
		Point target    = new Point(tx, ty);
		
		if(collision.x <= target.x)
			value.x = -1;
		else
			value.x = 1;
		
		if(collision.y <= target.y)
			value.y = 1;
		else
			value.y = -1;
		
		return value;
	}
	
	// Returns true if the boundaries are successfully resized else there was a boundary violation
	public boolean tryResizeCanvas(int screenWidth, int screenHeight) {
		int oldWidth = this.screenWidth;
		int oldHeight = this.screenHeight;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		if (hasSizeViolation()) {
			this.screenWidth = oldWidth;
			this.screenHeight = oldHeight;
			return false;
		}
		
		buffer.flush();
		buffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		return true;
	}
	
	// Returns true if the size was updated, and returns false if it was unsuccessful
	public boolean tryUpdateSize(int size) {
		// Repositions the ball so that it may grow about its middle #because the coords now refer to the upper-left corner
		int oldX = ball.x;
		int oldY = ball.y;
		int oldSize = ball.width;
		
		// Enforces an odd numbered size
		ball.height = (size / 2) * 2 + 1;
		ball.width = ball.height;
		
		ball.x += (oldSize - ball.width) / 2;
		ball.y += (oldSize - ball.height) / 2;
		
		// Checks if the new size is too large for the boundaries or not
		if (hasSizeViolation()) {
			ball.x = oldX;
			ball.y = oldY;
			ball.width = oldSize;
			ball.height = oldSize;
			return false;
		}
		
		repaint();
		
		return true;
	}
	
	public int getBallSize() {
		return (int)ball.getWidth();
	}
	
	private boolean hasSizeViolation() {
		boolean returnVal = false;
		
		if (ball.x + ball.width > screenWidth - 2)
			returnVal = true;
		else if (ball.x < 1)
			returnVal = true;
		else if (ball.y + ball.height > screenHeight - 2)
			returnVal = true;
		else if (ball.y < 1)
			returnVal = true;
		else {
			// Check for rectangle intersection
			for (Rectangle rect : rects) {
				if (rect.intersects(ball)) {
					returnVal = true;
					break;
				} else if (rect.x + rect.width > screenWidth - 2) {
					returnVal = true;
					break;
				} else if (rect.x < 1) {
					returnVal = true;
					break;
				} else if (rect.y + rect.height > screenHeight - 2) {
					returnVal = true;
					break;
				} else if (rect.y < 1) {
					returnVal = true;
					break;
				}
			}
		}
		
		return returnVal;
	}
	
	
	
	@Override
	public void update(Graphics cg) { 
		paint(cg);
	}

	@Override
	public synchronized void paint(Graphics cg) {
		if (g != null)
			g.dispose();
		g = buffer.getGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, screenWidth, screenHeight);
		
		// Drawing the border
		g.setColor(Color.red);
		g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
		
		// Drawing the rectangles
		for (Rectangle rect : rects) {
			g.setColor(Color.orange);
			g.fillRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
			g.setColor(Color.black);
			g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
		}
		
		// Drawing the ball
		g.setColor(Color.lightGray);
		g.fillOval((int)ball.getX(), (int)ball.getY(), (int)ball.getWidth(), (int)ball.getHeight());
		g.setColor(Color.black);
		g.drawOval((int)ball.getX(), (int)ball.getY(), (int)ball.getWidth(), (int)ball.getHeight());
		
		// Drawing the drag box
		if (dragBoxActive) {
			g.setColor(Color.black);
			g.drawRect((int)dragBox.getX(), (int)dragBox.getY(), (int)dragBox.getWidth(), (int)dragBox.getHeight());
		}
		
		cg.drawImage(buffer,  0,  0,  null);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (mouseActive) {
			tryRemoveRect(m1);
		}
		m1.x = -1;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (mouseActive) {
			m1 = e.getPoint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (mouseActive && m2.x > -1) {
			tryAddRect();
		}
		dragBoxActive = false;
		m2.x = -1;
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseActive = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseActive = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		m2 = e.getPoint();
		setDragBox(Math.min(m1.x, m2.x), Math.min(m1.y, m2.y), Math.abs(m1.x - m2.x), Math.abs(m1.y - m2.y));
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
}