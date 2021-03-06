package com.cineafx.icbot.bot.messageHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCommandHandler {
	private long startuptime;
	
	public SpecialCommandHandler() {
		System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
		startuptime = System.currentTimeMillis();
	}
	
	/**
	 * Returns a random integer between <b>min</b> and <b>max</b>
	 * @param min
	 * @param ma
	 * @return randomInt
	 */
	public String randomInt(String min, String max) {
		//If input isn't Integer number return error
		try {
			int minInt = Integer.parseInt(min);
			int maxInt = Integer.parseInt(max);
			//Only if max is larger or equals to min it can return a random number
			if (maxInt >= minInt) {
				return String.valueOf(ThreadLocalRandom.current().nextInt(minInt, maxInt + 1));
			} else {
				return "ERROR: min is larger than max";
			}
		} catch (NumberFormatException nfe) {
			return "ERROR: NumberFormat";
		}
	}
	
	/**
	 * Returns a random double between <b>min</b> and <b>max</b> with <b>digits</b> amount of decimal digits
	 * @param min
	 * @param max
	 * @param digits
	 * @return randomDouble
	 */
	public String randomDouble(String min, String max, String digits) {
		//If input isn't Integer number return error
		try {
			double minDouble = Double.parseDouble(min);
			double maxDouble = Double.parseDouble(max);
			double digitsDouble = Double.parseDouble(digits);
			
			//Only if max is larger or equals to min it can return a random number
			if (maxDouble >= minDouble) {
				double randomDouble = ThreadLocalRandom.current().nextDouble(minDouble, maxDouble + 1);
				
				randomDouble = Math.round(randomDouble * Math.pow(10, digitsDouble));
				randomDouble = randomDouble / Math.pow(10, digitsDouble);
				return String.valueOf(randomDouble);
			} else {
				return "ERROR: mix is larger than max";
			}
		} catch (NumberFormatException nfe) {
			return "ERROR: NumberFormat";
		}
	}
	
	public String botUpTime() {
		long millis = (System.currentTimeMillis() - startuptime);
		String uptime;
		
		//Calcuate "full" units ... makes 330 seconds into 5 minutes, 30 seconds
		long days = TimeUnit.MILLISECONDS.toDays(millis);
		long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
		
		//Only shows the required amount of units (leaves out units that are empty)
		if (days == 0) {
			if (hours == 0) {
				if (minutes == 0) {
					uptime = String.format("%d sec", seconds);
				} else {
					uptime = String.format("%d min, %d sec", minutes, seconds);
				}
			} else {
				uptime = String.format("%d hours, %d min, %d sec", hours, minutes, seconds);
			}
		} else {
			uptime = String.format("%d days, %d hours, %d min, %d sec", days, hours, minutes, seconds);
		}
		return uptime;
	}

	public String getFromURL(String inputUrl) {
		
		if (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://")) {
			inputUrl = "https://" + inputUrl;
		}
		
		try {
		    URI uri = new URI(inputUrl);
		    
		    if (uri.getHost() == null) {
		    	return "Not a valid URL";
		    }
		    
		    //doesn't end with .html 
		    Matcher matcher = Pattern.compile("(?i)((.*)\\.([A-Z]{1,6}))$").matcher(uri.getPath());
		    if(!matcher.find() && !(inputUrl.charAt(inputUrl.length()-1) == '/')) {

		    	inputUrl += "/";
		    }
		    
		    URL url = new URL(inputUrl);
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

	        String inputLine;
			String returnString = "";
	        while ((inputLine = in.readLine()) != null) {
	            returnString += inputLine;
	        }
	        in.close();

			return returnString;
			
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "ERROR";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "Not a valid URL";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "HTTP ERROR: 404";
		} catch (IOException e) {
			System.out.println(e);
			if (e.toString().contains("Server returned HTTP response code")) {
				return "HTTP ERROR: " + e.toString().substring(57, 60);
			}
			//e.printStackTrace();
			
			return "ERROR";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}	
	}
	
}
