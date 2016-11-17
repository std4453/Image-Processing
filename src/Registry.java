import java.util.HashMap;
import java.util.Map;

public class Registry {
	private Map<String, Entry> mapping = new HashMap<>();

	public Registry() {
	}

	public void register(String name, Entry entry) {
		this.mapping.put(name, entry);
	}

	public Entry query(String name) {
		return this.mapping.get(name);
	}

	public static Registry instance = new Registry();
}