package edu.buffalo.cse.cse486586.simpledht;


import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.database.MatrixCursor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.content.Context;
import android.database.Cursor;
import static android.content.ContentValues.TAG;



class Node_Store implements Comparable<Node_Store>{


    String currentID;
    String hashed_currentID;
    String previous;
    String next;
    String code;
    String key;
    String value;
    String from;


    public Node_Store(){
        currentID = null ;
        hashed_currentID = null;
        previous = null;
        next = null;
        code = "";
        key = "";
        value = "";
        from = null;
    }

    public Node_Store(String ID, String hashed_currentID, String previous, String next, String type){
        this.currentID = ID;
        this.code = type;
        this.hashed_currentID = hashed_currentID;
        this.previous = previous;
        this.next = next;
        this.key = "";
        this.value = "";
        this.from = null;
    }





    @Override
    public int compareTo(Node_Store node) {
        String hash = node.hashed_currentID;
        return this.hashed_currentID.compareTo(hash);
    }
}

public class SimpleDhtProvider extends ContentProvider {


    ArrayList<Node_Store> chord_implementation = new ArrayList<Node_Store>();
    String db_controller="";

    HashMap<String, String> store = new HashMap<String, String>();
    Map<String,Integer> store_int = new HashMap<String,Integer>();



    dbHelper dbHelper;
    SQLiteDatabase db;
    Boolean checkif_globaldump = false;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {



        if (!selection.isEmpty() && selection.equals("@")) {

            db_controller="delete";



            return perform_db_op(selection,db_controller);

        } else if (!selection.isEmpty() && selection.equals("*")) {
            db_controller="delete";
            return perform_db_op(selection,db_controller);



        } else {
            db_controller="delete";
            return perform_db_op(selection,db_controller);


        }

    }

