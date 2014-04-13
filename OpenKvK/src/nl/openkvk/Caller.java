package nl.openkvk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.simple.parser.JSONParser;

import android.os.Handler;
import android.util.Log;

public class Caller extends Thread {
	private final String _sql;
	private final int ifOk;
	private final Handler handler;
	private final Holder holder;

	public Caller(Handler handler, Holder holder, String sql, int ifOk) {
		this._sql = sql;
		this.ifOk = ifOk;
		this.handler = handler;
		this.holder = holder;
	}

	public void run() {
		try {
			Socket s = new Socket("api.openkvk.nl", 80);
			s.getOutputStream().write("GET ".getBytes());
			String sql = _sql.replace("%", "%25");
			sql = sql.replace("'", "%27");
			sql = sql.replace(" ", "%20");
			sql = sql.replace("*", "%2A");
			sql = sql.replace("=", "%3D");
			s.getOutputStream().write(sql.getBytes());
			s.getOutputStream().write(" HTTP/1.0\r\n".getBytes());
			s.getOutputStream().write("Host: api.openkvk.nl\r\n\r\n".getBytes());
			BufferedReader bfr = new BufferedReader(new InputStreamReader(s.getInputStream()));
			for (String st = bfr.readLine(); st != null && st.length() > 0; st = bfr.readLine()) {
				Log.d("skipping", st);
			}
			StringBuilder theMeat = new StringBuilder();
			for (String st = bfr.readLine(); st != null; st = bfr.readLine()) {
				Log.d("using", st);
				theMeat.append(st);
			}
			bfr.close();
			s.close();
			JSONParser parser = new JSONParser();
			holder.obj = parser.parse(theMeat.toString());
			handler.sendEmptyMessage(ifOk);
		} catch (Exception ex) {
			holder.exception = ex;
			handler.sendEmptyMessage(1);
		}
	}
}