package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class StaffTipoFavorito {
    public static final String COMPONENT = "staff_tipo_favorito";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "getByKey":
                getByKey(obj, session);
                break;
            case "getByUser":
                getByUser(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {

            String consulta = "select get_all('" + COMPONENT + "') as json";
            if (obj.has("key_usuario") && !obj.isNull("key_usuario")) {
                consulta = "select get_all('" + COMPONENT + "', 'key_usuario', '" + obj.get("key_usuario")
                        + "') as json";
            }
            if (obj.has("key_staff_tipo") && !obj.isNull("key_staff_tipo")) {
                consulta = "select get_all('" + COMPONENT + "', 'key_staff_tipo', '" + obj.get("key_staff_tipo")
                        + "') as json";
            }

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getByUser(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_staff_tipo_favorito('" + obj.getString("key_usuario") + "') as json";
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
            String consulta = "select get_by_key('" + COMPONENT + "', '" + obj.getString("key") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static JSONObject getByKeyUsuario(String key_usuario) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_usuario', '" + key_usuario + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getBy(String key_usuario, String key_staff_tipo, String key_company) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_usuario', '" + key_usuario
                    + "', 'key_staff_tipo', '" + key_staff_tipo + "', 'key_company', '" + key_company + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // public static JSONObject getBy(String key_usuario, String key_staff_tipo) {
    // try {
    // String consulta = "select get_all('" + COMPONENT + "', 'key_usuario',
    // '"+key_usuario+"', 'key_staff_tipo', '"+key_staff_tipo+"') as json";
    // return SPGConect.ejecutarConsultaObject(consulta);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return null;
    // }
    // }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {

            JSONObject data = obj.getJSONObject("data");

            JSONObject staffTipoFavorito = getBy(data.getString("key_usuario"), data.getString("key_staff_tipo"), data.getString("key_company"));

            if (staffTipoFavorito != null && !staffTipoFavorito.isEmpty()) {
                obj.put("estado", "error");
                obj.put("error", "Ya se encuentra registrado");
                return;
            }

            data.put("key", SUtil.uuid());
            data.put("estado", 1);
            data.put("fecha_on", SUtil.now());
            // data.put("key_usuario", obj.getString("key_usuario"));
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

}
