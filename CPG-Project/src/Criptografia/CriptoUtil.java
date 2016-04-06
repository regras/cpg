package Criptografia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.Base32;


public class CriptoUtil 
{
	private static String appPastaAplicacaoEncript = "PastaAplicacao/ArquivosCifrar/";
	private static String appPastaAplicacaoDecript = "PastaAplicacao/ArquivosDecifrar/";
	
	public static String CriptografarArquivo(File inputFile, File outputFile, String senhaUsuario, String encryptedName) throws Exception 
	{
		try 
		{
			if(inputFile != null && inputFile.isFile())
			{
				//Passos
				//0. Mover arquivo para pasta de trabalho do sistema e comprimí-lo para economizar espaço e manter atributos								
				
		        List<File> list = new ArrayList<File>();
		        
		        //Gerar Hash do arquivo para poder derivar uma chave a partir de seu hash
		        String hash = GerarHashArquivo(inputFile);	
		        
		        list.add(inputFile);
		        
		        ComprimirArquivo(list, appPastaAplicacaoEncript + inputFile.getName() + ".zip", "");
		        
		        
				
				/*
				
				//Passos
				//0. Mover arquivo para pasta de trabalho do sistema								
				FileChannel sourceChannel = null;  
			    FileChannel destinationChannel = null; 
			    
			    sourceChannel = new FileInputStream(inputFile).getChannel();  
			    String novoInputFile = appPastaAplicacaoEncript + inputFile.getName();
		        destinationChannel = new FileOutputStream(novoInputFile).getChannel();  
		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		        
		        if (sourceChannel != null && sourceChannel.isOpen())
		        	sourceChannel.close();  
		        if (destinationChannel != null && destinationChannel.isOpen())
		        	destinationChannel.close(); 
								
				//1. Comprimir arquivo - Economizar espaço e manter atributos
		        List<File> list = new ArrayList<File>();
		        
		        File file = new File(novoInputFile);
		        
		        //Gerar Hash do arquivo para poder derivar uma chave a partir de seu hash
		        String hash = GerarHashArquivo(file);	
		        
		        list.add(file);
		        ComprimirArquivo(list, novoInputFile + ".zip", "");
		        
		        file.delete();
		        
		        */
		        
		        //2. Gerar chave de arquivo
		        //String key = "testeteste123456"; 
		        
		        
		        
		        SecretKey key = GerarChaveSimetricaHashArquivo(hash);
		        
		        File arqZipTemp = new File(appPastaAplicacaoEncript + inputFile.getName() + ".zip");
		        
		        
		        //3. Criptografar nome do arquivo
		        
		        Base32 codec = new Base32();
		        String nomeArquivoEncrypt = "";
		        byte[] nomeArquivoEncryptBytes;
		        
		        nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(inputFile.getName(), key);
	        	nomeArquivoEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
		        
		        String nomeArquivoEncryptFinal = "";
		        
		        if(nomeArquivoEncrypt.length() > 30)
		        	nomeArquivoEncryptFinal = nomeArquivoEncrypt.substring(0, 29);
		        else
		        	nomeArquivoEncryptFinal = nomeArquivoEncrypt;
				
		        String pathArquivoEncrypt = appPastaAplicacaoEncript + nomeArquivoEncryptFinal; 
		        
		        
				FileWriter arqNomeArquivo = new FileWriter(pathArquivoEncrypt + ".config-encripted"); 
				PrintWriter gravarArqNomeArquivo = new PrintWriter(arqNomeArquivo); 
				
				gravarArqNomeArquivo.print(nomeArquivoEncrypt);
				
				arqNomeArquivo.close();
				gravarArqNomeArquivo.close();
				
							
				
				//4. Criptografar arquivo com a chave de arquivo
		        boolean result = CriptografarArquivoAlgSimetrico(key, arqZipTemp, new File(pathArquivoEncrypt + ".encripted"));
		        
		        arqZipTemp.delete();
				
		        if(result)
		        {
		        	//5. Criptografar a chave de arquivo com a chave pública de todos os usuários que tem acesso ao arquivo
					//Gravando chave cifrada em um arquivo
					FileWriter arqChaveArquivo = new FileWriter(pathArquivoEncrypt + ".key"); 
					PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
					
					String keyCifrada = CriptografarChaveSimetrica(key);
					
					gravarArqChaveArquivo.print(keyCifrada);

					arqChaveArquivo.close();
					gravarArqChaveArquivo.close();
					
					//8. Empacotar arquivos (arquivo criptografado, chave de arquivo e arquivo de configurações (contem o nome do arquivo criptografa completo), 
					//assim como o certificado do dono do arquivo para posterior utilização na chacagem da integridade do arquivo.
					
					List<File> listaArquivos = new ArrayList<File>();
					
					File temp1 = new File(pathArquivoEncrypt + ".encripted");
					listaArquivos.add(temp1);
					
					File temp2 = new File(pathArquivoEncrypt + ".key");
					listaArquivos.add(temp2);
					
					File temp3 = new File(pathArquivoEncrypt + ".config-encripted");
					listaArquivos.add(temp3);
								
					EmpacotarArquivos(listaArquivos, outputFile.getParent() + "/" + nomeArquivoEncryptFinal + ".encrypted", "");
					
					temp1.delete();
					temp2.delete();
					temp3.delete();					
					
					//Se houver uma versão anterior do arquivo sendo criptografado, ela deve ser excluída...
					if(encryptedName != null && !encryptedName.isEmpty() && outputFile.exists())
		        	{
			        	outputFile.delete();
		        	}
					
					return nomeArquivoEncrypt;
		        }
			}
			
			return "";
		}
		catch (FileNotFoundException e) 
		{
			throw new Exception("Erro ao criptografar arquivo: " + e.getMessage());
		}
	}
	
