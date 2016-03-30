package ApplicationTelas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import A2F.GoogleAuthenticator;
import QRCodeGenerator.QRCode;

public class TelaA2FCriarSegredo 
{
	String UserPassphrase = "";
	String A2FSecret = "";
	boolean ret = false;
	
	public String CriarSegredo(String userPassphraseParam) throws Exception
	{
		UserPassphrase = userPassphraseParam;
		
		try
		{
			A2FSecret = GoogleAuthenticator.CreatePasspgrase(UserPassphrase);
			//A2FSecret = "V2AICX2SPWXOHTRM";
			
			if(A2FSecret != null && !A2FSecret.isEmpty())
			{
				JOptionPane.showConfirmDialog(null, "Segredo criado com sucesso!", "A2F - Criação do segredo", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
				
				final JDialog janela = new JDialog();
				janela.setTitle("A2F - Adicionando segredo ao App Google Authenticator");
				janela.setSize(660,200);
				janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				janela.setResizable(false);	//Desabilita opção de expansão
				janela.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
									
				final JPanel panel = new JPanel();
				janela.add(panel);
				panel.setLayout(null);
				
				Font font = new Font("Arial", Font.BOLD,18);
				
				JLabel userLabel0 = new JLabel("É necessário incluir o segredo ao App no Google Authenticator de seu dispositivo móvel.");
				userLabel0.setBounds(10, 10, 650, 30);
				panel.add(userLabel0);
				
				JLabel userLabel1 = new JLabel("Há duas formas para realizar esta tarefa: manualmente ou por QR Code. Selecione a");
				userLabel1.setBounds(10, 30, 650, 30);
				panel.add(userLabel1);
				
				JLabel userLabel2 = new JLabel("maneira desejada para continuar com a configuração.");
				userLabel2.setBounds(10, 50, 550, 30);
				panel.add(userLabel2);
				
				JButton BtnManual = new JButton("Manualemente");
				BtnManual.setBounds(210, 100, 250, 25);
				panel.add(BtnManual);
				
				JButton BtnQRCode = new JButton("QR Code (Necessário leitor)");
				BtnQRCode.setBounds(210, 130, 250, 25);
				panel.add(BtnQRCode);
				
				
				
				BtnManual.addActionListener(new ActionListener()
		        {
		        	@Override
					public void actionPerformed(ActionEvent e) 
		        	{
		        		panel.hide();
		        		
		        		final JPanel panelManual = new JPanel();
	    				janela.add(panelManual);
	    				panelManual.setLayout(null);
	    				
	    				Font font = new Font("Arial", Font.BOLD,22);
	    				
	    				JLabel userLabelManual = new JLabel("Insira o código abaixo na sua app do Google Authenticator.");
	    				userLabelManual.setBounds(10, 10, 550, 30);
	    				panelManual.add(userLabelManual);
	    				
	    				JLabel userLabelManual1 = new JLabel(A2FSecret);
	    				userLabelManual1.setBounds(210, 60, 550, 30);
	    				userLabelManual1.setFont(font);
	    				panelManual.add(userLabelManual1);
	    				
	    				JButton BtnOk = new JButton("OK");
	    				BtnOk.setBounds(220, 125, 100, 25);
	    				panelManual.add(BtnOk);
	    				
	    				JButton BtnCancelar = new JButton("Cancelar");
	    				BtnCancelar.setBounds(330, 125, 100, 25);
	    				panelManual.add(BtnCancelar);
	    				
	    				BtnOk.addActionListener(new ActionListener()
	    		        {
	    		        	@Override
	    					public void actionPerformed(ActionEvent e) 
	    		        	{
	    		        		janela.dispose();
	    		        	}
    		        	});
	    				
	    				BtnCancelar.addActionListener(new ActionListener()
	    		        {
	    		        	@Override
	    					public void actionPerformed(ActionEvent e) 
	    		        	{
	    		        		janela.dispose();
	    		        	}
    		        	});
			        	
					}
		        });
				
				BtnQRCode.addActionListener(new ActionListener()
		        {
		        	@Override
					public void actionPerformed(ActionEvent e) 
		        	{
		        		panel.hide();
		        		
		        		String nomeLogonSO = System.getProperty("user.name");
		        		
	        			final JPanel panelQRCode = new JPanel();
	    				janela.add(panelQRCode);
	    				panelQRCode.setLayout(null);
	    				
	    				Font font = new Font("Arial", Font.BOLD,16);
	    				
	    				JLabel userLabel0 = new JLabel("Insira abaixo seu e-mail para que a aplicação possa criar o QRCode. Em seguida, clique");
	    				userLabel0.setBounds(10, 10, 650, 30);
	    				panelQRCode.add(userLabel0);

	    				JLabel userLabel1 = new JLabel("no botão gerar, acesse o app do Google Authenticator e cadastre o QRCode gerado.");
	    				userLabel1.setBounds(10, 30, 650, 30);
	    				panelQRCode.add(userLabel1);
	    				
	    				JLabel userUser = new JLabel("Usuário:");
	    				userUser.setBounds(10, 70, 60, 25);
	    				panelQRCode.add(userUser);
	    				
	    				final JTextField campoUser = new JTextField(40);
	    				campoUser.setBounds(73, 70, 420, 25);
	    				panelQRCode.add(campoUser);
	    				campoUser.setText(nomeLogonSO);
	    				
	    				JLabel userEmail = new JLabel("Email:");
	    				userEmail.setBounds(10, 100, 60, 25);
	    				panelQRCode.add(userEmail);
	    				
	    				final JTextField campoEmail = new JTextField(40);
	    				campoEmail.setBounds(73, 100, 420, 25);
	    				panelQRCode.add(campoEmail);
	    				
	    				JButton BtnGerar = new JButton("Gerar");
	    				BtnGerar.setBounds(220, 135, 100, 25);
	    				panelQRCode.add(BtnGerar);
	    				
	    				JButton BtnCancelar = new JButton("Cancelar");
	    				BtnCancelar.setBounds(330, 135, 100, 25);
	    				panelQRCode.add(BtnCancelar);
	    				
	    				BtnGerar.addActionListener(new ActionListener()
	    		        {
	    		        	@Override
	    					public void actionPerformed(ActionEvent e) 
	    		        	{
	    		        		try
	    		        		{
	    		        			if(!campoEmail.getText().toString().matches("^([\\w\\-]+\\.)*[\\w\\- ]+@([\\w\\- ]+\\.)+([\\w\\-]{2,3})$"))
	    		        				JOptionPane.showConfirmDialog(null, "E-mail inválido!", "Adicionando segredo ao App Google Authenticator", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
	    		        			/*
	    		        			\w: Permite a entrada de qualquer caratere alfanumérico incluindo underscore.

	    		        			\-: Permite a entrada do caractere -.

	    		        			\.: Permite a entrada do caractere ..

	    		        			*: Permite zero ou várias ocorrências de tudo que está a esquerda desse caractere na expressão.

	    		        			@: Obriga a ocorrência desse caractere na expressão.

	    		        			{2,3}: Permite x ocorrências de tudo que está a esquerda desse conjunto na expressão, onde x é o número indicado entre {}, nesse caso 2 ou 3.
									*/
	    		        			else
	    		        			{
	    		        				String[] email = campoEmail.getText().split("@");		    		        		
			    		        		
			    		        		if(email.length > 1)
		    		        			{
			    		        			if(GerarQRCode(campoUser.getText(), email[0], email[1]))
			    		        				janela.dispose();
			    		        			else
			    		        				JOptionPane.showConfirmDialog(null, "Não foi possível gerar o QR Code!", "Adicionando segredo ao App Google Authenticator", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
		    		        			}
			    		        		else
			    		        			JOptionPane.showConfirmDialog(null, "E-mail inválido!", "Adicionando segredo ao App Google Authenticator", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
	    		        			}
		    		        		
	    		        		}
	    		        		catch(Exception ex)
	    		        		{
	    		        			JOptionPane.showConfirmDialog(null, "Erro ao gerar QR Code! Erro: " + ex.getMessage(), "Adicionando segredo ao App Google Authenticator", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
	    		        			return;
	    		        		}
	    		        	}
    		        	});
	    				
	    				BtnCancelar.addActionListener(new ActionListener()
	    		        {
	    		        	@Override
	    					public void actionPerformed(ActionEvent e) 
	    		        	{
	    		        		janela.dispose();
	    		        	}
    		        	});
					}
		        });

				janela.setModal(true); //Bloqueia a tela e espera o fechamento da janela para continuar a execução
				janela.setVisible(true); //Mostra a janela
			}
			else
				return null;
		}
		catch(Exception ex)
		{
			throw new Exception("Erro: " + ex.getMessage());
		}
				
		return A2FSecret;
	}

	public boolean AtivarA2F()
	{
		final JDialog janela = new JDialog();
		janela.setTitle("A2F - Ativando a autenticação 2 fatores...");
		janela.setSize(470,465);
		janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		janela.setResizable(false);	//Desabilita opção de expansão
		janela.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
					
		JPanel panel = new JPanel();
		janela.add(panel);
		panel.setLayout(null);
		
		JLabel userLabel0 = new JLabel("Deseja ativar a autenticação de dois fatores nesta aplicação?");
		userLabel0.setBounds(10, 10, 550, 30);
		panel.add(userLabel0);
		
		JLabel userLabel2 = new JLabel("Requisito: possuir um dispositivo móvel com o app do");		
		userLabel2.setBounds(30, 40, 650, 25);
		panel.add(userLabel2);
		
		JLabel userLabel3 = new JLabel("do Google Authenticator instalado.");		
		userLabel3.setBounds(30, 60, 650, 25);
		panel.add(userLabel3);
		
		JLabel status = new JLabel();  
		status.setBorder(BorderFactory.createLineBorder(Color.BLACK));  
		status.setBounds(50, 100, 175, 285);  
		
		ImageIcon imageIcon = new ImageIcon("GoogleAuthenticator0.png"); // load the image to a imageIcon
		Image image = imageIcon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(175, 285,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon = new ImageIcon(newimg);  // transform it back
		status.setIcon(imageIcon);  
		panel.add(status);  
		
		JLabel status2 = new JLabel();  
		status2.setBorder(BorderFactory.createLineBorder(Color.BLACK));  
		status2.setBounds(240, 100, 175, 285);  
		
		ImageIcon imageIcon2 = new ImageIcon("GoogleAuthenticator.png"); // load the image to a imageIcon
		Image image2 = imageIcon2.getImage(); // transform it 
		Image newimg2 = image2.getScaledInstance(175, 285,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon2 = new ImageIcon(newimg2);  // transform it back
		status2.setIcon(imageIcon2);  
		panel.add(status2);
		
		JButton BtnSim = new JButton("Sim");
		BtnSim.setBounds(120, 405, 100, 25);
		panel.add(BtnSim);
		
		JButton BtnNao = new JButton("Não");
		BtnNao.setBounds(250, 405, 100, 25);
		panel.add(BtnNao);
		
		BtnSim.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		ret = true;
        		janela.dispose();
			}
        });
		
		BtnNao.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		ret = false;
        		janela.dispose();
			}
        });

		janela.setModal(true); //Bloqueia a tela e espera o fechamento da janela para continuar a execução
		janela.setVisible(true); //Mostra a janela
		
		return ret;
	}
	
	public boolean GerarQRCode(String user, String userEmail, String host) throws Exception
	{
		boolean retGerarQRCode = false;
		
		try
		{
			final JDialog janela = new JDialog();
			janela.setTitle("A2F - Gerando o QR Code");
			janela.setSize(240,290);
			janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			janela.setResizable(false);	//Desabilita opção de expansão
			janela.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
								
			final JPanel panelQRCode = new JPanel();
			janela.add(panelQRCode);
			panelQRCode.setLayout(null);
			
			JLabel userLabel0 = new JLabel("QR Code gerado: ");
			userLabel0.setBounds(10, 10, 650, 30);
			panelQRCode.add(userLabel0);

			JButton BtnOk = new JButton("Ok");
			BtnOk.setBounds(80, 260, 100, 25);
			panelQRCode.add(BtnOk);
			
			String x = "otpauth://totp/"+user+":"+userEmail+ "@" +host+"?secret="+ A2FSecret+"&issuer="+user;
			//String data = "otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example";
			
			String pathQRCode = QRCode.GerarQRCode(x);
			
			File fileQRCode = new File(pathQRCode);
			
			if(fileQRCode.exists())
			{
				JLabel status = new JLabel();  
				status.setBorder(BorderFactory.createLineBorder(Color.BLACK));  
				status.setBounds(20, 50, 200, 200);  
				
				ImageIcon imageIcon = new ImageIcon(pathQRCode); // load the image to a imageIcon
				Image image = imageIcon.getImage(); // transform it 
				Image newimg = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
				imageIcon = new ImageIcon(newimg);  // transform it back
				status.setIcon(imageIcon);  
				panelQRCode.add(status);
				
				JOptionPane.showConfirmDialog(null, "QR Code gerado com sucesso!", "Gerando o QR Code", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
				
				fileQRCode.delete();
				
				retGerarQRCode = true;
			}
			else
			{
				JOptionPane.showConfirmDialog(null, "Não foi possível encontrar o QR Code gerado!", "Gerando o QR Code", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
				
				retGerarQRCode = false;
			}
			
			//Abrir link do QRcode no navegador padrão do usuário
			//Desktop desk = java.awt.Desktop.getDesktop(); 
			
			//try 
			//{
			//	desk.browse(new java.net.URI(link));
				
			//} 
			//catch (Exception ex) 
			//{
			//	throw new Exception("Erro ao abrir navegador do usuário para exibir o QR Code! Erro: " + ex.getMessage());    
			//} 
			
						
			BtnOk.addActionListener(new ActionListener()
	        {
	        	@Override
				public void actionPerformed(ActionEvent e) 
	        	{
	        		janela.dispose();
	        	}
        	});
			
			janela.setModal(true); //Bloqueia a tela e espera o fechamento da janela para continuar a execução
			janela.setVisible(true); //Mostra a janela
		}
		catch(Exception ex)
		{
			throw new Exception("Erro ao gerar QR Code! Erro: " + ex.getMessage());
		}
		
		return retGerarQRCode;
	}
	
}
