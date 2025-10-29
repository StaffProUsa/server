package Component;

import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.SPGConect;
import Servisofts.SUtil;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;

public class UsuarioBloqueadoCliente {
    public static final String COMPONENT = "usuario_bloqueado_cliente";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            case "registro": registro(obj, session); break;
            case "editar": editar(obj, session); break;
            case "getByKey": getByKey(obj, session); break;
        }
    }

    public static void getByKey(JSONObject obj, SSSessionAbstract session) {
        try{
            String consulta = "select get_by_key('"+COMPONENT+"', '"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        }catch(Exception e){
            obj.put("error", e.getLocalizedMessage());
            obj.put("estado", "error");
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {

            String consulta = "select get_all('" + COMPONENT + "', 'key_cliente', '"+obj.getString("key_cliente")+"') as json";
            
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }


    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");

            if(!data.has("key_cliente") || data.getString("key_cliente").isEmpty() || data.isNull("key_cliente")){
                obj.put("estado", "error");
                obj.put("error", "NO existe key_cliente");
                return;
            }

            if(!data.has("key_usuario_bloqueado") || data.getString("key_usuario_bloqueado").isEmpty() || data.isNull("key_usuario_bloqueado")){
                obj.put("estado", "error");
                obj.put("error", "NO existe key_usuario_bloqueado");
                return;
            }
            data.put("key", UUID.randomUUID().toString());
            data.put("estado", 1);
            data.put("fecha_on", SUtil.now());

            SPGConect.insertArray(COMPONENT, new JSONArray().put(data));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void editar(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

}
