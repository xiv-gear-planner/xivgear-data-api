package gg.xp.xivgear.dataapi.util;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Context
public class EnvironmentLogger {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentLogger.class);

	public EnvironmentLogger(Environment environment) {
		log.info("Environments active: {}", environment.getActiveNames());
	}
}
