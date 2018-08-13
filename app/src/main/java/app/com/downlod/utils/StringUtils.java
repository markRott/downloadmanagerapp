package app.com.downlod.utils;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public final class StringUtils {

    public static final String EMPTY_STRING = "";

    private static final int NOT_FOUND = -1;
    private static final char UNIX_SEPARATOR = '/';
    private static final char WINDOWS_SEPARATOR = '\\';
    private static final String TAG = StringUtils.class.getSimpleName();

    private StringUtils() {
    }

    public static String getFileNameWithExtension(final String strUrl) {
        try {
            final URL urlTest = new URL(strUrl);
            return getName(urlTest.getPath());
        } catch (MalformedURLException e) {
            Log.e(TAG, "getFileNameWithExtension", e);
            return EMPTY_STRING;
        }
    }

    private static String getName(final String filename) {
        if (filename == null) {
            return EMPTY_STRING;
        }
        failIfNullBytePresent(filename);
        final int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    private static void failIfNullBytePresent(final String path) {
        final int len = path.length();
        for (int i = 0; i < len; i++) {
            if (path.charAt(i) == 0) {
                throw new IllegalArgumentException("Null byte present in file/path name. There are no " +
                        "known legitimate use cases for such data, but several injection attacks may use it");
            }
        }
    }

    private static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
}
