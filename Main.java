/*
.Program3, G.U.I. File Copy                   (Main.java).
.Created By:                                             .
- Daniel Hentosz,    (HEN3883@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu).                 .
.Last Revised: March 1st, 2021.                (3/1/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
	Makes use of java's <awt> library to create a GUI,
		- this GUI controls a file-copying program.
		
	The user can select:
		- a source file,
		- a target directory,
		- the source file copy's name.
	
	Whenever "OK" is pressed, and the following values are entered,
	- a copy of the source file is created at the target directory,
	  + this process can be then repeated with new value(s).
*/

// Imports Frame(), for use as a superclass (for GUIFile()).
// - serves as a platform for all UI Elements used in this program.
import java.awt.Frame;

// Imports ActionListener(), for use as an implementation within GUIFile().
import java.awt.event.ActionListener;


// Imports ActionEvent(),
// - serves as a datatype used by ActionListener().
import java.awt.event.ActionEvent;

// Imports WindowListener(), for use as an implementation within GUIFile().
import java.awt.event.WindowListener;

// Imports WindowEvent(),
// - serves as a datatype used by WindowListener().
import java.awt.event.WindowEvent;




// Imports the entierety of awt, for use in tandem with the imports above.
// - this is done to simplify other, more trivial imports (such as exception messages/extra classes).
import java.awt.*;


// Imports File(), an object class used for reading/writing to files within this program.
import java.io.File;

// Imports InputStreamReader(), which is used to instantiate BufferedReader (see below).
import java.io.InputStreamReader;

// Imports FileReader(), an object class used for reading/writing to files within this program.
import java.io.FileReader;

// Imports FileWriter(), an object class used for writing to files within this program.
import java.io.FileWriter;

// Imports BufferedReader(), the interface that this program uses to parse input from InputStreamReader().
import java.io.BufferedReader;

// Imports PrintWriter(), the interface that this program uses to output data into a file.
import java.io.PrintWriter;

// Imports IOException(), for use with InputStreamReader and BufferedReader.
// - without use of this class (throw or catch), java will refuse to initialize BufferedReader inside of a method.
import java.io.IOException;

// Imports IOException(), for use with InputStreamReader and BufferedReader.
// - without use of this class (throw or catch), java will refuse to initialize BufferedReader inside of a method.
import java.io.FileNotFoundException;





/*
The Main() Class:
	Description:
		- serves as a container class for main() (see block comment below),
	Preconditions:
		- shared with main() (see below),
	Postconditions:
		- Constructor returns an instance of Program2().
*/
class Main
{
	
	/*
	The main() method:
		Description: 
			- Opens a new instance of GUIFile(), and accounts for optional parameters. 
		Preconditions:
			>>> This method's conditions are optional. 
			- args[0]: - must be a valid directory (used as starting point),
			- additional args are ignored.
		Postconditions:
			- creates output file(s), based on user input.
	*/
	public static void main(String[] args)
	{
		boolean args_valid = true;
		GUIFile gui = null;
		
		// Checks to see if the user entered any arguement(s),
		// if so, they are validated below.
		if (args.length != 0)
		{
			// if so, creates the file <source>.
			File source = IOFile.OpenFile(args[0]);
			// Checks to see if <args[0]> is a directory, and whether or not it exists.
			if (IOFile.FileIsDirectory(source) && IOFile.FileExists(source) && args[0].indexOf("\\") != -1)
			{
				// Launches GUIFile() at the given arguement, if so.
				gui = new GUIFile(source.getAbsolutePath());
			}
			
			// Otherwise, the user is informed that the directory given wasn't valid.
			// - after this, <args_valid> is set to false.
			else
			{
				System.out.print(
				"| '" + args[0] + "' is an invalid directory!\n"
				);
				System.out.flush();
				args_valid = false;
			}
		}
		else
		{
			args_valid = false;

		}
		
		// If the arguements above were invalid, GUIFile() launches at the current program directory.
		if(!args_valid)
		{
			File dir        = IOFile.OpenFile(System.getProperty("user.dir"));
			String dir_path = dir.getAbsolutePath();
			gui = new GUIFile(dir_path);
			
			System.out.print(
			"| - using program directory as starting point...");
			System.out.flush();
		}
		
		return;
	}
}



