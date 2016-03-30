package Criptografia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import PBKDF2.PasswordHash;

//import com.Ostermiller.util.Base64;

import org.apache.commons.codec.binary.Base64;

//import com.Ostermiller.util.Base64;

public class CriptoAssimetrica 
{
	//String to hold name of the encryption algorithm.
	public static final String ALGORITHM = "RSA";
		
	//String to hold the name of the private key file.
	public static String PRIVATE_KEY_FILE = "keys/private.key";

	//String to hold name of the public key file.
	public static String PUBLIC_KEY_FILE = "keys/public.key";
		
	//String to hold name of the key of file's name.
	//public static final String FILENAME_KEY_FILE = "keys/filekey.key";
	
	/*
	public void RSACryptoUtil() throws Exception
	{
		if (!ChecarExistenciaDasChaves()) 
	    {
	    	// Method generates a pair of keys using the RSA algorithm and stores it
	    	// in their respective files
	    	
	    	//System.out.println("gerou chaves");
	    	
	    	try 
	    	{
				GerarChave();
			} 
	    	catch (Exception e) 
	    	{
				throw new Exception(e.getMessage());
			}
	    }
	}
	*/
	
	public boolean ChecarExistenciaDasChaves() 
	{
		File chavePrivada = new File(PRIVATE_KEY_FILE);
		File chavePublica = new File(PUBLIC_KEY_FILE);

		if (chavePrivada.exists() && chavePublica.exists()) 
		{
			return true;
		}
	    
		return false;
	}
	
	public boolean GerarChave(String password) throws Exception 
	{
		try
		{
			/*
			 * 
			 * http://knowledge-oracle.blogspot.in/2009/02/import-private-key-and-certificate-in.html
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
	    keyPairGenerator.initialize(2048);
	    KeyPair keyPair = keyPairGenerator.genKeyPair();
	    
		File privateKeyFile = new File(PRIVATE_KEY_FILE);
		File publicKeyFile = new File(PUBLIC_KEY_FILE);
		
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		
			
		
		ks.load(null, null);
		
		KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry((SecretKey) keyPair.getPrivate());
		
		//KeyStore.PrivateKeyEntry prEntry = new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), null);
		
		KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection("testesenha".toCharArray());
		
		//KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry((SecretKey) keyPair.getPrivate());
		ks.setEntry("myprivatekey", skEntry, protParam);
		
		FileOutputStream fos = new FileOutputStream("agb50.keystore");
		ks.store(fos, "somepassword".toCharArray());
		fos.close();
		
	    
        // get my private key
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)ks.getEntry("privateKeyAlias", protParam);
        PrivateKey myPrivateKey = pkEntry.getPrivateKey();
        
        
        
        
			
			
			
			
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
		    keyPairGenerator.initialize(2048);
		    KeyPair keyPair = keyPairGenerator.genKeyPair();
		    
			File privateKeyFile = new File(PRIVATE_KEY_FILE);
			File publicKeyFile = new File(PUBLIC_KEY_FILE);
			
			
			
			KeyStore ks = KeyStore.getInstance("JKS");
			//byte[] password = "teste".getBytes();
			
			
			ks.load(null, null);
			KeyStore.PrivateKeyEntry prEntry = new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), null);
			//KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry((SecretKey) keyPair.getPrivate());
			ks.setEntry("myprivatekey", prEntry, new KeyStore.PasswordProtection("testesenha".toCharArray()));
			
			FileOutputStream fos = new FileOutputStream("agb50.keystore");
			ks.store(fos, "somepassword".toCharArray());
			fos.close();
			
			// Load the keystore
			File keyStoreFileName = new File("agb50.keystore");
			
			KeyStore keyStore = KeyStore.getInstance("jks");
			FileInputStream keyStoreInputStream = new FileInputStream(keyStoreFileName);
			keyStore.load(keyStoreInputStream, "testesenha".toCharArray());
			keyStoreInputStream.close();
			
			Key senha = keyStore.getKey("myprivatekey", "testesenha".toCharArray());
			
			

			// Create files to store public and private key
			if (privateKeyFile.getParentFile() != null)
			{
				privateKeyFile.getParentFile().mkdirs();
			}
	      
			privateKeyFile.createNewFile();
			  
			if (publicKeyFile.getParentFile() != null) 
			{
				publicKeyFile.getParentFile().mkdirs();
			}
	      
			publicKeyFile.createNewFile();

			// Saving the Public key in a file
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
			publicKeyOS.writeObject(keyPair.getPublic());
			publicKeyOS.close();

			// Saving the Private key in a file
			ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
			privateKeyOS.writeObject(keyPair.getPrivate());
			privateKeyOS.close();
			
			*/
			
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
		    //keyPairGenerator.initialize(1024);
		    keyPairGenerator.initialize(2048);
		    KeyPair keyPair = keyPairGenerator.genKeyPair();
		    
			File privateKeyFile = new File(PRIVATE_KEY_FILE);
			File publicKeyFile = new File(PUBLIC_KEY_FILE);
			
			// Create files to store public and private key
			if (privateKeyFile.getParentFile() != null)
			{
				privateKeyFile.getParentFile().mkdirs();
			}
	      
			privateKeyFile.createNewFile();
			  
			if (publicKeyFile.getParentFile() != null) 
			{
				publicKeyFile.getParentFile().mkdirs();
			}
	      
			publicKeyFile.createNewFile();

			// Saving the Public key in a file
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
			publicKeyOS.writeObject(keyPair.getPublic());
			publicKeyOS.close();

			// Saving the Private key in a file
			//ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
			//privateKeyOS.writeObject(keyPair.getPrivate());
			//privateKeyOS.close();
			
			//Derivando uma chave simétrica da senha do usuário
			String[] key = PasswordHash.DerivarChaveSimetricaPassword(password); 
			
			if(key != null && key.length > 1)
			{
				//Saving the Private key in a file
				ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
				privateKeyOS.writeObject(CriptoSimetrica.CriptografarChavePrivada(true, key[0], key[1], keyPair.getPrivate().getEncoded()));
				
				//privateKeyOS.writeObject(keyPair.getPrivate());
				privateKeyOS.close();

			}
			else
				throw new Exception("Erro ao derivar chave simétrica!");
			
			//CriptografarChavePrivada(password);
			
		} 
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
		
		return true;
	}
	
