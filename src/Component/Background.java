package Component;

import Servisofts.SConsole;
import Servisofts.SPGConect;
import Servisofts.SUtil;

public class Background extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Quitando los que no aceptaron la invitacion...");
                
                String consulta = "select quitar_no_aprobados() as json";
                
                SPGConect.execute(consulta);

                String fecha = SUtil.now();
                System.out.println(fecha);
                
                Thread.sleep(30*60*1000);
            } catch (Exception e) {
                SConsole.error(e.getLocalizedMessage());
            }
        }
    }
}