/*
The GUIFile() Class:
	Description:
		- serves as a custom implementation,
	Preconditions:
		- shared with main() (see below),
	Postconditions:
		- Constructor returns an instance of Program2().
*/
class GUIFile extends Frame implements WindowListener, ActionListener {
	// Defines <serialVersionUID>, a universal identifier for this frame class's instances.
	// - this variable is FINAL, and cannot be changed.
	private static final long serialVersionUID = 1L;
	
	// Defines <lbl_1 ... lbl_5>, various text labels which identify sections of GUIFile's GUI.
	Label lbl_1 = new Label("Source:");
	Label lbl_2 = new Label();
	Label lbl_3 = new Label();
	Label lbl_4 = new Label("File Name:");
	Label lbl_5 = new Label();
	
	// Defines <list>, a UI container which contains buttons the user can interact with (see display() for details).
	List list = new List();
	
	// Defines <btn_target>, an instance of Button() which is used to assign a target director (once a source file is selected),
	// - see actionPerformed() for details.
	Button btn_target  = new Button("target");
	Button btn_confirm = new Button("OK");
	
	// Defines <txtbx_input>, an instance of TextField() which stores the desired file name (string),
	// - this value becomes the copied file's name (when a valid source and target are selected, and 'OK' is pressed).
	TextField txtbx_input = new TextField();
	
	// Defines <current_dir>, an instance of File() which points to the directory currently being accessed by GUIFile(),
	// - is initalized to a blank file, but is overwritten by this object's constructor (see GUIFile() below for details).
	File current_dir;
	
	// Defines various control booleans (<has_source>, <has_target>),
	// - each informs the program if a certain value(s) has been selected.
	boolean has_source  = false;
	boolean has_target  = false;
	
	// Defines <target_name>, an empty string which stores the value entered through <txtb_input>.
	String targetname = "";



