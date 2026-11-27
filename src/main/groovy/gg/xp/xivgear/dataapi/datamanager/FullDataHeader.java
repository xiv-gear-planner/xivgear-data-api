package gg.xp.xivgear.dataapi.datamanager;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public record FullDataHeader(
		Instant timestamp
) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;
}
