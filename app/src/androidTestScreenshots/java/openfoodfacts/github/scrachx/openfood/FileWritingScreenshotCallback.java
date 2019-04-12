package openfoodfacts.github.scrachx.openfood;//

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;
import openfoodfacts.github.scrachx.openfood.BuildConfig;
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper;
import openfoodfacts.github.scrachx.openfood.views.OFFApplication;
import tools.fastlane.screengrab.file.Chmod;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileWritingScreenshotCallback implements tools.fastlane.screengrab.ScreenshotCallback {
    private static final String LOG_TAG = FileWritingScreenshotCallback.class.getSimpleName();
    private DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd-HHmm");

    public FileWritingScreenshotCallback() {
    }

    public void screenshotCaptured(String screenshotName, Bitmap screenshot) {
        try {
            final Locale locale = LocaleHelper.getLocale();
            File screenshotDirectory = getFilesDirectory(locale);
            File screenshotFile = this.getScreenshotFile(screenshotDirectory, localeToDirName(locale) + "-" + screenshotName);
            try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(screenshotFile))) {
                screenshot.compress(CompressFormat.PNG, 100, fos);
                Chmod.chmodPlusR(screenshotFile);
            } finally {
                screenshot.recycle();
            }

            Log.d(LOG_TAG, "Captured screenshot \"" + screenshotFile.getName() + "\"");
        } catch (Exception var10) {
            throw new RuntimeException("Unable to capture screenshot.", var10);
        }
    }

    private File getScreenshotFile(File screenshotDirectory, String screenshotName) {
        String screenshotFileName = screenshotName + "_" + dateFormat.format(new Date()) + ".png";
        return new File(screenshotDirectory, screenshotFileName);
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File getFilesDirectory(Locale locale) throws IOException {

        if (!isExternalStorageWritable()) {
            Log.e(LOG_TAG, "Can't write to external storage check your installation");
            throw new IOException("Can't write to external storage");
        }

        File targetDirectory = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), BuildConfig.FLAVOR + "Screenshots");
        Log.w(LOG_TAG, "Screenshots created in " + targetDirectory.getAbsolutePath());
        File internalDir = new File(targetDirectory, localeToDirName(locale));
        File directory = initializeDirectory(internalDir);

        if (directory == null) {
            throw new IOException("Unable to get a screenshot storage directory");
        } else {
            Log.d(LOG_TAG, "Using screenshot storage directory: " + directory.getAbsolutePath());
            return directory;
        }
    }

    private static File initializeDirectory(File dir) {
        try {

            createPathTo(dir);
            if (dir.isDirectory() && dir.canWrite()) {
                return dir;
            }
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Failed to initialize directory: " + dir.getAbsolutePath(), exception);
        }

        return null;
    }

    private static String localeToDirName(Locale locale) {
        String localeCountry = locale.getCountry();
        if (localeCountry != null && localeCountry.length() != 0) {
            return locale.getLanguage() + "-" + localeCountry;
        }
        return locale.getLanguage();
    }

    private static void createPathTo(File dir) throws IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Unable to create output dir: " + dir.getAbsolutePath());
        } else {
            Chmod.chmodPlusRWX(dir);
        }
    }
}