	/*
	The GUIFile() method:
		Description: 
			- Constructs UI elements contained within GUIFile() alongside <current_dir>.
		Preconditions: 
			- <str> - must be a valid string value (used as a directory).
		Postconditions:
			- returns an instance of GUIFile(); calls GUIFile().display(<str>).
	*/
	GUIFile(String str) {
		// 
		current_dir = new File(str);

		// Our Layout manager.
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		// - each row and column has a width of 10.
		double colweight[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		double rowweight[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		int width[]        = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		int height[]       = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		
		// Assigns the templates defined above to <gbl>.
		gbl.rowHeights    = height;
		gbl.columnWidths  = width;
		gbl.columnWeights = colweight;
		gbl.rowWeights    = rowweight;
		
		// Packs the value(s), ensuring they cannot overlap.
		this.pack();
		
		// Sets an initial window size, before applying the layout above to GUIFile().
		this.setBounds(20, 20, 700, 350);
		this.setLayout(gbl);
		
		
		
		// Below are various constraints value(s) for UI objects.
		// - A summary of their post-conditions will be provided at each codeblock.
		
		
		
		// Constrains all list-objects to the upper-center portion of the screen.
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 10;
		gbc.weighty = 6;
		gbc.gridwidth = 10;
		gbc.gridheight = 6;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbl.setConstraints(list, gbc);
		this.add(list);
		
		
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
		this.add(lbl_1);
		
		
		// Constrains <lbl_2> (source file name) to the left side of the screen (with relevant offsets).
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(lbl_2, gbc);
		this.add(lbl_2);
		
		
		// Constrains <btn_target> ("Target") to the left side of the screen (underneath the previous two entries).
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(btn_target, gbc);
		this.add(btn_target);
		
		
		// Constrains <lbl_3> (target directory name) to the left side of the screen (with relevant offsets).
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.weighty = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(lbl_3, gbc);
		this.add(lbl_3);
		
		
		// Constrains <lbl_4> ("File Name") to the left side of the screen (below <btn_target>).
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbl.setConstraints(lbl_4, gbc);
		this.add(lbl_4);
		
		
		// Constrains <txtbx_input> (copied file name) to the left side of the screen (beside <lbl_4>).
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(txtbx_input, gbc);
		this.add(txtbx_input);

		
		// Constrains <btn_confirm> ("OK") to the left side of the screen (beside <txtbx_input>).
		gbc.gridx = 9;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbl.setConstraints(btn_confirm, gbc);
		this.add(btn_confirm);


		// Constrains <lbl_5> (confirmation/error messages) to the left side of the screen (underneath <txtbx_input>).
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 9;
		gbc.weightx = 9;
		gbl.setConstraints(lbl_5, gbc);
		this.add(lbl_5);

		
		
		// Appends any objects that must include action listener(s) to GUIFile().
		btn_target.addActionListener(this);
		btn_confirm.addActionListener(this);
		list.addActionListener(this);
		txtbx_input.addActionListener(this);
		
		
		// Disables UI elements which cannot be accessed without prerequisites (<btn_target>, <txtbx_input>).
		btn_target.setEnabled(false);
		txtbx_input.setEnabled(false);
		
		// Makes the window visible, and attaches a WindowListener() to GUIFile().
		this.setVisible(true);
		this.addWindowListener(this);
		
		// Calls the display() method, before ending this function.
		display(str);
		
		return;
	}



	/*
	The display() method:
		Description: 
			- Parses the current directory location of GUIFile, and constructs a button-list out of it,
			- includes exception handling for being at the root of a file, in an empty directory, etc.
		Preconditions: 
			- <str> - must be a valid string value (used as a directory).
		Postconditions:
			- flushes <list>, then repopulates it with new entries.
	*/
	void display(String str) {
		
		// Removes all entires from the current file list,
		// - this is done regardless of whether or not the current list object is empty.
		list.removeAll();
		
		// Creates the first list entry <"..">,
		// - only occurs if the current directory is not a root.
		if (current_dir.toPath().getNameCount() >= 1)
			list.add("..");
		else
			// - tells the user they are at the drive's root folder.
			lbl_5.setText("You're at the root.");
		
		// Changes GUIFile()'s title to reflect what directory it is in.
		this.setTitle(str);
			
		
		
		// Defines <names[]>, a string array of other file(s) currently in <current_dir>.
		String names[] = current_dir.list();
		
		// Iterates through each entry of <names>, assuming the array is not empty.
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				
				// Defines <dummy>, a temporary instance of File() used to make assumptions about an entry in <list>.
				File dummy = new File(current_dir.getAbsolutePath() + "\\" + names[i]);
				
				// Checks to see if the <dummy> file is a directory.
				if (IOFile.FileIsDirectory(dummy))
				{

					// Defines <dummy_names>, a temporary String array populated by <dummy>.
					String dummy_names[] = dummy.list();
					boolean status = false;
					
					// Further checks to see if the dummy file has populated subdirectories.
					if (dummy_names != null)
					{
						// Iterates through each entry of <dummy_names>, checking to see if -they- also are directories.
						for (int j = 0; j < dummy_names.length; j++) {
							// Defines <dummys_dummy>, a dummy file for the temporary variable <dummy>. 
							File dummys_dummy = new File(dummy.getAbsolutePath() + "\\" + dummy_names[j]);
							
							// Checks to see if <dummys_dummy> is a directory (and if one has been found inside this subdirectory).
							if (IOFile.FileIsDirectory(dummys_dummy) && !status) {
								
								// If so, a "+" is appeneded to the directory name.
								names[i] = names[i] + "+";
								
								// Updates status, so multiple plusses are not appended.
								status = true;
							}
						}
					}
				}
				// Adds a name from <names> to <list>, then iterates. 
				list.add(names[i]);
			}
		}
		return;
	}




