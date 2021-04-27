/*
.Program7, Network Chat Program               (Chat.java).
.Created By:                                             .
- Nathaniel Dehart   (DEH5850@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu),                 .
- Daniel Hentosz,    (HEN3883@calu.edu).                 .
.Last Revised: April 27th, 2021.              (4/27/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
A bundled server/client package that:
 - can function as the server port and client port for a chat client,
 - can send and recieve messages (as implied above),
 - allows for dynamic disconnection and reconnection,
 - informs the user of the current status of the program with a status area.

The user is able to:
 - change the host of the current chat application instance, (server)
 - change the port of the current chat application instance, (client port)
 - connect to another instance of the chat application,      (failure and success accounted for),
 - disconnect from another instance of the chat application, (only available when connected already),
 - send messages to another instance of the chat application,
 - recieve messages from another instance of the chat application.
 
for more implementation details, see GUIChat()'s class header.
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
import java.io.*;
import java.lang.Thread;
import java.net.*;

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
		- serves as a container window (Frame()) for several <...awt...> GUI components),
			+ Message Area,        - TextArea    (Holds current sent and recieved messages),
			
			+ Chat Field,          - TextField   (Sends messages whenever GUIChat() is currently connected),
			+ Send,                - Button      (Acts like pressing ENTER/RETURN while selecting Chat Field),
			
			+ Host Field,          - TextField   (Sets the current host address for connecting to (or hosting) a server),
			+ Change Host,         - Button      (Acts like pressing ENTER/RETURN while selecting Host Field),
			* Start Server,        - Button      (Starts listening for a client connection (uses HOST)),
			
			+ Port Field,          - TextField   (Sets the current port # for sending a connection request),
			+ Change Port,         - Button      (Acts like pressing ENTER/RETURN while selecting Port Field),
			** Connect,            - Button      (Sends a request to any servers listening (uses HOST and PORT),
			
			+ Disconnect           - Button      (Disconnects any active ports. Enabled when a connection is active),
			
			+ Status Area,        - TextArea    (Holds currently sent status messages).
			* Disables after being pressed once, enables after disconnecting.
			**Enables after activating Host Field or Change Host, disables after disconnecting.

	Implements:
		- WindowListener()     - for window related events (open, close, etc...),
		- ComponentListener()  - for resizing the screen (see componentResized()),
		- ActionListener()     - for interactions regarding TextField() and Button() objects,
		- Runnable()           - for the use of Thread() throughout instances of GUIChat().
	Preconditions:
		- N/A.
	Postconditions:
		- Constructor returns an instance of GUIChat(),
			+ internal methods (makeSheet(), initComponents(), sizeScreen(), start()) are also ran.
				- after the program has been instantiated, it can only be terminated by user input (clicking on [x]).
*/
class GUIChat implements ActionListener, WindowListener, ComponentListener, Runnable
{
	// Defines <serialVersionUID>, a universal identifier for this frame class's
	// instances.
	// - this variable is FINAL, and cannot be changed.
	static final long serialVersionUID = 10L;
	
	// Defines a constant which equals the default port # used for connections.
	private final int DEFAULT_PORT = 44004;
	
	// Defines a Panel() object, which olds all of the User Input elements used by an instance of GUIChat().
	private Panel control_panel; // - holds control components,

	
	// Defines <reader>, which is used for recieving messages from an active connection.
	private BufferedReader reader;
	
	
	// Defines <writer>, which is used for sending messages to an active connection.
	private PrintWriter writer;
	
	// Defines <server>, which serves as the active listening connection of a GUIChat(),
	// - active when in 'server/hosting' mode.
	private ServerSocket server;
	
	// Defines <server>, which serves as the active sending connection of a GUIChat(),
	// - active when in 'client/port' mode.
	private Socket client;
	
	// Defines the mutable height and width of the frame().
	private int winWidth = 640;
	private int winHeight = 500;
	
	// Defines dummy integers, which hold old values of the integers defined just above.
	private int oldWinWidth;
	private int oldWinHeight;
	
