package ApplicationTelas;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import A2F.GoogleAuthenticator;
import Criptografia.CriptoAssimetrica;

public class TelaInicialLogin extends JFrame 
{
	public int numeroTentativasAutenticacao = 3;
	private int contTentativasAutent = 0;
	private int contTentativasAutentA2F = 0;
	private String senha = "";
	private boolean A2FAtivado =false;
	
	public String RealizarLogin(boolean A2FAtivadoParam)
	{
		A2FAtivado = A2FAtivadoParam;
		String nomeLogonSO = System.getProperty("user.name");
		
		final JDialog janela = new JDialog();
		janela.setTitle("Login");
		janela.setSize(450,190);
		janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		janela.setResizable(false);	//Desabilita opção de expansão
		janela.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
		
			
		JPanel panel = new JPanel();
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
		
		JLabel userLabel2 = new JLabel("Digite seu segredo para acessar a aplicação");		
		userLabel2.setBounds(10, 50, 550, 25);
		panel.add(userLabel2);
				
		final JPasswordField passwordText = new JPasswordField(30);
		passwordText.setEchoChar('*');
		passwordText.setBounds(10, 80, 420, 25);
			
		Font font2 = new Font("Courier", Font.BOLD,14);
		passwordText.setFont(font2);
		
		panel.add(passwordText);
		
		JButton BtnOk = new JButton("Ok");
		BtnOk.setBounds(220, 120, 100, 25);
		panel.add(BtnOk);
		
		JButton BtnCancel = new JButton("Cancelar");
		BtnCancel.setBounds(330, 120, 100, 25);
		panel.add(BtnCancel);
		
		BtnOk.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		final String senhaDigitada = passwordText.getText();
        		
        		try
        		{
	        		if(senhaDigitada != null && !senhaDigitada.isEmpty())
	        		{
	        			//JOptionPane.showConfirmDialog(null, "A senha digitada foi " + senha, "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
	        			
	        			//Verificação da senha
	        			CriptoAssimetrica x = new CriptoAssimetrica();
	        			
	        			if(A2FAtivado)
        				{
	        				GoogleAuthenticator gooA2F = new GoogleAuthenticator();
	        				String A2Fsecret = gooA2F.VerifyPassphraseLogin(senhaDigitada);
	        				
	        				if(A2Fsecret !=  null && !A2Fsecret.isEmpty())
        					{
	        					TelaA2FLogin a2fLogin = new TelaA2FLogin();
	        					String A2FsecretChecked = a2fLogin.RealizarLoginA2F(A2Fsecret);
	        					
	        					if(A2FsecretChecked != null && !A2FsecretChecked.isEmpty())
	        					{
	        						A2Fsecret = "";
	        						
	        						if(x.ConferirSenhaUsuario(A2FsecretChecked + senhaDigitada))
			    					{
			        					JOptionPane.showConfirmDialog(null, "Autenticação realizada com sucesso!", "Autenticação", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
			        					senha = A2FsecretChecked + senhaDigitada;
			        					A2FsecretChecked = "";
			        					
			        					janela.dispose();
			    					}
			        				else
			        				{
			        					contTentativasAutentA2F++;
			        					
			        					if(!(contTentativasAutentA2F < numeroTentativasAutenticacao))
			        					{
			        						JOptionPane.showConfirmDialog(null, "Senha incorreta! Tente mais tarde...", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
				        	        		
				        	        		System.exit(0);	 
			        					}
			        					else
			        					{
			        						int reply = JOptionPane.showConfirmDialog(null, "Senha incorreta! Tente novamente.", "Aviso", JOptionPane.CANCEL_OPTION);
				        	        		
				        	        		if(reply == JOptionPane.CANCEL_OPTION)
				        	        			System.exit(0);	
				        	        		
				        	        		passwordText.setText("");
				        	        		passwordText.requestFocus();
			        					}
			        				}
	        					}
	        					else
	        						System.exit(0);        						
        					}
	        				else
        					{
	        					contTentativasAutent++;
	        					
	        					if(!(contTentativasAutent < numeroTentativasAutenticacao))
	        					{
	        						JOptionPane.showConfirmDialog(null, "Senha incorreta! Tente mais tarde...", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
		        	        		
		        	        		System.exit(0);	 
	        					}
	        					else
	        					{
	        						int reply = JOptionPane.showConfirmDialog(null, "Senha incorreta! Tente novamente.", "Aviso", JOptionPane.CANCEL_OPTION);
		        	        		
		        	        		if(reply == JOptionPane.CANCEL_OPTION)
		        	        			System.exit(0);	
		        	        		
		        	        		passwordText.setText("");
		        	        		passwordText.requestFocus();
	        					}
        					}
        				}
        				else
        				{
        					if(x.ConferirSenhaUsuario(senhaDigitada))
	    					{
	        					JOptionPane.showConfirmDialog(null, "Autenticação realizada com sucesso!", "Autenticação", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
	        					senha = senhaDigitada;
	        					janela.dispose();
	    					}
	        				else
	        				{
	        					contTentativasAutent++;
	        					
	        					if(!(contTentativasAutent < numeroTentativasAutenticacao))
	        					{
	        						JOptionPane.showConfirmDialog(null, "Senha incorreta! Tente mais tarde...", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
		        	        		
		        	        		System.exit(0);	 
	        					}
	        					else
	        					{
	        						int reply = JOptionPane.showConfirmDialog(null, "Senha incorreta! Tente novamente.", "Aviso", JOptionPane.CANCEL_OPTION);
		        	        		
		        	        		if(reply == JOptionPane.CANCEL_OPTION)
		        	        			System.exit(0);	
		        	        		
		        	        		passwordText.setText("");
		        	        		passwordText.requestFocus();
	        					}		        					 
	        				}
        				}
	        		}
	        		else
	        			JOptionPane.showConfirmDialog(null, "É necessário digitar uma senha! ", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
        		}
        		catch(Exception ex)
        		{
        			senha = null;
        			JOptionPane.showConfirmDialog(null, "Erro no processo de autenticação: " + ex.getMessage(), "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
        			return;
        		}
			}
        });
		
		BtnCancel.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		System.exit(0);	
			}
        });

		janela.setModal(true); //Bloqueia a tela e espera o fechamento da janela para continuar a execução
		janela.setVisible(true); //Mostra a janela
		
		passwordText.requestFocus();  
		
		return senha;
	}

}
