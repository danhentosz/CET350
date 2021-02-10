
/*
 * Program2.java
 * Daniel Hentosz and Nathaniel Dehart
 * Technical Computing Using Java || CET 350 
 * hen3883@calu.edu || deh5850@calu.edu
 * GROUP 7
 */
import java.io.*;

class Program2 {
	public static void main(String[] args) {
		IOFile f1 = new IOFile();
		if (f1.getNames(args)) {
			if (f1.choices()) {
				System.out.println("yay. we have valid shitttt");
				if (f1.choice1 == 1) {
					System.out.println("1");
				} else {
					System.out.println("2");
				}
			}
		}
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

	boolean getNames(String args[]) {
		File f1in = new File("");
		File f1out = new File("");
		String temp = " ";
		if (args.length == 2) {
			f1in = new File(args[0]);
			f1out = new File(args[1]);
			f1outs2=args[1];
		} else if (args.length == 1) {
			f1in = new File(args[0]);
			System.out.println("Enter a valid output file path:");
			try {
				temp = get.readLine();
				f1out = new File(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while (!f1in.exists() && !temp.isEmpty()) {
			System.out.println("Enter a vlid input file location:");
			try {
				temp = get.readLine();
				f1in = new File(temp);
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
}