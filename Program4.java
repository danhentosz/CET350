/*
.Program4, G.U.I. Bounce Program                   (Main.java).
.Created By:                                             .
- Daniel Hentosz    (HEN3883@calu.edu),                 .
- Scott Trunzo       (TRU1931@calu.edu),                 .
- Nathaniel Dehart   (DEH5850@calu.edu).                 .
.Last Revised: March 16th, 2021.                (3/16/2021).
.Written for Technical Computing Using Java (CET-350-R01).
Description:
	Makes use of java's <awt> library to create a GUI,
		- this GUI controls a object which will bounce around the frame.
		
	The user can:
		- change the object to a circle or a square,
		- Select a speed at which the object move's,
		- change the size of the object,
		- select rather or not the objects previous location is shown(tail or no tail),
		-and chose rather the object moves, or does not move.
		
*/

import Bounce.Bounce;

public class Program4 {
	public static void main(String[] args) {
		new Bounce();
	}
}