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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class TrackPageLoadingStateActivity extends Activity {
    public static final String TITLE = TrackPageLoadingStateActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setTitle(TITLE);

        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("http://mail.ru");
    }

    class MyWebViewClient extends WebViewClient {
        private final long LOADING_ERROR_TIMEOUT = TimeUnit.SECONDS.toMillis(45);

        // WebView instance is kept in WeakReference because of mPageLoadingTimeoutHandlerTask
        private WeakReference<WebView> mReference;
        private boolean mLoadingFinished = false;
        private boolean mLoadingError = false;
        private long mLoadingStartTime = 0;

        // Helps to handle case when onReceivedError get called before onPageStarted
        // Problem cached on Nexus 7; Android 5
        private String mOnErrorUrl;

        // Helps to know what page is loading in the moment
        // Allows check url to prevent onReceivedError/onPageFinished calling for wrong url
        // Helps to prevent double call of onPageStarted
        // These problems cached on many devices
        private String mUrl;

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String url) {
            if (mUrl != null && !mLoadingError) {
                Log.e(TITLE, "onReceivedError: " + errorCode + ", " + description);
                mLoadingError = true;
            } else {
                mOnErrorUrl = removeLastSlash(url);
            }
        }

        // We need startsWith because some extra characters like ? or # are added to the url occasionally
        // However it could cause a problem if your server load similar links, so fix it if necessary
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            url = removeLastSlash(url);
            if (!startsWith(url, mUrl) && !mLoadingFinished) {
                Log.e(TITLE, "shouldOverrideUrlLoading: " + url);
                Toast.makeText(TrackPageLoadingStateActivity.this, "Redirect detected :" + url, Toast.LENGTH_SHORT).show();

                mUrl = null;
                onPageStarted(view, url, null);
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favIcon) {
            url = removeLastSlash(url);
            if (startsWith(url, mOnErrorUrl)) {
                mUrl = url;
                mLoadingError = true;
                mLoadingFinished = false;
                onPageFinished(view, url);
            }
            if (mUrl == null) {
                Log.e(TITLE, "onPageStarted: " + url);
                mUrl = url;
                mLoadingError = false;
                mLoadingFinished = false;
                mLoadingStartTime = System.currentTimeMillis();
                view.removeCallbacks(mPageLoadingTimeoutHandlerTask);
                view.postDelayed(mPageLoadingTimeoutHandlerTask, LOADING_ERROR_TIMEOUT);
                mReference = new WeakReference<>(view);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e(TITLE, "onPageFinished: " + url);
            url = removeLastSlash(url);
            if (startsWith(url, mUrl) && !mLoadingFinished) {
                Log.e(TITLE, "Page ");
                mLoadingFinished = true;
                view.removeCallbacks(mPageLoadingTimeoutHandlerTask);

                long loadingTime = System.currentTimeMillis() - mLoadingStartTime;
                if (!mLoadingError) {
                    Toast.makeText(TrackPageLoadingStateActivity.this, "Web page loading success: " + loadingTime, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TrackPageLoadingStateActivity.this, "Web page loading error: " + loadingTime, Toast.LENGTH_SHORT).show();
                }

                mOnErrorUrl = null;
                mUrl = null;
            } else if (mUrl == null) {
                // On some devices (e.g. Lg Nexus 5) onPageStarted sometimes not called at all
                // The only way I found to fix it is to reset WebViewClient
                view.setWebViewClient(new MyWebViewClient());
                mLoadingFinished = true;
            }
        }

        private String removeLastSlash(String url) {
            while (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        }

        // We need startsWith because some extra characters like ? or # are added to the url occasionally
        // However it could cause a problem if your server load similar links, so fix it if necessary
        private boolean startsWith(String str, String prefix) {
            return str != null && prefix != null && str.startsWith(prefix);
        }

        private final Runnable mPageLoadingTimeoutHandlerTask = new Runnable() {
            @Override
            public void run() {
                Log.e(TITLE, "Web page loading timeout: " + mUrl);
                mUrl = null;
                mLoadingFinished = true;
                long loadingTime = System.currentTimeMillis() - mLoadingStartTime;
                if (mReference != null) {
                    WebView webView = mReference.get();
                    if (webView != null) {
                        webView.stopLoading();
                    }
                }

                Toast.makeText(TrackPageLoadingStateActivity.this, "Web page loading error: " + loadingTime, Toast.LENGTH_SHORT).show();
            }
        };
    };
}
