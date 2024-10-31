package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class Mesa {
    public static final String COMPONENT = "mesa";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "getMisMesas":
                getMisMesas(obj, session);
                break;
            case "getMisMesasGroup":
                getMisMesasGroup(obj, session);
                break;
            case "getAllKeyEvento":
                getAllKeyEvento(obj, session);
                break;
            case "getMisMesasEvento":
                getMisMesasEvento(obj, session);
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
            case "entregar":
                entregar(obj, session);
                break;
        }
    }

    public static void entregar(JSONObject obj, SSSessionAbstract session) {
        try {

            JSONObject mesa = getByKey(obj.getString("key_mesa"));

            if(mesa.has("fecha_entrega") && !mesa.isNull("fecha_entrega")){
                obj.put("estado", "error");
                obj.put("error", "La mesa ya se encuentra engregada");
                return;
            }

            JSONObject data = new JSONObject();
            data.put("key", obj.getString("key_mesa"));
            data.put("fecha_entrega", SUtil.now());
            data.put("key_usuario_entrega", obj.getString("key_usuario"));
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");

            new Notification().send_urlType(
                null,
                mesa.getString("key_usuario"),
                mesa.getString("key_usuario"),
                "mesa_entregar", 
                new JSONObject()
                    .put("key_mesa", obj.getString("key_mesa"))
            );

        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
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

    public static void getMisMesas(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mesas('"+obj.getString("key_usuario")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getMisMesasEvento(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mesas_evento('"+obj.getString("key_usuario")+"', '"+obj.getString("key_evento")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    public static void getMisMesasGroup(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_mesas_group('"+obj.getString("key_usuario")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getAllKeyEvento(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all_mesas('" + obj.getString("key_evento") + "') as json";
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
            String consulta = "select get_mesa('"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static JSONObject getByKey(String key) {
        try {
            String consulta = "select get_by_key('" + COMPONENT + "', '"+key+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            JSONArray mesas = obj.getJSONArray("data");
            
            
            for (int i = 0; i < mesas.length(); i++) {
                mesas.getJSONObject(i).put("key", SUtil.uuid());
                mesas.getJSONObject(i).put("estado", 1);
                mesas.getJSONObject(i).put("fecha_on", SUtil.now());
                mesas.getJSONObject(i).put("key_usuario", obj.getString("key_usuario"));
                mesas.getJSONObject(i).put("key_sector", obj.getString("key_sector"));
            }

            SPGConect.insertArray(COMPONENT, mesas);
            obj.put("data", mesas);
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

}
