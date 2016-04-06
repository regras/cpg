package PBKDF2;
import java.security.SecureRandom;

import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;

import Criptografia.HashGeneratorUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


//CÓDIGO ADAPTADO DE VERSÃO OBTIDA EM:

/* GitHub Gist
 * https://gist.github.com/jtan189/3804290#file-javapasswordsecurity-java
 * 
 * PBKDF2 salted password hashing.
 * Author: havoc AT defuse.ca
 * www: http://crackstation.net/hashing-security.htm
 * 
 * 
 * 
 */

public class PasswordHash
{
	//Classe de acordo com a RFC 2898
	//PKCS #5: Password-Based Cryptography Specification Version 2.0
	//RFC 2898	http://tools.ietf.org/html/rfc2898	
	
	//OBS:
	//Note that storing (also in cleartext) a variable number of iterations per user also helps. Instead of always running PBKDF2 64,000 time, 
	//instead generate a random salt, and a random number I between 1 and 20,000. Run PBKDF2 64,000 + I times for that particular username. 
	//This makes cracking it just a little more difficult, and may prevent certain optimizations in cracking code from being useful.
		
	
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1"; //PBKDF2WithHmacSHA512

    // The following constants may be changed without breaking existing hashes.
    public static final int SALT_BYTES = 24;
    public static final int HASH_BYTES = 24;
    public static final int PBKDF2_ITERATIONS = 10000;

    public static final int ITERATION_INDEX = 0;
    public static final int SALT_INDEX = 1;
    public static final int PBKDF2_INDEX = 2;
    
    public static final int QuantCaracteresChave = 16;
    public static final int TAMANHOHASH = 256;
    
    
    static File arquivo = new File("senha.txt");
	static FileWriter fw;
	static FileReader fr;
	static BufferedWriter bw;
	static BufferedReader br;
	
