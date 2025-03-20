package Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.SConfig;
import Servisofts.SUtil;

public class fetch {


    public static JSONArray get(String token, String service) {
        try {

            String url_ = SConfig.getJSON("kolping").getString("url");

            URL url = new URL(url_+service);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

            con.setRequestProperty("Authorization", "Bearer "+token);
            
            con.setUseCaches(false);
            con.setDoOutput(true);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            //System.out.println(content.toString());
            JSONObject resp = new JSONObject(content.toString());
            if(resp.has("Result") && !resp.isNull("Result")){
                return resp.getJSONArray("Result");
            }

            return new JSONArray();
            
            // System.out.println(content.toString());
            // return true;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject post(String url_, JSONObject data) throws Exception{
    

        URL url = new URL(url_);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json");
        
        con.setUseCaches(false);
        con.setDoOutput(true);

        String jsonInputString = data.toString();

        // Escribir el cuerpo de la petición
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        JSONObject resp;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            resp = new JSONObject(response.toString());
        }

       
        return  resp;
        
        
        // System.out.println(content.toString());
        // return true;
       
    }

}