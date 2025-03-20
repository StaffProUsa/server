package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class Board {
    public static final String COMPONENT = "board";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "principal":
                principal(obj, session);
                break;
            case "eventos":
                eventos(obj, session);
                break;
            case "timesheet":
                timesheet(obj, session);
                break;
            case "timesheet_company":
                timesheet_company(obj, session);
                break;
            case "timesheet_cliente":
                timesheet_cliente(obj, session);
                break;
        }
    }

    public static void principal(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select board_principal() as json";
            if (obj.has("key_usuario")) {
                consulta = "select board_principal('" + obj.getString("key_usuario") + "') as json";
            }
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void eventos(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select board_eventos() as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void timesheet(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_evento = obj.getString("key_evento");
            String consulta = "select board_timesheet('" + key_evento + "') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    public static void timesheet_company(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_company = obj.getString("key_company");
            String consulta = "select board_timesheet_company('" + key_company + "') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void timesheet_cliente(JSONObject obj, SSSessionAbstract session) {
        try {
            String key_cliente = obj.getString("key_cliente");
            String fecha_inicio = obj.getString("fecha_inicio");
            String fecha_fin = obj.getString("fecha_fin");
            String consulta = "select board_timesheet_cliente('" + key_cliente + "','" + fecha_inicio + "','"
                    + fecha_fin + "') as json";
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
