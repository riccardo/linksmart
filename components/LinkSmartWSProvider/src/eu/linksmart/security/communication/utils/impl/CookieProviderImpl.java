package eu.linksmart.security.communication.utils.impl;

import java.util.Timer;
import java.util.TimerTask;

import eu.linksmart.security.communication.utils.CookieProvider;
import eu.linksmart.security.communication.utils.NonceGenerator;

public class CookieProviderImpl implements CookieProvider{
	
	String previousCookie = null;
	String actualCookie = null;
	NonceGenerator nonceGen = null;
	Timer timer = null;
	
	public CookieProviderImpl(){
		nonceGen = NonceGeneratorFactory.getInstance();
		//create timer for creating random cookies
		timer = new Timer(true);
		timer.schedule(new CookieGenerator(), 0, CookieProvider.GENERATION_INTERVAL * 1000);
	}
	
	public boolean checkCookie(String cookie) {
		if((actualCookie != null && actualCookie.equals(cookie)) || (previousCookie != null && previousCookie.equals(cookie))){
			return true;
		}
		return false;
	}

	public String getCookie() {
		return actualCookie;
	}

	class CookieGenerator extends TimerTask{
		public void run(){
			previousCookie = actualCookie;
			actualCookie = nonceGen.getNextNonce();
		}
	}
}
