package BuscarArquivos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import ClassesGerais.Arquivos;
import ClassesGerais.DiretoriosDosArquivos;
import ClassesGerais.Escritor;
import ClassesGerais.Eventos;
import Criptografia.CriptoSimetrica;
import Criptografia.CriptoUtil;
import Criptografia.HashGeneratorUtils;

public class ManipularArquivos 
{
	private static Arquivos arq = new Arquivos();
	
	private static List<String> eventosGeradosAplicacaoPastaNuvem = new ArrayList<String>();
	private static List<String> eventosGeradosAplicacaoPastaUser = new ArrayList<String>();
	
	public static void TratarEventos(List<Eventos> pathEventos, boolean cifrar, String DirArquivoEstados, String DirArquivosSecretosUser, String DirNuvem, String senhaUsuario, DiretoriosDosArquivos diretorioArquivosList) throws Exception, IOException, RuntimeException, InterruptedException
	{
		try
		{
			if(pathEventos != null && pathEventos.size() > 0)
			{
				//Lista de caminhos dos diretÃ³rios que devem ter seus arquivos criptografados e enviados para a pasta da nuvem
	        	List<DiretoriosDosArquivos> arquivosAddOutrosDir = new ArrayList<DiretoriosDosArquivos>();
				
	        	System.out.println("Tratando eventos (Cifrar)...");
	        	
				for(int i=0; i<pathEventos.size(); i++)
				{
					System.out.println("########################################################");
					
					System.out.println("Evento nÃºmero " + i + "...");
					
					String path = "";
					String arqNome = "";
					boolean dir = false;
					
					//Se o arquivo jÃ¡ estiver presente e nÃ£o necessitar se criptografado novamente
					boolean continuar = true;
					
					String Evento = "";
					
					if(pathEventos.get(i).PathEvent != null && !pathEventos.get(i).PathEvent.isEmpty())
					{
						int ind0 = pathEventos.get(i).PathEvent.lastIndexOf('/') + 1;
						
						if(ind0 > 0)
						{
							path = pathEventos.get(i).PathEvent.substring(0, ind0);
							arqNome = pathEventos.get(i).PathEvent.substring(ind0, pathEventos.get(i).PathEvent.length());
							
							System.out.println("Nome do arquivo: " + arqNome);
							System.out.println("Caminho do arquivo: " + path);
							
							
							//Evento gerado pela aplicaÃ§Ã£o...
							if(arqNome != null && arqNome.startsWith(".goutputstream-"))
							{
								continuar=false;
								System.out.println("Evento gerado pela aplicaÃ§Ã£o, nÃ£o tratar...");
							}
						}
						else
							System.out.println("NÃ£o foi possÃ­vel encontrar o nome e path do arquivo relacionado ao evento");
						
						lock.lock();
						
						if(eventosGeradosAplicacaoPastaUser != null && eventosGeradosAplicacaoPastaUser.size() > 0)
						{
							for(int m=0; m<eventosGeradosAplicacaoPastaUser.size(); m++)
							{
								if(eventosGeradosAplicacaoPastaUser.get(m).equals(pathEventos.get(i).PathEvent))
								{
									System.out.println("Evento gerado pela aplicaÃ§Ã£o. NÃ£o tratar: " + pathEventos.get(i).PathEvent);
									continuar = false;
									break;
								}
							}
						}
						
						lock.unlock();
					}
					else
						continuar = false;					
					
					if(continuar && arqNome != null && !arqNome.isEmpty())
					{						
						if(pathEventos.get(i).KindEvent == null || pathEventos.get(i).KindEvent.isEmpty())
							System.out.println("KIND EVENT igual a null ou vazio.");
						else if(pathEventos.get(i).KindEvent.equals("ENTRY_CREATE"))
						{
							Evento = "CREATE";
							System.out.println("Manipular Arquivos -- > Evento: CREATE");
						}
						else if(pathEventos.get(i).KindEvent.equals("ENTRY_DELETE"))
						{
							Evento = "DELETE";
							System.out.println("Manipular Arquivos -- > Evento: DELETE");
						}
						else if(pathEventos.get(i).KindEvent.equals("ENTRY_MODIFY"))
						{
							Evento = "MODIFY";
							System.out.println("Manipular Arquivos -- > Evento: MODIFY");
						}
						else
							System.out.println("Evento nÃ£o identificado!");						
						
						if(Evento != "DELETE" && new File(pathEventos.get(i).PathEvent).exists() && new File(pathEventos.get(i).PathEvent).isDirectory())
							dir = true;
						
						System.out.println("Ã‰ um diretÃ³rio?: " + dir);
																		
						int ind = pathEventos.get(i).PathEvent.lastIndexOf('/') + 1;
						
						String dirEvento = "";
						
						if(ind > 0)
						{
							dirEvento = pathEventos.get(i).PathEvent.substring(0, ind);
						}
						else
							dirEvento = pathEventos.get(i).PathEvent;
						
						String[] ind2 = dirEvento.split(DirArquivosSecretosUser.toString());
										
						String pathArqEstadosEvento = "";
						
						if(ind2.length > 0)
						{
							int ind3 = DirArquivoEstados.lastIndexOf("/config.xml");
							
							if(ind3 > 0)
							{
								pathArqEstadosEvento = DirArquivoEstados.substring(0, ind3) + "/" + ind2[1].replace(" ", "--") + "config.xml";
							}
							else						
								pathArqEstadosEvento = DirArquivoEstados + ind2[1].replace(" ", "--") + "config.xml";
						}
						else if(ind2.length == 0)
						{
							pathArqEstadosEvento = DirArquivoEstados.replace(" ", "--");
						}
						
						System.out.println("DiretÃ³rio do arquivo de estados do evento: " + pathArqEstadosEvento);
												
						//TRATAR EVENTO...
												
						//AlteraÃ§Ãµes no arquivo de estados
						
						//EVENTO CREATE: 
							//Adicionar novos campos no arquivo de estados
							//Criptografar arquivo
						
						//EVENTO MODIFY: 
							//encontrar e alterar os registros de estados do arquivo em questÃ£o
							//Criptografar arquivo
						
						//EVENTO DELETE: 
							//encontrar e deletar os registros de estados do arquivo em questÃ£o
							//Deletar arquivo criptografado
						
						File arqEstados = new File(pathArqEstadosEvento);
						
						Escritor escritorArqEstados = new Escritor();
						
						String pathDirEncrypt = "";
						
						Arquivos arquivoEvento = new Arquivos();
						
						arquivoEvento.pathArquivo = path + arqNome;
						
						if(Evento != "DELETE")
						{
							//Buscando informaÃ§Ãµes do arquivo
		    	        	
		    	        	File arqTemp = new File(path + arqNome);
		    	        	
		    	        	if(arqTemp.exists())
		    	        	{
		    	        		Date lastModified = new Date(arqTemp.lastModified());
		    	        		
								arquivoEvento.nomeArquivo = arqTemp.getName();
		    	        		arquivoEvento.dataUltimaMod = lastModified;
		    	        		arquivoEvento.diretorio = arqTemp.isDirectory();
		    	        		arquivoEvento.pathArqEstados = arqTemp.getAbsolutePath().substring(0, arqTemp.getAbsolutePath().indexOf(arqTemp.getName()));				    	        		
		    	        	}
		    	        	else
		    	        	{
		    	        		//Caso em que o usuÃ¡rio cria um folder na pasta sendo monitorada. O folder serÃ¡ criado com um nome padÃ£o, mas o usuÃ¡rio altera este nome
		    	        		//Neste caso, serÃ£o gerados 3 eventos: 1 da criaÃ§Ã£o do folder com o nome default, que nÃ£o existirÃ¡ mais, 2 da deleÃ§Ã£o deste folder e o 3 da criaÃ§Ã£o
		    	        		//de um nome folder com o nome escolhido pelo usuÃ¡rio
		    	        		System.out.println("Arquivo/diretÃ³rio nÃ£o existe! Passando para o pÅ•oximo registro...");
		    	        		continuar = false;
		    	        	}
						}
						
						if(continuar && (arqEstados == null || !arqEstados.exists()))
			        	{
							if(Evento != "DELETE")
		        			{
								System.out.println("Arquivo de estados nÃ£o exite. Criando arquivo de estados...");
								
		        				arqEstados = new File(pathArqEstadosEvento);
			        			
			        			new File(arqEstados.getParent()).mkdirs();
			        			
			        			arqEstados.createNewFile();
			        			
			        			pathDirEncrypt = DirNuvem;
			        			        			
			        			escritorArqEstados.IniciarEscritaArquivoEstados(pathArqEstadosEvento, pathDirEncrypt);
				    			
			        			escritorArqEstados.FinalizarEscritaArquivoEstados();
		        			}
		        			else
		        			{
		        				continuar = false;
		        				
		        				System.out.println("Arquivo de estados nÃ£o exite. Encerrando tratamento do evento...");
		        			}
			        	}
			        	else if(continuar)
			        	{
			        		System.out.println("Abrindo arquivo de estados para leitura...");
		        			
		        			//Faz a leitura dos arquivos presentes no arquivo de estados e armazena suas infor. em "arq"
							pathDirEncrypt = Leitura(pathArqEstadosEvento, true);
							
							System.out.println("Arquivo lido...");
							
							if(Evento == "DELETE" && (arq == null || arq.arquivosList == null || arq.arquivosList.size() == 0))
								continuar = false;
							
							else if(arq != null)
							{
								//Verifica se o arquivo em questÃ£o existe no arquivo de estados
								for(int j = 0; j< arq.arquivosList.size(); j++)
								{
									String path2 = arq.arquivosList.get(j).pathArquivo;
									
									if(pathEventos.get(i).PathEvent.equals(path2))
									{
										if(Evento != "DELETE" && arquivoEvento.dataUltimaMod.compareTo(arq.arquivosList.get(j).dataUltimaMod) <= 0)
										{
											continuar = false;
											break;
										}
										else if(Evento.equals("DELETE"))
											dir = arq.arquivosList.get(j).diretorio;
														
										//Ã‰ diretÃ³rio?
										if(dir)
										{
											String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
											
											if(nomeArEncrypt.length() > 30)
											{
												nomeArEncrypt = nomeArEncrypt.substring(0, 29);
											}
											
											arquivoEvento.diretorio = true;
											arquivoEvento.dataUltimaModArqEncrypt = arq.arquivosList.get(j).dataUltimaModArqEncrypt;
											arquivoEvento.nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
											
											arquivoEvento.pathArquivoEncrypt = pathDirEncrypt + arquivoEvento.nomeArquivoEncrypt; // + "/config.xml.encrypted";
											
											arquivoEvento.nomeArquivo = arqNome;
											
											String nomeArquivoSemEspacos = arquivoEvento.nomeArquivo.replace(" ", "--"); 
											
											arquivoEvento.pathArqEstados = pathArqEstadosEvento.substring(0, pathArqEstadosEvento.lastIndexOf('/') + 1) + nomeArquivoSemEspacos + "/config.xml";
											
											//Criando uma lista para armazenar os diretÃ³rios encontrados na pasta sendo analisada
							            	//Posteriormente, estes diretÃ³rios serÃ£o analisados em busca de arquivos para criptografÃ¡-los e armazenÃ¡-los
							            	//em uma pasta equivalemte na pasta da nuvem
											
											/*
							            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
							            	
							            	arqDir.DirArquivosSecretosUser = path;
							            	arqDir.DirArquivosNuvem = pathDirEncrypt + nomeArEncrypt + "/";
							            	arqDir.DirArquivoConfig = arquivoEvento.pathArqEstados;
							            	arqDir.DirArquivoConfigEncrypted = arquivoEvento.pathArquivoEncrypt;
							            	
							            	arquivosAddOutrosDir.add(arqDir);
							            	
							            	*/							            	
										}
										else
										{
											if(continuar)
											{
												//Arquivo em questÃ£o jÃ¡ existe no arquivo de estados, provavelmente foi editado e seu correspondente cifrado deve ser deletado
												
												if(arq.arquivosList.get(j).pathArquivoEncrypt != null && !arq.arquivosList.get(j).pathArquivoEncrypt.isEmpty())
												{
													System.out.println("Arquivo jÃ¡ possui versÃ£o cifrada. Deletando esta versÃ£o...");													
													
													File arqTempDelete = new File(arq.arquivosList.get(j).pathArquivoEncrypt + ".encrypted");
													
													if(arqTempDelete.exists())
													{
														arqTempDelete.delete();
														
														System.out.println("VersÃ£o do arquivo cifrado deletado com sucesso...");
													}
													else
														System.out.println("VersÃ£o do arquivo cifrado nÃ£o encontrada!");														
												}
											}
										}
										
										//Verifica a Ãºltima data/horÃ¡rio de modificaÃ§Ã£o do arquivo. Se for o mesmo, o arquivo nÃ£o precisa ser encriptado
										//Se a data do arquivo da pasta do usuÃ¡rio for mais recente que a data do arquivo cifrado na nuvem, entÃ£o ele precisa ser encriptado
										if(Evento.equals("MODIFY"))
										{
											arquivoEvento.dataUltimaModArqEncrypt = arq.arquivosList.get(j).dataUltimaModArqEncrypt;
											arquivoEvento.nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
											arquivoEvento.pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;											
										}
										else
										{
											if(!dir)
											{
												arquivoEvento.nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
												arquivoEvento.pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
											}											
										}
										
										arq.arquivosList.remove(j);
																				
										break;
									}
								}
							}														
			        	}    	
												
						if(continuar)
						{
							System.out.println("Gravando informaÃ§Ãµes do arquivo no arquivo de estados...");
							
							//INSERIR DADOS DO ARQUIVO NO ARQUIVO DE ESTADOS 
							
							//Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos 
							escritorArqEstados.IniciarEscritaArquivoEstados(pathArqEstadosEvento, pathDirEncrypt);
			    	                 
			    	        //contador necessÃ¡rio para controlar o ID. 
			    	        //Aqui sÃ£o escritos no arquivo de configuraÃ§Ã£o os arquivos que jÃ¡ se encontram cifrados na pasta da nuvem
			    	        //O contador serÃ¡ utilizado no passo seguinte, onde os novos arquivos a serem cifrados devem continuar do nÃºmero parado neste loop
				            int contId = 0;
			    	        
				            //Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos que jÃ¡ estÃ£o cifrados e armazenados na pasta da nuvem
			    	        if(arq != null && arq.arquivosList != null && arq.arquivosList.size() > 0)
			    	        {
			    	        	for(int j=0; j < arq.arquivosList.size(); j++)
			    	        	{
			    	        		escritorArqEstados.EscreverNovoElemento(arq.arquivosList.get(j), contId);
			    	        		contId++;
		    	        		}
			    	        }
			    	        
			    	        if(Evento != "DELETE")
			    	        {
			    	        	//Criptografando o arquivo para que ele seja armazenado na pasta da nuvem
				    	        //Escrevendo os dados do novo arquivo no arquivo de estados
			    	        	
					            if(!arquivoEvento.diretorio)
					            {
					            	System.out.println("Criptografando o arquivo...");
					            	
					            	//Criptografando o arquivo
					            	if(arquivoEvento.nomeArquivoEncrypt != null && !arquivoEvento.nomeArquivoEncrypt.isEmpty())
					            		arquivoEvento.nomeArquivoEncrypt = CriptoUtil.CriptografarArquivo(new File(arquivoEvento.pathArquivo), new File(pathDirEncrypt + arquivoEvento.nomeArquivo), senhaUsuario, arquivoEvento.nomeArquivoEncrypt);
					            	else
					            		arquivoEvento.nomeArquivoEncrypt = CriptoUtil.CriptografarArquivo(new File(arquivoEvento.pathArquivo), new File(pathDirEncrypt + arquivoEvento.nomeArquivo), senhaUsuario, null);
					            	
					            	if(arquivoEvento.nomeArquivoEncrypt != null && !arquivoEvento.nomeArquivoEncrypt.isEmpty())
					            	{
					            		Date lastModified = new Date(new File(pathDirEncrypt + arquivoEvento.nomeArquivoEncrypt + ".encrypted").lastModified());
						            	
						            	arquivoEvento.dataUltimaModArqEncrypt = lastModified;
					            	}
					            	else
					            	{
					            		System.out.println("Erro ao cifrar arquivo! Dados do arquivo incompletos.");
					            		throw new Exception("Erro ao cifrar arquivo! Dados do arquivo incompletos.");
					            	}
					            }
					            else
					            {
					            	//Criptografando os diretÃ³rios - Na verdade Ã© criado um novo diretÃ³rio na pasta da nuvem e posteriormente inserido os 
					            	//arquivos nele, todos criptografados.
					            	
					            	//Criptografando o nome do diretÃ³rio
					            	
					            	System.out.println("Criando diretÃ³rio com nome criptografado...");
					            	
					            	File pastatemp;
					            	String nomeEncrypted = "";
					            	
					            	if(arquivoEvento.nomeArquivoEncrypt != null && !arquivoEvento.nomeArquivoEncrypt.isEmpty())
				            		{
					            		nomeEncrypted = arquivoEvento.nomeArquivoEncrypt;
					    				
					    				if(nomeEncrypted.length() > 30)
					    					nomeEncrypted = nomeEncrypted.substring(0, 29);
					    				
					            		pastatemp = new File(pathDirEncrypt + nomeEncrypted);
						            	
						            	if(!pastatemp.exists())
					            		{
						            		String hashNome = HashGeneratorUtils.generateSHA256(arquivoEvento.nomeArquivo.toString());
							            	
						            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
						            	
						            		Base32 codec = new Base32();
										
						            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(arquivoEvento.nomeArquivo.toString(), key);
						            		arquivoEvento.nomeArquivoEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
								        
						            		nomeEncrypted = arquivoEvento.nomeArquivoEncrypt;
						            		
						            		if(nomeEncrypted.length() > 30)
						            			nomeEncrypted = nomeEncrypted.substring(0, 29);
						            									            		
						            		pastatemp = new File(pathDirEncrypt + nomeEncrypted);
						            		
						            		if(!pastatemp.exists())
						            			pastatemp.mkdir();
						            		
						            		FileWriter arqChaveArquivo = new FileWriter(pathDirEncrypt + nomeEncrypted + "/keyFolder.encrypted"); 
											PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
											
											String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
											
											gravarArqChaveArquivo.println("chave="+keyCifrada);
											gravarArqChaveArquivo.println("nomePastaEncrypt="+arquivoEvento.nomeArquivoEncrypt);
											
											arqChaveArquivo.close();
											gravarArqChaveArquivo.close();
					            		}						            	
					            	}
					            	else
					            	{
					            		nomeEncrypted = "";
					            		
					            		String hashNome = HashGeneratorUtils.generateSHA256(arquivoEvento.nomeArquivo.toString());
					            	
					            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
					            	
					            		Base32 codec = new Base32();
									
					            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(arquivoEvento.nomeArquivo.toString(), key);
					            		arquivoEvento.nomeArquivoEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
							        
					            		nomeEncrypted = arquivoEvento.nomeArquivoEncrypt;
					            		
					            		if(nomeEncrypted.length() > 30)
					            			nomeEncrypted = nomeEncrypted.substring(0, 29);
					            		
					            		pastatemp = new File(pathDirEncrypt + nomeEncrypted);
						            	
						            	if(!pastatemp.exists())
						            		pastatemp.mkdir();
						            	
						            	FileWriter arqChaveArquivo = new FileWriter(pathDirEncrypt + nomeEncrypted + "/keyFolder.encrypted"); 
										PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
										
										String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
										
										gravarArqChaveArquivo.println("chave="+keyCifrada);
										gravarArqChaveArquivo.println("nomePastaEncrypt="+arquivoEvento.nomeArquivoEncrypt);
										
										arqChaveArquivo.close();
										gravarArqChaveArquivo.close();
					            	}					            			          	
					            			            	
					            	Date lastModified = new Date(pastatemp.lastModified());
					            	arquivoEvento.dataUltimaModArqEncrypt = lastModified;					            		            	
					            	
					            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo.replace(' ', '-') + "-config.xml";
					            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt +"/config.xml";
					            	String novoPathArqConfEncrypted = pathDirEncrypt + nomeEncrypted + "/config.xml";
					            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt + "-config.xml";
					            	
					            	//Config-files/config.xml
					            			            	
					            	//Criando uma lista para armazenar os diretÃ³rios encontrados na pasta sendo analisada
					            	//Posteriormente, estes diretÃ³rios serÃ£o analisados em busca de arquivos para criptografÃ¡-los e armazenÃ¡-los
					            	//em uma pasta equivalemte na pasta da nuvem
					            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
					            	
					            	String nomeArquivoSemEspaco = arquivoEvento.nomeArquivo.replace(" ", "--");
					            	
					            	arquivoEvento.pathArqEstados = pathArqEstadosEvento.substring(0, pathArqEstadosEvento.lastIndexOf('/') + 1) + nomeArquivoSemEspaco + "/config.xml"; 
					            	
					            	arqDir.DirArquivosSecretosUser = arquivoEvento.pathArquivo;
					            	//arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivoEvento.nomeArquivo + "/";
					            	arqDir.DirArquivosNuvem = pathDirEncrypt + nomeEncrypted + "/";
					            	arqDir.DirArquivoConfig = arquivoEvento.pathArqEstados;
					            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
					            			            			            	
					            	arquivosAddOutrosDir.add(arqDir);
					            }	
					            
					            String nomeEncrypted = arquivoEvento.nomeArquivoEncrypt;
					            
					            if(nomeEncrypted.length() > 30)
					            	nomeEncrypted = nomeEncrypted.substring(0, 29);
					            
					            arquivoEvento.pathArquivoEncrypt = pathDirEncrypt + nomeEncrypted;
					            
					            escritorArqEstados.EscreverNovoElemento(arquivoEvento, contId++);
					            
					            escritorArqEstados.FinalizarEscritaArquivoEstados();
						        
					            System.out.println("Verificando existencia de arquivos no diretÃ³rio para cifrar... ");
						        
					            //Verificando as pastas do diretÃ³rio sendo analizado para que os arquivos presentes nela possam ser encriptografados
					        	if(arquivosAddOutrosDir != null && arquivosAddOutrosDir.size() > 0)
					        	{
					        		for(int j = 0; j < arquivosAddOutrosDir.size(); j++)
					        		{
					        			//Processo recursivo de busca por arquivos para encriptaÃ§Ã£o
					        			VerificarArquivosCifrar(arquivosAddOutrosDir.get(j).DirArquivosSecretosUser, arquivosAddOutrosDir.get(j).DirArquivosNuvem, arquivosAddOutrosDir.get(j).DirArquivoConfig, senhaUsuario);
					        			
					        			//CARREGANDO LISTA DOS DIRETÃ“RIOS NA MEMÃ“RIA COM O DIRETÃ“RIO RECEM CRIADO
					        			AdicionarElementoDiretorios(arquivosAddOutrosDir.get(j).DirArquivoConfig, arquivosAddOutrosDir.get(j).DirArquivosSecretosUser, arquivosAddOutrosDir.get(j).DirArquivosNuvem, diretorioArquivosList, true, senhaUsuario);
					        		}
					        	}
			    	        }
			    	        else
			    	        {
			    	        	System.out.println("Deletando arquivo/diretÃ³rio...");
			    	        	
			    	        	escritorArqEstados.FinalizarEscritaArquivoEstados();
			    	        	
			    	        	String nomeEncrypted = arquivoEvento.nomeArquivoEncrypt;
		        			
			    	        	if(nomeEncrypted != null)
			    	        	{
			    	        		if(nomeEncrypted.length() > 30)
			        					nomeEncrypted = nomeEncrypted.substring(0, 29);
				    	        	
				    	        	//Deletando arquivo cifrado da pasta da nuvem
				        			if(!arquivoEvento.diretorio)
				        			{
				        				File tempFile = new File(pathDirEncrypt + nomeEncrypted + ".encrypted");
				            			
				        				if(tempFile != null && tempFile.exists());
				        					tempFile.delete();
				        			}
				        			else
				        			{
				        				//Se for diretÃ³rio, primeiro deve-se deletar todos os seus arquivos para que entÃ£o o diretÃ³rio seja deletado
				        				File tempFile = new File(pathDirEncrypt + nomeEncrypted);
				            			
				        				if(tempFile != null && tempFile.exists());
				        				{
				        					RemoverArquivos(tempFile);				        					
				        				}        					
				        				
				        				//tempFile.delete();
				        				
				        				//Deletando o arquivo de configuraÃ§Ã£o pertinente ao diretÃ³rio sendo deletado
				        				
				        				int indArquivo = arquivoEvento.pathArqEstados.lastIndexOf('/');
				        				String arquivo = "";
				        				
				        				if(indArquivo > 0)
				        					arquivo = arquivoEvento.pathArqEstados.substring(0, indArquivo);
				        				else
				        					arquivo = arquivoEvento.pathArqEstados;
				        				
				        				System.out.println("Deletando arquivo de estados equivalente...");
				        				
				        				File tempFileConfig = new File(arquivo);     
				        				
				        				if(tempFileConfig != null && tempFileConfig.exists())
				        					RemoverArquivos(tempFileConfig);
				        				
				        				System.out.println("Apagando registro da lista dos diretÃ³rios na memÃ³ria...");
				        				
				        				//APAGANDO O DIRETÃ“RIO DA LISTA DOS DIRETÃ“RIOS NA MEMÃ“RIA
					        			AdicionarElementoDiretorios(arquivoEvento.pathArqEstados, arquivoEvento.pathArquivo, arquivoEvento.pathArquivoEncrypt, diretorioArquivosList, false, senhaUsuario);
					        			
					        			System.out.println("Registro apagado da lista de diretÃ³rios da memÃ³ria");
				        			}
				        			
				        			System.out.println("Arquivo/DiretÃ³rio criptografado/deletado. Adicionando registro nas listas de exceÃ§Ãµes dos eventos...");
			    	        	}
			    	        	else
			    	        	{
			    	        		//Caso em que um diretÃ³rio foi criado e deletado/mudado de nome antes de ser criptografado e ter seus dados inseridos no arquivo de estados
			    	        		//NÃ£o precisa fazer nada
			    	        		System.out.println("Registro do Arquivo/DiretÃ³rio a ser deletado nÃ£o encontrado...");
			    	        	}
			    	        }
			    	        
			    	        
			    	        
			    	        lock.lock();
			    	        
			    	        if(eventosGeradosAplicacaoPastaNuvem == null)
			    	        	eventosGeradosAplicacaoPastaNuvem = new ArrayList<String>();
			    	        
			    	        if(arquivoEvento != null && arquivoEvento.diretorio)
			    	        	eventosGeradosAplicacaoPastaNuvem.add(arquivoEvento.pathArquivoEncrypt);
			    	        else
			    	        	eventosGeradosAplicacaoPastaNuvem.add(arquivoEvento.pathArquivoEncrypt + ".encrypted");
			    	        
			    	        lock.unlock();
			    	        
			    	        System.out.println("Processo terminado");
						}
					}
					
					System.out.println("########################################################");
				}
				
				lock.lock();
				
				eventosGeradosAplicacaoPastaUser = new ArrayList<String>();
				
				lock.unlock();
			}
		}
		catch(InterruptedException itex)
		{
			throw new InterruptedException("Erro ao tratar evento de cifrar! Erro: " + itex.getMessage());
		}
		catch(RuntimeException rex)
		{
			throw new RuntimeException("Erro ao tratar evento de cifrar! Erro: " + rex.getMessage());
		}
		catch(IOException e)
		{
			throw new IOException("Erro ao tratar evento de cifrar! Erro: " + e.getMessage());
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao tratar evento de cifrar! Erro: " + ex.getMessage());
		}		
	}
	
