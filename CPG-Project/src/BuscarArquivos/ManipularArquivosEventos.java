package BuscarArquivos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base32;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import ClassesGerais.Arquivos;
import ClassesGerais.DiretoriosDosArquivos;
import Criptografia.CriptoSimetrica;
import Criptografia.CriptoUtil;
import Criptografia.HashGeneratorUtils;

public class ManipularArquivosEventos 
{
private static Arquivos arq = new Arquivos();
	
	private static String appPastaAplicacao = "/home/vitormoia/workspace/ProjetoCNC/Testes/PastaAplicacao/";
	
	private static List<Arquivos> arquivosCifrarNovos = new ArrayList<Arquivos>();
	//private static List<Arquivos> arquivosCifrarExistentes = new ArrayList<Arquivos>();
	
	private static Arquivos AdicionarArquivosDiretorioParaLista(String dirArquivos) throws Exception
	{
		Arquivos arquivosDirUserList = new Arquivos();
		
		try
		{
			File dirArquivosUser = new File(dirArquivos);
			//File DirArquivosNuvem = new File(DirNuvem);
			
			File arquivosDirUser[]  = dirArquivosUser.listFiles();
			//File arquivosDirNuvem[]  = dirArquivosUser.listFiles();
			
			//Adicionar arquivos na lista
			for(int i = 0; i< arquivosDirUser.length; i++) 
			{
				File each = arquivosDirUser[i];
				
				if(each != null)  
				{
					Date lastModified = new Date(each.lastModified());
					
					Date dataInsercaoRegistro = new Date();
					
					//arquivosDirUserList.AdicionarNovoRegistro(each.getName(), each.getAbsolutePath(), lastModified, each.isDirectory(), each.getAbsolutePath().substring(0, each.getAbsolutePath().indexOf(each.getName())), null, "", "", true, dataInsercaoRegistro, null);					
				}
			}
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao adicionar registros a lista de arquivos locais! Erro: " + ex.getMessage());
		}
		
		return arquivosDirUserList;
	}
		
