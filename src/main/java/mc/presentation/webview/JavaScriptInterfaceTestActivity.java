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
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class JavaScriptInterfaceTestActivity extends Activity {
    public static final String TITLE = JavaScriptInterfaceTestActivity.class.getSimpleName();

    private WebView mWebView;

    @Override
    @SuppressLint("AddJavascriptInterface")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        mWebView.loadUrl("file://" + App.getHtmlFolder() + "/" + TITLE + ".html");
        mWebView.addJavascriptInterface(new JavaScriptInterface(), "test");
    }

    @SuppressWarnings("Annotator")
    private class JavaScriptInterface {
        @android.webkit.JavascriptInterface
        public byte get_byte() {
            return 1;
        }

        @android.webkit.JavascriptInterface
        public short get_short() {
            return 1;
        }

        @android.webkit.JavascriptInterface
        public char get_char() {
            return '1';
        }

        @android.webkit.JavascriptInterface
        public boolean get_boolean() {
            return true;
        }

        @android.webkit.JavascriptInterface
        public int get_int() {
            return 1;
        }

        @android.webkit.JavascriptInterface
        public long get_long() {
            return 2L;
        }

        @android.webkit.JavascriptInterface
        public float get_float() {
            return 3.5f;
        }

        @android.webkit.JavascriptInterface
        public double get_double() {
            return 4.5d;
        }

        @android.webkit.JavascriptInterface
        public Boolean get_Boolean() {
            return new Boolean(true);
        }

        @android.webkit.JavascriptInterface
        public Integer get_Integer() {
            return new Integer(1);
        }

        @android.webkit.JavascriptInterface
        public Long get_Long() {
            return new Long(2);
        }


        @android.webkit.JavascriptInterface
        public Character get_Character() {
            return new Character('+');
        }

        @android.webkit.JavascriptInterface
        public Object get_IntegerAsObject() {
            return new Integer(1);
        }

        @android.webkit.JavascriptInterface
        public String get_String() {
            return "str";
        }

        @android.webkit.JavascriptInterface
        public Object get_StringAsObject() {
            return "obj";
        }

        @android.webkit.JavascriptInterface
        public char[] get_charArray() {
            return new char[]{'a', 'b', 'c'};
        }

        @android.webkit.JavascriptInterface
        public int[] get_intArray() {
            return new int[]{5, 6, 7};
        }

        @android.webkit.JavascriptInterface
        public Integer[] get_IntegerArray() {
            return new Integer[]{15, 16, 17};
        }

        @android.webkit.JavascriptInterface
        public Object[] get_ObjectArray() {
            return new Object[]{215, 216, 217};
        }

        @android.webkit.JavascriptInterface
        public String get_null() {
            return null;
        }

        @android.webkit.JavascriptInterface
        public void get_Exception() throws Exception {
            throw new Exception("Test exception");
        }

        //--------------------------

        @android.webkit.JavascriptInterface
        public void put_byte(byte i) {
            showToast("put_byte", i);
        }

        @android.webkit.JavascriptInterface
        public void put_short(short i) {
            showToast("put_short", i);
        }

        @android.webkit.JavascriptInterface
        public void put_char(char i) {
            showToast("put_char", i);
        }

        @android.webkit.JavascriptInterface
        public void put_int(int i) {
            showToast("put_int", i);
        }

        @android.webkit.JavascriptInterface
        public void put_long(long i) {
            showToast("put_long", i);
        }

        @android.webkit.JavascriptInterface
        public void put_float(float i) {
            showToast("put_float", i);
        }

        @android.webkit.JavascriptInterface
        public void put_boolean(boolean b) {
            showToast("put_boolean", b);
        }

        @android.webkit.JavascriptInterface
        public void put_double(double i) {
            showToast("put_double", i);
        }

        @android.webkit.JavascriptInterface
        public void put_Integer(Integer i) {
            showToast("put_Integer", i);
        }

        @android.webkit.JavascriptInterface
        public void put_Boolean(Boolean b) {
            showToast("put_Boolean", b);
        }

        @android.webkit.JavascriptInterface
        public void put_String(String s) {
            showToast("put_String", s);
        }

        @android.webkit.JavascriptInterface
        public void put_charArray(char[] s) {
            showToast("put_charArray", s);
        }

        @android.webkit.JavascriptInterface
        public void put_Object(Object o) {
            showToast("put_Object", o);
        }

        @android.webkit.JavascriptInterface
        public void put_int_int(int i1, int i2) {
            showToast("put_int_int", i1 + i2);
        }

        @android.webkit.JavascriptInterface
        public void put_intArray(int... i) {
            showToast("put_intArray", i);
        }

        @android.webkit.JavascriptInterface
        public void put_Overloaded(int i) {
            showToast("put_Overloaded(int) ", i);
        }

        @android.webkit.JavascriptInterface
        public void put_Overloaded(String i) {
            showToast("put_Overloaded(String) ", i);
        }

        @android.webkit.JavascriptInterface
        public void put_Overloaded(int i, String s) {
            showToast("put_Overloaded(int,String)", "" + i + "; " + s);
        }
    }

    private void showToast(final String method, final Object o) {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JavaScriptInterfaceTestActivity.this, method + "(" + (o == null ? "null" : o.toString()) + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
