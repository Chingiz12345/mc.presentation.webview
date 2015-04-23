package mc.presentation.webview;

import android.app.Application;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class App extends Application {
    private static final String FOLDER_NAME = "html";

    @Override
    public void onCreate() {
        super.onCreate();
        copyFolder();
    }

    private void copyFolder() {
        AssetManager assetManager = getAssets();
        String[] files = null;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                files = assetManager.list(FOLDER_NAME);
            } catch (IOException e) {
                throw new RuntimeException("Failed to get assets file list", e);
            }

            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;

                File folder = new File(getHtmlFolder());
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (!success) {
                    throw new RuntimeException("Can't create html folder");
                }

                try {
                    in = assetManager.open(FOLDER_NAME + "/" + filename);
                    out = new FileOutputStream(getHtmlFolder() + "/" + filename);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy asset file: " + filename, e);
                } finally {
                    close(in);
                    close(out);
                }
            }
        } else {
            throw new RuntimeException("No sdcard mounted");
        }
    }

    public static String getHtmlFolder() {
        return Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                Log.e("ERROR", "Close error", e);
            }
        }
    }
}