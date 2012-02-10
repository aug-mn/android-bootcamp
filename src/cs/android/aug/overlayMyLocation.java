package cs.android.aug;

import com.google.android.maps.*;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.util.Log;

public class overlayMyLocation extends Overlay{
	
	private String CLASS_NAME = "overlayMyLocation";
		
	private Location location = null;
		
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow){
		
		try{
			Double lat = location.getLatitude()*1E6;
			Double lng = location.getLongitude()*1E6;
						
			// get the map projection
			Projection projection = mapView.getProjection();
			
			// create point in gps coordinates that have been converted to microdegrees
			GeoPoint geoPoint = new GeoPoint(lat.intValue(),lng.intValue());
						
			// create point in screen coordinates
			Point point = new Point();
			projection.toPixels(geoPoint, point);
			
			// create and s/u paint brush
			Paint paint = new Paint();
			paint.setARGB(250, 30, 144, 255);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			// create and s/u paint brush
			Paint paintSecondary = new Paint();
			paintSecondary.setARGB(250, 0, 0, 255);
			paintSecondary.setAntiAlias(true);
			paintSecondary.setFakeBoldText(true);
			
			// create the circle
			int rad = 4;								
			int opacity = 75;
			int intColor = Color.argb(opacity, 255, 255, 0);
			Paint hl = new Paint(Paint.ANTI_ALIAS_FLAG);
			hl.setColor(intColor);
			
			RectF oval = new RectF(point.x-rad, point.y-rad, point.x+rad, point.y+rad);
			canvas.drawOval(oval, paint);
			canvas.drawText("Me", point.x+rad, point.y, paint);			

        }catch(Exception e){
        	Log.v(CLASS_NAME,"[draw] Error: <"+e.getMessage());
        }	
	}
	
	@Override
	public boolean onTap(GeoPoint point, MapView mapView){
		
		return true;
		
	}
	
	public void setMyLocation(Location _location) {
		this.location = _location;
	}
	
}
