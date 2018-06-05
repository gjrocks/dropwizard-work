package com.howtodoinjava.rest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class EmployeeConfig extends Configuration {
	  private static final String DATABASE = "database";

	  @Valid
	  @NotNull
	  private DataSourceFactory dataSourceFactory = new DataSourceFactory();

	  @Valid
	  @NotNull
	  @JsonProperty
	  private String swaggerBasePath;
	  
	  public String getSwaggerBasePath(){
		  return swaggerBasePath;
	  }
	  
	  
	  @JsonProperty(DATABASE)
	  public DataSourceFactory getDataSourceFactory() {
	    return dataSourceFactory;
	  }

	  @JsonProperty(DATABASE)
	  public void setDataSourceFactory(final DataSourceFactory dataSourceFactory) {
	    this.dataSourceFactory = dataSourceFactory;
	  }
	}