	public static String[] DescriptografarArquivo(File inputFile, File outputFile, String senhaUsuario, String decryptedName) throws Exception 
	{
		//Passos
		
		try 
		{
			if(inputFile.isFile())
			{
				String[] retorno = new String[2];
				
				/*
				 
				
				//Passos
				//0. Mover arquivo para pasta de trabalho do sistema
				FileChannel sourceChannel = null;  
			    FileChannel destinationChannel = null; 
			    
			    sourceChannel = new FileInputStream(inputFile).getChannel();  
			    String novoInputFile = appPastaAplicaçãoDecript + inputFile.getName();
		        destinationChannel = new FileOutputStream(novoInputFile).getChannel();  
		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		        
		        if (sourceChannel != null && sourceChannel.isOpen())
		        	sourceChannel.close();  
		        if (destinationChannel != null && destinationChannel.isOpen())
		        	destinationChannel.close(); 
								
				
		        //1. Abrir o pacote e separar os arquivos: arquivo criptografado, a chave de arquivo criptografada, o Hash do arquivo assinado
				//e o certificado do dono do arquivo.
		        
		        File arqCifradoCompactado = new File(novoInputFile);
		        
		        String nomeArquivoComp = arqCifradoCompactado.getName();
		        
		        DesempacotarArquivos(arqCifradoCompactado, appPastaAplicaçãoDecript, "");
		        		        
		        String nomeArquivo = nomeArquivoComp.substring(0, nomeArquivoComp.lastIndexOf("."));
		        
		        arqCifradoCompactado.delete();
		        
		        */
				
				//Passos
				//0. Mover arquivo para pasta de trabalho do sistema. Abrir o pacote e separar os arquivos:
				//arquivo criptografado, a chave de arquivo criptografada, o Hash do arquivo assinado e o certificado do dono do arquivo.
					        
		        String nomeArquivoComp = inputFile.getName();
		        
		        DesempacotarArquivos(inputFile, appPastaAplicacaoDecript, "");
		        		        
		        String nomeArquivo = nomeArquivoComp.substring(0, nomeArquivoComp.lastIndexOf("."));
		        
		        //1. Descriptografar a chave de arquivo com a chave privada do usuário.
	        	String keyFile = appPastaAplicacaoDecript + nomeArquivo + ".key";
	        	FileReader arqChave = new FileReader(keyFile );
				BufferedReader lerArqChave = new BufferedReader(arqChave);
	        	      
	        	String linhaChave = lerArqChave.readLine(); // lê a primeira linha
				// a variável "linha" recebe o valor "null" quando o processo 
				// de repetição atingir o final do arquivo texto
	        	
				StringBuilder chave = new StringBuilder();
				
				while (linhaChave != null) 
				{ 
					chave.append(linhaChave);
					linhaChave = lerArqChave.readLine(); // lê da segunda até a última linha
				} 

				arqChave.close();
				lerArqChave.close();
				
				SecretKey keyDecifrada = (SecretKey) DescriptografarChaveSimetrica(chave.toString(), senhaUsuario);
	        	
				File keyTemp = new File(keyFile);
				keyTemp.delete();
				
				
				
				//2. Decifrar nome do arquivo
				
				String arqConf = appPastaAplicacaoDecript + nomeArquivo + ".config-encripted";
				String nomeDecifrado = "";
				
				Base32 codec = new Base32();
		        
		        FileReader arqNome = new FileReader(arqConf);
				BufferedReader lerArqNome = new BufferedReader(arqNome);
	        	      
	        	String linhaNome = lerArqNome.readLine(); // lê a primeira linha
				// a variável "linha" recebe o valor "null" quando o processo 
				// de repetição atingir o final do arquivo texto
	        	
				StringBuilder nomeTemp = new StringBuilder();
				
				while (linhaNome != null) 
				{ 
					nomeTemp.append(linhaNome);
					linhaNome = lerArqNome.readLine(); // lê da segunda até a última linha
				} 

				arqNome.close();
				lerArqNome.close();
				
				//byte[] secretBytes = new Base32().decode(secret);
				byte[] nomeArquivoEncrypt = codec.decode(nomeTemp.toString().getBytes());
				
				//byte[] nomeArquivoEncrypt = (byte[]) codec.decode(nomeTemp);
				
				retorno[0] = nomeTemp.toString();
				
				if(decryptedName != null && !decryptedName.isEmpty())
				{
					nomeDecifrado = decryptedName;						
				}
				else
				{
					nomeDecifrado = CriptoSimetrica.DesencriptarMensagem(nomeArquivoEncrypt, keyDecifrada);			        							
				}
				
				File nomeTempFile = new File(arqConf);
				nomeTempFile.delete();
		        
		        
		        //3. Descriptografar o arquivo com a chave de arquivo
				File arqCript = new File(appPastaAplicacaoDecript + nomeArquivo + ".encripted");
				File arqDescomp = new File(appPastaAplicacaoDecript + nomeDecifrado + ".zip");
				boolean result = DescriptografarArquivoAlgSimetrico(keyDecifrada, arqCript, arqDescomp);
				
				arqCript.delete();
				
				//4. Descomprimir o arquivo
				if(result)
				{
					DescomprimirArquivo(arqDescomp, outputFile.getParent(), "");
					
					arqDescomp.delete();
				}
				
				retorno[1] = nomeDecifrado;
				
				/*
				
	    		//5. Descomprimir o arquivo.
				
				if(result)
				{
					DescomprimirArquivo(arqDescomp, appPastaAplicaçãoDecript, "");
					
					arqDescomp.delete();
				}
				*/
									
				
				//6. Descriptografar nome do arquivo
				/*					
				String nameCifrado = appPastaAplicaçãoDecript + nomeArquivo + ".config-encripted";
				String nameDescifrado = appPastaAplicaçãoDecript + nomeArquivo + ".config";
				
				File fileNameCifrado = new File(nameCifrado);
				File fileNameDescifrado = new File(nameDescifrado);
				
				DescriptografarArquivoAlgSimetrico(keyDecifrada, fileNameCifrado, fileNameDescifrado);
				
				FileReader arqNome = new FileReader(fileNameDescifrado);
				BufferedReader lerArqNome = new BufferedReader(arqNome);
	        	      
	        	String linha = lerArqNome.readLine(); // lê a primeira linha
				// a variável "linha" recebe o valor "null" quando o processo 
				// de repetição atingir o final do arquivo texto
	        	
				StringBuilder nameFile = new StringBuilder();
				
				while (linha != null) 
				{ 
					nameFile.append(linha);
					linha = lerArqNome.readLine(); // lê da segunda até a última linha
				} 

				arqNome.close();
				lerArqNome.close();
				
				fileNameCifrado.delete();
				fileNameDescifrado.delete();
				
				//JOptionPane.showConfirmDialog(null, "Nome do arquivo: " + nameFile.toString(), "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
				
				*/
				
				/*
				
				//7. Movendo arquivo para o destino
				sourceChannel = null;  
			    destinationChannel = null; 
			    
			    sourceChannel = new FileInputStream(appPastaAplicaçãoDecript + nomeDecifrado).getChannel();  
			    destinationChannel = new FileOutputStream(outputFile.getParent() + "/" + nomeDecifrado).getChannel();  
		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		        
		        if (sourceChannel != null && sourceChannel.isOpen())
		        	sourceChannel.close();  
		        if (destinationChannel != null && destinationChannel.isOpen())
		        	destinationChannel.close(); 
				
		        new File(appPastaAplicaçãoDecript + nomeDecifrado).delete();
		        
		        */
		        
		        return retorno;
		        
							
			}
			
			return null;
		}
		catch (FileNotFoundException e) 
		{
			throw new Exception("Erro ao descriptografar arquivo: " + e.getMessage());
		}
		
	}
	