	public String CifrarTexto(String mensagem) throws Exception
	{
		try
		{
			ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
		    final PublicKey chavePublica = (PublicKey)fluxoEntrada.readObject();

		    Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cifrador.init(Cipher.ENCRYPT_MODE, chavePublica);

		    byte[] textoCifrado = cifrador.doFinal(mensagem.getBytes());
		    
		    //String textoCifradoBase64 = Base64.encodeToString(textoCifrado);
		    String textoCifradoBase64 = Base64.encodeBase64String(textoCifrado);
		    
		    return textoCifradoBase64;
		    
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao cifrar mensagem!" + e.getMessage());
		}
	}
	
	public String DecifrarTexto(String textoCifrado, String senhaUsuario) throws Exception
	{
		try
		{
			//Derivando a chave que desencriptará a chave privada do usuário
			String[] password = PasswordHash.DerivarChaveSimetricaPassword(senhaUsuario);
			
			if(password != null && password.length > 1)
			{
				//Carregar arquivo da chave privada - o conteúdo está cifrado
				ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
				
				//Decifrar chave privada		
				byte[] chave = CriptoSimetrica.CriptografarChavePrivada(false, password[0], password[1], (byte[])fluxoEntrada.readObject());
				
				if(chave != null && chave.length > 0)
				{
					//http://stackoverflow.com/questions/19353748/how-to-convert-byte-array-to-privatekey-or-publickey-type
					KeyFactory kf = KeyFactory.getInstance("RSA");
					
					PrivateKey chavePrivada = kf.generatePrivate(new PKCS8EncodedKeySpec(chave));
					//PublicKey publicA = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
							    
				    Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				    cifrador.init(Cipher.DECRYPT_MODE, chavePrivada);
				    
				    //Setar objeto da chave privada
				    //chavePrivada = kf.generatePrivate(new PKCS8EncodedKeySpec(new byte[]));
				        
				    //byte[] textoCifradoBase64 = Base64.decodeToBytes(textoCifrado);
				    
				    byte[] textoCifradoBase64 = Base64.decodeBase64(textoCifrado);
				    		    	    
				    byte[] textoDecifrado = cifrador.doFinal(textoCifradoBase64);
				    
				    return new String(textoDecifrado);
				}
				else
					return null;
			}
			else
				throw new Exception("Erro ao decifrar mensagem! Não foi possível derivar uma chave da senha do usuário.");
			
			
			/*
			ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
		    final PrivateKey chavePrivada = (PrivateKey) fluxoEntrada.readObject();
		    
		    Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cifrador.init(Cipher.DECRYPT_MODE, chavePrivada);
		        
		    byte[] textoCifradoBase64 = Base64.decodeToBytes(textoCifrado);
		    		    	    
		    byte[] textoDecifrado = cifrador.doFinal(textoCifradoBase64);
		    
		    return new String(textoDecifrado);
		    
		    */
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao decifrar mensagem!" + e.getMessage());
		}
	}
	
