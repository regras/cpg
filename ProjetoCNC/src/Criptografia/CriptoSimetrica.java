package Criptografia;

import java.awt.MouseInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CriptoSimetrica 
{
	private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	//private static final String TRANSFORMATION = "AES/CBC/ISO10126PADDING";
    private static final String PROVIDER = "SunJCE";
    
	public static void CriptografarArquivo(boolean cifrar, SecretKey key, File inputFile, File outputFile) throws Exception 
    {
    	try 
    	{
    		String IV = "AAAAAAAAAAAAAAAA";
    		
    		//Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
    		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);
    		
    		if(cifrar)
    			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
    		else
    			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes()));
    		
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            
            byte[] outputBytes = cipher.doFinal(inputBytes);
            
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
            
        } 
    	catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException | InvalidAlgorithmParameterException | NoSuchProviderException ex) 
    	{
    		if(cifrar)
    			throw new Exception("Erro na encriptação do arquivo! Erro: " + ex.getMessage());
    		
    		throw new Exception("Erro na desencriptação do arquivo! Erro: " + ex.getMessage());
        }
    }
	
	public static byte[] CriptografarMensagem(String plainText, SecretKey key) throws Exception 
	{ 
		String IV = "AAAAAAAAAAAAAAAA";
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER); 
		//SecretKeySpec key = new SecretKeySpec(chave.getBytes("UTF-8"), "AES"); 
		
		cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8"))); 
				
		return cipher.doFinal(plainText.getBytes("UTF-8")); 
	}
	
	public static byte[] CriptografarMensagem(String plainText, String chave) throws Exception 
	{ 
		String IV = "AAAAAAAAAAAAAAAA";
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER); 
		SecretKeySpec key = new SecretKeySpec(chave.getBytes("UTF-8"), "AES"); 
		
		cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8"))); 
				
		return cipher.doFinal(plainText.getBytes("UTF-8")); 
	}
	
	public static String DesencriptarMensagem(byte[] cipherText, String chave) throws Exception
	{ 
		String IV = "AAAAAAAAAAAAAAAA";
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER); 
		SecretKeySpec key = new SecretKeySpec(chave.getBytes("UTF-8"), "AES");
		
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8"))); 
		
		return new String(cipher.doFinal(cipherText), "UTF-8"); 
	}
	
	public static String DesencriptarMensagem(byte[] cipherText, SecretKey chave) throws Exception
	{ 
		String IV = "AAAAAAAAAAAAAAAA";
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER); 
		//SecretKeySpec key = new SecretKeySpec(chave.getBytes("UTF-8"), "AES");
		
		cipher.init(Cipher.DECRYPT_MODE, chave, new IvParameterSpec(IV.getBytes("UTF-8"))); 
		
		return new String(cipher.doFinal(cipherText), "UTF-8"); 
	}
		
	public static SecretKey GerarChaveSimetrica() throws Exception
	{
		try
		{
			KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM, PROVIDER);
			keyGen.init(256); // Tamanho da chave como exemplo.
			SecretKey secretKey = keyGen.generateKey();
			
			return secretKey;
			
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao gerar chave simétrica: " + e.getMessage());
		}
	}
	
	public static SecretKey GerarChaveSimetricaHashArquivo(String hashArquivo) throws Exception
	{
		try
		{
			Date date = new Date();

			String hashChave = HashGeneratorUtils.generateSHA256(date.getSeconds() + MouseInfo.getPointerInfo().getLocation().y + hashArquivo + date.getMinutes()  + MouseInfo.getPointerInfo().getLocation().x + date.getHours());
			
			SecretKeySpec secretKey = new SecretKeySpec(hashChave.substring(0, 16).getBytes(), ALGORITHM);
			
			return secretKey;
			
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao gerar chave simétrica: " + e.getMessage());
		}
	}
	
	public static byte[] CriptografarChavePrivada(boolean cifrar, String key, String IV, byte[] inputFile) throws Exception 
    {
		try 
    	{    		
    		Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
    		Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER);
    		
    		if(cifrar)
    			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV.getBytes()));
    		else
    			cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV.getBytes()));
    		
            byte[] outputBytes = cipher.doFinal(inputFile);
            
            return outputBytes;
            
        }
    	catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchProviderException ex) 
    	{
    		if(cifrar)
    			throw new Exception("Erro na encriptação da chave privada! Erro: " + ex.getMessage());
    		
    		throw new Exception("Error desencriptação da chave privada" + ex.getMessage());
    	}
    	catch (IllegalBlockSizeException ex1) 
    	{
    		if(cifrar)
    			throw new Exception("Erro na encriptação da chave privada! Erro: " + ex1.getMessage());
    		
    		throw new Exception("Error desencriptação da chave privada" + ex1.getMessage());
    	}
    	catch(BadPaddingException ex2)
    	{
    		//Chave errada
    		return null;
        }
    	
    	//catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | NoSuchProviderException ex)
    	
    }
	
}
