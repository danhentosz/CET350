
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class Program3 extends Frame implements WindowListener, ActionListener {
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
		curDir = new File(s);
		source = false;
		target = false;
		outfile = false;
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
		b1.addActionListener(this);
		b2.addActionListener(this);
		list.addActionListener(this);
		txt.addActionListener(this);
		b1.setEnabled(false);
		txt.setEnabled(false);
		// Do we need to pack it? It says to do this in the lectures(class 8)
		// this.pack();
		this.setVisible(true);
		this.addWindowListener(this);
		// Call the display method
		display(s);
	}

	void display(String s) {
		list.removeAll();
		// Making the title the current path, as long as its not the root
		if (curDir.toPath().getNameCount() > 0) {
			this.setTitle(s);
		}
		list.add("...");
		String names[] = curDir.list();
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				File f2 = new File(curDir.getAbsolutePath() + "\\" + names[i]);
				if (f2.isDirectory()) {
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
				list.add(names[i]);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == b1) {
			target = true;
			targetname = curDir.getAbsolutePath();
			l3.setText(curDir.getAbsolutePath());
		} else if (source == b2) {
			copy();
		} else if (source == list) {
			String name = list.getSelectedItem();
			if (name.equals("...")) {
				int pos = 0;
				pos = curDir.getAbsolutePath().lastIndexOf("\\");
				curDir = new File(curDir.getAbsolutePath().substring(0, pos));
				display(curDir.getAbsolutePath());
			} else if (name.endsWith("+")) {
				name = name.substring(0, name.length() - 1);
				File temp = new File(curDir.getAbsolutePath() + "\\" + name);
				if (temp.isDirectory()) {
					curDir = new File(curDir.getAbsolutePath() + "\\" + name);
					display(curDir.getAbsolutePath());
				}
			} else if (!name.contains(".")) {
				File temp = new File(curDir.getAbsolutePath() + "\\" + name);
				if (temp.isDirectory()) {
					curDir = new File(curDir.getAbsolutePath() + "\\" + name);
					display(curDir.getAbsolutePath());
				}
			} else {
				l2.setText(curDir.getAbsolutePath() + "\\" + name);
				b1.setEnabled(true);
				txt.setEnabled(true);
				txt.setText(name);
			}
		} else if (source == txt) {
		}
	}

	void copy() {
		l5.setText("");
		try {
			BufferedReader get=new BufferedReader(new FileReader(l2.getText()));
			File ftemp=new File(l3.getText()+"\\"+txt.getText());
			if(!ftemp.exists()) {
				l5.setText("Target file not specified.");
			}
			PrintWriter out=new PrintWriter(new FileWriter(ftemp));
			String temp="";
			while((temp=get.readLine())!=null) {
				out.println(temp);
			}
			out.close();
			l5.setText("File Copied");
		} catch (IOException e) {
			l5.setText("Target file not specified");
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