    public int perform_db_op(String criteria,String database_controller){



        if (!criteria.isEmpty() && database_controller.equals("@")) {


            store_int.put("number_of_deletes_performed",db.delete(dbHelper.TABLE_NAME, null, null));

            return store_int.get("number_of_deletes_performed");

        } else if (!criteria.isEmpty() && database_controller.equals("*")) {

            int deleteResult = db.delete(dbHelper.TABLE_NAME, null, null);
            store_int.put("number_of_deletes_performed",db.delete(dbHelper.TABLE_NAME, null, null));

            Node_Store node_alter = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "d_all");
            node_alter.key = criteria;
            node_alter.from = store.get("current_id");
            store.put("send",StringConstructor(node_alter));


            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

            return store_int.get("number_of_deletes_performed");

        } else {

            if(!criteria.isEmpty()) {

                String get_col = dbHelper.COL_2 + " ='" + criteria + "'";


                store_int.put("number_of_deletes_performed",db.delete(dbHelper.TABLE_NAME, get_col, null));


                if (store_int.get("number_of_deletes_performed") > 0) {
                    return store_int.get("number_of_deletes_performed");

                } else {
                    Node_Store node = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "d");
                    node.key = criteria;
                    node.from = store.get("current_id");
                    store.put("send",StringConstructor(node));


                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

                    return 0;
                }
            }
            return 0;

        }

    }





    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {


        try {

            if(store.get("next_id").equals(store.get("current_id")) && store.get("prev_id").equals(store.get("current_id")))
            {

                db.insert(dbHelper.TABLE_NAME, null, values);

            } else {


                try {

                    if(store.get("next_id").equals(store.get("current_id")) && store.get("prev_id").equals(store.get("current_id")))
                    {
                        db.insert(dbHelper.TABLE_NAME, null, values);

                    } else {

                        store.put("hkey_from_contentValue",genHash(values.getAsString("key")));
                        store.put("hcurrent",genHash(store.get("current_id")));
                        store.put("hnext",genHash(store.get("next_id")));
                        store.put("hprev",genHash(store.get("prev_id")));








                        if(store.get("hprev").compareTo(store.get("hcurrent")) > 0) {



                            if(store.get("hkey_from_contentValue").compareTo(store.get("hprev")) < 0 && store.get("hkey_from_contentValue").compareTo(store.get("hcurrent")) < 0){


                                db.insert(dbHelper.TABLE_NAME, null, values);


                            } else if(store.get("hkey_from_contentValue").compareTo(store.get("hprev")) > 0 && store.get("hkey_from_contentValue").compareTo(store.get("hcurrent")) > 0){


                                db.insert(dbHelper.TABLE_NAME, null, values);


                            } else{


                                try {
                                    Node_Store node_alter = new Node_Store(store.get("current_id"), genHash(store.get("current_id")), store.get("prev_id"), store.get("next_id"), "i");
                                    node_alter.key = values.getAsString("key");
                                    node_alter.value = values.getAsString("value");



                                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,StringConstructor(node_alter) );


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }  else if (store.get("hkey_from_contentValue").compareTo(store.get("hprev")) > 0 && store.get("hkey_from_contentValue").compareTo(store.get("hcurrent")) < 0) {



                            db.insert(dbHelper.TABLE_NAME, null, values);


                        } else {



                            try {
                                Node_Store node_alter = new Node_Store(store.get("current_id"), genHash(store.get("current_id")), store.get("prev_id"), store.get("next_id"), "i");
                                node_alter.key = values.getAsString("key");
                                node_alter.value = values.getAsString("value");



                                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, StringConstructor(node_alter));


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }



    @Override
    public boolean onCreate() {

        TelephonyManager tel = (TelephonyManager) this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));




        store.put("first_port","5554");
        store.put("server_port","10000");
        store.put("result","");
        store.put("global_Keys","");
        store.put("global_Val","");
        store.put("del_Val","");
        store.put("current_id","");
        store.put("next_id","");
        store.put("prev_id","");


        store.put("current_id",portStr);
        store.put("next_id",portStr);
        store.put("prev_id",portStr);




        dbHelper = new dbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS "+dbHelper.TABLE_NAME+" (id INTEGER PRIMARY KEY AUTOINCREMENT,key TEXT,value TEXT)");


        try {


            ServerSocket serverSocket = new ServerSocket(Integer.valueOf(store.get("server_port")));
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);

        } catch (Exception e) {
            Log.e(TAG, "Can't create a ServerSocket");
            return false;
        }


        if(store.get("current_id").equals(store.get("first_port"))){

            try{
                Node_Store node_alter = new Node_Store();
                node_alter.currentID =store.get("current_id");
                node_alter.next = store.get("next_id");
                node_alter.previous = store.get("prev_id");
                node_alter.hashed_currentID = genHash(store.get("current_id"));

                chord_implementation.add(node_alter);

            } catch(Exception e){
                e.printStackTrace();
            }

        } else {


            try{
                Node_Store node_alter = new Node_Store(store.get("current_id"), genHash(store.get("current_id")), store.get("prev_id"), store.get("next_id"), "j");
                store.put("send",StringConstructor(node_alter));
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"), store.get("current_id"));

            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return false;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {


        SQLiteDatabase queryDatabase = dbHelper.getReadableDatabase();

        try{

            if(!selection.isEmpty() && selection.equals("@")) {

                String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_NAME;
                Cursor cursor = queryDatabase.rawQuery(selectQuery, null);

                return cursor;

            } else if(!selection.isEmpty() && selection.equals("*")) {

                String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_NAME;
                Cursor cursor = queryDatabase.rawQuery(selectQuery, null);

                if(store.get("next_id").equals(store.get("current_id")) && store.get("prev_id").equals(store.get("current_id"))){
                    return cursor;
                }




                store.put("all_keys","");
                store.put("all_vals","");

                if (cursor != null) {
                    cursor.moveToFirst();
                    int keyIndex = cursor.getColumnIndex("key");
                    int valueIndex = cursor.getColumnIndex("value");

                    while (!cursor.isAfterLast()) {

                        store.put("all_keys",store.get("all_keys")+ cursor.getString(keyIndex) + "POINTBREAK");

                        store.put("all_vals",store.get("all_vals")+ cursor.getString(valueIndex) + "POINTBREAK");
                        cursor.moveToNext();

                    }
                }

                Node_Store node_alter = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "gq");
                node_alter.from = store.get("current_id");
                node_alter.key = store.get("all_keys");
                node_alter.value = store.get("all_vals");
                store.put("send",StringConstructor(node_alter));

                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

                while(!checkif_globaldump){

                }


                String [] get_keys = store.get("global_Keys").split("POINTBREAK");
                String [] get_vals = store.get("global_Val").split("POINTBREAK");

                MatrixCursor globalCursor = new MatrixCursor(new String[] { "key", "value"});

                for(int i = 0; i < get_keys.length; i++){
                    globalCursor.addRow(new String[] { get_keys[i], get_vals[i]});
                }


                store.put("global_Keys","");
                store.put("global_Val","");


                return globalCursor;

            } else {


                String[] columns = {"key", "value"};
                String keyValue = dbHelper.COL_2 + "=?";

                Cursor cursor = queryDatabase.query(dbHelper.TABLE_NAME, columns, keyValue, new String[]{selection}, null, null, null);


                if(cursor != null && cursor.getCount() > 0) {

                    if (cursor.moveToFirst())

                        return cursor;

                } else {

                    Node_Store node_alter = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "uq");
                    node_alter.key = selection;
                    node_alter.from = store.get("current_id");
                    store.put("send",StringConstructor(node_alter));

                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

                    while(store.get("result").equals("")){

                    }
                    MatrixCursor resultCursor = new MatrixCursor(new String[] { "key", "value"});
                    resultCursor.addRow(new String[] { selection, store.get("result")});
                    store.put("result","");

                    return resultCursor;
                }

            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }




    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }




    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];


            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();

                    DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

                    Node_Store build_message = null;



                    try {


                        build_message = deconstructor(dataInputStream.readUTF());

                        dataOutputStream.writeUTF("received");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    switch (build_message.code){
                        case "j":
                            server_join(build_message);
                            break;
                        case "u":
                            server_update(build_message);
                            break;
                        case "i":
                            server_insert(build_message);
                            break;
                        case "uq":
                            server_query(build_message);
                            break;
                        case "vf":
                            server_query_found(build_message);
                            break;
                        case "gq":
                            server_gQuery(build_message);
                            break;
                        case "gq_found":
                            server_gQuery_found(build_message);
                            break;
                        case "d":
                            server_delete(build_message);
                            break;
                        case "d_found":
                            server_delete_found(build_message);
                            break;
                        case "d_all":
                            server_delete_all(build_message);
                            break;


                    }



                    dataInputStream.close();;
                    dataOutputStream.close();
                }

            } catch(Exception e)
            {   e.printStackTrace();
                Log.e(TAG,"Socket Error");
            }
            return null;
        }

    }





    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... data) {

            try {


                store.put("data",data[0]);
                Node_Store decision_maker = deconstructor(store.get("data"));


                switch (decision_maker.code){
                    case "j":
                        client_j(store.get("data"));
                        break;
                    case "u":
                        client_u();
                        break;
                    case "i":
                        client_i(store.get("data"));
                        break;
                    case "uq":
                        client_i(store.get("data"));
                        break;
                    case "gq":
                        client_i(store.get("data"));
                        break;
                    case "d":
                        client_i(store.get("data"));
                        break;
                    case "d_all":
                        client_i(store.get("data"));
                        break;
                    case "vf":
                        client_f(store.get("data"));
                        break;
                    case "gq_found":
                        client_f(store.get("data"));
                        break;
                    case "d_found":
                        client_f(store.get("data"));
                        break;





                }



            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, " IOException");
            }

            return null;
        }
    }









    public String StringConstructor(Node_Store node){
        String formation =  node.currentID+"@@"+node.hashed_currentID+"@@"+node.previous+"@@"+node.next+"@@"+node.code+"@@"+node.key+"@@"+node.value+"@@"+node.from;


        return formation;


    }

    public Node_Store deconstructor(String str){
        Node_Store node_intermediary = new Node_Store();

        String[] msg = str.split("@@");

        node_intermediary.currentID = msg[0];
        node_intermediary.hashed_currentID = msg[1];
        node_intermediary.previous = msg[2];
        node_intermediary.next = msg[3];
        node_intermediary.code = msg[4];
        node_intermediary.key =  msg[5];
        node_intermediary.value = msg[6];
        node_intermediary.from = msg[7];

        return node_intermediary;

    }






    public void client_j(String data) throws IOException {


        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt("11108"));
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());


        try{

            dataOutputStream.writeUTF(data);
            dataOutputStream.flush();


        } catch(Exception e){

        }

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        String get_confirmation = dataInputStream.readUTF();
        if(get_confirmation.equals("received")) {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        }



    }

    public void server_join(Node_Store msg){


        chord_implementation.add(msg);
        Collections.sort(chord_implementation);



        for(int index = 0; index < chord_implementation.size(); index++){
            Node_Store node = chord_implementation.get(index);

            if(index == 0){
                node.previous = chord_implementation.get(chord_implementation.size() - 1).currentID;
                node.next = chord_implementation.get(index + 1).currentID;
                node.code = "u";

            }else if (index == chord_implementation.size() - 1){
                node.previous = chord_implementation.get(index - 1).currentID;
                node.next = chord_implementation.get(0).currentID;
                node.code = "u";

            } else {
                node.previous = chord_implementation.get(index - 1).currentID;
                node.next = chord_implementation.get(index + 1).currentID;
                node.code = "u";
            }

            chord_implementation.remove(index);
            chord_implementation.add(node);
            Collections.sort(chord_implementation);

        }

        msg.code = "u";
        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, StringConstructor(msg));




    }



    public void client_u() throws IOException {

        for(int i = 0; i < chord_implementation.size();i++){

            if(chord_implementation.get(i).currentID.equals("5554")) {
                store.put("next_id",chord_implementation.get(i).next);
                store.put("prev_id",chord_implementation.get(i).previous);



            } else {

                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(chord_implementation.get(i).currentID) * 2);

                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(StringConstructor(chord_implementation.get(i)));

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String get_confirmation = dataInputStream.readUTF();
                if(get_confirmation.equals("received")) {
                    dataOutputStream.close();
                    dataInputStream.close();
                    socket.close();
                }
            }

        }

    }

    public void server_update(Node_Store msg){

        store.put("next_id",msg.next);

        store.put("prev_id",msg.previous);


    }

    public void client_i(String data) throws IOException {
        Node_Store node = deconstructor(data);


        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(node.next) * 2);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        try{
            dataOutputStream.writeUTF(data);

        } catch (Exception e){
            e.printStackTrace();
        }

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        String get_confirmation = dataInputStream.readUTF();
        if(get_confirmation.equals("received")) {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        }
    }

    public void server_insert(Node_Store msg){

        ContentValues contentValues = new ContentValues();
        contentValues.put("key", msg.key);
        contentValues.put("value", msg.value);

        try {

            if(store.get("next_id").equals(store.get("current_id")) && store.get("prev_id").equals(store.get("current_id")))
            {
                db.insert(dbHelper.TABLE_NAME, null, contentValues);

            } else {

                store.put("hkey_from_contentValue",genHash(contentValues.getAsString("key")));
                store.put("hcurrent",genHash(store.get("current_id")));
                store.put("hnext",genHash(store.get("next_id")));
                store.put("hprev",genHash(store.get("prev_id")));








                if(store.get("hprev").compareTo(store.get("hcurrent")) > 0) {



                    if(store.get("hkey_from_contentValue").compareTo(store.get("hprev")) < 0 && store.get("hkey_from_contentValue").compareTo(store.get("hcurrent")) < 0){


                        long rowID = db.insert(dbHelper.TABLE_NAME, null, contentValues);


                    } else if(store.get("hkey_from_contentValue").compareTo(store.get("hprev")) > 0 && store.get("hkey_from_contentValue").compareTo(store.get("hcurrent")) > 0){


                        long rowID = db.insert(dbHelper.TABLE_NAME, null, contentValues);


                    } else{


                        try {
                            Node_Store node_alter = new Node_Store(store.get("current_id"), genHash(store.get("current_id")), store.get("prev_id"), store.get("next_id"), "i");
                            node_alter.key = contentValues.getAsString("key");
                            node_alter.value = contentValues.getAsString("value");

                            store.put("send",StringConstructor(node_alter));

                            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }  else if (store.get("hkey_from_contentValue").compareTo(store.get("hprev")) > 0 && store.get("hkey_from_contentValue").compareTo(store.get("hcurrent")) < 0) {



                    long rowID = db.insert(dbHelper.TABLE_NAME, null, contentValues);


                } else {



                    try {
                        Node_Store node_alter = new Node_Store(store.get("current_id"), genHash(store.get("current_id")), store.get("prev_id"), store.get("next_id"), "i");
                        node_alter.key = contentValues.getAsString("key");
                        node_alter.value = contentValues.getAsString("value");

                        store.put("send",StringConstructor(node_alter));

                        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    public void server_query(Node_Store msg){

        String[] columns = {"key", "value"};
        SQLiteDatabase queryDatabase = dbHelper.getReadableDatabase();

        String keyValue = dbHelper.COL_2 + "=?";
        Cursor cursor = queryDatabase.query(dbHelper.TABLE_NAME, columns, keyValue, new String[]{msg.key}, null, null, null);



        if(cursor != null && cursor.getCount() > 0) {


            if (cursor.moveToFirst()) {

                Node_Store node_alter = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "vf");
                node_alter.key = msg.key;
                node_alter.value = cursor.getString(cursor.getColumnIndex("value"));
                node_alter.from = msg.from;
                store.put("send",StringConstructor(node_alter));

                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

            }

        } else {
            Node_Store node_alter = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "uq");
            node_alter.key = msg.key;
            node_alter.from = msg.from;
            store.put("send",StringConstructor(node_alter));

            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

        }


    }






    public void client_f(String data) throws IOException {

        Node_Store node = deconstructor(data);



        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(node.from) * 2);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        try{
            dataOutputStream.writeUTF(data);

        } catch (Exception e){
            e.printStackTrace();;
        }
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        String get_confirmation = dataInputStream.readUTF();
        if(get_confirmation.equals("received")) {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
        }

    }

    public void server_query_found(Node_Store msg){

        store.put("result",msg.value);
    }






    public void server_gQuery(Node_Store node){



        SQLiteDatabase queryDatabase = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + dbHelper.TABLE_NAME;
        Cursor cursor = queryDatabase.rawQuery(selectQuery, null);


        store.put("all_keys","");

        store.put("all_vals","");

        if (cursor != null) {
            cursor.moveToFirst();
            int keyIndex = cursor.getColumnIndex("key");
            int valueIndex = cursor.getColumnIndex("value");

            while (!cursor.isAfterLast()) {

                store.put("all_keys",store.get("all_keys")+ cursor.getString(keyIndex) + "POINTBREAK");


                store.put("all_vals",store.get("all_vals")+ cursor.getString(valueIndex) + "POINTBREAK");
                cursor.moveToNext();

            }
        }

        Node_Store node_alter = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "gq_found");
        node_alter.from = node.from;
        if(!node.key.equals("") && !node.value.equals("")){
            node_alter.key = node.key.concat(store.get("all_keys"));
            node_alter.value = node.value.concat(store.get("all_vals"));

        } else {
            node_alter.key = store.get("all_keys");
            node_alter.value = store.get("all_vals");

        }

        if(!store.get("next_id").equals(node.from)) {

            node_alter.code = "gq";
            store.put("send",StringConstructor(node_alter));


            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

        } else {

            store.put("send",StringConstructor(node_alter));

            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));


        }
    }






    public void server_gQuery_found(Node_Store node){
        if(node.key != null) {

            store.put("global_Keys",node.key);

            store.put("global_Val",node.value);
        } else {

            store.put("global_Keys","test");
            store.put("global_Val","test");

        }

        checkif_globaldump = true;
    }




    public void server_delete(Node_Store node){



        String keyValue = dbHelper.COL_2 +" ='"+node.key+"'";
        int deleteResult = db.delete(dbHelper.TABLE_NAME, keyValue, null);

        Node_Store node_alter = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "d_found");
        node_alter.key = node.key;
        node_alter.from = node.from;
        node_alter.value = Integer.toString(deleteResult);

        if(deleteResult > 0 || store.get("next_id").equals(node_alter.from)) {

            store.put("send",StringConstructor(node_alter));

            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

        } else {

            node_alter.code = "d";
            store.put("send",StringConstructor(node_alter));
            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

        }



    }




    public void server_delete_found(Node_Store node){

        store.put("del_Val",node.value);
    }






    public void server_delete_all(Node_Store node){


        int deleteResult = db.delete(dbHelper.TABLE_NAME, null, null);

        if(store.get("next_id").equals(node.from)){

        }else {

            Node_Store node_alter = new Node_Store(store.get("current_id"), null, store.get("prev_id"), store.get("next_id"), "d_all");
            node_alter.from = node.from;
            node_alter.value = Integer.toString(deleteResult);
            node_alter.code = "d_all";
            store.put("send",StringConstructor(node_alter));
            new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, store.get("send"));

        }


    }









}
