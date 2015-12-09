package main;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

import InputHandlet.InputHandler;
import helpers.Helper;
import javafx.util.Pair;
import searchSystem.ConstraintChecker;
import searchSystem.OrTree;
import searchSystem.SearchModel;
import searchSystem.SearchProcess;
import types.Course;
import types.FileSections;
import types.Lab;
import types.Schedule;
import types.ScheduleDay;
import types.TimeSlot;
import types.Triple;

public class Main {

	// Section headers
	static final String NAME = "Name:";
	static final String CS = "Course slots:";
	static final String LS = "Lab slots:";
	static final String C = "Courses:";
	static final String L = "Labs:";
	static final String NC = "Not compatible:";
	static final String UW = "Unwanted:";
	static final String P = "Preferences:";
	static final String PAIR = "Pair:";
	static final String PAR = "Partial Assignments:";

	public static void main(String[] args) {
		

		InputHandler ih = new InputHandler(args);
		ih.ReadAndPopulate();
		
		ArrayList<TimeSlot> courseSlots = ih.getCourseSlots();
		ArrayList<TimeSlot> labSlots = ih.getLabSlots();
		ArrayList<Course> courses = ih.getCourses();
		ArrayList<Lab> labs = ih.getLabs();
		ArrayList<Pair<Course, Course>> notCompatible = ih.getNotCompatible();
		ArrayList<Pair<Course, TimeSlot>> unwanted = ih.getUnwanted();
		ArrayList<Triple<TimeSlot, Course, Integer>> preferences = ih.getPreferences();
		ArrayList<Pair<Course, Course>> pairs = ih.getPairs();
		ArrayList<Pair<Course, TimeSlot>> partials = ih.getPartials();
		
		boolean added813 = ih.isAdded813();
		boolean added913 = ih.isAdded913();

		
		Random rand = new Random();
		ConstraintChecker checker = new ConstraintChecker(courseSlots,
				labSlots,
				notCompatible, 
				unwanted, 
				partials, 
				preferences,
				pairs);
		checker.setMinFilledWeight(ih.get_minField());
		checker.setPairWeight(ih.get_pair());
		checker.setPrefWeight(ih.get_pref());
		checker.setSecDiffWeight(ih.get_secdiff());
		checker.set_penCoursemin(ih.get_penCoursemin());
		checker.set_penLabsmin(ih.get_penLabsmin());
		checker.set_penNotPaired(ih.get_penNotPaired());
		checker.set_penSection(ih.get_penSection());
		
		SearchProcess process = new SearchProcess(courses,
				labs, 
				courseSlots, 
				labSlots,
				checker);


		process.runSearchControl();
		

	}




