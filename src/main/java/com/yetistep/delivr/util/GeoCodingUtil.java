package com.yetistep.delivr.util;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.DistanceMatrixRow;
import com.yetistep.delivr.enums.DistanceType;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.enums.VehicleType;
import com.yetistep.delivr.service.impl.SystemPropertyServiceImpl;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import org.apache.log4j.Logger;

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
    private static final String GOOGLE_MAP_API_KEY = "AIzaSyCdr9eotT5ocnlRRhQoDxmZqYlN-Bbk5aE";
    private static final Logger log = Logger.getLogger(GeoCodingUtil.class);

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


    public static List<BigDecimal> getListOfAirDistances(String[] origin, String destination[]) throws Exception {
        List<BigDecimal> distanceList = new ArrayList<BigDecimal>();
        double lat1 = Double.parseDouble(origin[0].split(",")[0]);
        double lng1 = Double.parseDouble(origin[0].split(",")[1]);
        for (int i = 0; i < destination.length; i++) {
            double lat2 = Double.parseDouble(destination[i].split(",")[0]);
            double lng2 = Double.parseDouble(destination[i].split(",")[1]);
            Float distance = getDistance(lat1, lng1, lat2, lng2);
            distanceList.add(new BigDecimal(distance));
        }
        return distanceList;
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
        int oTracker = 0, dTracker = 0;
        for(DistanceMatrixRow distanceMatrixRow: distanceMatrix.rows){
            dTracker = 0;
            for(DistanceMatrixElement element: distanceMatrixRow.elements){
                if(element.status.equals(DistanceMatrixElementStatus.OK))
                    distance.add(new BigDecimal(element.distance.inMeters));
                else {
                    log.info("Error in Latitude-Longitude on Origin:"+origin[oTracker]+" Destination:"+destination[dTracker]);
                    distance.add(getAssumedDistance(origin[oTracker], destination[dTracker]));
                }
                dTracker++;
            }
            oTracker++;
        }
        return distance;
    }

    public static String getLatLong(String latitude, String longitude){
        if(latitude == null || latitude.isEmpty() || longitude == null || longitude.isEmpty()){
            throw new YSException("VLD015");
        }
        return latitude+","+longitude;
    }

    public static BigDecimal getAssumedDistance(String origin, String destination) throws Exception{
        SystemPropertyService systemPropertyService = new SystemPropertyServiceImpl();
        BigDecimal distanceFactor = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.AIR_TO_ROUTE_DISTANCE_FACTOR));
        double lat1 = Double.parseDouble(origin.split(",")[0]);
        double lng1 = Double.parseDouble(origin.split(",")[1]);
        double lat2 = Double.parseDouble(destination.split(",")[0]);
        double lng2 = Double.parseDouble(destination.split(",")[1]);
        BigDecimal distanceInMeters = new BigDecimal(getDistance(lat1, lng1, lat2, lng2));
        return distanceFactor.multiply(distanceInMeters);
    }

    public static List<BigDecimal> getListOfAssumedDistance(String origin, String destination[]) throws Exception{
       List<BigDecimal> distanceList = new ArrayList<BigDecimal>();
        for (int i = 0; i < destination.length; i++) {
            distanceList.add(getAssumedDistance(origin, destination[i]));
        }
        return distanceList;
    }

    public static Integer getRequiredTime(String origin, String destination, VehicleType vehicleType) throws Exception{
        SystemPropertyService systemPropertyService = new SystemPropertyServiceImpl();
        DistanceType distanceType = DistanceType.fromInt(Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.AIR_OR_ACTUAL_DISTANCE_SWITCH)));
        BigDecimal calculatedDistance = BigDecimal.ZERO;
        if(distanceType.equals(DistanceType.AIR_DISTANCE)){
            calculatedDistance = BigDecimalUtil.getDistanceInKiloMeters(getAssumedDistance(origin, destination));
        }else {
             List<BigDecimal> distances = getListOfDistances(new String[] {origin}, new String[]{destination});
            if(distances.size() > 0){
                calculatedDistance = BigDecimalUtil.getDistanceInKiloMeters(distances.get(0));
            }
        }
        int timeFactor = Integer.parseInt(systemPropertyService.readPrefValue(GeneralUtil.getTimeTakenFor(vehicleType)));
        BigDecimal requiredTime = calculatedDistance.multiply(new BigDecimal(timeFactor));
        return requiredTime.intValue();
    }
}