	public static String CriptografarArquivoComHash(File inputFile, File outputFile, String senhaUsuario, String encryptedName) throws Exception 
	{
		try 
		{
			if(inputFile != null && inputFile.isFile())
			{
				//Passos
				//0. Mover arquivo para pasta de trabalho do sistema e comprimí-lo para economizar espaço e manter atributos								
				
		        List<File> list = new ArrayList<File>();
		        
		        //Gerar Hash do arquivo para poder derivar uma chave a partir de seu hash
		        String hash = GerarHashArquivo(inputFile);	
		        
		        list.add(inputFile);
		        
		        ComprimirArquivo(list, appPastaAplicacaoEncript + inputFile.getName() + ".zip", "");
		        
		        
				
				/*
				
				//Passos
				//0. Mover arquivo para pasta de trabalho do sistema								
				FileChannel sourceChannel = null;  
			    FileChannel destinationChannel = null; 
			    
			    sourceChannel = new FileInputStream(inputFile).getChannel();  
			    String novoInputFile = appPastaAplicacaoEncript + inputFile.getName();
		        destinationChannel = new FileOutputStream(novoInputFile).getChannel();  
		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		        
		        if (sourceChannel != null && sourceChannel.isOpen())
		        	sourceChannel.close();  
		        if (destinationChannel != null && destinationChannel.isOpen())
		        	destinationChannel.close(); 
								
				//1. Comprimir arquivo - Economizar espaço e manter atributos
		        List<File> list = new ArrayList<File>();
		        
		        File file = new File(novoInputFile);
		        
		        //Gerar Hash do arquivo para poder derivar uma chave a partir de seu hash
		        String hash = GerarHashArquivo(file);	
		        
		        list.add(file);
		        ComprimirArquivo(list, novoInputFile + ".zip", "");
		        
		        file.delete();
		        
		        */
		        
		        //2. Gerar chave de arquivo
		        //String key = "testeteste123456"; 
		        
		        
		        
		        SecretKey key = GerarChaveSimetricaHashArquivo(hash);
		        
		        File arqZipTemp = new File(appPastaAplicacaoEncript + inputFile.getName() + ".zip");
		        
		        
		        //3. Criptografar nome do arquivo
		        
		        Base32 codec = new Base32();
		        String nomeArquivoEncrypt = "";
		        byte[] nomeArquivoEncryptBytes;
		        
		        nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(inputFile.getName(), key);
	        	nomeArquivoEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
		        
		        String nomeArquivoEncryptFinal = "";
		        
		        if(nomeArquivoEncrypt.length() > 30)
		        	nomeArquivoEncryptFinal = nomeArquivoEncrypt.substring(0, 29);
		        else
		        	nomeArquivoEncryptFinal = nomeArquivoEncrypt;
				
		        String pathArquivoEncrypt = appPastaAplicacaoEncript + nomeArquivoEncryptFinal; 
		        
		        
				FileWriter arqNomeArquivo = new FileWriter(pathArquivoEncrypt + ".config-encripted"); 
				PrintWriter gravarArqNomeArquivo = new PrintWriter(arqNomeArquivo); 
				
				gravarArqNomeArquivo.print(nomeArquivoEncrypt);
				
				arqNomeArquivo.close();
				gravarArqNomeArquivo.close();
				
							
				
				//4. Criptografar arquivo com a chave de arquivo
		        boolean result = CriptografarArquivoAlgSimetrico(key, arqZipTemp, new File(pathArquivoEncrypt + ".encripted"));
		        
		        arqZipTemp.delete();
				
		        if(result)
		        {
		        	//5. Criptografar a chave de arquivo com a chave pública de todos os usuários que tem acesso ao arquivo
					//Gravando chave cifrada em um arquivo
					FileWriter arqChaveArquivo = new FileWriter(pathArquivoEncrypt + ".key"); 
					PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
					
					String keyCifrada = CriptografarChaveSimetrica(key);
					
					gravarArqChaveArquivo.print(keyCifrada);

					arqChaveArquivo.close();
					gravarArqChaveArquivo.close();
					
		        	//6. Obter o Hash do arquivo para posterior checagem da integridade do mesmo
		        	String hashArqCifrado = GerarHashArquivo(new File(pathArquivoEncrypt + ".encripted"));			        
			        
					//7. Criptografar o Hash com a chave privada do usuário (assinatura)
		        	String hashAssinado = AssinarMensagem(hashArqCifrado, senhaUsuario);
		        	//Gravar hash em arquivo
					FileWriter arq = new FileWriter(pathArquivoEncrypt + ".hash"); 
					PrintWriter gravarArq = new PrintWriter(arq); 
					
					gravarArq.print(hashAssinado);

					arq.close();
					gravarArq.close();
					
					
					//8. Empacotar arquivos (arquivo criptografado, chave de arquivo e o Hash do arquivo assinado, assim como o
					//certificado do dono do arquivo para posterior utilização na chacagem da integridade do arquivo.
					
					List<File> listaArquivos = new ArrayList<File>();
					
					File temp1 = new File(pathArquivoEncrypt + ".encripted");
					listaArquivos.add(temp1);
					
					File temp2 = new File(pathArquivoEncrypt + ".hash");
					listaArquivos.add(temp2);
					
					File temp3 = new File(pathArquivoEncrypt + ".key");
					listaArquivos.add(temp3);
					
					File temp4 = new File(pathArquivoEncrypt + ".config-encripted");
					listaArquivos.add(temp4);
								
					EmpacotarArquivos(listaArquivos, outputFile.getParent() + "/" + nomeArquivoEncryptFinal + ".encrypted", "");
					
					temp1.delete();
					temp2.delete();
					temp3.delete();
					temp4.delete();
					
					
					if(encryptedName != null && !encryptedName.isEmpty() && outputFile.exists())
		        	{
			        	outputFile.delete();
		        	}
					
					return nomeArquivoEncrypt;

		        }
			}
			
			return "";
		}
		catch (FileNotFoundException e) 
		{
			throw new Exception("Erro ao criptografar arquivo: " + e.getMessage());
		}
	}
	
