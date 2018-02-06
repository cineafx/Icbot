package com.cineafx.icbot.bot.messageHandler;

import java.util.concurrent.TimeUnit;

public class SpecialCommandHandler {
	private long startuptime;
	
	public SpecialCommandHandler() {
		startuptime = System.currentTimeMillis();
	}
	
	public String botUpTime() {
		long millis = (System.currentTimeMillis() - startuptime);
		String uptime = String.format("%d days, %d hours, %d min, %d sec", TimeUnit.MILLISECONDS.toDays(millis),
				TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		return uptime;
	}
	
}
