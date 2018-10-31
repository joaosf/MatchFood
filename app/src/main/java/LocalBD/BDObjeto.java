package LocalBD;

/**
 * Created by joaos on 05/07/2017.
 */

public class BDObjeto {
    //Campos
    String Tabela;
    String CampoChave;
    String Chave;
    String[] Campos;
    String[] Valores;
    
    //Owner
    public BDObjeto(String tabela) {
        Tabela = tabela;
    }

    public void setChave(String chave) {
        Chave = chave;
    }

    public String getChave() {
        return Chave;
    }

    public void setCampoChave(String chave) {
        CampoChave = chave;
    }

    public String getCampoChave() {
        return CampoChave;
    }

    public String getTabela() {
        return Tabela;
    }

    public String[] getCampos() {
        return Campos;
    }

    public void setCampos(String[] campos) {
        Campos = campos;
    }

    public String[] getValores() {
        return Valores;
    }

    public void setValores(String[] valores) {
        Valores = valores;
    }
}
