
/*
 * Program2.java
 * Daniel Hentosz and Nathaniel Dehart
 * Technical Computing Using Java || CET 350 
 * hen3883@calu.edu || deh5850@calu.edu
 * GROUP 7
 */
import java.io.*;
import java.util.StringTokenizer;

class Program2 {
	public static void main(String[] args) {
		Word[] uniqueWords = new Word[100];
		int wordIndex = 0;
		int totalValue = 0;
		String inFileLine = null;
		String currentToken = null;
		StringTokenizer tokens;
		IOFile f1 = new IOFile();
		
		if (f1.getNames(args)) {
			if (f1.choices()) {
				System.out.println("yay. we have valid shitttt\n");
				if (f1.choice1 == 1) {
					System.out.println("1");
				} else {
					System.out.println("2");
					
					BufferedReader fileReader = new BufferedReader(f1.getIFileReader());
					
					if (fileReader != null)
					{
						// Initializes a string of the first line in the file
						try {
							inFileLine = fileReader.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						// Loops over each line in the input file
						while (inFileLine != null)
						{
							tokens = new StringTokenizer(inFileLine, "\t\n\r ");
							
							// Loops over each token found on the current line
							while (tokens.hasMoreElements())
							{
								currentToken = tokens.nextToken();
								
								if (isInteger(currentToken))
								{
									totalValue += Integer.parseInt(currentToken);
								}
								else
								{
									int tempIndex = Word.findWordIn(uniqueWords, currentToken, wordIndex);
									if (tempIndex != -1)
									{
										// A word was found to exist already
										uniqueWords[tempIndex].addOccurrence();
									}
									else
									{
										// A new word is added to uniqueWords
										uniqueWords[wordIndex] = new Word(currentToken);
										wordIndex++;
									}
								}
							}
							// Gets the next line
							try {
								inFileLine = fileReader.readLine();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						outputWordData(uniqueWords, wordIndex);
						System.out.println("\nAccumulated value: " + Integer.toString(totalValue));
					}
				}
			}
		}
	}
	static void outputWordData(Word[] words, int wordLimit)
	{
		if (words.length < wordLimit)
			wordLimit = words.length;
		for (int i = 0; i < wordLimit; i++)
		{
			System.out.println(words[i].getWord() + " was found " + Integer.toString(words[i].getCount()) + " times.");
		}
	}
	static boolean isInteger(String theString)
	{
		boolean returnVal = true;
		int i = 0;
		
		if (theString.length() > 0)
		{
			// Checks if it starts with a sign
			if (theString.charAt(0) == '-' || theString.charAt(0) == '+')
			{
				i = 1;
			}
			for (; i < theString.length(); i++)
			{
				if (theString.charAt(i) < 48 | theString.charAt(i) > 57)
				{
					returnVal = false;
					i = theString.length();
				}
			}
		}
		else
			returnVal = false;
		
		return returnVal;
	}
}

class IOFile {
	BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
	File f1in = new File("");
	File f1out = new File("");
	File f2out = new File("");
 
	int choice1;
	String f1outs;
	String f1outs2;
	String f1ins;

	boolean getNames(String args[]) {
		File f1in = new File("");
		File f1out = new File("");
		String temp = " ";
		if (args.length == 2) {
			f1in = new File(args[0]);
			f1out = new File(args[1]);
			f1ins = new String(args[0]);
			f1outs2=args[1];
		} else if (args.length == 1) {
			f1in = new File(args[0]);
			f1ins = new String(args[0]);
			System.out.println("Enter a valid output file path:");
			try {
				temp = get.readLine();
				f1out = new File(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while (!f1in.exists() && !temp.isEmpty()) {
			System.out.println("Enter a valid input file location:");
			try {
				temp = get.readLine();
				f1in = new File(temp);
				f1ins = new String(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while (!f1out.exists() && !temp.isEmpty()) {
			System.out.println("Enter a valid output file location:");
			try {
				temp = get.readLine();
				f1out = new File(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (f1in.exists() && f1out.exists()) {
			f1outs=new String(temp);
			return true;
		}
		return false;
	}

	boolean choices() {
		String choice = new String(" ");
		while (true) {
			System.out
					.println("PRESS 1: To enter a new output file name, backing up the existing output file first.\r\n"
							+ "PRESS 2: To overwrite the existing output file\r\n"
							+ "PRESS 3: To quit the program without opening any files.");
			try {
				choice = get.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (choice.isEmpty() | choice.equals("3"))
				return false;
			if (choice.equals("1")) {
				String temp = new String(" ");
				while (!f2out.exists() && !temp.isEmpty()) {
					System.out.println("Enter a valid output file path:");
					try {
						temp = get.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(temp.isEmpty())
						return false;
					f2out = new File(temp);
					if (f2out.exists()) {
						if(temp.equals(f1outs2)||temp.equals(f1outs))
						{
							System.out.println("File is already open.");
							f2out=new File("");
						} else {
						choice1=Integer.parseInt(choice);
						return true;
						}
					}
				}
				if (choice.equals("2")) {
					choice1 = Integer.parseInt(choice);
					return true;
				}
				System.out.println("Invalid input:");
			} else if(choice.equals("2")) {
				return true;
			}
		}
	}
	FileReader getIFileReader()
	{
		try 
		{
			return new FileReader(f1ins);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
}
class Word 
{
	String theWord;
	int occurrences;
	
	public Word(String newWord)
	{
		theWord = new String(newWord);
		occurrences = 1;
	}
	public int getCount()
	{
		return occurrences;
	}
	public String getWord()
	{
		return theWord;
	}
	public static boolean isAlpha(char character)
	{
		boolean returnVal = false;
		
		// Compares "character" with the ASCII decimal limits for alphabetical characters
		if ((character > 64 && character < 91) || (character > 96 && character < 123))
			returnVal = true;
		
		return returnVal;
	}
	public boolean isWord(String word)
	{
		return theWord == word;
	}
	public void addOccurrence()
	{
		occurrences++;
	}
	/*public void printTo(PrintWriter writer)
	{
		
	}*/
	public static int findWordIn(Word[] list, String word, int lengthOverride)
	{
		int returnVal = -1;
		
		if (list.length < lengthOverride)
			lengthOverride = list.length;
		for (int i = 0; i < lengthOverride; i++)
		{
			if (list[i].getWord().equals(word))
			{
				returnVal = i;
				i = lengthOverride;
			}
		}
		
		return returnVal;
	}
}