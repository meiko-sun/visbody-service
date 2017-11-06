package cn.lazy.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mockito.cglib.beans.BeanMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * 
 * @author vic
 * @desc json util 
 */
public class JSONUtil {
	
	private static Gson gson = null; 
	
	static{
		gson  = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();//todo yyyy-MM-dd HH:mm:ss 
	}
	
	public static synchronized Gson newInstance(){
		if(gson == null){
//			gson =  new Gson();
			gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		}
		return gson;
	}
	
	public static String toJson(Object obj){
		return gson.toJson(obj);
	}
	
	public static <T> T toBean(String json,Class<T> clz){
		return gson.fromJson(json, clz);
	}
	
	public static <T> Map<String, T> toMap(String json,Class<T> clz){
		 Map<String, JsonObject> map = gson.fromJson(json, new TypeToken<Map<String,JsonObject>>(){}.getType());
		 Map<String, T> result = Maps.newHashMap();
		 for(String key:map.keySet()){
			 result.put(key,gson.fromJson(map.get(key),clz) );
		 }
		 return result;
	}
	
	public static Map<String, Object> toMap(String json){
		
//		JsonReader reader = new JsonReader(new StringReader(json));
//		reader.setLenient(true);
//		BaseEntity<Entity, String> entity = gson.fromJson(reader, new TypeToken<BaseEntity<Entity, String>>() {}.getType());
//		return entity;
		 Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String,Object>>(){}.getType());
		 return map;
	}
	
	public static <T> List<T> toList(String json,Class<T> clz){
		JsonArray array = new JsonParser().parse(json).getAsJsonArray();  
		List<T> list  = Lists.newArrayList();
		for(final JsonElement elem : array){  
	         list.add(gson.fromJson(elem, clz));
	    }
	    return list;
	}
	
	
	 /** 
	 * 将对象装换为map 
	 * @param bean 
	 * @return 
	 */  
	public static <T> Map<String, Object> beanToMap(T bean) {  
	    Map<String, Object> map = Maps.newHashMap();  
	    if (bean != null) {  
	        BeanMap beanMap = BeanMap.create(bean);  
	        for (Object key : beanMap.keySet()) {  
	            map.put(key+"", beanMap.get(key));  
	        }             
	    }  
	    return map;  
	}  
	  
	/** 
	 * 将map装换为javabean对象 
	 * @param <T>
	 * @param map 
	 * @param bean 
	 * @return 
	 */  
	public static  <T> T mapToBean(Map<String, Object> map,T bean) {  
	    BeanMap beanMap = BeanMap.create(bean);  
	    beanMap.putAll(map);  
	    return bean;  
	}  
	  
	/** 
	 * 将List<T>转换为List<Map<String, Object>> 
	 * @param objList 
	 * @return 
	 * @throws JsonGenerationException 
	 * @throws JsonMappingException 
	 * @throws IOException 
	 */  
	public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {  
	    List<Map<String, Object>> list = Lists.newArrayList();  
	    if (objList != null && objList.size() > 0) {  
	        Map<String, Object> map = null;  
	        T bean = null;  
	        for (int i = 0,size = objList.size(); i < size; i++) {  
	            bean = objList.get(i);  
	            map = beanToMap(bean);  
	            list.add(map);  
	        }  
	    }  
	    return list;  
	}  
	  
	/** 
	 * 将List<Map<String,Object>>转换为List<T> 
	 * @param maps 
	 * @param clazz 
	 * @return 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */  
	public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps,Class<T> clazz) throws InstantiationException, IllegalAccessException {  
	    List<T> list = Lists.newArrayList();  
	    if (maps != null && maps.size() > 0) {  
	        Map<String, Object> map = null;  
	        T bean = null;  
	        for (int i = 0,size = maps.size(); i < size; i++) {  
	            map = maps.get(i);  
	            bean = clazz.newInstance();  
	            mapToBean(map, bean);  
	            list.add(bean);  
	        }  
	    }  
	    return list;  
	}  
	
	/**
	 * 
	  * @方法名: mapToObject
	  * @描述: maptoBo .
	  * @程序猿: sundefa .
	  * @日期: 2017年11月1日 上午11:23:26
	  * @返回值: Object  
	  * @版本号: V2.0 .
	  * @throws
	 */
	 public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {    
	        if (map == null)   
	            return null;    
	  
	        Object obj = beanClass.newInstance();  
	  
	        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
	        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
	        for (PropertyDescriptor property : propertyDescriptors) {  
	            Method setter = property.getWriteMethod();    
	            if (setter != null) {  
	                setter.invoke(obj, map.get(property.getName()));   
	            }  
	        }  
	  
	        return obj;  
	    } 
	 
	 

		
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMapFastJson(String json){
		 return JSON.parseObject(json, new HashMap<String, JSONObject>().getClass());
	}
	
//	public static void main(String[] args) {
//		Map<String, String> map = new HashMap<String, String>();
//		 map.put("mob", "133");
//		 map.put("adiree", "333333");
//		 System.out.println(JSONUtil.toJson(map));
//			
//			@SuppressWarnings("resource")
//			Jedis jedis = new Jedis("120.77.209.69", 6379);
//			jedis.auth("Abc123");
////			jedis.connect();
//////			jedis.set("java", "http://www.baidu.com");
////			String value = jedis.get("java");
////			System.out.println(value);
//			//-----添加数据----------  
//			        Map<String, String> map = new HashMap<String, String>();
//			        map.put("133908", "chen");
//			        map.put("133890", "wu");
//			        map.put("189123", "li");
//			        jedis.hmset("peopleControl",map);
//			        System.out.println(jedis.hlen("peopleControl")); //返回key为peopleControl的键中存放的值的个数2 
//			        System.out.println(jedis.exists("peopleControl"));//是否存在key为peopleControl的记录 返回true  
//			        System.out.println(jedis.hkeys("peopleControl"));//返回map对象中的所有key  
//			        System.out.println(jedis.hvals("peopleControl"));//返回map对象中的所有value 
//
//		 
//	}
	
}
