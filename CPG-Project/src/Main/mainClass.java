package Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import A2F.GoogleAuthenticator;
import ApplicationSystemTray.SystemTrayClass;
import ApplicationTelas.TelaA2FCriarSegredo;
import ApplicationTelas.TelaCadastroSenha;
import ApplicationTelas.TelaConfiguracao;
import ApplicationTelas.TelaCriarChavesCript;
import ApplicationTelas.TelaInicialLogin;
import BuscarArquivos.ManipularArquivos;
import ClassesGerais.CaminhosApp;
import ClassesGerais.DiretoriosDosArquivos;
import ClassesGerais.Eventos;
import Criptografia.CriptoAssimetrica;
import Log.LogApp;
import DetectarEventos.DetectarEventosDir;

public class mainClass 
{
	private static String DirArquivoConfigXml = "Config-files/config.xml";
	private static String ArquivoConfigXmlEncrypted = "config.xml.encrypted";
	//private static String ArquivoConfigXml = "config.xml.encrypted";
	private static LogApp log = new LogApp();
	private static String PathsConfig = "ApplicationConfig.txt";
	private static String DirArquivosConfigNuvem = "/Config";
	private static String DirKeys = "keys";
	//private static String DirArqConfig = "ArquivosConfig";
	
	private static String senha="";
	private static boolean authenticationA2F = false;
	
