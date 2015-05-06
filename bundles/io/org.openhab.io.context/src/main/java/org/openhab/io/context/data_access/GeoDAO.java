package org.openhab.io.context.data_access;

import org.openhab.io.context.ContextService;
import org.openhab.io.context.primitives.Location;
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
	
	public String getOriginAddress(Location l) {
		DistanceMatrix req = null;
		try {
            req = DistanceMatrixApi.newRequest(context)
	        .origins(new LatLng(l.getLatitude(), l.getLongitude()))
	        .destinations(new LatLng(ContextService.HOME_LATITUDE, ContextService.HOME_LONGITUDE))
	        .await();
		}
		catch(Exception e)
		{
			logger.debug("org.openhab.core.context.location exception"+e.toString());
		}
		return req.originAddresses[0];
	}
	
	public double[] getCoordinates(String address) {
		GeocodingResult[] result = null;
		try {
			result = GeocodingApi.geocode(context, address).await();
		} catch (Exception e1) {
			logger.debug(e1.getMessage());
		}
		double coord[] = {result[0].geometry.location.lat, result[0].geometry.location.lng};
        return coord;
	}
}
