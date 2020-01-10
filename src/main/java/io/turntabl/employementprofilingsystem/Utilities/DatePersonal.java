package io.turntabl.employementprofilingsystem.Utilities;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class DatePersonal {
    public java.sql.Date getCurrentDate (){
        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);
        return date;
    }

    public java.sql.Date getDateObject(String date) throws ParseException {
        SimpleDateFormat fromatDate = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date newDatew= fromatDate.parse(date);
        java.sql.Date finalDate = new java.sql.Date(newDatew.getTime());
        return finalDate;
    }
}


