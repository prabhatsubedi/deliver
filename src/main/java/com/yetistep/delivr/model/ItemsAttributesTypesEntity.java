package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/18/14
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ItemsAttributesTypes")
@Table(name = "items_attributes_types")
public class ItemsAttributesTypesEntity {

   private Integer id;
   private ItemEntity item;
   private List<ItemsAttributeEntity> itemsAttribute;
   private String type;
   private Boolean multiselect;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @OneToMany(mappedBy = "itemAttributeType")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<ItemsAttributeEntity> getItemsAttribute() {
        return itemsAttribute;
    }

    public void setItemsAttribute(List<ItemsAttributeEntity> itemsAttribute) {
        this.itemsAttribute = itemsAttribute;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "multi_select")
    public Boolean getMultiselect() {
        return multiselect;
    }

    public void setMultiselect(Boolean multiselect) {
        this.multiselect = multiselect;
    }
}
