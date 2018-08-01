package app.com.downlod;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadFileModel implements Parcelable {

    private int progress;
    private int totalFileSize;
    private int currentFileSize;

    public DownloadFileModel() {
    }

    public static DownloadFileModel EMPTY() {
        return new DownloadFileModel();
    }

    public DownloadFileModel(Parcel in) {
        progress = in.readInt();
        totalFileSize = in.readInt();
        currentFileSize = in.readInt();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(int totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    public int getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(int currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public static final Creator<DownloadFileModel> CREATOR = new Creator<DownloadFileModel>() {
        @Override
        public DownloadFileModel createFromParcel(Parcel in) {
            return new DownloadFileModel(in);
        }

        @Override
        public DownloadFileModel[] newArray(int size) {
            return new DownloadFileModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(progress);
        dest.writeInt(totalFileSize);
        dest.writeInt(currentFileSize);
    }

    @Override
    public String toString() {
        return "DownloadFileModel{" +
                "progress=" + progress +
                ", totalFileSize=" + totalFileSize +
                ", currentFileSize=" + currentFileSize +
                '}';
    }
}