	public static String[] DescriptografarArquivoComHash(File inputFile, File outputFile, String senhaUsuario, String decryptedName) throws Exception 
	{
		//Passos
		
		try 
		{
			if(inputFile.isFile())
			{
				String[] retorno = new String[2];
				
				/*
				 
				
				//Passos
				//0. Mover arquivo para pasta de trabalho do sistema
				FileChannel sourceChannel = null;  
			    FileChannel destinationChannel = null; 
			    
			    sourceChannel = new FileInputStream(inputFile).getChannel();  
			    String novoInputFile = appPastaAplicaçãoDecript + inputFile.getName();
		        destinationChannel = new FileOutputStream(novoInputFile).getChannel();  
		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		        
		        if (sourceChannel != null && sourceChannel.isOpen())
		        	sourceChannel.close();  
		        if (destinationChannel != null && destinationChannel.isOpen())
		        	destinationChannel.close(); 
								
				
		        //1. Abrir o pacote e separar os arquivos: arquivo criptografado, a chave de arquivo criptografada, o Hash do arquivo assinado
				//e o certificado do dono do arquivo.
		        
		        File arqCifradoCompactado = new File(novoInputFile);
		        
		        String nomeArquivoComp = arqCifradoCompactado.getName();
		        
		        DesempacotarArquivos(arqCifradoCompactado, appPastaAplicaçãoDecript, "");
		        		        
		        String nomeArquivo = nomeArquivoComp.substring(0, nomeArquivoComp.lastIndexOf("."));
		        
		        arqCifradoCompactado.delete();
		        
		        */
				
				//Passos
				//0. Mover arquivo para pasta de trabalho do sistema. Abrir o pacote e separar os arquivos:
				//arquivo criptografado, a chave de arquivo criptografada, o Hash do arquivo assinado e o certificado do dono do arquivo.
					        
		        String nomeArquivoComp = inputFile.getName();
		        
		        DesempacotarArquivos(inputFile, appPastaAplicacaoDecript, "");
		        		        
		        String nomeArquivo = nomeArquivoComp.substring(0, nomeArquivoComp.lastIndexOf("."));
		        
		        //2. Checar a integridade do arquivo com a chave pública do dono do arquivo. Se o arquivo foi alterado, 
		        //parar o processo. Caso contrário, continuar.
		        //boolean integridadeOk = false;
		        
		        File arqHashFile = new File(appPastaAplicacaoDecript + nomeArquivo + ".hash");
		        
		        FileReader arqHashAssinado = new FileReader(arqHashFile);
				BufferedReader lerArqHashAssinado = new BufferedReader(arqHashAssinado);
	        	      
	        	String linhaHash = lerArqHashAssinado.readLine(); // lê a primeira linha
				// a variável "linha" recebe o valor "null" quando o processo 
				// de repetição atingir o final do arquivo texto
	        	
				StringBuilder hashAssinado = new StringBuilder();
				
				while (linhaHash != null) 
				{ 
					hashAssinado.append(linhaHash);
					linhaHash = lerArqHashAssinado.readLine(); // lê da segunda até a última linha
				} 

				arqHashAssinado.close();
				lerArqHashAssinado.close();
				
				String hashNovo = GerarHashArquivo(new File(appPastaAplicacaoDecript + nomeArquivo + ".encripted"));
		        
				boolean hashDecifrado = ConferirAssinaturaMensagem(hashNovo, hashAssinado.toString());
		        
				arqHashFile.delete();
		        
		        //if(hashDecifrado == hashNovo)
		        	//integridadeOk = true;
		        
		        if(hashDecifrado)
		        {
		        	//3. Descriptografar a chave de arquivo com a chave privada do usuário.
		        	String keyFile = appPastaAplicacaoDecript + nomeArquivo + ".key";
		        	FileReader arqChave = new FileReader(keyFile );
					BufferedReader lerArqChave = new BufferedReader(arqChave);
		        	      
		        	String linhaChave = lerArqChave.readLine(); // lê a primeira linha
					// a variável "linha" recebe o valor "null" quando o processo 
					// de repetição atingir o final do arquivo texto
		        	
					StringBuilder chave = new StringBuilder();
					
					while (linhaChave != null) 
					{ 
						chave.append(linhaChave);
						linhaChave = lerArqChave.readLine(); // lê da segunda até a última linha
					} 

					arqChave.close();
					lerArqChave.close();
					
					SecretKey keyDecifrada = (SecretKey) DescriptografarChaveSimetrica(chave.toString(), senhaUsuario);
		        	
					File keyTemp = new File(keyFile);
					keyTemp.delete();
					
					
					
					//4. Decifrar nome do arquivo
					
					String arqConf = appPastaAplicacaoDecript + nomeArquivo + ".config-encripted";
					String nomeDecifrado = "";
					
					Base32 codec = new Base32();
			        
			        FileReader arqNome = new FileReader(arqConf);
					BufferedReader lerArqNome = new BufferedReader(arqNome);
		        	      
		        	String linhaNome = lerArqNome.readLine(); // lê a primeira linha
					// a variável "linha" recebe o valor "null" quando o processo 
					// de repetição atingir o final do arquivo texto
		        	
					StringBuilder nomeTemp = new StringBuilder();
					
					while (linhaNome != null) 
					{ 
						nomeTemp.append(linhaNome);
						linhaNome = lerArqNome.readLine(); // lê da segunda até a última linha
					} 

					arqNome.close();
					lerArqNome.close();
					
					//byte[] secretBytes = new Base32().decode(secret);
					byte[] nomeArquivoEncrypt = codec.decode(nomeTemp.toString().getBytes());
					
					//byte[] nomeArquivoEncrypt = (byte[]) codec.decode(nomeTemp);
					
					retorno[0] = nomeTemp.toString();
					
					if(decryptedName != null && !decryptedName.isEmpty())
					{
						nomeDecifrado = decryptedName;						
					}
					else
					{
						nomeDecifrado = CriptoSimetrica.DesencriptarMensagem(nomeArquivoEncrypt, keyDecifrada);			        							
					}
					
					File nomeTempFile = new File(arqConf);
					nomeTempFile.delete();
			        
			        
			        //4. Descriptografar o arquivo com a chave de arquivo
					File arqCript = new File(appPastaAplicacaoDecript + nomeArquivo + ".encripted");
					File arqDescomp = new File(appPastaAplicacaoDecript + nomeDecifrado + ".zip");
					boolean result = DescriptografarArquivoAlgSimetrico(keyDecifrada, arqCript, arqDescomp);
					
					arqCript.delete();
					
					//5. Descomprimir o arquivo
					if(result)
					{
						DescomprimirArquivo(arqDescomp, outputFile.getParent(), "");
						
						arqDescomp.delete();
					}
					
					retorno[1] = nomeDecifrado;
					
					/*
					
		    		//5. Descomprimir o arquivo.
					
					if(result)
					{
						DescomprimirArquivo(arqDescomp, appPastaAplicaçãoDecript, "");
						
						arqDescomp.delete();
					}
					*/
										
					
					//6. Descriptografar nome do arquivo
					/*					
					String nameCifrado = appPastaAplicaçãoDecript + nomeArquivo + ".config-encripted";
					String nameDescifrado = appPastaAplicaçãoDecript + nomeArquivo + ".config";
					
					File fileNameCifrado = new File(nameCifrado);
					File fileNameDescifrado = new File(nameDescifrado);
					
					DescriptografarArquivoAlgSimetrico(keyDecifrada, fileNameCifrado, fileNameDescifrado);
					
					FileReader arqNome = new FileReader(fileNameDescifrado);
					BufferedReader lerArqNome = new BufferedReader(arqNome);
		        	      
		        	String linha = lerArqNome.readLine(); // lê a primeira linha
					// a variável "linha" recebe o valor "null" quando o processo 
					// de repetição atingir o final do arquivo texto
		        	
					StringBuilder nameFile = new StringBuilder();
					
					while (linha != null) 
					{ 
						nameFile.append(linha);
						linha = lerArqNome.readLine(); // lê da segunda até a última linha
					} 

					arqNome.close();
					lerArqNome.close();
					
					fileNameCifrado.delete();
					fileNameDescifrado.delete();
					
					//JOptionPane.showConfirmDialog(null, "Nome do arquivo: " + nameFile.toString(), "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
					
					*/
					
					/*
					
					//7. Movendo arquivo para o destino
					sourceChannel = null;  
				    destinationChannel = null; 
				    
				    sourceChannel = new FileInputStream(appPastaAplicaçãoDecript + nomeDecifrado).getChannel();  
				    destinationChannel = new FileOutputStream(outputFile.getParent() + "/" + nomeDecifrado).getChannel();  
			        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
			        
			        if (sourceChannel != null && sourceChannel.isOpen())
			        	sourceChannel.close();  
			        if (destinationChannel != null && destinationChannel.isOpen())
			        	destinationChannel.close(); 
					
			        new File(appPastaAplicaçãoDecript + nomeDecifrado).delete();
			        
			        */
			        
			        return retorno;
						
		        }
		        else
		        	throw new Exception("Erro: A assinatura do arquivo não confere!");
							
			}
			
			return null;
		}
		catch (FileNotFoundException e) 
		{
			throw new Exception("Erro ao descriptografar arquivo: " + e.getMessage());
		}
		
	}
		
