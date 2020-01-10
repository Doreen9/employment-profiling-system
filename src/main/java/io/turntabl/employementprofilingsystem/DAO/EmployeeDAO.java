package io.turntabl.employementprofilingsystem.DAO;

import io.turntabl.employementprofilingsystem.Models.AddEmployee;
import org.springframework.stereotype.Component;

import java.util.Map;

public interface EmployeeDAO {
    public Map<String,Object> addEmployee( AddEmployee requestData);
    public Map<String, Object> getAllEmployeeProfile();
}
