
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
		int i = 0;
		int count = 0;
		int sum = 0;
		String word = null;
		String[] ionames = new String[3];
		Word words[] = new Word[100];
		StringTokenizer inline;
		BufferedReader infile;
		PrintWriter outfile;
		File in;
		File out;
		IOFile f1 = new IOFile();
		if (f1.getnames(args, ionames)) {
			in = new File(ionames[0]);
			out = new File(ionames[1]);
			ionames[2]=null;
			if (f1.choice(ionames)) {
				try {
					infile = new BufferedReader(new FileReader(in));
					while ((word = infile.readLine()) != null) {
						inline = new StringTokenizer(word, "~`!@#$%^&*()_=+><.,\n\r\t:;\" ");
						while (inline.hasMoreTokens()) {
							word = inline.nextToken().toString().toLowerCase();
							if (!(word.length() < 2)) {
								while (word.charAt(1) == '-') {
									word = word.substring(1);
								}
								try {
									sum = sum + Integer.parseInt(word);
								} catch (NumberFormatException e) {
									char c = word.charAt(0);
									String temp = new String("0");
									while (word.charAt(0) == '-' | word.charAt(0) == '\'' | Character.isDigit(c)) {
										if (Character.isDigit(c)) {
											temp = temp + Character.toString(c);
										}
										word = word.substring(1);
										c = word.charAt(0);
									}
									sum = sum + Integer.valueOf(temp);
									if (words[0] == null) {
										words[0] = new Word(word);
										count++;
									} else {
										i = words[0].findWord(words, word, count);
										if (i == -1) {
											words[count] = new Word(word);
											count++;
										} else {
											words[i].addOne();
										}
									}
								}
							} else {
								char c = word.charAt(0);
								if (!Character.isDigit(c) && c != '-' && c != '\'') {
									if (words[0] == null) {
										words[0] = new Word(word);
										count++;
									} else {
										i = words[0].findWord(words, word, count);
										if (i == -1) {
											words[count] = new Word(word);
										} else {
											words[i].addOne();
										}
									}
								} else {
									sum = sum + Integer.parseInt(word);
								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (ionames[2] != null) {
						outfile = new PrintWriter(new File(ionames[2]));
						for (int j = 0; j < count; j++) {
							outfile.write("WORD:    ");
							outfile.write(words[j].name);
							outfile.write("\nCOUNT:    ");
							outfile.print(words[j].count);
							outfile.write("\n");
						}
						outfile.write("TOTAL UNIQUE WORDS:    ");
						outfile.print(count);
						outfile.write("\nSUM OF INTEGERS:    ");
						outfile.print(sum);
					} else {
						outfile = new PrintWriter(new File(ionames[1]));
						for (int j = 0; j < count; j++) {
							outfile.write("WORD:    ");
							outfile.write(words[j].name);
							outfile.write("\nCOUNT    :");
							outfile.print(words[j].count);
							outfile.write("\n");
						}
						outfile.write("TOTAL UNIQUE WORDS:    ");
						outfile.print(count);
						outfile.write("\nSUM OF INTEGERS:    ");
						outfile.print(sum);
					}
					outfile.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

class Word {
	String name;
	int count;

	Word(String w) {
		name = w;
		count = 1;
	}

	int getCount() {
		return count;
	}

	void addOne() {
		count++;
	}

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
	// Returns true if both input and output file names are present
	boolean getnames(String args[], String[] ionames) {
		BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
		File in = new File("");
		File out = new File("");
		String temp = new String(" ");
		switch (args.length) {
		case 1:
			in = new File(args[0]);
			ionames[0] = args[0];
			break;
		case 2:
			in = new File(args[0]);
			out = new File(args[1]);
			ionames[0] = args[0];
			ionames[1] = args[1];
			break;
		default:
			break;
		}
		while (!in.exists() && !temp.isEmpty()) {
			System.out.println("Enter a valid input file path:");
			try {
				temp = get.readLine();
				ionames[0] = temp;
				in = new File(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while (!out.exists() && !temp.isEmpty()) {
			System.out.println("Enter a valid output file path:");
			try {
				temp = get.readLine();
				ionames[1] = temp;
				out = new File(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (in.exists() && out.exists()) {
			return true;
		}
		return false;
	}

	void fileBackup(String name, String ext) {
		name.substring(name.lastIndexOf('/'), name.lastIndexOf('.'));
	}

	String fileName(String name) {
		return null;
	}

	BufferedReader openin(String name) {
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(name));
			return in;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	int isInt(String w, int sum) {
		while (w.charAt(1) == '-' | w.charAt(1) == '\'') {
			w = w.substring(1);
		}
		try {
			sum = sum + Integer.parseInt(w);
			return sum;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	boolean choice(String ionames[]) {
		File f = new File("");
		String choice=" ";
		String choice2=" ";
		BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
		while(!choice.isEmpty()) {
		System.out.println("PRESS 1: To backup the exitsing output file");
		System.out.println("PRESS 2: To overwrite the exitsing output file");
		System.out.println("PRESS 3: To exit"); 
		try {
			choice=get.readLine();
			if(choice.equals("1")) {
				while(!choice2.isEmpty()&&!f.exists()) {
					System.out.println("Enter a new valid output file name:");
					choice2=get.readLine();
					if(choice2.isEmpty())
						return false;
					f=new File(choice2);
				}
				if(f.exists()) {
					ionames[2]=choice2;
					return true;
				}
			} else if(choice.equals("2")) {
				return true;
			} else if(choice.equals("3")) {
				return false;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		}
		return false;
	}
}