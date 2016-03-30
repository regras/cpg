package ApplicationTelas;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import A2F.GoogleAuthenticator;
import Criptografia.CriptoAssimetrica;

public class TelaCriarChavesCript extends JFrame
{
	private String criptoKeyFolder = "keys";
	
	public void CriarParDeChavesCriptografica(final String password)
	{
		String nomeLogonSO = System.getProperty("user.name");
		
		final JDialog janela = new JDialog();
		janela.setTitle("Criar par de chaves criptográficas");
		janela.setSize(460,210);
		janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		janela.setResizable(false);	//Desabilita opção de expansão
		janela.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
		
		//final JFrame frame = new JFrame("Login");
		//frame.setSize(450, 220);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setResizable(false);;
		//frame.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		//frame.add(panel);
		janela.add(panel);
		panel.setLayout(null);
		
		Font font = new Font("Arial", Font.BOLD,18);
		
		JLabel userLabel0 = new JLabel("Olá ");
		userLabel0.setBounds(10, 10, 550, 30);
		panel.add(userLabel0);
		
		JLabel userLabel = new JLabel(nomeLogonSO + ",");
		userLabel.setFont(font);
		userLabel.setBounds(42, 10, 550, 30);
		panel.add(userLabel);
		
		JLabel userLabel2 = new JLabel("Um par de chaves criptográficas é necessário para a realização das ativida-");		
		userLabel2.setBounds(10, 10, 550, 90);
		panel.add(userLabel2);		

		JLabel userLabel3 = new JLabel("des da aplicação. Para este fim, serão criados uma chave pública e uma");		
		userLabel3.setBounds(10, 10, 550, 130);
		panel.add(userLabel3);
		
		JLabel userLabel4 = new JLabel("privada para você. Clique em 'Sim' para prosseguir com este progresso ou");		
		userLabel4.setBounds(10, 10, 550, 170);
		panel.add(userLabel4);
		
		JLabel userLabel5 = new JLabel("caso já possua um par de chaves, clique em 'Não' e a localize.");		
		userLabel5.setBounds(10, 10, 550, 210);
		panel.add(userLabel5);
						
		JButton BtnSim = new JButton("Sim");
		BtnSim.setBounds(230, 150, 100, 25);
		panel.add(BtnSim);
		
		JButton BtnNao = new JButton("Não");
		BtnNao.setBounds(340, 150, 100, 25);
		panel.add(BtnNao);
		
		BtnSim.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		try
        		{
        			CriptoAssimetrica x = new CriptoAssimetrica();
            		
            		if(x.GerarChave(password))
            		{
            			JOptionPane.showConfirmDialog(null, "Par de chaves criado com sucesso!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
                		janela.dispose();
            		}
            		else
            		{
            			JOptionPane.showConfirmDialog(null, "Erro ao criar par de chaves!!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
                		janela.dispose();
            		}            		
        		}
        		catch(Exception ex)
        		{
        			JOptionPane.showConfirmDialog(null, "Erro ao criar par de chaves!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
        		}        		        			
			}
        });
		
		BtnNao.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		//JOptionPane.showConfirmDialog(null, "Por favor, localize suas chaves. ", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
        		        		
        		final JDialog janelaLocalizacao = new JDialog();
        		janelaLocalizacao.setTitle("Criar par de chaves criptográficas");
        		janelaLocalizacao.setSize(445,150);
        		janelaLocalizacao.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        		//janelaLocalizacao.setResizable(false);	//Desabilita opção de expansão
        		janelaLocalizacao.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
        	        		
        		JPanel panelLocalizacao = new JPanel();
        		//frame.add(panel);
        		janelaLocalizacao.add(panelLocalizacao);
        		panelLocalizacao.setLayout(null);
        		
        		JLabel userLabelLoc = new JLabel("Selecione o local onde está o par de chaves criptográficas");		
        		userLabelLoc.setBounds(10, 10, 440, 20);
        		panelLocalizacao.add(userLabelLoc);	
        		
        		final JTextField campoLoc = new JTextField(40);
        		campoLoc.setBounds(10, 35, 420, 25);
        		//campoLoc.enable(false);
        		panelLocalizacao.add(campoLoc);
        		
        		JButton BtnLoc = new JButton("Localizar");
        		BtnLoc.setBounds(230, 80, 100, 25);
        		panelLocalizacao.add(BtnLoc);
        		
        		JButton BtnCancel = new JButton("Cancelar");
        		BtnCancel.setBounds(340, 80, 100, 25);
        		panelLocalizacao.add(BtnCancel);
        		
        		BtnLoc.addActionListener(new ActionListener()
                {
                	@Override
        			public void actionPerformed(ActionEvent e) 
                	{
                		try
                		{
	                		JFileChooser chooser = new JFileChooser();  
	                		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	                		
	                		int i= chooser.showSaveDialog(null); 
	                		if (i==1)
	                		{ 
	                			JOptionPane.showConfirmDialog(null, "Erro ao selecionar chaves!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
	                		} 
	                		else 
	                		{ 
	                			String selectedPath = chooser.getSelectedFile().toString(); 
	                			campoLoc.setText(selectedPath);
	                			
	                			//Verificar se a pasta seleciona contem as chaves pública e privada
	                			
	                			CriptoAssimetrica x = new CriptoAssimetrica();
	                			
	                			if(selectedPath.indexOf(selectedPath.length()) != '/')
	                			{
	                				x.PRIVATE_KEY_FILE = selectedPath + "/private.key";
	                				x.PUBLIC_KEY_FILE = selectedPath + "/public.key";
	                				GoogleAuthenticator.A2F_KEY_FILE = selectedPath + "/a2f.key";
	                			}
	                			else
	                			{
	                				x.PRIVATE_KEY_FILE = selectedPath + "private.key";
	                				x.PUBLIC_KEY_FILE = selectedPath + "public.key";
	                				GoogleAuthenticator.A2F_KEY_FILE = selectedPath + "a2f.key";
	                			}
	                			
	                			 
	                			
	                			if(x.ChecarExistenciaDasChaves())
	                			{
	                				boolean a2FAtivado = false;
	                				
	                				if(GoogleAuthenticator.A2FUseCheck())
	                					a2FAtivado = true;
	                				
	                				//Testar se o par selecionado, é realmente um par de chaves, pedindo a senha do usuário através do processo de autenticação
	                				
	                				TelaInicialLogin login = new TelaInicialLogin();
	                				String senha = login.RealizarLogin(a2FAtivado);
	                				
	                				if(senha != null && !senha.isEmpty())
	                				{
	                					//Se tudo estiver OK, copiar o par par a pasta do sistema	                					
	                					File chavePrivada = new File(selectedPath + "/private.key");
	                        			File chavePublica = new File(selectedPath + "/public.key");
	                        				                        			
	                        			FileChannel sourceChannel = null;  
	                    			    FileChannel destinationChannel = null; 
	                    			    
	                    			    File keyFolder = new File(criptoKeyFolder);
	            		            	
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
	                    		        	                    		      
	                    		    	x.PRIVATE_KEY_FILE = "keys/private.key";
	                    		    	x.PUBLIC_KEY_FILE = "keys/public.key";
	                    		    	
	                    		    	if(a2FAtivado)
	                    		    	{
	                    		    		File chaveA2F = new File(selectedPath + "/a2f.key");
	                    		    		
	                    		    		sourceChannel = null;  
		                    			    destinationChannel = null; 
		                					
		                    		        sourceChannel = new FileInputStream(chaveA2F).getChannel();  
		                    			    String novoInputFileA2F = keyFolder + "/a2f.key";
		                    		        destinationChannel = new FileOutputStream(novoInputFileA2F).getChannel();  
		                    		        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
		                    		        
		                    		        if (sourceChannel != null && sourceChannel.isOpen())
		                    		        	sourceChannel.close();  
		                    		        if (destinationChannel != null && destinationChannel.isOpen())
		                    		        	destinationChannel.close();
		                    		        	                    		      
		                    		    	GoogleAuthenticator.A2F_KEY_FILE = "keys/a2f.key";
	                    		    	}
	                    		        
	                					JOptionPane.showConfirmDialog(null, "Par de chaves adicionado com sucesso!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
	                            		janelaLocalizacao.dispose();
	                					
	                				}
	                			}
	                			else
	                			{
	                				JOptionPane.showConfirmDialog(null, "Não foi possível localizar o par de chaves na pasta selecionada!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
	                			}                			
	            			}
                		}
                		catch(Exception ex)
                		{
                			JOptionPane.showConfirmDialog(null, "Erro ao adicionar par de chaves! Erro: " + ex.getMessage(), "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
                		}
                		
                		//chooser.setFileFilter(new ExtensionFileFilter("Arquivos de texto", "txt", "log", "html", "htm", "css"));  
                		//if (chooser.showOpenDialog() != JFileChooser.APPROVE_OPTION)   
                		   //return;  
                		  
                		//System.out.println("Arquivo selecionado: " + chooser.getSelectedFile().toString()); 
                		
                		      			
        			}
                });
        		
        		BtnCancel.addActionListener(new ActionListener()
                {
                	@Override
        			public void actionPerformed(ActionEvent e) 
                	{
                		janelaLocalizacao.dispose();        			
        			}
                });
        		
        		janelaLocalizacao.setModal(true); //Bloqueia a tela e espera o fechamento da janela para continuar a execução
        		janelaLocalizacao.setVisible(true); //Mostra a janela
        		
        		janela.dispose();
			}
        });
		

		janela.setModal(true); //Bloqueia a tela e espera o fechamento da janela para continuar a execução
		janela.setVisible(true); //Mostra a janela	
	}
}