	/*
	The actionPreformed() method:
		Description: 
			- Handles any and all user interaction with the GUIFile() object.
		Preconditions: 
			- <ae> - must be an instance of an ActionEvent().
		Postconditions:
			- changes aspects of GUIFile()'s internal variables, including the current directory and source/target files.
	*/
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		// Making an object of the action performed.
		Object source = ae.getSource();
		
		// The following codeblocks are an else-if chain,
		// - this chain handles input from only one GUIFile() object per event.
		
		
		// Checks for <btn_target>
		if (source == btn_target)
		{
			// If so, the target directory is set.
			// - internal booleans are also altered, if this is the first target directory.
			if(!has_target){
				txtbx_input.setEnabled(true);
				has_target = true;
			};
			targetname = current_dir.getAbsolutePath();
			lbl_3.setText(current_dir.getAbsolutePath());
		}
		
		// Checks for <btn_confirm>
		else if (source == btn_confirm) {
			// If so, tries to perform the Copy() method.
			copy();
		}
		
		
		// Checks for <list>
		else if (source == list) {
			
			// Clears the confirmation/error label.
			lbl_5.setText("");
			
			// Fetches the list item's String value.
			String name = list.getSelectedItem();
			
			// Compares <name> to "..", an equivalent to "cd ..".
			if (name.equals(".."))
			{
				// Defines <pos>, a temporary integer used to store the index of "\\".
				int pos = current_dir.getAbsolutePath().lastIndexOf("\\");
				
				// Checks to see if <pos> is not the last slash in the string's position,
				// - if <pos> was, that would mean it is the root of the directory.
				if(!(pos == current_dir.getAbsolutePath().indexOf("\\"))){
					// If not, the directory is changed, and display() is called.
					current_dir = new File(current_dir.getAbsolutePath().substring(0, pos));
					display(current_dir.getAbsolutePath());
				}
				
				// If <pos> is at the root, the root is re-assigned as the current directory, and display() is called.
				else
				{
					current_dir = new File(current_dir.getAbsolutePath().substring(0, pos + 1));
					display(current_dir.getAbsolutePath());
				}
			}
			
			// Otherwise, compares <name> against "+" (the symbol used for finding directories with subdirectories).
			else if (name.endsWith("+")) {
				// re-defines name to be a string with one less character(s).
				name = name.substring(0, name.length() - 1);
				
				// creates a temporary value <temp>, which appends the <current_dir> path to name.
				File temp = new File(current_dir.getAbsolutePath() + "\\" + name);
				current_dir = new File(current_dir.getAbsolutePath() + "\\" + name);
				display(current_dir.getAbsolutePath());
			}
			
			// Otherwise, checks to see if <name> is not associated with a file.
			else if (!name.contains(".")) {
				// creates a temporary value <temp>, which appends the <current_dir> path to name.
				File temp = new File(current_dir.getAbsolutePath() + "\\" + name);
				
				// Checks to be certain that <temp> belongs to a directory (some files lack extensions).
				if (temp.isDirectory()) {
					
					// if <temp> is a directory, display() is called after <current_dir> is altered.
					current_dir = new File(current_dir.getAbsolutePath() + "\\" + name);
					display(current_dir.getAbsolutePath());
				}
				
				// if the entry clicked -is- a file, code is used to assign that file as a new source.
				else 
				{
					lbl_2.setText(current_dir.getAbsolutePath() + "\\" + name);
					btn_target.setEnabled(true);
					if (txtbx_input.isEnabled())// Only set the text field when our target button is enabled.
						txtbx_input.setText(name);
				}
			}
			
			// Otherwise, assumes what the user is clicking is a file.
			else
			{
				lbl_2.setText(current_dir.getAbsolutePath() + "\\" + name);
				btn_target.setEnabled(true);
				if (txtbx_input.isEnabled())// Only set the text field when our target button is enabled.
					txtbx_input.setText(name);
			}
		} else if (source == txtbx_input) {
			// The user pressed enter when in the text field.
			copy();
		}
	}





	/*
	The copy() method:
		Description: 
			- Handles copying/exceptions related to copying file(s).
		Preconditions: 
			- N/A.
		Postconditions:
			- returns a status <lbl_5>, and (depending on internal GUIFile() values) copies a file to the target directory.
	*/
	void copy() {
		
		// Clears <lbl_5>, in case any text was previously entered into it.
		lbl_5.setText("");
		
		// Defines <dup>, a boolean control used for code below,
		// - used to see if there is a duplicate file.
		boolean dup = false;
		
		// Defines <good>, a boolean control used for code below,
		// Used to see if the file was copied correctly.
		boolean good = false;
		
		
		//Checks to see if each information field has data.
		if (!lbl_2.getText().isEmpty() && !lbl_3.getText().isEmpty() && !lbl_3.getText().contains("  ") && !txtbx_input.getText().isEmpty()) {
			
			// Furthermore, validates each file's directory; to see if they are of invalid type(s).
			File f1 = new File(lbl_2.getText());
			if (!f1.isFile())
				lbl_5.setText("Invalid source file");

			File f2 = new File(lbl_3.getText());
			if (!f2.isDirectory())
				lbl_5.setText("Invalid target directory.");
			
			
			// Proceeds only if the two conditions tested above are true.
			if (f1.isFile() && f2.isDirectory()) {
				
				// Defines another temporary file, which is used to step through the target directory.
				File f3 = new File(lbl_3.getText());
				
				// Defines a temporary string array, used to hold the name(s) that <f3> produces.
				String names[] = f3.list();
				
				
				// Steps through <f3>, and informs the user when an output file is going to be overwritten.
				for (int i = 0; i < names.length; i++) {
					if (names[i].equals(txtbx_input.getText())) {
						dup = true;
						lbl_5.setText("Output file exists; it will be overwritten.");
					}
				}
				
				// Tries to open the component(s) used for file writing/reading, before copying the source to the destination.
				try {
					
					// Constructs the BufferedReader() and PrintWriter() objects used for file reading/writing respectively.
					BufferedReader get = new BufferedReader(new FileReader(lbl_2.getText()));
					PrintWriter out = new PrintWriter(new FileWriter(lbl_3.getText() + "\\" + txtbx_input.getText()));
					
					// Defines <line>, which serves as a character-pointer for the reader/writer.
					int line;
					
					
					// Reading the file using integers, per the program Instructions.
					// Typically we would use .readLine(), however, we were instructed to use
					// .read().
					while ((line = get.read()) != -1) {
						out.write(line);
					}
					
					
					// Changes output text, but only if the file is not a copy.
					if (!dup)
						lbl_5.setText("File Copied.");
					
					// Close our PrintWriter object.
					out.close();
					
					// Clears all input fields, resets text input, and sets <good> to true.
					lbl_2.setText("");
					lbl_3.setText("");
					btn_target.setEnabled(false);
					txtbx_input.setText("");
					txtbx_input.setEnabled(false);
					has_source = false;
					has_target = false;
					good = true;
					
				// If the try above fails, the error is printed to the console.
				} catch (IOException e) {
					lbl_5.setText("An IO error occured.");
				}
				
				}
		}
		
		// If input was not <good>, then a relevant error message is displayed in <lbl_5>.
		if (!good) {
			
			if (lbl_2.getText().isEmpty() && txtbx_input.getText().isEmpty())
				lbl_5.setText("Source file, target directory, and target file not specified.");
			
			else if (lbl_3.getText().isEmpty()&& txtbx_input.getText().isEmpty())
				lbl_5.setText("Target directory and target file not specified.");
			
			else if (lbl_2.getText().isEmpty() || lbl_2.getText().equals(""))
				lbl_5.setText("Source file not specified.");
			
			else if (lbl_3.getText().isEmpty()||lbl_5.equals(""))
				lbl_5.setText("Target directory not specified.");
			
			else if (txtbx_input.getText().isEmpty())
				lbl_5.setText("Target file not specified.");
		}
		
		// Calls display() if input was <good>, updating the display to include the user's new file.
		else
			display(current_dir.getAbsolutePath());
		
		return;
	}



	/*
	The windowClosing() method:
		Description: 
			- Acts as a destructor for GUIFile().
		Preconditions: 
			- N/A.
		Postconditions:
			- frees memory associated with GUIFile() before the program terminates.
	*/
	@Override
	public void windowClosing(WindowEvent ae) {
		// Removing our listeners so they are erased from memory.
		this.removeWindowListener(this);
		btn_target.removeActionListener(this);
		btn_confirm.removeActionListener(this);
		txtbx_input.removeActionListener(this);
		this.dispose();
		return;
	}

	// Below are overwritten (but unimplemented) methods of the Frame() superclass.
	@Override
	public void windowDeactivated(WindowEvent arg0) {return;}
	@Override
	public void windowDeiconified(WindowEvent arg0) {return;}
	@Override
	public void   windowIconified(WindowEvent arg0) {return;}
	@Override
	public void      windowOpened(WindowEvent arg0) {return;}
	@Override
	public void   windowActivated(WindowEvent arg0) {return;}
	@Override
	public void      windowClosed(WindowEvent arg0) {return;}
}



