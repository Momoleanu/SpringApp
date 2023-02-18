package com.bdt.bdtemacasademo.Cotroller;


import com.bdt.bdtemacasademo.Entity.Appointment;
import com.bdt.bdtemacasademo.Entity.MedicalProblem;
import com.bdt.bdtemacasademo.Entity.Surgery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/account")
public class CrudController {

    boolean dateFlag = true;

    @RequestMapping("/")
    public ModelAndView userAccount(@ModelAttribute("doctor_id") int doctor_id, final RedirectAttributes redirectAttributes) {

        dateFlag=true;
        ModelAndView modelAndView = new ModelAndView("account");
        List<Appointment> appointmentList = new ArrayList<>();
        int n = 0;
        redirectAttributes.addFlashAttribute("rowID", n);

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl",
                    "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("\n" +
                    "SELECT APP_ID, PATIENT_ID, DOCTOR_ID, APP_DATE, REZULT_DATE, SURGERY_ID, TO_CHAR(APP_DATE , 'HH24:MI') AS HOUR\n" +
                    "FROM APPOINTMENTS\n" +
                    "WHERE DOCTOR_ID=?\n" +
                    "ORDER BY app_date asc");

            PreparedStatement statement2 = con.prepareStatement("\n" +
                    "select a.app_id , p.name\n" +
                    "from appointments a, patient p\n" +
                    "where a.patient_id=p.patient_id ORDER BY app_date asc");

            PreparedStatement statement3 = con.prepareStatement("select a.app_id, s.surgery_name \n" +
                    "from appointments a, surgery s\n" +
                    "where a.surgery_id=s.surgery_id(+) ORDER BY app_date asc");

            statement.setInt(1, doctor_id);
            ResultSet resultSet = statement.executeQuery();
            ResultSet resultSet2 = statement2.executeQuery();
            ResultSet resultSet3 = statement3.executeQuery();

            while (resultSet.next() && resultSet2.next() && resultSet3.next()) {
                int appID = resultSet.getInt("app_id");
                int patientID = resultSet.getInt("patient_id");
                java.util.Date appDate = resultSet.getDate("app_date");
                Date rezultDate = resultSet.getDate("rezult_date");
                int surgeryID = resultSet.getInt("surgery_id");
                String patient_name = resultSet2.getString("name");
                String surgery_name = resultSet3.getString("surgery_name");
                String hour=resultSet.getString("hour");
                Appointment appointment = new Appointment(appID, patientID, doctor_id, appDate, rezultDate, surgeryID,
                        patient_name, surgery_name,hour);
                appointmentList.add(appointment);
                redirectAttributes.addFlashAttribute("appointment", appointment);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        modelAndView.addObject("appointmentList", appointmentList);

        return modelAndView;
    }

    @RequestMapping("/delete")
    public String delete(@RequestParam int param1, @RequestParam int param2, final RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("doctor_id", param2);
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl", "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("delete from appointments where app_id = ?");
            statement.setInt(1, param1);
            ResultSet resultSet = statement.executeQuery();

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/account/";
    }

    @RequestMapping("/update")
    public String update(@ModelAttribute("appointment") Appointment appointment, @RequestParam int param1, @RequestParam int param2, @RequestParam int param3, Model model) {

        List<Surgery> surgeryList = new ArrayList<>();
        List<MedicalProblem> medicalProblemsList = new ArrayList<>();

        model.addAttribute("doctor_id", param2);
        model.addAttribute("appID", param1);
        model.addAttribute("patient_id", param3);
        model.addAttribute("appointment", appointment);
        model.addAttribute("dateFlag", dateFlag);

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl", "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("select * from surgery");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int surgery_id = resultSet.getInt("surgery_id");
                String surgery_name = resultSet.getString("surgery_name");
                int price = resultSet.getInt("price");

                surgeryList.add(new Surgery(surgery_id, surgery_name, price));
            }
            statement = con.prepareStatement("select * from medical_problems\n" +
                    "where prob_id not in ( select medical_problems_prob_id from problems_patient_fk where patient_patient_id = ?)");

            statement.setInt(1, param3);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int prob_id = resultSet.getInt("prob_id");
                String prob_name = resultSet.getString("prob_name");
                int grade = resultSet.getInt("grade");

                medicalProblemsList.add(new MedicalProblem(prob_id, prob_name, grade));
            }


            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("surgeryList", surgeryList);
        model.addAttribute("medicalProblemList", medicalProblemsList);
        System.out.println(param2);
        return "update";
    }

    @PostMapping("/updateData")
    public RedirectView updateData(@RequestParam("param1") int doctor_id, @RequestParam("param2") int app_id,
                                   @RequestParam("param3") int patient_id,
                                   @ModelAttribute("appointment") Appointment appointment, final RedirectAttributes redirectAttributes) {

        RedirectView redirectView;

        System.out.println(appointment.getRezultDate() + " " + appointment.getSurgeryID() + " " + app_id);
        redirectAttributes.addFlashAttribute("doctor_id", doctor_id);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String resultDate = dateFormat.format(appointment.getRezultDate());
        resultDate = resultDate.replace("Sept", "Sep");

        int surgery_id = appointment.getSurgeryID();


        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl", "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("UPDATE appointments set rezult_date=?, surgery_id=? where app_id=?");
            statement.setString(1, resultDate);
            statement.setInt(2, surgery_id);
            statement.setInt(3, app_id);

            ResultSet resultSet = statement.executeQuery();

            System.out.println("appid"+appointment.getProb_id());
            System.out.println("di"+patient_id);

            statement= con.prepareStatement(" INSERT INTO PROBLEMS_PATIENT_FK values (?,?)");
            statement.setInt(1,appointment.getProb_id());
            statement.setInt(2,patient_id);
            statement.executeQuery();

            con.close();

        } catch (java.sql.SQLIntegrityConstraintViolationException dateCatch) {
            dateFlag = false;
            redirectView=new RedirectView("/account/update");
            redirectView.addStaticAttribute("param1", app_id);
            redirectView.addStaticAttribute("param2", doctor_id);
            redirectView.addStaticAttribute("param3", patient_id);
            return redirectView;

        } catch (Exception e) {
            e.printStackTrace();
        }

        redirectView=new RedirectView("/account/");
        return redirectView;
    }

    @RequestMapping("/info")
    public String info(@RequestParam int param1, @RequestParam int param2, Model model) {

        List<String> medicalStringList = new ArrayList<>();
        model.addAttribute("doctor", param2);

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl", "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("select m.prob_name from medical_problems m\n" +
                    "where m.prob_id in ( select medical_problems_prob_id from problems_patient_fk where patient_patient_id = ?)");
            statement.setInt(1, param1);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                medicalStringList.add(resultSet.getString("prob_name"));
            }

            statement= con.prepareStatement("select sum(s.price) as totalPrice from appointments a, surgery s\n" +
                    "where a.surgery_id=s.surgery_id and a.patient_id=?");
            statement.setInt(1,param1);
            resultSet=statement.executeQuery();
            resultSet.next();
            int totalPrice=resultSet.getInt("totalPrice");

            model.addAttribute("totalPrice",totalPrice);
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("medicalStringList", medicalStringList);
        int m = 0;
        return "info";
    }

    @PostMapping("/infoPost/{doctor}")
    public String infoPost(@PathVariable int doctor, final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("doctor_id", doctor);
        return "redirect:/account/";
    }


}
