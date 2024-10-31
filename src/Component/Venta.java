package Component;

import java.math.BigInteger;
import java.util.NoSuchElementException;

import javax.print.attribute.standard.JobKOctets;

import org.json.JSONArray;
import org.json.JSONObject;

import Server.SSSAbstract.SSServerAbstract;
import Server.SSSAbstract.SSSessionAbstract;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class Venta {
    public static final String COMPONENT = "venta";

    public static void onMessage(JSONObject obj, SSSessionAbstract session) {
        switch (obj.getString("type")) {
            case "getAll":
                getAll(obj, session);
                break;
            case "getByKey":
                getByKey(obj, session);
                break;
            case "getDetalle":
                getDetalle(obj, session);
                break;
            case "pagoEfectivo":
                pagoEfectivo(obj, session);
                break;
            case "pagoCortesia":
                pagoCortesia(obj, session);
                break;
            case "registro":
                registro(obj, session);
                break;
            case "pago_tarjeta":
                pago_tarjeta(obj, session);
                break;
            case "pago_qr":
                pago_qr(obj, session);
                break;
            case "editar":
                editar(obj, session);
                break;
        }
    }

    public static void getAll(JSONObject obj, SSSessionAbstract session) {
        try {

            String consulta = "select get_all('venta_con_detalle', 'key_usuario', '"+obj.getString("key_usuario")+"') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    public static void pagoCortesia(JSONObject obj, SSSessionAbstract session) {
        try {
            
            JSONObject venta = getByKey(obj.getString("key_venta"));
            SolicitudQr.aprobarSolicitudQr(venta.getString("qrid"), "cortesia");

            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    public static void pagoEfectivo(JSONObject obj, SSSessionAbstract session) {
        try {
            
            JSONObject venta = getByKey(obj.getString("key_venta"));
            SolicitudQr.aprobarSolicitudQr(venta.getString("qrid"), "efectivo");

            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    
    public static JSONObject getByQr(String qrid) {
        try {
            String consulta = "select get_by_key('venta', 'qrid', '" + qrid + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static JSONObject getByKey(String key) {
        try {
            String consulta = "select get_by_key('venta_con_detalle', '" + key + "') as json";
            return SPGConect.ejecutarConsultaObject(consulta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public static JSONObject getByKey(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_by_key('venta_con_detalle', '" + obj.getString("key") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
            return data;
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject getDetalle(JSONObject obj, SSSessionAbstract session) {
        try {
            String consulta = "select get_venta_detalle('" + obj.getString("key") + "') as json";
            JSONObject data = SPGConect.ejecutarConsultaObject(consulta);
            obj.put("data", data);
            obj.put("estado", "exito");
            return data;
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getDetalle(String keyVenta) {
        try {
            String consulta = "select get_All('venta_detalle','key_venta', '" + keyVenta + "') as json";
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
            data.put("state", "pendiente");
            data.put("fecha_on", SUtil.now());
            data.put("key_usuario", obj.getString("key_usuario"));
            SPGConect.insertObject(COMPONENT, data);

            JSONArray detalle = data.getJSONArray("detalle");

            String key_venta_detalle;
            for (int i = 0; i < detalle.length(); i++) {

                key_venta_detalle = SUtil.uuid();

                detalle.getJSONObject(i).put("key", key_venta_detalle);
                detalle.getJSONObject(i).put("key_venta", data.getString("key"));
                detalle.getJSONObject(i).put("estado", 1);
                detalle.getJSONObject(i).put("fecha_on", SUtil.now());
                detalle.getJSONObject(i).put("key_usuario", obj.getString("key_usuario"));

                SPGConect.insertObject("venta_detalle", detalle.getJSONObject(i));

                if(detalle.getJSONObject(i).getString("tipo").equals("sector")) {
                    // Mesas

                    JSONArray mesas = detalle.getJSONObject(i).getJSONArray("mesas");
                    JSONObject mesa;
                    for (int j = 0; j < mesas.length(); j++) {
                        mesa = mesas.getJSONObject(j);

                        JSONObject mesaCompras = MesaCompra.getByKeyMesa(mesa.getString("key"));
                        // Consultar en mesa_compra
                        if(mesaCompras != null && !mesaCompras.isEmpty()){
                            throw new Exception("La mesa ya se encuentra asignada");
                        }
                    }

                    for (int j = 0; j < mesas.length(); j++) {
                        mesa = mesas.getJSONObject(j);

                        JSONObject mesaCompra = new JSONObject();
                        mesaCompra.put("key_mesa", mesa.getString("key"));
                        mesaCompra.put("key_venta_detalle", key_venta_detalle);
                        mesaCompra.put("key_venta_detalle", key_venta_detalle);
                        mesaCompra.put("key_usuario", obj.getString("key_usuario"));
                        MesaCompra.registro(mesaCompra);
                        
                    }
                }
            }

            obj.put("key", data.getString("key"));
            getByKey(obj, session);
            obj.put("estado", "exito");
        } catch (Exception e) {
            obj.put("estado", "error");
            obj.put("error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static void pago_tarjeta(JSONObject obj, SSSessionAbstract session) {
        JSONObject credit_card = obj.getJSONObject("data");
        // BigInteger num =
        credit_card.remove("key");
        credit_card.remove("fecha_on");
        credit_card.remove("key_usuario");
        credit_card.remove("estado");
        credit_card.put("credit_card_number", new BigInteger(credit_card.getString("credit_card_number")));
        credit_card.put("cvv_code", Integer.parseInt(credit_card.getString("cvv_code")));
        credit_card.put("tokenization", false);
        credit_card.put("encryption", false);
        //JSONObject resp = Qhantuy.createPaymentType(QPaymentType.CYBERSOURCE, obj.getString("key_venta"), credit_card);
        obj.put("data", new JSONObject().put("pendiente", "Pendiente de desarrollo"));
        obj.put("estado", "exito");

    }

    public static void pago_qr(JSONObject obj, SSSessionAbstract session) {

        new SolicitudQr().getQr(obj, session);

        if(obj.has("estado") && obj.getString("estado").equals("exito")){
            
            try{
                JSONObject venta = new JSONObject();
                venta.put("key", obj.getString("key_venta"));
                venta.put("qrid", obj.getJSONObject("data").getString("qrid"));
                SPGConect.editObject("venta", venta);
            }catch(Exception e){
                e.printStackTrace();
                obj.put("estado", "error");
                obj.put("error", e.getLocalizedMessage());
            }

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
    public static void editar(JSONObject data) {
        try {
            SPGConect.editObject(COMPONENT, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
