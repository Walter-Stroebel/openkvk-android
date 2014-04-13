/**
 * This application is OpenSource, under the BSD license model.
 * If contested, the intellectual property rights (c) 2013,2014 belong to:
 * InfComTec
 * Kaaghof 1
 * 6843 NM Arnhem
 * Web: www.infcomtec.nl
 * Kvk (Chamber of commerce): 27304499
 */
package nl.openkvk;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
	private final class KvkButListener implements OnClickListener {
		final String kvk;
		final String kvks;

		public KvkButListener(String kvk, String kvks) {
			this.kvk = kvk;
			this.kvks = kvks;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, KvK_Details.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("kvk", kvk);
			intent.putExtra("kvks", kvks);
			startActivity(intent);
		}
	}

	private final class PostcodeButListener implements OnClickListener {
		final String postcode;

		public PostcodeButListener(String postcode) {
			this.postcode = postcode;
		}

		@Override
		public void onClick(View v) {
			TableLayout tl = (TableLayout) findViewById(R.id.tableOuter);
			tl.removeAllViews();
			String sql = "/json/select * from kvk where postcode='" + postcode
					+ "' LIMIT 1000;";
			TextView tv = new TextView(MainActivity.this);
			tv.setText("Even geduld AUB...");
			TableRow tr = new TableRow(MainActivity.this);
			tr.addView(tv);
			tl.addView(tr);
			new Caller(handler, handler.holder, sql, 2).start();
		}
	}

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
			case 1:
				Log.e("Result", "Exception", holder.exception);
				break;
			case 2:
				globalSearchResults(holder.obj);
				break;
			}
		}
	}

	final Result handler = new Result();

	public void clearFields(View view) {
		((EditText) findViewById(R.id.inpNaam)).setText("");
		((EditText) findViewById(R.id.inpStraat)).setText("");
		((EditText) findViewById(R.id.inpPostcode)).setText("");
		((EditText) findViewById(R.id.inpPlaats)).setText("");
		TableLayout tl = (TableLayout) findViewById(R.id.tableOuter);
		tl.removeAllViews();
	}

	private void globalSearchResults(Object obj) {
		{
			Log.d("Result", obj.toString());
			TableLayout tl = (TableLayout) findViewById(R.id.tableOuter);
			tl.removeAllViews();
			JSONResult res = new JSONResult(
					(JSONObject) ((JSONObject) ((JSONArray) obj).get(0))
							.get("RESULT"));
			if (res.size() < 1) {
				TextView v1 = new TextView(this);
				v1.setText("Niets gevonden voor deze zoekvraag.");
				TableRow tr = new TableRow(this);
				tr.addView(v1);
				tl.addView(tr);
			}
			for (int r = 0; r < res.size(); r++) {
				{
					TableRow tr = new TableRow(this);
					TextView v1 = new TextView(this);
					tr.addView(v1);
					tl.addView(tr);
				}
				print(tl, "Naam: ", res.getValue(r, "bedrijfsnaam"));
				print(tl, "KvK: ", res.getValue(r, "kvks"));
				print(tl, "VestNr: ", res.getValue(r, "vestiging"));
				print(tl, "Type: ", res.getValue(r, "type"));
				print(tl, "Rechtsvorm: ", res.getValue(r, "rechtsvorm"));
				print(tl, "Status: ", res.getValue(r, "status"));
				print(tl, "Website: ", res.getValue(r, "website"));
				print(tl, "ANBI: ", res.getValue(r, "anbi"));
				print(tl, "Adres: ", res.getValue(r, "adres"));
				String po = res.getValue(r, "postcode");
				String pl = res.getValue(r, "plaats");
				if (po != null || pl != null) {
					if (po == null) {
						print(tl, "", pl);
					} else if (pl == null) {
						print(tl, "", po);
					} else {
						print(tl, "", po + " " + pl);
					}
				}
				{
					TableRow tr = new TableRow(this);
					if (res.getValue(r, "kvk") != null
							&& res.getValue(r, "kvks") != null) {
						Button but = new Button(this);
						but.setText("Details");
						but.setTextSize(10);
						but.setOnClickListener(new KvkButListener(res.getValue(
								r, "kvk"), res.getValue(r, "kvks")));
						tr.addView(but);
					} else {
						tr.addView(new TextView(this));
					}
					tr.addView(new TextView(this));
					if (po != null) {
						Button but = new Button(this);
						but.setText("Postcode");
						but.setTextSize(10);
						but.setOnClickListener(new PostcodeButListener(po));
						tr.addView(but);
					}
					tl.addView(tr);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TableLayout tl = (TableLayout) findViewById(R.id.tableOuter);
		tl.removeAllViews();
	}

	private void print(TableLayout tl, String l, String v) {
		if (v != null) {
			TextView v1 = new TextView(this);
			v1.setText(l);
			TextView v2 = new TextView(this);
			v2.setText(v.toString());
			TableRow tr = new TableRow(this);
			tr.addView(v1);
			tr.addView(new TextView(this));
			tr.addView(v2);
			tl.addView(tr);
		}
	}

	public void searchInKvK(View view) {
		TableLayout tl = (TableLayout) findViewById(R.id.tableOuter);
		tl.removeAllViews();
		EditText _naam = (EditText) findViewById(R.id.inpNaam);
		EditText _straat = (EditText) findViewById(R.id.inpStraat);
		EditText _postcode = (EditText) findViewById(R.id.inpPostcode);
		EditText _plaats = (EditText) findViewById(R.id.inpPlaats);
		EditText _hdlNaam = (EditText) findViewById(R.id.inpHandelsNaam);
		String naam = _naam.getText().toString().trim();
		String hdlNaam = _hdlNaam.getText().toString().trim();
		String straat = _straat.getText().toString().trim();
		String postcode = _postcode.getText().toString().trim().toUpperCase();
		if (postcode.indexOf(' ') > 0) {
			postcode = postcode.replace(" ", "");
		}
		String plaats = _plaats.getText().toString().trim();
		_naam.setText(naam);
		_hdlNaam.setText(hdlNaam);
		_straat.setText(straat);
		_postcode.setText(postcode);
		_plaats.setText(plaats);
		String sql = null;
		if (naam.length() > 0) {
			try {
				Integer kvk = Integer.valueOf(naam);
				sql = "/json/select * from kvk where kvks=" + kvk;
			} catch (Exception ignore) {
				sql = "/json/select * from kvk where bedrijfsnaam ilike '"
						+ naam + "%'";
			}
			if (straat.length() > 0) {
				sql += " and adres ilike '" + straat + "%'";
			}
			if (plaats.length() > 0) {
				sql += " and plaats ilike '" + plaats + "%'";
			}
			if (postcode.length() > 0) {
				sql += " and postcode ilike '" + postcode + "%'";
			}
			sql += " LIMIT 1000;";
		} else if (hdlNaam.length() > 0) {
			sql = "/json/select * from kvk,kvk_handelsnamen where kvk.kvks=kvk_handelsnamen.kvks and handelsnaam ilike '"
					+ hdlNaam + "%'";
			sql += " LIMIT 1000;";
		} else if (straat.length() > 0
				&& (plaats.length() > 0 || postcode.length() > 0)) {
			sql = "/json/select * from kvk where adres ilike '" + straat + "%'";
			if (plaats.length() > 0) {
				sql += " and plaats ilike '" + plaats + "%'";
			}
			if (postcode.length() > 0) {
				sql += " and postcode ilike '" + postcode + "%'";
			}
			sql += " LIMIT 1000;";
		} else if (postcode.length() == 6) {
			sql = "/json/select * from kvk where postcode='" + postcode + "'";
			sql += " LIMIT 1000;";
		}
		if (sql != null) {
			TextView tv = new TextView(this);
			tv.setText("Even geduld AUB...");
			TableRow tr = new TableRow(this);
			tr.addView(tv);
			tl.addView(tr);
			new Caller(handler, handler.holder, sql, 2).start();
		} else {
			{
				TextView tv = new TextView(this);
				tv.setText("Ongeldige invoer.");
				TableRow tr = new TableRow(this);
				tr.addView(tv);
				tl.addView(tr);
			}
			{
				TextView tv = new TextView(this);
				tv.setText("Geef een (gedeelte van) een bedrijfsnaam in. Of geef het KvK nummer.");
				tv.setSingleLine(false);
				TableRow tr = new TableRow(this);
				tr.addView(tv);
				tl.addView(tr);
			}
			{
				TextView tv = new TextView(this);
				tv.setText("Geef eventueel ook (het begin van) een straat, postcode en/of plaatsnaam in.");
				tv.setSingleLine(false);
				TableRow tr = new TableRow(this);
				tr.addView(tv);
				tl.addView(tr);
			}
			{
				TextView tv = new TextView(this);
				tv.setText("Of geef een straat (mag met huisnummer) en een plaats en/of een postcode in.");
				tv.setSingleLine(false);
				TableRow tr = new TableRow(this);
				tr.addView(tv);
				tl.addView(tr);
			}
			{
				TextView tv = new TextView(this);
				tv.setText("Of geef alleen een volledige postcode in.");
				tv.setSingleLine(false);
				TableRow tr = new TableRow(this);
				tr.addView(tv);
				tl.addView(tr);
			}
		}
	}
}
