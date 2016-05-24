package graduation.tatev.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import graduation.tatev.myapplication.Utils.OnTaskCompleted;
import graduation.tatev.myapplication.Utils.RouteUtlis;
import graduation.tatev.myapplication.Utils.Utils;
import graduation.tatev.myapplication.components.Animation;
import graduation.tatev.myapplication.components.Container;
import graduation.tatev.myapplication.components.GraphEdge;
import graduation.tatev.myapplication.components.Terminal;
import graduation.tatev.myapplication.components.Truck;
import graduation.tatev.myapplication.events.ContainerReadyEvent;

public class ActivityDes extends FragmentActivity implements OnMapReadyCallback, OnTaskCompleted {

    private static int SIMULATION_DURATION = 3 * 7 * 24 * 60; //three week in minutes
    private static float UNIT = 15; //three week in minutes
    private static float TRUCK_CAPACITY = 2; //three week in minutes
    private static float TIME_SCALE = 7200; // max route duration is aproximatly 5 days = 7200 min -> 1 min animation
    private static int MILLISECOND = 60000;
    private List<ContainerReadyEvent> initialEvents = new ArrayList<>();
    private static double[][] shortestDurationsMatrix;
    private static int[][] shortestRoutesMatrix;
    private List<Terminal> terminals;
    private Map<Terminal, List<List<ContainerReadyEvent>>> terminalListMap;
    private Date startDate = new Date();
    private List<Truck> truckList;
    private List<Animation> animationList;
    private GoogleMap mMap;
    private List<GraphEdge> graphEdges;
    public List<PolylineOptions> poliLinesMap;
    private Bitmap terminalBitmap, truckBitmap, delayedTruckBitmap;
    private Handler animHandler;
    private int countOfDelayedContainers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);

        //load animation bitmaps
        terminalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.color_icons_green_home);
        terminalBitmap = Bitmap.createScaledBitmap(terminalBitmap, 30, 45, true);
        truckBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.truck);
        truckBitmap = Bitmap.createScaledBitmap(truckBitmap, 30, 30, true);
        delayedTruckBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.delayed_truck);
        delayedTruckBitmap = Bitmap.createScaledBitmap(delayedTruckBitmap, 30, 30, true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                // get terminals from db
                try {
                    terminals = ConnectionService.getTerminalDao().getAllTerminals();
                    shortestDurationsMatrix = new double[terminals.size()][terminals.size()];
                    shortestRoutesMatrix = new int[terminals.size()][terminals.size()];
                } catch (Exception e) {
                    Log.d("ExaptionGetTermianls", e.toString());
                }
                graphEdges = new ArrayList<GraphEdge>();
                // get the graph and count all pair shortest paths
                Utils.fillAllPairShortestPaths(shortestDurationsMatrix, shortestRoutesMatrix, graphEdges);
                // get initial events and stating date
                Utils.getEventList(initialEvents, startDate);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                Log.d("onPostExecute", "onPostExecute");
                // after getting terminals and having shortest durations for each pair
                // mark terminals on map, draw graph and start simulation
                List<Marker> terminalMarkers = new ArrayList<>();
                for (Terminal terminal : terminals) {
                    terminalMarkers.add(mMap.addMarker(createMarker(BitmapDescriptorFactory.fromBitmap(terminalBitmap), terminal.getLatitude(), terminal.getLongitude(), terminal.getName())));
                }
                drawGraph(terminalMarkers);
                beginSimulation();
            }
        }.execute();
    }

    public void beginSimulation() {
        truckList = new ArrayList<>();
        animationList = new ArrayList<>();
        Random random = new Random();
        terminalListMap = new HashMap<>();
        for (Terminal terminal : terminals) {
            List<List<ContainerReadyEvent>> terminalEvents = Arrays.asList((List<ContainerReadyEvent>[]) new ArrayList[(int) (SIMULATION_DURATION / UNIT)]);
            for (ContainerReadyEvent event : initialEvents) {
                if (event.getDepartureTerminal().equals(terminal)) {
                    event.setType(ContainerReadyEvent.Type.DEPARTURE);
                    int tripDuration = (int) shortestDurationsMatrix[terminals.indexOf(event.getDepartureTerminal())][terminals.indexOf(event.getDestinationTerminal())];
                    event.getConteiner().setRecoveryTime(new Date(event.getStartTime().getTime() + tripDuration * MILLISECOND));
                    int randomDelay = random.nextInt(60) - 30; // [-30,30] minute delay
                    event.getConteiner().setArrivalTime(new Date(event.getStartTime().getTime() + (tripDuration + randomDelay) * MILLISECOND));
                    event.getConteiner().setReadyTime(event.getStartTime());
                    if(event.getConteiner().getRecoveryTime().before(event.getConteiner().getArrivalTime()))  countOfDelayedContainers+=event.getConteiner().getQuantity();
                    long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(event.getStartTime().getTime() - startDate.getTime());
                    int bucketIndex = (int) Math.ceil(diffInMinutes / UNIT);
                    if (terminalEvents.get(bucketIndex) == null) {
                        List<ContainerReadyEvent> list = new ArrayList<ContainerReadyEvent>();
                        list.add(event);
                        terminalEvents.set(bucketIndex, list);
                    } else {
                        terminalEvents.get(bucketIndex).add(event);
                    }
                }

            }
            terminalListMap.put(terminal, terminalEvents);
        }
        Utils.updateContainer(initialEvents);
        int currentBucket = 0;
        while (currentBucket < SIMULATION_DURATION) {
            for (Terminal terminal : terminals) {
                List<ContainerReadyEvent> currentEvents = terminalListMap.get(terminal).get((int) (currentBucket / UNIT));
                if (currentEvents == null) continue;
                Map<Terminal, List<Container>> nowInTerminal = new HashMap<>();
                List<Truck> trucksFromCurrentTerminal = new ArrayList<>();
                for (ContainerReadyEvent event : currentEvents) {
                    // if it's destination terminal just remove event from list, otherwise detect next terminal for current containers
                    // group all containers in terminal by next terminal to understand how many trucks we need to be add in simulation
                    // and schedule next event.
                    if (!event.getDestinationTerminal().equals(terminal)) {
                        Terminal nextTerminal = terminals.get(shortestRoutesMatrix[terminals.indexOf(terminal)][terminals.indexOf(event.getDestinationTerminal())]);
                        // count containers in terminal from this event
                        Container passContainer = new Container(event.getConteiner());
                        passContainer.setDepartureTerminal(terminal);
                        passContainer.setDestinationTerminal(nextTerminal);
                        // group by destination terminals
                        if (nowInTerminal.containsKey(nextTerminal))
                            nowInTerminal.get(nextTerminal).add(passContainer);
                        else {
                            ArrayList<Container> list = new ArrayList<>();
                            list.add(passContainer);
                            nowInTerminal.put(nextTerminal, list);
                        }
                        // schedule next event
                        ContainerReadyEvent next = new ContainerReadyEvent(event);
                        next.setDepartureTerminal(nextTerminal);
                        double durationToNextTerminal = shortestDurationsMatrix[terminals.indexOf(terminal)][terminals.indexOf(nextTerminal)];
                        next.setStartTime(new Date((long) (event.getStartTime().getTime() + durationToNextTerminal * MILLISECOND)));
                        next.getConteiner().setReadyTime(new Date((long) (event.getStartTime().getTime() + durationToNextTerminal * MILLISECOND)));
                        int nextBucketIndex = (int) (currentBucket / UNIT + (int) Math.ceil(durationToNextTerminal / UNIT));
                        if (terminalListMap.get(nextTerminal).get(nextBucketIndex) == null) {
                            List<ContainerReadyEvent> list = new ArrayList<ContainerReadyEvent>();
                            list.add(next);
                            terminalListMap.get(nextTerminal).set(nextBucketIndex, list);
                        } else {
                            terminalListMap.get(nextTerminal).get(nextBucketIndex).add(next);
                        }
                    }
                    terminalListMap.get(terminal).get((int) (currentBucket / UNIT)).remove(event);

                    //and now for current Terminal I have Terminal -> container1, container2... which need to be in th same "NextTerminal"
                    //so count of containers is enough to decide how many trucks I need to add
                    //but i must reuse trucks which are allready in simaltion and now in current Terminal
                    for (Map.Entry<Terminal, List<Container>> entry : nowInTerminal.entrySet()) {
                        int sumOfContainers = 0;
                        int sumOfDelayedContainers = 0;
                        for (Container container : entry.getValue()) {
                            sumOfContainers += container.getQuantity();
                            if (container.isDelayed())
                                sumOfDelayedContainers += container.getQuantity();
                        }
                        Animation animation = new Animation();
                        animation.setInitialTerminal(terminal);
                        animation.setFinalTerminal(entry.getKey());
                        animation.setDuration(MILLISECOND * (shortestDurationsMatrix[terminals.indexOf(terminal)][terminals.indexOf(entry.getKey())] / TIME_SCALE));
                        double offset = nowInTerminal.get(entry.getKey()).get(0).getReadyTime().getTime() - startDate.getTime();
                        offset = (offset / TIME_SCALE);
                        animation.setOffset((int) offset);
                        if (sumOfDelayedContainers != 0) {
                            animation.setCountOfTrucks((int) Math.ceil(sumOfDelayedContainers / TRUCK_CAPACITY));
                            animation.setIsDelayedAnim(true);
                            animationList.add(animation);
                        }
                        if (sumOfDelayedContainers != sumOfContainers) {
                            animation.setCountOfTrucks((int) Math.ceil(sumOfContainers / TRUCK_CAPACITY) - (int) Math.ceil(sumOfDelayedContainers / TRUCK_CAPACITY));
                            animation.setIsDelayedAnim(false);
                            animationList.add(animation);
                        }

                        Truck truck = new Truck();
                        int duration = (int) shortestDurationsMatrix[terminals.indexOf(terminal)][terminals.indexOf(entry.getKey())];
                        truck.setArrivalTime(new java.sql.Date(event.getStartTime().getTime() + duration * MILLISECOND));
                        truck.setInitialTerminal(terminal);
                        truck.setFinalTerminal(entry.getKey());
                        truck.setStartTime(new java.sql.Date(event.getStartTime().getTime()));
                        truck.setSpeed(80);
                        for (int i = 0; i < Math.ceil(sumOfContainers / TRUCK_CAPACITY); i++)
                            trucksFromCurrentTerminal.add(truck);
                    }
                    // this is list of all trucks we need to send from current terminal
                    // merge this with trucks allready in terminal
                    syncTrucksInTerminal(terminal, trucksFromCurrentTerminal, currentBucket);
                    // remaining trucks add to list
                    truckList.addAll(trucksFromCurrentTerminal);
                }
            }
            Log.d("currentBucket", currentBucket + "");
            currentBucket += UNIT;
        }
        Log.d("truckCount", truckList.size() + "");
        Utils.saveTrucks(truckList);

        Toast.makeText(this, truckList.size() + " trucks are needed to transport the containers", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext() ,countOfDelayedContainers + " delayed container(s)", Toast.LENGTH_SHORT).show();
            }
        },500);

    }

    // merged trucks if there are trucks in terminal
    private void syncTrucksInTerminal(Terminal currentTerminal, List<Truck> trucksFromCurrentTerminal, int bucket) {
        int k = trucksFromCurrentTerminal.size() - 1;
        for (int i = 0; i < truckList.size(); i++) {
            if (k != -1) {
                Date arrival = new Date(truckList.get(i).getArrivalTime().getTime());
                long arrivalInMinutes = TimeUnit.MILLISECONDS.toMinutes(arrival.getTime() - startDate.getTime());
                // first condition-> truck's targert is current terminal, second -> it will be in terminal just at the observing time
                if (truckList.get(i).getFinalTerminal().equals(currentTerminal) && (int) Math.ceil(arrivalInMinutes / UNIT) == bucket / UNIT) {
                    truckList.set(truckList.indexOf(truckList.get(i)), trucksFromCurrentTerminal.get(k));
                    trucksFromCurrentTerminal.remove(trucksFromCurrentTerminal.get(k));
                    k--;
                }
            }
        }
    }

    private void drawGraph(List<Marker> terninalMarkers) {
        RouteUtlis utlis = new RouteUtlis(this);
        utlis.drawRoutes(graphEdges, getApplicationContext());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : terninalMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 20; // offset from edges of the map in pixels
        try {
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MarkerOptions createMarker(BitmapDescriptor bitmap, double latitude, double longitude, String title) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(bitmap);
        return markerOptions;
    }

    @Override
    public void onTaskCompleted(List<PolylineOptions> polyLines) {
        Log.d("completed", polyLines.toString());
        this.poliLinesMap = polyLines;
        for (PolylineOptions polylineOptions : polyLines) {
            mMap.addPolyline(polylineOptions);
        }
        showTruckMotion();
    }

    class TruckAnimation implements Runnable {
        private static final float STEP = 100;
        long duration;
        Marker truckMarker;
        List<LatLng> points;
        int currentStep = 0;
        int timeInterval;

        TruckAnimation(GraphEdge edge, int duration, int countOfTrucks, BitmapDescriptor truck) {
            this.duration = duration;
            truckMarker = mMap.addMarker(createMarker(truck, edge.getSourceTerminal().getLatitude(), edge.getSourceTerminal().getLongitude(), countOfTrucks + ""));
            truckMarker.showInfoWindow();
            points = poliLinesMap.get(graphEdges.indexOf(edge)).getPoints();
            DecimalFormat decFormat = new DecimalFormat("##.##");

            if (!decFormat.format(edge.getSourceTerminal().getLatitude()).equals(decFormat.format(points.get(0).latitude)))
                Collections.reverse(points);
            timeInterval = (int) (duration / (points.size() / STEP));
        }

        @Override
        public void run() {
            truckMarker.setPosition(points.get(currentStep));
            currentStep += STEP;
            if (currentStep <= points.size())
                animHandler.postDelayed(this, timeInterval);
            else
                truckMarker.setVisible(false);
        }
    }

    private void showTruckMotion() {
        animHandler = new Handler();
        for (Animation animInfo : animationList) {
            BitmapDescriptor icon = animInfo.isDelayedAnim() ? BitmapDescriptorFactory.fromBitmap(delayedTruckBitmap) : BitmapDescriptorFactory.fromBitmap(truckBitmap);
            TruckAnimation anim = new TruckAnimation(new GraphEdge(animInfo.getInitialTerminal(), animInfo.getFinalTerminal()),
                    (int) animInfo.getDuration(), animInfo.getCountOfTrucks(), icon);
            animHandler.postDelayed(anim, animInfo.getOffset());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

}
