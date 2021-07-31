package de.uniba.dsg.jpb.server.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConfigurationProperties(prefix = "swagger")
@EnableSwagger2
public class SpringFoxConfig {

  private String title;
  private String description;
  private String basePackage;

  public SpringFoxConfig() {
    title = null;
    description = null;
    basePackage = null;
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(new ApiInfoBuilder().title(title).description(description).build())
        .select()
        .apis(RequestHandlerSelectors.basePackage(basePackage))
        .paths(PathSelectors.ant("/api/**"))
        .build();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBasePackage() {
    return basePackage;
  }

  public void setBasePackage(String basePackage) {
    this.basePackage = basePackage;
  }
}
