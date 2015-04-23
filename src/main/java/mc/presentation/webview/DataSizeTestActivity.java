/*
   Copyright 2015 Mikhail Chabanov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package mc.presentation.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DataSizeTestActivity extends Activity {
    public static final String TITLE = DataSizeTestActivity.class.getSimpleName();

    private static final String _1MB_IMAGE_NAME = "1_1mb.jpg";
    private static final String _1_6MB_IMAGE_NAME = "1_6mb.jpg";
    private static final String _2MB_IMAGE_NAME = "2_1mb.jpg";
    private static final String _3MB_IMAGE_NAME = "3mb.jpg";
    private static final String _4MB_IMAGE_NAME = "4mb.jpg";
    private static final String _5MB_IMAGE_NAME = "5_2mb.jpg";
    private static final String _6MB_IMAGE_NAME = "6_7mb.jpg";

    private WebView mWebView;

    @Override
    @SuppressLint("AddJavascriptInterface")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl("file://" + App.getHtmlFolder() + "/" + TITLE + ".html");
        mWebView.addJavascriptInterface(new JavaScriptInterface(), "test");
    }

    private class JavaScriptInterface {
        @android.webkit.JavascriptInterface
        public String getImage(int img) {
            File image = getImageFile(img);
            if (image == null) {
                return "";
            }

            byte[] bytes = getImageBytes(image);
            return "data:image/png;base64," + new String(bytes);
        }

        private File getImageFile(int img) {
            switch (img) {
                case 1100:
                    return new File(App.getHtmlFolder() + "/" + _1MB_IMAGE_NAME);
                case 1600:
                    return new File(App.getHtmlFolder() + "/" + _1_6MB_IMAGE_NAME);
                case 2100:
                    return new File(App.getHtmlFolder() + "/" + _2MB_IMAGE_NAME);
                case 3000:
                    return new File(App.getHtmlFolder() + "/" + _3MB_IMAGE_NAME);
                case 4000:
                    return new File(App.getHtmlFolder() + "/" + _4MB_IMAGE_NAME);
                case 5200:
                    return new File(App.getHtmlFolder() + "/" + _5MB_IMAGE_NAME);
                case 6700:
                    return new File(App.getHtmlFolder() + "/" + _6MB_IMAGE_NAME);
            }
            return null;
        }

        @android.webkit.JavascriptInterface
        public void checkImage(int img, byte[] imageData) {
            File image = getImageFile(img);
            if (image == null) {
                showToast("Images equals: false");
                return;
            }

            byte[] bytes = getImageBytes(image);
            String data = "data:image/png;base64," + new String(bytes);
            showToast("Images equals: " + (imageData.length == data.getBytes().length));
        }

        public byte[] getImageBytes(File image) {
            byte[] bytes = new byte[(int) image.length()];
            try {
                FileInputStream fis = new FileInputStream(image);
                int offset = 0;
                int numRead = 0;
                while (offset < bytes.length && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
                    offset += numRead;
                }
                fis.close();
                bytes = Base64.encode(bytes, 0);
            } catch (IOException e) {
                Log.e(TITLE, "Can't get image bytes", e);
            }
            return bytes;
        }
    }

    private void showToast(final String s) {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DataSizeTestActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
