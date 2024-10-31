package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class MesaCompra {
    public static final String COMPONENT = "mesa_compra";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "getByKey":
                getByKey(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
            case "getByKeyMesa":
                getByKeyMesa(obj, session);
                break;
            case "reasignarMesa":
                reasignarMesa(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all('" + COMPONENT + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_by_key('" + COMPONENT + "', '"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONObject data = obj.getJSONObject("data");
            data.put("key", SUtil.uuid());
            data.put("estado", 1);
            data.put("fecha_on", SUtil.now());
            data.put("key_usuario", obj.getString("key_usuario"));
            SPGConect.insertArray(COMPONENT, new JSONArray().put(data));
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
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
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void reasignarMesa(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_mesa_compra = obj.getString("key_mesa_compra");
            String key_mesa = obj.getString("key_mesa");
            JSONObject mesaCompra = getByKey(key_mesa_compra);
            mesaCompra.put("key_mesa", key_mesa);
            SPGConect.editObject(COMPONENT, mesaCompra);
            obj.put("data", mesaCompra);
            obj.put("estado", "exito");

            
            enviarNotificacionReasignarMesa(key_mesa, mesaCompra.getString("key_usuario"));

        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getByKeyMesa(JSONObject obj, SSSessionAbstract session) {
        try {
            obj.put("data", getByKeyMesa(obj.getString("key_mesa")));
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static JSONObject getByKey(String key) {
        try {
            String consulta = "select get_by_key('" + COMPONENT + "', '" + key + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject getByKeyMesa(String key_mesa) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_mesa' , '" + key_mesa + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            return null;
        }
    }

    public static void registro(JSONObject obj) {
        try {
            JSONObject data = obj;
            data.put("key", SUtil.uuid());
            data.put("estado", 1);
            data.put("fecha_on", SUtil.now());
            data.put("key_usuario", obj.getString("key_usuario"));
            SPGConect.insertArray(COMPONENT, new JSONArray().put(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarNotificacionReasignarMesa(String key_mesa, String key_usuario) {
        try {

            JSONObject mesa = Mesa.getByKey(key_mesa);
            JSONObject sector = Sector.getByKey(mesa.getString("key_sector"));
            String key_evento = sector.getString("key_evento");

            new Notification().send_urlType(
                null,
                key_usuario,
                key_usuario,
                "reasignar_mesa", 
                new JSONObject()
                    .put("key_evento", key_evento)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
