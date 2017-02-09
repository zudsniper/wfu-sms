package cc.holstr.wfu;

import cc.holstr.wfu.google.statistics.StatsManager;
import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.services.MerchantValidator;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.servlet.SMSErrorServlet;
import cc.holstr.wfu.servlet.SMSInternalServlet;
import cc.holstr.wfu.servlet.SMSShopServlet;
import cc.holstr.wfu.web.payment.controller.PaymentController;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.servlet.Servlet;

@SuppressWarnings("ALL")
@EnableAsync
@SpringBootApplication(exclude=DispatcherServletAutoConfiguration.class, scanBasePackages = {"cc.holstr.wfu"})
public class WfusmsInterfaceApplication {

	/*@Bean(name="paymentServlet")
	public ServletRegistrationBean getPaymentServlet() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(PaymentConfig.class);
		dispatcherServlet.setApplicationContext(applicationContext);
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/purchase*//*");
		servletRegistrationBean.setName("paymentServlet");
		return servletRegistrationBean;
	}*/

	public static void main(String[] args) {
		Unpacker.unpack();
		SpringApplicationBuilder parentBuilder
				= new SpringApplicationBuilder(ApplicationConfiguration.class);
		parentBuilder.child(SMSServiceConfiguration.class)
				.properties("server.port:8080",
						"security.ignored=/**").run(args);
		parentBuilder.child(PaymentServiceConfiguration.class)
				.properties("server.port:8443",
						"server.ssl.key-store:keystore.p12",
						"server.ssl.key-store-password=Shoop5sh%",
						"server.ssl.keyStoreType=PKCS12",
						"server.ssl.keyAlias=tomcat",
						"spring.thymeleaf.cache=false")
				.run(args);
		//SpringApplication.run(WfusmsInterfaceApplication.class, args);
	}

	@Configuration
	@EnableAsync
	static class ApplicationConfiguration {

		@Bean
		public PurchaseBuilder purchaseBuilder() {
			return new PurchaseBuilder();
		}

	}

	@Configuration
	@EnableAsync
	@EnableAutoConfiguration
	//@ComponentScan(basePackages = {"cc.holstr.wfu.servlet","cc.holstr.wfu.services"})
	static class SMSServiceConfiguration {

		@Bean
		public MerchantValidator merchantValidator(PurchaseBuilder purchaseBuilder) {return new MerchantValidator(purchaseBuilder);}

		@Bean(name="dispatcherServlet")
		public Servlet getServlet(PurchaseBuilder purchaseBuilder) {
			return new SMSShopServlet(purchaseBuilder);
		}

		@Bean(name="internalServlet")
		public ServletRegistrationBean getInternalServlet(PurchaseBuilder purchaseBuilder, MerchantValidator merchantValidator) {
			ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new SMSInternalServlet(purchaseBuilder, merchantValidator), "/internal/*");
			servletRegistrationBean.setName("internalServlet");
			return servletRegistrationBean;}

		@Bean(name="errorServlet")
		public ServletRegistrationBean getErrorServlet(PurchaseBuilder purchaseBuilder) {
			ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new SMSErrorServlet(purchaseBuilder), "/error/*");
			servletRegistrationBean.setName("errorServlet");
			return servletRegistrationBean;}
	}

	@Configuration
	@EnableAutoConfiguration
	static class PaymentServiceConfiguration {

		@Bean
		public PaymentController controller(PurchaseBuilder purchaseBuilder) {
			return new PaymentController(purchaseBuilder);
		}
	}
}