	public String AssinarMensagem(String mensagem, String senhaUsuario) throws Exception
	{
		try
		{
			//Derivando a chave que desencriptará a chave privada do usuário
			String[] password = PasswordHash.DerivarChaveSimetricaPassword(senhaUsuario);
			
			if(password != null && password.length > 1)
			{
				//Carregar arquivo da chave privada - o conteúdo está cifrado
				ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
				
				//Decifrar chave privada		
				byte[] chave = CriptoSimetrica.CriptografarChavePrivada(false, password[0], password[1], (byte[])fluxoEntrada.readObject());
				
				KeyFactory kf = KeyFactory.getInstance("RSA");
				
				PrivateKey chavePrivada = kf.generatePrivate(new PKCS8EncodedKeySpec(chave));
				
				//ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
			    //final PrivateKey chavePrivada = (PrivateKey) fluxoEntrada.readObject();
			    
				Signature signature = Signature.getInstance("SHA1withRSA");
				signature.initSign(chavePrivada);
				signature.update(mensagem.getBytes()); 
				
				byte[] assinatura= signature.sign();
				
				//return Base64.encodeToString(assinatura);
				
				return Base64.encodeBase64String(assinatura);
				
			}
			else
				throw new Exception("Erro ao assinar mensagem! Não foi possível derivar uma chave da senha do usuário.");
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao assinar mensagem! " + e.getMessage());
		}
	}
	
	public boolean ConferirAssinaturaMensagem(String mensagem, String assinatura) throws Exception
	{
		try
		{
			ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
		    final PublicKey chavePublica = (PublicKey) fluxoEntrada.readObject();
		    
			Signature signature = Signature.getInstance("SHA1withRSA");
			
			signature.initVerify(chavePublica);
			
			//byte[] assinaturaBase64 = Base64.decodeToBytes(assinatura);
			
			byte[] assinaturaBase64 = Base64.decodeBase64(assinatura);
			
			signature.update(mensagem.getBytes()); 
			
			return signature.verify(assinaturaBase64);
			
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao verificar assinatura da mensagem! " + e.getMessage());
		}
	}
	
