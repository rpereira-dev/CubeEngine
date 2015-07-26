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
		String cmd, droite;
		
		while(true){
	        try {
	        	
				message = in.readLine();
				System.out.println(login + " à envoyé : " + message);
				
				cmd = message.substring(0, 3);
				droite = message.substring(3);
				
				if (cmd.equals("MSG")) {
					System.out.println("Commande MSG RECU");
				} else if (cmd.equals("LOL")) {
					System.out.println("Commande LOL RECU");
				}
			
		    } catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}

}