package cz.fojtik.radek.countryviewer.sqlite;


public enum Tables {
    history("history"),
    settings("settings");

    private String dbName;

    Tables(String dbName) {
        this.dbName = dbName;
    }

    public String dbName() {
        return dbName;
    }

}