	/**
	 * Prints out a given schedule in the format specified by Jorg. We probably
	 * don't want it in main, we can move it after, i just watned to get it done
	 * 
	 * @param sched
	 *            is a schedule made up of labs and courses that we want to
	 *            print out. It does not have to be a complete schedule
	 */
	public static void displaySchedule(Schedule sched) {

		ArrayList<Course> masterCourses = sched.getCourses();
		Lab cpsc813 = null, cpsc913 = null;

		// Sorts the courses by: Department (alphabetical),
		// then course number (lowest first)
		// then lecture number (lowest first)
		// Can put into it's own method if things get messy
		Collections.sort(masterCourses, new Comparator<Course>() {
			public int compare(Course sched1, Course sched2) {
				if (!sched1.getDepartment().equals(sched2.getDepartment()))
					return sched1.getDepartment().compareToIgnoreCase(sched2.getDepartment());
				else if (!(sched1.getNumber() == sched2.getNumber()))
					return sched1.getNumber() - sched2.getNumber();
				else
					return sched1.getSection() - sched2.getSection();
			}
		});

		// Sorts the labs the same way as courses
		Collections.sort(sched.getLabs(), new Comparator<Lab>() {
			public int compare(Lab sched1, Lab sched2) {
				if (!sched1.getDepartment().equals(sched2.getDepartment()))
					return sched1.getDepartment().compareToIgnoreCase(sched2.getDepartment());
				else if (!(sched1.getNumber() == sched2.getNumber()))
					return sched1.getNumber() - sched2.getNumber();
				else
					return sched1.getSection() - sched2.getSection();
			}
		});

		// For every course in the list, we want to print out it's info (list is
		// already sorted)
		// then we want to print out all it's labs and tutorials
		System.out.print("Eval-Value: " +sched.getEvalRating());
		for (int i = 0; i < masterCourses.size(); i++) 
		{
			
			if(!masterCourses.get(i).getDepartment().equals("CPSC") && 
					masterCourses.get(i-1).getDepartment().equals("CPSC"))
			{
				if(cpsc813 != null)
					System.out.print("\nCPSC 813 \t\t\t: " + cpsc813.getSlot().getDay() + ", " + 
								cpsc813.getSlot().getStartTime());
				if(cpsc913 != null)
					System.out.print("\nCPSC 913 \t\t\t: " + cpsc913.getSlot().getDay() + ", " + 
								cpsc813.getSlot().getStartTime());
			}
			System.out.println();
			String tabFormatting;

			// Prints out the Department, course number and section/lecture num
			tabFormatting = "\t\t";
			System.out.print(masterCourses.get(i).getDepartment() + " ");
			System.out.print(masterCourses.get(i).getNumber() + " ");
			System.out.print("LEC " + String.format("%02d", masterCourses.get(i).getSection()) + " ");

			// Prints out the assigned time slot info. Or says it doesn't have
			// one
			System.out.print("\t\t: ");
			if (masterCourses.get(i).getSlot() == null)
				System.out.print("Not assigned a time slot yet");
			else {
				System.out.print(masterCourses.get(i).getSlot().getDay() + ", ");
				System.out.print(masterCourses.get(i).getSlot().getStartTime());
			}

			
			// Traverse through all the labs in the given scehdule
			for (int j = 0; j < sched.getLabs().size(); j++) 
			{
				Lab workingLab = sched.getLabs().get(j);
				String labDepartment = workingLab.getDepartment();
				int labCourseNum = workingLab.getNumber();
				int labCourseSecNum = workingLab.getLectureNum();
				
				if(labDepartment.equals("CPSC") && labCourseNum==813)
					cpsc813 = workingLab;
				if(labDepartment.equals("CPSC") && labCourseNum==913)
					cpsc913 = workingLab;

				// If this lab has the same course number, same department
				// and same lecture number (or -1 for only one lecture section)
				// it must belong to that course
				if (labCourseNum == masterCourses.get(i).getNumber()
						&& labDepartment.equals(masterCourses.get(i).getDepartment())
						&& (labCourseSecNum == masterCourses.get(i).getSection() || labCourseSecNum == -1)) {
					System.out.print("\n" + workingLab.getDepartment() + " ");
					System.out.print(workingLab.getNumber() + " ");

					// If the course only has more than 1 section we need to say
					// which section this is for
					if ((workingLab.getLectureNum() != -1)) {
						System.out.print("LEC " + String.format("%02d", workingLab.getLectureNum()) + " ");
						tabFormatting = "\t";
					}

					// Parser makes no distinction between Lab and TUT
					System.out.print("LAB/TUT " + String.format("%02d", workingLab.getSection()));

					// Prints out assigned time slot info. If none assigned,
					// print message
					System.out.print(tabFormatting + ": ");
					if (workingLab.getSlot() == null)
						System.out.print("Not assigned a time slot yet");
					else {
						System.out.print(workingLab.getSlot().getDay() + ", ");
						System.out.print(workingLab.getSlot().getStartTime());
					}
				}
			}
			
		}
		
		/*
		Lab tempLab_1 = null, tempLab_2 = null;
		
		for(int i = 0;i<sched.getLabs().size();i++)
		{
			if(sched.getLabs().get(i).getDepartment().equals("CPSC") &&
					sched.getLabs().get(i).getNumber() == 813 )
			{
				tempLab_1 = sched.getLabs().get(i);
			}
			
			if(sched.getLabs().get(i).getDepartment().equals("CPSC") &&
					sched.getLabs().get(i).getNumber() == 913 )
			{
				tempLab_2 = sched.getLabs().get(i);
			}
		}
		

		
		//Extra stuff for CPSC 813 and 913 cause they are really annoying to deal with...
		if(tempLab_1 != null)
		{
			System.out.print("\n" + tempLab_1.getDepartment() + " ");
			System.out.print(tempLab_1.getNumber() + " ");
			System.out.print("LAB/TUT ");
			System.out.print(String.format("%02d", tempLab_1.getSection()) + " ");
			if(tempLab_1.getSlot() != null)
			{
				System.out.print("\t\t: " + tempLab_1.getSlot().getDay() + ", ");
				System.out.print(tempLab_1.getSlot().getStartTime());
			}
		}
		if(tempLab_2 != null)
		{
			System.out.print("\n" + tempLab_2.getDepartment() + " ");
			System.out.print(tempLab_2.getNumber() + " ");
			System.out.print("LAB/TUT ");
			System.out.print(String.format("%02d", tempLab_2.getSection()) + " ");
			if(tempLab_2.getSlot() != null)
			{
				System.out.print("\t\t: " + tempLab_2.getSlot().getDay() + ", ");
				System.out.print(tempLab_2.getSlot().getStartTime());
			}
		}
		*/
		
	}
}
