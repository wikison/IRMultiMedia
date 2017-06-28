/*
 * run command in shell 
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */

package inroids.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class includes all features required by a shell on the Android platform.
 * 
 * @author Sealy
 * 
 */
public class Shell {
	private static final String sTag="IRLibrary";
	/** The default shell interpreter */
	private static final String sSu_Command = "su";
	/** Use relative commands because we're editing the path */
	private static final String ssAndroid_Shell = "sh";

	private static final String[] sPaths = { "/system/bin", "/system/xbin","/sbin", "/vendor/bin", "/system/sbin", "/system/bin/failsafe/","/data/local/" };

	/** Used to determine when a command has ended */
	private static final String ssCommand_end = "__END_SHELL_COMMAND";
	/**
	 * Exit status when the shell outputted something that was not a string nor
	 * a blank output
	 */
	private static final int iOther_Exit_Status = -1337;

	protected ProcessBuilder pBuilder;
	protected Process pProcess;
	public BufferedReader bReader;
	public BufferedWriter bWriter;

	/** Whether this shell is logging each command or not. */
	public boolean isMyLog = true;

	/**
	 * A new instance of {@link Shell} with the default android shell
	 * interpreter.
	 * 
	 */
	public Shell() {
		this(ssAndroid_Shell);
	}

	/***
	 * A new instance of {@link Shell}.
	 * 
	 * @param interpreter
	 *            The interpreter to use other than the default one.
	 */
	public Shell(String interpreter) {
		pBuilder = new ProcessBuilder(new String[] { interpreter });
		try {
			pBuilder.redirectErrorStream(true);
			pProcess = pBuilder.start();
			bReader = new BufferedReader(new InputStreamReader(
					pProcess.getInputStream()));
			bWriter = new BufferedWriter(new OutputStreamWriter(
					pProcess.getOutputStream()));

			// Prepare the path
			String oldPath = exec("echo \"$PATH\"").sOutput[0];
			MyLog.d(sTag,"Original shell path: " + oldPath);
			StringBuilder newPath = new StringBuilder();
			for (String path : sPaths) {
				if (!oldPath.contains(path)) {
					MyLog.d(sTag,"Adding new path " + path);
					newPath.append(':').append(path);
				}
			}
			exec("PATH=" + "$PATH" + newPath);
		} catch (IOException ioe) {
			handleWriteOnClosedShell(ioe);
		}
	}

	private void handleWriteOnClosedShell(IOException e) {		
		try {
			close();
		} catch (IOException f) {
			MyLog.e(sTag,"Attempted to write on a closed shell"+e.toString());
		}
	}