	private static boolean CriptografarArquivoAlgSimetrico(SecretKey key, File inputFile, File outputFile) throws Exception
	{
		try
		{
			CriptoSimetrica.CriptografarArquivo(true, key, inputFile, outputFile);
		}
        catch(Exception ex)
        {
        	throw new Exception("Erro na encriptação do arquivo: " + ex.getMessage());
        }
        
		return true;
	}
	
	private static boolean DescriptografarArquivoAlgSimetrico(SecretKey key, File inputFile, File outputFile) throws Exception
	{
		try
		{
			CriptoSimetrica.CriptografarArquivo(false, key, inputFile, outputFile);
		}
	    catch(Exception ex)
	    {
	    	throw new Exception("Erro na decriptação do arquivo: " + ex.getMessage());
	    }
		
		return true;
	}
	
	public static String CriptografarMensagemAlgAssimetrico(String textoClaro) throws Exception
	{
		try
		{
			CriptoAssimetrica rsaCripto = new CriptoAssimetrica();
			
			if(rsaCripto.ChecarExistenciaDasChaves())
				return rsaCripto.CifrarTexto(textoClaro);
				
			return null;
			
		}
	    catch(Exception ex)
	    {
	    	throw new Exception("Erro na encriptação assimétrica da mensagem: " + ex.getMessage());
	    }		
	}
	
