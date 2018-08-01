package app.com.downlod;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import app.com.downlod.di.ComponentsHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String FILE_URL = "https://download.learn2crack.com/files/Node-Android-Chat.zip";
    private static final String TAG = MainActivity.class.getSimpleName();

    ProgressBar progressBar;
    Button btnStartDownloadFile;
    TextView tvCurrentDownloadState;
    Button btnDownloadManager;

    @Inject
    RxBus rxBus;
    @Inject
    DownloadManagerInteractor downloadManagerInteractor;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compositeDisposable = new CompositeDisposable();
        ComponentsHelper.getMainAppComponent().inject(this);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenLoadingState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
    }

    private void listenLoadingState() {
        Disposable disposable = rxBus.getSubject()
                .startWith(DownloadFileModel.EMPTY())
                .debounce(10, TimeUnit.MILLISECONDS)
                .filter(o -> o instanceof DownloadFileModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        object -> updateUi(object),
                        error -> Log.e(TAG, "On error", error),
                        () -> tvCurrentDownloadState.setText("File Download Complete"));
        compositeDisposable.add(disposable);
    }

    @SuppressLint("DefaultLocale")
    private void updateUi(Object object) {
        DownloadFileModel fileModel = (DownloadFileModel) object;
        progressBar.setProgress(fileModel.getProgress());
        tvCurrentDownloadState.setText(
                String.format("Downloaded (%d/%d) MB",
                        fileModel.getCurrentFileSize(),
                        fileModel.getTotalFileSize()));
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    makeText(this, "Permission Denied, Please allow to proceed !", LENGTH_LONG).show();
                }
                break;
        }
    }

    private void startDownload() {
        final Intent intent = new Intent(this, DownloadFileService.class);
        intent.putExtra(DownloadFileService.ARGS_FILE_URL, FILE_URL);
        startService(intent);
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress);
        tvCurrentDownloadState = findViewById(R.id.progress_text);
        btnStartDownloadFile = findViewById(R.id.btn_download);
        btnDownloadManager = findViewById(R.id.btn_download_manager);

        btnStartDownloadFile.setOnClickListener(v -> {
            if (checkPermission()) {
                startDownload();
            } else {
                requestPermission();
            }
        });

        btnDownloadManager.setOnClickListener(v -> {
            if (checkPermission()) {
                downloadManagerInteractor.downloadFile();
            } else {
                requestPermission();
            }
        });
    }
}
