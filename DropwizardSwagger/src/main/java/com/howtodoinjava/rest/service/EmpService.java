package com.howtodoinjava.rest.service;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

import com.howtodoinjava.rest.dao.IEmployeeDAO;
import com.howtodoinjava.rest.representations.Employee;

public abstract class EmpService implements IEmpService {

	
	 @CreateSqlObject
	  abstract IEmployeeDAO employeeDAO();

	  public List<Employee> getEmployees() {
	    return employeeDAO().getEmployees();
	  }
}
