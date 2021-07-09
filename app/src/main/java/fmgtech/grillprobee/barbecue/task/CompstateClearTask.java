package fmgtech.grillprobee.barbecue.task;

import fmgtech.grillprobee.barbecue.GrillApplication;
import fmgtech.grillprobee.barbecue.utils.BleConnectUtils;


public class CompstateClearTask implements Runnable {
    String mac;
    public CompstateClearTask(String mac){
        this.mac = mac;
    }

    @Override
    public void run() {
        BleConnectUtils.compensateMap.remove(mac);
        GrillApplication.handlerRun.remove(mac);
    }
}
