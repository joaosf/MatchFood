package FirebaseDB;

/**
 * Created by joaos on 24/06/2017.
 */

public class FBObjeto {
    String Tabela;
    String Chave;
    String[] Campos;
    String[] Valores;

    public FBObjeto(String tabela) {
        Tabela = tabela;
    }

    public void setTabela(String tabela) {
        Tabela = tabela;
    }

    public String getChave() {
        return Chave;
    }

    public String getTabela() {
        return Tabela;
    }

    public void setChave(String chave) {
        Chave = chave;
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
