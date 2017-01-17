package cc.holstr.wfu;

import cc.holstr.wfu.properties.Unpacker;
import cc.holstr.wfu.services.PurchaseBuilder;
import cc.holstr.wfu.servlet.SMSInternalServlet;
import cc.holstr.wfu.servlet.SMSShopServlet;
import cc.holstr.wfu.web.payment.controller.PaymentController;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude=DispatcherServletAutoConfiguration.class)
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

	/**/

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
						"spring.thymeleaf.cache=false",
						"paypal.mode=sandbox",
						"paypal.client.app=AZvHPPpxD1_UFFYr-YxDiOxarrsYBOpvpbt-TN5-cqK0ZVwI2Qb5nX_byO86JuoqgsLkkjuoH4Jk234h",
						"paypal.client.secret=ELlfnn2_uHDAFUaSBajUPTCmfCb_fyUwWvHmlxuO9xnau3f8KGOW42KEzE_vJb01iFD_up7OY5Q9sv80")
				.run(args);
		//SpringApplication.run(WfusmsInterfaceApplication.class, args);
	}

	@Configuration
	static class ApplicationConfiguration {

		@Bean
		public PurchaseBuilder purchaseBuilder() {
			return new PurchaseBuilder();
		}
	}

	@Configuration
	@EnableAutoConfiguration
	static class SMSServiceConfiguration {

		@Bean(name="dispatcherServlet")
		public Servlet getServlet(PurchaseBuilder service) {
			return new SMSShopServlet(service);
		}
	}

	@Configuration
	@EnableAutoConfiguration
	static class PaymentServiceConfiguration {

		/**
		 * SRC: https://github.com/masasdani/paypal-springboot/
		 */

		@Bean
		public PaymentController controller(PurchaseBuilder service) {
			return new PaymentController(service);
		}

		@Value("${paypal.client.app}")
		private String clientId;
		@Value("${paypal.client.secret}")
		private String clientSecret;
		@Value("${paypal.mode}")
		private String mode;

		@Bean
		public Map<String, String> paypalSdkConfig(){
			Map<String, String> sdkConfig = new HashMap<>();
			sdkConfig.put("mode", mode);
			return sdkConfig;
		}

		@Bean
		public OAuthTokenCredential authTokenCredential(){
			return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
		}

		@Bean
		public APIContext apiContext() throws PayPalRESTException {
			APIContext apiContext = new APIContext(authTokenCredential().getAccessToken());
			apiContext.setConfigurationMap(paypalSdkConfig());
			return apiContext;
		}
	}
}