	public static void VerificarNovosArquivos(String DirArquivosSecretosUser, String DirNuvem, String DirArquivoConfigXml, String DirArquivoConfigXmlEncrypted, String senhaUsuario) throws Exception
	{
		try
		{
			boolean arquivoConfigAlterado = false;
			
			
			//1. Sincronizando arquivos da pasta da aplicação local com o arquivo de config. local
			
			List<Arquivos> arquivosLocais = new ArrayList<Arquivos>();
			//List<Arquivos> arquivosLocaisAntigos = new ArrayList<Arquivos>();
			//Lista de caminhos dos diretórios que devem ter seus arquivos criptografados e enviados para a pasta da nuvem
        	List<DiretoriosDosArquivos> arquivosAddOutrosDir = new ArrayList<DiretoriosDosArquivos>();
			
			//1.1 Lendo arquivos da pasta local
			Arquivos arquivosDirUserList = AdicionarArquivosDiretorioParaLista(DirArquivosSecretosUser);
			
			//1.2 Lendo arquivos que estão no arquivo de configuração local e obtendo a última hora/data de modificação
			Date dataUltModArquivoConfLocal = Leitura(DirArquivoConfigXml, true);
			
			//1.3 Comparando arquivos locais com os lançados no arquivo de configuração local
			
			arquivosLocais.addAll(arquivosDirUserList.arquivosList);
			
			if(arq != null && arq.arquivosList != null && arq.arquivosList.size() > 0)
			{
				for(int i = 0; i< arquivosDirUserList.arquivosList.size(); i++)
				{
					String path1 =arquivosDirUserList.arquivosList.get(i).pathArquivo;
					
					for(int j = 0; j< arq.arquivosList.size(); j++)
					{
						String path2 = arq.arquivosList.get(j).pathArquivo;
						
						if(path1.equals(path2))
						{
							int index= -1;
							
							for(int m=0;m<arquivosLocais.size();m++)
							{
								if(arquivosLocais.get(m).pathArquivo.equals(path1))
								{
									index = m;
									break;
								}
							}
							
							if(index >= 0)
							{
								if(arquivosDirUserList.arquivosList.get(i).diretorio)
								{
									String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
									
									String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
									
									String novoPathArqConf  = DirArquivoConfigXml.substring(0, DirArquivoConfigXml.lastIndexOf('/') + 1) + arquivosDirUserList.arquivosList.get(i).nomeArquivo + "/config.xml";
									
									//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
					            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
					            	//em uma pasta equivalemte na pasta da nuvem
					            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
					            	
					            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivosDirUserList.arquivosList.get(i).nomeArquivo + "/";
					            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
					            	arqDir.DirArquivoConfig = novoPathArqConf;
					            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
					            	
					            	arquivosAddOutrosDir.add(arqDir);
					            	
								}
								
								//Verifica a última data/horário de modificação do arquivo. Se for o mesmo, o arquivo não precisa ser encriptado
								//Se a data do arquivo da pasta do usuário for mais recente que a data do arquivo cifrado na nuvem, então ele precisa ser encriptado
								if(arquivosDirUserList.arquivosList.get(i).dataUltimaMod.compareTo(arq.arquivosList.get(j).dataUltimaMod) <= 0 || arquivosDirUserList.arquivosList.get(i).diretorio)
								{
									arquivosLocais.get(index).dataUltimaModArqEncrypt = arq.arquivosList.get(j).dataUltimaModArqEncrypt;
									arquivosLocais.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
									arquivosLocais.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
									//arquivosLocaisAntigos.add(arquivosLocaisNovos.get(index));										
									//arquivosLocaisNovos.remove(index);
									//i--;
								}
								else
								{
									arquivosLocais.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
									arquivosLocais.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
								}
							}
															
							arq.arquivosList.remove(j);
							j--;
							
							break;
						}							
					}
				}
			}
			
				
			
			//1.4 Separando os arquivos que não estão lançados no arquivo de configuração local mas estão na pasta da nuvem - precisar ser criptografados
			
			//Já estão presentes em --> arquivosLocaisNovos
			
			//1.5 Separando os arquivos que estão lançados no arquivo de configuração local mas não estão na pasta da nuvem - ????????? DELETAR ou CONFERIR COM A NUVEM??
			if(arq.arquivosList != null && arq.arquivosList.size() > 0)
			{
				
			}
			
			//2. Comparar arquivos que devem ser cifrados com o arquivo de configuração presente na nuvem
			
			//2.1 Decifrar arquivo de configuração presente na nuvem
			
			File arquivoConfigEncrypted = new File(DirArquivoConfigXmlEncrypted);
			String pathArquivoConfigTemp = "";
			
			int ind1 = arquivoConfigEncrypted.getName().lastIndexOf(".encrypted");
			
			if(ind1 > 0)
				pathArquivoConfigTemp = appPastaAplicacao + arquivoConfigEncrypted.getName().substring(0, ind1);
			else
				pathArquivoConfigTemp = appPastaAplicacao + arquivoConfigEncrypted.getName();
			
			new File(pathArquivoConfigTemp).delete();
			
			if(arquivoConfigEncrypted.exists())
			{
				//Decifrar arquivo para a pasta interna da aplicação
				CriptoUtil.DescriptografarArquivo(arquivoConfigEncrypted, new File(pathArquivoConfigTemp), senhaUsuario, arquivoConfigEncrypted.getName());
			}
			
			//2.2 Realizando leitura do arquivo de configuração da nuvem
			Date dataUltModArquivoConfNuvem = Leitura(pathArquivoConfigTemp, true);
			
			
			//2.3 Comparando os arquivos presentes em ambas as listas...
			//Lista de arquivos locais --> arquivosLocaisNovos
			//Lista de arquivos presentes na nuvem --> arq.arquivosList
			
			//Arquivos a serem criptografados e enviados para a pasta da nuvem
        	List<Arquivos> arquivosCifrar = new ArrayList<Arquivos>();
        	List<Arquivos> arquivosDecifrar = new ArrayList<Arquivos>();
        	List<Arquivos> arquivosAntigos = new ArrayList<Arquivos>();
        	
        	if(arq != null && arq.arquivosList != null && arq.arquivosList.size() > 0)
			{
				if(arquivosLocais != null && arquivosLocais.size() > 0)
				{
					//arquivosCifrar.addAll(arquivosLocais);
										
					for(int i = 0; i< arquivosLocais.size(); i++)
					{
						String path1 =arquivosLocais.get(i).pathArquivo;
						
						for(int j = 0; j< arq.arquivosList.size(); j++)
						{
							String path2 = arq.arquivosList.get(j).pathArquivo;
							
							if(path1.equals(path2))
							{
								/*
								int compare = arq.arquivosList.get(j).dataInsercaoRegistro.compareTo(arquivosLocais.get(i).dataInsercaoRegistro);
								
								if(compare < 0)
								{
									//Local é mais recente - Atualizar nuvem
									arquivosCifrar.add(arquivosLocais.get(i));
								}
								else if(compare > 0)
								{
									//Nuvem é mais recente - atualizar local
									arquivosDecifrar.add(arq.arquivosList.get(j));
								}
								else
								{
									arquivosAntigos.add(arq.arquivosList.get(j));
									//Data iguais - manter
								}								
								*/
								if(arq.arquivosList.get(j).diretorio)
								{
									String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
									
									String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
									
									String novoPathArqConf  = DirArquivoConfigXml.substring(0, DirArquivoConfigXml.lastIndexOf('/') + 1) + arq.arquivosList.get(j).nomeArquivo + "/config.xml";
									
									//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
					            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
					            	//em uma pasta equivalemte na pasta da nuvem
					            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
					            	
					            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arq.arquivosList.get(j).nomeArquivo + "/";
					            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
					            	arqDir.DirArquivoConfig = novoPathArqConf;
					            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
					            	
					            	arquivosAddOutrosDir.add(arqDir);
					            	
								}
																
								arq.arquivosList.remove(j);
								j--;
								
								arquivosLocais.remove(i);
								i--;
								
								break;
							}							
						}
					}
				}
				else
				{
					arquivosDecifrar.addAll(arq.arquivosList);
				}
			}
        	else if(arquivosLocais != null && arquivosLocais.size() > 0)
        		arquivosCifrar.addAll(arquivosLocais);
			
        	//2.3.1 Verificando os arquivos locais que não estão na nuvem
        	
        	int compareDates = dataUltModArquivoConfNuvem.compareTo(dataUltModArquivoConfLocal);
        	
			if(compareDates < 0)
			{
				//Local é mais recente - Atualizar nuvem
				
				//Existem arquivos na nuvem que não estão presentes localmente.
				//Como a data do arquivo de configuração local é mais recente, deletar os arquivos cifrados da nuvem
				
				if(arq != null && arq.arquivosList != null && arq.arquivosList.size() > 0)
				{
					for(int i=0; i < arq.arquivosList.size(); i++)
					{
						File tempFile = new File(DirNuvem + arq.arquivosList.get(i).nomeArquivoEncrypt);
	        			
	    				if(tempFile != null && tempFile.exists());
	    				{
	    					RemoverArquivos(tempFile);
	    					/*
	    					if(tempFile.listFiles() != null)
	    						for(File e:tempFile.listFiles())
	    						{
	    							if(e != null && e.exists())
	    								RemoverArquivos(e);
	    						}*/
	    				}        					
	    				
	    				tempFile.delete();
	    				
	    				//Deletando o arquivo de configuração pertinente ao diretório sendo deletado
	    				
	    				/*
	    				
	    				int indArquivo = arq.arquivosList.get(i).pathArqConfig.lastIndexOf('/');
	    				String arquivo = "";
	    				
	    				if(indArquivo > 0)
	    					arquivo = arq.arquivosList.get(i).pathArqConfig.substring(0, indArquivo);
	    				else
	    					arquivo = arq.arquivosList.get(i).pathArqConfig;
	    				
	    				File tempFileConfig = new File(arquivo);     
	    				
	    				if(tempFileConfig != null && tempFileConfig.exists())
	    					RemoverArquivos(tempFileConfig);
	    					
	    					*/
					}					
				}
				
				if(arquivosLocais != null)
					arquivosCifrar.addAll(arquivosLocais);
				
			}
			else if(compareDates > 0)
			{
				//Nuvem é mais recente - atualizar local
				
				if(arq != null && arq.arquivosList != null && arq.arquivosList.size() > 0)
				{
					//Existem arquivos na nuvem que não estão presentes localmente. 
					//Como a data do arquivo de configuração da nuvem é mais recente, deletar os arquivos em claro locais
					
					arquivosDecifrar.addAll(arq.arquivosList);
				}
				
				if(arquivosLocais != null && arquivosLocais.size() > 0)
				{
					for(int i=0; i < arquivosLocais.size(); i++)
					{
						File tempFile = new File(DirArquivosSecretosUser + arquivosLocais.get(i).nomeArquivo);
	        			
	    				if(tempFile != null && tempFile.exists());
	    				{
	    					RemoverArquivos(tempFile);
	    					/*
	    					if(tempFile.listFiles() != null)
	    						for(File e:tempFile.listFiles())
	    						{
	    							if(e != null && e.exists())
	    								RemoverArquivos(e);
	    						}*/
	    				}        					
	    				
	    				tempFile.delete();
	    				
	    				//Deletando o arquivo de configuração pertinente ao diretório sendo deletado
	    				
	    				/*
	    				
	    				int indArquivo = arq.arquivosList.get(i).pathArqConfig.lastIndexOf('/');
	    				String arquivo = "";
	    				
	    				if(indArquivo > 0)
	    					arquivo = arq.arquivosList.get(i).pathArqConfig.substring(0, indArquivo);
	    				else
	    					arquivo = arq.arquivosList.get(i).pathArqConfig;
	    				
	    				File tempFileConfig = new File(arquivo);     
	    				
	    				if(tempFileConfig != null && tempFileConfig.exists())
	    					RemoverArquivos(tempFileConfig);
	    					
	    					*/
					}					
				}
			}
			else
			{
				//Data iguais - manter
				
				//Não deveria ser a mesma e possuir listas de arquivos diferentes
				
				//??????
				
			}	
        	
        	
        	//2.3.2 Verificando os arquivos da nuvem que não estão presentes localmente
        	
			
			
			//3. Criando o arquivo de configuração atualizado, cifrando e decifrando os arquivos
			
			File arquivoConfig = new File(DirArquivoConfigXml);
        	
        	if(!arquivoConfig.exists())
        	{
        		try 
				{
        			arquivoConfigAlterado = true;
        			
        			new File(arquivoConfig.getParent()).mkdirs();
        			
        			arquivoConfig.createNewFile();
        			        			
        			FileWriter fw = new FileWriter(arquivoConfig, false);
	    			Element arquivos = new Element("Arquivos");
	    			
	    			//Define Agenda como root
	    	        Document documento = new Document(arquivos);
	    	        
	    	        //Classe responsável para imprimir / gerar o XML
			        XMLOutputter xout = new XMLOutputter();

		            //Imprimindo o XML no arquivo
		            xout.output(documento, fw);
					
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
        	}
        	
        	//3.1. Lançando os arquivos antigos que não sofreram modificações
        	if(arquivosAntigos != null && arquivosAntigos.size() > 0)
        	{
        		
        	}
        	
			//3.1. Criptografar arquivos e enviá-los para a nuvem
        	if(arquivosCifrar != null && arquivosCifrar.size() > 0)
        	{
        		
        	}	
        		
			//3.2. Decifrar arquivos da nuvem
        	if(arquivosDecifrar != null && arquivosDecifrar.size() > 0)
        	{
        		
        	}
			/*
        	//Os arquivos são criptogrados e armazenados na pasta da nuvem 
        	if(novosArquivos != null && novosArquivos.size() > 0)
	        {
        		//Escrevendo no arquivo de configuração os dados dos arquivos 
        		FileWriter fw = new FileWriter(arquivoConfig, false);
    			Element arquivos = new Element("Arquivos");
    			
    			arquivoConfigAlterado = true;
    			
    			//Define Arquivos como root
    	        Document documento = new Document(arquivos);
    	                 
    	        //contador necessário para controlar o ID. 
    	        //Aqui são escritos no arquivo de configuração os arquivos que já se encontram cifrados na pasta da nuvem
    	        //O contador será utilizado no passo seguinte, onde os novos arquivos a serem cifrados devem continuar do número parado neste loop
	            int contId = 0;
    	        
	            //Escrevendo no arquivo de configuração os dados dos arquivos que já estão cifrados e armazenados na pasta da nuvem
    	        if(antigosArquivos != null && antigosArquivos.size() > 0)
    	        {
    	        	for(int i=0; i < antigosArquivos.size(); i++)
    	        	{
    	        		//Cria o elemento Arquivo
    		            Element arq = new Element("Arquivo");
    		            
    	        		//Adiciona o atributo id ao arquivo
    		            arq.setAttribute("id",Integer.toString(i));
    		            contId++;
    		
    		            //Criando os elementos do arquivo    		
    		            Element nomeArquivo = new Element("nomeArquivo");
    		            nomeArquivo.setText(antigosArquivos.get(i).nomeArquivo);
    		
    		            Element pathArquivo = new Element("path");
    		            pathArquivo.setText(antigosArquivos.get(i).pathArquivo);
    		    		               		            
    		            Element ultimaModificacao = new Element("ultimaModificacao");
    		            ultimaModificacao.setText(antigosArquivos.get(i).dataUltimaMod.toString());
    		            
    		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
    		            ultimaModificacaoArqEncrypt.setText(antigosArquivos.get(i).dataUltimaModArqEncrypt.toString());
    		
    		            Element diretorio = new Element("diretorio");
    		            diretorio.setText(antigosArquivos.get(i).diretorio == true ? "true" : "false");
    		            
    		            
    		            
    		            //Confirmar
    		            
    		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
    		            nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.toString());
    		            
    		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
    		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.toString());
    		            
    		            //
    		            
    		            //Adicionando elementos no arquivo    		
    		            arq.addContent(nomeArquivo);    		
    		            arq.addContent(pathArquivo);    		
    		            arq.addContent(ultimaModificacao);
    		            arq.addContent(ultimaModificacaoArqEncrypt);
    		            
    		            if(!antigosArquivos.get(i).diretorio)
    		            	diretorio.setAttribute("pathArqConfig", "");        			            
    		            else
    		            	diretorio.setAttribute("pathArqConfig", antigosArquivos.get(i).pathArqConfig);        		            	
    		                		
    		            arq.addContent(diretorio);
    		            arq.addContent(nomeArquivoEncrypt);
    		            arq.addContent(pathArquivoEncrypt);
    		
    		            //Adicionado o arquivo a Arquivos    		
    		            arquivos.addContent(arq);
    	        	}
    	        }
    	        
    	        //Criptografando os arquivo para que sejam armazenados na pasta da nuvem
    	        for(int i = 0; i < novosArquivos.size(); i++)
		        {
    	        	//Escrevendo os dados dos novos arquivos no arquivo de configuração
    	        	
		        	//Cria o elemento Arquivo
		            Element arq = new Element("Arquivo");
		            
		        	//Adiciona o atributo id ao arquivo
		            arq.setAttribute("id",Integer.toString(i + contId));
		
		            //Criando os elementos do arquivo		
		            Element nomeArquivo = new Element("nomeArquivo");
		            nomeArquivo.setText(novosArquivos.get(i).nomeArquivo);
		
		            Element pathArquivo = new Element("path");
		            pathArquivo.setText(novosArquivos.get(i).pathArquivo);
		        
		            Element ultimaModificacao = new Element("ultimaModificacao");
		            ultimaModificacao.setText(novosArquivos.get(i).dataUltimaMod.toString());
		
		            Element diretorio = new Element("diretorio");
		            diretorio.setText(novosArquivos.get(i).diretorio == true ? "true" : "false");
		            
		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
		            		
		            //Adicionando elementos no arquivo		
		            arq.addContent(nomeArquivo);		
		            arq.addContent(pathArquivo);		
		            arq.addContent(ultimaModificacao);
		            
		            
		            
		          //Confirmar
		            
		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
		            
		            
		            
		            String nomeArEncrypt = "";
		            
		            
		            //
		            
		            
		            if(!novosArquivos.get(i).diretorio)
		            {
		            	diretorio.setAttribute("pathArqConfig", "");
		            	
		            	//Criptografando o arquivo
		            	if(novosArquivos.get(i).nomeArquivoEncrypt != null && !novosArquivos.get(i).nomeArquivoEncrypt.isEmpty())
		            		nomeArEncrypt = CriptoUtil.CriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirNuvem + novosArquivos.get(i).nomeArquivo), senhaUsuario, novosArquivos.get(i).nomeArquivoEncrypt);
		            	else
		            		nomeArEncrypt = CriptoUtil.CriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirNuvem + novosArquivos.get(i).nomeArquivo), senhaUsuario, null);
		            	
		            	if(nomeArEncrypt != null && !nomeArEncrypt.isEmpty())
		            	{
		            		Date lastModified = new Date(new File(DirNuvem + nomeArEncrypt + ".encrypted").lastModified());
			            	
			            	ultimaModificacaoArqEncrypt.setText(lastModified.toString());
		            	}
		            	else
		            		throw new Exception("Erro ao cifrar arquivo! ");
		            }
		            else
		            {
		            	//Criptografando os diretórios - Na verdade é criado um novo diretório na pasta da nuvem e posteriormente inserido os 
		            	//arquivos nele, todos criptografados.
		            	
		            	//Criptografando o nome do diretório
		            	
		            	File pastatemp;
		            	
		            	if(novosArquivos.get(i).nomeArquivoEncrypt != null && !novosArquivos.get(i).nomeArquivoEncrypt.isEmpty())
	            		{
	            			nomeArEncrypt = novosArquivos.get(i).nomeArquivoEncrypt;
	            			
	            			pastatemp = new File(DirNuvem + nomeArEncrypt);
			            	
			            	if(!pastatemp.exists())
		            		{
			            		String hashNome = HashGeneratorUtils.generateSHA256(novosArquivos.get(i).nomeArquivo.toString());
				            	
			            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
			            	
			            		Base32 codec = new Base32();
							
			            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(novosArquivos.get(i).nomeArquivo.toString(), key);
			            		String nomePastaEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
					        
			            		if(nomePastaEncrypt.length() > 30)
			            			nomeArEncrypt = nomePastaEncrypt.substring(0, 29);
			            		else
			            			nomeArEncrypt = nomePastaEncrypt;
			            		
			            		pastatemp = new File(DirNuvem + nomeArEncrypt);
			            		
			            		if(!pastatemp.exists())
			            			pastatemp.mkdir();
			            		
			            		FileWriter arqChaveArquivo = new FileWriter(DirNuvem + nomeArEncrypt + "/keyFolder.encrypted"); 
								PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
								
								String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
								
								gravarArqChaveArquivo.println("chave="+keyCifrada);
								gravarArqChaveArquivo.println("nomePastaEncrypt="+nomePastaEncrypt);
								
								arqChaveArquivo.close();
								gravarArqChaveArquivo.close();
		            		}
			            	
		            	}
		            	else
		            	{
		            		String hashNome = HashGeneratorUtils.generateSHA256(novosArquivos.get(i).nomeArquivo.toString());
		            	
		            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
		            	
		            		Base32 codec = new Base32();
						
		            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(novosArquivos.get(i).nomeArquivo.toString(), key);
		            		String nomePastaEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
				        
		            		if(nomePastaEncrypt.length() > 30)
		            			nomeArEncrypt = nomePastaEncrypt.substring(0, 29);
		            		else
		            			nomeArEncrypt = nomePastaEncrypt;
		            		
		            		pastatemp = new File(DirNuvem + nomeArEncrypt);
			            	
			            	if(!pastatemp.exists())
			            		pastatemp.mkdir();
			            	
			            	FileWriter arqChaveArquivo = new FileWriter(DirNuvem + nomeArEncrypt + "/keyFolder.encrypted"); 
							PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
							
							String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
							
							gravarArqChaveArquivo.println("chave="+keyCifrada);
							gravarArqChaveArquivo.println("nomePastaEncrypt="+nomePastaEncrypt);
							
							arqChaveArquivo.close();
							gravarArqChaveArquivo.close();
		            	}
		            	
		            	
		            			            	
		            	Date lastModified = new Date(pastatemp.lastModified());
		            	ultimaModificacaoArqEncrypt.setText(lastModified.toString());
		            	
		            	
		            	
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo.replace(' ', '-') + "-config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt +"/config.xml";
		            	String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt + "-config.xml";
		            	
		            	//Config-files/config.xml
		            			            	
		            	//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
		            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
		            	//em uma pasta equivalemte na pasta da nuvem
		            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
		            	
		            	
		            	String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo + "/config.xml";
		            	
		            	diretorio.setAttribute("pathArqConfig", novoPathArqConf);
		            	
		            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + novosArquivos.get(i).nomeArquivo + "/";
		            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
		            	arqDir.DirArquivoConfig = novoPathArqConf;
		            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
		            			            			            	
		            	arquivosAddOutrosDir.add(arqDir);
		            }	            
		            
		            arq.addContent(ultimaModificacaoArqEncrypt);
		            arq.addContent(diretorio);
		            
		            
		            //CONFIRMAR
		            
		            pathArquivoEncrypt.setText(DirNuvem + nomeArEncrypt);
		            nomeArquivoEncrypt.setText(nomeArEncrypt);
		            
		            
		            
		            arq.addContent(nomeArquivoEncrypt);
		            arq.addContent(pathArquivoEncrypt);
		            
		            
		            //
		            
		            
		            //Adicionado o arquivo a Arquivos		
		            arquivos.addContent(arq);		            
		        }
		        
		        //Classe responsável para imprimir / gerar o XML
		        XMLOutputter xout = new XMLOutputter();

	            //Imprimindo o XML no arquivo
	            xout.output(documento, fw);
	        }
        	*/
        	
        	//5. Atualizar arquivo na nuvem (o arquivo de configuração deve ser cifrado antes de ser enviado para a nuvem
        	if(arquivoConfigAlterado)
        	{
        		File arquivoConfigEncryptedNovo = new File(DirArquivoConfigXmlEncrypted);
        		
        		//Criptografar arquivo de configurações e enviá-lo para a pasta da nuvem...
        		CriptoUtil.CriptografarArquivo(arquivoConfig, arquivoConfigEncryptedNovo, senhaUsuario, arquivoConfig.getName());
        	}
        	
		}
		catch(Exception ex)
		{
			throw new Exception("Erro: " + ex.getMessage());
		}	
        	
			
			
			
			
			
	}
	
	public static void Escrita(List<Arquivos> arquivoAdicionar, String pathArquivoConfig, String pathArquivoConfigEncrypted, String DirArquivosSecretosUser, String DirNuvem, String senhaUsuario) throws Exception
	{
		try 
        {
			boolean arquivoConfigAlterado = false;
			
			String pathArquivoConfigTemp = AtualizarArquivoConfig(pathArquivoConfig, pathArquivoConfigEncrypted, senhaUsuario);
			
        	File arquivoConfig = new File(pathArquivoConfigTemp);
			
			//Arquivos a serem criptografados e enviados para a pasta da nuvem
        	List<Arquivos> novosArquivos = new ArrayList<Arquivos>();
        	
        	//Arquivos que já foram criptografados e se encontram na pasta da nuvem
        	List<Arquivos> antigosArquivos = new ArrayList<Arquivos>();
        	
        	//Lista de caminhos dos diretórios que devem ter seus arquivos criptografados e enviados para a pasta da nuvem
        	List<DiretoriosDosArquivos> arquivosAddOutrosDir = new ArrayList<DiretoriosDosArquivos>();
        	        	
        	if(!arquivoConfig.exists())
        	{
        		try 
				{
        			arquivoConfigAlterado = true;
        			
        			new File(arquivoConfig.getParent()).mkdirs();
        			
        			arquivoConfig.createNewFile();
        			        			
        			FileWriter fw = new FileWriter(arquivoConfig, false);
	    			Element arquivos = new Element("Arquivos");
	    			
	    			//Define Agenda como root
	    	        Document documento = new Document(arquivos);
	    	        
	    	        //Classe responsável para imprimir / gerar o XML
			        XMLOutputter xout = new XMLOutputter();

		            //Imprimindo o XML no arquivo
		            xout.output(documento, fw);   
					
					novosArquivos.addAll(arquivoAdicionar);
					antigosArquivos = null;
				} 
				catch (IOException e) 
				{
					novosArquivos = null;
					antigosArquivos = null;
					
					e.printStackTrace();
				}
        	}
        	else
        	{
        		try 
        		{
        			//Faz a leitura dos arquivos presentes no arquivo de configuração e armazena suas infor. em "arq"
					Leitura(pathArquivoConfigTemp, true);
					
					//Adiciona todos arquivos presentes no diretório do usuário em uma nova lista para manipulação
					novosArquivos.addAll(arquivoAdicionar);
										
					//Verifica quais são os arquivos que presentes no diretório do usuário que não foram criptografados e armazenados na pasta da nuvem
					for(int i = 0; i< arquivoAdicionar.size(); i++)
					{
						String path1 =arquivoAdicionar.get(i).pathArquivo;
						
						for(int j = 0; j< arq.arquivosList.size(); j++)
						{
							String path2 = arq.arquivosList.get(j).pathArquivo;
							
							if(path1.equals(path2))
							{
								int index= -1;
								
								for(int m=0;m<novosArquivos.size();m++)
								{
									if(novosArquivos.get(m).pathArquivo.equals(path1))
									{
										index = m;
										break;
									}
								}
								
								if(index >= 0)
								{
									if(arquivoAdicionar.get(i).diretorio)
									{
										String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										
										String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
										
										String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + arquivoAdicionar.get(i).nomeArquivo + "/config.xml";
										
										//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
						            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
						            	//em uma pasta equivalemte na pasta da nuvem
						            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
						            	
						            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivoAdicionar.get(i).nomeArquivo + "/";
						            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
						            	arqDir.DirArquivoConfig = novoPathArqConf;
						            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
						            	
						            	arquivosAddOutrosDir.add(arqDir);
						            	
									}
									
									//Verifica a última data/horário de modificação do arquivo. Se for o mesmo, o arquivo não precisa ser encriptado
									//Se a data do arquivo da pasta do usuário for mais recente que a data do arquivo cifrado na nuvem, então ele precisa ser encriptado
									if(arquivoAdicionar.get(i).dataUltimaMod.compareTo(arq.arquivosList.get(j).dataUltimaMod) <= 0 || arquivoAdicionar.get(i).diretorio)
									{
										novosArquivos.get(index).dataUltimaModArqEncrypt = arq.arquivosList.get(j).dataUltimaModArqEncrypt;
										novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										novosArquivos.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
										antigosArquivos.add(novosArquivos.get(index));										
										novosArquivos.remove(index);
									}
									else
									{
										novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										novosArquivos.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
									}
								}
																
								arq.arquivosList.remove(j);
								j--;
								
								break;
							}							
						}
					}					
				} 
        		catch (ParseException e) 
        		{
        			e.printStackTrace();
				}
        	}        	
        	
        	//Caso em que um arquivo foi deletado da pasta do usuário (Arquivos secretos): ele necessita ser apagado da pasta da nuvem e do arquivo de config.
        	if(arq.arquivosList != null && arq.arquivosList.size() > 0)
			{
        		arquivoConfigAlterado = true;
        		
        		//Se não houver a necessidade de criptografar nenhum arquivo na iteração em que há arquivos para serem deletados,
        		//deve-se criar o arquivo de config novamente, uma vez que quando um arquivo for deletado, ele também deve ser excluído 
        		//do arquivo de configuração
        		if(novosArquivos == null || novosArquivos.size() == 0)
        		{
        			//Escrevendo no arquivo de configuração os dados dos arquivos que já estão cifrados e armazenados na pasta da nuvem
        			if(antigosArquivos != null && antigosArquivos.size() > 0)
            		{
        				//Escrevendo no arquivo de configuração os dados dos arquivos 
        				FileWriter fw = new FileWriter(arquivoConfig, false);
    	    			Element arquivos = new Element("Arquivos");
    	    			
    	    			//Define Agenda como root
    	    	        Document documento = new Document(arquivos);
    	    	        
        				for(int i=0; i < antigosArquivos.size(); i++)
                		{
        					//Cria o elemento Arquivo
        		            Element arq = new Element("Arquivo");
        		            
        	        		//Adiciona o atributo id ao arquivo
        		            arq.setAttribute("id",Integer.toString(i));
        		
        		            //Criando os elementos do arquivo
        		
        		            Element nomeArquivo = new Element("nomeArquivo");
        		            nomeArquivo.setText(antigosArquivos.get(i).nomeArquivo);
        		
        		            Element pathArquivo = new Element("path");
        		            pathArquivo.setText(antigosArquivos.get(i).pathArquivo);
        		
        		            Element ultimaModificacao = new Element("ultimaModificacao");
        		            ultimaModificacao.setText(antigosArquivos.get(i).dataUltimaMod.toString());
        		
        		            Element diretorio = new Element("diretorio");
        		            diretorio.setText(antigosArquivos.get(i).diretorio == true ? "true" : "false");
        		            
        		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
        		            ultimaModificacaoArqEncrypt.setText(antigosArquivos.get(i).dataUltimaModArqEncrypt.toString());
        		            
        		            
        		            //Confirmar
        		            
        		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
        		            nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.toString());
        		            
        		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
        		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.toString());
        		            
        		            
        		            
        		            
        		            //
        		            
        		                    		
        		            //Adicionando elementos no arquivo        		
        		            arq.addContent(nomeArquivo);        		
        		            arq.addContent(pathArquivo);        		
        		            arq.addContent(ultimaModificacao);  
        		            arq.addContent(ultimaModificacaoArqEncrypt);
        		            
        		            if(!antigosArquivos.get(i).diretorio)
        		            	diretorio.setAttribute("pathArqConfig", "");        			            
        		            else
        		            	diretorio.setAttribute("pathArqConfig", antigosArquivos.get(i).pathArqEstados);        		            	
        		                    		
        		            arq.addContent(diretorio);
        		            arq.addContent(nomeArquivoEncrypt);
        		            arq.addContent(pathArquivoEncrypt);
        		
        		            //Adicionado o arquivo a Arquivos        		
        		            arquivos.addContent(arq);        					
                		}
        				
        				//Classe responsável para imprimir / gerar o XML
    			        XMLOutputter xout = new XMLOutputter();

    		            //Imprimindo o XML no arquivo
    		            xout.output(documento, fw);
            		}
        			//Se não há nenhum arquivo antigo, é necessário apenas criar um arquivo de configuração com a estrutura básica
        			else if(novosArquivos.size() == 0 && antigosArquivos.size() == 0)
        			{
        				FileWriter fw = new FileWriter(arquivoConfig, false);
    	    			Element arquivos = new Element("Arquivos");
    	    			
    	    			//Define Agenda como root
    	    	        Document documento = new Document(arquivos);
    	    	        
    	    	        //Classe responsável para imprimir / gerar o XML
    			        XMLOutputter xout = new XMLOutputter();

    		            //Imprimindo o XML no arquivo
    		            xout.output(documento, fw);   	 
        			}        			
        		}
        		
        		//Varrendo a lista de arquivos para buscar e deletar os arquivos presentes nela
        		for(int i=0; i < arq.arquivosList.size(); i++)
        		{
        			//Deletando arquivo cifrado da pasta da nuvem
        			if(!arq.arquivosList.get(i).diretorio)
        			{
        				File tempFile = new File(DirNuvem + arq.arquivosList.get(i).nomeArquivoEncrypt + ".encrypted");
            			
        				if(tempFile != null && tempFile.exists());
        					tempFile.delete();
        			}
        			else
        			{
        				//Se for diretório, primeiro deve-se deletar todos os seus arquivos para que então o diretório seja deletado
        				File tempFile = new File(DirNuvem + arq.arquivosList.get(i).nomeArquivoEncrypt);
            			
        				if(tempFile != null && tempFile.exists());
        				{
        					RemoverArquivos(tempFile);
        					/*
        					if(tempFile.listFiles() != null)
        						for(File e:tempFile.listFiles())
        						{
        							if(e != null && e.exists())
        								RemoverArquivos(e);
        						}*/
        				}        					
        				
        				tempFile.delete();
        				
        				//Deletando o arquivo de configuração pertinente ao diretório sendo deletado
        				
        				int indArquivo = arq.arquivosList.get(i).pathArqEstados.lastIndexOf('/');
        				String arquivo = "";
        				
        				if(indArquivo > 0)
        					arquivo = arq.arquivosList.get(i).pathArqEstados.substring(0, indArquivo);
        				else
        					arquivo = arq.arquivosList.get(i).pathArqEstados;
        				
        				File tempFileConfig = new File(arquivo);     
        				
        				if(tempFileConfig != null && tempFileConfig.exists())
        					RemoverArquivos(tempFileConfig);
        			}        			
        		}        		
			}         	
        	        	
        	//Os arquivos são criptogrados e armazenados na pasta da nuvem 
        	if(novosArquivos != null && novosArquivos.size() > 0)
	        {
        		//Escrevendo no arquivo de configuração os dados dos arquivos 
        		FileWriter fw = new FileWriter(arquivoConfig, false);
    			Element arquivos = new Element("Arquivos");
    			
    			arquivoConfigAlterado = true;
    			
    			//Define Arquivos como root
    	        Document documento = new Document(arquivos);
    	                 
    	        //contador necessário para controlar o ID. 
    	        //Aqui são escritos no arquivo de configuração os arquivos que já se encontram cifrados na pasta da nuvem
    	        //O contador será utilizado no passo seguinte, onde os novos arquivos a serem cifrados devem continuar do número parado neste loop
	            int contId = 0;
    	        
	            //Escrevendo no arquivo de configuração os dados dos arquivos que já estão cifrados e armazenados na pasta da nuvem
    	        if(antigosArquivos != null && antigosArquivos.size() > 0)
    	        {
    	        	for(int i=0; i < antigosArquivos.size(); i++)
    	        	{
    	        		//Cria o elemento Arquivo
    		            Element arq = new Element("Arquivo");
    		            
    	        		//Adiciona o atributo id ao arquivo
    		            arq.setAttribute("id",Integer.toString(i));
    		            contId++;
    		
    		            //Criando os elementos do arquivo    		
    		            Element nomeArquivo = new Element("nomeArquivo");
    		            nomeArquivo.setText(antigosArquivos.get(i).nomeArquivo);
    		
    		            Element pathArquivo = new Element("path");
    		            pathArquivo.setText(antigosArquivos.get(i).pathArquivo);
    		    		               		            
    		            Element ultimaModificacao = new Element("ultimaModificacao");
    		            ultimaModificacao.setText(antigosArquivos.get(i).dataUltimaMod.toString());
    		            
    		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
    		            ultimaModificacaoArqEncrypt.setText(antigosArquivos.get(i).dataUltimaModArqEncrypt.toString());
    		
    		            Element diretorio = new Element("diretorio");
    		            diretorio.setText(antigosArquivos.get(i).diretorio == true ? "true" : "false");
    		            
    		            
    		            
    		            //Confirmar
    		            
    		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
    		            nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.toString());
    		            
    		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
    		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.toString());
    		            
    		            //
    		            
    		            //Adicionando elementos no arquivo    		
    		            arq.addContent(nomeArquivo);    		
    		            arq.addContent(pathArquivo);    		
    		            arq.addContent(ultimaModificacao);
    		            arq.addContent(ultimaModificacaoArqEncrypt);
    		            
    		            if(!antigosArquivos.get(i).diretorio)
    		            	diretorio.setAttribute("pathArqConfig", "");        			            
    		            else
    		            	diretorio.setAttribute("pathArqConfig", antigosArquivos.get(i).pathArqEstados);        		            	
    		                		
    		            arq.addContent(diretorio);
    		            arq.addContent(nomeArquivoEncrypt);
    		            arq.addContent(pathArquivoEncrypt);
    		
    		            //Adicionado o arquivo a Arquivos    		
    		            arquivos.addContent(arq);
    	        	}
    	        }
    	        
