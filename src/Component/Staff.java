package Component;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class Staff {
    public static final String COMPONENT = "staff";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            case "getByKey": getByKey(obj, session); break;
            case "getByKeyDetalle": getByKeyDetalle(obj, session); break;
            case "getUsuariosDisponibles": getUsuariosDisponibles(obj, session); break;
            case "registro": registro(obj, session); break;
            case "editar": editar(obj, session); break;
            case "duplicar": duplicar(obj, session); break;
            case "getStaffChange": getStaffChange(obj, session); break;
            case "getPerfilBoss": getPerfilBoss(obj, session); break;
        }
    }

    public static void getPerfilBoss(JSONObject obj, SSSessionAbstract session) {
        try {

            String consulta = "select staff_profile_boss('" + obj.getString("key_staff") + "', '"+obj.getString("key_boss")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {

            String consulta = "select get_all('" + COMPONENT + "') as json";
            if(obj.has("key_evento")){
                consulta = "select get_all('" + COMPONENT + "', 'key_evento', '"+obj.getString("key_evento")+"') as json";
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

    public static void getByKeyDetalle(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_staff_by_key_detalle('"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    public static void getUsuariosDisponibles(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_usuarios_disponibles('"+obj.getString("key_staff")+"') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static JSONObject getByKeyEvento(String key_evento) {
        try {
            String consulta = "select get_all('" + COMPONENT + "', 'key_evento', '"+key_evento+"') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void duplicar(JSONObject obj, SSSessionAbstract session) {
        try {
            String key = obj.getString("key_staff");

            JSONObject staff = getByKey(key);
            staff.put("key", SUtil.uuid());
            
            String fec[] = staff.getString("fecha_inicio").split("T");
            staff.put("fecha_inicio", SUtil.now().split("T")[0] + "T" + fec[1]);
            
            fec = staff.getString("fecha_fin").split("T");
            staff.put("fecha_fin", SUtil.now().split("T")[0] + "T" + fec[1]);
        

            staff.put("fecha_on", SUtil.now());
            staff.put("estado", 1);
            SPGConect.insertArray(Staff.COMPONENT, new JSONArray().put(staff));

            obj.put("data", staff);
            obj.put("estado", "exito");
            
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    // Staff disponibles para cambiar en el booking
    public static void getStaffChange(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select board_staff_change('"+obj.getString("key_staff")+"') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
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
            
            JSONObject data = obj.getJSONObject("data");
            data.put("key", SUtil.uuid());
            data.put("estado", 1);
            data.put("fecha_on", SUtil.now());

            JSONObject evento = Evento.getByKey(data.getString("key_evento"));
            String fechaEvento = evento.getString("fecha").split("T")[0]; // Extrae solo la fecha en formato yyyy-MM-dd
            
            // Si existe y no es nulo fecha_inicio y fecha_fin en data
            if (data.has("fecha_inicio") && !data.isNull("fecha_inicio")) {
                // Extrae solo la parte de tiempo de fecha_inicio y fecha_fin
                String tiempoInicio = data.getString("fecha_inicio").split(" ")[1];
                
                // Concatena la fecha del evento con el tiempo de fecha_inicio y fecha_fin
                String fecIni = fechaEvento + "T" + tiempoInicio;
                
                // Parseo de fecha_inicio y fecha_fin concatenadas
                OffsetDateTime offsetDateTimeInicio = OffsetDateTime.parse(fecIni);
                Date dini = Date.from(offsetDateTimeInicio.toInstant());
            
                data.put("fecha_inicio", SUtil.formatTimestamp(dini));
            }



            if (data.has("fecha_fin") && !data.isNull("fecha_fin")) {
                // Extrae solo la parte de tiempo de fecha_inicio y fecha_fin
                String tiempoInicio = data.getString("fecha_inicio").split("T")[1];
                String tiempoFin = data.getString("fecha_fin").split(" ")[1];
            
                // Concatena la fecha del evento con el tiempo de fecha_inicio y fecha_fin
                String fecIni = fechaEvento + "T" + tiempoInicio;
                String fecFin = fechaEvento + "T" + tiempoFin;
            
                // Parseo de fecha_inicio y fecha_fin concatenadas
                OffsetDateTime offsetDateTimeInicio = OffsetDateTime.parse(fecIni);
                Date dini = Date.from(offsetDateTimeInicio.toInstant());
            
                OffsetDateTime offsetDateTimeFin = OffsetDateTime.parse(fecFin);
                Date dfin = Date.from(offsetDateTimeFin.toInstant());
            

                // Crear el objeto JSON y agregar la fecha
                
                if(dini.after(dfin) || dini.equals(dfin)){
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(dfin);
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    dfin = cal.getTime();
                }
                data.put("fecha_fin", SUtil.formatTimestamp(dfin));
            }else{
                OffsetDateTime offsetDateTimeInicio = OffsetDateTime.parse(data.getString("fecha_inicio"));
                Date dini = Date.from(offsetDateTimeInicio.toInstant());
                
                Calendar cal = new GregorianCalendar();
                cal.setTime(dini);
                cal.add(Calendar.DAY_OF_MONTH, 2); //El dijo 48 horas
                Date dfin = cal.getTime();
                
                data.put("fecha_fin", SUtil.formatTimestamp(dfin));
            }


            

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
            
           
            
            // Si existe y no es nulo fecha_inicio y fecha_fin en data
            if (data.has("fecha_inicio") && !data.isNull("fecha_inicio") && data.has("fecha_fin") && !data.isNull("fecha_fin")) {

                JSONObject evento = Evento.getByKey(data.getString("key_evento"));
                String fechaEvento = evento.getString("fecha").split("T")[0]; // Extrae solo la fecha en formato yyyy-MM-dd
                
                // Extrae solo la parte de tiempo de fecha_inicio y fecha_fin
                String tiempoInicio = data.getString("fecha_inicio").split(" ")[1];
                String tiempoFin = data.getString("fecha_fin").split(" ")[1];
            
                // Concatena la fecha del evento con el tiempo de fecha_inicio y fecha_fin
                String fecIni = fechaEvento + "T" + tiempoInicio;
                String fecFin = fechaEvento + "T" + tiempoFin;
            
                // Parseo de fecha_inicio y fecha_fin concatenadas
                OffsetDateTime offsetDateTimeInicio = OffsetDateTime.parse(fecIni);
                Date dini = Date.from(offsetDateTimeInicio.toInstant());
            
                OffsetDateTime offsetDateTimeFin = OffsetDateTime.parse(fecFin);
                Date dfin = Date.from(offsetDateTimeFin.toInstant());

                // Crear el objeto JSON y agregar la fecha
                
                if(dini.after(dfin)){
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(dfin);
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    dfin = cal.getTime();
                }
                data.put("fecha_inicio", SUtil.formatTimestamp(dini));
                data.put("fecha_fin", SUtil.formatTimestamp(dfin));
            }
            
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
