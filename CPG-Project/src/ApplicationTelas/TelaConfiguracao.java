package ApplicationTelas;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ClassesGerais.CaminhosApp;

public class TelaConfiguracao extends JFrame 
{
	private JTextField campoPastaNuvem, campoPastaAplicacao, campoPastaChaves;
	private boolean novo;
	private String PathsConfig;
	 
	public void Configuracao(final String PathsConfigParam)
	{
		novo = true;
		PathsConfig = PathsConfigParam;

		final JDialog janela = new JDialog();
		janela.setTitle("Configurações");
		janela.setSize(500,250);
		janela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		janela.setResizable(false);	//Desabilita opção de expansão
		janela.setLocationRelativeTo(null);	//Exibe a tela no modo CENTER SCREEN (meio da tela)
		
		JPanel panel = new JPanel();
		//frame.add(panel);
		janela.add(panel);
		panel.setLayout(null);
		
		Font font = new Font("Arial", Font.BOLD,26);
		
		JLabel userLabel = new JLabel("Definição das pastas da aplicação");
		userLabel.setFont(font);
		userLabel.setBounds(10, 10, 550, 30);
		panel.add(userLabel);
		
		JLabel userLabel2 = new JLabel("Pasta de arquivos criptografados (NUVEM): ");		
		userLabel2.setBounds(10, 70, 400, 20);
		panel.add(userLabel2);
		
		campoPastaNuvem = new JTextField(40);
		campoPastaNuvem.setBounds(10, 90, 440, 25);
		panel.add(campoPastaNuvem);
		
		JButton BtnLocPastaNuvem = new JButton("...");
		BtnLocPastaNuvem.setBounds(455, 90, 30, 25);
		panel.add(BtnLocPastaNuvem);
		
		JLabel userLabel3 = new JLabel("Pasta de arquivos para criptografar (LOCAL): ");		
		userLabel3.setBounds(10, 125, 400, 20);
		panel.add(userLabel3);
		
		campoPastaAplicacao = new JTextField(40);
		campoPastaAplicacao.setBounds(10, 145, 440, 25);
		panel.add(campoPastaAplicacao);
		
		JButton BtnLocPastaAplicacao = new JButton("?");
		BtnLocPastaAplicacao.setBounds(455, 145, 30, 25);
		panel.add(BtnLocPastaAplicacao);
		
		JLabel userLabel4 = new JLabel("Pasta das chaves criptográficas: ");		
		userLabel4.setBounds(10, 185, 200, 20);
		//panel.add(userLabel4);
		
		campoPastaChaves = new JTextField(40);
		campoPastaChaves.setBounds(170, 210, 440, 25);
		//panel.add(campoPastaChaves);
		
		JButton BtnLocPastaChaves = new JButton("?");
		BtnLocPastaChaves.setBounds(455, 210, 30, 25);
		//panel.add(BtnLocPastaChaves);
		
		JButton BtnSalvar = new JButton("Salvar");
		BtnSalvar.setBounds(270, 185, 100, 25);
		panel.add(BtnSalvar);
		
		JButton BtnCancel = new JButton("Cancelar");
		BtnCancel.setBounds(380, 185, 100, 25);
		panel.add(BtnCancel);
		
		//Ativações dos botões...
		//Função: Ao invés de digitar os caminhos, os usuários podem selecionar os locais...
		
		//Botão para selecionar a pasta da nuvem
		BtnLocPastaNuvem.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		JFileChooser chooser = new JFileChooser();  
        		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		
        		int i= chooser.showSaveDialog(null); 
        		if (i==1)
        		{ 
        			JOptionPane.showConfirmDialog(null, "Erro ao selecionar local!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
        		} 
        		else 
        		{ 
        			campoPastaNuvem.setText(chooser.getSelectedFile().toString());
    			}
			}
        });
		
		//Botão para selecionar a pasta do usuário
		BtnLocPastaAplicacao.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		JFileChooser chooser = new JFileChooser();  
        		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		
        		int i= chooser.showSaveDialog(null); 
        		if (i==1)
        		{ 
        			JOptionPane.showConfirmDialog(null, "Erro ao selecionar local!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
        		} 
        		else 
        		{ 
        			campoPastaAplicacao.setText(chooser.getSelectedFile().toString());
    			}
			}
        });
		
		//Botão para selecionar a pasta dos arquivos de configurações - DESABILITADO
		BtnLocPastaChaves.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		JFileChooser chooser = new JFileChooser();  
        		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		
        		int i= chooser.showSaveDialog(null); 
        		if (i==1)
        		{ 
        			JOptionPane.showConfirmDialog(null, "Erro ao selecionar local!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE);
        		} 
        		else 
        		{ 
        			campoPastaChaves.setText(chooser.getSelectedFile().toString());
    			}
			}
        });
		
		//Botão de salvar
		BtnSalvar.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		if(ChecarCampos())
        		{
        			if(SalvarConfiguracoes(PathsConfig))
        			{
        				if(novo)
        					JOptionPane.showConfirmDialog(null, "Configurações salvas com sucesso!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
        				else
        					JOptionPane.showConfirmDialog(null, "Configurações alteradas com sucesso!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
        				
                		janela.dispose();
        			}
        			else
        				JOptionPane.showConfirmDialog(null, "Não foi possível salvar as configurações!", "Aviso", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
        			 
        		}       			
        		
        		//JOptionPane.showConfirmDialog(null, "É necessário digitar uma senha! ", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE);
			}
        });
		
		//Botão de cancelar
		BtnCancel.addActionListener(new ActionListener()
        {
        	@Override
			public void actionPerformed(ActionEvent e) 
        	{
        		janela.dispose();
			}
        });
		
		//Preencher informações na tela...
		CaminhosApp cam = new CaminhosApp();		
		CaminhosApp caminhos = cam.RecuperarLocaisSalvos(PathsConfig);
		
		if(caminhos != null)
		{
			novo = false;
			
			campoPastaNuvem.setText(caminhos.pastaNuvem);
			campoPastaAplicacao.setText(caminhos.pastaTrabalho);
			//campoPastaChaves.setText(caminhos.pastaChaves);
		}

		janela.setModal(true); //Bloqueia a tela e espera o fechamento da janela para continuar a execução
		janela.setVisible(true); //Mostra a janela	
	
	}

	public boolean SalvarConfiguracoes(String PathsConfig)
	{
		File arquivo = new File(PathsConfig);
		FileWriter fw;
		
		try 
		{
			if(!arquivo.exists( ))
			{
				try 
				{
					arquivo.createNewFile();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			
			fw = new FileWriter(arquivo, false);
			BufferedWriter bw = new BufferedWriter(fw);
			
			if(campoPastaNuvem.getText().endsWith("/"))
				bw.append("pastaNuvem==" + campoPastaNuvem.getText());
			else
				bw.append("pastaNuvem==" + campoPastaNuvem.getText() + "/");
			
			bw.newLine();
			
			if(campoPastaAplicacao.getText().endsWith("/"))
				bw.append("pastaTrabalho==" + campoPastaAplicacao.getText());
			else
				bw.append("pastaTrabalho==" + campoPastaAplicacao.getText() + "/");
			
			bw.newLine();
			
			/*if(campoPastaChaves.getText().endsWith("/"))
				bw.append("pastaChaves==" + campoPastaChaves.getText());
			else
				bw.append("pastaChaves==" + campoPastaChaves.getText() + "/");
			
			bw.newLine();*/			
			
			bw.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return true;
	}

	public boolean ChecarCampos()
	{
		String campoPastaNuvemtemp = campoPastaNuvem.getText();
		
		if(campoPastaNuvemtemp == null || campoPastaNuvemtemp.isEmpty())
		{
			JOptionPane.showConfirmDialog(null, "É necessário fornecer o caminho da pasta da nuvem!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);		
			return false;
		}
		
		if(!new File(campoPastaNuvemtemp).exists())
		{
			JOptionPane.showConfirmDialog(null, "É necessário fornecer um caminho válido para a pasta da nuvem!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);		
			return false;
		}
		
		String campoPastaAplicacaotemp = campoPastaAplicacao.getText();
		
		if(campoPastaAplicacaotemp == null || campoPastaAplicacaotemp.isEmpty())
		{
			JOptionPane.showConfirmDialog(null, "É necessário fornecer o caminho do local de trabalho da aplicação!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);		
			return false;
		}
		
		if(!new File(campoPastaAplicacaotemp).exists())
		{
			JOptionPane.showConfirmDialog(null, "É necessário fornecer um caminho válido para o local de trabalho da aplicação!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);		
			return false;
		}
		
		/*String campoPastaChavestemp = campoPastaChaves.getText();
		
		if(campoPastaChavestemp == null || campoPastaChavestemp.isEmpty())
		{
			JOptionPane.showConfirmDialog(null, "É necessário fornecer o caminho das chaves criptográficas!", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);		
			return false;
		}
		
		if(!new File(campoPastaChavestemp).exists())
		{
			JOptionPane.showConfirmDialog(null, "É necessário fornecer um caminho válido para as chaves criptográfcas !", "Aviso", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);		
			return false;
		}*/
		
		return true;
	}
	
}