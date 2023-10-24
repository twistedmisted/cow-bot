package ua.zxc.cowbot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@OpenAPIDefinition
public class DocsConfig {

    @Bean
    public OpenApiCustomiser openApiCustomiser() {
        return openApi -> {
            var error = createErrorSchema();
            var success = createSuccessSchema();

            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            schemas.put(error.getName(), error);
            schemas.put(success.getName(), success);
        };
    }

    private Schema createErrorSchema() {
        return new MapSchema()
                .name("Error")
                .addProperty("message", new StringSchema().example("Error message"));
    }

    private Schema createSuccessSchema() {
        return new MapSchema()
                .name("Success")
                .addProperty("message", new StringSchema().example("Success message"));
    }
}
