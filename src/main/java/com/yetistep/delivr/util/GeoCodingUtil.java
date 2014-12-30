package com.yetistep.delivr.util;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/17/14
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeoCodingUtil {

   // private static final String GOOGLE_MAP_API_KEY = "AIzaSyAnm8zVlUj2KSh5nZA_MD72xZuSBPlmjEg";
   private static final String GOOGLE_MAP_API_KEY = "AIzaSyAkpDVXvLgVAL87I189yx5b-VD6FAoFLYo";

    //Return In Miter
    public static Float getDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

        return new Float(dist * meterConversion).floatValue();
    }

    public static GeoApiContext getGeoApiContext(){
        // Replace the API key below with a valid API key.
        GeoApiContext context = new GeoApiContext().setApiKey(GOOGLE_MAP_API_KEY);
        return context;
    }


    /**
     * This method return list of distances based on origin and destination addresses.
     * Example:
     *         String origin[] = [{27.2133,85.2323}];
     *         String destination[] = [{27.9133,85.4323},{27.5333,85.7723}];
     *         List<BigDecimal> distanceList = getListOfDistances(origin, destination);
     * @param origin Takes array of String as parameter which contains combination of both
     *               latitude and longitude of origin address.
     * @param destination  Takes array of String as parameter which contains combination of both
     *               latitude and longitude of origin address.
     * @return  list of distances BigDecimal values.
     * @throws Exception
     */
    public static List<BigDecimal> getListOfDistances(String[] origin, String destination[]) throws Exception {
        List<BigDecimal> distance = new ArrayList<BigDecimal>();
        GeoApiContext context = GeoCodingUtil.getGeoApiContext();
        DistanceMatrix distanceMatrix = DistanceMatrixApi.getDistanceMatrix(context, origin, destination).await();
        for(DistanceMatrixRow distanceMatrixRow: distanceMatrix.rows){
            for(DistanceMatrixElement element: distanceMatrixRow.elements){
                distance.add(new BigDecimal(element.distance.inMeters));
            }
        }
        return distance;
    }

    public static String getLatLong(String latitude, String longitude){
        if(latitude == null || latitude.isEmpty() || longitude == null || longitude.isEmpty()){
            throw new YSException("VLD015");
        }
        return latitude+","+longitude;
    }
}