	public static String[] DerivarChaveSimetricaPassword(String password) throws Exception
    {
    	try
    	{
    		// Generate a random salt
            //SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_BYTES];
            //random.nextBytes(salt);
            
            String hashPassword = HashGeneratorUtils.generateSHA256(password);
            
            int iteracoesAdicionais = 0;
			
			char[] cadeia = hashPassword.substring(TAMANHOHASH/4 - HASH_BYTES,  TAMANHOHASH/4).toCharArray();
			
			for(int i = 0; i < cadeia.length; i++)
			{
				int y = cadeia[i];
				iteracoesAdicionais = iteracoesAdicionais + y;
			}
			
			salt = hashPassword.substring(0, SALT_BYTES).getBytes();
			
            // Hash the password
            byte[] hash = pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS + (iteracoesAdicionais*2), HASH_BYTES);
            
            //Retornar uma chave com X caracteres, definidos pela variável "QuantCaracteresChave"
            
            String[] ret = new String[2];
            
            //Chave
            ret[0] = toHex(hash).substring(0, QuantCaracteresChave);
            //IV
            ret[1] =  toHex(hash).substring(QuantCaracteresChave + 1, QuantCaracteresChave + 17);
    		
            return ret;
    	}
    	catch(Exception e)
    	{
    		throw new Exception("Erro ao criar Password! Erro: " + e.getMessage());
    	}
    }
			
	public static String CriarPasswordArquivo(String password) throws Exception
    {
    	try
    	{
    		if(!arquivo.exists( ))
    		{
    			try 
    			{
    				arquivo.createNewFile();
    			} 
    			catch (IOException e) 
    			{
    				e.printStackTrace();
    			}
    		}	
    		
    		//Abrir arquivo de senha
    		fw = new FileWriter(arquivo, false);
    		bw = new BufferedWriter(fw);
    		
    		// Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_BYTES];
            random.nextBytes(salt);

            bw.append("salt:" + toHex(salt));
    		bw.newLine();
    		
    		bw.append("iterations:" + PBKDF2_ITERATIONS);
    		bw.newLine();
    		
    		String hashPassword = HashGeneratorUtils.generateSHA256(password);
    		
    		int xddd = 0;
			
			char[] xc = hashPassword.substring(0,  20).toCharArray();
			
			for(int i = 0; i < xc.length; i++)
			{
				int y = xc[i];
				xddd = xddd + y;
			}
			
			System.out.println("Número: " + xddd);
            
            // Hash the password
            byte[] hash = pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES);
            
    		//Fechar arquivo de senha
    		bw.close();
    		fw.close();
    		
    		return toHex(hash);
    	}
    	catch(Exception e)
    	{
    		throw new Exception("Erro ao criar Password! Erro: " + e.getMessage());
    	}
    }
	
	public static String VerificarPasswordArquivo(String password) throws Exception
    {
    	try
    	{
    		if(!arquivo.exists( ))
    		{
    			throw new Exception("Erro: arquivo de senhas não encontrado!");
    		}	
    		
    		//Abrir arquivo de senha para leitura
    		fr = new FileReader(arquivo);
    		br = new BufferedReader(fr); 
    		
    		String linha = ""; 
    		byte[] salt = null;
    		int iterations = 0;
    		    		
    		//Ler todas as linhas
    		while (true) 
    		{ 
    			if (linha != null) 
    			{ 
    				String[] params = linha.split(":");
    				
    				if(params != null)
    				{
    					if(params[0].equals("salt"))
    						salt = fromHex(params[1]);
    					else if(params[0].equals("iterations"))
    						iterations = Integer.parseInt(params[1]);
    				}
				} 
    			
    			else break; 
    			
    			linha = br.readLine(); 
			} 
    		
    		byte[] testHash = pbkdf2(password.toCharArray(), salt, iterations, HASH_BYTES);
    		
    		//Fechar arquivo de senha
    		br.close();
    		fr.close();
    		
    		return toHex(testHash);
    		
    	}
    	catch(Exception e)
    	{
    		throw new Exception("Erro ao criar Password! Erro: " + e.getMessage());
    	}
    }
    
	public static void main()
    {
        try
        {
            // Print out 10 hashes
            for(int i = 0; i < 10; i++)
                System.out.println(PasswordHash.createHash("p\r\nassw0Rd!"));

            // Test password validation
            boolean failure = false;
            System.out.println("Running tests...");
            for(int i = 0; i < 100; i++)
            {
                String password = ""+i;
                String hash = createHash(password);
                String secondHash = createHash(password);
                if(hash.equals(secondHash)) {
                    System.out.println("FAILURE: TWO HASHES ARE EQUAL!");
                    failure = true;
                }
                String wrongPassword = ""+(i+1);
                if(validatePassword(wrongPassword, hash)) {
                    System.out.println("FAILURE: WRONG PASSWORD ACCEPTED!");
                    failure = true;
                }
                if(!validatePassword(password, hash)) {
                    System.out.println("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
                    failure = true;
                }
            }
            if(failure)
                System.out.println("TESTS FAILED!");
            else
                System.out.println("TESTS PASSED!");
        }
        catch(Exception ex)
        {
            System.out.println("ERROR: " + ex);
        }
    }
    
    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param   password    the password to hash
     * @return              a salted PBKDF2 hash of the password
     */
    public static String createHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        return createHash(password.toCharArray());
    }

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param   password    the password to hash
     * @return              a salted PBKDF2 hash of the password
     */
    public static String createHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTES);
        // format iterations:salt:hash
        return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" +  toHex(hash);
    }
    
    /**
     * Validates a password using a hash.
     *
     * @param   password    the password to check
     * @param   goodHash    the hash of the valid password
     * @return              true if the password is correct, false if not
     */
    public static boolean validatePassword(String password, String goodHash) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        return validatePassword(password.toCharArray(), goodHash);
    }

    /**
     * Validates a password using a hash.
     *
     * @param   password    the password to check
     * @param   goodHash    the hash of the valid password
     * @return              true if the password is correct, false if not
     */
    public static boolean validatePassword(char[] password, String goodHash) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Decode the hash into its parameters
        String[] params = goodHash.split(":");
        int iterations = Integer.parseInt(params[ITERATION_INDEX]);
        byte[] salt = fromHex(params[SALT_INDEX]);
        byte[] hash = fromHex(params[PBKDF2_INDEX]);
        // Compute the hash of the provided password, using the same salt, 
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }
    
    /**
     * Validates a password using a hash.
     *
     * @param   password    the password to check
     * @param   goodHash    the hash of the valid password
     * @return              true if the password is correct, false if not
     */
    public static String validatePasswordWithParameters(String password, String iterationsParam, String saltParam) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = Integer.parseInt(iterationsParam);
        byte[] salt = fromHex(saltParam);
        
        // Compute the hash of the provided password, using the same salt, 
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password.toCharArray(), salt, iterations, HASH_BYTES);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return toHex(testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line 
     * system using a timing attack and then attacked off-line.
     * 
     * @param   a       the first byte array
     * @param   b       the second byte array 
     * @return          true if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(byte[] a, byte[] b)
    {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    /**
     *  Computes the PBKDF2 hash of a password.
     *
     * @param   password    the password to hash.
     * @param   salt        the salt
     * @param   iterations  the iteration count (slowness factor)
     * @param   bytes       the length of the hash to compute in bytes
     * @return              the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param   hex         the hex string
     * @return              the hex string decoded into a byte array
     */
    private static byte[] fromHex(String hex)
    {
        byte[] binary = new byte[hex.length() / 2];
        for(int i = 0; i < binary.length; i++)
        {
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return binary;
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param   array       the byte array to convert
     * @return              a length*2 character string encoding the byte array
     */
    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) 
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

}