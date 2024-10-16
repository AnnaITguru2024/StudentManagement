package raisetech.student.management;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@OpenAPIDefinition(info = @Info(title = "受講生管理システム"))
@SpringBootApplication
@ComponentScan(basePackages = {"raisetech.student.management"})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}