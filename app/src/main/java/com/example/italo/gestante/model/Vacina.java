package com.example.italo.gestante.model;

public class Vacina {

    private String idVacina,datavacina, horaVacina,tipoVacina,key;

    public Vacina() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIdVacina() {
        return idVacina;
    }

    public void setIdVacina(String idVacina) {
        this.idVacina = idVacina;
    }

    public String getDatavacina() {
        return datavacina;
    }

    public void setDatavacina(String datavacina) {
        this.datavacina = datavacina;
    }

    public String getHoraVacina() {
        return horaVacina;
    }

    public void setHoraVacina(String horaVacina) {
        this.horaVacina = horaVacina;
    }

    public String getTipoVacina() {
        return tipoVacina;
    }

    public void setTipoVacina(String tipoVacina) {
        this.tipoVacina = tipoVacina;
    }
}
