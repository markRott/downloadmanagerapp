package app.com.downlod;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import javax.inject.Inject;

import app.com.downlod.di.ComponentsHelper;
import app.com.downlod.interactors.NotificationInteractor;
import app.com.downlod.utils.RxBus;
import app.com.downlod.utils.StringUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DownloadFileService extends IntentService {

    public static final String ARGS_FILE_URL = "ARGS_FILE_URL";
    private static final String TAG = DownloadFileService.class.getSimpleName();
    private static final int BUFFER_SIZE = 1024 * 4;
    private static final int BUFFERED_INPUT_STREAM_SIZE = 1024 * 8;
    private static final int MAX_PROGRESS_VALUE = 100;

    @Inject
    RxBus rxBus;
    @Inject
    DownloadFileApi downloadFileApi;
    @Inject
    NotificationInteractor notificationInteractor;

    private int totalFileSize;
    private OutputStream output;
    private InputStream bufferedInputStream;
    private String downloadFileNameWithExtension = StringUtils.EMPTY_STRING;

    public DownloadFileService() {
        super("Download file from network");
        ComponentsHelper.getMainAppComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        notificationInteractor.setupNotification();
        final String url = getFileUrl(intent);
        executeDownload(url);
    }

    private String getFileUrl(final Intent intent) {
        if (intent == null) return StringUtils.EMPTY_STRING;
        final String downloadUrl = intent.getStringExtra(ARGS_FILE_URL);
        downloadFileNameWithExtension = StringUtils.getFileNameWithExtension(downloadUrl);
        return downloadUrl;
    }

    private void executeDownload(final String url) {
        final Call<ResponseBody> request = downloadFileApi.downloadFile(url);
        try {
            download(Objects.requireNonNull(request.execute().body()));
        } catch (IOException e) {
            Log.e(TAG, "download file IOException ", e);
        }
    }

    private void download(final ResponseBody body) throws IOException {
        int count;
        long total = 0;
        final byte data[] = new byte[BUFFER_SIZE];
        final long fileSize = body.contentLength();
        totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));

        init(body);
        DownloadFileModel downloadFileModel = new DownloadFileModel();
        downloadFileModel.setTotalFileSize(totalFileSize);

        while ((count = bufferedInputStream.read(data)) != -1) {
            total += count;
            double current = Math.round(total / (Math.pow(1024, 2)));
            int progress = (int) ((total * MAX_PROGRESS_VALUE) / fileSize);
            notify(downloadFileModel, (int) current, progress);
            output.write(data, 0, count);
        }
        onDownloadComplete();
        close();
    }

    private void init(@NonNull final ResponseBody body) throws FileNotFoundException {
        final File outputFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), downloadFileNameWithExtension);
        bufferedInputStream = new BufferedInputStream(body.byteStream(), BUFFERED_INPUT_STREAM_SIZE);
        output = new FileOutputStream(outputFile);
    }

    private void notify(@NonNull final DownloadFileModel downloadFileModel, int currentSize, int progress) {
        downloadFileModel.setCurrentFileSize(currentSize);
        downloadFileModel.setProgress(progress);
        rxBus.sendData(downloadFileModel);
        notificationInteractor.sendNotification(downloadFileModel);
    }

    private void close() throws IOException {
        if (output != null) {
            output.flush();
            output.close();
        }
        if (bufferedInputStream != null) {
            bufferedInputStream.close();
        }
    }

    private void onDownloadComplete() {
        DownloadFileModel downloadFileModel = new DownloadFileModel();
        downloadFileModel.setProgress(MAX_PROGRESS_VALUE);
        rxBus.onComplete();
        notificationInteractor.onDownloadComplete();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationInteractor.onTaskRemoved();
    }
}
