package com.loftysys.starbites.Extras;

import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;


public class Converter {

    public static class pin {
        public static List<String> getPins(ArrayList<pin> tables) {
            ArrayList<String> f=new ArrayList<>();
            for (int i = 0; i < tables.size(); i++) {
                f.add(tables.get(i).pincode);
            }
            return f;
        }
        @Override
        public String toString() {
            return pincode;
        }

        public String id="";
        public String pincode="";
        public String locationPrice="";
        public pin(String a ,String b,String c){
            this.id=a;
            this.pincode=b;
            this.locationPrice=c;
        }
        public static ArrayList<pin> getPins(JSONArray array)throws Exception {
            ArrayList<pin> pins = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                pins.add(new pin(
                        array.getJSONObject(i).getString("id"),
                        array.getJSONObject(i).getString("pincode"),
                        array.getJSONObject(i).getString("location_price")
                ));
            }
            return pins;
        }
    }

    public static String  getString(Response response) throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(response.getBody().in()));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            total.append(line);
        }
        return total.toString();
    }
    public static void logResponse(Response response) {
        try {
            Log.d("LOG CONVERTER",getString(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static public class Table {
        public String id;
        public String name;
        public Table(String a , String b) {
            this.id=a;
            this.name=b;
        }
        public static List<String> getTableLi(ArrayList<Table> tables) {
            ArrayList<String> f=new ArrayList<>();
            for (int i = 0; i < tables.size(); i++) {
                f.add(tables.get(i).name);
            }return f;
        }
        public static ArrayList<Table> gettables(JSONArray array) throws Exception {
            ArrayList<Table> tables = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                tables.add(new Table(
                        array.getJSONObject(i).getString("table_id"),
                        array.getJSONObject(i).getString("table_name")
                ));
            }
            return tables;
        }
    }
    static public class Branch {
        public String id;
        public String name;
        public String price;
        public Branch(String a,String b,String c) {
            this.id=a;
            this.name=b;
            this.price=c;
        }
        public static List<String> getAdapterObject(ArrayList<Branch> branches) {
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < branches.size(); i++) {
                arrayList.add(branches.get(i).name);
            }
            return arrayList;
        }
    }
    public static ArrayList<Branch> getBranches(String json) throws Throwable {
            JSONArray branchesArray = new JSONArray(json);
            ArrayList<Branch> branches = new ArrayList<>();
        branches.add(new Branch(
           null,"Select Branch","0"
        ));
            for (int i = 0; i < branchesArray.length(); i++) {
                branches.add(new Branch(
                        branchesArray.getJSONObject(i).getString("branch_id"),
                        branchesArray.getJSONObject(i).getString("branch_name"),
                        "0"
                        //branchesArray.getJSONObject(i).getString("location_price")
                ));
            }
            return branches;
    }
}
