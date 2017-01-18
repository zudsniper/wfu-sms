package cc.holstr.wfu;

import cc.holstr.wfu.services.PurchaseBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

/**
 * Created by jason on 1/17/17.
 */
@Configuration
@EnableAutoConfiguration(exclude=WfusmsInterfaceApplication.class)
@ComponentScan(basePackages = "cc.holstr.wfu.controller")
@PropertySource("classpath:modelapplication.properties")
public class ModelTestRunner{

	public static void main(String[] args) {
		SpringApplication.run(ModelTestRunner.class, args);
	}

}