	public static void TratarEventosDecifrar(List<Eventos> pathEventos, boolean cifrar, String DirArquivoEstados, String DirArquivosSecretosUser, String DirNuvem, String senhaUsuario, DiretoriosDosArquivos diretorioArquivosList) throws Exception, IOException, RuntimeException, InterruptedException
	{
		try
		{
			if(pathEventos != null && pathEventos.size() > 0)
			{
				//Lista de caminhos dos diretÃ³rios que devem ter seus arquivos criptografados e enviados para a pasta da nuvem
	        	List<DiretoriosDosArquivos> arquivosDecifrarOutrosDir = new ArrayList<DiretoriosDosArquivos>();
	        	
	        	System.out.println("Tratando eventos (Decifrar)...");
	        	
				for(int i=0; i<pathEventos.size(); i++)
				{
					String path = "";
					String arqNome = "";
					boolean dir = false;
					
					//Se o arquivo jÃ¡ estiver presente e nÃ£o necessitar se decifrado novamente
					boolean continuar = true;
					
					String Evento = "";
					
					System.out.println("########################################################");
					
					System.out.println("Evento nÃºmero " + i + "...");
					
					int ind0 = pathEventos.get(i).PathEvent.lastIndexOf('/') + 1;
					
					if(ind0 > 0)
					{
						path = pathEventos.get(i).PathEvent.substring(0, ind0);
						arqNome = pathEventos.get(i).PathEvent.substring(ind0, pathEventos.get(i).PathEvent.length());
						
						System.out.println("Nome do arquivo: " + arqNome);
						System.out.println("Caminho do arquivo: " + path);
					}
					else
						System.out.println("NÃ£o foi possÃ­vel encontrar o nome e path do arquivo relacionado ao evento");
					
					lock.lock();
					
					if(eventosGeradosAplicacaoPastaNuvem != null && eventosGeradosAplicacaoPastaNuvem.size() > 0)
					{
						for(int j=0; j<eventosGeradosAplicacaoPastaNuvem.size();j++)
						{
							if(eventosGeradosAplicacaoPastaNuvem.get(j).equals(pathEventos.get(i).PathEvent))
							{
								System.out.println("Evento gerado pela aplicaÃ§Ã£o. NÃ£o tratar: " + pathEventos.get(i).PathEvent);
								
								continuar = false;
								break;
							}
						}
					}
					
					lock.unlock();
														
					if(continuar && !arqNome.isEmpty() && !arqNome.startsWith(".goutputstream-") && !arqNome.startsWith("keyFolder.encrypted"))
					{
						if(pathEventos.get(i).KindEvent == null || pathEventos.get(i).KindEvent.isEmpty())
							System.out.println("KIND EVENT igual a null ou vazio.");
						
						if(pathEventos.get(i).KindEvent.equals("ENTRY_CREATE"))
						{
							Evento = "CREATE";
							System.out.println("Manipular Arquivos -- > Evento: CREATE");
						}
						else if(pathEventos.get(i).KindEvent.equals("ENTRY_DELETE"))
						{
							Evento = "DELETE";
							System.out.println("Manipular Arquivos -- > Evento: DELETE");
						}
						else if(pathEventos.get(i).KindEvent.equals("ENTRY_MODIFY"))
						{
							Evento = "MODIFY";
							System.out.println("Manipular Arquivos -- > Evento: MODIFY");
						}
						else
							System.out.println("Evento nÃ£o identificado!");				
						
						if(Evento != "DELETE" && new File(pathEventos.get(i).PathEvent).exists() && new File(pathEventos.get(i).PathEvent).isDirectory())
							dir = true;
						
						System.out.println("DiretÃ³rio: " + dir);
						
						
						//Checando 
						if(Evento != "DELETE" && !dir && !arqNome.endsWith(".encrypted"))
						{
							continuar = false;
							
							System.out.println("Arquivo para decifrar nÃ£o esta no formato suportado pelo CPG. NÃ£o Ã© da extensÃ£o '.encrypted'!");
						}
						else if(Evento != "DELETE" && dir)
						{
							if(!new File(pathEventos.get(i).PathEvent + "/keyFolder.encrypted").exists())
							{
								continuar = false;
								
								System.out.println("DiretÃ³rio para decifrar nÃ£o esta no formato suportado pelo CPG. NÃ£o foi encontrado o arquivo keyfolder.encrypted!");
							}
						}
						
						if(continuar)
						{
							///home/vitormoia/workspace/ProjetoCNC/Testes/DirArquivosUser/teste pasta/AAteste.txt
							
							int ind = pathEventos.get(i).PathEvent.lastIndexOf('/') + 1;
							
							String dirEvento = "";
							
							if(ind > 0)
							{
								dirEvento = pathEventos.get(i).PathEvent.substring(0, ind);
							}
							else
								dirEvento = pathEventos.get(i).PathEvent;
							
							String[] ind2 = dirEvento.split(DirNuvem.toString());
													
							String pathArqEstadosEvento = "";
							String pathDirArquivosUser = "";
							String[] retorno = new String[2];
							
							System.out.println("Procurando arquivo de estados equivalente ao evento...");
							
							if(ind2.length > 0)
							{
								retorno = RetornarPathArquivoEstadosPeloDirNuvem(diretorioArquivosList, dirEvento, DirNuvem, DirArquivoEstados, DirArquivosSecretosUser, DirNuvem, senhaUsuario);
								
								if(retorno != null && retorno.length > 1)
								{
									System.out.println("Arquivo de estados encontrado...");
									
									pathArqEstadosEvento = retorno[0];
									pathDirArquivosUser = retorno[1];
								}
								else
								{
									System.out.println("Arquivo de estados nÃ£o encontrado, tentando recriar Ã¡rvore de diretÃ³rios...");
									
									diretorioArquivosList = CriarArvoreDiretorios(DirArquivoEstados, DirArquivosSecretosUser, DirNuvem, senhaUsuario);
									
									System.out.println("Ã�rvore de diretÃ³rios criada, procurando arquivo de estados equivalente ao evento...");
									
									retorno = RetornarPathArquivoEstadosPeloDirNuvem(diretorioArquivosList, dirEvento, DirNuvem, DirArquivoEstados, DirArquivosSecretosUser, DirNuvem, senhaUsuario);
									
									if(retorno != null && retorno.length > 1)
									{
										System.out.println("Arquivo de estados encontrado...");
										
										pathArqEstadosEvento = retorno[0];
										pathDirArquivosUser = retorno[1];
									}
									else
									{
										System.out.println("Arquivo de estados nÃ£o encontrado...");
										
										pathArqEstadosEvento = null;
										pathDirArquivosUser = null;
									}
								}
							}
							else if(ind2.length == 0)
							{
								System.out.println("Arquivo de estados encontrado, sendo o mesmo do diretÃ³rio raiz da nuvem...");
								
								pathArqEstadosEvento = DirArquivoEstados.replace(" ", "--");
								pathDirArquivosUser = DirArquivosSecretosUser;
							}
							
							System.out.println("DiretÃ³rio do arquivo de estados do evento: " + pathArqEstadosEvento);
							
							//TRATAR EVENTO...
													
							//AlteraÃ§Ãµes no arquivo de estados
							
							//EVENTO CREATE: 
								//Adicionar novos campos no arquivo de estados
								//Criptografar arquivo
							
							//EVENTO MODIFY: 
								//encontrar e alterar os registros de estados do arquivo em questÃ£o
								//Criptografar arquivo
							
							//EVENTO DELETE: 
								//encontrar e deletar os registros de estados do arquivo em questÃ£o
								//Deletar arquivo criptografado						
							
							if(pathArqEstadosEvento != null && !pathArqEstadosEvento.isEmpty())
							{
								File arqEstados = new File(pathArqEstadosEvento);
								
								Escritor escritorArqEstados = new Escritor();
								
								String pathDirEncrypt = "";
								
								Arquivos arquivoEvento = new Arquivos();
								
								if(Evento != "DELETE")
								{
									//Buscando informaÃ§Ãµes do arquivo
									
									System.out.println("Buscando informaÃ§Ãµes do arquivo...");
				    	        	
				    	        	File arqTemp = new File(path + arqNome);
				    	        	
				    	        	if(arqTemp.exists())
				    	        	{
				    	        		Date lastModified = new Date(arqTemp.lastModified());
				    	        		
										arquivoEvento.nomeArquivoEncrypt = arqTemp.getName();
				    	        		arquivoEvento.pathArquivoEncrypt = arqTemp.getAbsolutePath();
				    	        		arquivoEvento.dataUltimaModArqEncrypt = lastModified;
				    	        		arquivoEvento.diretorio = arqTemp.isDirectory();
				    	        		arquivoEvento.pathArqEstados = pathArqEstadosEvento;				    	        		
				    	        	}
				    	        	else
				    	        	{
				    	        		System.out.println("Arquivo nÃ£o existe, nÃ£o foi possÃ­vel obter suas informaÃ§Ãµes...");
				    	        	}
								}
								
								if(arqEstados == null || !arqEstados.exists())
					        	{
					        		try 
									{
					        			if(Evento != "DELETE")
					        			{
					        				System.out.println("Criando arquivo de estados...");
					        				
					        				arqEstados = new File(pathArqEstadosEvento);
						        			
						        			new File(arqEstados.getParent()).mkdirs();
						        			
						        			arqEstados.createNewFile();
						        			
						        			pathDirEncrypt = DirNuvem;
						        			        			
						        			escritorArqEstados.IniciarEscritaArquivoEstados(pathArqEstadosEvento, pathDirEncrypt);
							    			
						        			escritorArqEstados.FinalizarEscritaArquivoEstados();
					        			}
					        			else
					        				continuar = false;
									} 
									catch (IOException e) 
									{
										e.printStackTrace();
									}
					        	}
					        	else
					        	{
					        		try 
					        		{
					        			System.out.println("Abrindo arquivo de estados para leitura...");
					        			
					        			//Faz a leitura dos arquivos presentes no arquivo de estados e armazena suas infor. em "arq"
										pathDirEncrypt = Leitura(pathArqEstadosEvento, true);
										
										System.out.println("Arquivo aberto...");
										
										String name1;
										
										int indFile = pathEventos.get(i).PathEvent.indexOf(".encrypted");
										
										if(indFile > 0)
											name1 = pathEventos.get(i).PathEvent.substring(0, indFile);
										else
											name1 = pathEventos.get(i).PathEvent;
																			
										//Verifica se o arquivo em questÃ£o existe no arquivo de estados
										for(int j = 0; j< arq.arquivosList.size(); j++)
										{
											String path2 = arq.arquivosList.get(j).pathArquivoEncrypt;
											
											if(name1.equals(path2))
											{
												if(Evento != "DELETE" && arquivoEvento.dataUltimaModArqEncrypt.compareTo(arq.arquivosList.get(j).dataUltimaModArqEncrypt) <= 0)
												{
													continuar = false;
													break;
												}
												else if(Evento.equals("DELETE"))
													dir = arq.arquivosList.get(j).diretorio;
												
												//Ã‰ diretÃ³rio?
												if(dir)
												{
													arquivoEvento.diretorio = true;
													arquivoEvento.dataUltimaMod = arq.arquivosList.get(j).dataUltimaMod;
													arquivoEvento.nomeArquivo = arq.arquivosList.get(j).nomeArquivo;
													arquivoEvento.pathArquivo = arq.arquivosList.get(j).pathArquivo;
													arquivoEvento.pathArquivoEncrypt = pathEventos.get(i).PathEvent;
													arquivoEvento.pathArqEstados = arq.arquivosList.get(j).pathArqEstados;
													arquivoEvento.nomeArquivoEncrypt = arqNome;
													
													//arquivoEvento.pathArquivoEncrypt = pathDirEncrypt + arquivoEvento.nomeArquivoEncrypt; // + "/config.xml.encrypted";
													
													//String nomeArquivoSemEspacos = arquivoEvento.nomeArquivo.replace(" ", "--"); 
													
													//arquivoEvento.pathArqEstados = pathArqEstadosEvento.substring(0, pathArqEstadosEvento.lastIndexOf('/') + 1) + nomeArquivoSemEspacos + "/config.xml";
													
													
													//Criando uma lista para armazenar os diretÃ³rios encontrados na pasta sendo analisada
									            	//Posteriormente, estes diretÃ³rios serÃ£o analisados em busca de arquivos para criptografÃ¡-los e armazenÃ¡-los
									            	//em uma pasta equivalemte na pasta da nuvem
													
													/*
									            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
									            	
									            	arqDir.DirArquivosSecretosUser = path;
									            	arqDir.DirArquivosNuvem = pathDirEncrypt + nomeArEncrypt + "/";
									            	arqDir.DirArquivoConfig = arquivoEvento.pathArqEstados;
									            	arqDir.DirArquivoConfigEncrypted = arquivoEvento.pathArquivoEncrypt;
									            	
									            	arquivosAddOutrosDir.add(arqDir);
									            	
									            	*/
									            	
												}
												
												//Verifica a Ãºltima data/horÃ¡rio de modificaÃ§Ã£o do arquivo. Se for o mesmo, o arquivo nÃ£o precisa ser encriptado
												//Se a data do arquivo da pasta do usuÃ¡rio for mais recente que a data do arquivo cifrado na nuvem, entÃ£o ele precisa ser encriptado
												if(Evento.equals("MODIFY"))
												{
													arquivoEvento.dataUltimaMod = arq.arquivosList.get(j).dataUltimaMod;
													arquivoEvento.nomeArquivo = arq.arquivosList.get(j).nomeArquivo;
													arquivoEvento.pathArquivo = arq.arquivosList.get(j).pathArquivo;											
												}
												else
												{
													if(!dir)
													{
														arquivoEvento.nomeArquivo = arq.arquivosList.get(j).nomeArquivo;
														arquivoEvento.pathArquivo = arq.arquivosList.get(j).pathArquivo;
														
														arquivoEvento.dataUltimaMod = arq.arquivosList.get(j).dataUltimaMod;
														arquivoEvento.pathArquivoEncrypt = pathEventos.get(i).PathEvent;
														arquivoEvento.pathArqEstados = arq.arquivosList.get(j).pathArqEstados;
														arquivoEvento.nomeArquivoEncrypt = arqNome;
													}											
												}
												
												arq.arquivosList.remove(j);
																						
												break;
											}
										}
									} 
					        		catch (ParseException e) 
					        		{
					        			e.printStackTrace();
									}
					        	} 
								
								if((arquivoEvento.nomeArquivoEncrypt==null || arquivoEvento.nomeArquivoEncrypt.isEmpty()) || (arquivoEvento.pathArquivoEncrypt == null || arquivoEvento.pathArquivoEncrypt.isEmpty()))
									continuar = false;
								
								
								if(continuar)
								{
									//INSERIR DADOS DO ARQUIVO NO ARQUIVO DE ESTADOS E DEPOIS DESCIFRÃ�-LO
									
									System.out.println("Gravando informaÃ§Ãµes do arquivo no arquivo de estados...");
									
									//Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos 
									escritorArqEstados.IniciarEscritaArquivoEstados(pathArqEstadosEvento, pathDirEncrypt);
					    	                 
					    	        //contador necessÃ¡rio para controlar o ID. 
					    	        //Aqui sÃ£o escritos no arquivo de configuraÃ§Ã£o os arquivos que jÃ¡ se encontram cifrados na pasta da nuvem
					    	        //O contador serÃ¡ utilizado no passo seguinte, onde os novos arquivos a serem cifrados devem continuar do nÃºmero parado neste loop
						            int contId = 0;
					    	        
						            //Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos que jÃ¡ estÃ£o cifrados e armazenados na pasta da nuvem
					    	        if(arq != null && arq.arquivosList != null && arq.arquivosList.size() > 0)
					    	        {
					    	        	for(int j=0; j < arq.arquivosList.size(); j++)
					    	        	{
					    	        		escritorArqEstados.EscreverNovoElemento(arq.arquivosList.get(j), contId);
					    	        		contId++;
				    	        		}
					    	        }
					    	        
					    	        if(Evento != "DELETE")
					    	        {
					    	        	//Descifrando o arquivo para que ele seja armazenado na pasta da aplicaÃ§Ã£o (local)
						    	        //Escrevendo os dados do novo arquivo no arquivo de estados			
					    	        	
							            if(!arquivoEvento.diretorio)
							            {
							            	System.out.println("Decifrando o arquivo...");
							            	
							            	int indNome = arquivoEvento.nomeArquivoEncrypt.indexOf(".encrypted");
								            int indPath = arquivoEvento.pathArquivoEncrypt.indexOf(".encrypted");
								            
								            if(indNome > 0)
								            	arquivoEvento.nomeArquivoEncrypt = arquivoEvento.nomeArquivoEncrypt.substring(0, indNome);
											
											if(indPath > 0)
												arquivoEvento.pathArquivoEncrypt = arquivoEvento.pathArquivoEncrypt.substring(0, indPath);
							            	
							            	//nomeArquivoOriginal = novosArquivos.get(i).nomeArquivo.substring(0, novosArquivos.get(i).nomeArquivo.indexOf(".encrypted"));
					    		            
					    		            //nomeArquivo.setText(nomeArquivoOriginal);
					    		            
											String nomeEncrypted = arquivoEvento.nomeArquivoEncrypt;
											
											if(nomeEncrypted.length() > 30)
					    		            	nomeEncrypted = nomeEncrypted.substring(0, 29);
											
					    		            //--String pathEncrypted = arquivoEvento.pathArquivoEncrypt.replace(DirNuvem, DirArquivosSecretosUser);
					    		            
					    		            //--String pathArq = pathEncrypted.substring(0, pathEncrypted.indexOf(nomeEncrypted));
					    		            
					    		            //Caminho do arquivo de configuraÃ§Ã£o da pasta
					    		            //Cada pasta tem um arquivo de configuraÃ§Ã£o diferente
					    		            arquivoEvento.pathArqEstados = "";
					    		            
					    		            String[] ret = new String[2];
					    		            
					    		            //Decifrando o arquivo - APENAS ARQUIVOS
					    		            if(arquivoEvento.nomeArquivo != null && !arquivoEvento.nomeArquivo.isEmpty())
					    		            {
					    		            	ret = CriptoUtil.DescriptografarArquivo(new File(arquivoEvento.pathArquivoEncrypt + ".encrypted"), new File(arquivoEvento.pathArquivo), senhaUsuario, arquivoEvento.nomeArquivo);
					    		            	
					    		            	arquivoEvento.nomeArquivoEncrypt = ret[0];
					    		            	arquivoEvento.nomeArquivo = ret[1];			    		            	
						            		}
					    		            else
				    		            	{
					    		            	ret = CriptoUtil.DescriptografarArquivo(new File(arquivoEvento.pathArquivoEncrypt + ".encrypted"), new File(pathDirArquivosUser + nomeEncrypted), senhaUsuario, null);
					    		            	
					    		            	arquivoEvento.nomeArquivoEncrypt = ret[0];
					    		            	arquivoEvento.nomeArquivo = ret[1];
				    		            	}
					    		            
					    		            //Data da Ãºltima modificaÃ§Ã£o do arquivo
					    		            arquivoEvento.dataUltimaMod = new Date(new File(pathDirEncrypt + arquivoEvento.nomeArquivo).lastModified());
					    		            arquivoEvento.pathArquivo = pathDirArquivosUser + arquivoEvento.nomeArquivo;
					    		            
					    		            System.out.println("Decifragem completa...");
				    		            }
							            else
							            {
							            	
							            	//DiretÃ³rios/Patas
							            	
							            	//nomeArquivoOriginal = novosArquivos.get(i).nomeArquivo;
							            	
							            	//nomeArquivo.setText(nomeArquivoOriginal);
							            	
							            	System.out.println("Criando pasta no diretÃ³rio do usuÃ¡rio...");
							            				            	
							            	File pastatemp;
							            	
							            	if(arquivoEvento.nomeArquivo != null && !arquivoEvento.nomeArquivo.isEmpty())
						            		{
							            		pastatemp = new File(DirArquivosSecretosUser + arquivoEvento.nomeArquivo);
								            	
								            	if(!pastatemp.exists())
							            		{
								            		Base32 codec = new Base32();
								            		
								            		FileReader arqNomeDecifrar = new FileReader(arquivoEvento.pathArquivoEncrypt + "/keyFolder.encrypted");
													BufferedReader lerArqNome = new BufferedReader(arqNomeDecifrar);
										        	      
										        	String linhaNome = lerArqNome.readLine(); // lÃª a primeira linha
													// a variÃ¡vel "linha" recebe o valor "null" quando o processo 
													// de repetiÃ§Ã£o atingir o final do arquivo texto
										        	
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
														
														linhaNome = lerArqNome.readLine(); // lÃª da segunda atÃ© a Ãºltima linha
													} 
								
													arqNomeDecifrar.close();
													lerArqNome.close();
													
													Key chaveDecifrada = CriptoUtil.DescriptografarChaveSimetrica(chaveCifrada, senhaUsuario);
													
													arquivoEvento.nomeArquivo = CriptoSimetrica.DesencriptarMensagem(codec.decode(nomeCifrado), (SecretKey)chaveDecifrada);									
							            		}				            	
							            	}
							            	else
						            		{
							            		Base32 codec = new Base32();
							            		
							            		FileReader arqNomeDecifrar = new FileReader(arquivoEvento.pathArquivoEncrypt + "/keyFolder.encrypted");
												BufferedReader lerArqNome = new BufferedReader(arqNomeDecifrar);
									        	      
									        	String linhaNome = lerArqNome.readLine(); // lÃª a primeira linha
												// a variÃ¡vel "linha" recebe o valor "null" quando o processo 
												// de repetiÃ§Ã£o atingir o final do arquivo texto
									        	
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
													
													linhaNome = lerArqNome.readLine(); // lÃª da segunda atÃ© a Ãºltima linha
												} 
							
												arqNomeDecifrar.close();
												lerArqNome.close();

												Key chaveDecifrada = CriptoUtil.DescriptografarChaveSimetrica(chaveCifrada, senhaUsuario);
												
												arquivoEvento.nomeArquivo = CriptoSimetrica.DesencriptarMensagem(codec.decode(nomeCifrado), (SecretKey)chaveDecifrada);
											}
							            	
						            		//nomeArquivoOriginal = CriptoUtil.DescriptografarMensagemAlgAssimetrico(novosArquivos.get(i).nomeArquivoEncrypt, senhaUsuario);
					    		            
					    		            String pathEncrypted = arquivoEvento.pathArquivoEncrypt.replace(DirNuvem, DirArquivosSecretosUser);
					    		            
					    		            String nomeEncrypted = arquivoEvento.nomeArquivoEncrypt;
					    		            
					    		            if(nomeEncrypted != null && nomeEncrypted.length() > 30)
					    		            	nomeEncrypted = nomeEncrypted.substring(0, 29);
					    		            
					    		            String pathArq = pathEncrypted.substring(0, pathEncrypted.indexOf(nomeEncrypted));
					    		            
					    		            arquivoEvento.pathArquivo = pathArq + arquivoEvento.nomeArquivo;
					    		            
					    		            //Decifrando os diretÃ³rios
							            	//File pastatemp = new File(DirNuvem + novosArquivos.get(i).nomeArquivo);
					    		            pastatemp = new File(pathArq + arquivoEvento.nomeArquivo);
							            	
							            	if(!pastatemp.exists())
							            	{
							            		pastatemp.mkdir();
							            	}
							            	
							            	//Data da Ãºltima modificaÃ§Ã£o da pasta
							            	arquivoEvento.dataUltimaMod = new Date(pastatemp.lastModified());
							            	
							            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo.replace(' ', '-') + "-config.xml";
							            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivoEncrypt +"-config.xml";
							            	
							            	//String nomeArEncrypt = novosArquivos.get(i).nomeArquivoEncrypt;
											//String novoPathArqConf = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
							            	
							            	String nomeArquivoSemEspaco = arquivoEvento.nomeArquivo.replace(" ", "--");
							            	
							            	String novoPathArqEstados  = pathArqEstadosEvento.substring(0, pathArqEstadosEvento.lastIndexOf('/') + 1) + nomeArquivoSemEspaco + "/config.xml";
							            	
							            	String novoPathArqConfEncrypted = DirNuvem + nomeEncrypted + "/config.xml.encrypted";
							            	
							            	arquivoEvento.pathArqEstados = novoPathArqEstados;
							            	
							            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
							            	
							            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivoEvento.nomeArquivo + "/";
							            	//arqDir.DirArquivosNuvem = DirNuvem + novosArquivos.get(i).nomeArquivoEncrypt + "/";
							            	arqDir.DirArquivosNuvem = pathDirEncrypt + nomeEncrypted + "/";
							            	arqDir.DirArquivoConfig = novoPathArqEstados;
							            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
							            	
							            	arquivosDecifrarOutrosDir.add(arqDir);
							            	
							            	System.out.println("Pasta criada com sucesso...");
							            	
							            }	
							            
							            String nomeEncrypted = arquivoEvento.nomeArquivoEncrypt;
							            
							            if(nomeEncrypted != null && nomeEncrypted.length() > 30)
							            	nomeEncrypted = nomeEncrypted.substring(0, 29);
							            
							            arquivoEvento.pathArquivoEncrypt = pathDirEncrypt + nomeEncrypted;
							            
							            escritorArqEstados.EscreverNovoElemento(arquivoEvento, contId++);
							            
							            escritorArqEstados.FinalizarEscritaArquivoEstados();
							            
							            System.out.println("Verificando se exitem arquivos para serem decifrados dentro da pasta...");
								        
								        
							            //Verificando as pastas do diretÃ³rio sendo analizado para que os arquivos presentes nela possam ser encriptografados
							        	if(arquivosDecifrarOutrosDir != null && arquivosDecifrarOutrosDir.size() > 0)
							        	{
							        		for(int j = 0; j < arquivosDecifrarOutrosDir.size(); j++)
							        		{
							        			//Processo recursivo de busca por arquivos para decifragem
							        			VerificarArquivosDecifrar(arquivosDecifrarOutrosDir.get(j).DirArquivosSecretosUser, arquivosDecifrarOutrosDir.get(j).DirArquivosNuvem, arquivosDecifrarOutrosDir.get(j).DirArquivoConfig, senhaUsuario);
							        			
							        			//CARREGANDO LISTA DOS DIRETÃ“RIOS NA MEMÃ“RIA COM O DIRETÃ“RIO RECEM CRIADO
							        			AdicionarElementoDiretorios(arquivosDecifrarOutrosDir.get(j).DirArquivoConfig, arquivosDecifrarOutrosDir.get(j).DirArquivosSecretosUser, arquivosDecifrarOutrosDir.get(j).DirArquivosNuvem, diretorioArquivosList, true, senhaUsuario);
							        		}
							        	}
					    	        }
					    	        else
					    	        {
					    	        	System.out.println("Deletando arquivo/diretÃ³rio...");
					    	        	
					    	        	escritorArqEstados.FinalizarEscritaArquivoEstados();
					    	        	
					    	        	//Deletando arquivo decifrado da pasta da aplicaÃ§Ã£o (local).
					    	        	
					    	        	if(arquivoEvento != null && arquivoEvento.pathArquivo != null && !arquivoEvento.pathArquivo.isEmpty())
					    	        	{
					    	        		if(!arquivoEvento.diretorio )
						        			{
						        				File tempFile = new File(arquivoEvento.pathArquivo);
						            			
						        				if(tempFile != null && tempFile.exists());
						        					tempFile.delete();
						        					
						        				System.out.println("Arquivo deletado...");
						        			}
						        			else
						        			{
						        				//Se for diretÃ³rio, primeiro deve-se deletar todos os seus arquivos para que entÃ£o o diretÃ³rio seja deletado
						        				File tempFile = new File(arquivoEvento.pathArquivo);
						            			
						        				if(tempFile != null && tempFile.exists());
						        				{
						        					RemoverArquivos(tempFile);				        					
						        				}        					
						        				
						        				tempFile.delete();
						        				
						        				//Deletando o arquivo de estados pertinente ao diretÃ³rio sendo deletado
						        				
						        				System.out.println("Deletando o arquivo de estados...");
						        				
						        				int indArquivo = arquivoEvento.pathArqEstados.lastIndexOf('/');
						        				String arquivo = "";
						        				
						        				if(indArquivo > 0)
						        					arquivo = arquivoEvento.pathArqEstados.substring(0, indArquivo);
						        				else
						        					arquivo = arquivoEvento.pathArqEstados;
						        				
						        				File tempFileConfig = new File(arquivo);     
						        				
						        				if(tempFileConfig != null && tempFileConfig.exists())
						        					RemoverArquivos(tempFileConfig);
						        				
						        				System.out.println("Deletando registro da pasta da lista dos diretÃ³rios da memÃ³ria...");
						        				
						        				//APAGANDO O DIRETÃ“RIO DA LISTA DOS DIRETÃ“RIOS NA MEMÃ“RIA
							        			AdicionarElementoDiretorios(arquivoEvento.pathArqEstados, arquivoEvento.pathArquivo, arquivoEvento.pathArquivoEncrypt, diretorioArquivosList, false, senhaUsuario);
						        			}
					    	        	}				        			    
					    	        }
					    	        
					    	        lock.lock();
					    	        
					    	        if(eventosGeradosAplicacaoPastaUser != null)
					    	        	eventosGeradosAplicacaoPastaUser.add(arquivoEvento.pathArquivo);
					    	        else
				    	        	{
					    	        	eventosGeradosAplicacaoPastaUser = new ArrayList<String>();
					    	        	eventosGeradosAplicacaoPastaUser.add(arquivoEvento.pathArquivo);
				    	        	}
					    	        
					    	        lock.unlock();
					    	        				    	        
								}
							}
						}
					}
					
					System.out.println("Processo terminado");
					
				}
				
				System.out.println("########################################################");
				
				
				lock.lock();
				
				eventosGeradosAplicacaoPastaNuvem = new ArrayList<String>();
				
				lock.unlock();
			}
		}
		catch(InterruptedException itex)
		{
			throw new InterruptedException("Erro ao tratar evento de decifrar! Erro: " + itex.getMessage());
		}
		catch(RuntimeException rex)
		{
			throw new RuntimeException("Erro ao tratar evento de decifrar! Erro: " + rex.getMessage());
		}
		catch(IOException e)
		{
			throw new Exception("Erro ao tratar evento de decifrar! Erro: " + e.getMessage());
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao tratar evento de decifrar! Erro: " + ex.getMessage());
		}
	}
	
	private static Lock lock = new Lock();
	
	public static class Lock
	{
		//http://tutorials.jenkov.com/java-concurrency/locks.html
			
		private boolean isLocked = false;
		
		public synchronized void lock() throws InterruptedException
		{
			while(isLocked)
			{
		      wait();
		    }
		    isLocked = true;
	    }
		
		public synchronized void unlock()
		{
			isLocked = false;
		    notify();
	    }
	}
		
	public static void AdicionarElementoDiretorios(String DirArquivoEstados, String DirArquivosSecretosUser, String DirNuvem, DiretoriosDosArquivos dir, boolean adicionarRemover, String senhaUsuario) throws Exception
	{
		try
		{
			if(dir != null)
			{
				if(dir.DirArquivosSecretosUser.equals(DirArquivosSecretosUser))
					return;
				
				int tamanho = DirArquivosSecretosUser.length();
				
				//if(DirArquivosSecretosUser.substring(tamanho - 1, tamanho).equals('/'))
					//DirArquivosSecretosUser = DirArquivosSecretosUser.substring(tamanho);
				
				if(DirArquivosSecretosUser.endsWith("/"))
					DirArquivosSecretosUser = DirArquivosSecretosUser.substring(0, tamanho-1);
				
				int ind = DirArquivosSecretosUser.lastIndexOf('/');
				
				String pathDir = "";
				String nameDir = "";
				
				if(ind > 0)
				{
					pathDir = DirArquivosSecretosUser.substring(0, ind) + "/";
					nameDir = DirArquivosSecretosUser.substring(ind +1, DirArquivosSecretosUser.length());
				}
				
				System.out.println("Nome do diretÃ³rio: " + nameDir);
				System.out.println("Path do diretÃ³rio: " + pathDir);
				
				DiretoriosDosArquivos diretorio = new DiretoriosDosArquivos();
				diretorio.DirArquivoConfig = DirArquivoEstados;
				diretorio.DirArquivosSecretosUser = DirArquivosSecretosUser + "/";
				diretorio.DirArquivosNuvem = DirNuvem;				
				
				if(!diretorio.DirArquivosNuvem.endsWith("/"))
					diretorio.DirArquivosNuvem = diretorio.DirArquivosNuvem + "/";
				
				
				//Se for para inserir um elemento...
				if(adicionarRemover)
				{
					//Sub-diretÃ³rios...
					diretorio.ListSubDiretorios = ProcurarSubDiretoriosPath(diretorio.DirArquivoConfig, diretorio.DirArquivosSecretosUser, diretorio.DirArquivosNuvem, senhaUsuario);
					
					if(dir.ListSubDiretorios != null && dir.ListSubDiretorios.size() > 0)
						ProcurarElementoInserir(dir, diretorio, pathDir);
					else
					{
						dir.ListSubDiretorios = new ArrayList<DiretoriosDosArquivos>();
						dir.ListSubDiretorios.add(diretorio);
					}
				}
				else
				{
					//se for para deletar um elemento...
					if(dir.ListSubDiretorios != null && dir.ListSubDiretorios.size() > 0)
						ProcurarElementoDeletar(dir, diretorio, pathDir);
					else
					{
						dir.ListSubDiretorios = new ArrayList<DiretoriosDosArquivos>();
						dir.ListSubDiretorios.remove(diretorio);
					}
				}
			}
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao adicionar novo elemento! Erro: " + ex.getMessage());
		}
	}
	
	public static void ProcurarElementoInserir(DiretoriosDosArquivos diretorio, DiretoriosDosArquivos novoDir, String pathDir)
	{
		if(diretorio != null && diretorio.DirArquivosSecretosUser != null && !diretorio.DirArquivosSecretosUser.isEmpty())
		{
			if(diretorio.DirArquivosSecretosUser.equals(pathDir))
			{
				if(diretorio.ListSubDiretorios != null && diretorio.ListSubDiretorios.size() > 0)
				{
					for(int i=0; i< diretorio.ListSubDiretorios.size(); i++)
					{
						if(diretorio.ListSubDiretorios.get(i).DirArquivosSecretosUser.equals(novoDir.DirArquivosSecretosUser))
						{
							return;
						}
					}
					
					//Se o diretÃ³rio ainda nÃ£o estÃ¡ na lista, deve inserÃ­-lo
					diretorio.ListSubDiretorios.add(novoDir);
					
					return;
				}
				else
				{
					diretorio.ListSubDiretorios = new ArrayList<DiretoriosDosArquivos>();
					diretorio.ListSubDiretorios.add(novoDir);
				}
					
			}
			
			if(diretorio.ListSubDiretorios != null && diretorio.ListSubDiretorios.size() > 0)
			{
				for(int i=0; i< diretorio.ListSubDiretorios.size(); i++)
				{
					ProcurarElementoInserir(diretorio.ListSubDiretorios.get(i), novoDir, pathDir);
				}
			}
		}
	}
	
	public static void ProcurarElementoDeletar(DiretoriosDosArquivos diretorio, DiretoriosDosArquivos dirExcluir, String pathDir)
	{
		if(diretorio != null && diretorio.DirArquivosSecretosUser != null && !diretorio.DirArquivosSecretosUser.isEmpty())
		{
			if(diretorio.DirArquivosSecretosUser.equals(pathDir))
			{
				if(diretorio.ListSubDiretorios != null && diretorio.ListSubDiretorios.size() > 0)
				{
					for(int i=0; i< diretorio.ListSubDiretorios.size(); i++)
					{
						if(diretorio.ListSubDiretorios.get(i).DirArquivosSecretosUser.equals(dirExcluir.DirArquivosSecretosUser))
						{
							diretorio.ListSubDiretorios.remove(i);
						}
					}
					
					return;
				}	
			}
			
			if(diretorio.ListSubDiretorios != null && diretorio.ListSubDiretorios.size() > 0)
			{
				for(int i=0; i< diretorio.ListSubDiretorios.size(); i++)
				{
					ProcurarElementoDeletar(diretorio.ListSubDiretorios.get(i), dirExcluir, pathDir);
				}
			}
		}
	}
		
	public static DiretoriosDosArquivos CriarArvoreDiretorios(String DirArquivoEstados, String DirArquivosSecretosUser, String DirNuvem, String senhaUsuario) throws Exception
	{
		try
		{
			DiretoriosDosArquivos dir = new DiretoriosDosArquivos();
			
			dir.DirArquivoConfig = DirArquivoEstados;
			
			if(DirArquivosSecretosUser != null && !DirArquivosSecretosUser.endsWith("/"))
				DirArquivosSecretosUser = DirArquivosSecretosUser + "/";
			
			dir.DirArquivosSecretosUser = DirArquivosSecretosUser;
			dir.DirArquivosNuvem = DirNuvem;
			
			File dirNuvem = new File(dir.DirArquivosNuvem);
			
			if(dirNuvem.exists())
			{
				File arquivosDirNuvem[] = dirNuvem.listFiles();
				
				if(arquivosDirNuvem != null)
				{
					//Adicionar arquivos na lista
					for(int i = 0; i< arquivosDirNuvem.length; i++) 
					{
						File each = arquivosDirNuvem[i];
						
						if(each.isDirectory())
						{
							if(dir.ListSubDiretorios == null)
								dir.ListSubDiretorios = new ArrayList<DiretoriosDosArquivos>();
							
							String nomeDirDecifrado = "";							
							
							Base32 codec = new Base32();
		            		
		            		FileReader arqNome = new FileReader(each.getAbsolutePath() + "/keyFolder.encrypted");
							BufferedReader lerArqNome = new BufferedReader(arqNome);
				        	      
				        	String linhaNome = lerArqNome.readLine(); // lÃª a primeira linha
							// a variÃ¡vel "linha" recebe o valor "null" quando o processo 
							// de repetiÃ§Ã£o atingir o final do arquivo texto
				        	
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
								
								linhaNome = lerArqNome.readLine(); // lÃª da segunda atÃ© a Ãºltima linha
							}
		
							arqNome.close();
							lerArqNome.close();

							Key chaveDecifrada = CriptoUtil.DescriptografarChaveSimetrica(chaveCifrada, senhaUsuario);
							
							nomeDirDecifrado = CriptoSimetrica.DesencriptarMensagem(codec.decode(nomeCifrado), (SecretKey)chaveDecifrada);
							
							int ind = DirArquivoEstados.indexOf("config.xml");
							
							String dirArquivoEstadosNovo = "";
							
							if(ind > 0)
								dirArquivoEstadosNovo = DirArquivoEstados.substring(0, ind) + nomeDirDecifrado.replace(" ", "--") + "/config.xml";
							
							dir.ListSubDiretorios.add(CriarArvoreDiretorios(dirArquivoEstadosNovo, DirArquivosSecretosUser + nomeDirDecifrado + "/", DirNuvem + each.getName() + "/", senhaUsuario));
														
						}
					}
				}
				else
					throw new Exception("NÃ£o foi possÃ­vel encontrar o diretÃ³rio dos arquivos para serem cifrados!");
			}
			
			return dir;				
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao gerar Ã¡rvore de diretÃ³rios! Erro: " + ex.getMessage());
		}
	}
	
	public static List<DiretoriosDosArquivos> ProcurarSubDiretoriosPath(String DirArquivoEstados, String DirArquivosSecretosUser, String DirNuvem, String senhaUsuario) throws Exception
	{
		try
		{
			List<DiretoriosDosArquivos> diretorioList = new ArrayList<DiretoriosDosArquivos>();
			
			//DiretoriosDosArquivos diretorio = new DiretoriosDosArquivos();
			
			File dirFile = new File(DirNuvem);
			
			if(dirFile.exists())
			{
				File arquivosDir[] = dirFile.listFiles();
				
				if(arquivosDir != null)
				{
					for(int i=0; i < arquivosDir.length; i++)
					{
						if(arquivosDir[i].isDirectory())
						{
							diretorioList.add(CriarArvoreDiretorios(DirArquivoEstados, DirArquivosSecretosUser, DirNuvem, senhaUsuario));
							
							//diretorio.DirArquivosSecretosUser=arquivosDir[i].getAbsolutePath();
						}
					}
				}
			}
			
			return diretorioList;
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao procurar por sub-diretÃ³rios! Erro: " + ex.getMessage());
		}
	}
	
	public static String[] RetornarPathArquivoEstadosPeloDirNuvem(DiretoriosDosArquivos diretorio, String pathDirNuvem, String DirNuvem, String DirArquivoEstados, String DirArquivosSecretosUser, String DirArquivosNuvem, String senha) throws Exception
	{
		String ret[] = null;
		
		try
		{		
			if(diretorio != null)
			{
				if(pathDirNuvem != null && !pathDirNuvem.isEmpty())
				{
					if(diretorio.DirArquivosNuvem.equals(pathDirNuvem))
					{
						ret = new String[2];
						ret[0] = diretorio.DirArquivoConfig.replace(" ", "--");
						ret[1] = diretorio.DirArquivosSecretosUser;
					}
					else
					{
						String[] ind2 = pathDirNuvem.split(DirNuvem.toString());
						
						if(ind2.length > 0)
						{
							int indPasta = ind2[1].indexOf('/') + 1;
							
							if(indPasta > 0)
							{
								String pasta = ind2[1].substring(0, indPasta);
								
								if(diretorio.ListSubDiretorios != null && diretorio.ListSubDiretorios.size() > 0)
								{
									for(int i=0; i<diretorio.ListSubDiretorios.size(); i++)
									{
										if(diretorio.ListSubDiretorios.get(i).DirArquivosNuvem != null && !diretorio.ListSubDiretorios.get(i).DirArquivosNuvem.isEmpty())
										{
											if(diretorio.ListSubDiretorios.get(i).DirArquivosNuvem.equals(DirNuvem + pasta))
											{
												if(diretorio.ListSubDiretorios.get(i).DirArquivosNuvem.equals(pathDirNuvem))
												{
													ret = new String[2];
													ret[0] = diretorio.ListSubDiretorios.get(i).DirArquivoConfig;
													ret[1] = diretorio.ListSubDiretorios.get(i).DirArquivosSecretosUser;
													
													if(ret != null && ret.length > 0)
														break;
												}
												else
												{
													ret = RetornarPathArquivoEstadosPeloDirNuvem(diretorio.ListSubDiretorios.get(i), pathDirNuvem, DirNuvem + pasta, DirArquivoEstados, DirArquivosSecretosUser, DirNuvem, senha);
													
													if(ret != null && ret.length > 0)
														break;
												}
											}
											
											/*
											else
											{
												if(diretorio.ListSubDiretorios.get(i).ListSubDiretorios != null && diretorio.ListSubDiretorios.get(i).ListSubDiretorios.size() > 0)
												{
													ret = RetornarPathArquivoEstadosPeloDirNuvem(diretorio.ListSubDiretorios.get(i), pathDirNuvem, DirNuvem + pasta, DirNuvem, DirArquivoEstados, DirArquivosSecretosUser, senha);
													
													if(ret != null && ret.length > 0)
														break;
												}
											}
											*/
										}							
									}		
								}
								else
								{
									/*
									//LOOP INFINITO...
									
									DiretoriosDosArquivos dir = CriarArvoreDiretorios(DirArquivoEstados, DirArquivosSecretosUser, DirArquivosNuvem, senha);
									
									ret = RetornarPathArquivoEstadosPeloDirNuvem(dir, pathDirNuvem, DirNuvem, DirArquivoEstados, DirArquivosSecretosUser, DirArquivosNuvem, senha);
									
									*/
								}
							}						
						}
						else if(ind2.length == 0)
						{
							ret = new String[2];
							ret[0] = diretorio.DirArquivoConfig;
							ret[1] = diretorio.DirArquivosSecretosUser;
						}					
					}				
				}			
			}
			
			if(ret != null && ret.length > 0)
			{
				ret[0].replace(" ", "--");
				return ret;
			}
		}
		catch(Exception e)
		{
			throw new Exception("Erro ao retornar path do arquivo de estados do diretÃ³rio da Nuvem! Erro: " + e.getMessage());
		}
		
		return ret;
	}
	
	public static void VerificarArquivosCifrar(String DirArquivosSecretosUser, String DirNuvem, String DirArquivoConfigXml, String senhaUsuario) throws Exception
	{
		try
		{
			File dirArquivosUser = new File(DirArquivosSecretosUser);
			
			File arquivosDirUser[]  = dirArquivosUser.listFiles();
			
			Arquivos arquivosDirUserList = new Arquivos();
			
			if(arquivosDirUser != null)
			{
				//Adicionar arquivos na lista
				for(int i = 0; i< arquivosDirUser.length; i++) 
				{
					File each = arquivosDirUser[i];
					
					if(each != null)  
					{
						Date lastModified = new Date(each.lastModified());
			        	
						arquivosDirUserList.AdicionarNovoRegistro(each.getName(), each.getAbsolutePath(), lastModified, each.isDirectory(), each.getAbsolutePath().substring(0, each.getAbsolutePath().indexOf(each.getName())), null, "", "", true, "");
					}
				}
			}
			//else
				//throw new Exception("NÃ£o foi possÃ­vel encontrar o diretÃ³rio dos arquivos para serem cifrados! O diretÃ³rio dos arquivos do usuÃ¡rio Ã© invÃ¡lido");
					
			File arquivoConfig = new File(DirArquivoConfigXml);
			
			//Arquivos a serem criptografados e enviados para a pasta da nuvem
        	List<Arquivos> novosArquivos = new ArrayList<Arquivos>();
        	
        	//Arquivos que jÃ¡ foram criptografados e se encontram na pasta da nuvem
        	List<Arquivos> antigosArquivos = new ArrayList<Arquivos>();
        	
        	//Lista de caminhos dos diretÃ³rios que devem ter seus arquivos criptografados e enviados para a pasta da nuvem
        	List<DiretoriosDosArquivos> arquivosAddOutrosDir = new ArrayList<DiretoriosDosArquivos>();
        	
        	//Escritor - arquivo de estados XML
        	Escritor escritorArqEstados = new Escritor();
        	
        	String pathDirEncrypt = "";
        	        	
        	if(!arquivoConfig.exists())
        	{
        		try
				{        			
        			new File(arquivoConfig.getParent()).mkdirs();
        			
        			arquivoConfig.createNewFile();
        			 
        			pathDirEncrypt = DirNuvem;
        			
        			//Inicinado o Escritor para a escrito no arquivo de estados XML 
        			escritorArqEstados.IniciarEscritaArquivoEstados(DirArquivoConfigXml, pathDirEncrypt);
	    			
	    			//Finalizando Escritor - O arquivo Ã© criado e entÃ£o fechado para posterior escrita
        			escritorArqEstados.FinalizarEscritaArquivoEstados();
        			
					novosArquivos.addAll(arquivosDirUserList.arquivosList);
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
        			//Faz a leitura dos arquivos presentes no arquivo de configuraÃ§Ã£o e armazena suas infor. em "arq"
					pathDirEncrypt = Leitura(DirArquivoConfigXml, true);
					
					//Adiciona todos arquivos presentes no diretÃ³rio do usuÃ¡rio em uma nova lista para manipulaÃ§Ã£o
					novosArquivos.addAll(arquivosDirUserList.arquivosList);
										
					//Verifica quais sÃ£o os arquivos que presentes no diretÃ³rio do usuÃ¡rio que nÃ£o foram criptografados e armazenados na pasta da nuvem
					for(int i = 0; i< arquivosDirUserList.arquivosList.size(); i++)
					{
						String path1 =arquivosDirUserList.arquivosList.get(i).pathArquivo;
						
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
									if(arquivosDirUserList.arquivosList.get(i).diretorio)
									{
										String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										
										if(nomeArEncrypt.length() > 30)
										{
											nomeArEncrypt = nomeArEncrypt.substring(0, 29);
										}
										
										String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
										
										String nomeArquivo = arquivosDirUserList.arquivosList.get(i).nomeArquivo.replace(" ", "--");
										
										String novoPathArqConf  = DirArquivoConfigXml.substring(0, DirArquivoConfigXml.lastIndexOf('/') + 1) + nomeArquivo + "/config.xml";
										
										//Criando uma lista para armazenar os diretÃ³rios encontrados na pasta sendo analisada
						            	//Posteriormente, estes diretÃ³rios serÃ£o analisados em busca de arquivos para criptografÃ¡-los e armazenÃ¡-los
						            	//em uma pasta equivalemte na pasta da nuvem
						            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
						            	
						            	if(!DirArquivosSecretosUser.endsWith("/"))
						            		DirArquivosSecretosUser = DirArquivosSecretosUser + "/";
						            	
						            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivosDirUserList.arquivosList.get(i).nomeArquivo + "/";
						            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
						            	arqDir.DirArquivoConfig = novoPathArqConf;
						            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
						            	
						            	arquivosAddOutrosDir.add(arqDir);
						            	
									}
									
									//Verifica a Ãºltima data/horÃ¡rio de modificaÃ§Ã£o do arquivo. Se for o mesmo, o arquivo nÃ£o precisa ser encriptado
									//Se a data do arquivo da pasta do usuÃ¡rio for mais recente que a data do arquivo cifrado na nuvem, entÃ£o ele precisa ser encriptado
									if(arquivosDirUserList.arquivosList.get(i).dataUltimaMod.compareTo(arq.arquivosList.get(j).dataUltimaMod) <= 0 || arquivosDirUserList.arquivosList.get(i).diretorio)
									{
										novosArquivos.get(index).dataUltimaModArqEncrypt = arq.arquivosList.get(j).dataUltimaModArqEncrypt;
										novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
										novosArquivos.get(index).pathArquivoEncrypt = arq.arquivosList.get(j).pathArquivoEncrypt;
										novosArquivos.get(index).pathArqEstados = arq.arquivosList.get(j).pathArqEstados;
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
        	
        	//Caso em que um arquivo foi deletado da pasta do usuÃ¡rio (Arquivos secretos): ele necessita ser apagado da pasta da nuvem e do arquivo de config.
        	if(arq.arquivosList != null && arq.arquivosList.size() > 0)
			{        		
        		//Se nÃ£o houver a necessidade de criptografar nenhum arquivo na iteraÃ§Ã£o em que hÃ¡ arquivos para serem deletados,
        		//deve-se criar o arquivo de config novamente, uma vez que quando um arquivo for deletado, ele tambÃ©m deve ser excluÃ­do 
        		//do arquivo de configuraÃ§Ã£o
        		if(novosArquivos == null || novosArquivos.size() == 0)
        		{
        			//Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos que jÃ¡ estÃ£o cifrados e armazenados na pasta da nuvem
        			if(antigosArquivos != null && antigosArquivos.size() > 0)
            		{
        				//Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos
        				escritorArqEstados.IniciarEscritaArquivoEstados(arquivoConfig.getPath(), pathDirEncrypt);
    	    	        
        				for(int i=0; i < antigosArquivos.size(); i++)
                		{
        					escritorArqEstados.EscreverNovoElemento(antigosArquivos.get(i), i);        		        					
                		}
        				
        				//Finalizando e escrevendo no arquivo...
        				escritorArqEstados.FinalizarEscritaArquivoEstados();
        				
            		}
        			//Se nÃ£o hÃ¡ nenhum arquivo antigo, Ã© necessÃ¡rio apenas criar um arquivo de configuraÃ§Ã£o com a estrutura bÃ¡sica
        			else
        			{
        				escritorArqEstados.IniciarEscritaArquivoEstados(arquivoConfig.getPath(), pathDirEncrypt);
	        			escritorArqEstados.FinalizarEscritaArquivoEstados();
        			}        			
        		}
        		
        		//Varrendo a lista de arquivos para buscar e deletar os arquivos presentes nela
        		for(int i=0; i < arq.arquivosList.size(); i++)
        		{
        			String nomeEncrypted = arq.arquivosList.get(i).nomeArquivoEncrypt;
    				
    				if(nomeEncrypted.length() > 30)
    					nomeEncrypted = nomeEncrypted.substring(0, 29);
    				
        			//Deletando arquivo cifrado da pasta da nuvem
        			if(!arq.arquivosList.get(i).diretorio)
        			{
        				File tempFile = new File(DirNuvem + nomeEncrypted + ".encrypted");
            			
        				if(tempFile != null && tempFile.exists());
        					tempFile.delete();
        					        					
    					lock.lock();
    	    	        
    	    	        if(eventosGeradosAplicacaoPastaNuvem != null)
    	    	        	eventosGeradosAplicacaoPastaNuvem.add(DirNuvem + nomeEncrypted + ".encrypted");
    	    	        else
        	        	{
    	    	        	eventosGeradosAplicacaoPastaNuvem = new ArrayList<String>();
    	    	        	eventosGeradosAplicacaoPastaNuvem.add(DirNuvem + nomeEncrypted + ".encrypted");
        	        	}
    	    	        
    	    	        lock.unlock();
        			}
        			else
        			{
        				//Se for diretÃ³rio, primeiro deve-se deletar todos os seus arquivos para que entÃ£o o diretÃ³rio seja deletado
        				File tempFile = new File(DirNuvem + nomeEncrypted);
            			
        				if(tempFile != null && tempFile.exists());
        				{
        					RemoverArquivos(tempFile);        					
        				}        					
        				
        				//tempFile.delete();
        				
        				//Deletando o arquivo de configuraÃ§Ã£o pertinente ao diretÃ³rio sendo deletado
        				
        				int indArquivo = arq.arquivosList.get(i).pathArqEstados.lastIndexOf('/');
        				String arquivo = "";
        				
        				if(indArquivo > 0)
        					arquivo = arq.arquivosList.get(i).pathArqEstados.substring(0, indArquivo);
        				else
        					arquivo = arq.arquivosList.get(i).pathArqEstados;
        				
        				File tempFileConfig = new File(arquivo);     
        				
        				if(tempFileConfig != null && tempFileConfig.exists())
        					RemoverArquivos(tempFileConfig);        				
        				
        				lock.lock();
    	    	        
    	    	        if(eventosGeradosAplicacaoPastaNuvem != null)
    	    	        	eventosGeradosAplicacaoPastaNuvem.add(DirNuvem + nomeEncrypted);
    	    	        else
        	        	{
    	    	        	eventosGeradosAplicacaoPastaNuvem = new ArrayList<String>();
    	    	        	eventosGeradosAplicacaoPastaNuvem.add(DirNuvem + nomeEncrypted);
        	        	}
    	    	        
    	    	        lock.unlock();
        			}
        		}        		
			}         	
        	        	
        	//Os arquivos sÃ£o criptogrados e armazenados na pasta da nuvem 
        	if(novosArquivos != null && novosArquivos.size() > 0)
	        {
        		//Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos
				escritorArqEstados.IniciarEscritaArquivoEstados(arquivoConfig.getPath(), pathDirEncrypt);
    			
    			//contador necessÃ¡rio para controlar o ID. 
    	        //Aqui sÃ£o escritos no arquivo de configuraÃ§Ã£o os arquivos que jÃ¡ se encontram cifrados na pasta da nuvem
    	        //O contador serÃ¡ utilizado no passo seguinte, onde os novos arquivos a serem cifrados devem continuar do nÃºmero parado neste loop
	            int contId = 0;
    	        
	            //Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos que jÃ¡ estÃ£o cifrados e armazenados na pasta da nuvem
    	        if(antigosArquivos != null && antigosArquivos.size() > 0)
    	        {
    	        	for(int i=0; i < antigosArquivos.size(); i++)
    	        	{
    	        		escritorArqEstados.EscreverNovoElemento(antigosArquivos.get(i), i);
    		            contId++;
    	        	}
    	        }
    	        
    	        //Criptografando os arquivo para que sejam armazenados na pasta da nuvem
    	        for(int i = 0; i < novosArquivos.size(); i++)
		        {
    	        	//Escrevendo os dados dos novos arquivos no arquivo de configuraÃ§Ã£o
    	        	
    	        	String nomeEncrypted = "";
    	        	
		            if(!novosArquivos.get(i).diretorio)
		            {
		            	novosArquivos.get(i).diretorio = false;
		            			
		            	//Criptografando o arquivo
		            	if(novosArquivos.get(i).nomeArquivoEncrypt != null && !novosArquivos.get(i).nomeArquivoEncrypt.isEmpty())
		            		novosArquivos.get(i).nomeArquivoEncrypt = CriptoUtil.CriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirNuvem + novosArquivos.get(i).nomeArquivo), senhaUsuario, novosArquivos.get(i).nomeArquivoEncrypt);
		            	else
		            		novosArquivos.get(i).nomeArquivoEncrypt = CriptoUtil.CriptografarArquivo(new File(novosArquivos.get(i).pathArquivo), new File(DirNuvem + novosArquivos.get(i).nomeArquivo), senhaUsuario, null);
		            	
		            	
		            	nomeEncrypted = novosArquivos.get(i).nomeArquivoEncrypt;
		            	
		            	if(nomeEncrypted.length() > 30)
		            		nomeEncrypted = nomeEncrypted.substring(0, 29);
		            	
		            	if(nomeEncrypted != null && !nomeEncrypted.isEmpty())
		            	{
		            		Date lastModified = new Date(new File(DirNuvem + nomeEncrypted + ".encrypted").lastModified());
			            	
		            		novosArquivos.get(i).dataUltimaModArqEncrypt = lastModified;
		            	}
		            	else
		            		throw new Exception("Erro ao cifrar arquivo! ");
		            			            	
		            	lock.lock();
    	    	        
    	    	        if(eventosGeradosAplicacaoPastaNuvem != null)
    	    	        	eventosGeradosAplicacaoPastaNuvem.add(DirNuvem + nomeEncrypted + ".encrypted");
    	    	        else
        	        	{
    	    	        	eventosGeradosAplicacaoPastaNuvem = new ArrayList<String>();
    	    	        	eventosGeradosAplicacaoPastaNuvem.add(DirNuvem + nomeEncrypted + ".encrypted");
        	        	}
    	    	        
    	    	        lock.unlock();     		            			            	
		            }
		            else
		            {
		            	//Criptografando os diretÃ³rios - Na verdade Ã© criado um novo diretÃ³rio na pasta da nuvem e posteriormente inserido os 
		            	//arquivos nele, todos criptografados.
		            	
		            	//Criptografando o nome do diretÃ³rio
		            	
		            	File pastatemp;
		            	if(novosArquivos.get(i).nomeArquivoEncrypt != null && !novosArquivos.get(i).nomeArquivoEncrypt.isEmpty())
	            		{
		            		nomeEncrypted = arq.arquivosList.get(i).nomeArquivoEncrypt;
		    				
		    				if(nomeEncrypted.length() > 30)
		    					nomeEncrypted = nomeEncrypted.substring(0, 29);
		    				
	            			pastatemp = new File(DirNuvem + nomeEncrypted);
			            	
			            	if(!pastatemp.exists())
		            		{
			            		String hashNome = HashGeneratorUtils.generateSHA256(novosArquivos.get(i).nomeArquivo.toString());
				            	
			            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
			            	
			            		Base32 codec = new Base32();
							
			            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(novosArquivos.get(i).nomeArquivo.toString(), key);
			            		novosArquivos.get(i).nomeArquivoEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
			            		
			            		if(novosArquivos.get(i).nomeArquivoEncrypt.length() > 30)
			            			nomeEncrypted = novosArquivos.get(i).nomeArquivoEncrypt.substring(0, 29);
			            		else
			            			nomeEncrypted = novosArquivos.get(i).nomeArquivoEncrypt;
			            		
			            		pastatemp = new File(DirNuvem + nomeEncrypted);
			            		
			            		if(!pastatemp.exists())
			            			pastatemp.mkdir();
			            		
			            		FileWriter arqChaveArquivo = new FileWriter(DirNuvem + nomeEncrypted + "/keyFolder.encrypted"); 
								PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
								
								String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
								
								gravarArqChaveArquivo.println("chave="+keyCifrada);
								gravarArqChaveArquivo.println("nomePastaEncrypt="+novosArquivos.get(i).nomeArquivoEncrypt);
								
								arqChaveArquivo.close();
								gravarArqChaveArquivo.close();
		            		}			            	
		            	}
		            	else
		            	{
		            		nomeEncrypted = "";
		            		
		            		String hashNome = HashGeneratorUtils.generateSHA256(novosArquivos.get(i).nomeArquivo.toString());
		            	
		            		SecretKey key = CriptoUtil.GerarChaveSimetricaHashArquivo(hashNome);
		            	
		            		Base32 codec = new Base32();
						
		            		byte[] nomeArquivoEncryptBytes = CriptoSimetrica.CriptografarMensagem(novosArquivos.get(i).nomeArquivo.toString(), key);
		            		novosArquivos.get(i).nomeArquivoEncrypt = codec.encodeToString(nomeArquivoEncryptBytes);
				        
		            		if(novosArquivos.get(i).nomeArquivoEncrypt.length() > 30)
		            			nomeEncrypted = novosArquivos.get(i).nomeArquivoEncrypt.substring(0, 29);
		            		else
		            			nomeEncrypted = novosArquivos.get(i).nomeArquivoEncrypt;
		            		
		            		pastatemp = new File(DirNuvem + nomeEncrypted);
			            	
			            	if(!pastatemp.exists())
			            		pastatemp.mkdir();
			            	
			            	FileWriter arqChaveArquivo = new FileWriter(DirNuvem + nomeEncrypted + "/keyFolder.encrypted"); 
							PrintWriter gravarArqChaveArquivo = new PrintWriter(arqChaveArquivo); 
							
							String keyCifrada = CriptoUtil.CriptografarChaveSimetrica(key);
							
							gravarArqChaveArquivo.println("chave="+keyCifrada);
							gravarArqChaveArquivo.println("nomePastaEncrypt="+novosArquivos.get(i).nomeArquivoEncrypt);
							
							arqChaveArquivo.close();
							gravarArqChaveArquivo.close();
		            	}
		            	
		            	novosArquivos.get(i).dataUltimaModArqEncrypt = new Date(pastatemp.lastModified());

		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo.replace(' ', '-') + "-config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt +"/config.xml";
		            	
		            	String novoPathArqConfEncrypted = DirNuvem + nomeEncrypted + "/config.xml";
		            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + nomeArEncrypt + "-config.xml";
		            	
		            	//Config-files/config.xml
		            			            	
		            	//Criando uma lista para armazenar os diretÃ³rios encontrados na pasta sendo analisada
		            	//Posteriormente, estes diretÃ³rios serÃ£o analisados em busca de arquivos para criptografÃ¡-los e armazenÃ¡-los
		            	//em uma pasta equivalemte na pasta da nuvem
		            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
		            	
		            	String nomeArquivoSemEspaco = arquivosDirUserList.arquivosList.get(i).nomeArquivo.replace(" ", "--");
		            	
		            	String novoPathArqConf  = DirArquivoConfigXml.substring(0, DirArquivoConfigXml.lastIndexOf('/') + 1) + nomeArquivoSemEspaco + "/config.xml";
		            	
		            	novosArquivos.get(i).pathArqEstados = novoPathArqConf;
		            	novosArquivos.get(i).diretorio = true;
		            	
		            	if(!DirArquivosSecretosUser.endsWith("/"))
		            		DirArquivosSecretosUser = DirArquivosSecretosUser + "/";
		            	
		            	
		            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arquivosDirUserList.arquivosList.get(i).nomeArquivo + "/";
		            	arqDir.DirArquivosNuvem = DirNuvem + nomeEncrypted + "/";
		            	arqDir.DirArquivoConfig = novoPathArqConf;
		            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
		            			            			            	
		            	arquivosAddOutrosDir.add(arqDir);
		            			            		            	
		            	lock.lock();
    	    	        
    	    	        if(eventosGeradosAplicacaoPastaNuvem != null)
    	    	        	eventosGeradosAplicacaoPastaNuvem.add(DirNuvem + nomeEncrypted);
    	    	        else
        	        	{
    	    	        	eventosGeradosAplicacaoPastaNuvem = new ArrayList<String>();
    	    	        	eventosGeradosAplicacaoPastaNuvem.add(DirNuvem + nomeEncrypted);
        	        	}
    	    	        
    	    	        lock.unlock();		            	
		            }	            
		            
		            novosArquivos.get(i).pathArquivoEncrypt = DirNuvem + nomeEncrypted;
		            
		            escritorArqEstados.EscreverNovoElemento(novosArquivos.get(i), i+contId);		            
		        }
		        
    	        escritorArqEstados.FinalizarEscritaArquivoEstados();
	        }        	
        	
        	//Verificando as pastas do diretÃ³rio sendo analizado para que os arquivos presentes nela possam ser encriptografados
        	if(arquivosAddOutrosDir != null && arquivosAddOutrosDir.size() > 0)
        	{
        		for(int i = 0; i < arquivosAddOutrosDir.size(); i++)
        		{
        			//Processo recursivo de busca por arquivos para encriptaÃ§Ã£o
        			VerificarArquivosCifrar(arquivosAddOutrosDir.get(i).DirArquivosSecretosUser, arquivosAddOutrosDir.get(i).DirArquivosNuvem, arquivosAddOutrosDir.get(i).DirArquivoConfig, senhaUsuario);
        		}
        	}
		}
		catch(Exception e)
		{
			throw new Exception("Erro na verificaÃ§Ã£o de novos arquivos! Erro: " + e.getMessage());
		}
	}
	
	public static void VerificarArquivosDecifrar(String DirArquivosSecretosUser, String DirNuvem, String DirArquivoConfigXml, String senhaUsuario) throws Exception
	{
		try
		{
			File DirArquivosNuvem = new File(DirNuvem);
			File arquivosDirNuvem[]  = DirArquivosNuvem.listFiles();
			
			if(arquivosDirNuvem != null)
			{
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
							if(fileName.endsWith(".encrypted") || each.isDirectory())
							{
								Date lastModified = new Date(each.lastModified());
			        	
								arquivosDirNuvemList.AdicionarNovoRegistro("", "", null, each.isDirectory(), each.getAbsolutePath().substring(0, each.getAbsolutePath().indexOf(each.getName())), lastModified, each.getName(), each.getAbsolutePath(), false, each.getParent());
							}
						}
					}
				}
				
				//Escritor - arquivo de estados XML
	        	Escritor escritorArqEstados = new Escritor();
				
				File arquivoConfig = new File(DirArquivoConfigXml);
				
				//Arquivo de configuraÃ§Ã£o, responsÃ¡vel por armazenar as informaÃ§Ãµes dos arquivos (nome, caminho, se Ã© diretÃ³rio ou nÃ£o etc)
	        	//File arquivoConfig = new File(pathArquivoConfigTemp);
	        	
	        	//Arquivos a serem decifrados e enviados para a pasta do usuÃ¡rio
	        	List<Arquivos> novosArquivos = new ArrayList<Arquivos>();
	        	
	        	//Arquivos que jÃ¡ foram decifrados e se encontram na pasta do usuÃ¡rio
	        	List<Arquivos> antigosArquivos = new ArrayList<Arquivos>();
	        	
	        	//Lista de caminhos dos diretÃ³rios que devem ter seus arquivos decifrados e enviados para a pasta do usuÃ¡rio
	        	List<DiretoriosDosArquivos> arquivosDecifrarOutrosDir = new ArrayList<DiretoriosDosArquivos>();
	        	
	        	String pathDirEncrypt = "";
	        	
	        	if(!arquivoConfig.exists())
	        	{
	        		try 
					{
	        			new File(arquivoConfig.getParent()).mkdirs();
	        			
	        			arquivoConfig.createNewFile();
	        			 
	        			pathDirEncrypt = DirNuvem;
	        			
	        			//Inicinado o Escritor para a escrito no arquivo de estados XML 
	        			escritorArqEstados.IniciarEscritaArquivoEstados(DirArquivoConfigXml, pathDirEncrypt);
		    			
		    			//Finalizando Escritor - O arquivo Ã© criado e entÃ£o fechado para posterior escrita
	        			escritorArqEstados.FinalizarEscritaArquivoEstados();	        			
	        			
	        			novosArquivos.addAll(arquivosDirNuvemList.arquivosList);
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
	        			//Faz a leitura dos arquivos presentes no arquivo de configuraÃ§Ã£o e armazena suas infor. em "arq"
						pathDirEncrypt = Leitura(DirArquivoConfigXml, false);
						
						//Adiciona todos arquivos presentes no diretÃ³rio da nuvem em uma nova lista para manipulaÃ§Ã£o
						novosArquivos.addAll(arquivosDirNuvemList.arquivosList);
											
						//Verifica quais sÃ£o os arquivos que presentes no diretÃ³rio da nuvem que nÃ£o foram decifrados e armazenados na pasta do usuÃ¡rio
						for(int i = 0; i< arquivosDirNuvemList.arquivosList.size(); i++)
						{
							String name1;
							
							int ind = arquivosDirNuvemList.arquivosList.get(i).nomeArquivoEncrypt.indexOf(".encrypted");
							
							//if(!arquivosDecifrar.get(i).diretorio)
							if(ind > 0)
								name1 = arquivosDirNuvemList.arquivosList.get(i).nomeArquivoEncrypt.substring(0, ind);
							else
								name1 = arquivosDirNuvemList.arquivosList.get(i).nomeArquivoEncrypt;
							
							for(int j = 0; j< arq.arquivosList.size(); j++)
							{		
								String name2 = "";
								
								if(arq.arquivosList.get(j).nomeArquivoEncrypt.length() > 30)
									name2 = arq.arquivosList.get(j).nomeArquivoEncrypt.substring(0, 29);
								else
									name2 = arq.arquivosList.get(j).nomeArquivoEncrypt;
								
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
										if(arquivosDirNuvemList.arquivosList.get(i).diretorio)
										{
											String nomeArEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
											
											if(nomeArEncrypt.length() > 30)
												nomeArEncrypt = nomeArEncrypt.substring(0, 29);
											
											//String novoPathArqConf = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
											
											String nomeArquivo = arq.arquivosList.get(j).nomeArquivo.replace(" ", "--");
											
											String novoPathArqConf  = DirArquivoConfigXml.substring(0, DirArquivoConfigXml.lastIndexOf('/') + 1) + nomeArquivo + "/config.xml";
											
											String novoPathArqConfEncrypted = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
							            	
							            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
							            	
							            	if(!DirArquivosSecretosUser.endsWith("/"))
							            		DirArquivosSecretosUser = DirArquivosSecretosUser + "/";
							            	
							            	if(!DirNuvem.endsWith("/"))
							            		DirNuvem = DirNuvem + "/";
							            	
							            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + arq.arquivosList.get(j).nomeArquivo + "/";
							            	arqDir.DirArquivosNuvem = DirNuvem + nomeArEncrypt + "/";
							            	arqDir.DirArquivoConfig = novoPathArqConf;
							            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
							            	
							            	arquivosDecifrarOutrosDir.add(arqDir);
							            	
										}
										
										//Verifica a Ãºltima data/horÃ¡rio de modificaÃ§Ã£o do arquivo. Se for o mesmo, o arquivo nÃ£o precisa ser encriptado
										//Se a data do arquivo da pasta do usuÃ¡rio for mais recente que a data do arquivo cifrado na nuvem, entÃ£o ele precisa ser encriptado
										if(arquivosDirNuvemList.arquivosList.get(i).dataUltimaModArqEncrypt.compareTo(arq.arquivosList.get(j).dataUltimaModArqEncrypt) <= 0 || arquivosDirNuvemList.arquivosList.get(i).diretorio)
										{
											novosArquivos.get(index).dataUltimaMod = arq.arquivosList.get(j).dataUltimaMod;										
											novosArquivos.get(index).nomeArquivo = arq.arquivosList.get(j).nomeArquivo;
											novosArquivos.get(index).pathArquivo = arq.arquivosList.get(j).pathArquivo;
											novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
											antigosArquivos.add(novosArquivos.get(index));
											novosArquivos.remove(index);
										}
										else
										{
											novosArquivos.get(index).nomeArquivoEncrypt = arq.arquivosList.get(j).nomeArquivoEncrypt;
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
	        	//Adiciando possÃ­veis arquivos que ainda nÃ£o foram enviados para a nuvem no momento da decifragem, para nÃ£o perdÃª-los...
	        	if(arq.arquivosList != null && arq.arquivosList.size() > 0)
	        	{
	        		antigosArquivos.addAll(arq.arquivosList);
	        	}
	        	*/
	        	
	        	
	        	//Arquivos presentes no diretÃ³rio do usuÃ¡rio e no arquivo de configuraÃ§Ã£o, mas que nÃ£o apresentam uma versÃ£o cifrada na nuvem
	        	//Provavelmente sÃ£o arquivos que foram excluÃ­dos em outros dispositivos.
	        	//Deve-se excluÃ­dos e apagÃ¡-los do arquivo de configuraÃ§Ã£o
	        	if(arq.arquivosList != null && arq.arquivosList.size() > 0)
	        	{
	        		//antigosArquivos.addAll(arq.arquivosList);
	        		
	        		//Se nÃ£o houver a necessidade de criptografar nenhum arquivo na iteraÃ§Ã£o em que hÃ¡ arquivos para serem deletados,
	        		//deve-se criar o arquivo de config novamente, uma vez que quando um arquivo for deletado, ele tambÃ©m deve ser excluÃ­do 
	        		//do arquivo de configuraÃ§Ã£o
	        		if(novosArquivos == null || novosArquivos.size() == 0)
	        		{
		        		if(antigosArquivos != null && antigosArquivos.size() != 0)
		        		{
		        			//Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos 
		        			
		        			//Inicinado o Escritor para a escrita no arquivo de estados XML 
		        			escritorArqEstados.IniciarEscritaArquivoEstados(DirArquivoConfigXml, pathDirEncrypt);
			    			
		    				for(int i=0; i < antigosArquivos.size(); i++)
		            		{
		    					escritorArqEstados.EscreverNovoElemento(antigosArquivos.get(i), i);		    					
	    		            }
		    				
		    				//Finalizando Escritor
		        			escritorArqEstados.FinalizarEscritaArquivoEstados();
		        		}
		        		else 
		    			{
		        			//Inicinado o Escritor para a escrita no arquivo de estados XML 
		        			escritorArqEstados.IniciarEscritaArquivoEstados(DirArquivoConfigXml, pathDirEncrypt);
		        			
		        			//Finalizando Escritor - O arquivo Ã© criado e entÃ£o fechado para posterior escrita
		        			escritorArqEstados.FinalizarEscritaArquivoEstados(); 
		    			}   	        		
	        		}
	        		        		
	        		//Varrendo a lista de arquivos para buscar e deletar os arquivos presentes nela
	        		for(int i=0; i < arq.arquivosList.size(); i++)
	        		{
	        			//Deletando arquivo em claro da pasta da aplicaÃ§Ã£o
	        			if(!arq.arquivosList.get(i).diretorio)
	        			{
	        				File tempFile = new File(DirArquivosSecretosUser + arq.arquivosList.get(i).nomeArquivo);
	            			
	        				if(tempFile != null && tempFile.exists());
	        					tempFile.delete();
	        			}
	        			else
	        			{
	        				//Se for diretÃ³rio, primeiro deve-se deletar todos os seus arquivos para que entÃ£o o diretÃ³rio seja deletado
	        				File tempFile = new File(DirArquivosSecretosUser + arq.arquivosList.get(i).nomeArquivo);
	            			
	        				if(tempFile != null && tempFile.exists());
	        				{
	        					RemoverArquivos(tempFile);
	        				}        					
	        				
	        				//tempFile.delete();
	        				
	        				//Deletando o arquivo de configuraÃ§Ã£o pertinente ao diretÃ³rio sendo deletado
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
	        			
	        			lock.lock();
		    	        
		    	        if(eventosGeradosAplicacaoPastaUser != null)
		    	        	eventosGeradosAplicacaoPastaUser.add(DirArquivosSecretosUser + arq.arquivosList.get(i).nomeArquivo);
		    	        else
	    	        	{
		    	        	eventosGeradosAplicacaoPastaUser = new ArrayList<String>();
		    	        	eventosGeradosAplicacaoPastaUser.add(DirArquivosSecretosUser + arq.arquivosList.get(i).nomeArquivo);
	    	        	}
		    	        
		    	        lock.unlock();
	        		} 	        		
	    		}
	        	
	        	//Arquivos que ainda nÃ£o foram decifrados serÃ£o neste momento
	        	if(novosArquivos != null && novosArquivos.size() > 0)
		        {
	        		//Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos
	        		escritorArqEstados.IniciarEscritaArquivoEstados(DirArquivoConfigXml, pathDirEncrypt);
	    	                  
	    	        //contador necessÃ¡rio para controlar o ID. 
	    	        //Aqui sÃ£o escritos no arquivo de configuraÃ§Ã£o os arquivos que jÃ¡ se encontram decifrados na pasta do usuÃ¡rio
	    	        //O contador serÃ¡ utilizado no passo seguinte, onde os novos arquivos a serem decifrados devem continuar do nÃºmero parado neste loop
		            int contId = 0;
	    	        
		            //Escrevendo no arquivo de configuraÃ§Ã£o os dados dos arquivos que jÃ¡ estÃ£o decifrados e armazenados na pasta do usuÃ¡rio
	    	        if(antigosArquivos != null && antigosArquivos.size() > 0)
	    	        {
	    	        	for(int i=0; i < antigosArquivos.size(); i++)
	    	        	{
	    	        		int indNome = antigosArquivos.get(i).nomeArquivoEncrypt.indexOf(".encrypted");
	    		            int indPath = antigosArquivos.get(i).pathArquivoEncrypt.indexOf(".encrypted");
							
							//if(!arquivosDecifrar.get(i).diretorio)
							if(indNome > 0)
								antigosArquivos.get(i).nomeArquivoEncrypt = antigosArquivos.get(i).nomeArquivoEncrypt.substring(0, indNome);
														
							if(indPath > 0)
								antigosArquivos.get(i).pathArquivoEncrypt = antigosArquivos.get(i).pathArquivoEncrypt.substring(0, indPath);
							
							//Pasta ou diretÃ³rio tem um tratamento diferente
	    		            //Seus nomes nÃ£o possuem a extensÃ£o .encrypted no fim
	    		            if(!antigosArquivos.get(i).diretorio)
	    		            {
	    		            	//Obtendo o caminho do arquivo na pasta da nuvem e o trocando por um equivalente na pasta do usuÃ¡rio
	        		            //String pathEncrypted = antigosArquivos.get(i).pathArquivo.replace(DirNuvem, DirArquivosSecretosUser);
	        		            
	        		            //String path = pathEncrypted.substring(0, pathEncrypted.indexOf(".encrypted"));
	        		            
	        		            //Caminho do arquivo de configuraÃ§Ã£o da pasta
	        		            //Cada pasta tem um arquivo de configuraÃ§Ã£o diferente
	    		            	
	    		            	antigosArquivos.get(i).pathArqEstados = "";
	    		            }
	    		            
	    		            antigosArquivos.get(i).pathArquivo = antigosArquivos.get(i).pathArquivo.replace(DirNuvem, DirArquivosSecretosUser);
	        		        
	    		            escritorArqEstados.EscreverNovoElemento(antigosArquivos.get(i), i);
	    		            contId++;	    		            
	    	        	}
	    	        }
	    	        
	    	        //Decifrando os arquivos que ainda nÃ£o se encontram na pasta do usuÃ¡rio em formato em claro
			        for(int i = 0; i < novosArquivos.size(); i++)
			        {
			        	int indNome = novosArquivos.get(i).nomeArquivoEncrypt.indexOf(".encrypted");
			            int indPath = novosArquivos.get(i).pathArquivoEncrypt.indexOf(".encrypted");
			            
			            if(indNome > 0)
			            	novosArquivos.get(i).nomeArquivoEncrypt = novosArquivos.get(i).nomeArquivoEncrypt.substring(0, indNome);
						
						if(indPath > 0)
							novosArquivos.get(i).pathArquivoEncrypt = novosArquivos.get(i).pathArquivoEncrypt.substring(0, indPath);
						
						if(!novosArquivos.get(i).diretorio)
			            {
			            	//nomeArquivoOriginal = novosArquivos.get(i).nomeArquivo.substring(0, novosArquivos.get(i).nomeArquivo.indexOf(".encrypted"));
	    		            
	    		            //nomeArquivo.setText(nomeArquivoOriginal);
	    		            
							String nomeEncrypted = novosArquivos.get(i).nomeArquivoEncrypt;
							
							if(nomeEncrypted.length() > 30)
	    		            	nomeEncrypted = nomeEncrypted.substring(0, 29);
							
	    		            String pathEncrypted = novosArquivos.get(i).pathArquivoEncrypt.replace(DirNuvem, DirArquivosSecretosUser);
	    		            
	    		            String path = pathEncrypted.substring(0, pathEncrypted.indexOf(nomeEncrypted));
	    		            
	    		            //Caminho do arquivo de configuraÃ§Ã£o da pasta
	    		            //Cada pasta tem um arquivo de configuraÃ§Ã£o diferente
	    		            novosArquivos.get(i).pathArqEstados = "";
	    		            
	    		            String[] ret = new String[2];
	    		            
	    		            //Decifrando o arquivo - APENAS ARQUIVOS
	    		            if(novosArquivos.get(i).nomeArquivo != null && !novosArquivos.get(i).nomeArquivo.isEmpty())
	    		            {
	    		            	ret = CriptoUtil.DescriptografarArquivo(new File(novosArquivos.get(i).pathArquivoEncrypt + ".encrypted"), new File(DirArquivosSecretosUser + novosArquivos.get(i).nomeArquivo), senhaUsuario, novosArquivos.get(i).nomeArquivo);
	    		            	
	    		            	novosArquivos.get(i).nomeArquivoEncrypt = ret[0];
	    		            	novosArquivos.get(i).nomeArquivo = ret[1];
	    		            	
		            		}
	    		            else
    		            	{
	    		            	ret = CriptoUtil.DescriptografarArquivo(new File(novosArquivos.get(i).pathArquivoEncrypt + ".encrypted"), new File(DirArquivosSecretosUser + nomeEncrypted), senhaUsuario, null);
	    		            	
	    		            	novosArquivos.get(i).nomeArquivoEncrypt = ret[0];
	    		            	novosArquivos.get(i).nomeArquivo = ret[1];
    		            	}
	    		            
	    		            //Data da Ãºltima modificaÃ§Ã£o do arquivo
	    		            novosArquivos.get(i).dataUltimaMod = new Date(new File(DirArquivosSecretosUser + novosArquivos.get(i).nomeArquivo).lastModified());
			            	novosArquivos.get(i).pathArquivo = path + novosArquivos.get(i).nomeArquivo;
			            	
			            }
			            else
			            {
			            	//DiretÃ³rios/Patas
			            	
			            	//nomeArquivoOriginal = novosArquivos.get(i).nomeArquivo;
			            	
			            	//nomeArquivo.setText(nomeArquivoOriginal);
			            	
			            				            	
			            	File pastatemp;
			            	
			            	if(novosArquivos.get(i).nomeArquivo != null && !novosArquivos.get(i).nomeArquivo.isEmpty())
		            		{
			            		pastatemp = new File(DirArquivosSecretosUser + novosArquivos.get(i).nomeArquivo);
				            	
				            	if(!pastatemp.exists())
			            		{
				            		Base32 codec = new Base32();
				            		
				            		FileReader arqNome = new FileReader(novosArquivos.get(i).pathArquivoEncrypt + "/keyFolder.encrypted");
									BufferedReader lerArqNome = new BufferedReader(arqNome);
						        	      
						        	String linhaNome = lerArqNome.readLine(); // lÃª a primeira linha
									// a variÃ¡vel "linha" recebe o valor "null" quando o processo 
									// de repetiÃ§Ã£o atingir o final do arquivo texto
						        	
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
										
										linhaNome = lerArqNome.readLine(); // lÃª da segunda atÃ© a Ãºltima linha
									} 
				
									arqNome.close();
									lerArqNome.close();
									
									Key chaveDecifrada = CriptoUtil.DescriptografarChaveSimetrica(chaveCifrada, senhaUsuario);
									
									novosArquivos.get(i).nomeArquivo = CriptoSimetrica.DesencriptarMensagem(codec.decode(nomeCifrado), (SecretKey)chaveDecifrada);									
			            		}				            	
			            	}
			            	else
		            		{
			            		Base32 codec = new Base32();
			            		
			            		FileReader arqNome = new FileReader(novosArquivos.get(i).pathArquivoEncrypt + "/keyFolder.encrypted");
								BufferedReader lerArqNome = new BufferedReader(arqNome);
					        	      
					        	String linhaNome = lerArqNome.readLine(); // lÃª a primeira linha
								// a variÃ¡vel "linha" recebe o valor "null" quando o processo 
								// de repetiÃ§Ã£o atingir o final do arquivo texto
					        	
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
									
									linhaNome = lerArqNome.readLine(); // lÃª da segunda atÃ© a Ãºltima linha
								} 
			
								arqNome.close();
								lerArqNome.close();

								Key chaveDecifrada = CriptoUtil.DescriptografarChaveSimetrica(chaveCifrada, senhaUsuario);
								
								novosArquivos.get(i).nomeArquivo = CriptoSimetrica.DesencriptarMensagem(codec.decode(nomeCifrado), (SecretKey)chaveDecifrada);
							}
			            	
		            		//nomeArquivoOriginal = CriptoUtil.DescriptografarMensagemAlgAssimetrico(novosArquivos.get(i).nomeArquivoEncrypt, senhaUsuario);
	    		            
	    		            String pathEncrypted = novosArquivos.get(i).pathArquivoEncrypt.replace(DirNuvem, DirArquivosSecretosUser);
	    		            
	    		            String nomeEncrypted = novosArquivos.get(i).nomeArquivoEncrypt;
	    		            
	    		            if(nomeEncrypted.length() > 30)
	    		            	nomeEncrypted = nomeEncrypted.substring(0, 29);
	    		            
	    		            String path = pathEncrypted.substring(0, pathEncrypted.indexOf(nomeEncrypted));
	    		            
	    		            novosArquivos.get(i).pathArquivo = path + novosArquivos.get(i).nomeArquivo;
	    		            
	    		            //Decifrando os diretÃ³rios
			            	//File pastatemp = new File(DirNuvem + novosArquivos.get(i).nomeArquivo);
	    		            pastatemp = new File(path + novosArquivos.get(i).nomeArquivo);
			            	
			            	if(!pastatemp.exists())
			            	{
			            		pastatemp.mkdir();
			            	}
			            	
			            	//Data da Ãºltima modificaÃ§Ã£o da pasta
			            	novosArquivos.get(i).dataUltimaMod = new Date(pastatemp.lastModified());
			            	
			            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivo.replace(' ', '-') + "-config.xml";
			            	//String novoPathArqConf = pathArquivoConfig.substring(0, pathArquivoConfig.lastIndexOf('/') + 1) + novosArquivos.get(i).nomeArquivoEncrypt +"-config.xml";
			            	
			            	//String nomeArEncrypt = novosArquivos.get(i).nomeArquivoEncrypt;
							//String novoPathArqConf = DirNuvem + nomeArEncrypt + "/config.xml.encrypted";
			            	
			            	String nomeArquivoSemEspaco = novosArquivos.get(i).nomeArquivo.replace(" ", "--");
			            	
			            	String novoPathArqEstados  = DirArquivoConfigXml.substring(0, DirArquivoConfigXml.lastIndexOf('/') + 1) + nomeArquivoSemEspaco + "/config.xml";
			            	
			            	String novoPathArqConfEncrypted = DirNuvem + nomeEncrypted + "/config.xml.encrypted";
			            	
			            	novosArquivos.get(i).pathArqEstados = novoPathArqEstados;
			            	
			            	DiretoriosDosArquivos arqDir = new DiretoriosDosArquivos();
			            	
			            	if(!DirArquivosSecretosUser.endsWith("/"))
			            		DirArquivosSecretosUser = DirArquivosSecretosUser + "/";
			            	
			            	if(!DirNuvem.endsWith("/"))
			            		DirNuvem = DirNuvem + "/";
			            	
			            	arqDir.DirArquivosSecretosUser = DirArquivosSecretosUser + novosArquivos.get(i).nomeArquivo + "/";
			            	//arqDir.DirArquivosNuvem = DirNuvem + novosArquivos.get(i).nomeArquivoEncrypt + "/";
			            	arqDir.DirArquivosNuvem = DirNuvem + nomeEncrypted + "/";
			            	arqDir.DirArquivoConfig = novoPathArqEstados;
			            	arqDir.DirArquivoConfigEncrypted = novoPathArqConfEncrypted;
			            	
			            	arquivosDecifrarOutrosDir.add(arqDir);				            	
			            }          						
						
						lock.lock();
		    	        
		    	        if(eventosGeradosAplicacaoPastaUser != null)
		    	        	eventosGeradosAplicacaoPastaUser.add(novosArquivos.get(i).nomeArquivo);
		    	        else
	    	        	{
		    	        	eventosGeradosAplicacaoPastaUser = new ArrayList<String>();
		    	        	eventosGeradosAplicacaoPastaUser.add(novosArquivos.get(i).nomeArquivo);
	    	        	}
		    	        
		    	        lock.unlock();						
			
			            escritorArqEstados.EscreverNovoElemento(novosArquivos.get(i), i + contId);
			        }
			        
			        escritorArqEstados.FinalizarEscritaArquivoEstados();
		        }
	        	
	        	if(arquivosDecifrarOutrosDir != null && arquivosDecifrarOutrosDir.size() > 0)
	        	{
	        		for(int i = 0; i < arquivosDecifrarOutrosDir.size(); i++)
	        		{
	        			VerificarArquivosDecifrar(arquivosDecifrarOutrosDir.get(i).DirArquivosSecretosUser, arquivosDecifrarOutrosDir.get(i).DirArquivosNuvem, arquivosDecifrarOutrosDir.get(i).DirArquivoConfig, senhaUsuario);
	        		}
	        	}				
			}
			else
				throw new Exception("NÃ£o foi possÃ­vel encontrar o diretÃ³rio dos arquivos para serem decifrados!");
		}
		catch(Exception e)
		{
			throw new Exception("Erro na verificaÃ§Ã£o de arquivos para decifrar! Erro: " + e.getMessage());
		}
	}
		
	public static String Leitura(String inputPath, boolean cifrar) throws Exception
	{
		Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        String pathDirEncrypt = "";
        
        try
        {
        	if(new File(inputPath).exists())
        		doc = builder.build(inputPath);
        	else
        		throw new Exception("Erro na leitura do arquivo de configuraÃ§Ã£o do sistema! NÃ£o foi possÃ­vel localizÃ¡-lo");
        }
        catch(Exception e)
        {
        	return "";
        }
        
        if(doc != null)
        {
	        Element arquivos = doc.getRootElement();
	        
	        List<Element> lista = arquivos.getChildren();
	        
	        arq = new Arquivos();
	        
	        pathDirEncrypt = arquivos.getAttributeValue("pathArqEncrypt");
	             
	        for(Element e:lista)
	        {
	        	//String data1 = "Thu Jul 30 14:25:42 BRT 2015";
				Date data = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US).parse(e.getChildText("ultimaModificacao"));
				
				Date dataArqEncrypt = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US).parse(e.getChildText("ultimaModificacaoArqEncrypt"));
				
				String nomeArquivo = e.getChildText("nomeArquivo");        	
	        	String pathArqConfig = e.getChild("diretorio").getAttributeValue("pathArqConfig");
	        	        	
	        	arq.AdicionarNovoRegistro(nomeArquivo, e.getChildText("path"), data, e.getChildText("diretorio").equals("true") ? true : false, pathArqConfig, dataArqEncrypt, e.getChildText("nomeArquivoEncrypt"), e.getChildText("pathArquivoEncrypt"), cifrar, pathDirEncrypt);
	        }
        }
        
        return pathDirEncrypt;
	}
	
	public static void RemoverArquivos(File arquivo) 
	{
		if(arquivo.exists())
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
	
}
