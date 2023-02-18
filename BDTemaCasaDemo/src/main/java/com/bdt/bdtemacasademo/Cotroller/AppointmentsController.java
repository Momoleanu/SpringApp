package com.bdt.bdtemacasademo.Cotroller;

import com.bdt.bdtemacasademo.Entity.Appointment;
import com.bdt.bdtemacasademo.Entity.Doctor;
import com.bdt.bdtemacasademo.Entity.Patient;
import com.bdt.bdtemacasademo.Entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.print.Doc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class AppointmentsController {

    boolean flag=true;
    boolean nameFlag=true;

    @RequestMapping("/appointments")
    public ModelAndView signIn(){

        List<Doctor> doctorList = new ArrayList<>();
        List<String> hours = new ArrayList<>();

        hours.add("8:00");
        hours.add("10:00");
        hours.add("12:00");
        hours.add("14:00");
        hours.add("16:00");

        ModelAndView modelAndView = new ModelAndView("appointments");
        Patient patient= new Patient();
        Appointment appointment= new Appointment();

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl", "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("select * from doctors");

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                int doctor_id= resultSet.getInt("doctor_id");
                String name = resultSet.getString("name");
                Date hire_date = resultSet.getDate("hire_date");
                doctorList.add(new Doctor(doctor_id, name, hire_date));

            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        modelAndView.addObject("patient", patient);
        modelAndView.addObject("appointment", appointment);
        modelAndView.addObject("flag", flag);
        modelAndView.addObject("doctorList", doctorList);
        modelAndView.addObject("hourList", hours);
        modelAndView.addObject("nameFlag", nameFlag);
        nameFlag=true;

        return modelAndView;
    }

    @RequestMapping(value = "/doappointments", method = RequestMethod.POST)
    public String doAppointments(@ModelAttribute("patient") Patient patient, @ModelAttribute("appointment") Appointment appointment,
                                  BindingResult bindingResult)
    {
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd-MMM-yyyy");
        String appDate=dateFormat.format(appointment.getAppDate());
        appDate=appDate.replace("Sept", "Sep");
        //System.out.println(hireDate);
        boolean patientFlag=false;
        int doctor_id = 0;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl", "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("select to_char(app_date,'HH24:mi') as hour, doctor_id, app_date from appointments");

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                String hour = resultSet.getString("hour");
                doctor_id = resultSet.getInt("doctor_id");
                Date app_date_unformatted = resultSet.getDate("app_date");
                String app_date = dateFormat.format(app_date_unformatted);
                app_date = app_date.replace("Sept", "Sep");


                if (appointment.getHour().equals(hour) && appointment.getDoctorID() == doctor_id && app_date.equals(appDate)) {
                    flag = false;
                    break;
                }
                else{
                    flag= true;
                }
            }

                    if(flag) {
                        statement = con.prepareStatement("SELECT patient_id from patient where email=?");
                        statement.setString(1, patient.getEmail());
                        int patient_id = 0;

                        resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                            patient_id = resultSet.getInt("patient_id");
                            patientFlag = true;
                        }

                        if (!patientFlag) {
                            statement = con.prepareStatement("INSERT INTO patient values (NULL,?,default,?)");
                            statement.setString(1, patient.getName());
                            statement.setString(2, patient.getEmail());
                            statement.executeQuery();

                            statement = con.prepareStatement("SELECT patient_id from patient where email=?");
                            statement.setString(1, patient.getEmail());
                            resultSet = statement.executeQuery();
                            if (resultSet.next()) {
                                patient_id = resultSet.getInt("patient_id");
                            }
                        }

                        appDate=appDate.concat(" ").concat(appointment.getHour());
                        statement = con.prepareStatement("INSERT INTO appointments values(NULL,?,?,to_date(?,'dd-mon-yyyy hh24:mi'),NULL,NULL )");
                        statement.setInt(1, patient_id);
                        statement.setInt(2, appointment.getDoctorID());
                        statement.setString(3, appDate);

                        statement.executeQuery();

                    }

            con.close();

        }
        catch (java.sql.SQLIntegrityConstraintViolationException nameCatch) {
            System.out.println(nameFlag);

            nameFlag=false;
            return "redirect:/appointments";
        }
        catch (Exception e) {
            e.printStackTrace();
        }
            if(!flag)
            {
                return "redirect:/appointments";
            }
            else
            {
                return "appointmentsCreated";
            }
    }



}
