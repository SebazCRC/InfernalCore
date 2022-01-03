package tech.sebazcrc.infernalcore.Util.Extra;

import tech.sebazcrc.infernalcore.Main;
import tech.sebazcrc.infernalcore.NMS.VersionManager;
import tech.sebazcrc.infernalcore.NMS.Versions.ClassFinder_1_15_R1;

public class NMSFinder {

    private Main instance;

    public NMSFinder(Main instance) {

        this.instance = instance;
    }

    public Object getNMSHandler() {

        if (VersionManager.isRunning15()) {

            return new ClassFinder_1_15_R1().findNmsHandler();
        }

        return null;
    }

    public Object getNMSAccesor() {

        if (VersionManager.isRunning15()) {

            return new ClassFinder_1_15_R1().findNmsAccesor();
        }

        return null;
    }
}
