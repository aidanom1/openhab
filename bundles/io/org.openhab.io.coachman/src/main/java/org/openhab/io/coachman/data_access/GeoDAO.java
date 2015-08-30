package org.openhab.io.coachman.data_access;

import org.openhab.io.coachman.ContextService;
import org.openhab.io.coachman.primitives.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class GeoDAO {
	private GeoApiContext context = null;
	private static final Logger logger = LoggerFactory.getLogger(ContextService.class);
	private static GeoDAO instance = null;
	
	public static GeoDAO getInstance() {
		if(instance == null) {
			instance = new GeoDAO();
		}
		return instance;
	}
	
	private GeoDAO() {
		context =  new GeoApiContext().setApiKey("AIzaSyBGAZA2p6mbK9k2LGNJji_U1BK1dancDnc");
	}
	
	/*******************************************
	 * This function makes use of reverse Geocoding
	 * 
	 * @param l - A Location object
	 * @return A String representation of the 
	 *          Location object
	 */
	public String getOriginAddress(Location l) {
		DistanceMatrix req = null;
		logger.info("Executing Reverse Geocode Request");
		try {
            req = DistanceMatrixApi.newRequest(context)
	        .origins(new LatLng(l.getLatitude(), l.getLongitude()))
	        .destinations(new LatLng(ContextService.HOME_LATITUDE, ContextService.HOME_LONGITUDE))
	        .await();
		}
		catch(Exception e)
		{
			logger.debug("org.openhab.core.coachman.location exception"+e.toString());
		}
		return req.originAddresses[0];
	}
	
	/********************************************
	 * This function makes use of Geocoding
	 * 
	 * @param address as a String
	 * @return longitude and latitude of the address
	 */
	public double[] getCoordinates(String address) {
		logger.info("Executing Geocode Request for "+address);
		GeocodingResult[] result = null;
		double coord[] = {0.0,0.0};
		try {
			result = GeocodingApi.geocode(context, address).await();
			coord[0] = result[0].geometry.location.lat;
			coord[1] = result[0].geometry.location.lng;
		} catch (Exception e1) {
			logger.debug(e1.getMessage());
		}
        return coord;
	}
}
