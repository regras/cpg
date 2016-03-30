package Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LogApp 
{
	static File arquivo = new File("log.txt");
	static FileWriter fw;
	BufferedWriter bw;
	
	public void InicializarLogDaApp() throws Exception
	{
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
			
			fw = new FileWriter(arquivo, true);
			bw = new BufferedWriter(fw);
			
			bw.append("#############################################################################");
			bw.newLine();
			
			Date data = new Date();
			
			bw.append("Começou a execução...\n\nData: " + data.toString());
			bw.newLine();
			bw.newLine();
			
		} 
		catch (Exception e) 
		{
			Date data = new Date();
			
			throw new Exception("Erro ao criar o arquivo de log da aplicação. Erro: " + e.getMessage() + "\nData: " + data.toString());
		}
	}
	
	public void EscreverNoLog(String texto) throws Exception
	{
		try
		{
			bw.append(texto);
			bw.newLine();
		}
		catch(Exception e)
		{
			Date data = new Date();
			
			throw new Exception("Erro na escrita ao log! Erro: " + e.getMessage()+ "\nData: " + data.toString());
		}		
	}
	
	public void EscreverNoLogNaoPularLinha(String texto) throws Exception
	{
		try
		{
			bw.append(texto);
		}
		catch(Exception e)
		{
			Date data = new Date();
			
			throw new Exception("Erro na escrita ao log! Erro: " + e.getMessage() + "\nData: " + data.toString());
		}		
	}
	
	public void DormirLogApp() throws Exception
	{
		try
		{
			bw.close();
			fw.close();
		}
		catch(Exception e)
		{
			Date data = new Date();
			
			throw new Exception("Erro ao dormir log! Erro: " + e.getMessage() + "\nData: " + data.toString());
		}	
	}
	
	public void AcordarLogApp() throws Exception
	{
		try
		{
			fw = new FileWriter(arquivo, true);
			bw = new BufferedWriter(fw);
		}
		catch(Exception e)
		{
			Date data = new Date();
			
			throw new Exception("Erro ao acordar log! Erro: " + e.getMessage() + "\nData: " + data.toString());
		}	
	}
	
	public void EncerrarLogApp() throws Exception
	{
		try
		{
			bw.append("Fim da execução");
			bw.newLine();
			
			bw.append("#############################################################################");
			bw.newLine();
			
			bw.close();
		}
		catch(Exception e)
		{
			Date data = new Date();
			
			throw new Exception("Erro na escrita ao log! Erro: " + e.getMessage() + "\nData: " + data.toString());
		}	
	}
}
