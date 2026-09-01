/* SPDX-License-Identifier: AGPL-3.0-or-later */
package com.gmrp.JGMRPBot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class BotConfigTest {

	@Test
	void getInstanceReturnsSameInstance() {
		BotConfig.init();

		BotConfig first = BotConfig.getInstance();
		BotConfig second = BotConfig.getInstance();

		assertSame(first, second);
	}
}