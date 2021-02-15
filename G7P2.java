
/*
 * Program2.java
 * Daniel Hentosz and Nathaniel Dehart
 * Technical Computing Using Java || CET 350 
 * hen3883@calu.edu || deh5850@calu.edu
 * GROUP 7
 */
import java.io.*;
import java.util.StringTokenizer;

class G7P2 {
	public static void main(String[] args) {
		int i = 0;
		int count = 0;
		int sum = 0;
		String word = null;
		String[] ioFilePaths = new String[2];
		Word words[] = new Word[100];
		StringTokenizer inline;
		BufferedReader infile;
		PrintWriter outfile;
		File in;
		IOFile f1 = new IOFile();
		String temp = new String("");
		if (f1.getnames(args, ioFilePaths)) {
			// They entered a valid input and output file path
			in = new File(ioFilePaths[0]);
			
			try {
				infile = new BufferedReader(new FileReader(in));
				while ((word = infile.readLine()) != null) {
					// read every line
					inline = new StringTokenizer(word, "~`!@#$%^&*()_+=[{]}\\|\n\t\r,<>./?;:\" ");
					// each line becomes several tokens
					while (inline.hasMoreTokens()) {
						word = inline.nextToken().toString().toLowerCase();
						// each token is made a string, so we can use our String methods
						temp = "0";
						if (f1.good(word)) {
							// Checks if the string is just - or '("----" or "''''''")
							if (word.length() > 1) {
								// So we don't get an index out of bounds exception when trying see see slot 2
								while (word.charAt(1) == '-') {
									word = word.substring(1);
								}
							}
							try {
								sum = sum + Integer.parseInt(word);
							} catch (NumberFormatException e) {
								// We know its either a word or numbers followed by a word(123cat)
								char c = word.charAt(0);
								while (word.charAt(0) == '-' | word.charAt(0) == '\'' | Character.isDigit(c)) {
									if (Character.isDigit(c)) {
										// If its number followed by a word(123cat)
										temp = temp + Character.toString(c);
									}
									word = word.substring(1);
									c = word.charAt(0);
								}
								sum = sum + Integer.parseInt(temp);
								// Creating our new Word object(if its needed)
								if (count == 0) {
									words[0] = new Word(word);
									count++;
								} else {
									// i will be -1 if the word is not already in the array
									i = words[0].findWord(words, word, count);
									if (i == -1) {
										words[count] = new Word(word);
										count++;
									} else {
										words[i].addOne();
										;
									}
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				// Writing to the file
				outfile = new PrintWriter(new File(ioFilePaths[1]));
				for (int j = 0; j < count; j++) {
					outfile.write("WORD:    " + words[j].getName());
					outfile.write("\nCOUNT:    ");
					outfile.print(words[j].getCount());
					outfile.write("\n");
				}
				outfile.write("TOTAL UNIQUE WORDS:    ");
				outfile.print(count);
				outfile.write("\nSUM OF INTEGERS:    ");
				outfile.print(sum);
				outfile.close();
				System.out.println("\nSuccessfully wrote data to: " + ioFilePaths[1]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class Word {
	private String name;
	private int count;

	// Constructor
	Word(String w) {
		name = w;
		count = 1;
	}

	// To display the count
	int getCount() {
		return count;
	}

	// To increment the count
	void addOne() {
		count++;
	}

	// To display the unique word
	String getName() {
		return name;
	}

	// To see if the word is already in the array
	// Returns -1 if not, otherwise returns the index of which the array exists
	int findWord(Word[] list, String w, int n) {
		for (int i = 0; i < n; i++) {
			if (list[i].name.equals(w)) {
				return i;
			}
		}
		return -1;
	}
}

class IOFile {
	// Returns false if the user chose not to enter file names
	boolean getnames(String args[], String[] ioFilePaths) {
		BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
		File in = new File("");
		File out = new File("");
		boolean quitting = false;
		String temp = new String(" ");
		// Seeing how many command line arguments they entered, if any at all
		switch (args.length) {
		case 1:
			in = new File(args[0]);
			ioFilePaths[0] = args[0];
			break;
		case 2:
			in = new File(args[0]);
			out = new File(args[1]);
			ioFilePaths[0] = args[0];
			ioFilePaths[1] = args[1];
			break;
		default:
			break;
		}
		// While the input file path is incorrect
		while (!in.exists() && !quitting) {
			System.out.println("\nEnter a valid input file path:");
			try {
				System.out.print("> ");
				temp = get.readLine();
				// update our array
				ioFilePaths[0] = new String(temp);
				in = new File(temp);
				if (!in.exists())
				{
					System.out.println("\nThe entered file path does not exist."
							+ "\nEnter 1 to try again"
							+ "\nEnter anything else to exit");
					System.out.print("> ");
					temp = get.readLine();
					if (!temp.equals("1"))
					{
						quitting = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// While the output file path is incorrect
		while (!out.exists() && !quitting) {
			System.out.println("\nEnter an output file path:");
			try {
				System.out.print("> ");
				temp = get.readLine();
				// update our array
				ioFilePaths[1] = new String(temp);
				out = new File(temp);
				// Checks if a file with the output file name already exists
				if (!out.createNewFile())
				{
					System.out.println("\nAn output file with that name already exists."
							+ "\nEnter 1 to overwrite it"
							+ "\nEnter 2 to back up the original"
							+ "\nEnter anything else to exit");
					System.out.print("> ");
					temp = get.readLine();
					if (temp.equals("1"))
					{
						out.delete();
						out.createNewFile();
					}
					else if (temp.equals("2"))
					{
						backupFile(out);
						out.createNewFile();
					}
					else 
					{
						quitting = true;
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return !quitting;
	}
	
	// Changes the name of "theFile" to it's current name with "BACKUP" appended to the end
	// Keeps the directory and extension of "theFile" intact
	void backupFile(File theFile)
	{
		File newFile = new File(getFileDirectory(theFile).concat(getFileNameNoExtension(theFile)).concat("BACKUP").concat(getFileExtension(theFile)));
		// Checks if a file matching this backup name already exists
		if (newFile.exists())
			newFile.delete();
		theFile.renameTo(newFile);
	}
	
	// Returns a String containing the extension of "theFile"
	String getFileExtension(File theFile)
	{
		return theFile.getName().substring(theFile.getName().indexOf('.'), theFile.getName().length());
	}
	
	// Returns a String containing the name without the extension of "theFile"
	String getFileNameNoExtension(File theFile)
	{
		return theFile.getName().substring(0, theFile.getName().indexOf('.'));
	}
	
	// Returns a String containing the directory of "theFile"
	String getFileDirectory(File theFile)
	{
		return theFile.getPath().substring(0, theFile.getPath().length() - theFile.getName().length());
	}

	// Check if the string is just - or '
	// We need this because we would get an ArrayIndexOutfBoundsException if we
	// didn't have it
	boolean good(String s) {
		char c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (Character.isLetter(c) | Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}
}