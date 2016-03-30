package ApplicationTelas;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import A2F.GoogleAuthenticator;

public class TelaA2FLogin 
{
	private int numeroTentativasAutenticacao = 3;
	private String UserPassphrase = ""; 
	private String a2fSecret = "";
	private int contTentativasAutent = 0;
	
	public String RealizarLoginA2F(String userPassphraseParam) throws Exception
	{
		UserPassphrase = userPassphraseParam;
		
		final JDialog janela = new JDialog();
		janela.setTitle("A2F");
		janela.setSize(470,150);
		janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		janela.setResizable(false);	//Desabilita opção de expansão
		janela.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
		
			
		JPanel panel = new JPanel();
		janela.add(panel);
		panel.setLayout(null);
		
		Font font = new Font("Arial", Font.BOLD,18);
		
		JLabel userLabel0 = new JLabel("Segundo passo na autenticação: ");
		userLabel0.setBounds(10, 10, 550, 30);
		panel.add(userLabel0);
		
		JLabel userLabel2 = new JLabel("Digite o código gerado pela aplicação de seu dispositivo móvel.");		
		userLabel2.setBounds(10, 45, 650, 25);
		panel.add(userLabel2);
				
		//final JTextField campoCodigo = new JTextField(40);
		
		javax.swing.text.MaskFormatter formataIntervalo = new javax.swing.text.MaskFormatter("######");  
		final JTextField campoCodigo = new javax.swing.JFormattedTextField(formataIntervalo);
		campoCodigo.setBounds(10, 75, 150, 25);
		panel.add(campoCodigo);
		
		
		JButton BtnOk = new JButton("Ok");
		BtnOk.setBounds(130, 115, 100, 25);
		panel.add(BtnOk);
		
		JButton BtnCancel = new JButton("Cancelar");
		BtnCancel.setBounds(240, 115, 100, 25);
		panel.add(BtnCancel);
		
		BtnOk.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		String entrada = campoCodigo.getText().toString();
        		
        		if(entrada != null && !entrada.trim().isEmpty())
        		{
	        		final Long code = Long.parseLong(campoCodigo.getText().toString());
	        		
	        		try
	        		{
	        			String secret = GoogleAuthenticator.Login(UserPassphrase, code);
        				
        				if(secret != null && !secret.isEmpty())
    					{
        					a2fSecret = secret;
        					janela.dispose();
    					}
        				else
        				{
        					contTentativasAutent++;
        					
        					if(!(contTentativasAutent < numeroTentativasAutenticacao))
        					{
        						JOptionPane.showConfirmDialog(null, "Código incorreto! Tente mais tarde...", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
	        	        		
	        	        		System.exit(0);	 
        					}
        					else
        					{
        						int reply = JOptionPane.showConfirmDialog(null, "Código incorreto! Tente novamente.", "Aviso", JOptionPane.CANCEL_OPTION);
	        	        		
	        	        		if(reply == JOptionPane.CANCEL_OPTION)
	        	        			System.exit(0);	
	        	        		
	        	        		campoCodigo.setText("");
	        	        		campoCodigo.requestFocus();
        					}
        				}
	        				
	        		}
	        		catch(Exception ex)
	        		{
	        			a2fSecret = null;
	        			JOptionPane.showConfirmDialog(null, "Erro na autencicação do segundo fator! Erro: " + ex.getMessage(), "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
	        		}
        		}
        		else
        			JOptionPane.showConfirmDialog(null, "É necessário inserir o código para proceguir com a autenticação!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
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
		
		campoCodigo.requestFocus();
		
		return a2fSecret;
	}

}
