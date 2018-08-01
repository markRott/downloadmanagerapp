package app.com.downlod.di;

import android.content.Context;

import javax.inject.Singleton;

import app.com.downlod.DownloadFileApi;
import app.com.downlod.MyApp;
import app.com.downlod.RxBus;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ApplicationModule {

    private MyApp application;

    public ApplicationModule(MyApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public RxBus provideRxBus() {
        return new RxBus();
    }

    @Provides
    @Singleton
    public DownloadFileApi provideApi() {
        final Retrofit retrofit = new Retrofit.Builder().baseUrl("https://github.com").build();
        return retrofit.create(DownloadFileApi.class);
    }
}
