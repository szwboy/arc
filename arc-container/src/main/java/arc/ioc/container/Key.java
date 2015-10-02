package arc.ioc.container;

import org.apache.commons.lang.StringUtils;

public class Key<T> {

	private Class<T> type;
	private String name;
	
	private Key(Class<T> type, String name){
		if(type== null) throw new NullPointerException(type+" is null");
		
		if(StringUtils.isBlank(name)) throw new NullPointerException(name+" is null");
		this.type= type;
		this.name= name;
	}
	
	public String getName(){
		return name;
	}
	
	public Class<T> getType(){
		return type;
	}
	
	public static <T>Key<T> newInstance(Class<T> type, String name){
		return new Key<T>(type, name);
	}
	
	public int hashCode(){
		return type.getName().hashCode()^name.hashCode();
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Key)) return false;
		
		if(o==this) return true;
		
		Key k= (Key)o;
		return name.equals(k.getName())&&type.equals(k.getType());
	}
	
	public String toString(){
		return "[type is "+type+"name is "+name+"]";
	}
}
