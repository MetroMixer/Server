package com.weeryan17.mixer.server.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "approved_clients")
public class ApprovedClient {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column
    private int id;

    @Column
    private String key;

}
