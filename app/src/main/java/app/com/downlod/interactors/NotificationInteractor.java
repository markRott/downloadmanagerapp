package app.com.downlod.interactors;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import app.com.downlod.DownloadFileModel;

public class NotificationInteractor {

    private static final int NOTIFY_ID = 3245;
    private static final int MAX_PROGRESS_VALUE = 100;
    private static final int MIN_PROGRESS_VALUE = 0;

    private final NotificationManager notificationManager;
    private final NotificationCompat.Builder notificationBuilder;

    public NotificationInteractor(
            final NotificationManager notificationManager,
            final NotificationCompat.Builder notificationBuilder) {
        this.notificationManager = notificationManager;
        this.notificationBuilder = notificationBuilder;
    }

    public void setupNotification() {
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }

    public void sendNotification(final DownloadFileModel fileModel) {
        notificationBuilder.setProgress(MAX_PROGRESS_VALUE, fileModel.getProgress(), false);
        notificationBuilder.setContentText(getProgressInfo(fileModel));
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }

    @NonNull
    private String getProgressInfo(final DownloadFileModel fileModel) {
        return new StringBuilder()
                .append("Downloading file ")
                .append(fileModel.getCurrentFileSize())
                .append("/")
                .append(fileModel.getTotalFileSize())
                .append(" MB")
                .toString();
    }

    public void onDownloadComplete() {
        notificationBuilder.setOngoing(false);
        notificationManager.cancel(NOTIFY_ID);
        notificationBuilder.setProgress(MIN_PROGRESS_VALUE, MIN_PROGRESS_VALUE, false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }

    public void onTaskRemoved() {
        notificationManager.cancel(NOTIFY_ID);
    }
}
