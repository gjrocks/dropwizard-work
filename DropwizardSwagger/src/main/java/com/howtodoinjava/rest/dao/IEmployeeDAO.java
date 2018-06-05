package com.howtodoinjava.rest.dao;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.howtodoinjava.rest.representations.Employee;

@RegisterMapper(EmpMapper.class)
public interface IEmployeeDAO {

	
	 @SqlQuery("select * from Employees;")
	  public List<Employee> getEmployees();
}
