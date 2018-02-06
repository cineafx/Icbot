package com.cineafx.icbot.bot.messageHandler;

import java.util.concurrent.TimeUnit;

public class SpecialCommandHandler {
	private long startuptime;
	
	public SpecialCommandHandler() {
		startuptime = System.currentTimeMillis();
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
	
}
