package com.howtodoinjava.rest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.howtodoinjava.rest.representations.Employee;

public class EmpMapper implements ResultSetMapper<Employee> {
	  private static final String ID = "id";
	  private static final String FNAME = "firstName";
	  private static final String LNAME = "lastName";
	  private static final String EMAIL = "email";
	  
	
	  public Employee map(int i, ResultSet resultSet, StatementContext statementContext)
	      throws SQLException {
	    return new Employee(resultSet.getInt(ID), resultSet.getString(FNAME), resultSet.getString(LNAME),resultSet.getString(EMAIL));
	  }
	}
