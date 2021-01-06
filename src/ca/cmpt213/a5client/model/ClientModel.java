package ca.cmpt213.a5client.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ClientModel class is responsible for interacting with the server
 */
public class ClientModel {
    private Map<Integer, Tokimon> tokimonMap = new HashMap<>();
    private final Gson gson = new Gson();

    public ClientModel() {
        updateTokimonList();
    }

    public List<Tokimon> getTokimonList() {
        return new ArrayList<>(tokimonMap.values());
    }

    public Tokimon getTokimon(int id) {
        return tokimonMap.get(id);
    }

    public int getNumTokimon() {
        return tokimonMap.size();
    }

    private void updateTokimonList() {
        String stringUrl = "http://localhost:8080/api/tokimon/all";
        try {
            HttpURLConnection connection = getGETHttpURLConnection(stringUrl);
            System.out.println(connection.getResponseCode());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String responseString = getResponseString(connection);
                System.out.println(responseString);
                List<Tokimon> tokimonList = gson.fromJson(responseString, getTokimonListType());
                if (tokimonList == null) {
                    tokimonList = new ArrayList<>();
                }
                tokimonMap.clear();
                for(Tokimon t : tokimonList) {
                    tokimonMap.put(t.getId(), t);
                }
            }

            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Error: Unable to GET all Tokimon");
            e.printStackTrace();
        }
    }

    public void addTokimon(Tokimon tokimon) {
        String stringUrl = "http://localhost:8080/api/tokimon/add";
        String requestBody = gson.toJson(tokimon);
        try{
            postRequest(stringUrl, requestBody);
        }
        catch (IOException e){
            System.out.println("Error: Unable to POST add tokimon");
        }
        updateTokimonList();

    }

    public void alterTokimon(Tokimon alteredTokimon) {
        String stringUrl = "http://localhost:8080/api/tokimon/change/" + alteredTokimon.getId();
        String requestBody = gson.toJson(alteredTokimon);
        try{
            postRequest(stringUrl, requestBody);
        }
        catch (IOException e){
            System.out.println("Error: Unable to POST alter tokimon");
        }
        updateTokimonList();
    }

    public void deleteTokimon(int id){
        Tokimon tokimon = tokimonMap.get(id);
        String stringUrl = "http://localhost:8080/api/tokimon/" + tokimon.getId();
        try{
            deleteRequest(stringUrl);
        }
        catch (IOException e){
            System.out.println("Error: Unable to DELETE tokimon");
        }
        updateTokimonList();

    }

    private void postRequest(String stringUrl, String requestBody) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

        // add to request body
        wr.write(requestBody);

        wr.flush();
        wr.close();
        connection.connect();
        System.out.println(connection.getResponseCode());
        connection.disconnect();
    }

    private void deleteRequest(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setDoOutput(true);
        connection.connect();

        System.out.println(connection.getResponseCode());
        connection.disconnect();
    }


    private String getResponseString(HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }

    private HttpURLConnection getGETHttpURLConnection(String stringUrl) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }

    // Returns Type of List<Tokimon>:
    // Help from: https://stackoverflow.com/a/49043900/8930125
    private Type getTokimonListType() {
        return TypeToken.getParameterized(List.class, Tokimon.class).getType();
    }
}
