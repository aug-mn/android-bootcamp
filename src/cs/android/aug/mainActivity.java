package cs.android.aug;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;


public class mainActivity extends MapActivity {
	
	private String CLASS_NAME = "mainActivity";
	
	public static final String ACTION_TIMER_ACQUIRE_LOC = "cs.android.aug.TIMER";
	
	private Mapping mapping = null;
	private IntentFilter filterAcquireLocation = new IntentFilter(ACTION_TIMER_ACQUIRE_LOC);
	private receiverAcquireLocation receiverAcquireLocation = new receiverAcquireLocation();
	private Timer timerAcquireLocation;
	private Timer timerKillLocation;

	
	/****************************************************/
	// Activity events
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide title bar	    	
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        // inflate the view       
        setContentView(R.layout.main);
                
    }
    
    protected void onDestroy(){
    	
		try{		
						
		}catch(Exception e){
			Log.v(CLASS_NAME,"[onDestroy] Error: "+e.getMessage());
		}finally{
			super.onDestroy();
			android.os.Process.killProcess(android.os.Process.myPid());			
		}
		
	}
	
	@Override
	protected void onPause(){
		try{			
			
			killLocationProvider();
			try{timerAcquireLocation.cancel();}catch(Exception e){}
			timerAcquireLocation = null;
			try{unregisterReceiver(receiverAcquireLocation);}catch(Exception e){}			
			receiverAcquireLocation = null;
			
			mapping.finalize();
			mapping = null;
			
		}catch(Exception e){
			Log.v(CLASS_NAME,"[onPause] Error: "+e.getMessage());
		}finally{
			super.onPause();
		}
	}
    
	@Override
    public void onResume() {
		super.onResume();
		
		try{		
			
			// initialize
			uiInit();		
	        	        
		}catch(Exception e){
						
		}
				
	}
	
	@Override	
	protected boolean isRouteDisplayed() {
		
		return false;
	}
	
	
	/****************************************************/
	// Methods
	/****************************************************/
	private void uiInit() {			
		
		try{
			
			// verify internet connection - map will not work without it						
			if(!isOnline()){
				String message = "No active internet connection found.  App will close now";
				
				new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Internet connection required")
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {						
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})	
										
				.show();
				return;
			}
					
	        // create mapping class       
			mapping = new Mapping((MapView)findViewById(R.id.mapView));		
			
			// set location provider and register receiver						     	       
			registerReceiver(receiverAcquireLocation,filterAcquireLocation);
			startLocationTimer();
				        		
		}catch(Exception e){						
			Log.v(CLASS_NAME,"[uiInit] Error: "+e.getMessage());						
		}
				 
	}
	
	private boolean isOnline() {
		
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		try{
	        
			if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()){
	            return true;
	        }
	        	        	        
	        return false;
		
		}catch(Exception e){
			Log.v(CLASS_NAME,"[isOnline] Error: "+e.getMessage());
			return false;
		}finally{
			cm = null;
		}
	}
    
	private void startLocationTimer(){
		
		try{
						
			long period = 60000;
			timerAcquireLocation = new Timer(); 
			timerAcquireLocation.scheduleAtFixedRate(new TimerTask() { 
	        	@Override
	        	public void run() { 
	        		try{
	        			
	        			// check for location every 5 minutes
	        			mapping.setLocationFound(false);	        			
	        			Intent intent = new Intent(ACTION_TIMER_ACQUIRE_LOC);
	        			sendBroadcast(intent);
	        			
	        		}catch(Exception e){				        			
	        			Log.v(CLASS_NAME,"[timerAcquireLocation.scheduleAtFixedRate.run] Error: "+e.getMessage());	        				        			
	        		}	
	        	}
	        }, 0, period);
	        
		}catch(Exception e){				        			
			Log.v(CLASS_NAME,"[startLocationTimer] Error: "+e.getMessage());	        				        			
		}
		
	}
	
	private void killLocationProvider(){
		
		try{
			
			long delay = 20000;
			timerKillLocation = new Timer(); 
			timerKillLocation.schedule(new TimerTask() { 
	        	@Override
	        	public void run() { 
	        		try{
	        			mapping.killLocationProvider();	        			
	        		}catch(Exception e){				        			
	        			Log.v(CLASS_NAME,"[killLocation.scheduleAtFixedRate.run] Error: "+e.getMessage());	        				        			
	        		}	
	        	}
	        }, delay);
	        
		}catch(Exception e){				        			
			Log.v(CLASS_NAME,"[killLocation] Error: "+e.getMessage());	        				        			
		}		
		
	}
	
	private void createLocationProvider() {		 	
		
		try{
			mapping.setLocationManagerSet(true);
			String location_context = Context.LOCATION_SERVICE;
		    mapping.setLocationManager((LocationManager)this.getSystemService(location_context));						
									
			/** get all enabled providers*/			
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(false);
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			mapping.setProvider(mapping.getLocationManager().getBestProvider(criteria, true));
			if (mapping.getProvider().length()>0){
				/** s/u event listeners for each provider*/
				mapping.setLocationListener(new LocationListener(){
										public void onLocationChanged(Location _location){
											/** update information as it becomes available*/											
											mapping.setLocation(_location);
											mapping.setLocationFound(true);											
										}
										
										public void onProviderDisabled(String _location){
											//updateDisplay();
										}
										public void onProviderEnabled(String _location){
											//updateDisplay();
										}
										public void onStatusChanged(String _location, int status, Bundle extras){
											//updateDisplay();
										}
									});
								
				mapping.getLocationManager().requestLocationUpdates(mapping.getProvider(), 2000, 10, mapping.getLocationListener());
				
				/** extract the last known location from the provider*/
				mapping.setLocation(mapping.getLocationManager().getLastKnownLocation(mapping.getProvider()));				
				
			}						
		}catch(Exception e){
    		Log.v(CLASS_NAME,"[getLocation] Error: "+e.getMessage());
    	}				
	}
	
	private void updateOverlays(){
		
		try{
			
			mapping.setOverlays(mapping.getMapView().getOverlays());							
			List<Overlay> overlays = (List<Overlay>) mapping.getOverlays();
			// clear all existing overlays to be redrawn  
			// 	the map controller will take care of clean up
			overlays.clear();
			overlayMyLocation olay = new overlayMyLocation();			
			olay.setMyLocation(mapping.getLocation());
			overlays.add(olay);
			overlays = null;			
			
		}catch(Exception e){
			Log.v(CLASS_NAME,"[updateOverlays] Error: "+e.getMessage());
		}
		
	}
	
	
	/****************************************************/
	// Receivers
	/****************************************************/
	public class receiverAcquireLocation extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			try{
				
				// check location provider			
				if (mapping.isLocationFound()){						
					if(mapping.getIndexLocationUpdate() > 5){
						// to save battery, kill location provider for a period
						killLocationProvider();
						mapping.setIndexLocationUpdate(0);
					}
				}else{							
					if(!mapping.isLocationManagerSet()){
						createLocationProvider();					
					}				
				}
				
				mapping.animateToCurrentLocation(true);
				updateOverlays();
				
				
			}catch(Exception e){
				Log.v(CLASS_NAME,"[receiverAcquireLocation.onReceive] Error: "+e.getMessage());
			}
			
		}

	}



	
	
	
}