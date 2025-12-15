package io.github.lionheartlattice.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Z-Admin 接口文档")
                                            .version("1.0")
                                            .description("Z-Admin 后台管理系统 API 接口文档")
                                            .contact(new Contact().name("Lionheart")
                                                                  .url("https://github.com/lionheartlattice/zadmin"))
                                            .license(new License().name("Apache 2.0")
                                                                  .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                            .externalDocs(new ExternalDocumentation().description("本地接口文档地址")
                                                                     .url("http://localhost:19991/api/admin/doc.html"));
    }
}
