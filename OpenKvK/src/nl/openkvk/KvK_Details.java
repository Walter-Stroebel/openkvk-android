package nl.openkvk;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class KvK_Details extends Activity {
	private String kvk = "";
	private String kvks = "";

	final class Result extends Handler {
		private Holder holder = new Holder();

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				Log.e("Result", "Exception", holder.exception);
				break;
			}
			case 2: {
				printTable(R.id.kvk_details, holder.obj);
				new Caller(this, holder,
						"/json/select handelsnaam from kvk_handelsnamen where bestaand=true and kvks="
								+ kvks + " limit 1000;", 3).start();
				break;
			}
			case 3: {
				printTable(R.id.trademarks, holder.obj);
				new Caller(
						this,
						holder,
						"/json/select sbi_codes.* from kvk_sbi left join sbi_codes on kvk_sbi.code=sbi_codes.code where kvk="
								+ kvk + " limit 1000;", 4).start();
				break;
			}
			case 4: {
				printTable(R.id.sectoren, holder.obj);
				new Caller(this, holder,
						"/json/select * from faillissementen where kvks="
								+ kvks + " limit 1000;", 5).start();
				break;
			}
			case 5: {
				printTable(R.id.faillis, holder.obj);
				break;
			}
			}
		}
	}

	final Result handler = new Result();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kvk_details);
		kvk = getIntent().getExtras().getString("kvk");
		kvks = getIntent().getExtras().getString("kvks");
		new Caller(handler, handler.holder,
				"/json/select * from kvk where kvk=" + kvk + " limit 1000;", 2)
				.start();
	}

	private void printTable(int tab, Object obj) {
		TableLayout tl = (TableLayout) findViewById(tab);
		tl.removeAllViews();
		JSONResult res = new JSONResult(
				(JSONObject) ((JSONObject) ((JSONArray) obj).get(0))
						.get("RESULT"));
		if (res.size() < 1) {
			TextView tv = new TextView(this);
			TableRow tr = new TableRow(this);
			tr.addView(new TextView(this));
			if (tab == R.id.faillis) {
				tv.setText("Geen faillissementen bekend.");
			} else {
				tv.setText("Niets gevonden.");
			}
			tr.addView(tv);
			tr.addView(new TextView(this));
			tl.addView(tr);
		} else {
			for (int r = 0; r < res.size(); r++) {
				for (String fn : res.fields) {
					String v = res.getValue(r, fn);
					if (v != null) {
						TextView tv = new TextView(this);
						TableRow tr = new TableRow(this);
						tv.setText(fn);
						tr.addView(tv);
						tv = new TextView(this);
						tv.setText(": ");
						tr.addView(tv);
						tv = new TextView(this);
						tv.setText(v);
						tr.addView(tv);
						tl.addView(tr);
					}
				}
				// blank line
				TableRow tr = new TableRow(this);
				tr.addView(new TextView(this));
				tl.addView(tr);
			}
		}
	}

}
