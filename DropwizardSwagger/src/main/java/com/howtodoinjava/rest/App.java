package com.howtodoinjava.rest;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.sql.DataSource;
import javax.ws.rs.client.Client;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.howtodoinjava.rest.basicauth.AppAuthenticator;
import com.howtodoinjava.rest.basicauth.AppAuthorizer;
import com.howtodoinjava.rest.basicauth.User;
import com.howtodoinjava.rest.controller.EmployeeRESTController;
import com.howtodoinjava.rest.controller.RESTClientController;
import com.howtodoinjava.rest.healthcheck.AppHealthCheck;
import com.howtodoinjava.rest.healthcheck.HealthCheckController;
import com.howtodoinjava.rest.service.EmpService;


import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.config.ConfigFactory;
import io.swagger.config.ScannerFactory;
import io.swagger.config.SwaggerConfig;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

public class App extends Application<EmployeeConfig> {
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	  private static final String SQL = "sql";
	  private static final String DROPWIZARD_BLOG_SERVICE = "Dropwizard blog service";
	  private static final String BEARER = "Bearer";
			  
	@Override
	public void initialize(Bootstrap<EmployeeConfig> b) {
		
		 b.addBundle(new AssetsBundle("/swagger/assets", "/", "index.html"));
	}

	@Override
	public void run(EmployeeConfig c, Environment e) throws Exception 
	{
		LOGGER.info("Registering REST resources");
		
		// Datasource configuration
	    final DataSource dataSource =
	        c.getDataSourceFactory().build(e.metrics(), SQL);
	    DBI dbi = new DBI(dataSource);
		
		
		e.jersey().register(new EmployeeRESTController(e.getValidator(),dbi.onDemand(EmpService.class)));

		final Client client = new JerseyClientBuilder(e)
				.build("DemoRESTClient");
		e.jersey().register(new RESTClientController(client));

		//swagger
		
		initSwagger(c, e);
		
		// Application health check
		e.healthChecks().register("APIHealthCheck", new AppHealthCheck(client));

		// Run multiple health checks
		e.jersey().register(new HealthCheckController(e.healthChecks()));
		
		//Setup Basic Security
		e.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new AppAuthenticator())
                .setAuthorizer(new AppAuthorizer())
                .setRealm("App Security")
                .buildAuthFilter()));
        e.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        e.jersey().register(RolesAllowedDynamicFeature.class);
        
        e.jersey().setUrlPattern("/api/*");
	}

	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

	private void initSwagger(EmployeeConfig  configuration, Environment environment) {
	    // Swagger Resource
	    environment.jersey().register(new ApiListingResource());
	 
	    // Swagger providers
	    environment.jersey().register(new SwaggerSerializers());
	    //environment.jersey().register(new ResourceListingProvider());
	 
	    // Swagger Scanner, which finds all the resources for @Api Annotations
	    ScannerFactory.setScanner(new io.swagger.jaxrs.config.DefaultJaxrsScanner());
	 
	    // Add the reader, which scans the resources and extracts the resource information
	    //ClassReaders.setReader(new DefaultJaxrsApiReader());
	 // required CORS support
	    FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
	    filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
	    filter.setInitParameter("allowedOrigins", "*"); // allowed origins comma separated
	    filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
	    filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS,HEAD");
	    filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
	    filter.setInitParameter("allowCredentials", "true");
	 
	    // Set the swagger config options
	   /* SwaggerConfig config = ConfigFactory.;
	    config.setApiVersion("1.0.1");
	    config.setBasePath(configuration.getSwaggerBasePath());*/
	    
	    BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8992");
        beanConfig.setBasePath("/api");
        beanConfig.setResourcePackage("com.howtodoinjava.rest.controller");
        beanConfig.setScan(true);
	}
}