	public static String DescriptografarMensagemAlgAssimetrico(String textoCifrado, String password) throws Exception
	{
		try
		{
			CriptoAssimetrica rsaCripto = new CriptoAssimetrica();
			
			if(rsaCripto.ChecarExistenciaDasChaves())
				return rsaCripto.DecifrarTexto(textoCifrado, password);
			
			return null;
			
		}
	    catch(Exception ex)
	    {
	    	throw new Exception("Erro na decriptação assimétrica da mensagem: " + ex.getMessage());
	    }	
	}
	
	public static String CriptografarChaveSimetrica(SecretKey chave) throws Exception
	{
		try
		{
			CriptoAssimetrica rsaCripto = new CriptoAssimetrica();
			
			if(rsaCripto.ChecarExistenciaDasChaves())
				return rsaCripto.CifrarChave(chave);
				
			return null;
			
		}
	    catch(Exception ex)
	    {
	    	throw new Exception("Erro na encriptação da chave simétrica: " + ex.getMessage());
	    }		
	}
	
	public static Key DescriptografarChaveSimetrica(String chaveCifrada, String password) throws Exception
	{
		try
		{
			CriptoAssimetrica rsaCripto = new CriptoAssimetrica();
			
			if(rsaCripto.ChecarExistenciaDasChaves())
				return rsaCripto.DecifrarChave(chaveCifrada, password);
			
			return null;
			
		}
	    catch(Exception ex)
	    {
	    	throw new Exception("Erro na decriptação da chave simétrica: " + ex.getMessage());
	    }	
	}
	