	// Defines an integer which holds the current port # (by default, DEFAULT_PORT).
	private int port = DEFAULT_PORT;
	
	// Defines the static timeout value (2 seconds),
	// - the raw value is used when connecting as a port,
	// - the value times ten is used when listening as a server.
	private int timeout = 2000;
	
	// Defines the main Frame() object.
	private Frame ChatFrame;
	
	// Defines two control booleans, which are used as logic/loop controls later in the program.
	private boolean isRunning = true;  // controls whether or not GUIChat()'s thread iterates,
	private boolean isServer  = false; // labels an instance of GUIChat() as a server,
	private boolean isClient  = false; // labels an instance of GUIChat() as a client.
	
	// Used for disabling a builtin feature of datastream objects (see actionPerformed()).
	private boolean auto_flush = false;
	
	// Holds the current host string used by both server and client modes.
	private String host = null;

	// Defines <thread>, which is used to run continuous code after this class is instantiated*
    // *
	private Thread thread;

	// Defines an instance of GridBagConstraints(), which is used in tandem with <control_panel>.
	GridBagConstraints gbc;

	// Defines various GUI elements <...awt...>,
	// - for implementation details, see the class header above.
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
			String <str_timeout> - contains a string (which can be convered into an integer). (overwrites default timeout value (miliseconds)).
		Postconditions:
			- Returns an initalized instance of GUIChat(), which will be running it's main loop (see start() and run() for details).
	*/
	public GUIChat(String str_timeout)
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
			initComponents(str_timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Calls makeSheet() to determine some component/screen sizes.
		makeSheet();
	}


