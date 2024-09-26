package com.lostmc.core.profile.password;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PasswordLevel {

	WEAK(0), 
	MEDIUM(5), 
	STRONG(10);

	private final int level;

	public static PasswordLevel fromPassword(String input) {
		int level = 0;

		if (input.length() < 8)
			return WEAK;
		else if (input.length() >= 10)
			level += 2;
		else
			level += 1;

		if (input.matches("(?=.*[0-9]).*"))
			level += 2;
		
		if (input.matches("(?=.*[a-z]).*"))
			level += 2;

		if (input.matches("(?=.*[A-Z]).*"))
			level += 2;

		if (input.matches("(?=.*[~!@#$%^&*()_-]).*"))
			level += 2;
		
		if (level >= STRONG.level) {
			return STRONG;
		} else if (level >= MEDIUM.level) {
			return MEDIUM;
		} else {
			return WEAK;
		}
	}
}
