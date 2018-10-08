package LocalBD;

import android.database.sqlite.SQLiteDatabase;

public class BDFunctions {
    public static String getSQLCreateTable(String NomeTabela, String[] Campos) {
        String SQL = " CREATE TABLE "+NomeTabela+" (";

        for (int i=0; i < Campos.length;i++) {
            SQL = SQL + Campos[i];
            if (i < Campos.length-1) {
                SQL = SQL + ", ";
            }
        }

        SQL = SQL + ");";
        return SQL;
    }

    public static String getSQLInsertByObjeto(BDObjeto objeto) {
        String get = "insert into "+objeto.getTabela()+" (";
        for (int i = 0;i < objeto.getCampos().length; i++) {
            get = get+objeto.getCampos()[i];
            if (i < objeto.getCampos().length-1) {
                get = get+", ";
            }
        }
        get = get + ") VALUES (";
        for (int j = 0; j <objeto.getValores().length;j++) {
            get = get+"'"+objeto.getValores()[j]+"'";
            if (j < objeto.getValores().length-1) {
                get = get + ", ";
            }
        }
        get = get + ");";
        return get;
    }

    public static String getSQLUpdateByObjeto(BDObjeto objeto) {
        String get = "update "+objeto.getTabela()+" set ";
        for (int i = 0;i < objeto.getCampos().length; i++) {
            get = get+objeto.getCampos()[i]+" = '"+objeto.getValores()[i]+"'";
            if (i < objeto.getCampos().length-1) {
                get = get+", ";
            }
        }
        get = get + " where "+objeto.getCampoChave()+" = "+objeto.getChave();
        return get;
    }

    public static String getSQLSelectByObjeto(BDObjeto objeto, boolean withWhereClause) {
        String get = "select ";
        for (int i = 0;i < objeto.getCampos().length; i++) {
            get = get+objeto.getCampos()[i];
            if (i < objeto.getCampos().length-1) {
                get = get+", ";
            }
        }
        get = get + " from "+objeto.getTabela();
        if (withWhereClause) {
            get = get + " where "+objeto.getCampoChave()+" = "+objeto.getChave();
        }
        return get;
    }

    public static String getSQLDeleteByObjeto(BDObjeto objeto) {
        String get = "delete from "+objeto.getTabela()+" where "+objeto.getCampoChave()+" = "+objeto.getChave();
        return get;
    }

    public static boolean existsTable(SQLiteDatabase sqLiteDatabase, String Nome) {
        try {
            sqLiteDatabase.execSQL("select * from "+Nome);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
