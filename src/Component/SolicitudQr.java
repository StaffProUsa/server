package Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.json.JSONArray;
import org.json.JSONObject;
import Servisofts.SPGConect;
import Servisofts.SUtil;
import Servisofts.SocketCliente.SocketCliente;
import Servisofts.Server.SSSAbstract.SSServerAbstract;
import Servisofts.Server.SSSAbstract.SSSessionAbstract;

public class SolicitudQr {
    public static final String COMPONENT = "solicitud_qr";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "getByQr":
                getByQr(obj, session);
                break;
            case "getQr":
                new SolicitudQr().getQr(obj, session);
                break;
        }
    }

    public static void aprobarSolicitudQr(String qrid, String tipo_pago){
        try{


            JSONObject solicitud = getByQr(qrid);
            
            solicitud.put("tipo", tipo_pago);
            SPGConect.editObject("solicitud_qr", solicitud);



            String consulta = "update "+COMPONENT+" set fecha_pago = '"+SUtil.now()+"' where qrid = '"+qrid+"'";
            SPGConect.ejecutar(consulta);

            JSONObject venta_ = Venta.getByQr(qrid);
            venta_.put("state", "pagado");
            Venta.editar(venta_);

          
            switch(tipo_pago){
                case "cortesia":
                    new Notification().send_urlType(
                        venta_.getString("key"),
                        solicitud.getString("key_usuario"),
                        solicitud.getString("key_usuario"),
                        "cortesia_pagada", 
                        new JSONObject()
                        .put("key_venta", venta_.getString("key"))
                        );
                    break;
                case "efectivo":
                new Notification().send_urlType(
                    venta_.getString("key"),
                    solicitud.getString("key_usuario"),
                    solicitud.getString("key_usuario"),
                    "efectivo_pagado", 
                    new JSONObject()
                    .put("key_venta", venta_.getString("key"))
                    );
                    break;
                case "qr":
                    new Notification().send_urlType(
                    venta_.getString("key"),
                    solicitud.getString("key_usuario"),
                    solicitud.getString("key_usuario"),
                    "qr_pagado", 
                    new JSONObject()
                    .put("key_venta", venta_.getString("key"))
                    .put("qrid", qrid)
                    );
                    break;
            }
            

            JSONObject detalles = Venta.getDetalle(venta_.getString("key"));

            for (int i = 0; i < JSONObject.getNames(detalles).length; i++) {
                JSONObject detalle = detalles.getJSONObject(JSONObject.getNames(detalles)[i]);
                
                if(detalle.getString("tipo").equals("entrada")){
                    
                    JSONArray entradas = new JSONArray();

                    for(int j = 0; j < detalle.getInt("cantidad"); j++){
                        JSONObject entrada = new JSONObject();
                        entrada.put("key", SUtil.uuid());
                        entrada.put("estado", 1);
                        entrada.put("fecha_on", SUtil.now());
                        entrada.put("key_usuario", venta_.getString("key_usuario"));
                        entrada.put("key_venta_detalle", detalle.getString("key"));
                        entrada.put("key_tipo_entrada", detalle.getString("key_item"));
                        entradas.put(entrada);
                    }
                    SPGConect.insertArray("entrada", entradas);
    
                }

                if(detalle.getString("tipo").equals("sector")){
                    consulta = "UPDATE mesa_compra SET estado = 1 WHERE key_venta_detalle = '" + detalle.getString("key") + "'";
                    SPGConect.ejecutar(consulta);
                }
            }
            
            

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }

    
        public static JSONObject getByQr(String qrId) {
            try {
                String consulta = "select get_by('" + COMPONENT + "', 'qrid','" + qrId + "') as json";
                return SPGConect.ejecutarConsultaObject(consulta);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
                
            }
        }

        public static void  getByQr(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_by('" + COMPONENT + "', 'qrid','" + obj.getString("qrid") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);

            JSONObject send = new JSONObject();
            send.put("component", "bg_payment_order");
            send.put("type", "getByQrId");
            send.put("qrid", obj.getString("qrid"));

            send = SocketCliente.sendSinc("banco_ganadero", send);

            if (send.getString("estado").equals("exito")) {
                data.put("qrImage", send.getJSONObject("data").getString("qrimage"));
            } 


            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            e.printStackTrace();
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_all('" + COMPONENT + "') as json";
            
            if(obj.has("key_empresa") && !obj.isNull("key_empresa")){
                consulta = "select get_all('" + COMPONENT + "', 'key_empresa', '"+obj.getString("key_empresa")+"') as json";
            }
            
            if(obj.has("key_empresa") && !obj.isNull("key_empresa") && obj.has("tipo") && !obj.isNull("tipo")){
                consulta = "select get_solicitud_qr_pagados('"+obj.getString("key_empresa")+"', '"+obj.getString("tipo")+"') as json";
            }
            if(obj.has("key_empresa") && !obj.isNull("key_empresa") && obj.has("key_usuario") && !obj.isNull("key_usuario")){
                consulta = "select get_solicitud_qr_pagados_usuario('"+obj.getString("key_empresa")+"', '"+obj.getString("key_usuario")+"') as json";
            }
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getMessage());
            e.printStackTrace();
        }
    }

    public void getQr(JSONObject obj, SSSessionAbstract session) {
        try {

            if (!obj.has("key_usuario") && obj.isNull("key_usuario")) {
                obj.put("estado", "error");
                obj.put("error", "key_usuario");
                return;
            }

            String nit = "S/N";
            if (obj.has("nit") && !obj.isNull("nit")) {
                nit = obj.getString("nit");
            }
            String razon_social = "S/N";
            if (obj.has("razon_social") && !obj.isNull("razon_social")) {
                razon_social = obj.getString("razon_social");
            }
            JSONArray correos = new JSONArray();
            if (obj.has("correos") && !obj.isNull("correos")) {
                correos = obj.getJSONArray("correos");
            }

            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.DATE, 1);

            SimpleDateFormat formatoJson = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");

            JSONObject solicitud_qr = new JSONObject();
            solicitud_qr.put("key", SUtil.uuid());
            solicitud_qr.put("fecha_on", SUtil.now());
            solicitud_qr.put("estado", 1);
            solicitud_qr.put("key_usuario", obj.getString("key_usuario"));
            solicitud_qr.put("nit", nit);
            solicitud_qr.put("razon_social", razon_social);
            if(obj.has("descripcion") && !obj.isNull("descripcion")){
                solicitud_qr.put("descripcion", obj.getString("descripcion"));
            }
            solicitud_qr.put("correos", correos);
            solicitud_qr.put("fecha_inicio", SUtil.now());
            solicitud_qr.put("fecha_vencimiento", formatoJson.format(cal.getTime()));
            solicitud_qr.put("monto", obj.getDouble("monto"));

            SPGConect.insertObject("solicitud_qr", solicitud_qr);

            JSONObject send = new JSONObject();
            send.put("component", "bg_payment_order");
            send.put("type", "registro");
            send.put("key_bg_profile", "0f6894c4-b78c-4cb0-b77e-9de332b46d36");

            send.put("monto", obj.getDouble("monto"));
            send.put("glosa", obj.getString("descripcion"));
            send.put("fecha_expiracion", formato.format(cal.getTime()));

            // Solicitamos un nuevo qr al banco ganadero
            send = SocketCliente.sendSinc("banco_ganadero", send, 2 * 60 * 1000);

            if (send.getString("estado").equals("exito")) {
                solicitud_qr.put("qrid", send.getJSONObject("data").getString("qrId"));
                solicitud_qr.put("qrImage", send.getJSONObject("data").getString("qrImage"));

                SPGConect.editObject("solicitud_qr", solicitud_qr);

                new Notification().send_urlType(
                    solicitud_qr.getString("key"),
                    solicitud_qr.getString("key_usuario"),
                    solicitud_qr.getString("key_usuario"),
                    "qr_solicitado", 
                    new JSONObject()
                    .put("key_venta", obj.getString("key_venta"))
                    .put("qrid", solicitud_qr.getString("qrid"))
                    );

                obj.put("data", solicitud_qr);
                obj.put("estado", "exito");
            } else {
                obj.put("error", send.getString("error"));
                obj.put("estado", "error");
            }

            // SSServerAbstract.sendAllServer(obj.toString());
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
            data.put("key_usuario", obj.getString("key_usuario"));
            data.put("fecha_on", SUtil.now());
            SPGConect.insertObject(COMPONENT, data);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getMessage());
            e.printStackTrace();
        }
    }

    public long versionToNumber(String v) {
        String[] array = v.split("\\.");
        final int vl = 100;
        long vn = 0;
        for (int i = 0; i < array.length; i++) {
            int element = Integer.parseInt(array[array.length - i - 1]);
            long vp = (long) Math.pow(vl, i);
            vn += (vp * element);
        }
        return vn;
    }
}