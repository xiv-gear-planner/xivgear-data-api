package gg.xp.xivgear.dataapi


import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License

@OpenAPIDefinition(
		info = @Info(
				title = "XivGear Reference Data API",
				version = "0.1",
				description = "Game data API for XivGear",
				license = @License(name = "GPLv3"),
				contact = @Contact(name = "Discord: xp")
		)
)
class Main {
	static void main(String[] args) {
		Micronaut.run(Main, args)
	}
}