/*
The IOfile() Class:
	Description:
		- serves as a wrapper for use in G7P3() (see above),
		- acts as a helper for File()'s methods.
	Preconditions:
	    - N/A.
	Postconditions:
		- constructor returns an instance of IOFile()*
		* this class has entirely static methods,
			+ this means the class does not need to be instantiated to be fully utilized.
*/
class IOFile
{	
	
	/*
	The FileExists() method:
		Description: 
			- determines whether <name> exists as a file path.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns a boolean <value>.
	*/
	public static boolean FileExists(String name){
		boolean value = false;
		
		File dummy_file = new File(name); 
		try
		{
			if(dummy_file.exists())
			{
				value = true;
			}
		}
		catch(NullPointerException FileExists_exception)
		{
			System.out.print(
			"|\n| - '" + name + "'\n"                     + 
			"|   * could not be opened (invalid path).\n" +
			"| - " + FileExists_exception + "\n"         +
			"|\n"
			);
			System.out.flush();
		}
		return value;
	}
	
	
	/*
	The FileExists() method:
		Description: 
			- determines whether <name> exists as a file path.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns a boolean <value>.
	*/
	public static boolean FileExists(File dummy_file){
		boolean value = false;
		if(dummy_file.exists())
		{
			value = true;
		}
		return value;
	}
	
	
	
