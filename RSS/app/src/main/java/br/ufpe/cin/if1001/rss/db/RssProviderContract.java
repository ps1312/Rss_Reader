package br.ufpe.cin.if1001.rss.db;

import android.net.Uri;
import android.provider.BaseColumns;

//BaseColumns automaticamente adiciona as colunas _ID e _COUNT
public class RssProviderContract implements BaseColumns {
    public static final String TITLE = "titulo";
    public static final String DATE = "dataPub";
    public static final String DESCRIPTION = "descricao";
    public static final String  LINK = "linkNoticia";
    public static final String UNREAD = "naoLido";

    public static final Uri BASE_SQL_URI = Uri.parse("content://br.ufpe.cin.if710.sqlprovider/");

    public static final Uri CONTENT_NEWS_URI = Uri.withAppendedPath(BASE_SQL_URI, SQLiteRSSHelper.DATABASE_TABLE);
}
