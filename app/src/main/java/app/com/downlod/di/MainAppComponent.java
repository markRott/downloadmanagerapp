package app.com.downlod.di;

import javax.inject.Singleton;

import app.com.downlod.DownloadFileService;
import app.com.downlod.MainActivity;
import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, NotificationModule.class})
public interface MainAppComponent {

    void inject(DownloadFileService downloadFileService);

    void inject(MainActivity mainActivity);
}
