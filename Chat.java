/*
.Program7, Network Chat Program               (Chat.java).
.Created By:                                             .
- Nathaniel Dehart   (DEH5850@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu),                 .
- Daniel Hentosz,    (HEN3883@calu.edu).                 .
.Last Revised: April 27th, 2021.              (4/27/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:

*/




// Packages the program into a folder called "Chat",
// When compiling this file via javac, intended command notation is "javac -d . Chat.java",
// - intended run notation is "java Chat.Chat" (contains main() method of this file).
package Chat;



// Imports components required for Frame(), Runnable(), and various action listeners. 
// ...lang.Thread() is also imported,
// ...image.BufferedImage() also is imported for use in Ball().
import java.awt.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.Thread;
import java.util.Vector;

import com.sun.corba.se.spi.activation.Server;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

/*
The Chat() Class:
	Description:
		- serves as a container class for main() (see block comment below),
	Extends:
		- N/A.
	Implements:
		- N/A.
	Preconditions:
		- shared with main() (see below),
	Postconditions:
		- Constructor returns an instance of GUIChat().
*/
public class Chat {
	
	
	/*
	The main() method:
		Description: 
			- Opens a new instance of GUIChat(). 
		Preconditions:
			N/A: method ignores values in <args>[].
		Postconditions:
			- Creates a new instance of GUIChat, <gui>,
				+ from here, implemented methods loop until the program is exited.
	*/
	public static void main(String[] args) {
		GUIChat gui;
		if(args.length != 0)
		{
			if(args[0] != null)
			{
				gui = new GUIChat(args[0]);
			}
			else gui = new GUIChat("");
		}
		else gui = new GUIChat("");
		
		return;
	}
}



/*
The GUIChat() Class:
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
		- Runnable()           - for the use of Thread() throughout instances of GUIChat(),
		* - this class also hosts listeners from Ball(). See that class's header comment for more informatjon.
	Preconditions:
		- N/A.
	Postconditions:
		- Constructor returns an instance of GUIChat(),
			+ internal methods (makeSheet(), initComponents(), sizeScreen(), start()) are also ran.
				- start() begins an internal loop, which can only be terminated by user input (clicking on "Quit" or [x]).
*/
class GUIChat implements ActionListener, WindowListener, ComponentListener, AdjustmentListener, ItemListener, Runnable
{
	// Defines <serialVersionUID>, a universal identifier for this frame class's
	// instances.
	// - this variable is FINAL, and cannot be changed.
	static final long serialVersionUID = 10L;
	
	private final int DEFAULT_PORT = 44004;
	
	// Defines two Panel() objects, which serve as sub-containers for components under Frame().
	private Panel control_panel; // - holds control components,
	private Panel ball_panel;    // - holds Ball().
	
	private BufferedReader reader;
	
	private PrintWriter writer;
	
	private ServerSocket server;
	private Socket client;
	
	// Defines mutable versions of the frame <width>, <height>, and <center>.
	private int winWidth = 640;
	private int winHeight = 500;
	private int oldWinWidth;
	private int oldWinHeight;
	
	private int port = DEFAULT_PORT;
	private int timeout = 1000;
	
	// Defines the main Frame() object.
	private Frame ChatFrame;
	
	// Defines two control booleans, which are used as logic/loop controls later in the program.
	private boolean isRunning = true; // controls whether or not GUIChat()'s thread iterates,
	private boolean isServer  = false;
	private boolean isClient  = false;
	private boolean auto_flush = false;
	
	private String host = null;

	// Defines <thread>, which is used to run continuous code after this class is instantiated. 
	private Thread thread;

	// Defines an instance of GridBagConstraints(), which is used in tandem with <control_panel>.
	GridBagConstraints gbc;

	
	private TextArea chatArea;
	
	private TextField chatField;
	
	private Button chatSend;
	
	
	private TextField hostField;
	private Label hostLabel;

	private Button hostChange;
	private Button hostStart;
	
	
	private TextField portField;
	private Label portLabel;

	private Button portChange;
	private Button portStart;


	private Button disconnect;
	
	private TextArea statusArea;

