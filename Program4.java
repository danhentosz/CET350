
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Thread;

public class Program4 
{
	public static void main(String[] args)
	{
		Bounce bounce = new Bounce();
	}
}

class Bounce extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable
{
	static final long serialVersionUID = 49L;
	
	private final int BUTTON_H = 25;
	private final int INIT_SIZE = 21;
	private final int MAX_SIZE = 100;
	private final int MIN_SIZE = 10;
	private final int SB_SPEED = 50;
	private final int SB_VIS = 10;
	private final int SB_STEP = 1;
	private final int SB_BLOCK = 10;
	private final int SB_HEIGHT = BUTTON_H;
	
	private int width = 640;
	private int height = 480;
    private int buttonW = 50;
    // Button spacing
	private int buttonS;
	// Button height spacing
	private int buttonHS = 10;
	private int center;
	private int sbMinSpeed = 1;
	private int sbMaxSpeed = 100 + SB_VIS;
	private int sbSpeed = SB_SPEED;
	private int sbW;
	private int objW = INIT_SIZE;
	private int delay = 16; // Change this to be a calculation based on "sbSpeed"
	
	private Objc theObj;
	
	private boolean isRunning = true;
	private boolean isPaused = true;
	private boolean hasStarted = false;
	private boolean hasTail = true;
	
	// Width of the window
	private int winWidth;
	// Height of the window
	private int winHeight;
	private int winLeft;
	private int winTop;
	// Width of the rendering area
	private int screenWidth;
	// Height of the rendering area
	private int screenHeight;
	
	private Insets insets;
	
	private Thread thread;
	
	private Button startButton;
	private Button shapeButton;
	private Button tailButton;
	private Button clearButton;
	private Button quitButton;
	
	private Label speedLabel = new Label("Speed", Label.CENTER);
	private Label sizeLabel = new Label("Size", Label.CENTER);
	
	private Scrollbar sizeBar;
	private Scrollbar speedBar;

