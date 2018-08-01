package app.com.downlod;

import android.app.DownloadManager;

public class DownloadManagerInteractor {

    private long requestId;
    private final DownloadManager downloadManager;
    private final DownloadManager.Request request;

    public DownloadManagerInteractor(
            final DownloadManager downloadManager,
            final DownloadManager.Request request) {
        this.downloadManager = downloadManager;
        this.request = request;
    }

    public void downloadFile() {
        requestId = downloadManager.enqueue(request);
    }
}
