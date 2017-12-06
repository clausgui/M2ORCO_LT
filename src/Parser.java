import java.io.File;
import java.util.Scanner;

import iofiles.InputFile;

public class Parser {
	
	private String name;
	private InputFile file;

/**
 * A constructor for the parser
 * @param name
 */
	public Parser(String name) {

		this.name = name;
		this.file = new InputFile();

	}
	
/**
 * opens the file we want to parser.
 */
	public void openFile() {

		this.file.open(name+".txt");
		
	}
	
/**
 * close an opened file.
 */
	public void closeFile() {
		
		this.file.close();
	}

	
/**
 * @return an instance created from the file
 */
	public Heuristic createSolver() {
		
		openFile();
		
		int numberOfSites = file.readInt();
		double[][] distanceMatrix = new double[numberOfSites][numberOfSites];

		for(int i=0;i<numberOfSites;i++) {
			for(int j=0;j<numberOfSites;j++) {
				distanceMatrix[i][j] = file.readDouble();
			}
		}
		
		closeFile();
		
		Heuristic h = new Heuristic(distanceMatrix, numberOfSites);
		
		return h;
	}
}
