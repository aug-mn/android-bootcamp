package cs.android.aug;

import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;


public class Mapping {
	
	private String CLASS_NAME = "Mapping";
	
	//objects
	private MapController  mapController = null;
	private LocationManager locationManager = null;	
	private LocationListener locationListener = null;
	private MapView mapView = null;
	private Location location = null;
	private GeoPoint point = null;
	private List<Overlay> overlays = null;
	private String provider = "";
	
	//primitives	
	private double lat = 0;
	private double lng = 0;
	private boolean locationFound = false;
	private int indexLocationUpdate = 0;
	private boolean locationManagerSet = false;
	
	
	
	private void getMap() {
		
		try{
			
			mapController = mapView.getController();
			mapView.setBuiltInZoomControls(true);
	        mapView.displayZoomControls(true);
			
		}catch(Exception e){
			Log.v(CLASS_NAME,"[getMap] Error: "+e.getMessage());			
		}	
		
	}
	
	
	// constructor
	public Mapping(MapView _mapView)
	{
		mapView = _mapView;		
		getMap();
	}
	
	public void finalize()
	{
		mapController = null;
	}
	
	public MapController getMapController() {
		return mapController;
	}

	public MapView getMapView() {
		return mapView;
	}

	public void setLocationManager(LocationManager _locationManager) {
		this.locationManager = _locationManager;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public void setLocationListener(LocationListener _locationListener) {
		this.locationListener = _locationListener;
	}

	public LocationListener getLocationListener() {
		return locationListener;
	}
	
	public void setLat(double _lat) {
		this.lat = _lat;
	}

	public double getLat() {
		return lat;
	}

	public void setLng(double _lng) {
		this.lng = _lng;
	}

	public double getLng() {
		return lng;
	}

	public void setLocation(Location _location) {
		this.location = _location;
	}

	public Location getLocation() {
		return location;
	}
	
	public void killLocationProvider(){
		
		try{			
			//locationManagerSet = false;
			try{locationManager.removeUpdates(locationListener);}catch(Exception e){}		
			locationManager = null;
			locationListener = null;	
			
		}catch(Exception e){
			Log.v(CLASS_NAME,"[killLocationProvider] Error: "+e.getMessage());
    	}
		
	}
	
	public void setLocationFound(boolean _locationFound)
	{
		this.locationFound = _locationFound;
	}
	
	public boolean isLocationFound()
	{
		return this.locationFound;
	}

	public void setIndexLocationUpdate(int _indexLocationUpdate) {
		this.indexLocationUpdate = _indexLocationUpdate;
	}

	public int getIndexLocationUpdate() {
		return this.indexLocationUpdate;
	}
	
	public void setLocationManagerSet(boolean _locationManagerSet) {
		this.locationManagerSet = _locationManagerSet;
	}
	
	public boolean isLocationManagerSet() {
		return locationManagerSet;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProvider() {
		return provider;
	}

	public void animateToCurrentLocation(boolean zoom) {
		
		try{
			
			if (zoom){
				mapController.setZoom(16);																	
			}							
			
			try{
				Double geoLat = location.getLatitude()*1E6;
				Double geoLng = location.getLongitude()*1E6;
				point = new GeoPoint(geoLat.intValue(),geoLng.intValue());	
			}catch(Exception e){
				
			}
			
			// go to the location
			mapController.animateTo(point);
			
			//killLocation();
			
		}catch(Exception e){
			Log.v(CLASS_NAME,"[animateToCurrentLocation] Error: "+e.getMessage());			
		}		
		
	}

	public void setOverlays(List<Overlay> overlays) {
		this.overlays = overlays;
	}

	public List<Overlay> getOverlays() {			
		return overlays;
	}
	
	
	
	
}
