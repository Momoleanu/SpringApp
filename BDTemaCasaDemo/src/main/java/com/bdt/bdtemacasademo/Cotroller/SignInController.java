package com.bdt.bdtemacasademo.Cotroller;

import com.bdt.bdtemacasademo.Entity.Appointment;
import com.bdt.bdtemacasademo.Entity.User;
import com.bdt.bdtemacasademo.Service.HashPassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class SignInController {

    @Autowired
    private HashPassService passService;

    @RequestMapping("/signin")
    public ModelAndView signIn(Model model){

        ModelAndView modelAndView = new ModelAndView("signin");
        User user= new User();
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @RequestMapping(value = "/doconnect", method = RequestMethod.POST)
    public String doLogin(@ModelAttribute("user") User user, BindingResult bindingResult, final RedirectAttributes redirectAttributes){
        boolean flag=false;
        String password;
        int doctor_id=0;
        int app_id=0;
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@bd-dc.cs.tuiasi.ro:1539:orcl", "bd024", "bd024");

            //Statement st = con.createStatement();
            PreparedStatement statement = con.prepareStatement("select * from users where email = ?");
            statement.setString(1, user.getEmail());

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                doctor_id= resultSet.getInt("doctor_id");
                password = resultSet.getString("password");
                flag = passService.checked(user.getPassword(), password);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



        if(!flag) {
            return "redirect:/doFail";
        }
        redirectAttributes.addFlashAttribute("doctor_id", doctor_id);
        redirectAttributes.addFlashAttribute("appID",app_id);
        return "redirect:/account/";
    }

    @RequestMapping("/doFail")
    public String doLoginFail(@ModelAttribute("user") User user, BindingResult bindingResult){
        return "signinfail";
    }



}
