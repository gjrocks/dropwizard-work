package com.howtodoinjava.rest.controller;
 
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.howtodoinjava.rest.basicauth.User;
import com.howtodoinjava.rest.dao.EmployeeDB;
import com.howtodoinjava.rest.representations.Employee;
import com.howtodoinjava.rest.service.EmpService;

 
@Path("/employees")
@Api(value="/employees", description="Operations on the EmployeeRESTController")
@Produces(MediaType.APPLICATION_JSON)
public class EmployeeRESTController {
 
    private final Validator validator;
  private final EmpService empService;
    public EmployeeRESTController(Validator validator,EmpService empService) {
        this.validator = validator;
        this.empService=empService;
    }
 
    @PermitAll
    @GET
    @ApiOperation(value="Return all employees", notes="testing for bb")
    public Response getEmployees(@Auth User user) {
        return Response.ok(EmployeeDB.getEmployees()).build();
    }
 
    @RolesAllowed("USER")
    @GET
    @Path("/{id}")
    public Response getEmployeeById(@PathParam("id") Integer id,@Auth User user) {
    	//You can validate here if user is watching his record
    	/*if(id != user.getId()){
    			//Not allowed
    	}*/
    	List<Employee> list= empService.getEmployees();
    	
        Employee employee = EmployeeDB.getEmployee(id);
        if (employee != null)
            return Response.ok(employee).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }
 
    @RolesAllowed("ADMIN")
    @POST
    public Response createEmployee(Employee employee, @Auth User user) throws URISyntaxException {
        // validation
        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);
        Employee e = EmployeeDB.getEmployee(employee.getId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<Employee> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
            return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
        }
        if (e != null) {
            EmployeeDB.updateEmployee(employee.getId(), employee);
            return Response.created(new URI("/employees/" + employee.getId()))
                    .build();
        } else
            return Response.status(Status.NOT_FOUND).build();
    }
 
    @PUT
    @Path("/{id}")
    public Response updateEmployeeById(@PathParam("id") Integer id, Employee employee) {
        // validation
        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);
        Employee e = EmployeeDB.getEmployee(employee.getId());
        if (violations.size() > 0) {
            ArrayList<String> validationMessages = new ArrayList<String>();
            for (ConstraintViolation<Employee> violation : violations) {
                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
            }
            return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
        }
        if (e != null) {
            employee.setId(id);
            EmployeeDB.updateEmployee(id, employee);
            return Response.ok(employee).build();
        } else
            return Response.status(Status.NOT_FOUND).build();
    }
 
    @DELETE
    @Path("/{id}")
    public Response removeEmployeeById(@PathParam("id") Integer id) {
        Employee employee = EmployeeDB.getEmployee(id);
        if (employee != null) {
            EmployeeDB.removeEmployee(id);
            return Response.ok().build();
        } else
            return Response.status(Status.NOT_FOUND).build();
    }
}