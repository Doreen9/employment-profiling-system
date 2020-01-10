package io.turntabl.employementprofilingsystem.Controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.turntabl.employementprofilingsystem.Transfers.Tech;
import io.turntabl.employementprofilingsystem.Utilities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api
@RestController
public class TechController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    Parsor parsor = new Parsor();

    @ApiOperation("List of Technologies")
    @CrossOrigin(origins = "*")
    @GetMapping("/v1/api/technologies")
    public Map<String, Object> getAllTechnologies(){

        Map<String, Object> response = new HashMap<>();
        try{
            List<Tech> Technologies =  jdbcTemplate.query(
                    "select * from tech",
                    BeanPropertyRowMapper.newInstance(Tech.class)
            );
            response.put("code","00");
            response.put("msg","Data retrieved successfully");
            response.put("data",Technologies);

        }catch (Exception e){
            e.printStackTrace();
            response.put("code","02");
            response.put("msg","Something went wrong, try again later");
        }
        return response;

        }
    @ApiOperation("Add New Technology")
    @CrossOrigin(origins = "*")
    @PostMapping("/v1/api/addtechnology")
    public Map<String, Object> addTechnology(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        try {

            List<String> requiredParams = Arrays.asList("tech_name", "tech_status");
            Map<String, Object> result = parsor.validate_params(requestData, requiredParams);
            if (result.get("code").equals("00")) {
                jdbcTemplate.update(
                        "insert into tech(tech_name, tech_status) values(?,?)",
                        new Object[]{
                                requestData.get("tech_name"),
                                requestData.get("tech_status")
                        }
                );
                response.put("code", "00");
                response.put("msg", "New technology added successfully");
            } else {
                response.put("code", "01");
                response.put("msg", "Failed to add new technology, try again");
            }
        }catch (Exception e){
                e.printStackTrace();
                response.put("code","02");
                response.put("msg","Something went wrong, try again later");
            }
            return response;

            }

    public Optional<List<Tech>> searchTechByID(Integer techID) {
        Optional<List<Tech>> technology = Optional.ofNullable(jdbcTemplate.query(
                "select * from tech where tech_id = ?",
                new Object[]{techID},
                BeanPropertyRowMapper.newInstance(Tech.class)
        ));
        return technology;
    }

    @ApiOperation("Edit Technology")
    @CrossOrigin(origins = "*")
    @PostMapping("/v1/api/editTechnology")
    public Map<String, Object>  editTechnology(@RequestBody Map<String, String> requestData) {

        Integer id = Integer.parseInt(requestData.get("tech_id"));
        Map<String, Object> response = new HashMap<>();
        Optional<List<Tech>> technology = searchTechByID(Integer.parseInt(requestData.get("tech_id")));
        String tech_name;
        String tech_status;

        if(technology.isPresent()){
            if (requestData.get("tech_name") == null && requestData.get("tech_status") == null){
                tech_name = technology.get().get(0).getTech_name();
                tech_status = technology.get().get(0).getTech_status();
            }
            else if (requestData.get("tech_name") == null && requestData.get("tech_status") != null){
                tech_name = technology.get().get(0).getTech_name();
                tech_status = requestData.get("tech_status");
            }
            else if (requestData.get("tech_name") != null && requestData.get("tech_status") == null){
                tech_status = technology.get().get(0).getTech_status();
                tech_name = requestData.get("tech_name");
            }else{
                tech_status = requestData.get("tech_status");
                tech_name = requestData.get("tech_name");
            }
            jdbcTemplate.update(
                    "update tech set tech_name = ?, tech_status = ? where tech_id = ?",
                    new Object[]{tech_name,tech_status,id}
            );

            response.put("code", "00");
            response.put("msg", "Technology updated successfully");
        }
        else if(!technology.isPresent()){
            response.put("code", "01");
            response.put("msg", "Technology does not exist!");
        }
        return response;

        }
    }