    	        //Criptografando os arquivo para que sejam armazenados na pasta da nuvem
    	        for(int i = 0; i < novosArquivos.size(); i++)
		        {
    	        	//Escrevendo os dados dos novos arquivos no arquivo de configuração
    	        	
		        	//Cria o elemento Arquivo
		            Element arq = new Element("Arquivo");
		            
		        	//Adiciona o atributo id ao arquivo
		            arq.setAttribute("id",Integer.toString(i + contId));
		
		            //Criando os elementos do arquivo		
		            Element nomeArquivo = new Element("nomeArquivo");
		            nomeArquivo.setText(novosArquivos.get(i).nomeArquivo);
		
		            Element pathArquivo = new Element("path");
		            pathArquivo.setText(novosArquivos.get(i).pathArquivo);
		        
		            Element ultimaModificacao = new Element("ultimaModificacao");
		            ultimaModificacao.setText(novosArquivos.get(i).dataUltimaMod.toString());
		
		            Element diretorio = new Element("diretorio");
		            diretorio.setText(novosArquivos.get(i).diretorio == true ? "true" : "false");
		            
		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
		            		
		            //Adicionando elementos no arquivo		
		            arq.addContent(nomeArquivo);		
		            arq.addContent(pathArquivo);		
		            arq.addContent(ultimaModificacao);
		            
		            
		            
		          //Confirmar
		            
		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
		            
		            
		            
		            String nomeArEncrypt = "";
		            
		            
		            //
		            
		            
		            if(!novosArquivos.get(i).diretorio)
		            {
		            	diretorio.setAttribute("pathArqConfig", "");
		            	
		            	//Criptografando o arquivo
		            	if(novosArquivos.get(i).nomeArquivoEncrypt != null && !novosArquivos.get(i).nomeArquivoEncrypt.isEmpty())
		            		nomeArEncrypt = CriptoUtil.CriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirNuvem + novosArquivos.get(i).nomeArquivo), senhaUsuario, novosArquivos.get(i).nomeArquivoEncrypt);
		            	else
		            		nomeArEncrypt = CriptoUtil.CriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirNuvem + novosArquivos.get(i).nomeArquivo), senhaUsuario, null);
		            	
		            	if(nomeArEncrypt != null && !nomeArEncrypt.isEmpty())
		            	{
		            		Date lastModified = new Date(new File(DirNuvem + nomeArEncrypt + ".encrypted").lastModified());
			            	
			            	ultimaModificacaoArqEncrypt.setText(lastModified.toString());
		            	}
		            	else
		            		throw new Exception("Erro ao cifrar arquivo! ");
		            }
		            else
		            {
		            	//Criptografando os diretórios - Na verdade é criado um novo diretório na pasta da nuvem e posteriormente inserido os 
		            	//arquivos nele, todos criptografados.
		            	
		            	//Criptografando o nome do diretório
		            	
		            	File pastatemp;
		            	
		            	if(novosArquivos.get(i).nomeArquivoEncrypt != null && !novosArquivos.get(i).nomeArquivoEncrypt.isEmpty())
	            		{
	            			nomeArEncrypt = novosArquivos.get(i).nomeArquivoEncrypt;
	            			
	            			pastatemp = new File(DirNuvem + nomeArEncrypt);
			            	
			            	if(!pastatemp.exists())
		            		{
			            		String hashNome = HashGeneratorUtils.generateSHA256(novosArquivos.get(i).nomeArquivo.toString());
				            	
			            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
			            	
			            		Base32 codec = new Base32();
							
			            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(novosArquivos.get(i).nomeArquivo.toString(), key);
			            		String nomePastaEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
					        
			            		if(nomePastaEncrypt.length() > 30)
			            			nomeArEncrypt = nomePastaEncrypt.substring(0, 29);
			            		else
			            			nomeArEncrypt = nomePastaEncrypt;
			            		
			            		pastatemp = new File(DirNuvem + nomeArEncrypt);
			            		
			            		if(!pastatemp.exists())
			            			pastatemp.mkdir();
			            		
			            		FileWriter arqChaveArquivo = new FileWriter(DirNuvem + nomeArEncrypt + "/keyFolder.encrypted"); 
								PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
								
								String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
								
								gravarArqChaveArquivo.println("chave="+keyCifrada);
								gravarArqChaveArquivo.println("nomePastaEncrypt="+nomePastaEncrypt);
								
								arqChaveArquivo.close();
								gravarArqChaveArquivo.close();
		            		}
			            	
		            	}
		            	else
		            	{
		            		String hashNome = HashGeneratorUtils.generateSHA256(novosArquivos.get(i).nomeArquivo.toString());
		            	
		            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
		            	
		            		Base32 codec = new Base32();
						
		            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(novosArquivos.get(i).nomeArquivo.toString(), key);
		            		String nomePastaEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
				        
		            		if(nomePastaEncrypt.length() > 30)
		            			nomeArEncrypt = nomePastaEncrypt.substring(0, 29);
		            		else
		            			nomeArEncrypt = nomePastaEncrypt;
		            		
		            		pastatemp = new File(DirNuvem + nomeArEncrypt);
			            	
			            	if(!pastatemp.exists())
			            		pastatemp.mkdir();
			            	
			            	FileWriter arqChaveArquivo = new FileWriter(DirNuvem + nomeArEncrypt + "/keyFolder.encrypted"); 
							PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
							
							String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
							
							gravarArqChaveArquivo.println("chave="+keyCifrada);
							gravarArqChaveArquivo.println("nomePastaEncrypt="+nomePastaEncrypt);
							
							arqChaveArquivo.close();
							gravarArqChaveArquivo.close();
		            	}
		            	
		            	
		            			            	
		            	Date lastModified = new Date(pastatemp.lastModified());
		            	ultimaModificacaoArqEncrypt.setText(lastModified.toString());
		            	
		            	
		            	
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo.replace(' ', '-') + "-config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt +"/config.xml";
		            	String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt + "-config.xml";
		            	
		            	//Config-files/config.xml
		            			            	
		            	//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
		            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
		            	//em uma pasta equivalemte na pasta da nuvem
		            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
		            	
		            	
		            	String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo + "/config.xml";
		            	
		            	diretorio.setAttribute("pathArqConfig", novoPathArqConf);
		            	
		            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + novosArquivos.get(i).nomeArquivo + "/";
		            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
		            	arqDir.DirArquivoConfig = novoPathArqConf;
		            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
		            			            			            	
		            	arquivosAddOutrosDir.add(arqDir);
		            }	            
		            
		            arq.addContent(ultimaModificacaoArqEncrypt);
		            arq.addContent(diretorio);
		            
		            
		            //CONFIRMAR
		            
		            pathArquivoEncrypt.setText(DirNuvem + nomeArEncrypt);
		            nomeArquivoEncrypt.setText(nomeArEncrypt);
		            
		            
		            
		            arq.addContent(nomeArquivoEncrypt);
		            arq.addContent(pathArquivoEncrypt);
		            
		            
		            //
		            
		            
		            //Adicionado o arquivo a Arquivos		
		            arquivos.addContent(arq);		            
		        }
		        
		        //Classe responsável para imprimir / gerar o XML
		        XMLOutputter xout = new XMLOutputter();

	            //Imprimindo o XML no arquivo
	            xout.output(documento, fw);
	        }
        	
        	if(arquivoConfigAlterado)
        	{
        		File arquivoConfigEncrypted = new File(pathArquivoConfigEncrypted);
        		
        		//Criptografar arquivo de configurações e enviá-lo para a pasta da nuvem...
        		CriptoUtil.CriptografarArquivo(arquivoConfig, arquivoConfigEncrypted, senhaUsuario, arquivoConfig.getName());
        	}
        	
        	//Deletando arquivo config...
    		//arquivoConfig.delete();
        
        	//Verificando as pastas do diretório sendo analizado para que os arquivos presentes nela possam ser encriptografados
        	if(arquivosAddOutrosDir != null && arquivosAddOutrosDir.size() > 0)
        	{
        		for(int i = 0; i < arquivosAddOutrosDir.size(); i++)
        		{
        			//Processo recursivo de busca por arquivos para encriptação
        			VerificarNovosArquivos(arquivosAddOutrosDir.get(i).DirArquivosSecretosUser, arquivosAddOutrosDir.get(i).DirArquivosNuvem, arquivosAddOutrosDir.get(i).DirArquivoConfig, arquivosAddOutrosDir.get(i).DirArquivoConfigEncrypted, senhaUsuario);
        		}
        	}        	
        } 
        catch (IOException e) 
        {
        	throw new Exception("Erro na escrita no arquivo de configuração do sistema! " + e.getMessage());
    	}  
	}
	
	public static void VerificarArquivosParaDecifrar(String DirArquivosSecretosUser, String DirNuvem, String DirArquivoConfigXml, String DirArquivoConfigXmlEncrypted, String senhaUsuario) throws Exception
	{
		try
		{
			//File dirArquivosUser = new File(DirArquivosSecretosUser);
			File DirArquivosNuvem = new File(DirNuvem);
			
			//File arquivosDirUser[]  = dirArquivosUser.listFiles();
			File arquivosDirNuvem[]  = DirArquivosNuvem.listFiles();
			
			Arquivos arquivosDirNuvemList = new Arquivos();
						
			//Adicionar arquivos na lista
			for(int i = 0; i< arquivosDirNuvem.length; i++) 
			{
				File each = arquivosDirNuvem[i];
				
				if(each != null) 
				{
					String fileName = each.getName();
					
					if(!fileName.equals("config.xml") && !fileName.equals("config.xml.encrypted") && !fileName.equals("config.xml.encrypted~") && !fileName.equals("config.xml~") && !fileName.equals("keyFolder.encrypted") && !fileName.equals("keyFolder.encrypted~"))
					{
						Date lastModified = new Date(each.lastModified());
		        	
						arquivosDirNuvemList.AdicionarNovoRegistro("", "", null, each.isDirectory(), each.getAbsolutePath().substring(0, each.getAbsolutePath().indexOf(each.getName())), lastModified, each.getName(), each.getAbsolutePath(), false, each.getParent());
					}
				}
			}
			
			EscritaArqDecifrar(arquivosDirNuvemList.arquivosList, DirArquivoConfigXml, DirArquivoConfigXmlEncrypted, DirArquivosSecretosUser, DirNuvem, senhaUsuario);
		}
		catch(Exception e)
		{
			throw new Exception("Erro na verificação de arquivos para decifrar! Erro: " + e.getMessage());
		}
	}
	
	public static List<DiretoriosDosArquivos> EscritaEvento(List<Arquivos> arquivosLocal, String pathArquivoConfig, String pathArquivoConfigEncrypted, String DirArquivosSecretosUser, String DirNuvem, String senhaUsuario) throws Exception
	{
		try 
        {
			//Arquivo de configuração atualizado
        	File arquivoConfig = new File(pathArquivoConfig);
			
			//Arquivos a serem criptografados e enviados para a pasta da nuvem
        	List<Arquivos> novosArquivos = new ArrayList<Arquivos>();
        	
        	//Arquivos que já foram criptografados e se encontram na pasta da nuvem
        	List<Arquivos> antigosArquivos = new ArrayList<Arquivos>();
        	
        	//Lista de caminhos dos diretórios que devem ter seus arquivos criptografados e enviados para a pasta da nuvem
        	List<DiretoriosDosArquivos> arquivosAddOutrosDir = new ArrayList<DiretoriosDosArquivos>();
        	        	
        	if(!arquivoConfig.exists())
        	{
        		try 
				{
        			if(!new File(arquivoConfig.getParent()).exists());
        				new File(arquivoConfig.getParent()).mkdirs();
        			
        			arquivoConfig.createNewFile();
        			        			
        			FileWriter fw = new FileWriter(arquivoConfig, false);
	    			Element arquivos = new Element("Arquivos");
	    			
	    			//Define Agenda como root
	    	        Document documento = new Document(arquivos);
	    	        
	    	        //Classe responsável para imprimir / gerar o XML
			        XMLOutputter xout = new XMLOutputter();

		            //Imprimindo o XML no arquivo
		            xout.output(documento, fw);   
					
					novosArquivos.addAll(arquivosLocal);
					antigosArquivos = null;
					
					arquivosCifrarNovos.addAll(novosArquivos);					
					
					if(novosArquivos != null && novosArquivos.size() > 0)
			        {
						for(int i=0; i < novosArquivos.size(); i++)
						{
							if(novosArquivos.get(i).diretorio)
							{
								
								//novosArquivos.get(i).arquivosList = new ArrayList<Arquivos>();
								
								String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + arquivosLocal.get(i).nomeArquivo + "/config.xml";
								
								//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
				            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
				            	//em uma pasta equivalemte na pasta da nuvem
				            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
				            	
				            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivosLocal.get(i).nomeArquivo + "/";
				            	arqDir.DirArquivosNuvem = "";//DirNuvem + nomeArEncrypt + "";
				            	arqDir.DirArquivoConfig = novoPathArqConf;
				            	arqDir.DirArquivoConfigEncrypted = "";
				            	
				            	arquivosAddOutrosDir.add(arqDir);						            	
							}
						}
			        }
					
					return arquivosAddOutrosDir;
					
				} 
				catch (IOException e) 
				{
					novosArquivos = null;
					antigosArquivos = null;
					
					e.printStackTrace();
				}
        	}
        	else
        	{
        		try 
        		{
        			//Faz a leitura dos arquivos presentes no arquivo de configuração e armazena suas infor. em "arq"
					Leitura(pathArquivoConfig, true);
					
					//Adiciona todos arquivos presentes no diretório do usuário em uma nova lista para manipulação
					novosArquivos.addAll(arquivosLocal);
					
					//Verifica quais são os arquivos que presentes no diretório do usuário que não foram criptografados e armazenados na pasta da nuvem
					for(int i = 0; i< arquivosLocal.size(); i++)
					{
						String path1 = arquivosLocal.get(i).pathArquivo;
						
						for(int j = 0; j< arq.arquivosList.size(); j++)
						{
							String path2 = arq.arquivosList.get(j).pathArquivo;
							
							if(path1.equals(path2))
							{
								int index= -1;
								
								for(int m=0;m<novosArquivos.size();m++)
								{
									if(novosArquivos.get(m).pathArquivo.equals(path1))
									{
										index = m;
										break;
									}
								}
								
								if(index >= 0)
								{
									if(arquivosLocal.get(i).diretorio)
									{
										String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										
										String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
										
										String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + arquivosLocal.get(i).nomeArquivo + "/config.xml";
										
										//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
						            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
						            	//em uma pasta equivalemte na pasta da nuvem
						            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
						            	
						            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivosLocal.get(i).nomeArquivo + "/";
						            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
						            	arqDir.DirArquivoConfig = novoPathArqConf;
						            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
						            	
						            	arquivosAddOutrosDir.add(arqDir);						            	
									}
									
									//Verifica a última data/horário de modificação do arquivo. Se for o mesmo, o arquivo não precisa ser encriptado
									//Se a data do arquivo da pasta do usuário for mais recente que a data do arquivo cifrado na nuvem, então ele precisa ser encriptado
									if(arquivosLocal.get(i).dataUltimaMod.compareTo(arq.arquivosList.get(j).dataUltimaMod) <= 0 || arquivosLocal.get(i).diretorio)
									{
										novosArquivos.get(index).dataUltimaModArqEncrypt = arq.arquivosList.get(j).dataUltimaModArqEncrypt;
										novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										novosArquivos.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
										antigosArquivos.add(novosArquivos.get(index));										
										novosArquivos.remove(index);
									}
									else
									{
										novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										novosArquivos.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
									}
								}
																
								arq.arquivosList.remove(j);
								j--;
								
								break;
							}							
						}
					}					
				} 
        		catch (ParseException e) 
        		{
        			e.printStackTrace();
				}
        	} 
        	
        	
        	
			
        	//Os arquivos são criptogrados e armazenados na pasta da nuvem 
        	if(novosArquivos != null && novosArquivos.size() > 0)
	        {
        		//Escrevendo no arquivo de configuração os dados dos arquivos 
        		FileWriter fw = new FileWriter(arquivoConfig, false);
    			Element arquivos = new Element("Arquivos");
    			
    			//arquivoConfigAlterado = true;
    			
    			//Define Arquivos como root
    	        Document documento = new Document(arquivos);
    	                 
    	        //contador necessário para controlar o ID. 
    	        //Aqui são escritos no arquivo de configuração os arquivos que já se encontram cifrados na pasta da nuvem
    	        //O contador será utilizado no passo seguinte, onde os novos arquivos a serem cifrados devem continuar do número parado neste loop
	            int contId = 0;
    	        
	            //Escrevendo no arquivo de configuração os dados dos arquivos que já estão cifrados e armazenados na pasta da nuvem
    	        if(antigosArquivos != null && antigosArquivos.size() > 0)
    	        {
    	        	for(int i=0; i < antigosArquivos.size(); i++)
    	        	{
    	        		//Cria o elemento Arquivo
    		            Element arq = new Element("Arquivo");
    		            
    	        		//Adiciona o atributo id ao arquivo
    		            arq.setAttribute("id",Integer.toString(i));
    		            contId++;
    		
    		            //Criando os elementos do arquivo    		
    		            Element nomeArquivo = new Element("nomeArquivo");
    		            nomeArquivo.setText(antigosArquivos.get(i).nomeArquivo);
    		
    		            Element pathArquivo = new Element("path");
    		            pathArquivo.setText(antigosArquivos.get(i).pathArquivo);
    		    		               		            
    		            Element ultimaModificacao = new Element("ultimaModificacao");
    		            ultimaModificacao.setText(antigosArquivos.get(i).dataUltimaMod.toString());
    		            
    		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
    		            ultimaModificacaoArqEncrypt.setText(antigosArquivos.get(i).dataUltimaModArqEncrypt.toString());
    		
    		            Element diretorio = new Element("diretorio");
    		            diretorio.setText(antigosArquivos.get(i).diretorio == true ? "true" : "false");
    		            
    		            
    		            
    		            //Confirmar
    		            
    		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
    		            nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.toString());
    		            
    		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
    		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.toString());
    		            
    		            //
    		            
    		            //Adicionando elementos no arquivo    		
    		            arq.addContent(nomeArquivo);    		
    		            arq.addContent(pathArquivo);    		
    		            arq.addContent(ultimaModificacao);
    		            arq.addContent(ultimaModificacaoArqEncrypt);
    		            
    		            if(!antigosArquivos.get(i).diretorio)
    		            	diretorio.setAttribute("pathArqConfig", "");        			            
    		            else
    		            	diretorio.setAttribute("pathArqConfig", antigosArquivos.get(i).pathArqEstados);        		            	
    		                		
    		            arq.addContent(diretorio);
    		            arq.addContent(nomeArquivoEncrypt);
    		            arq.addContent(pathArquivoEncrypt);
    		
    		            //Adicionado o arquivo a Arquivos    		
    		            arquivos.addContent(arq);
    	        	}
    	        }
    	        
    	        
		        
	        }
        	
        	//Deletando arquivo config...
    		//arquivoConfig.delete();
        
        	//Verificando as pastas do diretório sendo analizado para que os arquivos presentes nela possam ser encriptografados
        	if(arquivosAddOutrosDir != null && arquivosAddOutrosDir.size() > 0)
        	{
        		for(int i = 0; i < arquivosAddOutrosDir.size(); i++)
        		{
        			//Processo recursivo de busca por arquivos para encriptação
        			VerificarNovosArquivos(arquivosAddOutrosDir.get(i).DirArquivosSecretosUser, arquivosAddOutrosDir.get(i).DirArquivosNuvem, arquivosAddOutrosDir.get(i).DirArquivoConfig, arquivosAddOutrosDir.get(i).DirArquivoConfigEncrypted, senhaUsuario);
        		}
        	}    
        	
        	
        	
        	
        	
        	
        	
        	
        	
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			boolean arquivoConfigAlterado = false;
			
			//atualizar arquivo de configuração
			String pathArquivoConfigTemp = AtualizarArquivoConfig(pathArquivoConfig, pathArquivoConfigEncrypted, senhaUsuario);
			
			//Arquivo de configuração atualizado
        	//File arquivoConfig = new File(pathArquivoConfigTemp);
			
			//Arquivos a serem criptografados e enviados para a pasta da nuvem
        	//List<Arquivos> novosArquivos = new ArrayList<Arquivos>();
        	
        	//Arquivos que já foram criptografados e se encontram na pasta da nuvem
        	//List<Arquivos> antigosArquivos = new ArrayList<Arquivos>();
        	
        	//Lista de caminhos dos diretórios que devem ter seus arquivos criptografados e enviados para a pasta da nuvem
        	//List<DiretoriosDosArquivos> arquivosAddOutrosDir = new ArrayList<DiretoriosDosArquivos>();
        	        	
        	if(!arquivoConfig.exists())
        	{
        		try 
				{
        			arquivoConfigAlterado = true;
        			
        			new File(arquivoConfig.getParent()).mkdirs();
        			
        			arquivoConfig.createNewFile();
        			        			
        			FileWriter fw = new FileWriter(arquivoConfig, false);
	    			Element arquivos = new Element("Arquivos");
	    			
	    			//Define Agenda como root
	    	        Document documento = new Document(arquivos);
	    	        
	    	        //Classe responsável para imprimir / gerar o XML
			        XMLOutputter xout = new XMLOutputter();

		            //Imprimindo o XML no arquivo
		            xout.output(documento, fw);   
					
					novosArquivos.addAll(arquivosLocal);
					antigosArquivos = null;
				} 
				catch (IOException e) 
				{
					novosArquivos = null;
					antigosArquivos = null;
					
					e.printStackTrace();
				}
        	}
        	else
        	{
        		try 
        		{
        			//Faz a leitura dos arquivos presentes no arquivo de configuração e armazena suas infor. em "arq"
					Leitura(pathArquivoConfigTemp, true);
					
					//Adiciona todos arquivos presentes no diretório do usuário em uma nova lista para manipulação
					novosArquivos.addAll(arquivosLocal);
					
					//Verifica quais são os arquivos que presentes no diretório do usuário que não foram criptografados e armazenados na pasta da nuvem
					for(int i = 0; i< arquivosLocal.size(); i++)
					{
						String path1 = arquivosLocal.get(i).pathArquivo;
						
						for(int j = 0; j< arq.arquivosList.size(); j++)
						{
							String path2 = arq.arquivosList.get(j).pathArquivo;
							
							if(path1.equals(path2))
							{
								int index= -1;
								
								for(int m=0;m<novosArquivos.size();m++)
								{
									if(novosArquivos.get(m).pathArquivo.equals(path1))
									{
										index = m;
										break;
									}
								}
								
								if(index >= 0)
								{
									if(arquivosLocal.get(i).diretorio)
									{
										String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										
										String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
										
										String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + arquivosLocal.get(i).nomeArquivo + "/config.xml";
										
										//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
						            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
						            	//em uma pasta equivalemte na pasta da nuvem
						            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
						            	
						            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivosLocal.get(i).nomeArquivo + "/";
						            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
						            	arqDir.DirArquivoConfig = novoPathArqConf;
						            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
						            	
						            	arquivosAddOutrosDir.add(arqDir);						            	
									}
									
									//Verifica a última data/horário de modificação do arquivo. Se for o mesmo, o arquivo não precisa ser encriptado
									//Se a data do arquivo da pasta do usuário for mais recente que a data do arquivo cifrado na nuvem, então ele precisa ser encriptado
									if(arquivosLocal.get(i).dataUltimaMod.compareTo(arq.arquivosList.get(j).dataUltimaMod) <= 0 || arquivosLocal.get(i).diretorio)
									{
										novosArquivos.get(index).dataUltimaModArqEncrypt = arq.arquivosList.get(j).dataUltimaModArqEncrypt;
										novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										novosArquivos.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
										antigosArquivos.add(novosArquivos.get(index));										
										novosArquivos.remove(index);
									}
									else
									{
										novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										novosArquivos.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
									}
								}
																
								arq.arquivosList.remove(j);
								j--;
								
								break;
							}							
						}
					}					
				} 
        		catch (ParseException e) 
        		{
        			e.printStackTrace();
				}
        	}       
        	
        	//Arquivos que precisam ser deletados
        	
        	for(int i = 0; i < arquivosLocal.size(); i++)
        	{
        		
        	}
        	
        	
        	
        	//Arquivos que precisam ser decifrados
        	
        	if(arq.arquivosList != null && arq.arquivosList.size() > 0)
        	{
        		
        	}
        	
        	
        	
        	//*********************************
        	
        	//Caso em que um arquivo foi deletado da pasta do usuário (Arquivos secretos): ele necessita ser apagado da pasta da nuvem e do arquivo de config.
        	if(arq.arquivosList != null && arq.arquivosList.size() > 0)
			{
        		arquivoConfigAlterado = true;
        		
        		//Se não houver a necessidade de criptografar nenhum arquivo na iteração em que há arquivos para serem deletados,
        		//deve-se criar o arquivo de config novamente, uma vez que quando um arquivo for deletado, ele também deve ser excluído 
        		//do arquivo de configuração
        		if(novosArquivos == null || novosArquivos.size() == 0)
        		{
        			//Escrevendo no arquivo de configuração os dados dos arquivos que já estão cifrados e armazenados na pasta da nuvem
        			if(antigosArquivos != null && antigosArquivos.size() > 0)
            		{
        				//Escrevendo no arquivo de configuração os dados dos arquivos 
        				FileWriter fw = new FileWriter(arquivoConfig, false);
    	    			Element arquivos = new Element("Arquivos");
    	    			
    	    			//Define Agenda como root
    	    	        Document documento = new Document(arquivos);
    	    	        
        				for(int i=0; i < antigosArquivos.size(); i++)
                		{
        					//Cria o elemento Arquivo
        		            Element arq = new Element("Arquivo");
        		            
        	        		//Adiciona o atributo id ao arquivo
        		            arq.setAttribute("id",Integer.toString(i));
        		
        		            //Criando os elementos do arquivo
        		
        		            Element nomeArquivo = new Element("nomeArquivo");
        		            nomeArquivo.setText(antigosArquivos.get(i).nomeArquivo);
        		
        		            Element pathArquivo = new Element("path");
        		            pathArquivo.setText(antigosArquivos.get(i).pathArquivo);
        		
        		            Element ultimaModificacao = new Element("ultimaModificacao");
        		            ultimaModificacao.setText(antigosArquivos.get(i).dataUltimaMod.toString());
        		
        		            Element diretorio = new Element("diretorio");
        		            diretorio.setText(antigosArquivos.get(i).diretorio == true ? "true" : "false");
        		            
        		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
        		            ultimaModificacaoArqEncrypt.setText(antigosArquivos.get(i).dataUltimaModArqEncrypt.toString());
        		            
        		            
        		            //Confirmar
        		            
        		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
        		            nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.toString());
        		            
        		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
        		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.toString());
        		            
        		            
        		            
        		            
        		            //
        		            
        		                    		
        		            //Adicionando elementos no arquivo        		
        		            arq.addContent(nomeArquivo);        		
        		            arq.addContent(pathArquivo);        		
        		            arq.addContent(ultimaModificacao);  
        		            arq.addContent(ultimaModificacaoArqEncrypt);
        		            
        		            if(!antigosArquivos.get(i).diretorio)
        		            	diretorio.setAttribute("pathArqConfig", "");        			            
        		            else
        		            	diretorio.setAttribute("pathArqConfig", antigosArquivos.get(i).pathArqEstados);        		            	
        		                    		
        		            arq.addContent(diretorio);
        		            arq.addContent(nomeArquivoEncrypt);
        		            arq.addContent(pathArquivoEncrypt);
        		
        		            //Adicionado o arquivo a Arquivos        		
        		            arquivos.addContent(arq);        					
                		}
        				
        				//Classe responsável para imprimir / gerar o XML
    			        XMLOutputter xout = new XMLOutputter();

    		            //Imprimindo o XML no arquivo
    		            xout.output(documento, fw);
            		}
        			//Se não há nenhum arquivo antigo, é necessário apenas criar um arquivo de configuração com a estrutura básica
        			else if(novosArquivos.size() == 0 && antigosArquivos.size() == 0)
        			{
        				FileWriter fw = new FileWriter(arquivoConfig, false);
    	    			Element arquivos = new Element("Arquivos");
    	    			
    	    			//Define Agenda como root
    	    	        Document documento = new Document(arquivos);
    	    	        
    	    	        //Classe responsável para imprimir / gerar o XML
    			        XMLOutputter xout = new XMLOutputter();

    		            //Imprimindo o XML no arquivo
    		            xout.output(documento, fw);   	 
        			}        			
        		}
        		
        		//Varrendo a lista de arquivos para buscar e deletar os arquivos presentes nela
        		for(int i=0; i < arq.arquivosList.size(); i++)
        		{
        			//Deletando arquivo cifrado da pasta da nuvem
        			if(!arq.arquivosList.get(i).diretorio)
        			{
        				File tempFile = new File(DirNuvem + arq.arquivosList.get(i).nomeArquivoEncrypt + ".encrypted");
            			
        				if(tempFile != null && tempFile.exists());
        					tempFile.delete();
        			}
        			else
        			{
        				//Se for diretório, primeiro deve-se deletar todos os seus arquivos para que então o diretório seja deletado
        				File tempFile = new File(DirNuvem + arq.arquivosList.get(i).nomeArquivoEncrypt);
            			
        				if(tempFile != null && tempFile.exists());
        				{
        					RemoverArquivos(tempFile);
        					/*
        					if(tempFile.listFiles() != null)
        						for(File e:tempFile.listFiles())
        						{
        							if(e != null && e.exists())
        								RemoverArquivos(e);
        						}*/
        				}        					
        				
        				tempFile.delete();
        				
        				//Deletando o arquivo de configuração pertinente ao diretório sendo deletado
        				
        				int indArquivo = arq.arquivosList.get(i).pathArqEstados.lastIndexOf('/');
        				String arquivo = "";
        				
        				if(indArquivo > 0)
        					arquivo = arq.arquivosList.get(i).pathArqEstados.substring(0, indArquivo);
        				else
        					arquivo = arq.arquivosList.get(i).pathArqEstados;
        				
        				File tempFileConfig = new File(arquivo);     
        				
        				if(tempFileConfig != null && tempFileConfig.exists())
        					RemoverArquivos(tempFileConfig);
        			}        			
        		}        		
			}   
        	
        	
        	
        	
        	
        	
        	        	
        	//Os arquivos são criptogrados e armazenados na pasta da nuvem 
        	if(novosArquivos != null && novosArquivos.size() > 0)
	        {
        		//Escrevendo no arquivo de configuração os dados dos arquivos 
        		FileWriter fw = new FileWriter(arquivoConfig, false);
    			Element arquivos = new Element("Arquivos");
    			
    			arquivoConfigAlterado = true;
    			
    			//Define Arquivos como root
    	        Document documento = new Document(arquivos);
    	                 
    	        //contador necessário para controlar o ID. 
    	        //Aqui são escritos no arquivo de configuração os arquivos que já se encontram cifrados na pasta da nuvem
    	        //O contador será utilizado no passo seguinte, onde os novos arquivos a serem cifrados devem continuar do número parado neste loop
	            int contId = 0;
    	        
	            //Escrevendo no arquivo de configuração os dados dos arquivos que já estão cifrados e armazenados na pasta da nuvem
    	        if(antigosArquivos != null && antigosArquivos.size() > 0)
    	        {
    	        	for(int i=0; i < antigosArquivos.size(); i++)
    	        	{
    	        		//Cria o elemento Arquivo
    		            Element arq = new Element("Arquivo");
    		            
    	        		//Adiciona o atributo id ao arquivo
    		            arq.setAttribute("id",Integer.toString(i));
    		            contId++;
    		
    		            //Criando os elementos do arquivo    		
    		            Element nomeArquivo = new Element("nomeArquivo");
    		            nomeArquivo.setText(antigosArquivos.get(i).nomeArquivo);
    		
    		            Element pathArquivo = new Element("path");
    		            pathArquivo.setText(antigosArquivos.get(i).pathArquivo);
    		    		               		            
    		            Element ultimaModificacao = new Element("ultimaModificacao");
    		            ultimaModificacao.setText(antigosArquivos.get(i).dataUltimaMod.toString());
    		            
    		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
    		            ultimaModificacaoArqEncrypt.setText(antigosArquivos.get(i).dataUltimaModArqEncrypt.toString());
    		
    		            Element diretorio = new Element("diretorio");
    		            diretorio.setText(antigosArquivos.get(i).diretorio == true ? "true" : "false");
    		            
    		            
    		            
    		            //Confirmar
    		            
    		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
    		            nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.toString());
    		            
    		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
    		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.toString());
    		            
    		            //
    		            
    		            //Adicionando elementos no arquivo    		
    		            arq.addContent(nomeArquivo);    		
    		            arq.addContent(pathArquivo);    		
    		            arq.addContent(ultimaModificacao);
    		            arq.addContent(ultimaModificacaoArqEncrypt);
    		            
    		            if(!antigosArquivos.get(i).diretorio)
    		            	diretorio.setAttribute("pathArqConfig", "");        			            
    		            else
    		            	diretorio.setAttribute("pathArqConfig", antigosArquivos.get(i).pathArqEstados);        		            	
    		                		
    		            arq.addContent(diretorio);
    		            arq.addContent(nomeArquivoEncrypt);
    		            arq.addContent(pathArquivoEncrypt);
    		
    		            //Adicionado o arquivo a Arquivos    		
    		            arquivos.addContent(arq);
    	        	}
    	        }
    	        
    	        //Criptografando os arquivo para que sejam armazenados na pasta da nuvem
    	        for(int i = 0; i < novosArquivos.size(); i++)
		        {
    	        	//Escrevendo os dados dos novos arquivos no arquivo de configuração
    	        	
		        	//Cria o elemento Arquivo
		            Element arq = new Element("Arquivo");
		            
		        	//Adiciona o atributo id ao arquivo
		            arq.setAttribute("id",Integer.toString(i + contId));
		
		            //Criando os elementos do arquivo		
		            Element nomeArquivo = new Element("nomeArquivo");
		            nomeArquivo.setText(novosArquivos.get(i).nomeArquivo);
		
		            Element pathArquivo = new Element("path");
		            pathArquivo.setText(novosArquivos.get(i).pathArquivo);
		        
		            Element ultimaModificacao = new Element("ultimaModificacao");
		            ultimaModificacao.setText(novosArquivos.get(i).dataUltimaMod.toString());
		
		            Element diretorio = new Element("diretorio");
		            diretorio.setText(novosArquivos.get(i).diretorio == true ? "true" : "false");
		            
		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
		            		
		            //Adicionando elementos no arquivo		
		            arq.addContent(nomeArquivo);		
		            arq.addContent(pathArquivo);		
		            arq.addContent(ultimaModificacao);
		            
		            
		            
		          //Confirmar
		            
		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
		            
		            
		            
		            String nomeArEncrypt = "";
		            
		            
		            //
		            
		            
		            if(!novosArquivos.get(i).diretorio)
		            {
		            	diretorio.setAttribute("pathArqConfig", "");
		            	
		            	//Criptografando o arquivo
		            	if(novosArquivos.get(i).nomeArquivoEncrypt != null && !novosArquivos.get(i).nomeArquivoEncrypt.isEmpty())
		            		nomeArEncrypt = CriptoUtil.CriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirNuvem + novosArquivos.get(i).nomeArquivo), senhaUsuario, novosArquivos.get(i).nomeArquivoEncrypt);
		            	else
		            		nomeArEncrypt = CriptoUtil.CriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirNuvem + novosArquivos.get(i).nomeArquivo), senhaUsuario, null);
		            	
		            	if(nomeArEncrypt != null && !nomeArEncrypt.isEmpty())
		            	{
		            		Date lastModified = new Date(new File(DirNuvem + nomeArEncrypt + ".encrypted").lastModified());
			            	
			            	ultimaModificacaoArqEncrypt.setText(lastModified.toString());
		            	}
		            	else
		            		throw new Exception("Erro ao cifrar arquivo! ");
		            }
		            else
		            {
		            	//Criptografando os diretórios - Na verdade é criado um novo diretório na pasta da nuvem e posteriormente inserido os 
		            	//arquivos nele, todos criptografados.
		            	
		            	//Criptografando o nome do diretório
		            	
		            	File pastatemp;
		            	
		            	if(novosArquivos.get(i).nomeArquivoEncrypt != null && !novosArquivos.get(i).nomeArquivoEncrypt.isEmpty())
	            		{
	            			nomeArEncrypt = novosArquivos.get(i).nomeArquivoEncrypt;
	            			
	            			pastatemp = new File(DirNuvem + nomeArEncrypt);
			            	
			            	if(!pastatemp.exists())
		            		{
			            		String hashNome = HashGeneratorUtils.generateSHA256(novosArquivos.get(i).nomeArquivo.toString());
				            	
			            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
			            	
			            		Base32 codec = new Base32();
							
			            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(novosArquivos.get(i).nomeArquivo.toString(), key);
			            		String nomePastaEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
					        
			            		if(nomePastaEncrypt.length() > 30)
			            			nomeArEncrypt = nomePastaEncrypt.substring(0, 29);
			            		else
			            			nomeArEncrypt = nomePastaEncrypt;
			            		
			            		pastatemp = new File(DirNuvem + nomeArEncrypt);
			            		
			            		if(!pastatemp.exists())
			            			pastatemp.mkdir();
			            		
			            		FileWriter arqChaveArquivo = new FileWriter(DirNuvem + nomeArEncrypt + "/keyFolder.encrypted"); 
								PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
								
								String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
								
								gravarArqChaveArquivo.println("chave="+keyCifrada);
								gravarArqChaveArquivo.println("nomePastaEncrypt="+nomePastaEncrypt);
								
								arqChaveArquivo.close();
								gravarArqChaveArquivo.close();
		            		}
			            	
		            	}
		            	else
		            	{
		            		String hashNome = HashGeneratorUtils.generateSHA256(novosArquivos.get(i).nomeArquivo.toString());
		            	
		            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
		            	
		            		Base32 codec = new Base32();
						
		            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(novosArquivos.get(i).nomeArquivo.toString(), key);
		            		String nomePastaEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
				        
		            		if(nomePastaEncrypt.length() > 30)
		            			nomeArEncrypt = nomePastaEncrypt.substring(0, 29);
		            		else
		            			nomeArEncrypt = nomePastaEncrypt;
		            		
		            		pastatemp = new File(DirNuvem + nomeArEncrypt);
			            	
			            	if(!pastatemp.exists())
			            		pastatemp.mkdir();
			            	
			            	FileWriter arqChaveArquivo = new FileWriter(DirNuvem + nomeArEncrypt + "/keyFolder.encrypted"); 
							PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
							
							String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
							
							gravarArqChaveArquivo.println("chave="+keyCifrada);
							gravarArqChaveArquivo.println("nomePastaEncrypt="+nomePastaEncrypt);
							
							arqChaveArquivo.close();
							gravarArqChaveArquivo.close();
		            	}
		            	
		            	
		            			            	
		            	Date lastModified = new Date(pastatemp.lastModified());
		            	ultimaModificacaoArqEncrypt.setText(lastModified.toString());
		            	
		            	
		            	
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo.replace(' ', '-') + "-config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt +"/config.xml";
		            	String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt + "-config.xml";
		            	
		            	//Config-files/config.xml
		            			            	
		            	//Criando uma lista para armazenar os diretórios encontrados na pasta sendo analisada
		            	//Posteriormente, estes diretórios serão analisados em busca de arquivos para criptografá-los e armazená-los
		            	//em uma pasta equivalemte na pasta da nuvem
		            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
		            	
		            	
		            	String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo + "/config.xml";
		            	
		            	diretorio.setAttribute("pathArqConfig", novoPathArqConf);
		            	
		            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + novosArquivos.get(i).nomeArquivo + "/";
		            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
		            	arqDir.DirArquivoConfig = novoPathArqConf;
		            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
		            			            			            	
		            	arquivosAddOutrosDir.add(arqDir);
		            }	            
		            
		            arq.addContent(ultimaModificacaoArqEncrypt);
		            arq.addContent(diretorio);
		            
		            
		            //CONFIRMAR
		            
		            pathArquivoEncrypt.setText(DirNuvem + nomeArEncrypt);
		            nomeArquivoEncrypt.setText(nomeArEncrypt);
		            
		            
		            
		            arq.addContent(nomeArquivoEncrypt);
		            arq.addContent(pathArquivoEncrypt);
		            
		            
		            //
		            
		            
		            //Adicionado o arquivo a Arquivos		
		            arquivos.addContent(arq);		            
		        }
		        
		        //Classe responsável para imprimir / gerar o XML
		        XMLOutputter xout = new XMLOutputter();

	            //Imprimindo o XML no arquivo
	            xout.output(documento, fw);
	        }
        	
        	if(arquivoConfigAlterado)
        	{
        		File arquivoConfigEncrypted = new File(pathArquivoConfigEncrypted);
        		
        		//Criptografar arquivo de configurações e enviá-lo para a pasta da nuvem...
        		CriptoUtil.CriptografarArquivo(arquivoConfig, arquivoConfigEncrypted, senhaUsuario, arquivoConfig.getName());
        	}
        	
        	//Deletando arquivo config...
    		//arquivoConfig.delete();
        
        	//Verificando as pastas do diretório sendo analizado para que os arquivos presentes nela possam ser encriptografados
        	if(arquivosAddOutrosDir != null && arquivosAddOutrosDir.size() > 0)
        	{
        		for(int i = 0; i < arquivosAddOutrosDir.size(); i++)
        		{
        			//Processo recursivo de busca por arquivos para encriptação
        			VerificarNovosArquivos(arquivosAddOutrosDir.get(i).DirArquivosSecretosUser, arquivosAddOutrosDir.get(i).DirArquivosNuvem, arquivosAddOutrosDir.get(i).DirArquivoConfig, arquivosAddOutrosDir.get(i).DirArquivoConfigEncrypted, senhaUsuario);
        		}
        	}        	
        } 
        catch (IOException e) 
        {
        	throw new Exception("Erro na escrita no arquivo de configuração do sistema! " + e.getMessage());
    	}
		return null;  
	}
	
	public static void EscritaArqDecifrar(List<Arquivos> arquivosDecifrar, String pathArquivoConfig, String pathArquivoConfigEncrypted, String DirArquivosSecretosUser, String DirNuvem, String senhaUsuario) throws Exception
	{
		try 
        {
			boolean arquivoConfigAlterado = false;
			
			String pathArquivoConfigTemp = AtualizarArquivoConfig(pathArquivoConfig, pathArquivoConfigEncrypted, senhaUsuario);
			
        	File arquivoConfig = new File(pathArquivoConfig);
			
			//Arquivo de configuração, responsável por armazenar as informações dos arquivos (nome, caminho, se é diretório ou não etc)
        	//File arquivoConfig = new File(pathArquivoConfigTemp);
        	
        	//Arquivos a serem decifrados e enviados para a pasta do usuário
        	List<Arquivos> novosArquivos = new ArrayList<Arquivos>();
        	
        	//Arquivos que já foram decifrados e se encontram na pasta do usuário
        	List<Arquivos> antigosArquivos = new ArrayList<Arquivos>();
        	
        	//Lista de caminhos dos diretórios que devem ter seus arquivos decifrados e enviados para a pasta do usuário
        	List<DiretoriosDosArquivos> arquivosDecifrarOutrosDir = new ArrayList<DiretoriosDosArquivos>();
        	
        	if(!arquivoConfig.exists())
        	{
        		try 
				{
        			arquivoConfig.createNewFile();
        			
        			arquivoConfigAlterado = true;
					
					novosArquivos.addAll(arquivosDecifrar);
					antigosArquivos = null;
				} 
				catch (IOException e) 
				{
					novosArquivos = null;
					antigosArquivos = null;
					
					e.printStackTrace();
				}
        	}
        	else
        	{
        		try 
        		{
        			//Faz a leitura dos arquivos presentes no arquivo de configuração e armazena suas infor. em "arq"
					Leitura(pathArquivoConfigTemp, false);
					
					//Adiciona todos arquivos presentes no diretório da nuvem em uma nova lista para manipulação
					novosArquivos.addAll(arquivosDecifrar);
										
					//Verifica quais são os arquivos que presentes no diretório da nuvem que não foram decifrados e armazenados na pasta do usuário
					for(int i = 0; i< arquivosDecifrar.size(); i++)
					{
						String name1;
						
						int ind = arquivosDecifrar.get(i).nomeArquivoEncrypt.indexOf(".encrypted");
						
						//if(!arquivosDecifrar.get(i).diretorio)
						if(ind > 0)
							name1 = arquivosDecifrar.get(i).nomeArquivoEncrypt.substring(0, ind);
						else
							name1 = arquivosDecifrar.get(i).nomeArquivoEncrypt;
						
						for(int j = 0; j< arq.arquivosList.size(); j++)
						{		
							String name2 = arq.arquivosList.get(j).nomeArquivoEncrypt;
							
							if(name1.equals(name2))
							{
								int index= -1;
								
								for(int m=0;m<novosArquivos.size();m++)
								{
									if(!novosArquivos.get(m).diretorio)
									{
										if(novosArquivos.get(m).nomeArquivoEncrypt.equals(name1 + ".encrypted"))
										{
											index = m;
											break;
										}
									}
									else
									{
										if(novosArquivos.get(m).nomeArquivoEncrypt.equals(name1))
										{
											index = m;
											break;
										}
									}									
								}
								
								if(index >= 0)
								{
									if(arquivosDecifrar.get(i).diretorio)
									{
										String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										
										//String novoPathArqConf = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
										String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + arq.arquivosList.get(j).nomeArquivo + "/config.xml";
										
										String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
						            	
						            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
						            	
						            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arq.arquivosList.get(j).nomeArquivo + "/";
						            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
						            	arqDir.DirArquivoConfig = novoPathArqConf;
						            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
						            	
						            	arquivosDecifrarOutrosDir.add(arqDir);
						            	
									}
									
									//Verifica a última data/horário de modificação do arquivo. Se for o mesmo, o arquivo não precisa ser encriptado
									//Se a data do arquivo da pasta do usuário for mais recente que a data do arquivo cifrado na nuvem, então ele precisa ser encriptado
									if(arquivosDecifrar.get(i).dataUltimaModArqEncrypt.compareTo(arq.arquivosList.get(j).dataUltimaModArqEncrypt) <= 0 || arquivosDecifrar.get(i).diretorio)
									{
										novosArquivos.get(index).dataUltimaMod = arq.arquivosList.get(j).dataUltimaMod;										
										novosArquivos.get(index).nomeArquivo = arq.arquivosList.get(j).nomeArquivo;
										novosArquivos.get(index).pathArquivo = arq.arquivosList.get(j).pathArquivo;
										
										antigosArquivos.add(novosArquivos.get(index));
										novosArquivos.remove(index);
									}
									else
									{
										novosArquivos.get(index).nomeArquivo = arq.arquivosList.get(j).nomeArquivo;
										novosArquivos.get(index).pathArquivo = arq.arquivosList.get(j).pathArquivo;
										novosArquivos.get(index).dataUltimaMod = arq.arquivosList.get(j).dataUltimaMod;
									}
								}
								
								arq.arquivosList.remove(j);
								j--;
								break;								
							}							
						}
					}
				} 
        		catch (ParseException e) 
        		{
        			e.printStackTrace();
				}
        	}     
        	
        	
        	/*
        	//Adiciando possíveis arquivos que ainda não foram enviados para a nuvem no momento da decifragem, para não perdê-los...
        	if(arq.arquivosList != null && arq.arquivosList.size() > 0)
        	{
        		antigosArquivos.addAll(arq.arquivosList);
        	}
        	*/
        	
        	
        	//Arquivos presentes no diretório do usuário e no arquivo de configuração, mas que não apresentam uma versão cifrada na nuvem
        	//Provavelmente são arquivos que foram excluídos em outros dispositivos.
        	//Deve-se excluídos e apagá-los do arquivo de configuração
        	if(arq.arquivosList != null && arq.arquivosList.size() > 0)
        	{
        		arquivoConfigAlterado = true;
        		
        		//antigosArquivos.addAll(arq.arquivosList);
        		
        		//Se não houver a necessidade de criptografar nenhum arquivo na iteração em que há arquivos para serem deletados,
        		//deve-se criar o arquivo de config novamente, uma vez que quando um arquivo for deletado, ele também deve ser excluído 
        		//do arquivo de configuração
        		if(novosArquivos == null || novosArquivos.size() == 0)
        		{
	        		if(antigosArquivos != null && antigosArquivos.size() != 0)
	        		{
	        			//Escrevendo no arquivo de configuração os dados dos arquivos 
	    				FileWriter fw = new FileWriter(arquivoConfig, false);
		    			Element arquivos = new Element("Arquivos");
		    			
		    			//Define Agenda como root
		    	        Document documento = new Document(arquivos);
		    	        
	    				for(int i=0; i < antigosArquivos.size(); i++)
	            		{
	    					//Cria o elemento Arquivo
	    		            Element arq = new Element("Arquivo");
	    		            
	    	        		//Adiciona o atributo id ao arquivo
	    		            arq.setAttribute("id",Integer.toString(i));
	    		
	    		            //Criando os elementos do arquivo
	    		
	    		            Element nomeArquivo = new Element("nomeArquivo");
	    		            nomeArquivo.setText(antigosArquivos.get(i).nomeArquivo);
	    		
	    		            Element pathArquivo = new Element("path");
	    		            pathArquivo.setText(antigosArquivos.get(i).pathArquivo);
	    		
	    		            Element ultimaModificacao = new Element("ultimaModificacao");
	    		            ultimaModificacao.setText(antigosArquivos.get(i).dataUltimaMod.toString());
	    		
	    		            Element diretorio = new Element("diretorio");
	    		            diretorio.setText(antigosArquivos.get(i).diretorio == true ? "true" : "false");
	    		            
	    		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
	    		            ultimaModificacaoArqEncrypt.setText(antigosArquivos.get(i).dataUltimaModArqEncrypt.toString());
	    		            
	    		            
	    		            //Confirmar
	    		            
	    		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
	    		            nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.toString());
	    		            
	    		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
	    		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.toString());
	    		            
	    		            
	    		            
	    		            
	    		            //
	    		            
	    		                    		
	    		            //Adicionando elementos no arquivo        		
	    		            arq.addContent(nomeArquivo);        		
	    		            arq.addContent(pathArquivo);        		
	    		            arq.addContent(ultimaModificacao);  
	    		            arq.addContent(ultimaModificacaoArqEncrypt);
	    		            
	    		            if(!antigosArquivos.get(i).diretorio)
	    		            	diretorio.setAttribute("pathArqConfig", "");        			            
	    		            else
	    		            	diretorio.setAttribute("pathArqConfig", antigosArquivos.get(i).pathArqEstados);        		            	
	    		                    		
	    		            arq.addContent(diretorio);
	    		            arq.addContent(nomeArquivoEncrypt);
	    		            arq.addContent(pathArquivoEncrypt);
	    		
	    		            //Adicionado o arquivo a Arquivos        		
	    		            arquivos.addContent(arq);        					
	            		}
	    				
	    				//Classe responsável para imprimir / gerar o XML
				        XMLOutputter xout = new XMLOutputter();
	
			            //Imprimindo o XML no arquivo
			            xout.output(documento, fw);
	        		}
	        		else 
	    			{
	    				FileWriter fw = new FileWriter(arquivoConfig, false);
		    			Element arquivos = new Element("Arquivos");
		    			
		    			//Define Agenda como root
		    	        Document documento = new Document(arquivos);
		    	        
		    	        //Classe responsável para imprimir / gerar o XML
				        XMLOutputter xout = new XMLOutputter();
	
			            //Imprimindo o XML no arquivo
			            xout.output(documento, fw);   	 
	    			}   
        		
        		}
        		        		
        		//Varrendo a lista de arquivos para buscar e deletar os arquivos presentes nela
        		for(int i=0; i < arq.arquivosList.size(); i++)
        		{
        			//Deletando arquivo em claro da pasta da aplicação
        			if(!arq.arquivosList.get(i).diretorio)
        			{
        				File tempFile = new File(DirArquivosSecretosUser + arq.arquivosList.get(i).nomeArquivo);
            			
        				if(tempFile != null && tempFile.exists());
        					tempFile.delete();
        			}
        			else
        			{
        				//Se for diretório, primeiro deve-se deletar todos os seus arquivos para que então o diretório seja deletado
        				File tempFile = new File(DirArquivosSecretosUser + arq.arquivosList.get(i).nomeArquivo);
            			
        				if(tempFile != null && tempFile.exists());
        				{
        					RemoverArquivos(tempFile);
        				}        					
        				
        				tempFile.delete();
        				
        				//Deletando o arquivo de configuração pertinente ao diretório sendo deletado
        				int indArquivo = arq.arquivosList.get(i).pathArqEstados.lastIndexOf('/');
        				String arquivo = "";
        				
        				if(indArquivo > 0)
        					arquivo = arq.arquivosList.get(i).pathArqEstados.substring(0, indArquivo);
        				else
        					arquivo = arq.arquivosList.get(i).pathArqEstados;
        				
        				File tempFileConfig = new File(arquivo);     
        				
        				if(tempFileConfig != null && tempFileConfig.exists())
        					RemoverArquivos(tempFileConfig);
        			}        			
        		} 
        		
    		}
        	
        	//Arquivos que ainda não foram decifrados serão neste momento
        	if(novosArquivos != null && novosArquivos.size() > 0)
	        {
        		arquivoConfigAlterado = true;
        		
        		//Escrevendo no arquivo de configuração os dados dos arquivos
        		FileWriter fw = new FileWriter(arquivoConfig, false);
    			Element arquivos = new Element("Arquivos");
    			
    			//Define Arquivos como root
    	        Document documento = new Document(arquivos);
    	                  
    	        //contador necessário para controlar o ID. 
    	        //Aqui são escritos no arquivo de configuração os arquivos que já se encontram decifrados na pasta do usuário
    	        //O contador será utilizado no passo seguinte, onde os novos arquivos a serem decifrados devem continuar do número parado neste loop
	            int contId = 0;
    	        
	            //Escrevendo no arquivo de configuração os dados dos arquivos que já estão decifrados e armazenados na pasta do usuário
    	        if(antigosArquivos != null && antigosArquivos.size() > 0)
    	        {
    	        	for(int i=0; i < antigosArquivos.size(); i++)
    	        	{
    	        		//Cria o elemento Arquivo
    		            Element arq = new Element("Arquivo");
    		            
    	        		//Adiciona o atributo id ao arquivo
    		            arq.setAttribute("id",Integer.toString(i));
    		            contId++;
    		
    		            //Criando os elementos do arquivo
    		            Element nomeArquivo = new Element("nomeArquivo");
    		            
    		            Element pathArquivo = new Element("path");
    		            
    		            Element ultimaModificacao = new Element("ultimaModificacao");
    		            ultimaModificacao.setText(antigosArquivos.get(i).dataUltimaMod.toString());
    		            
    		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
    		            ultimaModificacaoArqEncrypt.setText(antigosArquivos.get(i).dataUltimaModArqEncrypt.toString());
    		
    		            Element diretorio = new Element("diretorio");
    		            diretorio.setText(antigosArquivos.get(i).diretorio == true ? "true" : "false");
    		            
    		            
    		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
    		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
    		            
    		            int indNome = antigosArquivos.get(i).nomeArquivoEncrypt.indexOf(".encrypted");
    		            int indPath = antigosArquivos.get(i).pathArquivoEncrypt.indexOf(".encrypted");
						
						//if(!arquivosDecifrar.get(i).diretorio)
						if(indNome > 0)
							nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.substring(0, indNome));
						else
							nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt);
						
						if(indPath > 0)
							pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.substring(0, indPath));
						else
							pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt);
						
						
    		            /*
    		            if(!antigosArquivos.get(i).diretorio)
    		            {
    		            	nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt.substring(0, ind));
        		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt.substring(0, antigosArquivos.get(i).pathArquivoEncrypt.indexOf(".encrypted")));
    		            }
    		            else
    		            {
    		            	nomeArquivoEncrypt.setText(antigosArquivos.get(i).nomeArquivoEncrypt);
        		            pathArquivoEncrypt.setText(antigosArquivos.get(i).pathArquivoEncrypt);	
    		            }
    		            */
    		            
    		            //Pasta ou diretório tem um tratamento diferente
    		            //Seus nomes não possuem a extensão .encrypted no fim
    		            if(!antigosArquivos.get(i).diretorio)
    		            {
    		            	//Obtendo o nome original do arquivo
    		            	//Necessário quando o nome do arquivo for encriptado
    		            	//String nomeArquivoOriginal = antigosArquivos.get(i).nomeArquivo.substring(0, antigosArquivos.get(i).nomeArquivo.indexOf(".encrypted"));
    		            	String nomeArquivoOriginal = antigosArquivos.get(i).nomeArquivo;
    		            	
        		            nomeArquivo.setText(nomeArquivoOriginal);
        		            
        		            //Obtendo o caminho do arquivo na pasta da nuvem e o trocando por um equivalente na pasta do usuário
        		            //String pathEncrypted = antigosArquivos.get(i).pathArquivo.replace(DirNuvem, DirArquivosSecretosUser);
        		            
        		            //String path = pathEncrypted.substring(0, pathEncrypted.indexOf(".encrypted"));
        		            
        		            String path= antigosArquivos.get(i).pathArquivo.replace(DirNuvem, DirArquivosSecretosUser);
        		            
        		            pathArquivo.setText(path);
        		            
        		            //Caminho do arquivo de configuração da pasta
        		            //Cada pasta tem um arquivo de configuração diferente
        		            diretorio.setAttribute("pathArqConfig", ""); 
    		            }
    		            else
    		            {
    		            	//Pastas/Diretórios
    		            	
    		            	nomeArquivo.setText(antigosArquivos.get(i).nomeArquivo);
        		            
    		            	//Obtendo o caminho da pasta na pasta da nuvem e o trocando por um equivalente na pasta do usuário
        		            String path = antigosArquivos.get(i).pathArquivo.replace(DirNuvem, DirArquivosSecretosUser);
        		            
        		            pathArquivo.setText(path);
        		            
        		            //Caminho do arquivo de configuração da pasta
        		            //Cada pasta tem um arquivo de configuração diferente
        		            diretorio.setAttribute("pathArqConfig", antigosArquivos.get(i).pathArqEstados);        	
    		            }		
    		            
    		            //Adicionando elementos no arquivo
    		
    		            arq.addContent(nomeArquivo);    		
    		            arq.addContent(pathArquivo);    		
    		            arq.addContent(ultimaModificacao);
    		            arq.addContent(ultimaModificacaoArqEncrypt);
    		            arq.addContent(diretorio);
    		            arq.addContent(nomeArquivoEncrypt);
    		            arq.addContent(pathArquivoEncrypt);
    		                		            
    		            //Adicionado o arquivo a Arquivos
    		
    		            arquivos.addContent(arq);
    	        	}
    	        }
    	        
    	        //Decifrando os arquivos que ainda não se encontram na pasta do usuário em formato em claro
		        for(int i = 0; i < novosArquivos.size(); i++)
		        {
		        	//Cria o elemento Arquivo
		            Element arq = new Element("Arquivo");
		            
		        	//Adiciona o atributo id ao arquivo
		            arq.setAttribute("id",Integer.toString(i + contId));
		
		            //Criando os elementos do arquivo		
		            
		            Element nomeArquivo = new Element("nomeArquivo");		            
		            Element pathArquivo = new Element("path");	
		            Element ultimaModificacao = new Element("ultimaModificacao");
		            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
		            ultimaModificacaoArqEncrypt.setText(novosArquivos.get(i).dataUltimaModArqEncrypt.toString());
		            Element diretorio = new Element("diretorio");
		            diretorio.setText(novosArquivos.get(i).diretorio == true ? "true" : "false");
		            
		            
		          //Conferir
		            
		            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
		            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
		            
		            int indNome = novosArquivos.get(i).nomeArquivoEncrypt.indexOf(".encrypted");
		            int indPath = novosArquivos.get(i).pathArquivoEncrypt.indexOf(".encrypted");
		            
		            if(indNome > 0)
						nomeArquivoEncrypt.setText(novosArquivos.get(i).nomeArquivoEncrypt.substring(0, indNome));
					else
						nomeArquivoEncrypt.setText(novosArquivos.get(i).nomeArquivoEncrypt);
					
					if(indPath > 0)
						pathArquivoEncrypt.setText(novosArquivos.get(i).pathArquivoEncrypt.substring(0, indPath));
					else
						pathArquivoEncrypt.setText(novosArquivos.get(i).pathArquivoEncrypt);
					
					
					/*
		            if(!novosArquivos.get(i).diretorio)
		            {
		            	nomeArquivoEncrypt.setText(novosArquivos.get(i).nomeArquivoEncrypt.substring(0, novosArquivos.get(i).nomeArquivoEncrypt.indexOf(".encrypted")));
			            pathArquivoEncrypt.setText(novosArquivos.get(i).pathArquivoEncrypt.substring(0, novosArquivos.get(i).pathArquivoEncrypt.indexOf(".encrypted")));
		            }
		            else
		            {
		            	nomeArquivoEncrypt.setText(novosArquivos.get(i).nomeArquivoEncrypt);
			            pathArquivoEncrypt.setText(novosArquivos.get(i).pathArquivoEncrypt);	
		            }
		            */
		            //
		            
		            
		            String nomeArquivoOriginal = "";		            

		            if(!novosArquivos.get(i).diretorio)
		            {
		            	//nomeArquivoOriginal = novosArquivos.get(i).nomeArquivo.substring(0, novosArquivos.get(i).nomeArquivo.indexOf(".encrypted"));
    		            
    		            //nomeArquivo.setText(nomeArquivoOriginal);
    		            
    		            String pathEncrypted = novosArquivos.get(i).pathArquivoEncrypt.replace(DirNuvem, DirArquivosSecretosUser);
    		            
    		            String path = pathEncrypted.substring(0, pathEncrypted.indexOf(novosArquivos.get(i).nomeArquivoEncrypt));
    		            
    		            //Caminho do arquivo de configuração da pasta
    		            //Cada pasta tem um arquivo de configuração diferente
    		            diretorio.setAttribute("pathArqConfig", "");
    		            
    		            
    		            String[] ret = new String[2];
    		            
    		            
    		            //Decifrando o arquivo - APENAS ARQUIVOS
    		            if(novosArquivos.get(i).nomeArquivo != null && !novosArquivos.get(i).nomeArquivo.isEmpty())
    		            {
    		            	nomeArquivoOriginal = novosArquivos.get(i).nomeArquivo;
    		            	
		            		CriptoUtil.DescriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirArquivosSecretosUser + nomeArquivoOriginal), senhaUsuario, novosArquivos.get(i).nomeArquivo);
	            		}
    		            else
		            	{
    		            	ret = CriptoUtil.DescriptografarArquivo(new File(novosArquivos.get(i).pathArquivoEncrypt), new File(DirArquivosSecretosUser + novosArquivos.get(i).nomeArquivoEncrypt), senhaUsuario, null);
    		            	
    		            	nomeArquivoOriginal = ret[1];
		            	}
    		            
    		            
    		            
    		            //Data da última modificação do arquivo
    		            Date lastModified = new Date(new File(DirArquivosSecretosUser + nomeArquivoOriginal).lastModified());
		            	ultimaModificacao.setText(lastModified.toString());
		            	
		            	pathArquivo.setText(path + nomeArquivoOriginal);
		            }
		            else
		            {
		            	//Diretórios/Patas
		            	
		            	//nomeArquivoOriginal = novosArquivos.get(i).nomeArquivo;
		            	
		            	//nomeArquivo.setText(nomeArquivoOriginal);
		            	
		            	
		            	
		            	File pastatemp;
		            	
		            	if(novosArquivos.get(i).nomeArquivo != null && !novosArquivos.get(i).nomeArquivo.isEmpty())
	            		{
		            		nomeArquivoOriginal = novosArquivos.get(i).nomeArquivo;
	            			
	            			pastatemp = new File(DirArquivosSecretosUser + nomeArquivoOriginal);
			            	
			            	if(!pastatemp.exists())
		            		{
			            		Base32 codec = new Base32();
			            		
			            		FileReader arqNome = new FileReader(novosArquivos.get(i).pathArquivoEncrypt + "/keyFolder.encrypted");
								BufferedReader lerArqNome = new BufferedReader(arqNome);
					        	      
					        	String linhaNome = lerArqNome.readLine(); // lê a primeira linha
								// a variável "linha" recebe o valor "null" quando o processo 
								// de repetição atingir o final do arquivo texto
					        	
								String chaveCifrada = "";
								String nomeCifrado = "";
								
								while (linhaNome != null) 
								{ 
									String[] indChave = linhaNome.split("chave=");
									String[] indNomeCifrado = linhaNome.split("nomePastaEncrypt=");
									
									if(indChave.length > 1)
										chaveCifrada = indChave[1].toString();
									else if(indNomeCifrado.length > 1)
										nomeCifrado = indNomeCifrado[1].toString();
									
									linhaNome = lerArqNome.readLine(); // lê da segunda até a última linha
								} 
			
								arqNome.close();
								lerArqNome.close();

								
								Key chaveDecifrada = CriptoUtil.DescriptografarChaveSimetrica(chaveCifrada, senhaUsuario);
								
								nomeArquivoOriginal = CriptoSimetrica.DesencriptarMensagem(codec.decode(nomeCifrado), (SecretKey)chaveDecifrada);
								
		            		}
			            	
		            	}
		            	else
	            		{
		            		Base32 codec = new Base32();
		            		
		            		FileReader arqNome = new FileReader(novosArquivos.get(i).pathArquivoEncrypt + "/keyFolder.encrypted");
							BufferedReader lerArqNome = new BufferedReader(arqNome);
				        	      
				        	String linhaNome = lerArqNome.readLine(); // lê a primeira linha
							// a variável "linha" recebe o valor "null" quando o processo 
							// de repetição atingir o final do arquivo texto
				        	
							String chaveCifrada = "";
							String nomeCifrado = "";
							
							while (linhaNome != null) 
							{ 
								String[] indChave = linhaNome.split("chave=");
								String[] indNomeCifrado = linhaNome.split("nomePastaEncrypt=");
								
								if(indChave.length > 1)
									chaveCifrada = indChave[1].toString();
								else if(indNomeCifrado.length > 1)
									nomeCifrado = indNomeCifrado[1].toString();
								
								linhaNome = lerArqNome.readLine(); // lê da segunda até a última linha
							} 
		
							arqNome.close();
							lerArqNome.close();

							Key chaveDecifrada = CriptoUtil.DescriptografarChaveSimetrica(chaveCifrada, senhaUsuario);
							
							nomeArquivoOriginal = CriptoSimetrica.DesencriptarMensagem(codec.decode(nomeCifrado), (SecretKey)chaveDecifrada);
						}
		            	
	            		//nomeArquivoOriginal = CriptoUtil.DescriptografarMensagemAlgAssimetrico(novosArquivos.get(i).nomeArquivoEncrypt, senhaUsuario);
    		            
    		            String pathEncrypted = novosArquivos.get(i).pathArquivoEncrypt.replace(DirNuvem, DirArquivosSecretosUser);
    		            
    		            String path = pathEncrypted.substring(0, pathEncrypted.indexOf(novosArquivos.get(i).nomeArquivoEncrypt));
    		            
    		            pathArquivo.setText(path + nomeArquivoOriginal);
    		            
    		            //Decifrando os diretórios
		            	//File pastatemp = new File(DirNuvem + novosArquivos.get(i).nomeArquivo);
    		            pastatemp = new File(path + nomeArquivoOriginal);
		            	
		            	if(!pastatemp.exists())
		            	{
		            		pastatemp.mkdir();
		            	}
		            	
		            	//Data da última modificação da pasta
		            	Date lastModified = new Date(pastatemp.lastModified());
		            	ultimaModificacao.setText(lastModified.toString());
		            	
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo.replace(' ', '-') + "-config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivoEncrypt +"-config.xml";
		            	
		            	String nomeArEncrypt = novosArquivos.get(i).nomeArquivoEncrypt;
						//String novoPathArqConf = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
		            	String novoPathArqConf  = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo + "/config.xml";
		            	
		            	String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
		            	
		            	diretorio.setAttribute("pathArqConfig", novoPathArqConf);
		            	
		            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
		            	
		            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + nomeArquivoOriginal + "/";
		            	//arqDir.DirArquivosNuvem = DirNuvem + novosArquivos.get(i).nomeArquivoEncrypt + "/";
		            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
		            	arqDir.DirArquivoConfig = novoPathArqConf;
		            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
		            	
		            	arquivosDecifrarOutrosDir.add(arqDir);	
		            	
		            }
		            
		            nomeArquivo.setText(nomeArquivoOriginal);
		            
		            
		
		            //Adicionando elementos no arquivo
		
		            arq.addContent(nomeArquivo);
		            arq.addContent(pathArquivo);
		            arq.addContent(ultimaModificacao);
		            arq.addContent(ultimaModificacaoArqEncrypt);
		            arq.addContent(diretorio);
		            arq.addContent(nomeArquivoEncrypt);
		            arq.addContent(pathArquivoEncrypt);
		
		            //Adicionado o arquivo a Arquivos
		
		            arquivos.addContent(arq);		      
		            
		        }
		        
		        //Classe responsável para imprimir / gerar o XML
		        XMLOutputter xout = new XMLOutputter();

	            //Imprimindo o XML no arquivo
	            xout.output(documento, fw);
	        }
        	
        	if(arquivoConfigAlterado)
        	{
        		File arquivoConfigEncrypted = new File(pathArquivoConfigEncrypted);
        		
        		//Criptografar arquivo de configurações e enviá-lo para a pasta da nuvem...
        		CriptoUtil.CriptografarArquivo(arquivoConfig, arquivoConfigEncrypted, senhaUsuario, arquivoConfigEncrypted.getName());
        		
        		//Deletando arquivo config...
        		arquivoConfig.delete();
        	}
        	
        	if(arquivosDecifrarOutrosDir != null && arquivosDecifrarOutrosDir.size() > 0)
        	{
        		for(int i = 0; i < arquivosDecifrarOutrosDir.size(); i++)
        		{
        			VerificarArquivosParaDecifrar(arquivosDecifrarOutrosDir.get(i).DirArquivosSecretosUser, arquivosDecifrarOutrosDir.get(i).DirArquivosNuvem, arquivosDecifrarOutrosDir.get(i).DirArquivoConfig, arquivosDecifrarOutrosDir.get(i).DirArquivoConfigEncrypted, senhaUsuario);
        		}
        	}
            
        } 
        catch (IOException e) 
        {
        	throw new Exception("Erro na escrita no arquivo de configuração do sistema! " + e.getMessage());
    	}  
	}	

	public static Date Leitura(String inputPath, boolean cifrar) throws Exception
	{
		Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        
        try
        {
        	if(new File(inputPath).exists())
        		doc = builder.build(inputPath);
        	else
        		throw new Exception("Erro na leitura do arquivo de configuração do sistema! Não foi possível localizá-lo");
        }
        catch(Exception e)
        {
        	throw new Exception("Erro na leitura do arquivo de configuração do sistema! " + e.getMessage());
        }
        
        Element arquivos = doc.getRootElement();
        
        List<Element> lista = arquivos.getChildren();
        
        arq = new Arquivos();
        
        for(Element e:lista)
        {
        	//e.getAttribute("dataInsercaoRegistro");
        	//String data1 = "Thu Jul 30 14:25:42 BRT 2015";
			Date data = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US).parse(e.getChildText("ultimaModificacao"));
			
			Date dataArqEncrypt = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US).parse(e.getChildText("ultimaModificacaoArqEncrypt"));
			
			Date dataInsercaoRegistro = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US).parse(e.getAttribute("dataInsercaoRegistro").getValue());
			
			String nomeArquivo = e.getChildText("nomeArquivo");        	
        	String pathArqConfig = e.getChild("diretorio").getAttributeValue("pathArqConfig");
        	        	
        	//arq.AdicionarNovoRegistro(nomeArquivo, e.getChildText("path"), data, e.getChildText("diretorio").equals("true") ? true : false, pathArqConfig, dataArqEncrypt, e.getChildText("nomeArquivoEncrypt"), e.getChildText("pathArquivoEncrypt"), cifrar, dataInsercaoRegistro, null);
        }
        
        return new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US).parse(arquivos.getAttribute("").getValue());
	}
	
	public static String AtualizarArquivoConfig(String pathArquivoConfig, String pathArquivoConfigEncrypted, String senhaUsuario) throws Exception
	{
		try
		{
			//Decifrar arquivo de configuração presente na nuvem
			
			File arquivoConfigEncrypted = new File(pathArquivoConfigEncrypted);
			String pathArquivoConfigTemp = "";
			
			int ind1 = arquivoConfigEncrypted.getName().lastIndexOf(".encrypted");
			
			if(ind1 > 0)
				pathArquivoConfigTemp = appPastaAplicacao + arquivoConfigEncrypted.getName().substring(0, ind1);
			else
				pathArquivoConfigTemp = appPastaAplicacao + arquivoConfigEncrypted.getName();
			
			new File(pathArquivoConfigTemp).delete();
			
			if(arquivoConfigEncrypted.exists())
			{
				//Decifrar arquivo para a pasta interna da aplicação
				CriptoUtil.DescriptografarArquivo(arquivoConfigEncrypted, new File(pathArquivoConfigTemp), senhaUsuario, arquivoConfigEncrypted.getName());
			}
		
			//Verificação do arquivo de configuração da nuvem com o arquivo de configuração local
		
			File arquivoConfigTempLocal = new File(pathArquivoConfig);
			long arquivoConfigTempUltModLocal = arquivoConfigTempLocal.lastModified();
			
			//Arquivo de configuração, responsável por armazenar as informações dos arquivos (nome, caminho, se é diretório ou não etc)
	    	File arquivoConfigDecryptedNuvem = new File(pathArquivoConfigTemp);
	    	long arquivoConfigDecryptedUltModNuvem = arquivoConfigDecryptedNuvem.lastModified();
	    	
	    	if(arquivoConfigTempUltModLocal < arquivoConfigDecryptedUltModNuvem)
	    	{
	    		//Copiar arquivo de configuração para a pasta interna da aplicação Config caso ele seja o mais recente...
	    		FileChannel sourceChannel = null;  
			    FileChannel destinationChannel = null; 
			    
			    sourceChannel = new FileInputStream(arquivoConfigDecryptedNuvem).getChannel();
			    
			    if(!arquivoConfigTempLocal.exists())
			    	if(arquivoConfigTempLocal.isDirectory())
			    		arquivoConfigTempLocal.mkdirs();
			    	else
			    		new File(arquivoConfigTempLocal.getParent()).mkdirs();
			    
			    destinationChannel = new FileOutputStream(pathArquivoConfig).getChannel();  
		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		        
		        if (sourceChannel != null && sourceChannel.isOpen())
		        	sourceChannel.close();  
		        if (destinationChannel != null && destinationChannel.isOpen())
		        	destinationChannel.close();
	    	}
	    	
	    	return pathArquivoConfig;
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao atualizar arquivo de configuração! Erro: " + ex.getMessage());
		}
	}
	
	public static void RemoverArquivos(File arquivo) 
	{
		if (arquivo.isDirectory()) 
		{
	         File[] files = arquivo.listFiles();
	         for (File file : files) 
	         {
	             RemoverArquivos(file);
	         }
	    }
		
		arquivo.delete();
  }
		

}