	public boolean ConferirSenhaUsuario(String senhaUsuario) throws Exception
	{
		//Verificação se a senha que o usuário inseriu é realmente a correta
		//Primeiramente, é decifrado a chave privada do usuário com a senha fornecida.
		//No segundo passo, um texto qualquer é cifrado com a chave privada decifrada
		//No terceiro passo, o texto cifrado é decifrado com a chave pública do usuário
		//No último passo, o texto decifrado é comparado ao texto original. 
			//Se ambos forem iguais, a senha está correta.
			//Caso contrário, é requisitado a senha do usuário novamente.
		
		try
		{
			if(senhaUsuario != null && !senhaUsuario.isEmpty())
			{
				String mensagemTeste = "Teste de mensagem a ser cifrada";
				
				//Cifrando mensagem de teste com a chave pública do usuário
				String testeCifrado = CifrarTexto(mensagemTeste);
								
				//Deciifrando mensagem de teste e conferindo com a original
				String testeDecifrado = DecifrarTexto(testeCifrado, senhaUsuario);
				
				if(testeDecifrado != null && testeDecifrado.equals(mensagemTeste))
					return true;				
			}
			
		}
		catch(Exception e)
		{
			throw new Exception("Erro: Não foi possível conferir a senha do usuário! Erro: " + e.getMessage());
		}
		
		return false;
	}
	
	
	
	public String CifrarChave(SecretKey key) throws Exception
	{
		try
		{
			ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
		    final PublicKey chavePublica = (PublicKey)fluxoEntrada.readObject();

		    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cipher.init(Cipher.WRAP_MODE, chavePublica);

		    byte[] wrappedKey = cipher.wrap(key);
		    
		    //String textoCifradoBase64 = Base64.encodeToString(wrappedKey);
		    
		    String textoCifradoBase64 = Base64.encodeBase64String(wrappedKey);
		    
		    return textoCifradoBase64;
		    
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao cifrar mensagem!" + e.getMessage());
		}
	}
	
	public Key DecifrarChave(String textoCifrado, String senhaUsuario) throws Exception
	{
		try
		{
			//Derivando a chave que desencriptará a chave privada do usuário
			String[] password = PasswordHash.DerivarChaveSimetricaPassword(senhaUsuario);
			
			if(password != null && password.length > 1)
			{
				//Carregar arquivo da chave privada - o conteúdo está cifrado
				ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
				
				//Decifrar chave privada		
				byte[] chave = CriptoSimetrica.CriptografarChavePrivada(false, password[0], password[1], (byte[])fluxoEntrada.readObject());
				
				//http://stackoverflow.com/questions/19353748/how-to-convert-byte-array-to-privatekey-or-publickey-type
				KeyFactory kf = KeyFactory.getInstance("RSA");
				
				PrivateKey chavePrivada = kf.generatePrivate(new PKCS8EncodedKeySpec(chave));
				//PublicKey publicA = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
						    
			    Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			    cifrador.init(Cipher.UNWRAP_MODE, chavePrivada);
			    
			    //Setar objeto da chave privada
			    //chavePrivada = kf.generatePrivate(new PKCS8EncodedKeySpec(new byte[]));
			        
			    //byte[] textoCifradoBase64 = Base64.decodeToBytes(textoCifrado);
			    byte[] textoCifradoBase64 = Base64.decodeBase64(textoCifrado);
			    		    	    
			    Key textoDecifrado = cifrador.unwrap(textoCifradoBase64, "AES",  Cipher.SECRET_KEY);
			    
			    return textoDecifrado;
			}
			else
				throw new Exception("Erro ao decifrar mensagem! Não foi possível derivar uma chave da senha do usuário.");
			
			
			/*
			ObjectInputStream fluxoEntrada = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
		    final PrivateKey chavePrivada = (PrivateKey) fluxoEntrada.readObject();
		    
		    Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cifrador.init(Cipher.DECRYPT_MODE, chavePrivada);
		        
		    byte[] textoCifradoBase64 = Base64.decodeToBytes(textoCifrado);
		    		    	    
		    byte[] textoDecifrado = cifrador.doFinal(textoCifradoBase64);
		    
		    return new String(textoDecifrado);
		    
		    */
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao decifrar mensagem!" + e.getMessage());
		}
	}
	
	
	
