import java.util.*;

public static class Registry {
  Map<String, Entry> mapping = new HashMap<String, Entry>();
  public Registry() {}
  
  public void register(String name, Entry entry) {
    this.mapping.put(name, entry);
  }
  
  public Entry query(String name) {
    return this.mapping.get(name);
  }
  
  public static Registry instance = new Registry();
}