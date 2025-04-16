package Component;

import org.json.JSONArray;
import org.json.JSONObject;

import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SocketCliente.SocketCliente;

public class usuario {
    public static final String COMPONENT = "usuario";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "identificacion":
                identificacion(obj, session);
                break;
        }
    }

    public static void identificacion(JSONObject obj, SSSessionAbstract session) {
        JSONObject send = new JSONObject();
        send.put("component", "firebase_token");
        send.put("type", "registro");
        if(obj.has("firebase")){
            send.put("firebase", obj.getJSONObject("firebase"));
            send.put("estado", "cargando");
            SocketCliente.send("notification", send);
        }
        
        //Firebase.identificacion(obj, session);
    }

    public static JSONObject getUsuario(String key_usuario) throws Exception{
        JSONObject obj = new JSONObject();
        obj.put("component", "usuario");
        obj.put("version", "2.0");
        obj.put("type", "getAllKeys");
        obj.put("keys", new JSONArray().put(key_usuario));
        obj = SocketCliente.sendSinc("usuario", obj);
        if(obj.getString("estado").equals("exito")){
            obj = obj.getJSONObject("data");
            return  obj.getJSONObject(JSONObject.getNames(obj)[0]).getJSONObject("usuario");
        }
        return null;
    }

    public static String getNombreUsuario(String key_usuario) {
        
        try {
            JSONObject usuario = getUsuario(key_usuario);
            if(usuario == null) {
                return "";
            }

            String nombre = "";
            if(usuario.has("Nombres") && !usuario.isNull("Nombres")) {
                nombre += usuario.getString("Nombres");
            }
            if(usuario.has("Apellidos") && !usuario.isNull("Apellidos")) {
                if(nombre.length() > 0) {
                    nombre += " ";
                }
                nombre += usuario.getString("Apellidos");
            }

            return nombre;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } 
    }

}
