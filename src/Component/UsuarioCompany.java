package Component;

import java.util.Date;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.SPGConect;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import Server.SSSAbstract.SSSessionAbstract;

public class UsuarioCompany {
    public static final String COMPONENT = "usuario_company";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            case "getAllStaff": getAllStaff(obj, session); break;
            case "registro": registro(obj, session); break;
            case "editar": editar(obj, session); break;
            case "getCompanys": getCompanys(obj, session); break;
        }
    }

    public static void getCompanys(JSONObject obj, SSSessionAbstract session) {
        try{
            String consulta = "select get_companys('"+obj.getString("key_usuario")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        }catch(Exception e){
            obj.put("error", e.getLocalizedMessage());
            obj.put("estado", "error");
        }
    }

    public static void getAllStaff(JSONObject obj, SSSessionAbstract session) {
        try {
            
            String consulta = "select get_all_staff('" + obj.getString("key_company") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_company;
            try {
                key_company = obj.getString("key_company");
            } catch (Exception e) {
                key_company = null;
            }

            String consulta = "select get_all('" + COMPONENT + "') as json";
            
            
            if (key_company != null) {
                consulta = "select get_all('" + COMPONENT + "', 'key_company', '" + key_company + "') as json";
            } 
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            e.printStackTrace();
        }
    }

    public static JSONObject getAll(String key_restaurante) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_restaurante', '" + key_restaurante
                    + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void registro(JSONObject obj, SSSessionAbstract session) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            String fecha_on = formatter.format(new Date());
            JSONObject data = obj.getJSONObject("data");
            data.put("key", UUID.randomUUID().toString());
            data.put("estado", 1);
            data.put("fecha_on", fecha_on);

            JSONObject objetoExiste = SPGConect.ejecutarConsultaObject(
                    "select to_json(usuario_company.*) as json from usuario_company where usuario_company.key_usuario = '"
                            + data.getString("key_usuario")
                            + "' AND usuario_company.key_company = '" + data.getString("key_company")
                            + "'  AND  usuario_company.estado > 0");
            if (objetoExiste.has("key")) {
                obj.put("estado", "error");
                obj.put("error", "existe");
                return;
            }
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
