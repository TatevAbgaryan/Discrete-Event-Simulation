package com.example.tatev.truckworkmodel;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.tatev.truckworkmodel.components.Truck;
import com.example.tatev.truckworkmodel.jdbstemplates.TruckJDBSTemplate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

        TruckJDBSTemplate studentJDBCTemplate =
                (TruckJDBSTemplate)context.getBean("TruckJDBCTemplate");

//        System.out.println("------Records Creation--------" );
//        studentJDBCTemplate.create("Zara", 11);
//        studentJDBCTemplate.create("Nuha", 2);
//        studentJDBCTemplate.create("Ayan", 15);

        List<Truck> trucks = studentJDBCTemplate.listTrucks();
        for (Truck record : trucks) {
            System.out.print("ID : " + record.getTruckID() );
            System.out.print(", RecoveryTime : " + record.getRecoveryTime() );
            System.out.println(", StartTime : " + record.getStartTime());
            System.out.println(", Speed : " + record.getSpeed());
        }


        System.out.println("----Listing Record with ID = 2 -----" );
        Truck truck = studentJDBCTemplate.getTruck(2);
        System.out.print("ID : " + truck.getTruckID() );
        System.out.print(", RecoveryTime : " + truck.getRecoveryTime() );
        System.out.println(", StartTime : " + truck.getStartTime());
        System.out.println(", Speed : " + truck.getSpeed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
