package com.supermap.mytracks.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.supermap.mytracks.common.Params;

import android.os.Handler;

/**
 * <p>
 * 登录助手类，真正的实现登录
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class LoginActive {    
    public static void iClouldlogin(final String userName, final String passWord, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String str = "https://sso.isupermap.com/login?service=http://www.isupermap.com/shiro-cas";
                    URL url = new URL(str);
                    SSLContext sslctxt = SSLContext.getInstance("TLS");
                    sslctxt.init(null, new TrustManager[] { new MyX509TrustManager() }, new java.security.SecureRandom());
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setSSLSocketFactory(sslctxt.getSocketFactory());
                    conn.setReadTimeout(150000);
                    conn.setHostnameVerifier(new MyHostnameVerifier());
                    InputStream input = conn.getInputStream();
                    StringBuilder strBuf = toStringThenClose(input);

                    String login = substring(strBuf, "<form id=\"fm1\" action=\"", "\"");
                    String lt = substring(strBuf, "<input type=\"hidden\" name=\"lt\" value=\"", "\"");

                    String sessionId = loginForJSession(login, lt, userName, passWord);
                    conn.disconnect();
                    // 保存JsessionId
                    Params.cookie_Jessionid = sessionId;
                    handler.sendEmptyMessage(Params.SUCCESS);
                } catch (Exception e) {
                    // loginForJSession方法抛出的异常,抛出异常标示登陆失败。
                    handler.sendEmptyMessage(Params.ERROR);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String loginForJSession(String login, String lt, String username, String password) throws Exception {
        URL url = new URL("https://sso.isupermap.com" + login);
        String entity = "username=%s&password=%s&lt=%s&execution=e1s1&_eventId=submit";
        entity = String.format(entity, username, password, lt) + "&submit=%E7%99%BB%E5%BD%95";
        SSLContext sslctxt = SSLContext.getInstance("TLS");
        sslctxt.init(null, new TrustManager[] { new MyX509TrustManager() }, new java.security.SecureRandom());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sslctxt.getSocketFactory());
        conn.setReadTimeout(150000);
        conn.setHostnameVerifier(new MyHostnameVerifier());
        conn.setInstanceFollowRedirects(false);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.connect();
        OutputStream os = conn.getOutputStream();
        os.write(entity.getBytes("utf-8"));
        os.close();
        String location = conn.getHeaderField("Location");
        url = new URL(location);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setInstanceFollowRedirects(false);
        httpConn.connect();
        List<String> cookies = httpConn.getHeaderFields().get("Set-Cookie");
        for (String cookie : cookies) {
            if (cookie.startsWith("JSESSIONID=")) {
                return cookie.substring(0, cookie.indexOf(';'));
            }
        }
        throw new IllegalStateException();
    }

    private static StringBuilder toStringThenClose(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder buf = new StringBuilder();
        String data;
        while ((data = reader.readLine()) != null) {
            buf.append(data);
        }
        is.close();
        return buf;
    }

    private static String substring(StringBuilder buf, String prefix, String postfix) {
        int startIndex = buf.indexOf(prefix);
        startIndex += prefix.length();
        int lastIndex = buf.indexOf(postfix, startIndex);
        return buf.substring(startIndex, lastIndex);
    }

    static class MyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (null != chain) {
                for (int k = 0; k < chain.length; k++) {
                    X509Certificate cer = chain[k];
                    print(cer);
                }
            }
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (null != chain) {
                for (int k = 0; k < chain.length; k++) {
                    X509Certificate cer = chain[k];
                    print(cer);
                }
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        private void print(X509Certificate cer) {
        }
    }

    static class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
