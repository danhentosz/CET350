
/*
 * Program3.java
 * Daniel Hentosz, Nathaniel Dehart, Scott Trunzo
 * Technical Computing Using Java || CET 350 
 * hen3883@calu.edu || deh5850@calu.edu || tru1931@calu.edu
 * GROUP 7
 */

import java.awt.*;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class Program3 extends Frame implements WindowListener, ActionListener {
	// Creating our variables.
	private static final long serialVersionUID = 1L;
	Label l1 = new Label("Source:");
	Label l2 = new Label();
	Label l3 = new Label(
			"                                                                                                                                  ");
	Label l4 = new Label("File Name:");
	Label l5 = new Label();
	List list = new List();
	Button b1 = new Button("Target");
	Button b2 = new Button("OK");
	TextField txt = new TextField();
	File curDir = new File("");
	boolean source, target, outfile;
	String targetname = "";

	// Our main method
	public static void main(String[] args) {
		// Did they enter a command line parameter?
		if (args.length == 1) {
			try {
				File f1 = new File(args[0]);
				f1.createNewFile();
				if (f1.isDirectory()) {
					new Program3(f1.getAbsolutePath());
				} else {
					// The command line parameter was not a valid directory path.
					System.out.println("Enter a valid directory path.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// If they did not enter any command line arguments.
			// Getting the directory that the user is currently in.
			new Program3(new File(System.getProperty("user.dir")).getAbsolutePath());
		}
	}

	// This class creates a basic user interface using AWT.
	// Frame, ActionListener, Buttons, Labels, TextFields, and the GridBagLAyout are
	// all utilized.
	Program3(String s) {
		// Will be used to keep track of our current location.
		curDir = new File(s);
		// Used to keep track of which buttons and fields are enabled.
		source = false;
		target = false;
		outfile = false;
		// Our Layout manager.
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		// Each row and column has a width of 10.
		double colweight[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		double rowweight[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		int width[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		int height[] = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		gbl.rowHeights = height;
		gbl.columnWidths = width;
		gbl.columnWeights = colweight;
		gbl.rowWeights = rowweight;
		// Making sure all the areas fit and do not overlap.
		this.pack();
		// Setting the window size.
		this.setBounds(20, 20, 700, 350);
		this.setLayout(gbl);
		// Constraints for list.
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
		// Constraints for label 1.
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
		// Constraints for label 2.
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(l2, gbc);
		this.add(l2);
		// Constraints for button 1.
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
		// Constraints for label 3.
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.weighty = 1;
		gbc.gridheight = 1;
		gbl.setConstraints(l3, gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.EAST;
		this.add(l3);
		// Constraints for label 4.
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbl.setConstraints(l4, gbc);
		this.add(l4);
		// Constraints for our text field.
		gbc.gridx = 1;
		gbc.gridwidth = 8;
		gbc.weightx = 8;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(txt, gbc);
		this.add(txt);
		// Constraints for label 5.
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.gridwidth = 9;
		gbc.weightx = 9;
		gbl.setConstraints(l5, gbc);
		this.add(l5);
		// Constraints for button 2.
		gbc.gridx = 9;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbl.setConstraints(b2, gbc);
		this.add(b2);
		// Adding our action listeners to each necessary component.
		b1.addActionListener(this);
		b2.addActionListener(this);
		list.addActionListener(this);
		txt.addActionListener(this);
		// Disabling our button and text field.
		b1.setEnabled(false);
		txt.setEnabled(false);
		this.setVisible(true);
		this.addWindowListener(this);
		// Call the display method.
		display(s);
	}

	void display(String s) {
		list.removeAll();
		// Making the title the current path, as long as its not the root.
		if (curDir.toPath().getNameCount() >= 2)
			list.add("...");
		else
			l5.setText("You're at the root");
		if (curDir.toPath().getNameCount() >= 1)
			this.setTitle(s);
		// An array of all the children.
		String names[] = curDir.list();
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				File f2 = new File(curDir.getAbsolutePath() + "\\" + names[i]);
				if (f2.isDirectory()) {
					// Checking if the directory has a sub directory, so we can add "+".
					String names2[] = f2.list();
					boolean status = false;
					if (names2 != null) {
						for (int j = 0; j < names2.length; j++) {
							File f3 = new File(f2.getAbsolutePath() + "\\" + names2[j]);
							if (f3.isDirectory() && !status) {
								names[i] = names[i] + "+";
								status = true;
							}
						}
					}
				}
				// Adding all the directory/file names
				list.add(names[i]);
			}
		}
	}

	// When an action is performed(a button clicked or a text field and ENTER), it
	// is sent here.
	@Override
	public void actionPerformed(ActionEvent ae) {
		// Making an object of the action performed.
		Object source = ae.getSource();
		// Button 1(Target).
		if (source == b1) {
			txt.setEnabled(true);
			target = true;
			targetname = curDir.getAbsolutePath();
			l3.setText(curDir.getAbsolutePath());
		} else if (source == b2) {
			// Button 2(OK).
			copy();
		} else if (source == list) {
			// The user clicked an item in the list.
			l5.setText("");
			String name = list.getSelectedItem();
			if (name.equals("...")) {
				// They want to go to the parent directory.
				if (curDir.toPath().getNameCount() == 1)// They are at the root, do not continue.
					l5.setText("You're at the root");
				else {
					int pos = 0;
					pos = curDir.getAbsolutePath().lastIndexOf("\\");
					curDir = new File(curDir.getAbsolutePath().substring(0, pos));
					display(curDir.getAbsolutePath());
				}
			} else if (name.endsWith("+")) {
				// If they want to open a directory with sub directories.
				name = name.substring(0, name.length() - 1);
				File temp = new File(curDir.getAbsolutePath() + "\\" + name);
				if (temp.isDirectory()) {
					curDir = new File(curDir.getAbsolutePath() + "\\" + name);
					display(curDir.getAbsolutePath());
				}
			} else if (!name.contains(".")) {
				// They clicked a file.
				File temp = new File(curDir.getAbsolutePath() + "\\" + name);
				if (temp.isDirectory()) {
					curDir = new File(curDir.getAbsolutePath() + "\\" + name);
					display(curDir.getAbsolutePath());
				}
			} else {
				l2.setText(curDir.getAbsolutePath() + "\\" + name);
				b1.setEnabled(true);
				if (txt.isEnabled())// Only set the text field when our target button is enabled.
					txt.setText(name);
			}
		} else if (source == txt) {
			// The user pressed enter when in the text field.
			copy();
			l2.setText("");
			l3.setText("                                                                                  ");
			txt.setText("");
			b1.setEnabled(false);
		}
	}

	// Our copy method used to display the correct messages for label 5(Our messages
	// label).
	void copy() {
		l5.setText("");
		// Used to see if there is a duplicate file.
		boolean dup = false;
		// Used to see if the file was copied correctly.
		boolean good = false;
		// If all the information fields have data.
		if (!l2.getText().isEmpty() && !l3.getText().isEmpty() && !l3.getText().contains("  ")
				&& !txt.getText().isEmpty()) {
			// Checking if label 2 is a valid file path.
			File f1 = new File(l2.getText());
			if (!f1.isFile())
				l5.setText("Invalid source file");
			// Checking is label 3 is a valid directory path.
			File f2 = new File(l3.getText());
			if (!f2.isDirectory())
				l5.setText("Invalid target directory.");
			// If both the conditions previously tested are true.
			if (f1.isFile() && f2.isDirectory()) {
				File f3 = new File(l3.getText());
				String names[] = f3.list();
				// Checking if the file already exists.
				for (int i = 0; i < names.length; i++) {
					if (names[i].equals(txt.getText())) {
						dup = true;
						l5.setText("Output file exists. It will be overwritten");
					}
				}
				try {
					BufferedReader get = new BufferedReader(new FileReader(l2.getText()));
					PrintWriter out = new PrintWriter(new FileWriter(l3.getText() + "\\" + txt.getText()));
					int line;
					// Reading the file using integers, per the program Instructions.
					// Typically we would use .readLine(), however, we were instructed to use
					// .read().
					while ((line = get.read()) != -1) {
						out.write(line);
					}
					// Only change the text in label 5 if the file was not a duplicate.
					if (!dup)
						l5.setText("File Copied");
					// Close our PrintWriter object.
					out.close();
					l2.setText("");
					l3.setText(
							"                                                                                                                   ");
					b1.setEnabled(false);
					txt.setText("");
					txt.setEnabled(false);
					good = true;
				} catch (IOException e) {
					l5.setText("An IO error occured");
				}
			}
		}
		if (!good) {// If copy was unsuccessful, we display the correct message in label 5.
			if (l2.getText().isEmpty() && l3.getText().contains("  ") && txt.getText().isEmpty())
				l5.setText("Source file, target directory, and target file not specified.");
			else if (l3.getText().contains("  ") && txt.getText().isEmpty())
				l5.setText("Target directory and target file not specified.");
			else if (l2.getText().isEmpty() || l2.getText().equals(""))
				l5.setText("Source file not specified.");
			else if (l3.getText().contains("  "))
				l5.setText("Target directory not specified");
			else if (txt.getText().isEmpty())
				l5.setText("Target file not specified");
		}
	}

	// This method is used when the user wishes to close the window.
	@Override
	public void windowClosing(WindowEvent ae) {
		// Removing our listeners so they are erased from memory.
		this.removeWindowListener(this);
		b1.removeActionListener(this);
		b2.removeActionListener(this);
		txt.removeActionListener(this);
		this.dispose();
	}

	// We do not use the rest of the methods, however, they must be in our code.
	// Due to us implementing WindowListener.
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}