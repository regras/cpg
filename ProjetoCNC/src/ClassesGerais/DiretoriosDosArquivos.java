package ClassesGerais;

import java.util.List;

public class DiretoriosDosArquivos 
{
	public String DirArquivosSecretosUser;
	public String DirArquivosNuvem;
	public String DirArquivoConfig;
	public String DirArquivoConfigEncrypted;
	public List<DiretoriosDosArquivos> ListSubDiretorios;
	
	public DiretoriosDosArquivos(){}
		
	public DiretoriosDosArquivos(String dirArquivosSecretosUserParam, String dirArquivosNuvemParam, String dirArquivoConfigParam, String dirArquivoConfigEncryptedParam)
	{
		DirArquivosSecretosUser = dirArquivosSecretosUserParam;
		DirArquivosNuvem = dirArquivosNuvemParam;
		DirArquivoConfig = dirArquivoConfigParam;
		DirArquivoConfigEncrypted = dirArquivoConfigEncryptedParam;
	}
	
	public DiretoriosDosArquivos(String dirArquivosSecretosUserParam, String dirArquivosNuvemParam, String dirArquivoConfigParam, List<DiretoriosDosArquivos> listSubDiretoriosParam)
	{
		DirArquivosSecretosUser = dirArquivosSecretosUserParam;
		DirArquivosNuvem = dirArquivosNuvemParam;
		DirArquivoConfig = dirArquivoConfigParam;
		ListSubDiretorios = listSubDiretoriosParam;
	}
}
