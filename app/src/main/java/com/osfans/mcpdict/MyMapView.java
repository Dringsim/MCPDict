package com.osfans.mcpdict;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.Style;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.io.IOException;

public class MyMapView extends MapView {
    FolderOverlay mHzOverlay;

    public MyMapView(Context context) {
        super(context);
    }

    public MyMapView(Context context, String hz) {
        this(context);
        init(hz);
    }

    public void show() {
        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_DayNight_NoActionBar)
                .setView(this)
                .show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    public void init(String hz) {
        //setTileSource(TileSourceFactory.MAPNIK);
        addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (getOverlays().contains(mHzOverlay)) {
                    Double level = event.getZoomLevel();
                    for(Overlay item: mHzOverlay.getItems()) {
                        ((MyMarker)item).setZoomLevel(level);
                    }
                    invalidate();
                }
                return true;
            }
        });
        setMultiTouchControls(true);
        getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        setMinZoomLevel(4d);
        setMaxZoomLevel(20d);
//        GroundOverlay chinaOverlay = new GroundOverlay();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.china);
//        chinaOverlay.setImage(bitmap);
//        chinaOverlay.setPosition(new GeoPoint(57.5d, 67.1d), new GeoPoint(-6.9d, 141.4d));
//        chinaOverlay.setTransparency(0.5f);
        FolderOverlay geoOverlay = geoJsonifyMap("china.geojson");
        CopyrightOverlay copyrightOverlay = new CopyrightOverlay(getContext()) {
            @Override
            public void setCopyrightNotice(String pCopyrightNotice) {
                super.setCopyrightNotice(getResources().getString(R.string.app_name)+"【"+ hz + "】");
            }
        };
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(this);
        scaleBarOverlay.setAlignBottom(true);
        scaleBarOverlay.setAlignRight(true);
        mHzOverlay = initHZ(hz);

        //getOverlays().add(chinaOverlay);
        getOverlays().add(geoOverlay);
        getOverlays().add(copyrightOverlay);
        getOverlays().add(scaleBarOverlay);
        getOverlays().add(mHzOverlay);
        invalidate();

        // Workaround for osmdroid issue
        // See: https://github.com/osmdroid/osmdroid/issues/337
        addOnFirstLayoutListener((v, left, top, right, bottom) -> {
            BoundingBox boundingBox = (mHzOverlay.getItems().size() >= 3 ? mHzOverlay : geoOverlay).getBounds();
            // Yep, it's called 2 times. Another workaround for zoomToBoundingBox.
            // See: https://github.com/osmdroid/osmdroid/issues/236#issuecomment-257061630
            zoomToBoundingBox(boundingBox, false);
            zoomToBoundingBox(boundingBox, false);
            invalidate();
        });
    }
    
    private FolderOverlay initHZ(String hz) {
        Cursor cursor = DB.directSearch(hz);
        cursor.moveToFirst();
        Context context = getContext();
        FolderOverlay folderOverlay = new FolderOverlay();
        for (String lang: DB.getVisibleColumns(context)) {
            GeoPoint point = DB.getPoint(lang);
            if (point == null) continue;
            int i = DB.getColumnIndex(lang);
            String string = cursor.getString(i);
            if (TextUtils.isEmpty(string)) continue;
            CharSequence yb = DictApp.formatIPA(lang,  DictApp.getRawText(string));
            CharSequence js = DictApp.formatIPA(lang,  string);
            int size = DB.getSize(lang);
            MyMarker marker = new MyMarker(this, DB.getColor(lang), DB.getLabel(lang), yb.toString() , js.toString(), size);
            marker.setPosition(point);
            folderOverlay.add(marker);
        }
        return folderOverlay;
    }

    public FolderOverlay geoJsonifyMap(String fileName) {
        final KmlDocument kmlDocument = new KmlDocument();

        try {
            kmlDocument.parseGeoJSON(FileUtils.getStringFromAssets(fileName, getContext()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Style defaultStyle = new Style(null, 0x1f000000, 2f, 0xffffffff);
        return (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(this, defaultStyle, null, kmlDocument);
    }
}
