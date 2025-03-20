package Component;

import org.json.JSONArray;
import org.json.JSONObject;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SConfig;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class Evento {
    public static final String COMPONENT = "evento";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll": getAll(obj, session); break;
            case "getInicioFiltros": getInicioFiltros(obj, session); break;
            case "getInicio": getInicio(obj, session); break;
            case "getByKey": getByKey(obj, session); break;
            case "getPerfil": getPerfil(obj, session); break;
            case "registro": registro(obj, session); break;
            case "editar": editar(obj, session); break;
            case "duplicar": duplicar(obj, session); break;
            case "getEntregas": getEntregas(obj, session); break;
            case "getEstadoReclutas": getEstadoReclutas(obj, session); break;
            case "getEstadoAsistencias": getEstadoAsistencias(obj, session); break;
            case "getEstadoEventos": getEstadoEventos(obj, session); break;
            case "getTrabajosPerfil": getTrabajosPerfil(obj, session); break;
        }
    }

    public static void getTrabajosPerfil(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_staff_favoritos_evento('"+obj.getString("key_evento")+"', '" + obj.get("key_usuario") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        }catch(Exception e){
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        
    }
    
    public static void getEstadoEventos(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_estado_eventos('" + obj.getString("key_cliente") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        }catch(Exception e){
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        
    }

    public static void getEstadoReclutas(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_estado_reclutas('" + obj.getString("key_evento") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        }catch(Exception e){
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        
    }

    public static void getEstadoAsistencias(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_estado_asistencias('" + obj.getString("key_evento") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        }catch(Exception e){
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        
    }

    public static void getInicioFiltros(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_eventos_inicio_filtros('" + obj.getString("key_usuario") + "') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);

            JSONObject favorito = StaffTipoFavorito.getByKeyUsuario(obj.getString("key_usuario"));


            obj.put("data", data);
            obj.put("favoritos", favorito);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    public static void getInicio(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_eventos_inicio('" + obj.getString("key_usuario") + "', '" + obj.getString("fecha_inicio") + "', '"+obj.getString("fecha_fin")+"') as json";
            JSONArray data = SPGConect.ejecutarConsultaArray(consulta);
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
            // if(obj.has("key_usuario") && !obj.isNull("key_usuario")){
            //     consulta = "select get_eventos_usuario('" + obj.getString("key_usuario") + "') as json";
            // }

            if(obj.has("key_company") && !obj.isNull("key_company")) {
                consulta = "select get_all('" + COMPONENT + "', 'key_company', '" + obj.get("key_company") + "') as json";
            }
            if(obj.has("key_cliente") && !obj.isNull("key_cliente")) {
                consulta = "select get_all('" + COMPONENT + "', 'key_cliente', '" + obj.get("key_cliente") + "') as json";
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

    public static void getPerfil(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_evento_perfil('"+obj.getString("key")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            Visitante.registro(obj.getString("key"), obj.get("key_usuario")+"", obj.getJSONObject("device_info"));
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
            JSONObject data = obj.getJSONObject("data");
            data.put("key", SUtil.uuid());
            data.put("estado", 1);
            data.put("fecha_on", SUtil.now());
            data.put("key_usuario", obj.getString("key_usuario"));
            SPGConect.insertArray(COMPONENT, new JSONArray().put(data));
            obj.put("data", data);
            obj.put("estado", "exito");

            enviarNotificacionEventoVentaEnable(null, data, obj.getString("key_usuario"));
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    public static void getEntregas(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select entradas_por_evento('" + obj.getString("key_evento") + "', '"+obj.getString("key_venta")+"') as json";
            JSONObject entradas = SPGConect.ejecutarConsultaObject(consulta);


            obj.put("data", entradas);
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
            JSONObject dataOld = getByKey(data.getString("key"));
            SPGConect.editObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
            
            enviarNotificacionEventoVentaEnable(dataOld, data, obj.getString("key_usuario"));
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //duplucar evento
        SPGConect.setConexion(SConfig.getJSON("data_base"));
        JSONObject obj = new JSONObject();
        obj.put("key_evento", "d1b84a36-daa1-49bd-ad7d-70d45704ffb2");
        duplicar(obj, null);
    }

    public static void duplicar(JSONObject obj, SSSessionAbstract session) {
        try {
            String key = obj.getString("key_evento");

            JSONObject evento = getByKey(key);
            evento.put("key", SUtil.uuid());
            evento.put("descripcion", evento.getString("descripcion") + " (Copy)");
            evento.put("fecha_on", SUtil.now());
            evento.put("estado", 1);
            SPGConect.insertArray(COMPONENT, new JSONArray().put(evento));

            JSONObject staffs = Staff.getByKeyEvento(key);
            JSONArray staffsNew = new JSONArray();
            for (int i = 0; i < staffs.length(); i++) {
                JSONObject staff = staffs.getJSONObject(JSONObject.getNames(staffs)[i]);
                staff.put("key", SUtil.uuid());
                staff.put("key_evento", evento.getString("key"));
                staff.put("fecha_on", SUtil.now());
                staff.put("estado", 1);
                staffsNew.put(staff);
            }
            SPGConect.insertArray(Staff.COMPONENT, staffsNew);

            obj.put("data", evento);
            obj.put("estado", "exito");
            
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void enviarNotificacionEventoVentaEnable(JSONObject dataOld, JSONObject data, String key_usuario) {
        try {
            int estado_venta_old = 0;
            if(dataOld != null && data.has("estado_venta") && !dataOld.isNull("estado_venta")) {
                estado_venta_old = Integer.valueOf(dataOld.get("estado_venta") + "").intValue();
            }

            if(estado_venta_old == 0 && Integer.valueOf(data.get("estado_venta") + "").intValue() == 1 ) {
                new Notification().send_urlTypeTags(
                    null,
                    key_usuario,
                    null,
                    "evento_venta_enable", 
                    new JSONObject()
                    .put("key_evento", data.getString("key"))
                    .put("nombre", data.getString("descripcion"))
                    .put("descripcion", data.getString("observacion"))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
