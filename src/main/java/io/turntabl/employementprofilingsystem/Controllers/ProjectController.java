package io.turntabl.employementprofilingsystem.Controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.turntabl.employementprofilingsystem.DAO.ProjectDAO;
import io.turntabl.employementprofilingsystem.Models.AddProject;
import io.turntabl.employementprofilingsystem.Utilities.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Api
@RestController
public class ProjectController implements ProjectDAO {
    @Autowired
    JdbcTemplate jdbcTemplate;

    Parsor parsor = new Parsor();
    DatePersonal date = new DatePersonal();

    @ApiOperation("Add New Project")
    @CrossOrigin(origins = "*")
    @PostMapping("/v1/api/project")
    @Override
    public Map<String, Object> addProject(@RequestBody AddProject requestData) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        request.put("project_name",requestData.getProject_name());
        request.put("project_description",requestData.getProject_description());
        request.put("project_start_date",requestData.getProject_start_date());
        request.put("project_end_date",requestData.getProject_end_date());
        request.put("project_tech_stack",requestData.getProject_tech_stack());

        try{

            List<String> requiredParams = Arrays.asList(
                    "project_name",
                    "project_description",
                    "project_start_date",
                    "project_end_date",
                    "project_tech_stack"
            );

            Map<String, Object> result = parsor.validate_params(request,requiredParams);
            if (result.get("code").equals("00")){

                String project_startDate =  requestData.getProject_start_date();
                String project_endDate = requestData.getProject_end_date();

                java.sql.Date project_start_date = date.getDateObject(project_startDate);
                java.sql.Date project_end_date = date.getDateObject(project_endDate);

                SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate).withTableName("project").usingGeneratedKeyColumns("project_id");

                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("project_name",requestData.getProject_name());
                parameters.put("project_description",requestData.getProject_description());
                parameters.put("project_start_date",project_start_date);
                parameters.put("project_end_date",project_end_date);

                Number key = insertActor.executeAndReturnKey(parameters);
                Long project_key = key.longValue();
                if (key != null){

                    List<Integer> project_tech_stack = requestData.getProject_tech_stack();
                    for(Integer tech: project_tech_stack){
                        if (tech instanceof Integer){
                            jdbcTemplate.update(
                                    "insert into techproject(tech_id,project_id) values(?,?)",
                                    new Object[]{
                                            tech,
                                            project_key
                                    }
                            );
                            response.put("code","00");
                            response.put("msg","New project added successfully");
                        }else {
                            this.deleteProjectRow(project_key);
                            response.put("code","01");
                            response.put("msg","Invalid input type [project_tech_stack], try again later");
                        }
                    }
                }else {
                    this.deleteProjectRow(project_key);
                    response.put("code","01");
                    response.put("msg","Failed to add new project, try again later");
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
    public void deleteProjectRow(Long id){
        String sql = "delete from project where project_id = ? ";
        jdbcTemplate.update(
                sql,
                new Object[]{id}
        );
    }
}