	private static String AssinarMensagem(String mensagem, String senhaUsuario) throws Exception
	{
		try
		{
			CriptoAssimetrica rsaCripto = new CriptoAssimetrica();
			if(rsaCripto.ChecarExistenciaDasChaves())
				return rsaCripto.AssinarMensagem(mensagem, senhaUsuario);
			else
				return null;
			
		}
	    catch(Exception ex)
	    {
	    	throw new Exception("Erro ao assinar mensagem: " + ex.getMessage());
	    }		
	}
	
	private static Boolean ConferirAssinaturaMensagem(String mensagem, String mensagemAssinada) throws Exception
	{
		try
		{
			CriptoAssimetrica rsaCripto = new CriptoAssimetrica();
			if(rsaCripto.ChecarExistenciaDasChaves())
				return rsaCripto.ConferirAssinaturaMensagem(mensagem, mensagemAssinada);
			
			return false;
			
		}
	    catch(Exception ex)
	    {
	    	throw new Exception("Erro na verificação da assinatura da mensagem: " + ex.getMessage());
	    }		
	}
	
	private static SecretKey GerarChaveSimetrica() throws Exception
	{
		try 
    	{
			return CriptoSimetrica.GerarChaveSimetrica();
		} 
    	catch (Exception e) 
    	{
			throw new Exception("Erro ao chave simétrica: " + e.getMessage());
		}
	}
	
