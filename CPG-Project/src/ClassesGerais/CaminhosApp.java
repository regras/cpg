package ClassesGerais;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class CaminhosApp 
{
	public String pastaNuvem;
	public String pastaTrabalho;
	public String pastaChaves;
	
	public CaminhosApp(){}
		
	public CaminhosApp(String pastaNuvemParam, String pastaTrabalhoParam, String pastaChavesParam)
	{
		pastaNuvem = pastaNuvemParam;
		pastaTrabalho = pastaTrabalhoParam;
		pastaChaves = pastaChavesParam;
	}	
	
	public CaminhosApp RecuperarLocaisSalvos(String pathsConfig)
	{
		CaminhosApp caminhos = new CaminhosApp();
		
		File arquivo = new File(pathsConfig);
		FileWriter fw;
		
		try 
		{
			if(arquivo.exists( ))
			{
				FileReader fr = new FileReader(arquivo);
				BufferedReader br = new BufferedReader( fr );
				
				//equanto houver mais linhas
				while(br.ready())
				{
					//lê a proxima linha
					String linha = br.readLine();
					
					String[] temp = linha.toString().split("==");
					
					if(temp[0].toString().equals("pastaNuvem".toString()))
						caminhos.pastaNuvem = temp[1].toString();
					else if (temp[0].toString().equals("pastaTrabalho".toString()))
						caminhos.pastaTrabalho = temp[1].toString();
					else if (temp[0].toString().equals("pastaChaves".toString()))
					{
						caminhos.pastaChaves = temp[1].toString();				
					}
				}
				
				br.close();
				fr.close();
				
			}
			else
				return null;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return caminhos;
	}

}
