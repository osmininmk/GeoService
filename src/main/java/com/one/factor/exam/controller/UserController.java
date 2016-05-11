package com.one.factor.exam.controller;

import com.one.factor.exam.core.HibernateManager;
import com.one.factor.exam.entities.UserPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private HibernateManager hibernateManager;

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String createOrUpdate(@PathVariable("id") int id,
                        @RequestParam("lon") double lon,
                        @RequestParam("lat") double lat,
                        HttpServletResponse response) {
        UserPosition userPosition = hibernateManager.getById(id, UserPosition.class);
        boolean createUser = false;
        if (userPosition == null) {
            createUser = true;
            userPosition = new UserPosition();
            userPosition.setId(id);
        }
        userPosition.setLat(lat);
        userPosition.setLon(lon);
        if (hibernateManager.saveOrUpdate(userPosition)) {
            if (createUser) {
                response.setStatus(HttpStatus.CREATED.value());
            }
            return "SUCCESS";
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return "ERROR";
        }
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String add(@PathVariable("id") int id,
                      HttpServletResponse response) {
        UserPosition userPosition = hibernateManager.getById(id, UserPosition.class);
        if (userPosition == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "ERROR";
        } else {
            hibernateManager.delete(userPosition);
            return "SUCCESS";
        }
    }
}
