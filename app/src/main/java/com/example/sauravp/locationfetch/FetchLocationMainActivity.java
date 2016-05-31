package com.example.sauravp.locationfetch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FetchLocationMainActivity extends Activity implements LocationListener {
    Button but1;
    Button but2;
    Button but3;
    Button but4;
    Location lastKnownLocation = null;
    LocationManager locationManager = null;
    LocationManager nlocationManager = null;
    String tail = "</trkseg></trk></gpx>";
    boolean isStopClicked = false;
    final boolean saurav = true;

    @SuppressWarnings("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_location_main);
        but1 = (Button) findViewById(R.id.button);
        but2 = (Button) findViewById(R.id.button2);
        but3 = (Button) findViewById(R.id.button3);
        but4 = (Button) findViewById(R.id.button4);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        but2.setVisibility(View.INVISIBLE);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                but1.setVisibility(View.INVISIBLE);
                but2.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Writing to File started!", Toast.LENGTH_SHORT).show();

                startWritingToFile();

            }
        });
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                but2.setVisibility(View.INVISIBLE);
                but1.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Writing to File stopped!", Toast.LENGTH_SHORT).show();
                isStopClicked = true;
                stopWritingToFileandAppendFiles();
                Toast.makeText(getApplicationContext(),"Find the GPX file at: /Download",Toast.LENGTH_LONG).show();
            }
        });
        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lastKnownLocation = getLastKnownLocation();
                if (lastKnownLocation != null) {
                    //Toast.makeText(getApplicationContext(),"Last Location Obtained!"+ "Lat:"+lastKnownLocation.getLatitude(),Toast.LENGTH_SHORT).show();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    //sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, "I am currently at:Latitude:" + lastKnownLocation.getLatitude() + " Longitude:" + lastKnownLocation.getLongitude() + " Altitude:" + lastKnownLocation.getAltitude()
                            + "Speed:" + lastKnownLocation.getSpeed() + " Heading:" + lastKnownLocation.getBearing());
                    startActivity(sharingIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Location Unknown, Perform some GPS activities like opening GMAPS and try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
        but4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saurav test code start
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/download");
                File file = new File(dir, "myData.gpx");
                //saurav test code end
                if (file.exists()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "File Has not been created yet!", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Log.d("hello", "deb@g Time is:" + currentDateandTime + " Latitude:" + location.getLatitude() + "  Longitude:" + location.getLongitude() + "\n");
        Toast.makeText(getApplicationContext(),"Data getting Fetched from GPS!",Toast.LENGTH_SHORT).show();
        /*   Toast.makeText(getApplicationContext(),
                "Latitude:" + location.getLatitude() +
                        " \nLongitude:" + location.getLongitude() +
                        " \nAltitude:" + location.getAltitude() +
                        " \nSpeed:" + location.getSpeed() +
                        " \nTime:" + location.getTime()
                , Toast.LENGTH_SHORT).show();*/
        String writeToFile = "<trkpt lat=\"" + location.getLatitude() +
                "\" lon=\"" + location.getLongitude() +
                "\"><time>" + location.getTime() + "</time> " +
                "<altitude>"+ location.getAltitude() + "</altitude>"+
                "<speed>"+ location.getSpeed()+ "</speed>"+
                "<heading>"+location.getBearing()+"</heading>"+
                "</trkpt>\n";
        //saurav new code end
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/download");
        dir.mkdirs();
        File file = new File(dir, "myData.gpx");
        try {
            FileOutputStream f = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(f, true);
            pw.println(writeToFile);
            pw.println("\n");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("saurav", "Exception is : " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("hello", "deb@g OnDestroy");
        locationManager.removeUpdates(this);
    }

    @SuppressWarnings("MissingPermission")
    private void stopWritingToFileandAppendFiles() {
        locationManager.removeUpdates(this);
        locationManager.removeUpdates(this);
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/download");
        File file = new File(dir, "myData.gpx");
        try {
            FileOutputStream f = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(f, true);
            pw.println(tail);
            pw.println("\n");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("saurav", "Excpetion is : " + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void startWritingToFile() {
        File root = android.os.Environment.getExternalStorageDirectory();

        File dir = new File(root.getAbsolutePath() + "/download");
        dir.mkdirs();
        //saurav new code start
        File file = new File(dir, "myData.gpx");
        if (file.exists()) {
            boolean isDeleteSuccess = file.delete();
            Log.d("hello", "deb@g File deleted before creating a new one on every start!");
        } else {
            Log.d("hello", "deb@g File does not exist!");
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d("hello", "deb@g NETWORK_PROVIDER registered");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("hello", "deb@g GPS_PROVIDER registered");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }
        String head = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><gpx xmlns=\"h" +
                "ttp://https://github.com/sauravpradhan/GPX/1/1\" creator=\"Saurav\" version=" +
                "\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation" +
                "=\"http://www.topografix.com/GPX/1/1 http://https://github.com/sauravpradhan/GPX/1/1/gpx" +
                ".xsd\"><trk><trkseg>\n";
        if (!file.exists()) {
            try {
                FileOutputStream f = new FileOutputStream(file);
                PrintWriter pw = new PrintWriter(f);
                pw.println(head);
                pw.println("\n");
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i("saurav", "Excpetion is : " + e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Location getLastKnownLocation() {
        nlocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = nlocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            //noinspection MissingPermission
            Location l = nlocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
