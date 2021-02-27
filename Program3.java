
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

class Program3 extends Frame implements WindowListener, ActionListener {
	private static final long serialVersionUID = 1L;
	String curDir = null;
	Label l1 = new Label("Source:");
	Label l2 = new Label();
	Label l3 = new Label("Select Target Directory:");
	Label l4 = new Label("File Name:");
	Label l5 = new Label();
	List list = new List();
	Button b1 = new Button("Target");
	Button b2 = new Button("OK");
	TextField txt = new TextField();

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
		this.setTitle("Current Directory Goes Here");
		this.setVisible(true);
		this.addWindowListener(this);
		
		setDir(s);
		list.addActionListener(this);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == list) {
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
				else {
					// Do stuff with target/source
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