package mcjty.lib.setup;

import mcjty.lib.varia.WrenchChecker;

public class ModSetup extends DefaultModSetup {

    @Override
    protected void setupModCompat() {
        // @todo 1.14: check (right place?)
        WrenchChecker.init();
    }

    @Override
    protected void setupConfig() {

    }

    @Override
    public void createTabs() {
    }
}
