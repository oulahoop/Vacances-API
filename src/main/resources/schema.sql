CREATE TABLE IF NOT EXISTS academie (
    libelle         TEXT        PRIMARY KEY,
    region          VARCHAR     NOT NULL,
    code_academie   INTEGER     NOT NULL,
    code_region     INTEGER     NOT NULL,
    longitude       INTEGER     NOT NULL,
    latitude        INTEGER     NOT NULL
);
CREATE TABLE IF NOT EXISTS vacances_scolaire (
    id              INTEGER     PRIMARY KEY,
    academie_id     TEXT        REFERENCES academie(libelle) NOT NULL,
    description     VARCHAR     NOT NULL,
    population      VARCHAR     NOT NULL,
    date_debut      DATE        NOT NULL,
    date_fin        DATE        NOT NULL,
    zone            VARCHAR     NOT NULL,
    annee_scolaire  VARCHAR     NOT NULL
);
CREATE TABLE IF NOT EXISTS utilisateur (
    id              INTEGER     PRIMARY KEY,
    nom             VARCHAR     NOT NULL,
    prenom          VARCHAR     NOT NULL,
    email           VARCHAR     NOT NULL UNIQUE,
    password        VARCHAR     NOT NULL,
    role            VARCHAR     NOT NULL
);