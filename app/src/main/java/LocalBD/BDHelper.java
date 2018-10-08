package LocalBD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDHelper extends SQLiteOpenHelper {

    static int VERSAO = 25;
    static String DATABASE = "DBMatchFood.db";

    public BDHelper(Context context) {
        super(context, DATABASE, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String [] VarCampos;
        VarCampos = new String[]{"CodUsuario integer primary key"};
        db.execSQL(BDFunctions.getSQLCreateTable("UsuLog", VarCampos));
        db.execSQL(getSQLCreateCurtidas());
    }

    private String getSQLCreateCurtidas() {
        String Table = "curtidas";
        String [] VarCampos;
        VarCampos = new String[]{
                "codigocardapio integer"
        };
        return BDFunctions.getSQLCreateTable(Table,VarCampos);
    }

    private String getDropCurtidas() {
        return "drop table curtidas";
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(getDropCurtidas());
        sqLiteDatabase.execSQL("drop table UsuLog");

        if (!BDFunctions.existsTable(sqLiteDatabase,"curtidas")) {
            sqLiteDatabase.execSQL(getSQLCreateCurtidas());
        }

        String [] VarCampos;
        VarCampos = new String[]{"CodUsuario integer primary key"};
        sqLiteDatabase.execSQL(BDFunctions.getSQLCreateTable("UsuLog", VarCampos));
    }

}