	/**
	 * Internally write the command to the shell and get it's output.
	 * 
	 * @param command
	 * @return
	 */
	private Command writeCommand(Command command, boolean isL) {
		if (command == null)
			throw new NullPointerException("Cannot execute a null command");
		try {
			this.bWriter.write(command.sInput + "\n");
			this.bWriter.write("echo \"" + ssCommand_end + " $?\"\n");
			this.bWriter.flush();

			ArrayList<String> output = new ArrayList<String>();
			String line = null;
			while ((line = this.bReader.readLine()) != null
					&& !line.startsWith(ssCommand_end)) {
				output.add(line);
			}
			command.sOutput = output.toArray(new String[output.size()]);
			try {
				command.iExitStatus = Integer.parseInt(line.split(" ")[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				// Empty error status is assumed to be OK.
				MyLog.w(sTag,"Command returned an empty exit status");
				command.iExitStatus = 0;
			} catch (Exception e) {
				command.iExitStatus = iOther_Exit_Status;
			}
		} catch (IOException ioe) {
			handleWriteOnClosedShell(ioe);
		}

		// MyLogging:
		if (isL && this.isMyLog) {
			MyLog.i(sTag,"Executing command: " + command);
			MyLog.d(sTag," > input: " + command.sInput);
			if (command.sOutput.length > 0) {
				MyLog.d(sTag," > output:");
				for (String outLine : command.sOutput) {
					MyLog.d(sTag," - " + outLine);
				}
			}
			if (command.iExitStatus == 0) {
				MyLog.d(sTag," > exit: " + command.iExitStatus);
			} else {
				MyLog.w(sTag," > exit: " + command.iExitStatus);
			}
		}
		return command;
	}

	/**
	 * A convenience method to execute a command without logging. This is used
	 * for the "echo $?", to get the error state.
	 * 
	 * @param command
	 *            The command to execute.
	 * @return The command with it's output and iExitStatus set.
	 */
	private Command exec(String command) {
		return writeCommand(new Command(command), false);
	}

	/**
	 * Execute a single command on the shell and get it's output.
	 * 
	 * @param command
	 *            The command to be executed.
	 * @return The output of the command.
	 */
	public Command execute(Command command) {
		return writeCommand(command, true);
	}

	/**
	 * Execute multiple commands on the shell and get their outputs.
	 * 
	 * @param commands
	 *            The commands to be executed.
	 * @return The output of the commands.
	 */

	public Command[] execute(Command... commands) {
		for (Command c : commands) {
			writeCommand(c, true);
		}
		return commands;
	}

	/**
	 * Execute a single command on the shell and get it's output.
	 * 
	 * @param command
	 *            The command to be executed.
	 * @return The output of the command.
	 */
	public Command execute(String command) {
		return writeCommand(new Command(command), true);
	}

	/**
	 * Execute multiple commands on the shell and get their outputs.
	 * 
	 * @param commands
	 *            The commands to be executed.
	 * @return The output of the commands.
	 */
	public Command[] execute(String... commands) {
		Command[] out = new Command[commands.length];
		for (int i = 0; i < commands.length; i++) {
			out[i] = writeCommand(new Command(commands[i]), true);
		}
		return out;
	}

	/**
	 * Attempt to get root if it hasn't root already
	 * 
	 * @return This shell for fluent API concatenation
	 * @see #isRootShell()
	 */
	public Shell getRoot() {
		if (!isRootShell()) {
			MyLog.i(sTag,"Getting root");
			Command c = execute(sSu_Command);
			// We can also check if the exit status is 0 from the superuser app,
			// but this is not reliable because we can't trust the writer of the
			// superuser app.
			// if (c.iExitStatus == 0) { // we have su }
			MyLog.v(sTag,"Su command exit value:" + c.iExitStatus);
			if (isRootShell()) {
				MyLog.i(sTag,"Got root");
			} else {
				MyLog.w(sTag,"Couldn't get root");
			}

		} else {
			MyLog.w(sTag,"Attempted to get root on this shell, but it was already root.");
		}
		return this;
	}

	/**
	 * Will check if this shell has root permissions.
	 * 
	 * @return true if root, false if not
	 * @see #getRoot()
	 */
	public boolean isRootShell() {
		// Whoami is not used because it doesn't come with all toolboxes.
		return (getUID() == 0);
	}

	/**
	 * Get the current UID for this shell. If the "id" command is not available
	 * or gives wrong output, it will return -1
	 * 
	 * @return The UID or -1
	 */
	public int getUID() {

		String idOutput = exec("id").sOutput[0];
		String bbId = exec("busybox id").sOutput[0];
		String tbId = exec("toolbox id").sOutput[0];
		// check for both the normal id and the busybox command.
		Matcher match = Pattern.compile("uid=([0-9]*)").matcher(
				idOutput + "|" + bbId + "|" + tbId);
		// uid = null when nothing matched (i.e.: "id" command not
		// found)
		// else the id will be a number, so check for 0.
		if (match.find()) {
			String uid = match.group(1);
			return Integer.parseInt(uid);
		}
		return -1;
	}

	/**
	 * Cleans up the shell. Always invoke this method before losing reference to
	 * the shell to avoid memory leaks
	 */
	public void close() throws IOException {
		// Exit the shell before closing it
		bWriter.write("\n");
		try {
			for (int i = 0; i < 5; i++) {
				bWriter.write("exit\n");
			}
		} catch (IOException e) {
		}
		bReader.close();
		bWriter.close();
		pProcess.destroy();
	}

}
