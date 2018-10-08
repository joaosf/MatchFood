package Classes;

import android.location.Location;

import java.util.Arrays;

public abstract class MatchFunctions {

    private static String[] optSexo = {"Selecione seu gênero...","Masculino", "Feminino"};
    private static String[] optObjetivo = {"Estou buscando ...", "Amizade", "Homens", "Mulheres"};

    private static boolean isValidRadius(Location Local1, Location Local2, int Raio) {
        Double radius = Double.valueOf(Raio);
        Float distancia = Local1.distanceTo(Local2);
        return (distancia <= radius);
    }

    public static boolean inLocationRange(Double Latitude1, Double Longitude1, Double Latitude2, Double Longitude2, int Limite1, int Limite2) {
        Location Local1 = new Location("Usuario1");
        Local1.setLatitude(Latitude1);
        Local1.setLongitude(Longitude1);

        Location Local2 = new Location("Usuario2");
        Local2.setLatitude(Latitude2);
        Local2.setLongitude(Longitude2);

        if ((!isValidRadius(Local1,Local2,Limite1)) || (!isValidRadius(Local2,Local1,Limite2))) {
            return false;
        }
        return true;
    }

    private static String[] getSexoObjetivo(String Objetivo) {
        String[] SexoObjetivo = new String[]{"Masculino","Feminino"};
        if (Objetivo.equals("Amizade")) {
            SexoObjetivo = new String[]{"Masculino","Feminino"};
        } else
        if (Objetivo.equals("Homens")) {
            SexoObjetivo = new String[]{"Masculino"};
        } else
        if (Objetivo.equals("Mulheres")) {
            SexoObjetivo = new String[]{"Feminino"};
        }

        return SexoObjetivo;
    }

    private static boolean inStringArray(String contains, String[] Array) {
        for (int i = 0;i < Array.length;i++) {
            if (contains.equals(Array[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean inSexoRange(String SexoUsuario1, String ObjetivoUsuario1, String SexoUsuario2, String ObjetivoUsuario2) {
        String[] SexoObjetivoUsuario1 = getSexoObjetivo(ObjetivoUsuario1);
        String[] SexoObjetivoUsuario2 = getSexoObjetivo(ObjetivoUsuario2);

        if ((!inStringArray(SexoUsuario1, SexoObjetivoUsuario2)) || (!inStringArray(SexoUsuario2, SexoObjetivoUsuario1))) {
            return false;
        }
        return true;
    }

    public static String[] getOptionsSexo() {
        return optSexo;
    }

    public static String[] getOptionsObjetivo() {
        return optObjetivo;
    }

}