/*
 * basic command
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */

package inroids.common;

/**
 * Defines a Command. It's input, output and exit status.
 * @author Sealy
 */
public class Command {
	//private static final String strTag="Inroids";
	/** Reboot the system to bootloader */
	//public static final Command REBOOT = new Command("reboot");
	/** Reboot the system to recovery */
	//public static final Command REBOOT_RECOVERY = new Command("reboot recovery");
	/** Poweroff the system, non graceful implementation */
	//public static final Command POWEROFF = new Command("reboot -p");

	/** The input and parameters of the command. */
	public String sInput;
	/** The output of the command, one string per line */
	public String[] sOutput;
	/** The exit status of the command */
	public int iExitStatus;
	
	/**
	 * Constructs a new Command.
	 * @param sIn The input and parameters of the command.
	 */
	public Command(String sIn) {
		if (sIn == null)
			throw new NullPointerException("Cannot use a null input for the command.");
		this.sInput = sIn;
	}

	/**
	 * split String
	 */
	public String toString() {
		return sInput.split(" ")[0];
	}
}