	public static SecretKey GerarChaveSimetricaHashArquivo(String hashArquivo) throws Exception
	{
		try 
    	{
			return CriptoSimetrica.GerarChaveSimetricaHashArquivo(hashArquivo);
		} 
    	catch (Exception e) 
    	{
			throw new Exception("Erro ao chave simétrica a partir do hash do arquivo: " + e.getMessage());
		}
	}
	
	public static String GerarHashArquivo(File inputFile) throws Exception
	{
		try
		{
			return HashGeneratorUtils.generateSHA256(inputFile);
		}
	    catch(Exception ex)
	    {
	    	throw new Exception("Erro na geração do Hash do arquivo: " + ex.getMessage());
	    }
	}
	
	private static boolean ComprimirArquivo(List<File> listaArquivos, String output, String password) throws Exception
	{
		try 
		{
			CompressaoArquivos.ComprimirArquivos(listaArquivos, output, password);
		} 
		catch (Exception e) 
		{
			throw new Exception("Erro ao comprimir arquivo: " + e.getMessage());
		}
		
		return true;
	}
	
	private static boolean DescomprimirArquivo(File arquivoCompactado, String caminhoExtracao, String password) throws Exception
	{
		try 
		{
			CompressaoArquivos.DescomprimirArquivos(arquivoCompactado, caminhoExtracao, password);
		} 
		catch (Exception e) 
		{
			throw new Exception("Erro ao descomprimir arquivo: " + e.getMessage());
		}
		
		return true;
	}
	
	private static boolean EmpacotarArquivos(List<File> listaArquivos, String output, String password) throws Exception
	{
		try 
		{
			CompressaoArquivos.EmpacotarArquivos(listaArquivos, output, password);
		} 
		catch (Exception e) 
		{
			throw new Exception("Erro ao empacotar arquivo(s): " + e.getMessage());
		}
		
		return true;
	}
	
	private static boolean DesempacotarArquivos(File arquivoEmpacotado, String caminhoExtracao, String password) throws Exception
	{
		try 
		{
			CompressaoArquivos.DescomprimirArquivos(arquivoEmpacotado, caminhoExtracao, password);
		} 
		catch (Exception e) 
		{
			throw new Exception("Erro ao desempacotar arquivo(s): " + e.getMessage());
		}
		
		return true;
	}	
}

class CryptoException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public CryptoException(){}
 
    public CryptoException(String message, Throwable throwable) 
    {
        super(message, throwable);
    }
}
