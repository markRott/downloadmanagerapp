package app.com.downlod.di;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import javax.inject.Singleton;

import app.com.downlod.DownloadManagerInteractor;
import app.com.downlod.StringUtils;
import dagger.Module;
import dagger.Provides;

@Module
public class DownloadManagerModule {

    private static final String FILE_URL = "https://download.learn2crack.com/files/Node-Android-Chat.zip";

    @Provides
    @Singleton
    public DownloadManager provideDownloadManager(final Context context) {
        return (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Provides
    @Singleton
    public DownloadManager.Request provideRequest() {
        final Uri downloadUri = Uri.parse(FILE_URL);
        final String fileName = StringUtils.getFileNameWithExtension(FILE_URL);
        final String path = new StringBuilder()
                .append("/CustomDownloadDir/")
                .append("/")
                .append(fileName).toString();

        final DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle(StringUtils.getFileNameWithExtension(downloadUri.toString()));
        request.setDescription("Downloading " + fileName);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, path);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();

        return request;
    }

    @Provides
    @Singleton
    public DownloadManagerInteractor provideDownloadManagerInteractor(
            final DownloadManager downloadManager,
            final DownloadManager.Request request) {
        return new DownloadManagerInteractor(downloadManager, request);
    }
}
