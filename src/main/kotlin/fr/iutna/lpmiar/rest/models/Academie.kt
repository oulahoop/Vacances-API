package fr.iutna.lpmiar.rest.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.*

@Entity
@Table(name = "academie")
data class Academie(
    @Id var libelle     : String,
    val region          : String    = "",
    @Column(name = "code_academie")
    val codeAcademie    : Int       = 0,
    @Column(name = "code_region")
    val codeRegion      : Int       = 0,
    val longitude       : Double    = 0.0,
    val latitude        : Double    = 0.0,
    @OneToMany(mappedBy = "academie", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("academie") //ignore les académies de la vacance scolaire
    val vacancesScolaire: List<VacancesScolaire> = emptyList(),
)
