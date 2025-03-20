package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
            case "asistirEvento":
                asistirEvento(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
            case "detalleStaff":
                detalleStaff(obj, session);
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

    public static void asistirEvento(JSONObject obj, SSSessionAbstract session) {
        try {

            String consulta = "select get_asistencias('" + obj.getString("key_usuario") + "', '"
                    + obj.getString("codigo") + "', '"+obj.getString("key_evento")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);

            if (data.isEmpty()) {
                obj.put("estado", "error");
                obj.put("error", "no existe horario");
                return;
            }

            JSONObject asistencia;
            for (int i = 0; i < JSONObject.getNames(data).length; i++) {

                asistencia = data.getJSONObject(JSONObject.getNames(data)[i]);

                if (!asistencia.has("fecha_ingreso") || asistencia.isNull("fecha_ingreso")) {
                    // se crea una asistencia de ingreso
                    //data.put("tipo", "ingreso");
                    SPGConect.editObject("staff_usuario",
                            new JSONObject().put("key", asistencia.getString("key_staff_usuario"))
                                    .put("fecha_ingreso", SUtil.now())
                                    .put("key_asistencia_ingreso", asistencia.getString("key")));
                } else if (!asistencia.has("fecha_salida") || asistencia.isNull("fecha_salida")) {
                    //data.put("tipo", "salida");
                    JSONObject staffUsuario = SPGConect.ejecutarConsultaObject("Select to_json(staff_usuario.*)::json as json from staff_usuario where staff_usuario.key = '" + asistencia.getString("key_staff_usuario") + "'");

                    if (staffUsuario.getString("key_asistencia_ingreso").equals(asistencia.getString("key"))) {
                        obj.put("estado", "error");
                        obj.put("error", "Codigo ya ocupado para el ingreso");
                        return;
                    }


                    SPGConect.editObject("staff_usuario",
                            new JSONObject().put("key", asistencia.getString("key_staff_usuario"))
                                    .put("fecha_salida", SUtil.now())
                                    .put("key_asistencia_salida", asistencia.getString("key")));
                } else {
                    //data.put("tipo", "niguno");
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

    public static void asistir(JSONObject obj, SSSessionAbstract session) {
        try {

            String consulta = "select get_asistencias('" + obj.getString("key_usuario") + "', '"+ obj.getString("codigo") + "') as json";

            if(obj.has("key_evento") && !obj.isNull("key_evento")){
                consulta = "select get_asistencias('" + obj.getString("key_usuario") + "', '"+ obj.getString("codigo") + "', '"+obj.getString("key_evento")+"') as json";    
            }

            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);

            if (data.isEmpty()) {
                obj.put("estado", "error");
                obj.put("error", "no existe horario");
                return;
            }

            JSONObject asistencia;
            for (int i = 0; i < JSONObject.getNames(data).length; i++) {

                asistencia = data.getJSONObject(JSONObject.getNames(data)[i]);

                if (!asistencia.has("fecha_ingreso") || asistencia.isNull("fecha_ingreso")) {
                    // se crea una asistencia de ingreso
                    //data.put("tipo", "ingreso");
                    SPGConect.editObject("staff_usuario",
                            new JSONObject().put("key", asistencia.getString("key_staff_usuario"))
                                    .put("fecha_ingreso", SUtil.now())
                                    .put("key_asistencia_ingreso", asistencia.getString("key")));
                } else if (!asistencia.has("fecha_salida") || asistencia.isNull("fecha_salida")) {
                    //data.put("tipo", "salida");
                    JSONObject staffUsuario = SPGConect.ejecutarConsultaObject("Select to_json(staff_usuario.*)::json as json from staff_usuario where staff_usuario.key = '" + asistencia.getString("key_staff_usuario") + "'");

                    if (staffUsuario.getString("key_asistencia_ingreso").equals(asistencia.getString("key"))) {
                        obj.put("estado", "error");
                        obj.put("error", "Codigo ya ocupado para el ingreso");
                        return;
                    }


                    SPGConect.editObject("staff_usuario",
                            new JSONObject().put("key", asistencia.getString("key_staff_usuario"))
                                    .put("fecha_salida", SUtil.now())
                                    .put("key_asistencia_salida", asistencia.getString("key")));
                } else {
                    //data.put("tipo", "niguno");
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
            
            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.SECOND, 60);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
            String now = formatter.format(cal.getTime());
            
            data.put("fecha", now);
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

    public static void detalleStaff(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_staff = obj.getString("key_staff");
            String consulta = "SELECT array_to_json(array_agg(sq1.*)) as json\n"+
                    "FROM (\n" +
                    "select staff_usuario.*, \n" + //
                    "to_json(evento.*) as evento, \n" + //
                    "to_json(staff.*) as staff \n" + //
                    "from staff_usuario, staff, evento \n" + //
                    "WHERE staff_usuario.key_staff = staff.key\n" +
                    "and staff.key = '" + key_staff + "'\n" +
                    "and staff.estado > 0\n" +
                    "AND staff_usuario.estado > 0\n"+
                    "AND staff_usuario.fecha_aprobacion_invitacion is not null\n"+
                    "AND evento.key = staff.key_evento\n"+
                    "AND evento.estado > 0\n"+
                    
                    ") sq1";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
