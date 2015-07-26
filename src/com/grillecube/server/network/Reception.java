package com.grillecube.server.network;

import java.io.BufferedReader;
import java.io.IOException;


public class Reception implements Runnable {

	private BufferedReader in;
	private String message = null, login = null;
	
	public Reception(BufferedReader in, String login){
		
		this.in = in;
		this.login = login;
	}
	
	public void run() {
		
		while(true){
	        try {
	        	
			message = in.readLine();
			System.out.println(login+" : "+message);
			
		    } catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}

}