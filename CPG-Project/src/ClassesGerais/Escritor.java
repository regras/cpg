package ClassesGerais;

import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class Escritor 
{
	private FileWriter fw;
	private Document documento;
	private Element arquivos;
	
	public Escritor(){}
	
	public void IniciarEscritaArquivoEstados(String path, String pathArqEncrypt) throws Exception
	{
		try 
		{
			//Escrevendo no arquivo de estados os dados dos arquivos
			fw = new FileWriter(path, false);
			
			arquivos = new Element("Arquivos");
			arquivos.setAttribute("pathArqEncrypt", pathArqEncrypt);
			
			//Define Arquivos como root
	        documento = new Document(arquivos);
	        
		} 
		catch (IOException e) 
		{
			throw new Exception("Erro ao iniciar escritor! Erro: " + e.getMessage());
		}		
	}
	
	public void FinalizarEscritaArquivoEstados() throws Exception
	{
		try 
		{
			//Classe responsável para imprimir / gerar o XML
	        XMLOutputter xout = new XMLOutputter();

	        //Imprimindo o XML no arquivo
	        xout.output(documento, fw);
		} 
		catch (Exception e) 
		{
			throw new Exception("Erro ao encerrar escritor! Erro: " + e.getMessage());
		}	
		finally
		{
		    fw.close();
		}
	}
	
	public void EscreverNovoElemento(Arquivos arquivo, int index) throws Exception
	{
		try 
		{
			//Cria o elemento Arquivo
            Element arq = new Element("Arquivo");
            
            //Adiciona o atributo id ao arquivo
            arq.setAttribute("id",Integer.toString(index));

            //Criando os elementos do arquivo

            Element nomeArquivo = new Element("nomeArquivo");
            nomeArquivo.setText(arquivo.nomeArquivo);

            Element pathArquivo = new Element("path");
            pathArquivo.setText(arquivo.pathArquivo);

            Element ultimaModificacao = new Element("ultimaModificacao");
            ultimaModificacao.setText(arquivo.dataUltimaMod.toString());

            Element diretorio = new Element("diretorio");
            diretorio.setText(arquivo.diretorio == true ? "true" : "false");
            
            Element ultimaModificacaoArqEncrypt = new Element("ultimaModificacaoArqEncrypt");
            ultimaModificacaoArqEncrypt.setText(arquivo.dataUltimaModArqEncrypt.toString());
            
            Element nomeArquivoEncrypt = new Element("nomeArquivoEncrypt");
            nomeArquivoEncrypt.setText(arquivo.nomeArquivoEncrypt.toString());
            
            Element pathArquivoEncrypt = new Element("pathArquivoEncrypt");
            pathArquivoEncrypt.setText(arquivo.pathArquivoEncrypt.toString());
            
                    		
            //Adicionando elementos no arquivo        		
            arq.addContent(nomeArquivo);        		
            arq.addContent(pathArquivo);        		
            arq.addContent(ultimaModificacao);  
            arq.addContent(ultimaModificacaoArqEncrypt);
            
            if(!arquivo.diretorio)
            	diretorio.setAttribute("pathArqConfig", "");        			            
            else
            	diretorio.setAttribute("pathArqConfig", arquivo.pathArqEstados);        		            	
                    		
            arq.addContent(diretorio);
            arq.addContent(nomeArquivoEncrypt);
            arq.addContent(pathArquivoEncrypt);

            //Adicionado o arquivo a Arquivos        		
            arquivos.addContent(arq);        					
	
		} 
		catch (Exception e) 
		{
			throw new Exception("Erro ao adicionar novo elemento no escritor! Erro: " + e.getMessage());
		}
	}
			
}
