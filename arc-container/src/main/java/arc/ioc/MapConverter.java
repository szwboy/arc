package arc.ioc;

import org.codehaus.jackson.map.ObjectMapper;

import arc.ioc.container.annotation.Inject;
import arc.ioc.container.annotation.Value;

import java.util.Map;

public class MapConverter implements Converter {
	ObjectMapper ow= null;

	@Override
	public <Map>Map convert(String value, Class<Map> c) {
		Map m= null;
		try {
			m = ow.readValue(value, c);
		} catch (Exception e) {
			
			throw new IllegalArgumentException("parse error:", e);
		} 
		return m;
	}
	
	public MapConverter(){
		ow= new ObjectMapper();
	}
	
	@Inject
	public void set(@Value("{\"name\":\"sunzw\"}")Map<String, String> map){
		System.out.println(map.get("name"));
		this.ow= new ObjectMapper();
	}

}