	public static List<Eventos> listaArqModificadosCifrar = new ArrayList<Eventos>();
	public static List<Eventos> listaArqModificadosDecifrar = new ArrayList<Eventos>();
	public static boolean gravarElemento = true;
	
	
	public static void main(String[] args) throws Exception
	{
		try 
		{	
			log.InicializarLogDaApp();
			
			log.EscreverNoLog("Iniciando verificação do sistema...");
			
			if(VerificacaoDosComponentesDoSistema())
			{
				log.EscreverNoLog("Verificação do sistema completa.");
				
				CaminhosApp caminhosApp = new CaminhosApp();
				CaminhosApp pathList = caminhosApp.RecuperarLocaisSalvos(PathsConfig);
				
				//Verifica se existe o arquivo de configuraÃ§Ãµes das pastas do sistema. 
				//Caso nÃ£o exista ou ele nÃ£o estaja preenchido, Ã© aberto a tela de configuraÃ§Ãµes para o preenchimento
				if(pathList != null && !pathList.pastaTrabalho.isEmpty() && !pathList.pastaNuvem.isEmpty())
				{
					log.EscreverNoLog("Iniciando aplicação...");
					
					log.EscreverNoLog("Pasta do usuário: " + pathList.pastaTrabalho);
					log.EscreverNoLog("Pasta da nuvem: " + pathList.pastaNuvem);
					//log.EscreverNoLog("Pasta dos arquivos de configuraÃ§Ãµes: " + pathList.pastaNuvem + ArquivoConfigXml);
					log.EscreverNoLog("Pasta dos arquivos de configurações: " + DirArquivoConfigXml);
					
					//Iniciando o SystemTray (função para montar o í­cone do lado do relógio!)
					log.EscreverNoLog("Iniciando System Tray...");	
					
					SystemTrayClass.IniciarSystemTray(log, PathsConfig);
					
					log.EscreverNoLog("System Tray iniciado com sucesso!");	
					
					//IniciarAplicacao(pathList.pastaTrabalho, pathList.pastaNuvem, pathList.pastaNuvem + ArquivoConfigXml);
					IniciarAplicacao(pathList.pastaTrabalho, pathList.pastaNuvem, DirArquivoConfigXml, pathList.pastaNuvem + ArquivoConfigXmlEncrypted);
				}
				else
				{
					JOptionPane.showConfirmDialog(null, "Não foi possivél iniciar a apicação! Não foi possí­vel obter as informações do arquivo de configurações das pastas.", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
					
					log.EscreverNoLog("Erro na inicialização da aplicação: não foi possí­vel obter as informações do arquivos de configurações das pastas.");
				}	
			}
		} 
		catch (Exception e) 
		{
			log.EscreverNoLog("Erro na aplicação: " + e.getMessage());
			log.EncerrarLogApp();
			
			e.printStackTrace();
		}		
	}
	
	public static boolean VerificacaoDosComponentesDoSistema() throws Exception
	{		
		boolean ret=false;
		
		try 
		{
			//VerificaÃ§Ã£o do arquivo de configuraÃ§Ã£o das pastas da aplicaÃ§Ã£o
			
			log.EscreverNoLog("Verificação do arquivo de configuração das pastas da aplicação");
			
			CaminhosApp caminhosApp = new CaminhosApp();
			CaminhosApp pathList = caminhosApp.RecuperarLocaisSalvos(PathsConfig);
			
			//Verifica se existe o arquivo de configuraÃ§Ãµes das pastas do sistema. 
			//Caso nÃ£o exista ou ele nÃ£o estaja preenchido, Ã© aberto a tela de configuraÃ§Ãµes para o preenchimento
			if(pathList != null && !pathList.pastaTrabalho.isEmpty() && !pathList.pastaNuvem.isEmpty())
			{
				ret = true;
				log.EscreverNoLog("Arquivo de configuração das pastas da aplicação encontrado com sucesso!");
			}
			else
			{
				log.EscreverNoLog("Arquivo de configuração das pastas não encontrado!");
				
				log.EscreverNoLog("Iniciando tela para configuração do mesmo...");
				
				TelaConfiguracao conf = new TelaConfiguracao();
				conf.Configuracao(PathsConfig);
				
				log.EscreverNoLog("Verificação do arquivo de configuração das pastas da aplicação...");
				
				pathList = caminhosApp.RecuperarLocaisSalvos(PathsConfig);
				
				if(pathList != null && !pathList.pastaTrabalho.isEmpty() && !pathList.pastaNuvem.isEmpty())
				{
					VerificarExistenciaDeArquivosConfi(pathList.pastaNuvem);
					
					ret = true;
					
					log.EscreverNoLog("Arquivo de configuração das pastas da aplicação encontrado com sucesso!");
				}
				else
				{
					while(ret)
					{
						int reply = JOptionPane.showConfirmDialog(null, "Para iniciar a aplicação é necessário a configuração de algumas pastas de seu uso. Deseja continuar e proceder com o processo de configuração?", "Aviso", JOptionPane.YES_NO_OPTION);
	        		
						if(reply == JOptionPane.NO_OPTION)
						{
							log.EscreverNoLog("Cliente escolhe encerrar a aplicação");
							log.EncerrarLogApp();
							System.exit(0);
						}
						
						log.EscreverNoLog("Arquivo de configuração das pastas não encontrado!");
						
						log.EscreverNoLog("Iniciando tela para configuração do mesmo...");
						
						conf = new TelaConfiguracao();
						conf.Configuracao(PathsConfig);
						
						log.EscreverNoLog("Verificação do arquivo de configuração das pastas da aplicação...");
						
						pathList = caminhosApp.RecuperarLocaisSalvos(PathsConfig);
						
						if(pathList != null && !pathList.pastaTrabalho.isEmpty() && !pathList.pastaNuvem.isEmpty())
						{
							VerificarExistenciaDeArquivosConfi(pathList.pastaNuvem);
							
							ret = true;
							log.EscreverNoLog("Arquivo de configuração das pastas da aplicação encontrado com sucesso!");
						}
					}
				}
			}
			
			//VerificaÃ§Ã£o da existÃªncia do par de chaves do usuÃ¡rio e autenticaÃ§Ã£o
			
			log.EscreverNoLog("Verificação do par de chaves...");
			
			CriptoAssimetrica x = new CriptoAssimetrica();
			
			if(!x.ChecarExistenciaDasChaves())
			{
				//Verifica se existem arquivos de config na nuvem (Par de chaves e arquivos config.xml)
				if(!VerificarExistenciaDeArquivosConfi(pathList.pastaNuvem))
				{
					log.EscreverNoLog("Par de chaves não encontrado!");
					
					log.EscreverNoLog("Cadastro da senha do usuário...");
					
					//CriaÃ§Ã£o do segredo do usuÃ¡rio
					TelaCadastroSenha cadSenha = new TelaCadastroSenha();
					senha = cadSenha.CadastrarSenha();
					
					//Verificar se serÃ¡ utilizado a autenticaÃ§Ã£o de 2 fatores.
					TelaA2FCriarSegredo a2f = new TelaA2FCriarSegredo();
					authenticationA2F = a2f.AtivarA2F();
					
					if(authenticationA2F)
					{
						senha = a2f.CriarSegredo(senha) + senha;
					}
					
					//PASSAR A SENHA COMO PARÃ‚METRO PARA CIFRAR A CHAVE...
					
					log.EscreverNoLog("Criação do par de chaves do usuário...");
					
					if(senha != null && !senha.isEmpty())
					{
						//CriaÃ§Ã£o do par de chaves criptogrÃ¡fica do usuÃ¡rio
						TelaCriarChavesCript criacaoChaves = new TelaCriarChavesCript();
						criacaoChaves.CriarParDeChavesCriptografica(senha);
						
						log.EscreverNoLog("Verificação do par de chaves...");
						
						if(!x.ChecarExistenciaDasChaves())
						{
							log.EscreverNoLog("Par de chaves não encontrado!");
							
							int reply = JOptionPane.showConfirmDialog(null, "Para iniciar a aplicação é necessário a criação de um par de chaves criptográficas. Deseja continuar e proceder com o processo de criação das chaves?", "Aviso", JOptionPane.YES_NO_OPTION);
			        		
			        		if(reply == JOptionPane.NO_OPTION)
		        			{
			        			log.EscreverNoLog("Cliente escolhe encerrar aplicação");
			        			log.EncerrarLogApp();
			        			
			        			System.exit(0);	  
		        			}
			        		else
			        			VerificacaoDosComponentesDoSistema();
						}
					}
					else
					{
						log.EscreverNoLog("Senha não criada!");
						
						int reply = JOptionPane.showConfirmDialog(null, "Não foi possí­vel realizar a criação da senha! Deseja tentar novamente?", "Aviso", JOptionPane.YES_NO_OPTION);
		        		
		        		if(reply == JOptionPane.NO_OPTION)
	        			{
		        			log.EscreverNoLog("Cliente escolhe encerrar aplicação");
		        			log.EncerrarLogApp();
		        			
		        			System.exit(0);	  
	        			}
		        		else
		        			VerificacaoDosComponentesDoSistema();
					}
				}
				else
				{
					log.EscreverNoLog("Arquivos da aplicação encontrados na pasta da nuvem! Verificando se existe um par de chaves...");
					
					if(!x.ChecarExistenciaDasChaves())
					{
						log.EscreverNoLog("Par de chaves não encontrado!");
						
						int reply = JOptionPane.showConfirmDialog(null, "Para iniciar a aplicação é necessário a criação de um par de chaves criptográficas. Deseja continuar e proceder com o processo de criação das chaves?", "Aviso", JOptionPane.YES_NO_OPTION);
		        		
		        		if(reply == JOptionPane.NO_OPTION)
	        			{
		        			log.EscreverNoLog("Cliente escolhe encerrar aplicação");
		        			log.EncerrarLogApp();
		        			
		        			System.exit(0);	  
	        			}
		        		else
		        			VerificacaoDosComponentesDoSistema();
					}
				}
			}
			else
			{
				log.EscreverNoLog("Par de chaves localizado com sucesso!");
				
				if(GoogleAuthenticator.A2FUseCheck())
					authenticationA2F = true;
				
				//AutenticaÃ§Ã£o
				
				log.EscreverNoLog("Iniciando autenticação...");
				
				if(authenticationA2F)
				{
					log.EscreverNoLog("Autenticação de dois fatores habilitada...");
					
					TelaInicialLogin login = new TelaInicialLogin();
					senha = login.RealizarLogin(authenticationA2F);
					
					if(senha == null || senha.isEmpty())
					{
						log.EscreverNoLog("Falha na autenticação!");
						
						System.exit(0);
					}
				}
				else
				{
					log.EscreverNoLog("autenticação de dois fatores desabilitada...");
					
					TelaInicialLogin login = new TelaInicialLogin();
					senha = login.RealizarLogin(false);
					
					if(senha == null || senha.isEmpty())
					{
						log.EscreverNoLog("Falha na autenticação!");
						
						System.exit(0);
					}
				}
				
				log.EscreverNoLog("Autenticação realizada com sucesso!");
			}
						
			return ret;
		} 
		catch (Exception e) 
		{
			throw new Exception("Erro na verificação da existência do par de chaves criptográficas do usuário! Erro: " + e.getMessage());
		}
	}
	
	public static void ImprimirLista(DiretoriosDosArquivos dir, int cont)
	{
		System.out.println("Árvore de diretórios... ");
		
		System.out.println(cont + ". Dir arquivos secretos: " + dir.DirArquivosSecretosUser);
		System.out.println(cont + ". Dir arquivos nuvem: " + dir.DirArquivosNuvem);
		System.out.println(cont + ". Dir arquivos de estados: " + dir.DirArquivoConfig);
		
		if(dir.ListSubDiretorios != null && dir.ListSubDiretorios.size() > 0)
		{
			cont++;
			
			for(int i=0; i< dir.ListSubDiretorios.size(); i++)
			{
				ImprimirLista(dir.ListSubDiretorios.get(i), cont + i);
			}
		}
	}
		
	public static void IniciarAplicacao(String pastaUsuario, String pastaNuvem, String pastaArquivoEstadosXml, String arquivoConfigXmlEncrypted) throws Exception
	{
		try
		{
			DiretoriosDosArquivos diretorioArquivosList = new DiretoriosDosArquivos();
			
			DetectarEventosDir detect = new DetectarEventosDir(pastaUsuario, true);
			DetectarEventosDir detectNuvem = new DetectarEventosDir(pastaNuvem, false);
						
			Thread threadBuscarArquivosAplicacao = new Thread(detect);
			Thread threadBuscarArquivosNuvem = new Thread(detectNuvem);
			
			while(true)
			{
				log.AcordarLogApp();
								
				//REALIZANDO A AUTENTICAÃ‡ÃƒO
				//############################################################################################		
				
				if(senha == null || senha.isEmpty())
				{
					//AutenticaÃ§Ã£o
					log.EscreverNoLog("Iniciando autenticação...");
					
					TelaInicialLogin login = new TelaInicialLogin();
					senha = login.RealizarLogin(authenticationA2F);
					
					if(senha == null || senha.isEmpty())
					{
						System.exit(0);
						log.EscreverNoLog("Falha na autenticação!");
					}
					
					log.EscreverNoLog("Autenticação realizada com sucesso!");
				}
				
				//############################################################################################		
										
				//CARREGANDO LISTA DOS DIRETÃ“RIOS NA MEMÃ“RIA - PERMITE QUE A PARTIR DO DIRETÃ“RIO DO ARQUIVO 
				//(NA NUVEM OU LOCAL) SEJA POSSÃ�VEL LOCALIZAR SEU ARQUIVO DE ESTADOS CORRESPONDENTE
				//############################################################################################
				
				if(diretorioArquivosList == null || diretorioArquivosList.DirArquivosSecretosUser == null || diretorioArquivosList.DirArquivosSecretosUser.isEmpty())
					diretorioArquivosList = ManipularArquivos.CriarArvoreDiretorios(pastaArquivoEstadosXml, pastaUsuario, pastaNuvem, senha);
				
				//--O CORRETO Ã‰ INICIAR AS THREADS ANTES DA VERIFICAÃ‡ÃƒO COMPLETA, ASSIM SE UM ARQUIVO FOR DEPOSITADO DURANTE A VERIFICAÃ‡ÃƒO, ELE SERÃ� CIFRADO/DECIFRADO
				
				//############################################################################################
								
				//INICIANDO THREADS VERIFICAÃ‡ÃƒO PARCIAL
				//############################################################################################
				
				//Iniciando threads para a verificaÃ§Ã£o parcial... -- somente quando ocorrem eventos de criaÃ§Ã£o, alteraÃ§Ã£o e deleÃ§Ã£o de arquivos
				log.EscreverNoLog("Iniciando threads para verificação parcial da aplicação...");
				
				//AtivarThreadsVerificacaoParcial(pastaUsuario, pastaNuvem);
				
				System.out.println("Thread arquivos aplicação status: " + threadBuscarArquivosAplicacao.getState().toString());
				
				if(threadBuscarArquivosAplicacao.getState() == Thread.State.NEW)
				{
					threadBuscarArquivosAplicacao.start();
					
					System.out.println("Thread arquivos aplicação iniciada...");
				}
				else if(threadBuscarArquivosAplicacao.isInterrupted() || threadBuscarArquivosAplicacao.getState() == Thread.State.TERMINATED)
				{
					threadBuscarArquivosAplicacao = new Thread(detect);
					threadBuscarArquivosAplicacao.start();
					
					System.out.println("Thread arquivos aplicação criada e iniciada...");
				}
				
				System.out.println("Thread arquivos nuvem status: " + threadBuscarArquivosNuvem.getState().toString());
				
				if(threadBuscarArquivosNuvem.getState() == Thread.State.NEW)
				{
					threadBuscarArquivosNuvem.start();
					System.out.println("Thread arquivos nuvem iniciada...");
				}
				else if(threadBuscarArquivosNuvem.isInterrupted() || threadBuscarArquivosNuvem.getState() == Thread.State.TERMINATED)
				{
					threadBuscarArquivosNuvem = new Thread(detect);
					threadBuscarArquivosNuvem.start();
					
					System.out.println("Thread arquivos nuvem criada e iniciada...");
				}
								
				//VERIFICAÃ‡ÃƒO COMPLETA ... 
				//############################################################################################
				
				//Realizando a verificaÃ§Ã£o completa
				log.EscreverNoLog("Realizando a verificaÃ§Ã£o completa...");
				
				log.EscreverNoLog("Verificando novos arquivos para cifrar...");	
				//ManipularArquivos.VerificarNovosArquivos(pastaUsuario, pastaNuvem, pastaArquivoConfigXml, arquivoConfigXmlEncrypted, senha);
				ManipularArquivos.VerificarArquivosCifrar(pastaUsuario, pastaNuvem, pastaArquivoEstadosXml, senha);
											
				log.EscreverNoLog("Verificando arquivos para decifrar...");
				
				//ManipularArquivos.VerificarArquivosParaDecifrar(pastaUsuario, pastaNuvem, pastaArquivoConfigXml, arquivoConfigXmlEncrypted, senha);
				ManipularArquivos.VerificarArquivosDecifrar(pastaUsuario, pastaNuvem, pastaArquivoEstadosXml, senha);
				
				log.EscreverNoLog("Verificação completa realizada com sucesso.");
				
				//############################################################################################
									
				boolean realizarVerificacaoCompleta = false;
				
				long tempoParaVeriCompleta = 0;
				
				while(!realizarVerificacaoCompleta)
				{
					//TRATANDO EVENTOS DA VERIFICAÃ‡ÃƒO PARCIAL
					//############################################################################################
					
					log.AcordarLogApp();
					
					log.EscreverNoLog("Realizando a verificação parcial...");
					
					List<Eventos> listaArqModificadosTempCifrar= new ArrayList<Eventos>();
					List<Eventos> listaArqModificadosTempDecifrar= new ArrayList<Eventos>();
					
					//Verificando o estado das threads...
					//Caso elas sejam finalizadas, sÃ£o iniciadas novamente...					
					if(threadBuscarArquivosAplicacao.isInterrupted() || threadBuscarArquivosAplicacao.getState() == Thread.State.TERMINATED)
					{
						threadBuscarArquivosAplicacao = new Thread(detect);
						threadBuscarArquivosAplicacao.start();
					}
					
					if(threadBuscarArquivosNuvem.isInterrupted() || threadBuscarArquivosNuvem.getState() == Thread.State.TERMINATED)
					{
						threadBuscarArquivosNuvem = new Thread(detect);
						threadBuscarArquivosNuvem.start();
					}
										
					//Verificando a existÃªncia de eventos na pasta da aplicaÃ§Ã£o - Cifrar arquivos
					
					lock.lock();
					
					if(listaArqModificadosCifrar != null && listaArqModificadosCifrar.size() > 0)
					{
						//gravarElemento = false;
						//lock.lock();
						listaArqModificadosTempCifrar.addAll(listaArqModificadosCifrar);
						listaArqModificadosCifrar = new ArrayList<Eventos>();	
						//gravarElemento = true;
						//lock.unlock();
						
						//Tratando eventos...
						ManipularArquivos.TratarEventos(listaArqModificadosTempCifrar, true, pastaArquivoEstadosXml, pastaUsuario, pastaNuvem, senha, diretorioArquivosList);
						
						if(diretorioArquivosList != null)
						{
							ImprimirLista(diretorioArquivosList, 0);
						}					
					}
					
					lock.unlock();
					
					//Verificando a existÃªncia de eventos na pasta da nuvem - Decifrar arquivos
					
					lock.lock();
					
					if(listaArqModificadosDecifrar != null && listaArqModificadosDecifrar.size() > 0)
					{
						//gravarElemento = false;
						//lock.lock();
						listaArqModificadosTempDecifrar.addAll(listaArqModificadosDecifrar);
						listaArqModificadosDecifrar = new ArrayList<Eventos>();	
						//gravarElemento = true;
						//lock.unlock();
						
						//Tratando eventos...
						ManipularArquivos.TratarEventosDecifrar(listaArqModificadosTempDecifrar, false, pastaArquivoEstadosXml, pastaUsuario, pastaNuvem, senha, diretorioArquivosList);
						
						if(diretorioArquivosList != null)
						{
							ImprimirLista(diretorioArquivosList, 0);
						}	
					}	
					
					lock.unlock();			
					log.DormirLogApp();
					
					//10 segundos...
					Thread.sleep(10000);
					
					tempoParaVeriCompleta = tempoParaVeriCompleta + 10000;
					
					//2 min - 120 segundos
					if(tempoParaVeriCompleta > 120000)
						realizarVerificacaoCompleta = true;					
				}
				
				//PARANDO THREADS
				//############################################################################################		
				
				
				
				//############################################################################################		
												
				log.DormirLogApp();				
			}
		}
		catch(Exception e)
		{
			
			//Mostrar mensagem ao usuÃ¡rio
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Aviso", JOptionPane.ERROR_MESSAGE);
			
			IniciarAplicacao(pastaUsuario, pastaNuvem, pastaArquivoEstadosXml, arquivoConfigXmlEncrypted);
						
			//throw new Exception("Erro: " + e.getMessage());
			
			//System.exit(0);
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
	
	public static Runnable TratarEventos(List<Eventos> list, boolean cifrar) throws InterruptedException
	{
		lock.lock();
		
		if(list == null || list.size() == 0)
			return null;
		
		if(cifrar)
		{
    		if(listaArqModificadosCifrar == null)
    			listaArqModificadosCifrar = new ArrayList<Eventos>();
    		
    		if(listaArqModificadosCifrar.size() > 0)
            {
            	for(int i=0; i < list.size(); i++)
            	{
            		boolean add = true;
            		
            		for(int j=0; j < listaArqModificadosCifrar.size(); j++)
            		{
            			if(list.get(i).PathEvent.equals(listaArqModificadosCifrar.get(j).PathEvent))
            			{
            				if(list.get(i).KindEvent.equals("ENTRY_CREATE") && (listaArqModificadosCifrar.get(j).KindEvent.equals("ENTRY_MODIFY") || listaArqModificadosCifrar.get(j).KindEvent.equals("ENTRY_CREATE")))
            				{
            					add = false;
                				break;
            				}
            				
            				if(list.get(i).KindEvent.equals("ENTRY_MODIFY") && (listaArqModificadosCifrar.get(j).KindEvent.equals("ENTRY_CREATE") || listaArqModificadosCifrar.get(j).KindEvent.equals("ENTRY_MODIFY")))
            				{
            					add = false;
                				break;
            				}
            				
            				if(list.get(i).KindEvent.equals("ENTRY_DELETE") && listaArqModificadosCifrar.get(j).KindEvent.equals("ENTRY_DELETE"))
            				{
            					add = false;
                				break;
            				}
            			}
            		}
            		
            		if(add)
            			listaArqModificadosCifrar.add(list.get(i));
            	}
            }
            else
            	listaArqModificadosCifrar.addAll(list);				
		}
    	else
		{
    		if(listaArqModificadosDecifrar == null)
    			listaArqModificadosDecifrar = new ArrayList<Eventos>();
    		
    		if(listaArqModificadosDecifrar.size() > 0)
            {
            	for(int i=0; i < list.size(); i++)
            	{
            		boolean add = true;
            		
            		for(int j=0; j < listaArqModificadosDecifrar.size(); j++)
            		{
            			if(list.get(i).PathEvent.equals(listaArqModificadosDecifrar.get(j).PathEvent))
            			{
            				if(list.get(i).KindEvent.equals("ENTRY_CREATE") && (listaArqModificadosDecifrar.get(j).KindEvent.equals("ENTRY_MODIFY") || listaArqModificadosDecifrar.get(j).KindEvent.equals("ENTRY_CREATE")))
            				{
            					add = false;
                				break;
            				}
            				
            				if(list.get(i).KindEvent.equals("ENTRY_MODIFY") && (listaArqModificadosDecifrar.get(j).KindEvent.equals("ENTRY_CREATE") || listaArqModificadosDecifrar.get(j).KindEvent.equals("ENTRY_MODIFY")))
            				{
            					add = false;
                				break;
            				}
            				
            				if(list.get(i).KindEvent.equals("ENTRY_DELETE") && listaArqModificadosDecifrar.get(j).KindEvent.equals("ENTRY_DELETE"))
            				{
            					add = false;
                				break;
            				}
            			}
            		}
            		
            		if(add)
            			listaArqModificadosDecifrar.add(list.get(i));
            	}
            }
            else
            	listaArqModificadosDecifrar.addAll(list);
		}
		
		/*
		if(cifrar)
		{
			for(int i=0;i<listaArqModificadosCifrar.size();i++)
				System.out.println("Main class Tratar Arquivos Cifrar --> " + listaArqModificadosCifrar.get(i).KindEvent + " == " + listaArqModificadosCifrar.get(i).PathEvent);
			
			System.out.println("Lista completa de eventos cifrar...");	    	
		}
		else
		{
			for(int i=0;i<listaArqModificadosDecifrar.size();i++)
				System.out.println("Main class Tratar Arquivos Decifrar --> " + listaArqModificadosDecifrar.get(i).KindEvent + " == " + listaArqModificadosDecifrar.get(i).PathEvent);
	    	
			System.out.println("Lista completa de eventos decifrar...");
		}
    	 */
		
    	lock.unlock();
		return null;
	}
	
	public static boolean VerificarExistenciaDeArquivosConfi(String dirNuvem) throws Exception
	{
		File dirConfigNuvem = new File(dirNuvem + DirArquivosConfigNuvem);
		
		boolean ret = false;
		authenticationA2F = false;
    	
		try
		{
			if(dirConfigNuvem.exists())
	    	{
	    		//Checar par de chaves...
				File dirKeys = new File(dirNuvem + DirArquivosConfigNuvem + DirKeys);
	    		
	    		CriptoAssimetrica x = new CriptoAssimetrica();
    			
	    		x.PRIVATE_KEY_FILE = dirKeys + "/private.key";
				x.PUBLIC_KEY_FILE = dirKeys + "/public.key";
    			
    			if(x.ChecarExistenciaDasChaves())
    			{
    				GoogleAuthenticator.A2F_KEY_FILE = dirKeys + "/a2f.key";
    				
    				if(GoogleAuthenticator.A2FUseCheck())
    					authenticationA2F = true;
    				
    				TelaInicialLogin login = new TelaInicialLogin();
    				senha = login.RealizarLogin(authenticationA2F);
    				
    				if(senha != null && !senha.isEmpty())
    				{
    					//Se tudo estiver OK, copiar o par par a pasta do sistema	                					
    					File chavePrivada = new File(dirKeys + "/private.key");
            			File chavePublica = new File(dirKeys + "/public.key");
            			File chaveA2F = new File(dirKeys + "/a2f.key");
            			
            			FileChannel sourceChannel = null;  
        			    FileChannel destinationChannel = null; 
        			    
        			    File keyFolder = new File(DirKeys);
		            	
		            	if(!keyFolder.exists())
		            		keyFolder.mkdir();
        			    
        			    sourceChannel = new FileInputStream(chavePrivada).getChannel();  
        			    String novoInputFilePri = keyFolder + "/private.key";
        		        destinationChannel = new FileOutputStream(novoInputFilePri).getChannel();  
        		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        		        
        		        if (sourceChannel != null && sourceChannel.isOpen())
        		        	sourceChannel.close();  
        		        if (destinationChannel != null && destinationChannel.isOpen())
        		        	destinationChannel.close(); 
        		        
        		        sourceChannel = null;  
        			    destinationChannel = null; 
    					
        		        sourceChannel = new FileInputStream(chavePublica).getChannel();  
        			    String novoInputFilePub = keyFolder + "/public.key";
        		        destinationChannel = new FileOutputStream(novoInputFilePub).getChannel();  
        		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        		        
        		        if (sourceChannel != null && sourceChannel.isOpen())
        		        	sourceChannel.close();  
        		        if (destinationChannel != null && destinationChannel.isOpen())
        		        	destinationChannel.close();
        		        
        		        sourceChannel = new FileInputStream(chaveA2F).getChannel();  
        			    String novoInputFileA2F = keyFolder + "/a2f.key";
        		        destinationChannel = new FileOutputStream(novoInputFileA2F).getChannel();  
        		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        		        
        		        if (sourceChannel != null && sourceChannel.isOpen())
        		        	sourceChannel.close();  
        		        if (destinationChannel != null && destinationChannel.isOpen())
        		        	destinationChannel.close(); 
        		        
        		        sourceChannel = null;  
        			    destinationChannel = null; 
        		        	                    		      
        		    	x.PRIVATE_KEY_FILE = "keys/private.key";
        		    	x.PUBLIC_KEY_FILE = "keys/public.key";
        		    	GoogleAuthenticator.A2F_KEY_FILE = "keys/a2f.key";
        		        
    					JOptionPane.showConfirmDialog(null, "Par de chaves adicionado com sucesso!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
                		
    					ret = true;
    				}
    			}
	    	}
		}
		catch(Exception ex)
		{
			throw new Exception("Erro na verificaÃ§Ã£o da existÃªncia de arquivos de configuraÃ§Ã£o na nuvem. Erro: " + ex.getMessage());
		}
    	
    	return ret;
	}

}

