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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class InputFileTestActivity extends Activity {
    public static final String TITLE = InputFileTestActivity.class.getSimpleName();

    private static final int SELECT_PICTURE = 1;

    private ValueCallback<Uri> mCallback;
    private ValueCallback<Uri[]> mArrayCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(mWebChromeClient);

        webView.loadUrl("file://" + App.getHtmlFolder() + "/" + TITLE + ".html");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String pixPath = getPath(selectedImageUri);
                if (mCallback != null) {
                    mCallback.onReceiveValue(Uri.parse("file://" + pixPath));
                } else if (mArrayCallback != null) {
                    mArrayCallback.onReceiveValue(new Uri[]{Uri.parse("file://" + pixPath)});
                }
            }
        }
    }

    // Caution: Your gallery response format could be different
    public String getPath(Uri uri) {
        if (uri == null) {
            return "";
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }

    private final WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, WebChromeClient.FileChooserParams fileChooserParams) {
            runFileChooser(null, callback);
            return true;
        }

        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> callback, String accept, String capture) {
            this.openFileChooser(callback);
        }

        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> callback, String accept) {
            this.openFileChooser(callback);
        }

        public void openFileChooser(ValueCallback<Uri> callback) {
            runFileChooser(callback, null);
        }

        public void runFileChooser(ValueCallback<Uri> callback, ValueCallback<Uri[]> arrayCallback) {
            mCallback = callback;
            mArrayCallback = arrayCallback;
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    };
}
