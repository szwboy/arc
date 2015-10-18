package arc.core.io;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

	private PathMatcher matcher= new RegexPathMatcher();
	private ClassLoader classLoader;
	
	public PathMatchingResourcePatternResolver(){
		this.classLoader= Thread.currentThread().getContextClassLoader();
	}
	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public URL[] getResources(String path) {

		if(path.startsWith("/")){
			path= path.substring(1);
		}
		
		path+="/";
		
		RegexPath regexPath= new RegexPath(path);
		List<URL> result= new LinkedList<URL>();
		if(matcher.isPattern(path)){
			findMatchingNodes(new RegexNode(regexPath, regexPath.getRootDir()), result);
		}else{
			return findAllResources(path);
		}
		
		return result.toArray(new URL[0]);
	}
	
	private void findMatchingNodes(Node node, List<URL> nodes){
		if(node.isLeaf()){
			nodes.add(node.getURL());
			return;
		}
		
		Node[] ns= node.getChildNodes();
		for(Node n: ns){
			findMatchingNodes(n, nodes);
		}
	}
	
	private URL[] findAllResources(String path){
		
		Set<URL> result;
		Enumeration<URL> urls;
		try {
			urls = classLoader.getResources(path);
			result= new LinkedHashSet<URL>();
			while(urls.hasMoreElements()){
				URL url= urls.nextElement();
				result.add(url);
			}
		} catch (IOException e) {
			throw new IllegalStateException("No resources exsit");
		}
		return result.toArray(new URL[0]);
	}
	
	private boolean isJarFile(URL url){
		String protocol= url.getProtocol();
		
		if(protocol.equals("jar")|| protocol.equals("zip")) return true;
		
		return false;
	}
	
	private class RegexPath{
		
		String rootDir, name="";
		RegexPath next;
		boolean includeAllSub;
		
		RegexPath(String path){
			if(matcher.isPattern(path)){
//				path= path.replaceAll("\\*\\*", "\\\\w+");
//				path= path.replaceAll("\\*", "[a-zA-Z_0-9\\$]+");
				path= path.replace("\\.", "\\\\.");
				int start= start(path);
				rootDir= path.substring(0, start+1);
				group(path.substring(start+1));
			}else{
				rootDir= path;
			}
			
		}
		
		private int start(String path){
			String pattern= "/\\w*\\*+\\w*";
			Pattern p= Pattern.compile(pattern);
			Matcher matcher= p.matcher(path);
			
			if(matcher.find()){
				return matcher.start();
			}
			
			return 0;
		}
		
		private RegexPath(){}
		
		private void group(String path){
			StringTokenizer tokenizer= new StringTokenizer(path, "/");
			
			RegexPath rp= this;
			while(tokenizer.hasMoreTokens()){
				String name=tokenizer.nextToken();
				//test if the pattern can be resolved
				if(name.equals("**")){
					name="\\w+";
					includeAllSub= true;
				}else if(name.indexOf("*")!=-1){
					name= name.replaceAll("\\*", "[a-zA-Z0-9_]+");
				}

				rp.name= name;
				Pattern.compile(rp.name);
				
				if(!tokenizer.hasMoreTokens()) break;
				RegexPath regexPath= new RegexPath();
				rp.next= regexPath;
				rp= rp.next;
			}
		}
		
		String getRootDir(){
			return rootDir;
		}
		
		String getName(){
			return name;
		}
		
		RegexPath next(){
			
			if(next== null){
				next= new RegexPath();
				
			}
			return next;
		}
		
		boolean hasNext(){
			return next!= null;
		}
	}
	
	interface Node{
		Node[] getChildNodes();
		
		URL getURL();
		
		boolean isLeaf();
	}
	
	class RegexNode implements Node{
		RegexPath path;
		String urlAddress;
		boolean isLeaf;
		
		RegexNode(RegexPath path, String urlAddress){
			this(path, urlAddress, false);
		}
		
		RegexNode(RegexPath path, String urlAddress, boolean isLeaf){
			this.path= path;
			
			if(matcher.isPattern(urlAddress)) throw new IllegalArgumentException("root cannot be a matching address");
			this.urlAddress= urlAddress;
			this.isLeaf= isLeaf;
		}
		
		@Override
		public Node[] getChildNodes() {
			
			if(isLeaf()) return null;
			
			List<Node> result= new ArrayList<Node>();
			Enumeration<URL> urls;
			try {
				urls = classLoader.getResources(urlAddress);
				
				while(urls.hasMoreElements()){
					URL url= urls.nextElement();
					
					if(url.getProtocol().equals("jar")){

						URLConnection urlConnection= url.openConnection();
						JarEntry rootEntry= ((JarURLConnection) urlConnection).getJarEntry();
						
						String fullPattern= rootEntry.getName();
						RegexPath rp= path;
						while(rp!=null&& rp.hasNext()){
							if(!fullPattern.endsWith("/")) fullPattern+= "/";
							fullPattern+= rp.includeAllSub? "["+rp.getName()+"/]+": rp.getName();
							rp= rp.next();
						}
						fullPattern+= "/.+\\.class$";
						fullPattern= "^"+fullPattern;
						
						Pattern pattern= Pattern.compile(fullPattern);
						JarFile jarFile= ((JarURLConnection) urlConnection).getJarFile();
						Enumeration<JarEntry> entries= jarFile.entries();
						
						while(entries.hasMoreElements()){
							JarEntry entry= entries.nextElement();
							Matcher matcher= pattern.matcher(entry.getName());
							if(matcher.matches()){
									
								if(!entry.isDirectory())
									result.add(new RegexNode(path.next(), entry.getName(), true));
							}
						}
					}else{
						File f= new File(url.getFile());
						if(f.isFile()) throw new IllegalStateException(f.getName()+" should be a folder");
						
						doGetChildNodes(f.listFiles(), result);
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			return result.toArray(new Node[0]);
		}
		
		private void doGetChildNodes(File[] sfs, List<Node> result){
//			File[] sfs= f.listFiles();
			for(File sf: sfs){
				String name= sf.getName();
				if(matcher.isMatch(name, path.name)){
				
					if(sf.isDirectory()){
						RegexPath rp;
						if(!path.hasNext()){
							rp= new RegexPath();
							rp.name="\\w+.*";
						}else{
							rp= path.next();
						}	
						
						result.add(new RegexNode(rp, urlAddress+sf.getName()+"/", false));
					}	
					else if(!path.hasNext())
						result.add(new RegexNode(null, urlAddress+sf.getName(), true));
				}
				
				if(path.includeAllSub&& sf.isDirectory()){
					String originalAddress= urlAddress;
					try{
						urlAddress+= sf.getName()+"/";
						doGetChildNodes(sf.listFiles(), result);
					}finally{
						urlAddress= originalAddress;
					}
				}
			}
		}

		@Override
		public URL getURL() {
			return classLoader.getResource(urlAddress);
		}

		@Override
		public boolean isLeaf() {
			return isLeaf;
		}
	}

	@Override
	public URL getResource(String location) {
		return classLoader.getResource(location);
	}

}
