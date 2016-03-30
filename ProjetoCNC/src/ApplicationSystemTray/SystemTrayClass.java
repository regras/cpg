package ApplicationSystemTray;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import ApplicationTelas.TelaConfiguracao;
import ClassesGerais.CaminhosApp;
import Log.LogApp;

public class SystemTrayClass 
{
	private static LogApp log = new LogApp();
	private static String PathsConfig; 
	
	public static void IniciarSystemTray(LogApp logParam, String PathsConfigParam)
	{
		log = logParam;
		PathsConfig = PathsConfigParam;
		
		//Tutorial: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/misc/TrayIconDemoProject/src/misc/TrayIconDemo.java
		
		//String icoPath = "/home/vitormoia/workspace/ProjetoCNC/java.png";
		//String icoPath = "/home/vitormoia/workspace/ProjetoCNC/bulb.gif";
		String icoPath = "Imagem5.png";
		
		//Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        //final TrayIcon trayIcon = new TrayIcon(createImage("images/bulb.gif", "tray icon"));
        final TrayIcon trayIcon = new TrayIcon(new ImageIcon(icoPath, "omt").getImage(), "CNCrypt");
        final SystemTray tray = SystemTray.getSystemTray();
       
        // Create a pop-up menu components
        MenuItem sobreItem = new MenuItem("Sobre");
        MenuItem confItem = new MenuItem("Configurações");
        MenuItem abrirLocItem = new MenuItem("Abrir local de trabalho");
        MenuItem ajudaItem = new MenuItem("Ajuda");
        MenuItem fecharItem = new MenuItem("Fechar");
        
      //Add components to pop-up menu
        popup.add(sobreItem);
        popup.addSeparator();
        popup.add(confItem);
        popup.add(abrirLocItem);
        popup.add(ajudaItem);
        popup.addSeparator();
        popup.add(fecharItem);
               
        trayIcon.setPopupMenu(popup);
       
        try 
        {
        	tray.add(trayIcon);    
        } 
        catch (AWTException e) 
        {
            System.out.println("TrayIcon could not be added.");
        }
        
        trayIcon.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		JOptionPane.showMessageDialog(null, "This dialog box is run from System Tray");
            }
        });
        
        sobreItem.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		JOptionPane.showMessageDialog(null, "Projeto: Cloud Privacy Guard - CPG"
        				+ "\n\nSoftware desenvolvido por: Vitor Hugo G. Moia - Mestrando em Eng. Elétrica. "
        				+ "\nSupervisão: Prof. Marco Aurélio Amaral Henriques "
        				+ "\nFaculdade de Eng. Elétrica e de Computação (FEEC) "
        				+ "\nUniversidade Estadual de Campinas (UNICAMP)");
            }
        });
        
        confItem.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		TelaConfiguracao conf = new TelaConfiguracao();
        		conf.Configuracao(PathsConfig);
            }
        });
        
        abrirLocItem.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		CaminhosApp cam = new CaminhosApp();
        		
        		CaminhosApp caminhos = cam.RecuperarLocaisSalvos(PathsConfig);
        		
        		if(caminhos != null)
        		{
        			try 
            		{
        				if(caminhos.pastaTrabalho != null && !caminhos.pastaTrabalho.isEmpty())
        				{
        					//linux
                			String nameSO =  System.getProperty("os.name");
                			
                			JOptionPane.showMessageDialog(null, nameSO);
                			
                			if(nameSO == "Linux")
                			{
                				Runtime.getRuntime().exec("nautilus " + caminhos.pastaTrabalho);
                			}
                			else if(nameSO.startsWith("Win"))
                			{
                				Runtime.getRuntime().exec("explorer " + caminhos.pastaTrabalho);
                			}
                			else
                				JOptionPane.showMessageDialog(null, "Não implementado ainda...");
        				}
        				else
        					JOptionPane.showMessageDialog(null, "O caminho em questão se encontra vazio!");        				
            			
    				} 
            		catch (IOException e1) 
            		{
            			e1.printStackTrace();
    				}
        		}
        		else
        		{
        			JOptionPane.showMessageDialog(null, "Não foi possível localizar o caminho desta pasta! Verifique as configurações do aplicativo.");
        		}
            }
        });
        
        ajudaItem.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		JOptionPane.showMessageDialog(null, "Em caso de dúvidas, envie um e-mail para vhgmoia@dca.fee.unicamp.br com o assunto CPG.");
            }
        });
        
        fecharItem.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		int reply = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja fechar a aplicação?", "Aviso", JOptionPane.YES_NO_OPTION);
        		
        		if(reply == JOptionPane.YES_OPTION)
        		{
        			try
        			{
        				log.EscreverNoLog("Encerrando aplicação...");
        				
        				log.EncerrarLogApp();
        			}
        			catch(Exception ex)
        			{
        				
        			}
        			
        			tray.remove(trayIcon);
                    System.exit(0);
        		}
            }
        });
        
        /*
        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
        Menu displayMenu = new Menu("Display");
        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        
        popup.addSeparator();
        popup.add(fecharItem);
        displayMenu.add(errorItem);
        displayMenu.add(warningItem);
        displayMenu.add(infoItem);
        displayMenu.add(noneItem);
        popup.add(exitItem);
        
        cb1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb1Id = e.getStateChange();
                if (cb1Id == ItemEvent.SELECTED){
                    trayIcon.setImageAutoSize(true);
                } else {
                    trayIcon.setImageAutoSize(false);
                }
            }
        });
         
        cb2.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                int cb2Id = e.getStateChange();
                if (cb2Id == ItemEvent.SELECTED){
                    trayIcon.setToolTip("Sun TrayIcon");
                } else {
                    trayIcon.setToolTip(null);
                }
            }
        });
        
        ActionListener listener = new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem)e.getSource();
                //TrayIcon.MessageType type = null;
                System.out.println(item.getLabel());
                if ("Error".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.ERROR;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an error message", TrayIcon.MessageType.ERROR);
                     
                } else if ("Warning".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.WARNING;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is a warning message", TrayIcon.MessageType.WARNING);
                     
                } else if ("Info".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.INFO;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an info message", TrayIcon.MessageType.INFO);
                     
                } else if ("None".equals(item.getLabel())) {
                    //type = TrayIcon.MessageType.NONE;
                    trayIcon.displayMessage("Sun TrayIcon Demo",
                            "This is an ordinary message", TrayIcon.MessageType.NONE);
                }
            }
        };
         
        errorItem.addActionListener(listener);
        warningItem.addActionListener(listener);
        infoItem.addActionListener(listener);
        noneItem.addActionListener(listener);
        */
       
	}	
}
