package com.lostmc.core.profile.password;

import com.lostmc.core.utils.EncryptionUtil;
import lombok.Getter;

@Getter
public final class EncryptedPassword {

	private final long createdIn;
	private final byte[] encryption;
	private final PasswordLevel level;
	
	public EncryptedPassword(String input) {
		this.createdIn = System.currentTimeMillis();
		this.encryption = EncryptionUtil.encrypt(input);
		this.level = PasswordLevel.fromPassword(input);
	}
}
