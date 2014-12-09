package com.yetistep.delivr.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="ItemsImageEntity")
@Table(name = "items_image")
public class ItemsImageEntity implements Serializable {

    private Integer id;
    private ItemEntity item;
    private String url;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