	/*
	The FileIsDirectory() method:
		Description: 
			- determines whether <name> exists as a file directory.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns a boolean <value>.
	*/
	public static boolean FileIsDirectory(String name){
		boolean value = false;
		
		try
		{
			File dummy_file = new File(name); 
			if(dummy_file.isDirectory())
			{
				value = true;
			}
		}
		catch(NullPointerException FileIsDirectory_exception)
		{
			System.out.print(
			"|\n| - '" + name + "'\n"                     + 
			"|   * could not be opened (invalid path).\n" +
			"| - " + FileIsDirectory_exception + "\n"     +
			"|\n"
			);
			System.out.flush();
		}
		return value;
	}
	
	
	/*
	The FileIsDirectory() method:
		Description: 
			- determines whether <name> exists as a file directory.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns a boolean <value>.
	*/
	public static boolean FileIsDirectory(File dummy_file){
		boolean value = false;
		if(dummy_file.isDirectory())
		{
			value = true;
		}
		return value;
	}
	
	
	
	
	
	
	
	/*
	The FileIsWritable() method:
		Description: 
			- determines whether <name> is writable.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns a boolean <value>.
	*/
	public static boolean FileIsWritable(String name){
		boolean value = false;
		
		try
		{
			File dummy_file = new File(name); 
			if(dummy_file.canWrite())
			{
				value = true;
			}
		}
		catch(NullPointerException FileIsWritable_exception)
		{
			System.out.print(
			"|\n| - '" + name + "'\n"                     + 
			"|   * could not be opened (invalid path).\n" +
			"| - " + FileIsWritable_exception + "\n"      +
			"|\n"
			);
			System.out.flush();
		}
		return value;
	}
	
	

