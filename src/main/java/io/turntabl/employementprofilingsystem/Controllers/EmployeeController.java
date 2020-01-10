package io.turntabl.employementprofilingsystem.Controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.turntabl.employementprofilingsystem.DAO.EmployeeDAO;

import io.turntabl.employementprofilingsystem.Models.AddEmployee;
import io.turntabl.employementprofilingsystem.Utilities.*;
import io.turntabl.employementprofilingsystem.Transfers.*;
import io.turntabl.employementprofilingsystem.Transfers.Tech;
import io.turntabl.employementprofilingsystem.Utilities.DatePersonal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;

@Api
@RestController
class EmployeeController implements EmployeeDAO {
    @Autowired
    JdbcTemplate jdbcTemplate;

    Parsor parsor = new Parsor();
    DatePersonal date = new DatePersonal();


    @ApiOperation("Add New Employee")
    @CrossOrigin(origins = "*")
    @PostMapping("/v1/api/employee")
    @Override
    public Map<String, Object> addEmployee(@RequestBody AddEmployee requestData) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("employee_firstname",requestData.getEmployee_firstname());
        request.put("employee_lastname",requestData.getEmployee_lastname());
        request.put("employee_phonenumber",requestData.getEmployee_phonenumber());
        request.put("employee_email",requestData.getEmployee_email());
        request.put("employee_role",requestData.getEmployee_role());
        request.put("employee_address",requestData.getEmployee_address());
        request.put("employee_dev_level",requestData.getEmployee_dev_level());
        request.put("employee_gender",requestData.getEmployee_gender());
        request.put("employee_tech_stack",requestData.getEmployee_tech_stack());

        try{

            List<String> requiredParams = Arrays.asList(
                    "employee_firstname",
                    "employee_lastname",
                    "employee_phonenumber",
                    "employee_email",
                    "employee_role",
                    "employee_address",
                    "employee_dev_level",
                    "employee_gender",
                    "employee_tech_stack"
            );

            Map<String, Object> result = parsor.validate_params(request,requiredParams);
            if (result.get("code").equals("00")){

                java.sql.Date employee_hire_date = date.getCurrentDate();
                Boolean employee_onleave = false;
                String employee_dev_level = requestData.getEmployee_dev_level();
                String employee_gender = requestData.getEmployee_gender();
                String employee_role = requestData.getEmployee_role();

                SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate).withTableName("employee").usingGeneratedKeyColumns("employee_id");

                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("employee_firstname",requestData.getEmployee_firstname());
                parameters.put("employee_lastname",requestData.getEmployee_lastname());
                parameters.put("employee_phonenumber",requestData.getEmployee_phonenumber());
                parameters.put("employee_email",requestData.getEmployee_email());
                parameters.put("employee_address",requestData.getEmployee_address());
                parameters.put("employee_role",employee_role.toUpperCase());
                parameters.put("employee_dev_level",employee_dev_level.toUpperCase());
                parameters.put("employee_hire_date",employee_hire_date);
                parameters.put("employee_onleave",employee_onleave);
                parameters.put("employee_gender",employee_gender.toUpperCase());

                Number key = insertActor.executeAndReturnKey(parameters);
                Long employee_key = key.longValue();
                if (key != null){

                    List<Integer> employee_tech_stack = requestData.getEmployee_tech_stack();
                    for(Integer tech: employee_tech_stack){
                        if (tech instanceof Integer){
                            jdbcTemplate.update(
                                    "insert into employeetech(tech_id,employee_id) values(?,?)",
                                    new Object[]{
                                            tech,
                                            employee_key
                                    }
                            );
                            response.put("code","00");
                            response.put("msg","New employee added successfully");
                        }else {
                            this.deleteEmployeeRow(employee_key);
                            response.put("code","01");
                            response.put("msg","Invalid input type [project_tech_stack], try again later");
                        }
                    }
                }else {
                    this.deleteEmployeeRow(employee_key);
                    response.put("code","01");
                    response.put("msg","Failed to add new employee, try again later");
                }
            }else {
                response.put("code",result.get("code"));
                response.put("msg",result.get("msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
            response.put("code","02");
            response.put("msg","Something went wrong, try again later");
        }
        return response;
    }
    @ApiOperation("List of Employee Profile")
    @CrossOrigin(origins = "*")
    @GetMapping("/v1/api/employees")
    @Override
    public Map<String, Object> getAllEmployeeProfile(){
        List<SingleProfileTO> result = new ArrayList<>();
        Map<String, Object> response = new HashMap<>();
        try{
            List<Employee> employeeTOList =  jdbcTemplate.query(
                    "select * from employee",
                    BeanPropertyRowMapper.newInstance(Employee.class)
            );

            for (Employee employee: employeeTOList){
                List<Project> projectTOS =  jdbcTemplate.query(
                        "select * from project inner join assignedproject on project.project_id = assignedproject.project_id inner join employee on assignedproject.employee_id = employee.employee_id where employee.employee_id = ? ",
                        new Object[]{employee.getEmployee_id()},
                        BeanPropertyRowMapper.newInstance(Project.class)
                );
                List<Tech>techStack =  jdbcTemplate.query(

                        "select * from tech inner join employeetech on tech.tech_id = employeetech.tech_id inner join employee on employeetech.employee_id = employee.employee_id where employee.employee_id = ? ",
                        new Object[]{employee.getEmployee_id()},
                        BeanPropertyRowMapper.newInstance(Tech.class)
                );
                result.add(SingleProfileTOrowMappper(employee,projectTOS, techStack));
            }

            response.put("code","00");
            response.put("msg","Data retrieved successfully");
            response.put("data",result);

        }catch (Exception e){
            e.printStackTrace();
            response.put("code","02");
            response.put("msg","Something went wrong, try again later");
        }
        return response;
    }

    public SingleProfileTO SingleProfileTOrowMappper(Employee employee, List<Project> projectTOS, List<Tech> techStack ) throws SQLException {
        SingleProfileTO singleProfileTO = new SingleProfileTO();
        singleProfileTO.setEmployee(employee);
        singleProfileTO.setProjects(projectTOS);
        singleProfileTO.setTech_stack(techStack);
        return singleProfileTO;
    }

    public void deleteEmployeeRow(Long id){
        String sql = "delete from employee where employee_id = ? ";
        jdbcTemplate.update(
                sql,
                new Object[]{id}
        );
    }


}
