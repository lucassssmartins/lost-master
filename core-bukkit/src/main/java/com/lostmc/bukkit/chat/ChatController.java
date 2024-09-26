package com.lostmc.bukkit.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lostmc.bukkit.control.Control;
import com.lostmc.bukkit.control.Controller;
import lombok.Getter;
import lombok.Setter;

public class ChatController extends Controller {

	@Getter
	@Setter
	private ChatState chatState;
	private List<Pattern> patterns = new ArrayList<>();

	public ChatController(Control control) {
		super(control);
	}

	@Override
	public void onEnable() {
		this.chatState = ChatState.ENABLED;

		registerRegex("^l{1,101}$");
		registerRegex("lixo");
		registerRegex("noob");
		registerRegex("nub");
		registerRegex("autista");
		registerRegex("autismo");
		registerRegex("perdedor");
		registerRegex("loser");
		registerRegex("doente");
		registerRegex("macaco");
		registerRegex("gorila");
		registerRegex("porra");
		registerRegex("fdp");
		registerRegex("puta");
		registerRegex("puto");
		registerRegex("caralho");
		registerRegex("desgraçado");
		registerRegex("arrombado");
		registerRegex("fode");
		registerRegex("foder");
	}

	public void clearChat(int rows) {
		for (int i = 0; i <= rows; i++)
			getControl().getPlugin().getServer().broadcastMessage("§a§l §b§l §c§l §d§l §e§l §f§l");
	}

	protected void registerRegex(String expression) {
		registerRegex(expression, Pattern.CASE_INSENSITIVE);
	}

	protected void registerRegex(String expression, int flag) {
		this.patterns.add(Pattern.compile(expression, flag));
	}

	public String compileMessage(String original) {
		String[] messageArray = original.split(" ");

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < messageArray.length; i++) {
			
			synchronized (this.patterns) {
				for (Pattern pattern : this.patterns) {
					Matcher matcher = pattern.matcher(messageArray[i]);
					while (matcher.find()) {
						String matched = matcher.group(0);
						StringBuilder replace = new StringBuilder();
						for (int j = 0; j < matched.length(); j++)
							replace.append("*");
						messageArray[i] = messageArray[i].replaceAll("(?i)" + matched, replace.toString());
					}
				}
			}
			
			builder.append(messageArray[i]).append(i + 1 >= messageArray.length ? "" : " ");
		}

		return builder.toString();
	}

	@Override
	public void onDisable() {
		this.chatState = null;
	}
}