	Bounce()
	{
		setLayout(null);
		setVisible(true);
		makeSheet();
		
		try
		{
			initComponents();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		sizeScreen();
		start();
	}
	
	private void start()
	{
		delay = sbMaxSpeed - speedBar.getValue() + 2;
		theObj.repaint();
		if (thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}
	
	private void sizeScreen() 
	{
		startButton.setLocation(center - 2 * (buttonW + buttonS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		shapeButton.setLocation(center - (buttonW + buttonS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		tailButton.setLocation(center - buttonW / 2, screenHeight + buttonHS + insets.top);
		clearButton.setLocation(center + (buttonW + buttonS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		quitButton.setLocation(center + 2 * (buttonW + buttonS) - buttonW / 2, screenHeight + buttonHS + insets.top);
		speedBar.setLocation(insets.left + buttonS, screenHeight + buttonHS + insets.top);
		speedLabel.setLocation(insets.left + buttonS, screenHeight + buttonHS + BUTTON_H + insets.top);
		sizeBar.setLocation(winWidth - sbW - insets.right - buttonS, screenHeight + buttonHS + insets.top);
		sizeLabel.setLocation(winWidth - sbW - insets.right - buttonS, screenHeight + buttonHS + BUTTON_H + insets.top);
		
		startButton.setSize(buttonW, BUTTON_H);
		shapeButton.setSize(buttonW, BUTTON_H);
		tailButton.setSize(buttonW, BUTTON_H);
		clearButton.setSize(buttonW, BUTTON_H);
		quitButton.setSize(buttonW, BUTTON_H);
		sizeBar.setSize(sbW, SB_HEIGHT);
		sizeLabel.setSize(sbW, BUTTON_H);
		speedLabel.setSize(sbW, BUTTON_H);
		speedBar.setSize(sbW, SB_HEIGHT);
		
		theObj.setBounds(insets.left, insets.top, screenWidth, screenHeight);
	}

	public void makeSheet()
	{
		insets = getInsets();
		screenWidth = winWidth - insets.left - insets.right;
		screenHeight = winHeight - insets.top - insets.bottom - 2 * (BUTTON_H + buttonHS);
		setSize(winWidth, winHeight);
		center = screenWidth / 2;
		buttonW = screenWidth / 11;
		sbW = buttonW * 2;
		buttonS = buttonW / 4;
		this.setBackground(Color.lightGray);
	}
	
	public void initComponents() throws Exception, IOException
	{
		startButton = new Button("Run");
		shapeButton = new Button("Circle");
		tailButton = new Button("No Tail");
		clearButton = new Button("Clear");
		quitButton = new Button("Quit");
		
		speedBar = new Scrollbar(Scrollbar.HORIZONTAL);
		speedBar.setMaximum(sbMaxSpeed + SB_VIS);
		speedBar.setMinimum(sbMinSpeed);
		speedBar.setUnitIncrement(SB_STEP);
		speedBar.setBlockIncrement(SB_BLOCK);
		speedBar.setValue(sbSpeed);
		speedBar.setVisibleAmount(SB_VIS);
		speedBar.setBackground(Color.gray);
		
		sizeBar = new Scrollbar(Scrollbar.HORIZONTAL);
		sizeBar.setMaximum(MAX_SIZE + SB_VIS);
		sizeBar.setMinimum(MIN_SIZE);
		sizeBar.setUnitIncrement(SB_STEP);
		sizeBar.setBlockIncrement(SB_BLOCK);
		sizeBar.setValue(INIT_SIZE);
		sizeBar.setVisibleAmount(SB_VIS);
		sizeBar.setBackground(Color.gray);
		
		theObj = new Objc(objW, screenWidth, screenHeight);
		theObj.setBackground(Color.white);
		
		this.add("Center", startButton);
		this.add("Center", shapeButton);
		this.add("Center", tailButton);
		this.add("Center", clearButton);
		this.add("Center", quitButton);
		
		this.add(speedBar);
		this.add(sizeBar);
		this.add(speedLabel);
		this.add(sizeLabel);
		this.add(theObj);
		
		speedBar.addAdjustmentListener(this);
		sizeBar.addAdjustmentListener(this);
		
		startButton.addActionListener(this);
		shapeButton.addActionListener(this);
		tailButton.addActionListener(this);
		clearButton.addActionListener(this);
		quitButton.addActionListener(this);
		
		this.addComponentListener(this);
		this.addWindowListener(this);
		this.setPreferredSize(new Dimension(width, height));
		this.setMinimumSize(getPreferredSize());
		this.setBounds(winLeft, winTop, width, height);
		
		speedBar.setEnabled(true);
		speedBar.setVisible(true);
		
		validate();
	}
	
	public void stop()
	{
		isRunning = false;
		
		startButton.removeActionListener(this);
		shapeButton.removeActionListener(this);
		tailButton.removeActionListener(this);
		clearButton.removeActionListener(this);
		quitButton.removeActionListener(this);
		
		speedBar.removeAdjustmentListener(this);
		sizeBar.removeAdjustmentListener(this);
		
		this.removeComponentListener(this);
		this.removeWindowListener(this);
		
		dispose();
		System.exit(0);
	}

	@Override
	public void run() 
	{
		while (isRunning)
		{
			if (!isPaused)
			{
				theObj.repaint();
				try 
				{
					Thread.sleep(delay);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			try 
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		int scrollVal;
		Scrollbar sb = (Scrollbar)e.getSource();
		scrollVal = sb.getValue();
		
		if (sb == speedBar)
		{
			delay = sbMaxSpeed - speedBar.getValue() + 2;
		}
		else if (sb == sizeBar)
		{
			scrollVal = (scrollVal / 2) * 2 + 1;
			theObj.updateSize(scrollVal);
		}
		theObj.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if (source == startButton)
		{
			if (isPaused)
			{
				isPaused = false;
				startButton.setLabel("Stop");
			}
			else
			{
				isPaused = true;
				startButton.setLabel("Run");
			}
		}
		else if (source == shapeButton)
		{
			if (shapeButton.getLabel() == "Circle")
			{
				shapeButton.setLabel("Square");
				theObj.setRect(false);
			}
			else
			{
				shapeButton.setLabel("Circle");
				theObj.setRect(true);
			}
			theObj.repaint();
		}
		else if (source == tailButton)
		{
			if (hasTail)
			{
				hasTail = false;
				theObj.setTail(false);
				tailButton.setLabel("Tail");
			}
			else
			{
				hasTail = true;
				theObj.setTail(true);
				tailButton.setLabel("No Tail");
			}
		}
		else if (source == clearButton)
		{
			theObj.clear();
			theObj.repaint();
		}
		else // Quit button
		{
			stop();
		}
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		winWidth = getWidth();
		winHeight = getHeight();
		makeSheet();
		theObj.reSize(screenWidth, screenHeight);
		sizeScreen();
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		
	}

	@Override
	public void componentHidden(ComponentEvent e)
	{
		
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		
	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		stop();
	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		
	}

	@Override
	public void windowIconified(WindowEvent e)
	{
		
	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{
		
	}

	@Override
	public void windowActivated(WindowEvent e)
	{
		
	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		
	}
}

class Objc extends Canvas
{
	static final long serialVersionUID = 11L;
	
	private int screenWidth;
	private int screenHeight;
	private int size;
	private int lastSize;
	private int x, y;
	private int oldX, oldY;
	private boolean isRect;
	private boolean clearFlag;
	private boolean hasTail;
	private boolean wasRect;
	private Point velocity;
	
	Objc(int objSize, int width, int height)
	{
		screenWidth = width;
		screenHeight = height;
		size = objSize;
		isRect = true;
		clearFlag = false;
		x = screenWidth / 2;
		y = screenHeight / 2;
		velocity = new Point(1, 1);
		hasTail = true;
	}
	
	public void setTail(boolean hasTail)
	{
		this.hasTail = hasTail;
		lastSize = size;
		wasRect = isRect;
		oldX = x;
		oldY = y;
	}
	
	public void setRect(boolean isRect)
	{
		this.isRect = isRect;
	}
	
	@Override
	public void update(Graphics g)
	{
		if (clearFlag)
		{
			clearFlag = false;
			super.paint(g);
			g.setColor(Color.red);
			g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
		}
		
		// Update the position
		x += velocity.x;
		y += velocity.y;
		
		// Check for boundaries
		if ((velocity.x > 0 && x + size / 2 > screenWidth - 4) || (velocity.x < 0 && x - size / 2 < 2))
			velocity.x = -velocity.x;
		if ((velocity.y > 0 && y + size / 2 > screenHeight - 4) || (velocity.y < 0 && y - size / 2 < 2))
			velocity.y = -velocity.y;
		
		// Draws over the last draw of the Bounce Object
		if (!hasTail)
		{
			g.setColor(Color.white);
			if (wasRect)
				g.fillRect(oldX - lastSize / 2, oldY - lastSize / 2, lastSize + 1, lastSize + 1);
			else
				g.fillOval(oldX - lastSize / 2 - 1, oldY - lastSize / 2 - 1, lastSize + 2, lastSize + 2);
			lastSize = size;
			wasRect = isRect;
			oldX = x;
			oldY = y;
		}
		
		if (isRect)
		{
			g.setColor(Color.lightGray);
			g.fillRect(x - size / 2, y - size / 2, size, size);
			g.setColor(Color.black);
			g.drawRect(x - size / 2, y - size / 2, size, size);
		}
		else
		{
			g.setColor(Color.lightGray);
			g.fillOval(x - size / 2, y - size / 2, size, size);
			g.setColor(Color.black);
			g.drawOval(x - size / 2, y - size / 2, size, size);
		}
	}
	
	public void reSize(int screenWidth, int screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		checkOverlap();
	}
	
	// Checks if the bounce object has a position beyond any of the boundaries and resolve it
	// If the object is beyond a boundary then it will also set its velocity accordingly
	public void checkOverlap()
	{
		if (x + size / 2 > screenWidth - 4)
		{
			velocity.x = -1;
			x = screenWidth - 2 - size / 2;
		}
		else if (x - size / 2 < 2)
		{
			velocity.x = 1;
			x = size / 2;
		}
		if (y + size / 2 > screenHeight - 4)
		{
			velocity.y = -1;
			y = screenHeight - 2 - size / 2;
		}
		else if (y - size / 2 < 2)
		{
			velocity.y = 1;
			y = size / 2;
		}
	}
	
	public void updateSize(int size)
	{
		this.size = size;
		
		checkOverlap();
	}
	
	public void clear()
	{
		clearFlag = true;
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(Color.red);
		g.drawRect(0, 0, screenWidth - 1, screenHeight - 1);
		update(g);
	}
}