	/*
	 * KEYSTORE
	 * 
	 * 
	 * 
	 * Security.addProvider(new FlexiCoreProvider());

        /*
         * Cipher cipher1 = Cipher.getInstance("AES128_CBC", "FlexiCore");
         * KeyGenerator keyGen = KeyGenerator.getInstance("AES", "FlexiCore");
         * SecretKey secKey = keyGen.generateKey();
         * System.out.println(secKey);

        Cipher cipher1 = Cipher.getInstance("AES128_CBC", "FlexiCore");
        KeyStore keyStore = KeyStore.getInstance("JCEKS");

        FileInputStream fis = new FileInputStream("C:\\mykey.keystore"); // here
                                                                         // i am
                                                                         // uploading
        keyStore.load(fis, "javaci123".toCharArray());
        fis.close();
        Key secKey = (Key) keyStore.getKey("mySecretKey",
                "javaci123".toCharArray()); // line 35

        System.out.println("Found Key: " + (secKey));

        String cleartextFile = "C:\\cleartext.txt";
        String ciphertextFile = "C:\\ciphertextSymm.txt";

        // FileInputStream fis = new FileInputStream(cleartextFile);
        FileOutputStream fos = new FileOutputStream(ciphertextFile);

        String cleartextAgainFile = "C:\\cleartextAgainSymm.txt";

        cipher1.init(Cipher.DECRYPT_MODE, secKey);
        fis = new FileInputStream(ciphertextFile);

        // fis = new FileInputStream(ciphertextFile);
        CipherInputStream cis = new CipherInputStream(fis, cipher1);
        fos = new FileOutputStream(cleartextAgainFile);
        byte[] block = new byte[8];
        int i;
        while ((i = fis.read(block)) != -1) {
            cis.read(block, 0, i);
        }
        cis.close();
	 * 
	 * 
	 * 
	 * */
	
	
	
	/*
	KEYSTORE
	
	@Test
	public void testEncrypt() throws Exception 
	{
		SecretKey key = KeyGenerator.getInstance("AES").generateKey();
		
		KeyStore ks = KeyStore.getInstance("JCEKS");
		ks.load(null, null);
		KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
		ks.setEntry("mykey", skEntry, new KeyStore.PasswordProtection("mykeypassword".toCharArray()));
		
		FileOutputStream fos = new FileOutputStream("agb50.keystore");
		ks.store(fos, "somepassword".toCharArray());
		fos.close();
		
		Cryptographical crypto = AESCryptoImpl.initialize(new AESCryptoKey(key));
		String enc = crypto.encrypt("Andy");
		Assert.assertEquals("Andy", crypto.decrypt(enc));
		
		//alternatively, read the keystore file itself to obtain the key

 		Cryptographical anotherInst = AESCryptoImpl.initialize(new AESCryptoKey(key));
 		String anotherEncrypt = anotherInst.encrypt("Andy");
 		Assert.assertEquals("Andy", anotherInst.decrypt(anotherEncrypt));

 		Assert.assertTrue(anotherEncrypt.equals(enc));
	}
	
	*/
	
	
	