	/*
	The FileIsReadable() method:
		Description: 
			- determines whether <name> exists as a file path.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns a boolean <value>.
	*/
	public static boolean FileIsReadable(String name){
		boolean value = false;
		
		try
		{
			File dummy_file = new File(name); 
			if(dummy_file.canRead())
			{
				value = true;
			}
		}
		catch(NullPointerException FileIsReadable_exception)
		{
			System.out.print(
			"|\n| - '" + name + "'\n"                     + 
			"|   * could not be opened (invalid path).\n" +
			"| - " + FileIsReadable_exception + "\n"      +
			"|\n"
			);
			System.out.flush();
		}
		return value;
	}
	
	
	/*
	The FileExtension() method:
		Description: 
			- returns the file extension of <name>.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns String <value>, <name>'s extension.
	*/
	public static String FileExtension(String name){
		String value = null;
		
		int i = name.lastIndexOf(".");
		if(i != -1){
			value = name.substring(i+1);
		}
		return value;
	}
	
	
	/*
	The FileName() method:
		Description: 
			- returns the file name of <name>.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns String <value>, <name>'s file name.
	*/
	public static String FileName(String name){
		String value = null;
		try
		{
			File dummy = new File(name);
			value = dummy.getName();
		}
		catch(NullPointerException FileName_exception)
		{
			System.out.print(
			"|\n| - '" + name + "'\n"                     + 
			"|   * could not be opened (invalid path).\n" +
			"| - " + FileName_exception + "\n"            +
			"|\n"
			);
			System.out.flush();
		}
		return value;
	}
	
	
	/*
	The Filename() method:
		Description: 
			- returns the file path of <name>,
			- this path is absolute. 
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns String <value>, <name>'s file path.
	*/
	public static String FileName(File dummy){
		String value = dummy.getName();
		return value;
	}
	

	
	/*
	The OpenIn() method:
		Description: 
			- returns an initialized instance of BufferedReader(),
			- said instance is read to be read as input.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns BufferedReader() <value>.
	*/
	public static BufferedReader OpenIn(String name){
		BufferedReader value = null;
		
		if(name != null){
			try
			{
				value = new BufferedReader(new FileReader(name));
			}
			catch (FileNotFoundException OpenIn_exception)
			{
				System.out.print(
				"|\n| - '" + name + "'\n"                + 
				"|   * could not be opened (invalid path).\n" +
				"| - " + OpenIn_exception + "\n"
				);
				System.out.flush();
			};
		};
		return value;
	}
	
	/*
	The OpenOut() method:
		Description: 
			- returns an initialized instance of PrintWriter(),
			- said instance is written as output.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns OpenOut() <value>.
	*/
	public static PrintWriter OpenOut(String name){
		PrintWriter value = null;
		
				
		if(name != null){
			try
			{
				value = new PrintWriter(new File(name));
			}
			catch (FileNotFoundException OpenOut_exception)
			{
				System.out.print(
				"|\n| - '" + name + "'\n"                + 
				"|   * could not be opened (invalid path).\n" +
				"| - " + OpenOut_exception + "\n"
				);
				System.out.flush();
			}
		};
		return value;
	}


	/*
	The OpenFile() method:
		Description: 
			- returns an initialized File() class,
			  + this File() is initialized using <name>.
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns File() <value>.
	*/
	public static File OpenFile(String name){
		File value = null;
		
				
		if(name != null){
			try
			{
				value = new File(name);
			}
			catch (NullPointerException OpenFile_exception)
			{
				System.out.print(
				"|\n| - '" + name + "'\n"                + 
				"|   * could not be opened (I/0 Exception).\n" +
				"| - " + OpenFile_exception + "\n"
				);
				System.out.flush();
			}
		};
		return value;
	}
}
