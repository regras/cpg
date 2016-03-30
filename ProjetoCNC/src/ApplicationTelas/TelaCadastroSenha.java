package ApplicationTelas;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class TelaCadastroSenha extends JFrame 
{
	private String senhaUsuario = "";
	private boolean aceitarSenha = false;
	
	public String CadastrarSenha() throws Exception
	{
		/*
		 * 
		 user.name	User account name
		 user.dir	User’s current working directory
		 user.home	User’s home directory
		 
		 https://ricardospinoza.wordpress.com/2011/04/19/java-obtenha-informacoes-do-sistema-operacional-usuario-e-do-java-do-cliente-facilmente-com-a-api-system-properties/
		 
		 */
		
		String nomeLogonSO = System.getProperty("user.name");
		
		final JDialog janela = new JDialog();
		janela.setTitle("Login");
		janela.setSize(450,295);//470,220
		janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		janela.setResizable(false);	//Desabilita opção de expansão
		janela.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
		
		//final JFrame frame = new JFrame("Login");
		//frame.setSize(450, 220);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setResizable(false);;
		//frame.setLocationRelativeTo(null);
		
		final JPanel panel = new JPanel();
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
		
		JLabel userLabel2 = new JLabel("É necessário a criação de um segredo para possibilitar o acesso a esta");		
		userLabel2.setBounds(10, 10, 550, 90);
		panel.add(userLabel2);
		

		JLabel userLabel3 = new JLabel("aplicação e a todos os dados criptografados por ela. Recomenda-se um");		
		userLabel3.setBounds(10, 10, 550, 130);
		panel.add(userLabel3);
		
		JLabel userLabel4 = new JLabel("segredo com um grande nível de complexidade a fim de dificultar acessos");		
		userLabel4.setBounds(10, 10, 550, 170);
		panel.add(userLabel4);
		
		JLabel userLabel5 = new JLabel("não autorizados.");		
		userLabel5.setBounds(10, 10, 550, 210);
		panel.add(userLabel5);
		
		final JPasswordField passwordText = new JPasswordField(30);
		passwordText.setEchoChar('*');
		passwordText.setBounds(10, 140, 420, 25);
		
		Font font2 = new Font("Courier", Font.BOLD,14);
		passwordText.setFont(font2);
		
		panel.add(passwordText);
		
		JButton BtnOk = new JButton("Ok");
		BtnOk.setBounds(220, 230, 100, 25);
		panel.add(BtnOk);
		
		JButton BtnCancel = new JButton("Cancelar");
		BtnCancel.setBounds(330, 230, 100, 25);
		panel.add(BtnCancel);
		
		JLabel userLabel6Msg = new JLabel("Força da senha: ");
		userLabel6Msg.setBounds(10, 175, 550, 30);
		panel.add(userLabel6Msg);
		
		final JLabel userLabel6 = new JLabel();		
		userLabel6.setBounds(330, 175, 550, 30);	
		Font font1 = new Font("Arial", Font.BOLD,17);
		userLabel6.setFont(font1);
		panel.add(userLabel6);
		
		final JPanel painel1 = new JPanel();
		painel1.setBounds(130, 175, 35, 33);
		
		final JPanel painel2 = new JPanel();
		painel2.setBounds(167, 175, 35, 33);		
		
		final JPanel painel3 = new JPanel();
		painel3.setBounds(204, 175, 35, 33);		
		
		final JPanel painel4 = new JPanel();
		painel4.setBounds(241, 175, 35, 33);	
		
		final JPanel painel5 = new JPanel();
		painel5.setBounds(278, 175, 35, 33);
		
		
		/*
painel1.setBounds(10, 185, 15, 20);
		
		final JPanel painel2 = new JPanel();
		painel2.setBounds(27, 185, 15, 20);		
		
		final JPanel painel3 = new JPanel();
		painel3.setBounds(44, 185, 15, 20);		
		
		final JPanel painel4 = new JPanel();
		painel4.setBounds(61, 185, 15, 20);	
		
		final JPanel painel5 = new JPanel();
		painel5.setBounds(78, 185, 15, 20);*/
		
		painel1.setBackground(Color.GRAY);
		panel.add(painel1);
									
		painel2.setBackground(Color.GRAY);
		panel.add(painel2);
		
		painel3.setBackground(Color.GRAY);
		panel.add(painel3);
		
		painel4.setBackground(Color.GRAY);
		panel.add(painel4);
		
		painel5.setBackground(Color.GRAY);
		panel.add(painel5);
		
		BtnOk.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		try
        		{
	        		String senha = passwordText.getText();
	        		
	        		if(senha != null && !senha.isEmpty())
	        		{
	        			if(aceitarSenha)
	        			{
	        			//JOptionPane.showConfirmDialog(null, "A senha digitada foi " + senha, "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
	        			senhaUsuario = senha;
	        			
	        			janela.dispose();
	        			
	        			//mainClass.senha = hashSenha.substring(0,4) + hashSenha.substring(7, 11) + hashSenha.substring(15, 19) + hashSenha.substring(23, 27);
	        			
	        			return;
	        			}
	        			else
	        				JOptionPane.showConfirmDialog(null, "A senha fornecida possui segurança insuficente. É necessário criar uma senha com força Média ou superior! ", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
	        		}
	        		else
	        			JOptionPane.showConfirmDialog(null, "É necessário digitar uma senha! ", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
        		}
        		catch(Exception ex)
        		{
        			JOptionPane.showConfirmDialog(null, "Erro ao criar senha! Erro: " + ex.getMessage(), "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
        		}
			}
        });
		
		BtnCancel.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		janela.dispose();
			}
        });
		
		
		
		passwordText.addKeyListener(new KeyListener() 
		{ 
			public void keyTyped(KeyEvent e) 
            {  
            	
            }  
            
            public void keyPressed(KeyEvent e) 
            {
            	    
            }  
            
            public void keyReleased(KeyEvent e) 
            {
            	try 
            	{
	            	if(e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_ENTER || 
	e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_ALT || e.getKeyCode() == KeyEvent.VK_ALT_GRAPH || e.getKeyCode() == KeyEvent.VK_F1 || 
	e.getKeyCode() == KeyEvent.VK_F12 || e.getKeyCode() == KeyEvent.VK_F3 || e.getKeyCode() == KeyEvent.VK_F4 || e.getKeyCode() == KeyEvent.VK_F5 || 
	e.getKeyCode() == KeyEvent.VK_F6 || e.getKeyCode() == KeyEvent.VK_F7 || e.getKeyCode() == KeyEvent.VK_F8 || e.getKeyCode() == KeyEvent.VK_F9 || 
	e.getKeyCode() == KeyEvent.VK_F10 || e.getKeyCode() == KeyEvent.VK_F11 || e.getKeyCode() == KeyEvent.VK_F12 || e.getKeyCode() == KeyEvent.VK_PAUSE || 
	e.getKeyCode() == KeyEvent.VK_CAPS_LOCK || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN || e.getKeyCode() == KeyEvent.VK_PAGE_UP || 
	e.getKeyCode() == KeyEvent.VK_END || e.getKeyCode() == KeyEvent.VK_HOME || e.getKeyCode() == KeyEvent.VK_INSERT || e.getKeyCode() == KeyEvent.VK_NUM_LOCK || 
	e.getKeyCode() == KeyEvent.VK_WINDOWS)
	            		return;
	            	            	
            		String senha = passwordText.getText();
            		
            		if(senha == null)
            		{
            			aceitarSenha = false;
            			
            			userLabel6.setText("");
            			
            			painel1.setBackground(Color.GRAY);
    					panel.add(painel1);
    												
    					painel2.setBackground(Color.GRAY);
    					panel.add(painel2);
    					
    					painel3.setBackground(Color.GRAY);
    					panel.add(painel3);
    					
    					painel4.setBackground(Color.GRAY);
    					panel.add(painel4);
    					
    					painel5.setBackground(Color.GRAY);
    					panel.add(painel5);
            		}
            		else if (senha.isEmpty())
            		{
            			aceitarSenha = false;
            			
            			userLabel6.setText("");
            			
            			painel1.setBackground(Color.GRAY);
    					panel.add(painel1);
    												
    					painel2.setBackground(Color.GRAY);
    					panel.add(painel2);
    					
    					painel3.setBackground(Color.GRAY);
    					panel.add(painel3);
    					
    					painel4.setBackground(Color.GRAY);
    					panel.add(painel4);
    					
    					painel5.setBackground(Color.GRAY);
    					panel.add(painel5);
            		}
            		else 
            		{
            			int score = ChecarSegurancaSenha2(senha);
            			
            			if(score < 16)
    					{
            				aceitarSenha = false;
            				
            				userLabel6.setText("Muito fraca");
							
							painel1.setBackground(Color.RED);
							painel2.setBackground(Color.GRAY);
							painel3.setBackground(Color.GRAY);
							painel4.setBackground(Color.GRAY);
							painel5.setBackground(Color.GRAY);
							
    					}
    					else if (score > 15 && score < 21)
    					{
    						aceitarSenha = false;
    						
    						userLabel6.setText("Fraca");
    						
    						painel1.setBackground(Color.RED);
							painel2.setBackground(Color.RED);
							painel3.setBackground(Color.GRAY);
							painel4.setBackground(Color.GRAY);
							painel5.setBackground(Color.GRAY);
							
    					}
    					else if (score > 20 && score < 32)
    					{
    						aceitarSenha = true;
    						
    						userLabel6.setText("Média");
    						
    						painel1.setBackground(Color.ORANGE);
							painel2.setBackground(Color.ORANGE);
							painel3.setBackground(Color.ORANGE);
							painel4.setBackground(Color.GRAY);
							painel5.setBackground(Color.GRAY);
							
    					}
    					else if (score > 31 && score < 35)
    					{
    						aceitarSenha = true;
    						
    						userLabel6.setText("Forte");
    						
    						painel1.setBackground(Color.GREEN);
							painel2.setBackground(Color.GREEN);
							painel3.setBackground(Color.GREEN);
							painel4.setBackground(Color.GREEN);
							painel5.setBackground(Color.GRAY);
    					}
    					else
    					{
    						aceitarSenha = true;
    						
    						userLabel6.setText("Muito forte");
    						
    						painel1.setBackground(Color.GREEN);
							painel2.setBackground(Color.GREEN);
							painel3.setBackground(Color.GREEN);
							painel4.setBackground(Color.GREEN);
							painel5.setBackground(Color.GREEN);
    					}
            			
            			panel.add(painel1);
            			panel.add(painel2);
            			panel.add(painel3);
            			panel.add(painel4);
            			panel.add(painel5);
            		}
				} 
            	catch (Exception ex) 
            	{
            		aceitarSenha = false;
            		
            		JOptionPane.showConfirmDialog(null, "Erro na criação da senha do usuário! Erro: " + ex.getMessage(), "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
				}     	
            }  
        });  

		janela.setModal(true); //Bloqueia a tela e espera o fechamento da janela para continuar a execução
		janela.setVisible(true); //Mostra a janela	

		return senhaUsuario;
	}
	
	private void ChecarSegurancaSenha(String senha) throws Exception
	{
		int entrada = 0;
		String resultado = null;
		
		try
		{
			if(senha.length() < 7)
				entrada = entrada - 1;
          
			if(!senha.matches("/[a-z_]/i") || !senha.matches("/[0-9]/"))
				entrada = entrada - 1;  
          
			if(!senha.matches("/W/"))  
                entrada = entrada - 1;  
				            
			if(entrada == 0)
				resultado = "A Segurança de sua senha é: <font color=\'#99C55D\'>EXCELENTE</font>";
			else if(entrada == -1)  
				resultado = "A Segurança de sua senha é: <font color=\'#7F7FFF\'>BOM</font>";  
			else if(entrada == -2)  
				resultado = "A Segurança de sua senha é: <font color=\'#FF5F55\'>BAIXA</font>";  
			else if(entrada == -3)  
				resultado = "A Segurança de sua senha é: <font color=\'#A04040\'>MUITO BAIXA</font>";  
        
			JOptionPane.showConfirmDialog(null, resultado, "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
		}
		catch(Exception e)
		{
			throw new Exception("Erro na checagem da segurança da senha do usuário! Erro: " + e.getMessage());
		}
	}
	
	private int ChecarSegurancaSenha2(String passwd) throws Exception
	{
		/*
		Tips for strong passwords:

		Make your password 8 characters or more
		Use mixed case letters (upper and lower case)
		Use more than one number
		Use special characters (!,@,#,$,%,^,&,*,?,_,~)
		Use L33t
		Use a random password generator/password vault like Password Safe or pwsafe
		Use PasswordMaker
		- See more at: http://www.geekwisdom.com/dyn/passwdmeter#sthash.nflLBIr8.dpuf
		
		
		http://www.geekwisdom.com/dyn/passwdmeter
		
		*/
		
		/*
		 * Mínimo: 8 caracteres + letras + números
		 * 
		 * Muito fraco: apenas letras ou apenas números, menor do que 8 caracteres 		(05+1) = 6
		 * Fraco: apenas letras ou apenas números, maior ou igual do que 8 caracteres 		(15+1) = 16
		 * Médio: Mais que 8 caracteres + letras(upper and lower) + número 			(15+1+1+1+2) = 20
		 * Forte: Mais que 8 caracteres + letras (upper and lower) + número + caract. esp 	(15+1+1+1+1+2+3+3+4)= 31
		 * Muito forte: Mais que 12 caracteres + letras(upper and lower) + números +caracts. esp(15+1+1+1+1+1+2+2+3+3+4 = 34)
		 */
		
		
		
		
		try
		{
			int intScore = 0;
			String strLog = "";
					
					// PASSWORD LENGTH
					if (passwd.length() < 8) 
						intScore = (intScore+5);
					
					else if (passwd.length() > 7 && passwd.length() < 16)
						intScore = (intScore+15);
					
					else if (passwd.length() > 15)
						intScore = (intScore+18);
										
					
					// LETTERS
					// [verified] at least one lower case letter
					if (passwd.matches(".*[a-z]+.*"))
						intScore = (intScore+1);
											
	                // [verified] at least one upper case letter
					if (passwd.matches(".*[A-Z]+.*"))
						intScore = (intScore+1);
											
					// NUMBERS
					// [verified] at least one number
					if (passwd.matches(".*\\d+.*"))
						intScore = (intScore+1);
											
					// [verified] at least three numbers
					if (passwd.matches(".*\\d+.*\\d+.*\\d+.*"))             
						intScore = (intScore+1);
											
					String regexCaracterEsp = "!,@#$%^&*?_~+=-";
					
					// SPECIAL CHAR
					// [verified] at least one special character
					if (passwd.matches(".*["+ regexCaracterEsp + "]+.*"))            
						intScore = (intScore+1);
											
					// [verified] at least two special characters
					if (passwd.matches(".*[" + regexCaracterEsp + "]+.*[" + regexCaracterEsp + "]+.*"))
						intScore = (intScore+2);
						
					//2 COMBOS
					
					// [verified] both upper and lower case
					if (passwd.matches(".*[a-z]+.*[A-Z]+.*|.*[A-Z]+.*[a-z]+.*"))
						intScore = (intScore+2);
											
					// [verified] both letters upper and numbers
					//if (passwd.matches(".*[A-Z]+.*\\d+.*|.*\\d+.*[A-Z]+.*")) 
						//intScore = (intScore+2);
											
					// [verified] both letters lower and numbers
					//if (passwd.matches(".*[a-z]+.*\\d+.*|.*\\d+.*[a-z]+.*")) 
						//intScore = (intScore+2);
										
					//[verified] numbers and special characters
					//if (passwd.matches(".*\\d+.*[" + regexCaracterEsp + "]+.*|.*[" + regexCaracterEsp + "]+.*\\d+.*")) 
						//intScore = (intScore+2);
											
					//[verified] both letters lower and special characters
					//if (passwd.matches(".*[a-z]+.*[" + regexCaracterEsp + "]+.*|.*[" + regexCaracterEsp + "]+.*[a-z]+.*")) 
						//intScore = (intScore+2);
										
					//[verified] both letters upper and special characters
					//if (passwd.matches(".*[A-Z]+.*[" + regexCaracterEsp + "]+.*|.*[" + regexCaracterEsp + "]+.*[A-Z]+.*")) 
						//intScore = (intScore+2);
											
					
					//3 COMBOS
					
					
					//[verified] letters (upper and lower) and special characters
					if (passwd.matches(".*[a-z]+.*[A-Z]+.*[" + regexCaracterEsp + "]+.*|.*[a-z]+.*[" + regexCaracterEsp + "]+.*[A-Z]+.*|.*[A-Z]+.*[a-z]+.*[" + regexCaracterEsp + "]d+.*|.*[A-Z]+.*[" + regexCaracterEsp + "]+.*[a-z]+.*|.*[" + regexCaracterEsp + "]+.*[A-Z]+.*[a-z]+.*|.*[" + regexCaracterEsp + "]+.*[a-z]+.*[A-Z]+.*")) 
						intScore = (intScore+3);
											
					//[verified] letters upper, special characters and numbers
					//if (passwd.matches(".*[" + regexCaracterEsp + "]+.*[A-Z]+.*\\d+.*|.*[" + regexCaracterEsp + "]+.*\\d+.*[A-Z]+.*|.*[A-Z]+.*[" + regexCaracterEsp + "]+.*\\d+.*|.*[A-Z]+.*\\d+.*[" + regexCaracterEsp + "]+.*|.*\\d+.*[A-Z]+.*[" + regexCaracterEsp + "]+.*|.*\\d+.*[" + regexCaracterEsp + "]+.*[A-Z]+.*")) 
						//intScore = (intScore+2);
											
					//[verified] letters lower, special characters and numbers
					//if (passwd.matches(".*[a-z]+.*[" + regexCaracterEsp + "]+.*\\d+.*|.*[a-z]+.*\\d+.*[" + regexCaracterEsp + "]+.*|.*[" + regexCaracterEsp + "]+.*[a-z]+.*\\d+.*|.*[" + regexCaracterEsp + "]+.*\\d+.*[a-z]+.*|.*\\d+.*[" + regexCaracterEsp + "]+.*[a-z]+.*|.*\\d+.*[a-z]+.*[" + regexCaracterEsp + "]+.*")) 
						//intScore = (intScore+2);
						
					// [verified] both letters (upper and lower) and numbers
					if (passwd.matches(".*[a-z]+.*[A-Z]+.*\\d+.*|.*[a-z]+.*\\d+.*[A-Z]+.*|.*[A-Z]+.*[a-z]+.*\\d+.*|.*[A-Z]+.*\\d+.*[a-z]+.*|.*\\d+.*[A-Z]+.*[a-z]+.*|.*\\d+.*[a-z]+.*[A-Z]+.*")) 
						intScore = (intScore+3);
											
					//4 COMBOS
					
					// [verified] letters, numbers, and special characters
					if (passwd.matches(
							   ".*[a-z]+.*[A-Z]+.*\\d+.*[" + regexCaracterEsp + "]+.*"
							+ "|.*[a-z]+.*[A-Z]+.*[" + regexCaracterEsp + "]+.*\\d+.*"
							+ "|.*[a-z]+.*\\d+.*[A-Z]+.*[" + regexCaracterEsp + "]+.*"
							+ "|.*[a-z]+.*\\d+.*[" + regexCaracterEsp + "]+.*[A-Z]+.*"
							+ "|.*[a-z]+.*[" + regexCaracterEsp + "]+.*\\d+.*[A-Z]+.*"
							+ "|.*[a-z]+.*[" + regexCaracterEsp + "]+.*[A-Z]+.*\\d+.*"
							
							+ "|.*[A-Z]+.*[a-z]+.*\\d+.*[" + regexCaracterEsp + "]+.*"
							+ "|.*[A-Z]+.*[a-z]+.*[" + regexCaracterEsp + "]+.*\\d+.*"
							+ "|.*[A-Z]+.*\\d+.*[a-z]+.*[" + regexCaracterEsp + "]+.*"
							+ "|.*[A-Z]+.*\\d+.*[" + regexCaracterEsp + "]+.*[a-z]+.*"
							+ "|.*[A-Z]+.*[" + regexCaracterEsp + "]+.*[a-z]+.*\\d+.*"
							+ "|.*[A-Z]+.*[" + regexCaracterEsp + "]+.*\\d+.*[a-z]+.*"
							
							+ "|.*\\d+.*[A-Z]+.*[a-z]+.*[" + regexCaracterEsp + "]+.*"
							+ "|.*\\d+.*[A-Z]+.*[" + regexCaracterEsp + "]+.*[a-z]+.*"
							+ "|.*\\d+.*[a-z]+.*[A-Z]+.*[" + regexCaracterEsp + "]+.*"
							+ "|.*\\d+.*[a-z]+.*[" + regexCaracterEsp + "]+.*[A-Z]+.*"
							+ "|.*\\d+.*[" + regexCaracterEsp + "]+.*[A-Z]+.*[a-z]+.*"
							+ "|.*\\d+.*[" + regexCaracterEsp + "]+.*[a-z]+.*[A-Z]+.*"
							
							+ "|.*[" + regexCaracterEsp + "]+.*[a-z]+.*[A-Z]+.*\\d+.*"
							+ "|.*[" + regexCaracterEsp + "]+.*[a-z]+.*\\d+.*[A-Z]+.*"
							+ "|.*[" + regexCaracterEsp + "]+.*[A-Z]+.*[a-z]+.*\\d+.*"
							+ "|.*[" + regexCaracterEsp + "]+.*[A-Z]+.*\\d+.*[a-z]+.*"
							+ "|.*[" + regexCaracterEsp + "]+.*\\d+.*[A-Z]+.*[a-z]+.*"
							+ "|.*[" + regexCaracterEsp + "]+.*\\d+.*[a-z]+.*[A-Z]+.*"
							))
					intScore = (intScore+4);
											
        
			//JOptionPane.showConfirmDialog(null, "Score: " + intScore + "    Verdict: "+ strVerdict + "      Log: " + strLog, "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
			
			return intScore;
		}
		catch(Exception e)
		{
			throw new Exception("Erro na checagem da segurança da senha do usuário! Erro: " + e.getMessage());
		}
	}
}
