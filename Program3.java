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

import java.awt.*;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

class Program3 extends Frame implements WindowListener, ActionListener {
	private static final long serialVersionUID = 1L;
	String curDir = null;
	String curFile = null;
	String sourceFile = null;
	String targetFile = null;
	// Extra space for grid constraints to leave room for a file name (Messy so maybe find different solution)
	Label l1 = new Label("Source:                                      ");
	Label l2 = new Label();
	// Extra space for same reason as "l1"
	Label l3 = new Label("Select Target Directory:                                      ");
	Label l4 = new Label("File Name:");
	Label l5 = new Label();
	List list = new List();
	Button b1 = new Button("Target");
	Button b2 = new Button("OK");
	TextField txt = new TextField();
	boolean targetMode = false;

	public static void main(String[] args) {
		if (args.length == 1) {
			try {
				File f1 = new File(args[0]);
				f1.createNewFile();
				if (f1.isDirectory()) {
					new Program3(f1.getAbsolutePath());
				} else {
					System.out.println("Enter a valid directory path.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			new Program3(new File(System.getProperty("user.dir")).getAbsolutePath());
		}
	}

	Program3(String s) {
		System.out.println(s);
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		double colweight[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		double rowweight[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		int width[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		int height[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		gbl.rowHeights = height;
		gbl.columnWidths = width;
		gbl.columnWeights = colweight;
		gbl.rowWeights = rowweight;
		this.setBounds(20, 20, 700, 350);
		this.setLayout(gbl);
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
		gbc.gridy = 6;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(l1, gbc);
		this.add(l1);
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(l2, gbc);
		this.add(l2);
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(b1, gbc);
		this.add(b1);
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.weighty = 1;
		gbc.gridheight = 1;
		gbl.setConstraints(l3, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(l3);
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbl.setConstraints(l4, gbc);
		this.add(l4);
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(txt, gbc);
		this.add(txt);
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 9;
		gbc.weightx = 9;
		gbl.setConstraints(l5, gbc);
		this.add(l5);
		gbc.gridx = 9;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbl.setConstraints(b2, gbc);
		this.add(b2);
		this.setVisible(true);
		this.addWindowListener(this);
		
		setDir(s);
		list.addActionListener(this);
		b1.addActionListener(this);
		b2.addActionListener(this);
		txt.addActionListener(this);
		
		b1.setEnabled(false);
		txt.setEnabled(false);
	}
	
		// Updates the title of the program, displays the files in the directory as a list in the window
	public void setDir(String dir) {
		
		curDir = new String(dir);
		this.setTitle(curDir);
		File file = new File(curDir);
		
		if (list != null) {
			list.removeAll();
		}
		
		if (file.getParent() != null)
			list.add("...");
		for (int i = 0; i < file.listFiles().length; i++)
		{
			if (file.listFiles()[i].isDirectory())
			list.add(file.listFiles()[i].getName() + " +");
			else
				list.add(file.listFiles()[i].getName());
		}
		
	}
	
	public boolean tryCopy()
	{
		boolean returnVal = false;
		if (IOFile.FileIsWritable(targetFile))
		{
			// Do copying
			IOFile.Copy(sourceFile, targetFile);
			// Displays message at the bottom of the screen
			l5.setText("Copied from " + sourceFile + " to " + targetFile);
			targetMode = false;
			l3.setText("Select Target Directory: ");
			l1.setText("Source: ");
			returnVal = true;
		}
		else
		{
			// Displays message at the bottom of the screen
			l5.setText("The Target file is not writable!");
		}
		return returnVal;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		// Checks if a file name was selected
		if (source == list) {
			curFile = null;
			b1.setEnabled(false);
			String selection = list.getSelectedItem();
			// Checks if it is a directory and if so removes the plus at the end
			if (selection.contains(" +"))
				selection = selection.substring(0, selection.length() - 2);
			File fileSelect = new File(curDir + "\\" + selection);
			if (fileSelect.exists()) {
				// Checks if parent was selected
				if (selection.equals("...")) {
					selection = new File(curDir).getParent();
					setDir(selection);
				}
				else if (fileSelect.isDirectory()) {
					setDir(fileSelect.getPath());
				}
				// A file (not directory) was selected
				else {
					curFile = new String(selection);
					if (!targetMode)
					{
						if (!b1.isEnabled())
							b1.setEnabled(true);
						l1.setText("Source: " + curFile);
					}
					else
					{
						l3.setText("Select Target Directory: " + curFile);
						targetFile = new String(curDir + "\\" + curFile);
					}
				}
			}
		}
		// Checks if "Target" was selected
		else if (source == b1 && curFile != null) {
			if (!targetMode)
			{
				sourceFile = new String(curDir + "\\" + curFile);
				if (IOFile.FileIsReadable(sourceFile))
				{
					targetMode = true;
					b1.setEnabled(false);
					txt.setEnabled(true);
				}
				else
				{
					// Displays message at the bottom of the screen
					l5.setText("The Source file is not readable!");
				}
			}
			// Ensures that the mode was changed to "target mode"
			if (targetMode)
			{
				l3.setText("Select Target Directory: " + curFile);
				targetFile = new String(curDir + "\\" + curFile);
			}
		}
		// Checks if "OK" was selected
		else if (source == b2) {
			if (targetMode)
			{
				if (targetFile.equals(sourceFile))
				{
					// Displays message at the bottom of the screen
					l5.setText("The Target file cannot match the Source file!");
				}
				else
				{
					if (tryCopy())
						txt.setEnabled(false);
				}
			}
			else
			{
				// Displays message at the bottom of the screen
				l5.setText("Source and Target file names not specified!");
			}
		} // End of "OK" button
		
		// Checks if enter was pressed in the text field
		else if (source == txt)
		{
			String selection = txt.getText();
			// First checks if the typed target file exists
			if (!IOFile.FileExists(selection))
			{
				selection = curDir + "\\" + selection;
				// Then checks if the typed file name with the current directory exists
				if (!IOFile.FileExists(selection))
				{
					// Displays message at the bottom of the screen
					l5.setText("Invalid target file");
					selection = null;
				}
			}
			if (selection != null)
			{
				targetFile = new String(selection);
				if (tryCopy())
				{
					txt.setText("");
					txt.setEnabled(false);
				}
			}
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent ae) {
		this.removeWindowListener(this);
		this.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
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
	The Copy() method:
		Description:
			- Copies the contents of <inFile> to <outFile>
		Preconditions:
			- <inFile> and <outFile> must be valid files
		Postconditions:
			- Returns true if the method was successful
	*/
	public static boolean Copy(String inFile, String outFile)
	{
		boolean returnVal = false;
		
		BufferedReader reader = IOFile.OpenIn(inFile);
		PrintWriter writer = IOFile.OpenOut(outFile);
		returnVal = reader != null && writer != null;
		if (writer == null)
			System.out.println("writer is null");
		String line = null;
		boolean done = false;
		while (!done)
		{
			try {
				line = reader.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
				returnVal = false;
			}
			if (line == null)
				done = true;
			else
				writer.println(line);
		}
		try {
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		writer.close();
		
		return returnVal;
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