	/*
	The start() method:
		Description: 
			- Starts the current thread, checking to see if it exists in the process (this prevents the thread from somehow being overwritten, if this method is ran twice without deletion.
		Preconditions:
			N/A.
		Postconditions:
			- Starts the main data-transfer loop (see run() for details).
	*/
	private void start() {
		
		// Sets various default button states (for when a thread is started).
		disconnect.setEnabled(true);
		chatField.setEnabled(true);
		chatSend.setEnabled(true);
		hostStart.setEnabled(true);
		portStart.setEnabled(true);
		
		// Starts the main data-transfer loop.
		isRunning = true;
		
		// Creates a new thread (if one has not been made already).
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
			- Resizes the current window, and stores old Height/Width values.
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
	private void initComponents(String str_timeout) throws Exception, IOException {
		

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
		
		// Sets some constraints which are shared by all elements of <control_panel>.
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		
		
		
		// Assigns names, colors, and positions to various GUI elements.
		// Due to the repetetive nature of these codeblocks, they will mostly be left uncommented.
		// - all UI elements are added to the control panel, save for the message field. 
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
		
		
		// Adds listeners (and the control Panel()) to Frame().
		ChatFrame.addComponentListener(this);
		ChatFrame.addWindowListener(this);
		ChatFrame.setPreferredSize(new Dimension(winWidth, winHeight));
		ChatFrame.setMinimumSize(ChatFrame.getPreferredSize());
		ChatFrame.add(control_panel, BorderLayout.SOUTH);

		// Adds the <chatArea> (messages) to the panel()'s center.
		chatArea = new TextArea();
		chatArea.setEditable(false);
		ChatFrame.add(chatArea, BorderLayout.CENTER);

		// Sets either Panel()'s background color to the relevant value.
		control_panel.setBackground(Color.lightGray);

		// Makes both Panel() instances visible.
		control_panel.setVisible(true);
		
		
		// Adds relevant action listeners to objects defined above.
		chatField.addActionListener(this);
		hostField.addActionListener(this);
		chatSend.addActionListener(this);
		hostChange.addActionListener(this);
		hostStart.addActionListener(this);
		portField.addActionListener(this);
		portChange.addActionListener(this);
		portStart.addActionListener(this);
		disconnect.addActionListener(this);
		
		// Sets the program's initial UI state.
		setInitialButtonStates();

		// Calls the inherited function validate(), which reduces the available space within Frame().
		ChatFrame.validate();
		
		// Displays a boot message, and changes the frame()'s title. 
		displayMessage("[STA] Booted without errors.");
		
		// Checks to see if the user entered a unique parameter for the timeout value.
		if(str_timeout != ""){
			try{
				// tries to typecast (and store) that value, if so.
				timeout = Integer.valueOf(str_timeout);
			}
			// If unable to be typecasted, the user is informed that their input was ignored.
			catch (NumberFormatException er)
			{
				displayMessage("[STA] Timeout value ignored; \"" + str_timeout + "\" is not a valid value.");
			}
		}
		// Displays the current timeout values in miliseconds and whole seconds.
		displayMessage("[STA] Current client timeout value is: " + timeout + " milliseconds (" + (float)timeout/1000 + " seconds).");
		displayMessage("[STA] Current server timeout value is: " + timeout * 10 + " milliseconds ("  + (float)timeout * 10 /1000 + " seconds).");
		
		// Changes GUIChat()'s Frame() to it's default.
		ChatFrame.setTitle("Network Chat: Currently Idling...");
		
		
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
	The displayMessage() method:
		Description: 
			- A macro function that pushes a message to <statusArea>, which is visible to the end user.
		Preconditions:
			String <msg> - the string to be displayed. 
		Postconditions:
			- Appends a string (and a new line) to <statusArea>.
	*/
	private void displayMessage(String msg)
	{
		
		// Appends signifiers for the status log, letting the user know what mode the chat was in.
		if (isServer)
		{
		statusArea.append("[SERVER] ");
		}
		else if (isClient)
		{
			statusArea.append("[CLIENT] ");
		}
		// Appends the actual message, alongside a newline.
		statusArea.append(msg + "\n");
		
		// Returns, ending the function.
		return;
	}
	
	
	/*
	The close() method:
		Description: 
			- Disposes of the program's main thread and Sockets, before returning control to the program's event handler.
		Preconditions:
			N/A (accounts for errors).
		Postconditions:
			- Disposes of the program's main thread and Sockets, before returning control to the program's event handler. 
	*/
	private void close()
	{
		// Changes GUIChat()'s Frame's title, to reflect a lack of a special status.
		ChatFrame.setTitle("Network Chat: Currently Idling...");
		
		// Resets the UI before continuing.
		setInitialButtonStates();
		
		
		// Disposes of IO elements pertaining to a server socket,
		// - this is wrapped in a try statement to prevent potential (but unlikely) IO errors.
		try
		{
			
			if (server != null)
			{
				if (writer != null)
				{
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
		
		
		// Disposes of IO elements pertaining to a client socket,
		// - this is wrapped in a try statement to prevent potential (but unlikely) IO errors.
		try
		{
			if (client != null)
			{
				if (writer != null)
				{
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
		
		// Deletes the main thread.
		if (thread != null)
		{
			thread.setPriority(Thread.MIN_PRIORITY);
			thread = null;
		}
		
		// Cleans up various metadata booleans, which dictate the mode of GUIChat().
		isClient = false;
		isServer = false;
		isRunning = false;
		
		// Returns, ending the function.
		return;
	}
	
	

	/*
	The stop() method:
		Description: 
			- Serves as a destructor for GUIChat().
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- Frees memory taken up by GUIChat()'s member variables and listeners, before returning.
	*/
	public void stop() {
		
		// Terminates the active <thread>'s loop (see run() for details).
		isRunning = false;


		// Removes the UI's many action listeners. 
		chatField.removeActionListener(this);
		hostField.removeActionListener(this);
		chatSend.removeActionListener(this);
		hostChange.removeActionListener(this);
		hostStart.removeActionListener(this);
		portField.removeActionListener(this);
		portChange.removeActionListener(this);
		portStart.removeActionListener(this);
		disconnect.removeActionListener(this);


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
	The setInitalButtonStates() method:
		Description: 
			- A macro that resets the GUI of GUIChat().
		Preconditions:
			- the initComponents() method must have had been ran already. 
		Postconditions:
			- Toggles various UI elements on and off to achieve the default states described in the class header.
	*/
	private void setInitialButtonStates()
	{
		disconnect.setEnabled(false);
		portStart.setEnabled(false);
		portChange.setEnabled(true);
		hostChange.setEnabled(true);
		hostStart.setEnabled(true);
		chatSend.setEnabled(false);
		chatField.setEnabled(false);
		
		// returns, ending the function. 
		return;
	}
	
	

	/*
	The run() method:
		Description: 
			- Iterates so long as <isRunning> is true, and waits for data to be recieved by GUIChat()'s currently paired socket.
		Preconditions:
			- the start() method must have had been ran already. 
		Postconditions:
			- This thread is disposed by close() (see close()'s method header for details).
	*/
	@Override
	public void run()
	{
		
		// Changes this thread's priority, 
		// - this is done so that there is minimal latency between messages being recieved.
		thread.setPriority(Thread.MAX_PRIORITY);
		
		// Defines a temporary value, <data>,
		// - holds information fed by <reader>. 
		String data = new String();


		// Runs so long as the connection between GUIChat()'s and it's paired Socket is maintained. 
		while (isRunning)
		{
			
			// The main data-recieving section,
			// - this is wrapped in a try statement to prevent potential (but unlikely) IO errors.
			try 
			{
				
				// Reads data from <reader>
				data = reader.readLine();
				
				// Checks to see if the datastream has been broken.
				if (data == null || data.equals(""))
				{
					// If so, the thread enters a termination state (connection was lost),
					// - the user is informed of this. 
					displayMessage("[STA] Socket connection was lost.");
					close();
				}
				
				// Otherwise, the information read from <reader> must be a message,
				// - said messages are appended directly to <chatArea>, for the user to see.
				else
				{
					chatArea.append("in: " + data + "\n");
					data = new String();
				}
			}
			catch (IOException er) {}
		} 
		
		// Returns, ending the function.
		return;
	}
	


	/*
	The actionPerformed() method:
		Description: 
			- Handles interaction with any element of GUIChat()'s namesake, it's GUI.
		Preconditions:
			- the initComponents() method must have had been ran already.
		Postconditions:
			- Bestows functionality to each GUI component (for implementation details, see the class header).
	*/
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Stores the source of the event via getSource().
		Object source = e.getSource();
		
		
		// First, checks to see if the source was <chatField> or <chatSend>
		if (source == chatField || source == chatSend)
		{
			// If so, the text currently entered into <chatField> is retrieved.
			String data = chatField.getText();
			
			// That text is then compared against the default value, ""
			if(data != "")
			{
				// Assuming something different was entered, the text is sent via <writer>,
				// - the text is also displayed to <chatArea>, for the user to see.
				chatArea.append("out: " + data + "\n");
				writer.println(data);
				writer.flush();
				
				// <chatField> is then reset.
				chatField.setText("");
				
				
				// Focuses on the chat field, if the user was typing in it already.
				if (source == chatField)
				{
					chatField.requestFocus();
				}
			};
		}
		
		// Otherwise, checks to see if the user is trying to start a server.
		else if (source == hostStart)
		{
			// 
			try
			{
				
				// Disables both <...Start> buttons, as a precaution.
				hostStart.setEnabled(false);
				portStart.setEnabled(false);

		
				// Checks to see if a server instance still exists,
				// - ideally, close will have had cleaned this up already.
				if (server != null)
				{
					// If one does exist, the current server is closed (and the user is informed).
					server.close();
					server = null;
					displayMessage("[STA] Current server socket closed.");
				}
	
				// Checks to see if a client instance still exists,
				// - ideally, close will have had cleaned this up already.
				if (client != null)
				{
					client.close();
					client = null;
					displayMessage("[STA] Current client socket closed.");
				}

				// Creates a new ServerSocket, using the port value currently entered by the user.
				server = new ServerSocket(port);
				displayMessage("[STA] New server socket opened through port: " + port + ".");
				
				// Sets a timeout timer for the server's listening (started through ...accept(), below).
				server.setSoTimeout(10 * timeout);
				

				try
				{
					
					// Listens for a connection (and informs the user that the program is doing so,
					// - if this process takes too long, this try portion of the method will be interrupted.
					displayMessage("[STA] Listening for a client...");
					client = server.accept();
					
					// If a connection is made, the user is told where they connected from,
					// - also, the GUIChat() frame update's it's title, to reflect the program's mode.
					ChatFrame.setTitle("Network Chat: Running in Server Mode...");
					displayMessage("[SERVER] [STA] Recieved connection from: " + client.getInetAddress() + ".");
					
					// Tries to create IO objects (<reader> and <writer>),
					
					try
					{
						reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
						writer = new PrintWriter(client.getOutputStream(), auto_flush);
						
						// Also sets a system boolean, and runs start(), to begin the main thread.
						isServer = true;
						start();
					}
					catch (IOException er)
					{
						displayMessage("[ERR] Failed to instance I/O objects.");
						close();
					}
				}
				catch (SocketTimeoutException er)
				{
					displayMessage("[ERR] Server socket's connection timed out.");
					close();
				}
			}
			catch (IOException er)
			{
				displayMessage("[ERR] Failed to close old sockets.");
				close();
			}
		}
		// Checks if the client is trying to connect to a server
		else if (source == portStart)
		{
			try
			{
				portStart.setEnabled(false);
				hostStart.setEnabled(false);
				
				
				// Checks to see if a client instance still exists,
				// - ideally, close will have had cleaned this up already.
				if (client != null)
				{
					client.close();
					client = null;
					displayMessage("[STA] Current client socket closed.");
				}
				
				// Creates a new client socket.
				client = new Socket();
				client.setSoTimeout(timeout);
				try
				{
					displayMessage("[STA] Sending a connection request on port " + port + ".");
					client.connect(new InetSocketAddress(host, port));
					ChatFrame.setTitle("Network Chat: Running in Client Mode...");
					displayMessage("[CLIENT] [STA] A connection has been made to " + host + " through port " + port);
					try
					{
						reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
						writer = new PrintWriter(client.getOutputStream(), auto_flush);
						isClient = true;
						start();
					}
					catch (IOException er)
					{
						displayMessage("[ERR] Failed to instance I/O objects.");
						close();
					}
				}
				catch (SocketTimeoutException er)
				{
					displayMessage("[ERR] Client's socket connection timed out.");
					close();
				}
				catch (IOException er)
				{
					displayMessage("[ERR] Failed to send connection request.");
					close();
				}
				
			}
			catch (IOException er)
			{
				displayMessage("[ERR] Failed to close old sockets.");
				close();
			}
		}
		else if (source == disconnect)
		{
			// Informs the user that the program's current connections are being closed.
			displayMessage("[STA] Closing current connections.");
			
			// Sends a formal disconnect value.
			writer.println("");
			
			// Interupts the current data-handling thread.
			thread.interrupt();
			
			// Calls close() to handle the actual closing.
			close();
		}
		else if (source == hostField || source == hostChange)
		{
			if (hostField.getText() != null && hostField.getText() != "")
			{
				host = hostField.getText();
				portStart.setEnabled(true);
				displayMessage("[STA] Host value successfully changed to: " + host);
			}
			else
				portStart.setEnabled(false);
		}
		else if (source == portField || source == portChange)
		{
			if (portField.getText() != null && portField.getText() != "")
			{
				try
				{
					int temp = Integer.valueOf(portField.getText());
					
					if(!(temp >= 65535) && temp >= 1)
					{
						port = Integer.valueOf(portField.getText());
						if (host != null)
						{
							portStart.setEnabled(true);
						}
						displayMessage("[ERR] Invalid port value (outside of the valid range: 1 to 65535).");
					}
				}
				catch (NumberFormatException er)
				{
				displayMessage("[ERR] Invalid port value (must be an integer, default is: 44004).");
				}
			}
			else
				port = DEFAULT_PORT;
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