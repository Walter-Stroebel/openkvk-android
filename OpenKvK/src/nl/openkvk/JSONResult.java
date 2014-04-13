package nl.openkvk;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONResult {
	private class ResultField {
		final String type;
		final String value;

		ResultField(Object t, Object v) {
			super();
			this.type = t == null ? "none" : t.toString();
			this.value = v == null ? null : v.toString();
		}
	}

	private class ResultFields extends TreeMap<String, ResultField> {

		/**
		 * Required.
		 */
		private static final long serialVersionUID = 1L;
	}

	private final ArrayList<ResultFields> data = new ArrayList<ResultFields>();
	public final TreeSet<String> fields = new TreeSet<String>();

	public JSONResult(JSONObject result) {
		JSONArray types = (JSONArray) result.get("TYPES");
		JSONArray header = (JSONArray) result.get("HEADER");
		JSONArray rows = (JSONArray) result.get("ROWS");
		for (Object row : rows) {
			JSONArray a = (JSONArray) row;
			ResultFields rfs = new ResultFields();
			data.add(rfs);
			for (int i = 0; i < header.size(); i++) {
				if (a.get(i) != null) {
					fields.add(header.get(i).toString());
					rfs.put(header.get(i).toString(), new ResultField(types.get(i), a.get(i)));
				}
			}
		}
	}

	public String getValue(int row, String field) {
		ResultField ret = data.get(row).get(field);
		if (ret != null)
			return ret.value;
		return null;
	}

	public String getType(int row, String field) {
		ResultField ret = data.get(row).get(field);
		if (ret != null)
			return ret.value;
		return null;
	}

	public int size() {
		return data.size();
	}
}
