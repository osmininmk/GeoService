package com.one.factor.exam.controller;

import com.one.factor.exam.core.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/geo")
public class GeoController {

    @Autowired
    private GeoService geoService;

    /**
     *
     * @param id user_id
     * @param lon current lon
     * @param lat current lat
     * @return String is user near his mark calculate with accuracy specified in grid
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String check(@PathVariable("id") int id,
                        @RequestParam("lon") double lon,
                        @RequestParam("lat") double lat) {
        return geoService.checkUserPosition(lon, lat, id);
    }
}
