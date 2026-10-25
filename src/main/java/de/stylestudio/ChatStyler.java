package de.stylestudio;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class ChatStyler {
	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

	private ChatStyler() {
	}

	public static Text style(Text message) {
		StylesConfig cfg = StylesConfig.get();
		if (!cfg.chatEnabled) {
			return message;
		}

		boolean hasPrefix = cfg.chatPrefix != null && !cfg.chatPrefix.isBlank();
		if (!cfg.chatTimestamp && !hasPrefix && cfg.chatTextColor == -1) {
			return message;
		}

		MutableText result = Text.empty();

		if (cfg.chatTimestamp) {
			String time = "[" + LocalTime.now().format(TIME) + "] ";
			result.append(Text.literal(time).styled(s -> s.withColor(cfg.chatTimestampColor)));
		}

		if (hasPrefix) {
			String prefix = cfg.chatPrefix.trim() + " ";
			result.append(Text.literal(prefix).styled(s -> s.withColor(cfg.chatPrefixColor)));
		}

		MutableText body = message.copy();
		if (cfg.chatTextColor != -1) {
			body.styled(s -> s.getColor() == null ? s.withColor(cfg.chatTextColor) : s);
		}
		result.append(body);

		return result;
	}
}
