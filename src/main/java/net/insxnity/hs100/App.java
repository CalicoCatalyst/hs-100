package net.insxnity.hs100;

import java.io.IOException;

public class App {

	public static void main(String[] args) throws IOException {
		
		HS100 plug = new HS100("192.168.1.103");
		
		if(plug.isOn()) {
			plug.switchOff();
		} else {
			plug.switchOn();
		}

	}
	
}
