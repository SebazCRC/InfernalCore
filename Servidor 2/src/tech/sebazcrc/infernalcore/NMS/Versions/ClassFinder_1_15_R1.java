package tech.sebazcrc.infernalcore.NMS.Versions;

import tech.sebazcrc.infernalcore.NMS.ClassFinder;
import tech.sebazcrc.infernalcore.NMS.Versions.NMSAccesor.NMSAccesor_1_15_R1;

public class ClassFinder_1_15_R1 implements ClassFinder {
    @Override
    public Object findNmsHandler() {
        return new NMSHandler_1_15_R1();
    }

    @Override
    public Object findNmsAccesor() {
        return new NMSAccesor_1_15_R1();
    }
    
}
