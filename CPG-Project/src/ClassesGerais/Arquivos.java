package ClassesGerais;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Arquivos
{
	public Date dataUltimaMod;
	public Date dataUltimaModArqEncrypt;
	public String pathArquivo;
	public String nomeArquivo;
	public boolean diretorio;
	public String pathArqEstados;
	public String nomeArquivoEncrypt;
	public String pathArquivoEncrypt;
	public String pathDirEncrypt;
	public List<Arquivos> arquivosList;
	
	public Arquivos(String nomeArquivoParam, String pathArquivoParam, Date dataUltimaModParam, boolean diretorioParam, String pathArqEstadosParam, Date dataUltimaModArqEncryptParam, String nomeArquivoEncryptParam, String pathArquivoEncryptParam, String pathDirEncryptParam)
	{
		dataUltimaMod = dataUltimaModParam;
		pathArquivo = pathArquivoParam;
		nomeArquivo = nomeArquivoParam;
		diretorio = diretorioParam;
		pathArqEstados = pathArqEstadosParam;
		dataUltimaModArqEncrypt = dataUltimaModArqEncryptParam;
		nomeArquivoEncrypt = nomeArquivoEncryptParam;
		pathArquivoEncrypt = pathArquivoEncryptParam;
		pathDirEncrypt = pathDirEncryptParam;
		arquivosList = new ArrayList<Arquivos>();		
	}
	
	public Arquivos()
	{
		arquivosList = new ArrayList<Arquivos>();
	}
	
	public boolean AdicionarNovoRegistro(String nomeArquivoParam, String pathArquivoParam, Date lastModified, boolean diretorioParam, String pathArqEstadosParam, Date dataUltimaModArqEncryptParam, String nomeArquivoEncryptParam, String pathArquivoEncryptParam, boolean cifrar, String pathDirEncryptParam) throws Exception
	{
		try
		{
			if(arquivosList == null)
				arquivosList = new ArrayList<Arquivos>();
			
			Arquivos arq = new Arquivos(nomeArquivoParam, pathArquivoParam, lastModified, diretorioParam, pathArqEstadosParam, dataUltimaModArqEncryptParam, nomeArquivoEncryptParam, pathArquivoEncryptParam, pathDirEncryptParam);
			
			if(arquivosList.size() > 0)
			{
				for(int i = 0; i< arquivosList.size(); i++) 
				{
					Arquivos arquivo = arquivosList.get(i);
					
					if(cifrar)
					{
						if(arquivo.pathArquivo.equals(arq.pathArquivo))
							return false;
					}
					else
					{
						if(arquivo.pathArquivoEncrypt != null && arq.pathArquivoEncrypt != null && arquivo.pathArquivoEncrypt.equals(arq.pathArquivoEncrypt))
							return false;
					}
				}					
			}		
			
			arquivosList.add(arq);
			
		}
		catch(Exception e) 
		{
			throw new Exception("Erro ao adicionar novo registro na lista! Erro: " + e.getMessage());
		}
		
		return true;	
	}
	
	public boolean ImprimirElementosLista()
	{
		try
		{
			if(arquivosList == null)
				return false;
			
			for(int i = 0; i< arquivosList.size(); i++) 
			{
				Arquivos arquivo = arquivosList.get(i);
				
				if(arquivo != null) 
				{
					System.out.printf(""
							+ "Nome do arquivo: " + arquivo.nomeArquivo + " \n"
							+ "Caminho do arquivo: " + arquivo.pathArquivo + " \n"
							+ "É diretório?: " + arquivo.diretorio + " \n"
							+ "Caminho do arquivo de configurações (Quando for pasta): "
							+ "Última modificação: " + arquivo.dataUltimaMod + " \n"
							+ "Última modificação arq. encriptado: " + arquivo.dataUltimaModArqEncrypt + "\n"
							+ "\n\n");
				}
			}
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			return false;
		}
		
		
		List<File> listaArquivos = new ArrayList<File>();
		for(int i = 0; i< arquivosList.size(); i++)
		{
			listaArquivos.add(new File(arquivosList.get(i).pathArquivo));
		}
		
		//Comprimit arquivos TESTE
		try 
		{
			//CompressaoArquivos.ComprimirArquivos(listaArquivos, "/home/vitormoia/workspace/ProjetoCNC/Testes/testando", "");
			//CompressaoArquivos.EmpacotarArquivos(listaArquivos, "/home/vitormoia/workspace/ProjetoCNC/Testes/testando", "");
			//CompressaoArquivos.DescomprimirArquivos(new File("/home/vitormoia/workspace/ProjetoCNC/Testes/testando"), "/home/vitormoia/workspace/ProjetoCNC/Testes/", "");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return true;
		
	}
}


