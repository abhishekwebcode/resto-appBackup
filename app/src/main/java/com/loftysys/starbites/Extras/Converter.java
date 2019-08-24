package com.loftysys.starbites.Extras;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

public class Converter {
    public static String  getString(Response response) throws Exception {
        BufferedReader r = new BufferedReader(new InputStreamReader(response.getBody().in()));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            total.append(line);
        }
        return total.toString();
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
        public Branch(String a,String b) {
            this.id=a;
            this.name=b;
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
           null,"Select Branch"
        ));
            for (int i = 0; i < branchesArray.length(); i++) {
                branches.add(new Branch(
                        branchesArray.getJSONObject(i).getString("branch_id"),
                        branchesArray.getJSONObject(i).getString("branch_name")
                ));
            }
            return branches;
    }
}
