package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;
import java.security.SecureRandom;
import java.sql.SQLException;

public class Asistencia {
    public static final String COMPONENT = "asistencia";

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
            case "asistir":
                asistir(obj, session);
                break;
            case "getSinSalida":
                getSinSalida(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all('" + COMPONENT + "') as json";
            if(obj.has("key_usuario") && !obj.isNull("key_usuario")) {
                consulta = "select get_all('" + COMPONENT + "', 'key_usuario', '" + obj.get("key_usuario") + "') as json";
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

    public static void getSinSalida(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all_sin_salida_jefe('"+obj.getString("key_usuario")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

   
    public static void asistir(JSONObject obj, SSSessionAbstract session) {
        try {
            
            String consulta = "select get_asistencias('"+obj.getString("key_usuario")+"', '"+obj.getString("codigo")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);

            if(data.isEmpty()){
                obj.put("estado", "error");
                obj.put("error", "no existe horario");
                return;
            }


            
            JSONObject asistenciaStaffUsuario;

            JSONObject asistencia;
            for (int i = 0; i < JSONObject.getNames(data).length; i++) {

                asistencia = data.getJSONObject(JSONObject.getNames(data)[i]);

                asistenciaStaffUsuario = AsistenciaStaffUsuario.getByKey(asistencia.getString("key"), asistencia.getString("key_staff_usuario"));

                if(asistenciaStaffUsuario.isEmpty()){
                    asistenciaStaffUsuario = new JSONObject();
                    asistenciaStaffUsuario.put("key", SUtil.uuid());
                    asistenciaStaffUsuario.put("estado", 1);
                    asistenciaStaffUsuario.put("fecha_on", SUtil.now());
                    asistenciaStaffUsuario.put("key_usuario", obj.getString("key_usuario"));
                    asistenciaStaffUsuario.put("key_asistencia", asistencia.getString("key"));
                    asistenciaStaffUsuario.put("key_staff_usuario", asistencia.getString("key_staff_usuario"));
    
                    SPGConect.insertObject("asistencia_staff_usuario", asistenciaStaffUsuario);
                }else{
                    asistencia.put("key", asistencia.getString("key"));
                    if(obj.has("observacion")){
                        asistencia.put("observacion", obj.getString("observacion"));
                    }
                    asistencia.put("fecha_off", SUtil.now());
                    SPGConect.editObject("asistencia_staff_usuario", asistencia);    
                }
               
            }

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
            data.put("codigo", getCode());
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

    private static String getCode() throws SQLException {
        String consulta = "select generar_codigo_asistencia()::json as json";
        JSONObject codigo = SPGConect.ejecutarConsultaObject(consulta);
        return codigo.getString("codigo");
    }

}
