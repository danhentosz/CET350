package Bounce;

/*
 * Bounce.java
 * Daniel Hentosz, Nathaniel Dehart, Scott Trunzo
 * Technical Computing Using Java || CET 350 
 * hen3883@calu.edu || deh5850@calu.edu || tru1931@calu.edu
 * GROUP 7
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class Bounce extends Frame implements WindowListener, ActionListener, ComponentListener, AdjustmentListener {
	// We will use the null layout manager
	// ActionListener for buttons
	// ComponetListener for resizing
	// AdjustmentListener for scroll Bars
	// Use a thread so we can multiple thing running at the same time
	// Use a canvas so we can draw
	private static final long serialVersionUID = 10L;
	// Constants used to calculate other parameters in the program
	private final int WIDTH = 640;// Frame width
	private final int HEIGHT = 400;// Frame height
	private final int BUTTONH = 20;// Button height
	private final int BUTTONHS = 5;// Button height spacing
	// Variable declarations:
	private int WinWidth = WIDTH;
	private int WinHeight = HEIGHT;
	private int ScreenWidth;// For drawing area
	private int ScreenHeight;// For drawing area
	private int WinTop = 10;// Starting top position
	private int WinLeft = 10;// Starting left position
	private int BUTTONW = 10;// NOT SURE ON THE VALUE OF THIS..WASNT IN THE SLIDES
	private int CENTER = (WIDTH / 2);
	private int BUTTONS = BUTTONW / 4;
	private Insets I;
	Button Start, Shape, Clear, Tail, Quit;
	// A few constants for the scroll bar.
	private final int MAXObj = 100;
	private final int MINObj = 10;
	private final int SPEED = 50;
	private final int SBvisible = 10;
	private final int SBunit = 1;// Unit increments.
	private final int SBblock = 10;// Block increment.
	private final int SCROLLBARH = BUTTONH;
	private final int SOBJ = 21;// It must always be an odd value so we have a center.
	private int SObj = SOBJ;
	private int SpeedSBmin = 1;
	private int SpeedSBmax = 100 + SBvisible;
	private int SpeedSBinit = SPEED;
	private int ScrollBarW;
	// Our objects.
	private Objc Obj;// NOT SURE ON THIS, HE HAS private Objc Obj
	private Label SPEEDL = new Label("Speed", Label.CENTER);
	private Label SIZEL = new Label("Size", Label.CENTER);
	Scrollbar SpeedScrollBar, ObjSizeScrollBar;

	public static void main(String[] args) {
		new Bounce();
	}

	Bounce() {
		setLayout(null);
		setVisible(true);
		// To determine the sizes for the sheet
		MakeSheet();
		try {
			// Can throw back an exception from adding objects and listeners
			initComponets();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SizeScreen();
		// To begin animation and graphics.
		start();
	}

	// For new Frame information and to recalculate Frame sizes
	//
	private void MakeSheet() {
		I = getInsets();
		ScreenWidth = WinWidth - I.left - I.right;
		ScreenHeight = WinHeight - I.top - 2 * (BUTTONH + BUTTONHS) - I.bottom;
		setSize(WinWidth, WinHeight);
		CENTER = (ScreenWidth / 2);
		// Using a width of 11 because we have 5 buttons and it just works out nice.
		BUTTONW = ScreenWidth / 11;
		BUTTONS = BUTTONW / 4;
		setBackground(Color.pink);
		ScrollBarW = 2 * BUTTONW;
	}

	// For adding the objects, only used once
	void initComponets() {
		// Can throw back an IOException
		Start = new Button("Run");
		Shape = new Button("Circle");
		Clear = new Button("Clear");
		Tail = new Button("No Tail");
		Quit = new Button("Quit");
		// Adding our buttons to the frame
		add("Center", Start);
		add("Center", Shape);
		add("Center", Tail);
		add("Center", Clear);
		add("Center", Quit);
		Start.addActionListener(this);
		Shape.addActionListener(this);
		Tail.addActionListener(this);
		Clear.addActionListener(this);
		Quit.addActionListener(this);
		this.addComponentListener(this);
		this.addWindowListener(this);
		// Will attempt to set the size
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		// We will never let the frame get any smaller than the preferred size.
		setMinimumSize(getPreferredSize());
		setBounds(WinLeft, WinTop, WIDTH, HEIGHT);
		validate();

		SpeedScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
		SpeedScrollBar.setMaximum(SpeedSBmax);
		SpeedScrollBar.setMinimum(SpeedSBmin);
		SpeedScrollBar.setUnitIncrement(SBunit);
		SpeedScrollBar.setBlockIncrement(SBblock);
		SpeedScrollBar.setValue(SpeedSBinit);
		SpeedScrollBar.setVisibleAmount(SBvisible);
		SpeedScrollBar.setBackground(Color.gray);

		ObjSizeScrollBar = new Scrollbar(Scrollbar.HORIZONTAL);
		ObjSizeScrollBar.setMaximum(MAXObj);
		ObjSizeScrollBar.setMinimum(MINObj);
		ObjSizeScrollBar.setUnitIncrement(SBunit);
		ObjSizeScrollBar.setBlockIncrement(SBblock);
		ObjSizeScrollBar.setValue(SOBJ);
		ObjSizeScrollBar.setVisibleAmount(SBvisible);
		ObjSizeScrollBar.setBackground(Color.gray);
		Obj = new Objc(SObj, ScreenWidth, ScreenHeight);
		Obj.setBackground(Color.white);

		add(SpeedScrollBar);
		add(ObjSizeScrollBar);
		add(SPEEDL);
		add(SIZEL);
		add(Obj);
		SpeedScrollBar.addAdjustmentListener(this);
		ObjSizeScrollBar.addAdjustmentListener(this);
	}

	// For sizing of the objects
	void SizeSheet() {

	}

	void SizeScreen() {
		// Positioning the buttons.
		Start.setLocation(CENTER - 2 * (BUTTONW + BUTTONS) - BUTTONW / 2, ScreenHeight + BUTTONHS + I.top);
		Shape.setLocation(CENTER - BUTTONW - BUTTONS - BUTTONW / 2, ScreenHeight + BUTTONHS + I.top);
		Tail.setLocation(CENTER - BUTTONW / 2, ScreenHeight + BUTTONHS + I.top);
		Clear.setLocation(CENTER + BUTTONS + BUTTONW / 2, ScreenHeight + BUTTONHS + I.top);
		Quit.setLocation(CENTER + BUTTONW + 2 * BUTTONS + BUTTONW / 2, ScreenHeight + BUTTONHS + I.top);
		// Size the buttons.
		Start.setSize(BUTTONW, BUTTONH);
		Shape.setSize(BUTTONW, BUTTONH);
		Tail.setSize(BUTTONW, BUTTONH);
		Clear.setSize(BUTTONW, BUTTONH);
		Quit.setSize(BUTTONW, BUTTONH);

		SpeedScrollBar.setLocation(I.left + BUTTONS, ScreenHeight + BUTTONHS + I.top);
		ObjSizeScrollBar.setLocation(WinWidth - ScrollBarW - I.right - BUTTONS, ScreenHeight + BUTTONHS + I.top);
		SPEEDL.setLocation(I.left + BUTTONS, ScreenHeight + BUTTONHS + BUTTONH + I.top);
		SIZEL.setLocation(WinWidth - ScrollBarW - I.right, ScreenHeight + BUTTONHS + BUTTONH + I.top);
		SpeedScrollBar.setSize(ScrollBarW, SCROLLBARH);
		ObjSizeScrollBar.setSize(ScrollBarW, SCROLLBARH);
		SPEEDL.setSize(ScrollBarW, BUTTONH);
		SIZEL.setSize(ScrollBarW, SCROLLBARH);
		Obj.setBounds(I.left, I.top, ScreenWidth, ScreenHeight);
	}

	public void start() {
		// Obj.repaint();
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent ae) {
		WinWidth = getWidth();
		WinHeight = getWidth();
		// Will recalculate screen width, height, and button sizes
		MakeSheet();
		// Will change the sizes in the frame
		SizeScreen();
		Obj.reSize(ScreenWidth, ScreenHeight);// THIS LINE MIGHT BE GOOD AFTER WE SET THE WIDTH AND HEIGHT, IDK
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == Start) {
			if (Start.getLabel().equals("Pause")) {
				Start.setLabel("Run");
			} else {
				Start.setLabel("Pause");
			}
		} else if (source == Shape) {
			if (Shape.getLabel().equals("Circle")) {
				Shape.setLabel("Square");
				Obj.rectangle(false);
			} else {
				Shape.setLabel("Circle");
				Obj.rectangle(true);
			}
			Obj.repaint();
		} else if (source == Tail) {
			if (Tail.getLabel().equals("Tail")) {
				Tail.setLabel("No Tail");
			} else {
				Tail.setLabel("Tail");
			}
		} else if (source == Clear) {
			Obj.Clear();
			Obj.repaint();
		} else if (source == Quit)
			stop();
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
	public void windowClosing(WindowEvent arg0) {
		stop();
	}

	// Used to close everything when the user wants to close the window
	// We can call this from windowCloing and our ButtonEvent t quit the program.
	void stop() {
		Start.removeActionListener(this);
		Shape.removeActionListener(this);
		Clear.removeActionListener(this);
		Tail.removeActionListener(this);
		Quit.removeActionListener(this);
		this.removeComponentListener(this);
		this.removeWindowListener(this);
		SpeedScrollBar.removeAdjustmentListener(this);
		ObjSizeScrollBar.removeAdjustmentListener(this);
		dispose();
		System.exit(0);
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

	@Override
	public void adjustmentValueChanged(AdjustmentEvent ae) {
		int TS;
		Scrollbar sb = (Scrollbar) ae.getSource();
		if (sb == SpeedScrollBar) {
			// We are not going to do anything with the speed inn this program.
		}
		if (sb == ObjSizeScrollBar) {
			TS = ae.getValue();
			TS = (TS / 2) * 2 + 1;// Making it an odd size, so we cn get a center.
			Obj.update(TS);
		}
		Obj.repaint();
	}
}

class Objc extends Canvas {
	private static final long serialVersionUID = 11L;
	private int ScreenWidth;
	private int ScreenHeight;
	private int SObj;
	private int x, y;
	private boolean rect = true;
	private boolean clear = false;

	public Objc(int SB, int w, int h) {
		ScreenWidth = w;
		ScreenHeight = h;
		SObj = SB;
		rect = true;
		clear = false;
		y = ScreenHeight / 2;
		x = ScreenWidth / 2;
	}

	public void rectangle(boolean r) {
		rect = r;
	}

	public void update(int NS) {
		SObj = NS;
	}

	public void reSize(int w, int h) {
		ScreenWidth = w;
		ScreenHeight = h;
		y = ScreenHeight / 2;
		x = ScreenWidth / 2;
	}

	public void Clear() {
		clear = true;
	}

	public void paint(Graphics g) {
		g.setColor(Color.red);
		g.drawRect(0, 0, ScreenWidth - 1, ScreenHeight - 1);
		update(g);
	}

	// Will perform the drawing/
	public void update(Graphics g) {
		if (clear) {
			super.paint(g);
			clear = false;
			g.setColor(Color.red);
			g.drawRect(0, 0, ScreenWidth - 1, ScreenHeight - 1);
		}
		if (rect) {
			g.setColor(Color.lightGray);
			// Filling the rectangle using x as the center position.
			// Going half the width to the left and half the width up with y as the center
			// psoition
			g.fillRect(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj);
			g.setColor(Color.black);
			g.drawRect(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj - 1, SObj - 1);
		} else {
			// If its not a rectangle, its a circle.
			// Same thing we did with rectangle, except we re using the fillOval as opposed
			// to the deawOval.
			g.setColor(Color.lightGray);
			g.fillOval(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj, SObj);
			g.setColor(Color.black);
			g.drawOval(x - (SObj - 1) / 2, y - (SObj - 1) / 2, SObj - 1, SObj - 1);
		}
	}
}