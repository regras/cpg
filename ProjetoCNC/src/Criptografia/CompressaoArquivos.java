package Criptografia;

import java.io.File;
import java.util.List;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.core.ZipFile;

public class CompressaoArquivos 
{
	public static boolean ComprimirArquivos(List<File> inputFiles, String outputFile, String password) throws Exception
	{
		try
		{
			ZipFile zipFile = new ZipFile(outputFile);
			
			ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			
			// DEFLATE_LEVEL_ULTRA = maximum compression
			// DEFLATE_LEVEL_MAXIMUM
			// DEFLATE_LEVEL_NORMAL = normal compression
			// DEFLATE_LEVEL_FAST
			// DEFLATE_LEVEL_FASTEST = fastest compression
            
            if(password.length()>0)
            {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

                parameters.setPassword(password);
            }
            
			for(int i = 0; i< inputFiles.size(); i++) 
			{
				File arquivo = inputFiles.get(i);
				
				if(arquivo.isFile())
					zipFile.addFile(arquivo, parameters);
				
				else if(arquivo.isDirectory())
                    zipFile.addFolder(arquivo, parameters);
				
				else
					throw new Exception("Erro na compressão! Não foi possível identificar o tipo de arquivo.");                
			}			
			
		}
		catch(ZipException e)
		{
			throw new Exception("Erro na compressão: " + e.getMessage());
		}
		
		return true;
	}
		
	public static boolean DescomprimirArquivos(File inputZip, String output, String password) throws Exception
	{
		try
		{
			ZipFile zipFile = new ZipFile(inputZip);
					
			if (zipFile.isEncrypted()) 
				zipFile.setPassword(password);
            
			zipFile.extractAll(output);	
			
		}
		catch(ZipException e)
		{
			throw new Exception("Erro na descompressão: " + e.getMessage());
		}
		
		return true;
	}
	
	public static boolean EmpacotarArquivos(List<File> inputFiles, String outputFile, String password) throws Exception
	{
		try
		{
			ZipFile zipFile = new ZipFile(outputFile);
			
			ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);//No compression
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            
            if(password.length()>0)
            {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
                parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);

                parameters.setPassword(password);
            }
            
			for(int i = 0; i< inputFiles.size(); i++) 
			{
				File arquivo = inputFiles.get(i);
				
				if(arquivo.isFile())
					zipFile.addFile(arquivo, parameters);
				
				else if(arquivo.isDirectory())
                    zipFile.addFolder(arquivo, parameters);
				
				else
					throw new Exception("Erro na empacotação dos arquivos! Não foi possível identificar o tipo de arquivo.");                
			}			
			
		}
		catch(ZipException e)
		{
			throw new Exception("Erro na empacotação dos arquivos: " + e.getMessage());
		}
		
		return true;
	}

}
