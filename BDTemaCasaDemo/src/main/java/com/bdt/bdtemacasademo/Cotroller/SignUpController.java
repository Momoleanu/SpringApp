package com.bdt.bdtemacasademo.Cotroller;

import com.bdt.bdtemacasademo.Entity.Doctor;
import com.bdt.bdtemacasademo.Entity.User;
import com.bdt.bdtemacasademo.Service.HashPassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

@Controller
public class SignUpController {

    @Autowired
    HashPassService hashPassService;
    private boolean flagIncorrect = true;
    private boolean flagEmail = true;

    @RequestMapping("/signup")
    public ModelAndView signUp() {

        ModelAndView modelAndView = new ModelAndView("signup");

        User user = new User();
        Doctor doctor = new Doctor();
        modelAndView.addObject("user", user);
        modelAndView.addObject("doctor", doctor);
        modelAndView.addObject("flagName", flagIncorrect);
        modelAndView.addObject("flagEmail", flagEmail);
        return modelAndView;
    }

    @RequestMapping(value = "/dosignup", method = RequestMethod.POST)
    public ModelAndView doSignUp(@ModelAttribute("user") User user, @ModelAttribute("doctor") Doctor doctor,
                                 BindingResult bindingResult) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        ModelAndView modelAndView = null;
        String hireDate = dateFormat.format(doctor.getHireDate());
        hireDate = hireDate.replace("Sept", "Sep");
        System.out.println(hireDate);

        flagEmail = true;
        flagIncorrect=true;

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl",
                    "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("select * from doctors where name = ? and hire_date =?");
            statement.setString(1, doctor.getName());
            statement.setString(2, hireDate);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                int doctor_id = resultSet.getInt("doctor_id");
                statement = con.prepareStatement("insert into users values (?,?,?)");
                statement.setString(1, user.getEmail());
                statement.setString(2, hashPassService.hashed(user.getPassword()));
                statement.setInt(3, doctor_id);
                statement.executeQuery();
                modelAndView = new ModelAndView("index");

            } else {
                modelAndView = new ModelAndView("redirect:/signup");
                flagIncorrect = false;
            }
            con.close();

        } catch (java.sql.SQLIntegrityConstraintViolationException emailCatch) {
            flagEmail = false;
            modelAndView = new ModelAndView("redirect:/signup");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelAndView;
    }

}
