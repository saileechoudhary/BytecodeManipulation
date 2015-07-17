//package be.ac.ulg.montefiore.run.jahmm.io.HmmReader;

import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfIntegerReader;
import be.ac.ulg.montefiore.run.jahmm.io.HmmReader;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.ViterbiCalculator;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.HmmReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationIntegerReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationSequencesReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationVectorReader;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfIntegerReader;
import be.ac.ulg.montefiore.run.jahmm.io.OpdfReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HmmTestingSailee
{
//initHmm = readHmm("C:\\Users\\Ashwin\\Dropbox\\Project\\smoothing\\smoothed_temp");

	
	public static Hmm readHmm(String fileName) throws FileNotFoundException, IOException, FileFormatException {
        FileReader fr = new FileReader(fileName);
        OpdfIntegerReader oir = new OpdfIntegerReader();
        Hmm model = null;
        try {
        	OpdfReader<? extends Opdf<Observation>> opdfReader;
            model = HmmReader.read(fr, oir);
            } catch (FileFormatException e) {
            System.err.println("Caught error trying to build HMM: " + e);
            throw e;
           }
        return model;
    }
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, FileFormatException {
		
		File file = new File("benign_against_obfus_st1_m1.txt"); //Your file
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
		//System.out.println("This goes to out.txt");
		
		HmmTestingSailee h = new HmmTestingSailee();
		Hmm init = h.readHmm("C:\\Sailee\\Projects\\Data2\\obfus_hmm\\smoothed-st2-1.hmm");
		//System.out.println(init);
		
		
		
		//Scanner inFile1 = new Scanner(new File("C:\\Sailee\\Projects\\Data\\modified_RecursionExampleDirectory.class.seq")).useDelimiter(";\\s*");
		
		File dir = new File("C:\\Sailee\\Projects\\Data\\2");
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		      // Do something with child
		    	System.out.println("Files"+child);
		    	Reader reader = new FileReader(child);
				
				List<ObservationInteger> v = ObservationSequencesReader.readSequence(new ObservationIntegerReader(), reader);
				reader.close();
				
				//System.out.println("List "+v);
				System.out.println("Number of sequences = "+v.size());
				double probab = init.lnProbability(v);
				System.out.println("ln Probability is\t"+probab);
				double prob = (probab/v.size());
		        System.out.println("Probablity is"+prob);
		       }
				
		       
				
		    }
		  }


		
		
//		String i = "C:\\Sailee\\Projects\\Data\\AirlineProblem.class.seq";
//		Reader reader = new FileReader(i);
//		
//		List<ObservationInteger> v = ObservationSequencesReader.readSequence(new ObservationIntegerReader(), reader);
//		reader.close();
//		
//		System.out.println("List "+v);
//		double probab = init.lnProbability(v);
//		System.out.println("Probability is\t"+probab);
//		
	}
		
