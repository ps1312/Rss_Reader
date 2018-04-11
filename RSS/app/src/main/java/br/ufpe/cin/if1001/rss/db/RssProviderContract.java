package br.ufpe.cin.if1001.rss.db;

import android.provider.BaseColumns;

//BaseColumns automaticamente adiciona as colunas _ID e _COUNT
public class RssProviderContract implements BaseColumns {
    public static final String TITLE = "titulo";
    public static final String DATE = "dataPub";
    public static final String DESCRIPTION = "descricao";
    public static final String  LINK = "linkNoticia";
    public static final String UNREAD = "naoLido";
}