	/*
	The GUIChat() constructor:
		Description: 
			- calls several subfunctions to initalize a GUIChat() object. 
		Preconditions:
			N/A.
		Postconditions:
			- Returns an initalized instance of GUIChat(), which will be running it's main loop (see start() and run() for details).
	*/
	public GUIChat(String connection)
	{
		// Instantiates the main frame object.
		ChatFrame = new Frame();
		
		// Sets the Frame()'s layout to null.
		ChatFrame.setLayout(new BorderLayout());
		
		// Makes the Frame() visible.
		ChatFrame.setVisible(true);
		
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
			- Mutates some mutable values inside of GUIChat(), mostly through the use of Ball()*
			* other components can be dynamically resized on their own, due to utilizing layout managers.
	*/
	private void makeSheet()
	{

		ChatFrame.setSize(winWidth, winHeight);
		ChatFrame.validate();

		winWidth = oldWinWidth;
		winHeight = oldWinHeight;

		
		// Returns, ending the function.
		return;
	}



	/*
	The initComponents() method:
		Description: 
			- Instantiates all component objects used by GUIChat(),
				+ also adds all components to their relevant listeners. 
		Preconditions:
			- Must be called after makeSheet().
		Postconditions:
			- Populates all object-associated variables with their proper values, and adds them to implemented listeners of GUIChat().
	*/
	private void initComponents() throws Exception, IOException {
		
		// Initalizes <gbl> and <gbc>, which are used together to constrain components within GUIChat().
		GridBagLayout gbl = new GridBagLayout();
		gbc               = new GridBagConstraints();


		// Initalizes the col/row/width/height dimensions used for <gbl>,
		// - since a set of GridBagConstraints are used alongside these values, they all get a weight of 1.
		double colweight[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		double rowweight[] = { 1, 1, 1, 1};
		int width[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		int height[] = { 1, 1, 1, 1};

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
		

		chatField = new TextField();
		chatSend  = new Button("Send");
		addToControlPanel(chatSend, 20,1,1,1);
		addToControlPanel(chatField, 0,1,20,1);	
		

		hostLabel   = new Label("Host");
		hostLabel.setBackground(Color.lightGray);
		hostField   = new TextField();
		hostChange  = new Button("Change Host");
		hostStart   = new Button("Start Server");
		addToControlPanel(hostLabel,   0,2,1,1);
		addToControlPanel(hostStart,  20,2,1,1);
		addToControlPanel(hostChange, 19,2,1,1);
		addToControlPanel(hostField,   1,2,18,1);

	
		portLabel   = new Label("Port");
		portLabel.setBackground(Color.lightGray);
		portField   = new TextField();
		portChange  = new Button("Change Port");
		portStart   = new Button("Connect");
		addToControlPanel(portLabel,   0,3,1,1);
		addToControlPanel(portStart,  20,3,1,1);
		addToControlPanel(portChange, 19,3,1,1);
		addToControlPanel(portField,   1,3,18,1);

		disconnect  = new Button("Disconnect");
		addToControlPanel(disconnect, 20,4,1,1);
		
		statusArea = new TextArea();
		statusArea.setEditable(false);
		addToControlPanel(statusArea, 0,5,0,1);
		
		// Adds listeners (and either Panel()) to Frame().
		ChatFrame.addComponentListener(this);
		ChatFrame.addWindowListener(this);
		ChatFrame.setPreferredSize(new Dimension(winWidth, winHeight));
		ChatFrame.setMinimumSize(ChatFrame.getPreferredSize());

		ChatFrame.add(control_panel, BorderLayout.SOUTH);

		chatArea = new TextArea();
		chatArea.setEditable(false);
		ChatFrame.add(chatArea, BorderLayout.CENTER);

		// Sets either Panel()'s background color to the relevant value.
		control_panel.setBackground(Color.lightGray);

		// Makes both Panel() instances visible.
		control_panel.setVisible(true);
		ball_panel.setVisible(true);


		// Calls the inherited function validate(), which reduces the available space within Frame().
		ChatFrame.validate();
		
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
	
	
	private void displayMessage(String msg)
	{
		if (isServer)
		{
			statusArea.append("Server: ");
		}
		else if (isClient)
		{
			statusArea.append("Client: ");
		}
		statusArea.append(msg + "\n");
		chatArea.requestFocus();
	}
	
	
	private void close()
	{
		// Still need to reset the buttons and textfield
		
		try
		{
			if (server != null)
			{
				if (writer != null)
				{
					writer.print("");
					writer.close();
				}
				if (reader != null)
				{
					reader.close();
				}
				server.close();
				server = null;
			}
		}
		catch (IOException e) {}
		
		try
		{
			if (client != null)
			{
				if (writer != null)
				{
					writer.print("");
					writer.close();
				}
				if (reader != null)
				{
					reader.close();
				}
				client.close();
				client = null;
			}
		}
		catch (IOException e) {}
		
		if (thread != null)
		{
			thread.setPriority(Thread.MIN_PRIORITY);
			thread = null;
		}
		
		isClient = false;
		isServer = false;
	}
	
	

	/*
	The stop() method:
		Description: 
			- Serves as a destructor for GUIChat(), and a terminator for it's internal loop (see run()).
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- Frees memory taken up by GUIChat()'s member variables and listeners, before returning.
	*/
	public void stop() {
		
		// Terminates the active <thread>'s loop (see run() for details).
		isRunning = false;

		// Finally, removes any listeners still attached to the Frame().
		ChatFrame.removeComponentListener(this);
		ChatFrame.removeWindowListener(this);
		
		// Calls cleanup functions, before ending the function.
		ChatFrame.dispose();
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
		
		thread.setPriority(Thread.MAX_PRIORITY);
		
		// Iterates so long as <isRunning> is true.
		while (isRunning) {
			// Lets the thread sleep for one tick, allowing for interupts.
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Updates the game's elements whenever <isPaused> is false.
			while (isServer)
			{
				
			}
			

			// Updates the game's elements whenever <isPaused> is false.
			while (isClient)
			{
				
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
 }

	/*
	The adjustmentValueChanged() method:
		Description: 
			- Handles changes to either ScrollBar() instance within the Frame().
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- Alters relevant values within GUIChat(), or, Ball() (depending on the bar that was changed).
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
		
		if (source == chatField)
		{
			String data = chatField.getText();
			chatArea.append("out: " + data + "\n");
			writer.println(data);
			chatField.setText("");
		}
		else if (source == hostStart)
		{
			try
			{
				hostStart.setEnabled(false);
				if (server != null)
				{
					server.close();
					server = null;
				}
				displayMessage("The server was closed");
				server = new ServerSocket(port);
				displayMessage("Opened server through port: " + port);
				server.setSoTimeout(10 * timeout);
				if (client != null)
				{
					client.close();
					client = null;
				}
				try
				{
					displayMessage("Listening for a client");
					client = server.accept();
					ChatFrame.setTitle("Server");
					displayMessage("connection from: " + client.getInetAddress());
					try
					{
						reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
						writer = new PrintWriter(client.getOutputStream(), auto_flush);
						isRunning = true;
						start();
					}
					catch (IOException er)
					{
						displayMessage("Error while instantiating a reader/writer");
						close();
					}
				}
				catch (SocketTimeoutException er)
				{
					displayMessage("The server timed out");
					close();
				}
			}
			catch (IOException er)
			{
				displayMessage("Error while closing server");
				close();
			}
		}
		else if (source == portStart)
		{
			try
			{
				if (client != null)
				{
					client.close();
					client = null;
					displayMessage("Disconnected");
				}
				client = new Socket();
				client.setSoTimeout(timeout);
				try
				{
					displayMessage("Trying to connect");
					client.connect(new InetSocketAddress(host, port));
					ChatFrame.setTitle("Client");
					displayMessage("A connection has been made to " + host + " through port " + port);
					try
					{
						reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
						writer = new PrintWriter(client.getOutputStream(), auto_flush);
						isClient = true;
						isRunning = true;
						start();
					}
					catch (IOException er)
					{
						displayMessage("Error while instantiating a reader/writer");
						close();
					}
				}
				catch (SocketTimeoutException er)
				{
					displayMessage("The connection timed out");
					close();
				}
				
			}
			catch (IOException er)
			{
				displayMessage("Error while closing client");
				close();
			}
		}
		else if (source == disconnect)
		{
			displayMessage("Closing connection");
			writer.println("");
			thread.interrupt();
			close();
		}
		else if (source == hostField || source == hostChange)
		{
			if (hostField.getText() != null)
			{
				host = hostField.getText();
				portStart.setEnabled(true);
			}
		}
		else if (source == portField || source == portChange)
		{
			if (portField.getText() != null)
			{
				try
				{
					port = Integer.valueOf(portField.getText());
					if (host != null)
					{
						portStart.setEnabled(true);
					}
				}
				catch (NumberFormatException er)
				{
					displayMessage("Error the port must be an integer!");
				}
			}
		}
		
		chatField.requestFocus();
		
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
		winWidth = ChatFrame.getWidth();
		winHeight = ChatFrame.getHeight();
		makeSheet();
		
		// Returns, ending the function.
		return;
	}



	/*
	The windowClosing() method:
		Description: 
			- Handles the first steps of an instance of GUIChat() being terminated (from the user clicking on the Frame()'s [x]).
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
	
	/*
	
	*/
	@Override
	public void windowOpened(WindowEvent e)
	{
		chatField.requestFocus();
		
		// Returns, ending the function.
		return;
	}
	
	/*
	
	*/
	@Override
	public void windowClosed(WindowEvent e)
	{
		chatField.requestFocus();
		
		// Returns, ending the function.
		return;
	}
	
	/*
	
	*/
	@Override
	public void windowIconified(WindowEvent e)
	{
		chatField.requestFocus();
		
		// Returns, ending the function.
		return;
	}
	
	/*
	
	*/
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		chatField.requestFocus();
		
		// Returns, ending the function.
		return;
	}
	
	/*
	
	*/
	@Override
	public void windowActivated(WindowEvent e)
	{
		chatField.requestFocus();
		
		// Returns, ending the function.
		return;
	}
	
	/*
	
	*/
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		chatField.requestFocus();
		
		// Returns, ending the function.
		return;
	}

	// Below are overwritten (but unimplemented) methods of this class's implemented listeners.
	public void componentMoved(ComponentEvent e)  {return;}
	public void componentShown(ComponentEvent e)  {return;}
	public void componentHidden(ComponentEvent e) {return;}
}