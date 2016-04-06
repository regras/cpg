package A2F;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

import Criptografia.CriptoSimetrica;
import PBKDF2.PasswordHash;

public class GoogleAuthenticator 
{
	//String to hold the name of the A2F key file.
	public static String A2F_KEY_FILE = "keys/a2f.key";
		
	public static String CreatePasspgrase(String userPassphrase) throws Exception 
	{
		try
		{
			// Allocating the buffer
			byte[] buffer = new byte[16];
			
			// Filling the buffer with random numbers.
			// Notice: you want to reuse the same random generator
			// while generating larger random number sequences.
			new Random().nextBytes(buffer);
			
			// Getting the key and converting it to Base32
			Base32 codec = new Base32();
			byte[] secretKey = Arrays.copyOf(buffer, 10);
			byte[] bEncodedKey = codec.encode(secretKey);
			String encodedKey = new String(bEncodedKey);
	
			System.out.println("secret " + encodedKey);
	        
	        File a2fKeyFile = new File(A2F_KEY_FILE);
					
			// Create a file to store the A2F key
			if (a2fKeyFile.getParentFile() != null)
			{
				a2fKeyFile.getParentFile().mkdirs();
			}
			
			if(bEncodedKey != null && bEncodedKey.length > 1)
			{
				String[] password = PasswordHash.DerivarChaveSimetricaPassword(userPassphrase);
				
				//Saving the A2F key in a file
				ObjectOutputStream a2fKey = new ObjectOutputStream(new FileOutputStream(a2fKeyFile));
				a2fKey.writeObject(CriptoSimetrica.CriptografarChavePrivada(true, password[0], password[1], bEncodedKey));
				
				a2fKey.close();
	
			}
			else
				throw new Exception("Erro ao criar segredo da autenticação dois fatores!");
			
			return encodedKey;
        
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao criar segredo da autenticação dois fatores! Erro:" + ex.getMessage());
		}
    }
	
	public static String VerifyPassphraseLogin(String userPassphrase) throws Exception
	{
		try
		{
			String[] password = PasswordHash.DerivarChaveSimetricaPassword(userPassphrase);
			
			//Carregar arquivo da chave A2F - o conteúdo está cifrado
			ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(A2F_KEY_FILE));
			
			//Decifrar chave privada		
			byte[] chave = CriptoSimetrica.CriptografarChavePrivada(false, password[0], password[1], (byte[])fluxoEntrada.readObject());
			
			if(chave != null && chave.length > 1)
			{
				String decodedKey = new String(chave);
				
				return decodedKey;
					
			}
			else
				return null;			
			
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao verificar login! Erro: " + ex.getMessage());
		}
	}
	
	public static String Login(String decodedKey, long code) throws Exception
	{
		try
		{
			long t = getTimeIndex();
						
			if(CheckCode(decodedKey, code, t))
				return decodedKey;
			
			return null;			
			
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao realizar login A2F! Erro: " + ex.getMessage());
		}
	}
	
	public static boolean A2FUseCheck() 
	{
		File chaveA2F = new File(A2F_KEY_FILE);
		
		if (chaveA2F.exists()) 
		{
			return true;
		}
	    
		return false;
	}
	
	public static String getQRBarcodeURL(String user, String host, String secret) 
	{
		String format = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s";
		return String.format(format, user, host, secret);
	}
		
	public static boolean CheckCode(String secret, long code, long t) throws NoSuchAlgorithmException, InvalidKeyException 
	{
		Base32 codec = new Base32();

		//byte[] secretBytes = new Base32().decode(secret);
		byte[] decodedKey = codec.decode(secret);
		
		// Window is used to check codes generated in the near past.
		// You can use this value to tune how far you're willing to go.
		int window = 3;
		
		for (int i = -window; i <= window; ++i) 
		{
			long hash = verify_code(decodedKey, t + i);
			
			if (hash == code) 
			{
				return true;
			}
		}
		
		// The validation code is invalid.
		return false;
		
	}
	
	private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException 
	{
		byte[] data = new byte[8];
		long value = t;
		
		for (int i = 8; i-- > 0; value >>>= 8) 
		{
			data[i] = (byte) value;
		}
		
		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);
		int offset = hash[20 - 1] & 0xF;
		
		// We're using a long because Java hasn't got unsigned int.
		long truncatedHash = 0;
		
		for (int i = 0; i < 4; ++i) 
		{
			truncatedHash <<= 8;
			// We are dealing with signed bytes:
			// we just keep the first byte.
			truncatedHash |= (hash[offset + i] & 0xFF);
		}
		
		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;
		
		return (int) truncatedHash;
		
	}

	public static long getTimeIndex() 
	{
	    return System.currentTimeMillis()/1000/30;
	}
}
