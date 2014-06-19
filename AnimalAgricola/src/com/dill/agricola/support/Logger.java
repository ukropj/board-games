package com.dill.agricola.support;

public class Logger {
	// TODO use std logger
	
	public static final boolean LOG_UNDO = false;
	
	public static void logUndo(String message) {
		if (LOG_UNDO) {
			System.out.println("# " + message);
		}
	}

}
