package test.jta.xa;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SimpleXAApplication extends SpringBootServletInitializer {

  public static void main(String[] args) throws Exception {
    new SimpleXAApplication().configure(new SpringApplicationBuilder(SimpleXAApplication.class))
        .run(args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SimpleXAApplication.class);
  }
}
