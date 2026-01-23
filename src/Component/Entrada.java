package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class Entrada {
    public static final String COMPONENT = "entrada";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "getMisEntradas":
                getMisEntradas(obj, session);
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
            case "aceptarInvitacion":
                aceptarInvitacion(obj, session);
                break;
        }
    }

    public static void getMisEntradas(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_entradas('"+obj.getString("key_usuario")+"') as json";
            JSONObject entradas = SPGConect.ejecutarConsultaObject(consulta);

            obj.put("entradas", entradas);
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
            String consulta = "select get_entrada('"+obj.getString("key")+"') as json";
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
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);

            JSONObject tipo_entrada = TipoEntrada.getByKey(data.getString("key_tipo_entrada"));

            if(tipo_entrada.has("color") && !tipo_entrada.isNull("color")){
                data.put("color", tipo_entrada.getString("color"));
            }

            if(tipo_entrada.has("descripcion") && !tipo_entrada.isNull("descripcion")){
                data.put("producto", tipo_entrada.getString("descripcion"));
            }
            

            return data;
            
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
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

    public static void entregar(JSONObject obj, SSSessionAbstract session) {
        try {

            JSONObject entrada = getByKey(obj.getString("key_entrada"));

            if(entrada.has("fecha_entrega") && !entrada.isNull("fecha_entrega")){
                obj.put("estado", "error");
                obj.put("error", "La entrada ya se encuentra engregada");
                return;
            }

            JSONObject data = new JSONObject();
            data.put("key", obj.getString("key_entrada"));
            data.put("fecha_entrega", SUtil.now());
            data.put("key_usuario_entrega", obj.getString("key_usuario"));
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");

            enviarNotificacionEntradaEntregada(obj, entrada);

        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void aceptarInvitacion(JSONObject obj, SSSessionAbstract session) {
        try {

            JSONObject entrada = getByKey(obj.getString("key_entrada"));

            if(entrada.has("key_participante") && !entrada.isNull("key_participante")){
                obj.put("estado", "error");
                obj.put("error", "La entrada ya se encuentra asignada a un participante.");
                return;
            }

            

            JSONObject data = new JSONObject();
            data.put("key", obj.getString("key_entrada"));
            data.put("key_participante", obj.getString("key_participante"));
            data.put("fecha_participante", SUtil.now());
            data.put("key_usuario_invito", obj.getString("key_usuario_invito"));
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");

            enviarNotificacionEntradaCobrada(obj, entrada);
            

        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private static void enviarNotificacionEntradaCobrada(JSONObject obj, JSONObject entrada) {
        try {
            // Notificar al que compro la entrada
            String usuario_invitado = usuario.getNombreUsuario(obj.getString("key_participante"));

            new Notification().send_urlType(
                null,
                obj.getString("key_participante"),
                entrada.getString("key_usuario"),
                "entrada_aceptar_invitacion_participante", 
                new JSONObject()
                    .put("key_entrada", obj.getString("key_entrada"))
                    .put("ticket", entrada.get("numero"))
                    .put("invitado", usuario_invitado)
            );


            // Notificar al que acepto la invitacion
            String usuario_participante = usuario.getNombreUsuario(entrada.getString("key_usuario"));

            new Notification().send_urlType(
                null,
                entrada.getString("key_usuario"),
                obj.getString("key_participante"),
                "entrada_aceptar_invitacion_invitado", 
                new JSONObject()
                .put("key_entrada", obj.getString("key_entrada"))
                .put("ticket", entrada.get("numero"))
                .put("participante", usuario_participante)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarNotificacionEntradaEntregada(JSONObject obj, JSONObject entrada) {
        try {
            // Notificar al participante que recogieron la entrada
            String usuario_invitado = usuario.getNombreUsuario(entrada.getString("key_participante"));

            new Notification().send_urlType(
                null,
                entrada.getString("key_participante"),
                entrada.getString("key_usuario"),
                "entrada_entregar_invitacion_participante", 
                new JSONObject()
                    .put("key_entrada", obj.getString("key_entrada"))
                    .put("ticket", entrada.get("numero"))
                    .put("invitado", usuario_invitado)
            );


            // Notificar al invitado que recogio la entrada
            String usuario_participante = usuario.getNombreUsuario(entrada.getString("key_usuario"));

            new Notification().send_urlType(
                null,
                entrada.getString("key_usuario"),
                entrada.getString("key_participante"),
                "entrada_entregar_invitacion_invitado", 
                new JSONObject()
                .put("key_entrada", obj.getString("key_entrada"))
                .put("ticket", entrada.get("numero"))
                .put("participante", usuario_participante)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}