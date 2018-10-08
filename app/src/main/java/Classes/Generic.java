package Classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.joao.matchfood.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class Generic {
    public static void MostrarMensagem(Context Janela, String Texto){
        Toast.makeText(Janela,Texto, Toast.LENGTH_LONG).show();
    }

    public static SimpleAdapter getAdapterListView(Context Contexto, String[] Titulos, String[] SubTitulos, Integer[] Imagens) {

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < Imagens.length; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", Titulos[i]);
            hm.put("listview_discription", SubTitulos[i]);
            hm.put("listview_image", Integer.toString(Imagens[i]));
            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(Contexto, aList, R.layout.custom_listview, from, to);//getBaseContext()
        return simpleAdapter;
    }

    public static ProgressDialog showLoadingDialog(Context ctx, String Titulo) {
        final ProgressDialog pd = new ProgressDialog(ctx);
        pd.setMessage(Titulo);
        pd.show();
        return pd;
    }

    public static void stopLoadingDialog(ProgressDialog pg) {
        if (pg.isShowing()) {
            pg.dismiss();
        }
    }

    public static boolean isValidText(EditText editText, String MensagemErro, int TamanhoMinimo, String MensagemTamanho) {
        if (editText.getText().toString().equals("")) {
            editText.setError(MensagemErro);
            return false;
        } else
        if (editText.getText().toString().length() < TamanhoMinimo) {
            editText.setError(MensagemTamanho);
            return false;
        }
        return true;
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static void showErrorSpinner(Spinner sp) {
        TextView errorText = (TextView)sp.getSelectedView();
        errorText.setError("");
        errorText.setTextColor(Color.RED);
        //errorText.setText(Mensagem);
    }

    public static String getSpinnerText(Spinner sp) {
        //TextView sText = (TextView)sp.getSelectedView();
        return sp.getSelectedItem().toString();//sText.getText().toString();
    }

    public static void setSpinnerText(Spinner sp, String txt) {
        if (!txt.equals("")) {
            TextView sText = (TextView) sp.getSelectedView();
            sText.setText(txt);
        }
    }

    public static int getPosOnList(String value, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
