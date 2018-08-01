package app.com.downlod.di;

import app.com.downlod.MyApp;

public class ComponentsHelper {

    private static MainAppComponent mainAppComponent;

    private ComponentsHelper() {
    }

    public static MainAppComponent initMainAppComponent(final MyApp application) {
        if (mainAppComponent != null) return mainAppComponent;
        return mainAppComponent = DaggerMainAppComponent.builder()
                .applicationModule(new ApplicationModule(application))
                .notificationModule(new NotificationModule())
                .build();
//        return null;
    }

    public static MainAppComponent getMainAppComponent() {
        return mainAppComponent;
    }
}
