package app.com.downlod.di;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import javax.inject.Singleton;

import app.com.downlod.NotificationInteractor;
import app.com.downlod.R;
import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule {

    private static final String DOWNLOAD_CHANEL_ID = "DOWNLOAD_CHANEL_ID";

    @Provides
    @Singleton
    public NotificationManager provideNotificationManager(final Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @Singleton
    public NotificationCompat.Builder privideNotificationCompatBuilder(final Context context) {
        return new NotificationCompat.Builder(context, DOWNLOAD_CHANEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.getString(R.string.title_download))
                .setContentText(context.getString(R.string.content_text))
                .setOngoing(true)
                .setAutoCancel(true);
    }

    @Provides
    @Singleton
    public NotificationInteractor provideNotificationInteractor(
            final NotificationManager notificationManager,
            final NotificationCompat.Builder builder) {

        return new NotificationInteractor(notificationManager, builder);
    }
}