	/*

	
	
	private static Random rand = new Random();
	private static char[] letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
	

	public RSACryptoUtil()
	{
		//ENCODER: http://ostermiller.org/utils/download.html
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		// Check if the pair of keys are present else generate those.
	    if (!ChecarExistenciaDasChaves()) 
	    {
	    	// Method generates a pair of keys using the RSA algorithm and stores it
	    	// in their respective files
	    	
	    	//System.out.println("gerou chaves");
	    	
	    	GerarChave();
	    }
	}
	
	
	
	
	
	public String CifrarNomeArquivo(String mensagem)
	{
		try
		{
			//
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] salt=new byte[]{1,2,3,4,5};
			int iterationCount=10;
			char[] password=new char[]{'1','2','3','4','5'};
			KeySpec ks = new PBEKeySpec(password,salt,1024,128);
			PBEKeySpec pbeks=new PBEKeySpec(password,salt,iterationCount);
			SecretKey s = f.generateSecret(ks);
			Key k = new SecretKeySpec(s.getEncoded(),"AES");
			
			
			//QUANDO USA O CBC, TEM O IV. LEMBRAR DE GUARDÁ-lo NA DECIFRAGEM.
			
			ChecarGerarChaveNomeArquivo();
			
			FileReader arq = new FileReader(FILENAME_KEY_FILE);
			BufferedReader lerArq = new BufferedReader(arq);
			
			StringBuilder chave = new StringBuilder();

			String linha = lerArq.readLine(); // lê a primeira linha
			// a variável "linha" recebe o valor "null" quando o processo 
			// de repetição atingir o final do arquivo texto 
			
			while (linha != null) 
			{ 
				chave.append(linha);
				linha = lerArq.readLine(); // lê da segunda até a última linha
			} 
			
			//System.out.printf("\nConteúdo de chave: " + chave);
			
			arq.close(); 
			
			// Check if the pair of keys are present else generate those.
		    if (!ChecarExistenciaDasChaves()) 
		    {
		    	// Method generates a pair of keys using the RSA algorithm and stores it
		    	// in their respective files
		    	
		    	System.out.println("gerou chaves");
		    	
		    	GerarChave();
		    }
		    
		    String chaveDecifrada = DecifrarTexto(chave.toString());
		    
		    Key secretKey = new SecretKeySpec(chaveDecifrada.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipher.ENCRYPT_MODE, secretKey);
            
            byte[] textoCifradoBase64 = Base64.decodeToBytes(mensagem);
    	    
		    byte[] textoDecifrado = cipher.doFinal(textoCifradoBase64);
		        
		    return new String(Base64.encodeToString(textoDecifrado));          
            
		    
		}
		catch(Exception e)
		{
			System.out.println("Erro ao cifrar mensagem!" + e.getMessage());
			
			return null;
		}
	}
	
	
	
	public boolean ChecarGerarChaveNomeArquivo()
	{
		try
		{
			File fileNameKeyFile = new File(FILENAME_KEY_FILE);
			
			if(!fileNameKeyFile.exists())
			{
				// Check if the pair of keys are present else generate those.
			    if (!ChecarExistenciaDasChaves()) 
			    {
			    	// Method generates a pair of keys using the RSA algorithm and stores it
			    	// in their respective files
			    	
			    	System.out.println("gerou chaves");
			    	
			    	GerarChave();
			    }
			    
				//Gravando chave cifrada em um arquivo
				FileWriter arq = new FileWriter(fileNameKeyFile); 
				PrintWriter gravarArq = new PrintWriter(arq); 
				
				String chaveSimetrica = "";
				
				//DateFormat dateFormat = new SimpleDateFormat("ddssHHMMmm"); 
				Date date = new Date(); 
				//chaveSimetrica = dateFormat.format(date);

				chaveSimetrica = nomeAleatorio(8, (date.getMinutes()%date.getSeconds()));
				chaveSimetrica = chaveSimetrica + nomeAleatorio(8, ((date.getMinutes()%date.getSeconds()) + (MouseInfo.getPointerInfo().getLocation().y%MouseInfo.getPointerInfo().getLocation().x)));
				
				String chaveSimetricaCifrada = CifrarTexto(chaveSimetrica);	
				
				gravarArq.print(chaveSimetricaCifrada);

				arq.close();
				
				return true;
			}
			
			return true;
			
		}
		catch(Exception e)
		{
			System.out.println("Erro ao gerar chave dos nomes dos arquivos!" + e.getMessage());
			
			return false;
		}
	}
	
	private static String nomeAleatorio (int nCaracteres, int seed) 
	{
		//http://www.guj.com.br/java/59011-como-gerar-letrascharactere-ou-nomesstrings-aleatoriamete-como-um-random
		
	    StringBuffer sb = new StringBuffer();
	    
	    rand.setSeed(seed);
	    //System.out.printf("\nSemente: " + seed);
	    
	    for (int i = 0; i < nCaracteres; i++) 
	    {  
	        int ch = rand.nextInt(letras.length);  
	        sb.append(letras[ch]);  
	    }      
	    
	    return sb.toString();     
	}
	
	
	 */
}
