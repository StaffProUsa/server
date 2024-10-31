package Component;

import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;

public class Reportes {
    public static final String COMPONENT = "reportes";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "ventas_por_eventos": ventas_por_eventos(obj, session); break;
            case "ventas_por_evento_detalle": ventas_por_evento_detalle(obj, session); break;
            case "ventas_por_evento_grafico": ventas_por_evento_grafico(obj, session); break;

            case "entradas_por_evento": entradas_por_evento(obj, session); break;
            case "visitas_por_evento": visitas_por_evento(obj, session); break;
        }
    }

    public static void ventas_por_eventos(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select ventas_por_eventos('" + obj.getString("fecha_inicio") + "', '"+obj.getString("fecha_fin")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void ventas_por_evento_detalle(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select ventas_por_evento_detalle('" + obj.getString("key_evento") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void ventas_por_evento_grafico(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select ventas_por_evento_grafico('" + obj.getString("key_evento") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void entradas_por_evento(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select entradas_por_evento('" + obj.getString("key_evento") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    public static void visitas_por_evento(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select visitas_por_evento('" + obj.getString("key_evento") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
