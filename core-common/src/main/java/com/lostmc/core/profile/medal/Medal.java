package com.lostmc.core.profile.medal;

public enum Medal {

	PEACE_LOVE('a', "✌"), 
	HEART('4', "❤"),
	UPSET_HEART('4', "❥"),
	SMILE('e', "ツ"),
	TOXIC('e', "☣"),
	RAY('6', "⚡"),
	MUSIC('5', "♫"),
	COFFEE('8', "☕"),
	RADIOACTIVE('6', "☢"),
	SKELETON('7', "☠"),
	UMBRELLA('d', "☂"),
	HALLOWEEN_CROSS('f', "✞"),
	CROSS('7', "✠"),
	WRONG('c', "✘"),
	CORRECT('a', "✔"),
	YIN_YANG('f', "☯"),
	BATTLEBITS('9', "⚔"),
	STARRY('b', "❄"),
	SIGNAL('2', "♻"),
	STUDENT('e', "✍"),
	EMAIL('e', "✉"),
	F('f', "Ⓕ");
	
	private char color;
	private String symbol;
	
	Medal(char color, String symbol) {
		this.color = color;
		this.symbol = symbol;
	}
	
	public String getColor() {	
		return "§" + color;
	}
	
	public String getSymbol() {
		if (symbol.length() > 1)
			symbol = symbol.substring(0, 1);
		return symbol.toUpperCase();
	}
	
	public static Medal fromString(String name) {
		try {
			return Medal.valueOf(name.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}
}
