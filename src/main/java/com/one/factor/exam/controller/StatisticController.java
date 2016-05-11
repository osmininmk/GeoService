package com.one.factor.exam.controller;

import com.one.factor.exam.core.GeoService;
import com.one.factor.exam.core.HibernateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("statistic")
public class StatisticController {

    @Autowired
    private HibernateManager hibernateManager;

    /**
     *
     * @param lon
     * @param lat
     * @return count of user in same grid item that point
     */
    @RequestMapping(value = "grid", method = RequestMethod.GET)
    @ResponseBody
    public String gridStatistics(
            @RequestParam("lon") double lon,
            @RequestParam("lat") double lat) {
        return String.valueOf(hibernateManager.getCountPerSection(lon, lat));
    }
}
