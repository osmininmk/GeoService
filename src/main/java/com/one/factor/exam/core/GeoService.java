package com.one.factor.exam.core;

import com.one.factor.exam.entities.UserPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeoService {
    private static final int EQUATORIALRADIUS = 6378137;


    @Autowired
    private  HibernateManager hibernateManager;

    private int getAccuracy (double lon, double lat) {
        return hibernateManager.getDistanceError(lon, lat);
    }

    public String checkUserPosition(double lon, double lat, int userId) {
        UserPosition position = hibernateManager.getById(userId, UserPosition.class);
        if (sphericalDistance(position.getLon(), position.getLat(), lon, lat) < getAccuracy(position.getLon(), position.getLat())) {
            return "Near position";
        } else {
            return "Far away position";
        }
    }

    /**
     *
     * @param lon1 point1 lon
     * @param lat1 point1 lat
     * @param lon2 point2 lon
     * @param lat2 point2 lat
     * @return distance in meters between two points
     */
    private static int sphericalDistance(double lon1, double lat1, double lon2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int)(c * EQUATORIALRADIUS);
    }

}
