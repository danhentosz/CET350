/*
.Program3, G.U.I. File Copy                   (G7P3.java).
.Created By:                                             .
- Daniel Hentosz,    (HEN3883@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu).                 .
.Last Revised: Feburary 24th, 2021.           (2/24/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
T.B.W. - to be written.
*/


// Imports IOException(), for use with InputStreamReader and BufferedReader.
// - without use of this class (throw or catch), java will refuse to initialize BufferedReader inside of a method.
import java.io.IOException;

// Imports IOException(), for use with InputStreamReader and BufferedReader.
// - without use of this class (throw or catch), java will refuse to initialize BufferedReader inside of a method.
import java.io.FileNotFoundException;

// Imports InputStreamReader(), which is used to instantiate BufferedReader (see below).
import java.io.InputStreamReader;

// Imports FileReader(), an object class used for reading/writing to files within this program.
import java.io.FileReader;

// Imports BufferedReader(), the interface that this program uses to parse input from InputStreamReader().
import java.io.BufferedReader;

// Imports PrintWriter(), the interface that this program uses to output data into a file.
import java.io.PrintWriter;

// Imports File(), an object class used for reading/writing to files within this program.
import java.io.File;


// Imports the entire .awt namespace, for use in conjunction with FileGUI() to create a Graphical User Interface.
// Classes utilized by this program include:
// - Frame(), an object class used for containing .awt classes within this program.
//   + the subclass FileGUI() uses Frame() as it's superclass.
// - ActionListener() for eventhandling regarding class(es) inside of FileGUI(),
// - WindowListener() for eventhandling regarding class(es) inside of FileGUI(),
// - List() for use conglomerating class(es) defined for use within FileGUI().
import java.awt.*;

// Imports the entire .event namespace, for use in conjunction with WindowListener().
import java.awt.event.*;


/*
The G7P3() Class:
	Description:
		- serves as a container class for main() (see block comment below),
	Preconditions:
		- shared with main() (see below),
	Postconditions:
		- Constructor returns an instance of G7P3().
*/
public class G7P3
{
	
	/*
	The main() method:
		Description: 
			- T.B.W. - to be written.
		Preconditions:
			- N/A.
		Postconditions:
			- can copy file(s) specified by user input (see below for details).
	*/
	public static void main(String[] args)
	{
		// STRINGS;
		// A set of string variables used for various modular labels.
		String prompt_req_sfile = "Source file not specified.";
		String prompt_req_tfile = "Target not specified.";
		String prompt_wrt_tfile = "Output file exists ... It will be overwritten";
		String prompt_err_io    = "An I/O Error occured; terminating.";
		String prompt_err_open  = "Error Opening File.";
		String prompt_success   = "File Copied!";
		
		FileGUI GUI = new FileGUI();
		
		GUI.Window();
	}
}





/*
The FileGUI() Class:
	Description:
		- serves as a Graphical User Interface, and the main body of G7P3.
	Preconditions:
		- N/A,
	Postconditions:
		- Constructor returns an instance of FileGUI().
*/
class FileGUI extends Frame implements WindowListener
{
	private static final long serialVersionUID = 3108240192874L;
	Label MessageLabel = new Label("Program #3 - File Copying G.U.I.");
	
	/*
	The window() method:
		Description: 
			- Acts as a constructor for elements inside of FileGUI();
		Preconditions:
			- N/A.
		Postconditions:
			- Instantiates object(s) required for a functioning Frame() subclass.
	*/
	void Window()
	{
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout displ = new GridBagLayout();
		
		double colWeights[] = {1};
		int     colWidths[] = {1};

		int    rowHeights[] = {1};
		double rowWeights[] = {1};
		
		displ.rowHeights = rowHeights;
		displ.columnWidths = colWidths;
		displ.columnWeights = colWeights;
		displ.rowWeights = rowWeights;
		
		this.setBounds(20, 20, 20, 20);
		this.setLayout(displ);
		
		// Creates a message.
		displ.setConstraints(MessageLabel, c);
		this.add(MessageLabel);
		this.setVisible(true);
		this.addWindowListener(this);
		
		return;
	}
	
	public void windowActivated(WindowEvent e)
	{
		MessageLabel.setText("A window activated");
		return;
	}
	
	public void windowDeactivated(WindowEvent e)
	{
		MessageLabel.setText("A window deactivated");
		return;
	}
	
	public void windowClosing(WindowEvent e)
	{
		this.removeWindowListener(this);
		this.dispose();
		
		return;
	}
	
	
	/*
	Unimplemented events of WindowListener();
	 - due to implementation requirements, these functions are overwritten with blanks.
	 - they are not referenced in this program otherwise.
	*/
	public void windowClosed(WindowEvent e)     {return;};
	
	public void windowOpened(WindowEvent e)     {return;};
	
	public void windowIconified(WindowEvent e)  {return;};

	public void windowDeiconified(WindowEvent e) {return;};

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
		String value      = null;
		String value_temp = null;
		
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
	The FilePath() method:
		Description: 
			- returns the file path of <name>,
			- this path is absolute. 
		Preconditions:
			name:    - is a valid String value.
		Postconditions:
			- Returns String <value>, <name>'s file path.
	*/
	public static String FilePath(String name){
		String value = null;
		
		try{
			File dummy = new File(name);
			value = dummy.getAbsolutePath();
		}
		catch(NullPointerException FilePath_exception)
		{
			System.out.print(
			"|\n| - '" + name + "'\n"                     + 
			"|   * could not be opened (invalid path).\n" +
			"| - " + FilePath_exception + "\n"            +
			"|\n"
			);
			System.out.flush();
		}
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
}
