/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.roleandjoin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author alejandro
 */
public class ValidadorDeCadenas {

    private Pattern patternEmail;
    private Pattern patternNombreUsuario;
    private Pattern patternFileName;
    private Matcher matcher;

    private static final String EMAIL_PATTERN
            = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String NOMBRE_USUARIO_PATTERN = "^@[\\w-]+";
    

    public ValidadorDeCadenas() {
        patternEmail = Pattern.compile(EMAIL_PATTERN);
        patternNombreUsuario = Pattern.compile(NOMBRE_USUARIO_PATTERN);
    }

    /**
     * Validate hex with regular expression
     *
     * @param email hex for validation
     * @return true valid hex, false invalid hex
     */
    public boolean validarEmail(final String email) {
        matcher = patternEmail.matcher(email);
        return matcher.matches();
    }

    public boolean validarNombreUsuario(String nombreUsuario) {
        nombreUsuario = addArrobaNombreUsuario(nombreUsuario);
        matcher = patternNombreUsuario.matcher(nombreUsuario);
        return matcher.matches();
    }

    public static String addArrobaNombreUsuario(String nombreUsuario) {
        if (!nombreUsuario.startsWith("@")) {
            nombreUsuario = "@" + nombreUsuario;
        }
        return nombreUsuario;
    }
    
    public static String addNumeralCanal(String nombreCanal){
        if(!nombreCanal.startsWith("#"))
        {
            nombreCanal = "#"+ nombreCanal;
        }
        return  nombreCanal;
    }
    
    public static String textToHtml(String text){
    	String cad = "";
    	if(text==null||text.trim().equals(""))
    		return text;
    	
		String[] arrayHttp= text.split("http");
		for (int i = 0; i < arrayHttp.length; i++) {
			cad = arrayHttp[i];
			if(cad.length()>0 && cad.contains("://")){
				cad="http"+cad;
				cad = cad.replaceAll(
						"(\\A|\\s)((http|https|ftp|mailto):\\S+)(\\s|\\z)",
						"$1<a target=\"_blank\" href=\"$2\">$2</a>$4");
				 arrayHttp[i] = cad;
			}
		}
		cad = "";
		for (int i = 0; i < arrayHttp.length; i++) {
			cad += arrayHttp[i];
		}
		cad = cad.replace("\n", "<br/>");
		return cad;
    }
    
    

}
