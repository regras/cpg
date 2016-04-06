package DetectarEventos;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

import ClassesGerais.Eventos;
import Main.mainClass;


//http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java

public class DetectarEventosDir implements Runnable
{
	private WatchService watcher;
	private Map<WatchKey,Path> keys;
	private boolean recursive;
	private boolean trace = false;
	private boolean cifrar = false;
	
	public void run()
	{
		try 
		{
			ProcessarEventos();
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DetectarEventosDir(String pathParam, boolean cifrarParam) throws IOException
	{
		cifrar = cifrarParam;
		
		Path dir = Paths.get(pathParam);
		
		DetectarEventosDirRegistrar(dir, true);
	}
		    
	@SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) 
    {
        return (WatchEvent<T>)event;
    }
	
    /**
     * Register the given directory with the WatchService
     */
	private void RegistrarDiretorio(Path dir) throws IOException 
	{
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) 
        {
        	Path prev = keys.get(key);
        	
            if (prev == null) 
            {
            	System.out.format("register: %s\n", dir);
            } 
            else 
            {
            	if (!dir.equals(prev)) 
            	{
            		System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        
        keys.put(key, dir);
    }
 
    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void RegistrarTodosDiretorios(final Path start) throws IOException 
    {
    	try
    	{
    		// register directory and sub-directories
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() 
    		{
            	@Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
                {
            		RegistrarDiretorio(dir);
                    return FileVisitResult.CONTINUE;
                }
        	});
    	}
    	catch(IOException e)
    	{
    		throw new IOException("Erro ao registrar diretório. Erro: " + e.getMessage());
    	}
    }
 
    /**
     * Creates a WatchService and registers the given directory
     * @return 
     */
    
    private void DetectarEventosDirRegistrar(Path dir, boolean recursive) throws IOException 
    {
    	this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.recursive = recursive;
 
        if (recursive) 
        {
        	System.out.format("Scanning %s ...\n", dir);
        	RegistrarTodosDiretorios(dir);
            System.out.println("Done.");
        } 
        else 
        {
        	RegistrarDiretorio(dir);
        }
        
        // enable trace after initial registration
        this.trace = true;
    }
 
    /**
     * Process all events for keys queued to the watcher
     * @throws Exception 
     */
    public Runnable ProcessarEventos() throws Exception 
    {
    	List<Eventos> listEvents = new ArrayList<Eventos>();
    	
    	try
    	{
    		for (;;) 
	    	{
	    		// wait for key to be signalled
	            WatchKey key;
	            
	            try 
	            {
	            	key = watcher.take();
	            } 
	            catch (InterruptedException x) 
	            {
	                return null;
	            }
	 
	            Path dir = keys.get(key);
	            
	            if (dir == null) 
	            {
	                System.err.println("WatchKey not recognized!!");
	                continue;
	            }
	 
	            for (WatchEvent<?> event: key.pollEvents()) 
	            {
	            	WatchEvent.Kind kind = event.kind();
	 
	                // TBD - provide example of how OVERFLOW event is handled
	                if (kind == OVERFLOW) 
	                	continue;
	 
	                // Context for directory entry event is the file name of entry
	                WatchEvent<Path> ev = cast(event);
	                Path name = ev.context();
	                Path child = dir.resolve(name);
	 
	                // if directory is created, and watching recursively, then
	                // register it and its sub-directories
	                if (recursive && (kind == ENTRY_CREATE)) 
	                {
	                	try 
	                	{
	                		if (Files.isDirectory(child, NOFOLLOW_LINKS)) 
	                			RegistrarTodosDiretorios(child);
	                        
	                    } 
	                	catch (IOException x) 
	                    {
	                        // ignore to keep sample readbale
	                    }
	                }
	                
	                String arqName = "";
	                
	                int ind0 = child.toString().lastIndexOf('/') + 1;
	               
	                if(ind0 > 0)
					{
						arqName = child.toString().substring(ind0, child.toString().length());
					}
	                
	                if(!arqName.isEmpty() && !arqName.startsWith(".goutputstream-"))
	                {
	                	if(listEvents == null)
	                    	listEvents = new ArrayList<Eventos>();
	                    
	                    if(listEvents.size() > 0)
	                    {
	                    	boolean add = true;
	                    	
	                    	for(int i=0; i<listEvents.size(); i++)
	                    	{
	                    		if(listEvents.get(i).PathEvent.equals(child.toString()))
	                			{
	                    			if(listEvents.get(i).KindEvent.equals("ENTRY_CREATE") && (event.kind().name().equals("ENTRY_MODIFY") || event.kind().name().equals("ENTRY_CREATE")))
	                    			{
	                    				add = false;
	                    				break;
	                    			}
	                    			
	                    			if(listEvents.get(i).KindEvent.equals("ENTRY_MODIFY") && (event.kind().name().equals("ENTRY_CREATE") || event.kind().name().equals("ENTRY_MODIFY")))
	                    			{
	                    				add = false;
	                        			break;
	                    			}
	                    			
	                    			if(listEvents.get(i).KindEvent.equals("ENTRY_DELETE") && event.kind().name().equals("ENTRY_DELETE"))
	                    			{
	                    				add = false;
	                        			break;
	                    			}
	                			}
	                    	}
	                    	
	                    	if(add)
	                    		listEvents.add(new Eventos(child.toString(), event.kind().name()));
	                    }
	                    else
	                    	listEvents.add(new Eventos(child.toString(), event.kind().name()));
	                }           
	            }
	            	
	            // reset key and remove from set if directory no longer accessible
	            boolean valid = key.reset();
	            
	            if (!valid) 
	            {
	            	keys.remove(key);
	 
	                // all directories are inaccessible
	                if (keys.isEmpty()) 
	                	break;                
	            }
	            
	            if(listEvents != null && listEvents.size() > 0)
	        	{            
	            	mainClass.TratarEventos(listEvents, cifrar);
	            	
	            	listEvents = new ArrayList<Eventos>();            	 
	        	}           
	        }
    	}
    	catch (Exception ex) 
        {
            throw new Exception("Processar eventos --> Erro na detecção de eventos! Erro: " + ex.getMessage());
        }
    	
    	return null;
    }
    
    static void usage() 
    {
        System.err.println("usage: java WatchDir [-r] dir");
        System.exit(-1);
    }
    
    public static String main(String[] args) throws IOException 
    {    	
        // parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
        
        return "";
	}
}
